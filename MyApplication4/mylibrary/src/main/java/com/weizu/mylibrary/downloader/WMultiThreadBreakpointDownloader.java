package com.weizu.mylibrary.downloader;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

public class WMultiThreadBreakpointDownloader {
    private static final String TAG = "MultiThreadBreakpointDownloader";
    private String url;
    private int connectionTimeout;
    private String method = "GET";
    private Context context;
    private String cachePath = "imgs";
    private WFileSuffix suffix;
    private long totalLength;
    private volatile boolean isPause = false;
    private Executor executor;
    private int maximumPoolSize;


    private WMultiThreadBreakpointDownloader(){}

    public WMultiThreadBreakpointDownloader(Context context){
        connectionTimeout = 500; // 500毫秒
        method = "GET";
        this.context = context;
    }

    public WMultiThreadBreakpointDownloader url(String url){
        this.url = url;
        return this;
    }

    public WMultiThreadBreakpointDownloader fileSuffix(WFileSuffix fileSuffix){
        this.suffix = fileSuffix;
        return this;
    }

    public WMultiThreadBreakpointDownloader cacheDir(String dir){
        this.cachePath = dir;
        return this;
    }


    public WMultiThreadBreakpointDownloader(Builder builder){
        this.url = builder.url;
        this.connectionTimeout = builder.connectionTimeout;
        this.context = builder.context;
        this.cachePath = builder.cachePath;
        this.suffix = builder.suffix;
        WMultiThreadDownloaderConfig config = new WMultiThreadDownloaderConfig();
        executor = config.getExecutor();
        maximumPoolSize = config.getMaximumPoolSize();
    }

    public static class Builder {
        private String url;
        private int connectionTimeout;
        private String cachePath = "imgs";
        private Context context;
        private WFileSuffix suffix;

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

        public Builder suffix(WFileSuffix suffix){
            this.suffix = suffix;
            return this;
        }

        public Builder cacheDirName(String cacheDirName){
            this.cachePath = cacheDirName;
            return this;
        }

        public WMultiThreadBreakpointDownloader build(){
            return new WMultiThreadBreakpointDownloader(this);
        }
    }

    public void setIsPause(boolean isPause){
        WDownloadControl.pause();
    }

    public void download(IWDownLoadListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                WDownloadProgress.resetVal(); // 重置进度条
                HttpURLConnection connection = null;
                File file = null;
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
                    Log.e(TAG, "文件总长度: " + totalLength);

                    // todo 分为多个线程下载
                    long step = totalLength / maximumPoolSize;
                    Log.e(TAG, "每个线程下载的数据量大小为：" + step);

                    for (int i = 0; i < maximumPoolSize; i++) {
                        WDownLoadFileInfo info = null;
                        WDownloadThread downloadThread = null;
                        if(i != maximumPoolSize - 1) {
                            info = new WDownLoadFileInfo(context, url, suffix,
                                    i * step, (i + 1) * step - 1, totalLength, i);
                        }else{
                            info = new WDownLoadFileInfo(context, url, suffix,
                                    i * step, totalLength, totalLength, i);
                        }
                        // todo 更新进度条
                        WDownloadProgress.addProgressVal(info.getCurrentPosition(i));
                        if(null != listener) listener.onListener(WDownloadProgress.getProgressVal(), totalLength);
                        info.setCacheDir(cachePath);
                        downloadThread = new WDownloadThread(info, i);
                        downloadThread.setDownloadListener(listener);
                        executor.execute(downloadThread);
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
}

