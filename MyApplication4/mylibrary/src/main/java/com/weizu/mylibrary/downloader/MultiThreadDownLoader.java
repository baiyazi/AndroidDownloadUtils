package com.weizu.mylibrary.downloader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.weizu.mylibrary.utils.EncoderUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2021年9月24日
 * by: 梦否
 *
 * 多线程下载文件类
 * 使用案例：
 * String url1 = "http://vjs.zencdn.net/v/oceans.mp4";
 *
 * MultiThreadDownLoader downloader = new MultiThreadDownLoader.Builder(getApplicationContext())
 *         .url(url1)
 *         .cacheDirName("Images")
 *         .suffix(MultiThreadDownLoader.FileSuffix.MP4)
 *         .method("GET")
 *         .timeout(3000)
 *         .build();
 *
 * downloader.download(new MultiThreadDownLoader.DownloadListener() {
 *     @Override
 *     public void onSuccess(File file) {
 *
 *     }
 *
 *     @Override
 *     public void onError(String msg) {
 *         Log.e("TAG", "onError: ");
 *     }
 * });
 */
public class MultiThreadDownLoader {
    private static final String TAG = "MultiThreadDownLoader";
    private String url;
    private int connectionTimeout;
    private String method;
    private Context context;
    private String cachePath = "imgs";
    private static final int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
    private static final int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private HttpURLConnection connection = null;
    private FileSuffix suffix;

    private MultiThreadDownLoader(){}

    public MultiThreadDownLoader(Context context){
        connectionTimeout = 500; // 500毫秒
        method = "GET";
        this.context = context;
    }

    public MultiThreadDownLoader url(String url){
        this.url = url;
        return this;
    }

    // 支持的文件类型后缀
    public enum FileSuffix{
        EXE("exe"),
        ZIP("zip"),
        JPEG("jpeg"),
        PNG("png"),
        GIF("gif"),
        MP4("mp4"),
        MP3("mp3"),
        PDF("pdf");

        private String value; // 实际值
        FileSuffix(String value) {
            this.value = value;
        }

        public String getValue(){
            return this.value;
        }
    }

    /**
     * 指明下载文件后缀
     * @param fileSuffix
     * @return
     */
    public MultiThreadDownLoader fileSuffix(FileSuffix fileSuffix){
        this.suffix = fileSuffix;
        return this;
    }

    public MultiThreadDownLoader(Builder builder){
        this.url = builder.url;
        this.connectionTimeout = builder.connectionTimeout;
        this.method = builder.method;
        this.context = builder.context;
        this.cachePath = builder.cachePath;
        this.suffix = builder.suffix;
    }

    public static class Builder{
        private String url;
        private int connectionTimeout;
        private String method;
        private String cachePath = "imgs";
        private Context context;
        private FileSuffix suffix;

        private Builder(){}
        public Builder(Context context){
            this.context = context;
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder timeout(int ms){
            this.connectionTimeout = ms;
            return this;
        }

        public Builder suffix(FileSuffix suffix){
            this.suffix = suffix;
            return this;
        }

        public Builder method(String method) {
            if (!(method.toUpperCase().equals("GET") || method.toUpperCase().equals("POST"))) {
                throw new AssertionError("Assertion failed");
            }
            this.method = method;
            return this;
        }

        public Builder cacheDirName(String cacheDirName){
            this.cachePath = cacheDirName;
            return this;
        }

        public MultiThreadDownLoader build(){
            return new MultiThreadDownLoader(this);
        }
    }

    public interface DownloadListener{
        void onSuccess(File file);
        void onError(String msg);
    }

    private static class DownloadThread extends Thread{
        private long startPos;
        private long endPos;
        private RandomAccessFile randomAccessFile;
        private File file;
        private String url;
        private int connectionTimeout = 5 * 1000;  // 5秒钟
        private String method = "GET";
        private long maxFileSize;
        private DownloadListener listener;

        public DownloadThread(String url, long startPos, long endPos, long maxFileSize, File file) {
            this.startPos = startPos;
            this.endPos = endPos;
            this.url = url;
            this.file = file;
            this.maxFileSize = maxFileSize;
        }

        public void setDownloadListener(DownloadListener listener){
            this.listener = listener;
        }

