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

/**
 * 单线程断点下载
 *
 * 示例：
 * Button start = findViewById(R.id.start);
 * Button pause = findViewById(R.id.pause);
 *
 * String url = "http://vjs.zencdn.net/v/oceans.mp4";
 *
 * SingleThreadBreakpointDownloader build = new SingleThreadBreakpointDownloader.Builder(getApplicationContext())
 *         .url(url)
 *         .suffix(SingleThreadBreakpointDownloader.FileSuffix.MP4)
 *         .startPoint(0)
 *         .build();
 *
 * start.setOnClickListener(new View.OnClickListener() {
 *     @Override
 *     public void onClick(View v) {
 *         build.download(new SingleThreadBreakpointDownloader.DownLoadListener(){
 *             @Override
 *             public void onSuccess(File file) {
 *                 Log.e("ThreeActivity", "onSuccess");
 *             }
 *
 *             @Override
 *             public void onError(String msg) {
 *                 Log.e("ThreeActivity", "onError: " + msg);
 *             }
 *
 *             @Override
 *             public void onListener(long currentPos, long totalLength) {
 *                 int val = (int) (currentPos * 1.0 / totalLength * 100);
 *                 progressbar.setProgress(val);
 *             }
 *         });
 *     }
 * });
 *
 * pause.setOnClickListener(new View.OnClickListener() {
 *     @Override
 *     public void onClick(View v) {
 *         build.setIsPause(true);
 *     }
 * });
 */

public class SingleThreadBreakpointDownloader {
    private static final String TAG = "DownLoader";
    private String url;
    private int connectionTimeout;
    private String method = "GET";
    private Context context;
    private String cachePath = "imgs";
    private FileSuffix suffix;
    private long startPoint, totalLength;
    private State isPause = State.ISPAUSE;
    private State isCancel = State.ISCANCEL;

    private SingleThreadBreakpointDownloader(){}

    public SingleThreadBreakpointDownloader(Context context){
        connectionTimeout = 500; // 500毫秒
        method = "GET";
        this.context = context;
    }

    public SingleThreadBreakpointDownloader url(String url){
        this.url = url;
        return this;
    }

    public SingleThreadBreakpointDownloader fileSuffix(FileSuffix fileSuffix){
        this.suffix = fileSuffix;
        return this;
    }

    public SingleThreadBreakpointDownloader cacheDir(String dir){
        this.cachePath = dir;
        return this;
    }

    public SingleThreadBreakpointDownloader startPoint(long start){
        this.startPoint = start;
        return this;
    }

    public SingleThreadBreakpointDownloader(Builder builder){
        this.url = builder.url;
        this.connectionTimeout = builder.connectionTimeout;
        this.context = builder.context;
        this.cachePath = builder.cachePath;
        this.suffix = builder.suffix;
        this.startPoint = builder.startPoint;
    }

    public static class Builder {
        private String url;
        private int connectionTimeout;
        private String cachePath = "imgs";
        private Context context;
        private FileSuffix suffix;
        private long startPoint;

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

        public Builder startPoint(long start){
            this.startPoint = start;
            return this;
        }

        public Builder suffix(FileSuffix suffix){
            this.suffix = suffix;
            return this;
        }

        public Builder cacheDirName(String cacheDirName){
            this.cachePath = cacheDirName;
            return this;
        }

        public SingleThreadBreakpointDownloader build(){
            return new SingleThreadBreakpointDownloader(this);
        }
    }

    public enum State{
        ISPAUSE(false),
        ISCANCEL(false);

        private boolean value;
        State(boolean value){
            this.value = value;
        }

        public boolean getValue(){
            return this.value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }


    /**
     * 支持的文件类型后缀
     */
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

    public void setIsPause(boolean isPause){
        this.isPause.setValue(isPause);
    }

    public void setIsCancel(boolean isCancel){
        this.isCancel.setValue(isCancel);
    }

    public interface IDownLoadListener{
        void onStart(long startPos); // 开始
        void onPause(long pausePos); // 暂停
        void onResume(long resumePos); // 恢复下载
        void onSuccess(File file); // 下载成功
        void onError(String msg); // 下载失败
        void onCancel();  // 取消下载
        void onListener(long currentPos, long totalLength);  // 监听下载进度
    }

    public abstract static class DownLoadListener implements IDownLoadListener{

        @Override
        public void onStart(long startPos) {
            Log.d(TAG, "DownLoadListener  ==>  onStart");
        }

        @Override
        public void onPause(long pausePos) {
            Log.d(TAG, "DownLoadListener  ==>  onPause");
        }

        @Override
        public void onResume(long resumePos) {
            Log.d(TAG, "DownLoadListener  ==>  onResume");
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "DownLoadListener  ==>  onCancel");
        }
    }

    public void download(DownLoadListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File path = buildPath(cachePath);
                HttpURLConnection connection = null;
                try {
                    URL url1 = new URL(url);
                    connection = (HttpURLConnection) url1.openConnection();
                    connection.setConnectTimeout(connectionTimeout);
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("accept", "*/*");
                    connection.connect();

                    // 获取文件总长度
                    totalLength = connection.getContentLength();

                    if (suffix == null) {
                        throw new RuntimeException("You must set the download file suffix before.");
                    }

                    File file = new File(path, EncoderUtils.hashKeyFromUrl(url) + "." + suffix.getValue());

                    startPoint = file.length();

                    connection.disconnect();

                    // 重新请求一次
                    connection = (HttpURLConnection) url1.openConnection();
                    connection.setConnectTimeout(connectionTimeout);
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("Range", "bytes=" + startPoint + "-" );

                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
                    randomAccessFile.seek(startPoint);

                    InputStream inputStream = connection.getInputStream();

                    if(startPoint == 0 && listener != null) {
                        listener.onStart(0);
                    }

                    if (connection.getResponseCode() == 206) {
                        byte[] buffer = new byte[2048];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1) {
                            randomAccessFile.write(buffer, 0, len);
                            startPoint += len;

                            if(listener != null) listener.onListener(startPoint, totalLength);

                            if(isPause.getValue()){
                                Log.d(TAG, "Download paused!");
                                if(listener != null) listener.onPause(startPoint);
                                connection.disconnect();
                                isPause.setValue(false); // 重置
                                return;
                            }
                        }
                        Log.d(TAG, "Download successful.");
                        if(listener != null) listener.onSuccess(file);
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
