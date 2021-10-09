package com.weizu.mylibrary.downloader;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


public class WDownloadThread extends Thread{
    private static final String TAG = "DownloadThread";
    private long startPos, endPos, maxFileSize, currentPosition;
    private File file;
    private String url;
    private IWDownLoadListener listener;
    private int curIndex;
    private WDownLoadFileInfo fileInfo;

    private WDownloadThread(){}
    public WDownloadThread(WDownLoadFileInfo fileInfo, int index) {
        this.startPos = fileInfo.getStartPosition();
        this.endPos = fileInfo.getEndPosition();
        this.url = fileInfo.getUrl();
        this.file = fileInfo.getFile();
        this.maxFileSize = fileInfo.getTotalSize();
        this.currentPosition = fileInfo.getCurrentPosition(index);
        this.curIndex = index;
        this.fileInfo = fileInfo;
    }

    public void setDownloadListener(IWDownLoadListener listener){
        this.listener = listener;
    }

    @Override
    public void run() {
        if((startPos + currentPosition) == endPos){
            return;
        }
        // 开始下载
        WDownloadControl.restart();
        HttpURLConnection connection = null;
        URL url_c = null;
        InputStream inputStream = null;
        try{
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.file, "rwd");
            // 设置写入文件的开始位置
            randomAccessFile.seek(this.startPos + this.currentPosition);
            url_c = new URL(url);
            connection = (HttpURLConnection) url_c.openConnection();
            connection.setConnectTimeout(5 * 1000); // 5秒钟超时
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Range", "bytes=" + (this.startPos + this.currentPosition) +"-" + endPos);

            Log.e(TAG, Thread.currentThread().getName() + "请求数据范围：bytes=" + (this.startPos + this.currentPosition) + "-" + endPos);
            inputStream = connection.getInputStream();

            if (connection.getResponseCode() == 206) {
                byte[] buffer = new byte[1024 * 1024 * 10];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, len);
                    // todo 【下载进度】
                    this.fileInfo.addCurrentPosition(curIndex, len);
                    WDownloadProgress.addProgressVal(len);
                    if(null != listener) listener.onListener(WDownloadProgress.getProgressVal(), maxFileSize);
                    // todo 【Pause】
                    if(WDownloadControl.isIsPause()){
                        Log.d(TAG, "Download paused!");
                        if(connection != null) connection.disconnect();
                        if(inputStream != null) inputStream.close();
                        return;
                    }
                }
            }
        }catch (IOException e){
            Log.e(TAG, "Download bitmap failed.", e);
        }finally {
            try{
                if(inputStream != null) inputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(connection != null) connection.disconnect();
        }
    }
}
