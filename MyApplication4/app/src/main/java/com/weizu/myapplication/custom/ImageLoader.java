package com.weizu.myapplication.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.weizu.myapplication.R;
import com.weizu.mylibrary.utils.EncoderUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static final int MESSAGE_RESULT = 1;
    private static final int IMAGEVIEW_TAG = R.id.image_tag;
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCacheHelper mDiskLruCacheHelper;
    private String url;
    private static ImageLoader imageLoader;


    private ImageLoader(Context context){
        mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 将八分之一的应用内存分配给了缓存。
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        mDiskLruCacheHelper = new DiskLruCacheHelper(context);
    }

    public static ImageLoader with(Context context){
        if(imageLoader == null){
            synchronized (ImageLoader.class){
                if(imageLoader == null){
                    imageLoader = new ImageLoader(context);
                }
            }
        }
        return imageLoader;
    }

    public ImageLoader load(String url){
        this.url = url;
        return this;
    }

    public void into(ImageView imageView){
        loadBitmap(this.url, imageView);
    }


    private static final ThreadFactory mThreadFactory = new ThreadFactory(){
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread#" + mCount.getAndIncrement());
        }
    };

    private Executor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1,
            Runtime.getRuntime().availableProcessors() * 2 + 1,
            10L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(),
            mThreadFactory);

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            RequestResult result = (RequestResult) msg.obj;
            ImageView imageView = result.imageView;
            String url = (String) imageView.getTag(IMAGEVIEW_TAG);
            if(url.equals(result.url)){
                imageView.setImageBitmap(result.bitmap);
            }else{
                Log.w(TAG, "set image bitmap, but url has changed, ignored!");
            }
        }
    };

    private void loadBitmap(String url, ImageView imageView) {
        Bitmap bitmap = loadBitmapFromMemCache(url);
        imageView.setTag(IMAGEVIEW_TAG, url);
        if(bitmap != null){
            Log.d(TAG, "load bitmap from memory cache. url:" + url);
            imageView.setImageBitmap(bitmap);
            return;
        }
        try{
            bitmap = mDiskLruCacheHelper.getBitmap(url);
            if(bitmap != null){
                Log.d(TAG, "load bitmap from disk cache. url:" + url);
                imageView.setImageBitmap(bitmap);
                return;
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = loadBitmapFromHttp(url);
                    Log.d(TAG, "load bitmap from network. url:" + url);
                    if(bitmap != null){ // 加入内存缓存、磁盘缓存
                        addBitmapToMemoryCache(url, bitmap);
                        mDiskLruCacheHelper.put(url, bitmap);
                        // 传递消息
                        RequestResult result = new RequestResult(imageView, url, bitmap);
                        handler.obtainMessage(MESSAGE_RESULT, result).sendToTarget();
                    }
                }
            };
            executor.execute(runnable);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void addBitmapToMemoryCache(String url, Bitmap bitmap) {
        if (loadBitmapFromMemCache(url) == null) {
            mMemoryCache.put(EncoderUtils.hashKeyFromUrl(url), bitmap);
        }
    }

    private Bitmap loadBitmapFromMemCache(String url) {
        String key = EncoderUtils.hashKeyFromUrl(url);
        return mMemoryCache.get(key);
    }

    private Bitmap loadBitmapFromHttp(String url){
        if(Looper.myLooper() == Looper.getMainLooper()){
            throw new RuntimeException("can't visit network from UI thread.");
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            URL url1 = new URL(url);
            urlConnection = (HttpURLConnection) url1.openConnection();
            inputStream = urlConnection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        }catch (IOException e){
            Log.e(TAG, "download bitmap failed.", e);
            e.printStackTrace();
        }finally {
            try{
                if(inputStream != null) inputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }

    private static class RequestResult{
        private ImageView imageView;
        private String url;
        private Bitmap bitmap;

        public RequestResult(ImageView imageView, String url, Bitmap bitmap) {
            this.imageView = imageView;
            this.url = url;
            this.bitmap = bitmap;
        }
    }
}
