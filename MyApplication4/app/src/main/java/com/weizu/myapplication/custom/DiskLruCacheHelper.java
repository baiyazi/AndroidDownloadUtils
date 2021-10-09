package com.weizu.myapplication.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.weizu.mylibrary.utils.BitmapFormatConvertUtils;
import com.weizu.mylibrary.utils.EncoderUtils;
import com.weizu.mylibrary.utils.MemoryUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class DiskLruCacheHelper {
    private static final String TAG = "DiskLruCacheHelper";
    private static final String DIR_NAME = "diskCache";  // 缓存目录
    private static final int MAX_SIZE = 5 * 1024 * 1024; // 目录大小
    private static final int VERSION = 202109;       // 版本
    private static final int VALUECOUNT = 1;        // 每个缓存条目的个数

    private DiskLruCache mDiskLruCache;

    public DiskLruCacheHelper(Context context){
        File directory = getDiskCacheDir(context, DIR_NAME);
        if(!directory.exists()) directory.mkdirs(); // 目录不存在就创建
        if(MemoryUtils.getUsableSpace(directory) < MAX_SIZE)
            throw new RuntimeException("The current definition maxSize is too large, please try to reduce it a bit.");
        try{
            mDiskLruCache = DiskLruCache.open(directory, VERSION, VALUECOUNT, MAX_SIZE);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void put(String url, String value){
        String key = EncoderUtils.hashKeyFromUrl(url);
        DiskLruCache.Editor edit = null;
        BufferedWriter bw = null;
        OutputStream out = null;
        try{
            edit = mDiskLruCache.edit(key);
            if(edit == null) return;
            out = edit.newOutputStream(0); // valueCount为1，所以只能取0
            bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(value);
            edit.commit();
        }catch (IOException e){
            e.printStackTrace();
            try{
                edit.abort();  // 回滚
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }finally {
            try{
                if(bw != null) bw.close();
                if(out != null) out.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void put(String url, byte[] value){
        String key = EncoderUtils.hashKeyFromUrl(url);
        DiskLruCache.Editor edit = null;
        OutputStream out = null;
        try{
            edit = mDiskLruCache.edit(key);
            if(edit == null) return;
            out = edit.newOutputStream(0); // valueCount为1，所以只能取0
            out.write(value);
            edit.commit();
        }catch (IOException e){
            e.printStackTrace();
            try{
                edit.abort();  // 回滚
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }finally {
            try{
                if(out != null) out.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void put(String url, Bitmap value){
        put(url, BitmapFormatConvertUtils.bitmap2Bytes(value));
    }

    public void put(String url, Drawable value){
        put(url, BitmapFormatConvertUtils.drawable2Bitmap(value));
    }


    public boolean remove(String url){
        String key = EncoderUtils.hashKeyFromUrl(url);
        try{
            return mDiskLruCache.remove(key);
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public Bitmap getBitmap(String url) throws IOException {
        if(Looper.myLooper() == Looper.getMainLooper()){
            Log.w(TAG, "load bitmap from UI thread, it's not recommended!");
        }
        if(mDiskLruCache == null) return null;
        String key = EncoderUtils.hashKeyFromUrl(url);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if(snapshot != null){
            InputStream inputStream = snapshot.getInputStream(0);
            return BitmapFactory.decodeStream(inputStream);
        }else{
            Log.e(TAG, "not find entry.");
        }
        return null;
    }

    public Drawable getAsDrawable(String url) throws IOException {
        return BitmapFormatConvertUtils.bitmap2Drawable(getBitmap(url));
    }

    public void close() throws IOException {
        Log.d(TAG, "Closes this cache. Stored values will remain on the filesystem.");
        mDiskLruCache.close();
    }

    public void delete() throws IOException {
        Log.d(TAG, "Closes the cache and deletes all of its stored values. ");
        mDiskLruCache.delete();
    }

    public void flush() throws IOException {
        Log.d(TAG, "Force buffered operations to the filesystem.");
        mDiskLruCache.flush();
    }

    public File getDirectory(){
        return mDiskLruCache.getDirectory();
    }

    /**
     * 获取当前使用的文件路径
     * @param context
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName){
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 是否有SD卡
        // 如果有SD卡就存在外存，否则就位于这个应用的data/package name/cache目录下
        final String cachePath;
        if(flag) cachePath = context.getExternalCacheDir().getPath();
        else cachePath = context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

}