        @Override
        public void run() {
            Log.e(TAG, "=========> " + Thread.currentThread().getName());
            HttpURLConnection connection = null;
            URL url_c = null;
            try{
                randomAccessFile = new RandomAccessFile(this.file, "rwd");
                randomAccessFile.seek(this.startPos);
                url_c = new URL(url);
                connection = (HttpURLConnection) url_c.openConnection();
                connection.setConnectTimeout(this.connectionTimeout);
                connection.setRequestMethod(this.method);
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("Range", "bytes=" + startPos +"-" + endPos);

                Log.e(TAG, "Range: bytes=" + startPos +"-" + endPos);

                InputStream inputStream = connection.getInputStream();

                Log.e(TAG, "connection.getContentLength() == " + connection.getContentLength());

                int contentLength  = connection.getContentLength();
                if(contentLength  < 0) {
                    Log.e(TAG, "Download fail!");
                    return;
                }

                try {
                    if (connection.getResponseCode() == 206) {
                        byte[] buffer = new byte[2048];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1) {
                            randomAccessFile.write(buffer, 0, len);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try{
                        if(inputStream != null) inputStream.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }catch (IOException e){
                Log.e(TAG, "Download bitmap failed.", e);
                if(listener!=null) listener.onError(e.getLocalizedMessage());
                e.printStackTrace();
            }finally {
                if(connection != null) connection.disconnect();
                // todo 通知下载完毕
                if(this.endPos == this.maxFileSize){
                    Log.e(TAG, "Download bitmap success.");
                    if(listener!=null) listener.onSuccess(this.file);
                }
            }
        }
    }


    private static final ThreadFactory mThreadFactory = new ThreadFactory(){
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread#" + mCount.getAndIncrement());
        }
    };


    private Executor executor = new ThreadPoolExecutor(corePoolSize,
            maximumPoolSize,
            10L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(),
            mThreadFactory);


    public void download(DownloadListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File path = buildPath(cachePath);
                try{
                    URL url1 = new URL(url);
                    connection = (HttpURLConnection) url1.openConnection();
                    connection.setConnectTimeout(connectionTimeout);
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("accept", "*/*");
                    connection.connect();

                    int contentLength  = connection.getContentLength();
                    if(contentLength  < 0) {
                        Log.e(TAG, "Download fail!");
                        return;
                    }

                    // TODO 分为多个线程，进行执行
                    int step = contentLength / maximumPoolSize;

                    Log.d(TAG, "maximumPoolSize: " + maximumPoolSize +" , step:" + step);
                    Log.d(TAG, "contentLength: " + contentLength);

                    if(suffix == null){
                        throw new RuntimeException("You must set the download file suffix before.");
                    }

                    File file = new File(path, EncoderUtils.hashKeyFromUrl(url) + "." + suffix.getValue());
                    if(contentLength == file.length()){
                        Log.e(TAG, "Nothing changed!"); // already downlaod.
                        if(listener != null) listener.onSuccess(file);
                        return;
                    }
                    // 否则就下载
                    for (int i = 0; i < maximumPoolSize; i++) {
                        if(i != maximumPoolSize - 1) {
                            DownloadThread downloadThread = new DownloadThread(url, i * step, (i + 1) * step - 1, contentLength, file);
                            executor.execute(downloadThread);
                        }else{
                            DownloadThread downloadThread = new DownloadThread(url, i * step, contentLength, contentLength, file);
                            if(listener != null) downloadThread.setDownloadListener(listener);
                            executor.execute(downloadThread);
                        }
                    }
                }catch (IOException e){
                    Log.e(TAG, "Download bitmap failed.", e);
                    if(listener != null) listener.onError(e.getLocalizedMessage());
                    e.printStackTrace();
                }finally {
                    if(connection != null) connection.disconnect();
                }
            }
        }).start();
    }

    private File buildPath(String filePath) {
        // 是否有SD卡
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        // 如果有SD卡就存在外存，否则就位于这个应用的data/package name/cache目录下
        final String cachePath;
        if(flag) cachePath = context.getExternalCacheDir().getPath();
        else cachePath = context.getCacheDir().getPath();

        File directory = new File(cachePath + File.separator + filePath);
        // 目录不存在就创建
        if(!directory.exists()) directory.mkdirs();
        return directory;
    }

}

