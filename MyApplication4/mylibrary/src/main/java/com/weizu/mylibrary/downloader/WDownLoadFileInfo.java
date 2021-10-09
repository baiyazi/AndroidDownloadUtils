package com.weizu.mylibrary.downloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.weizu.mylibrary.utils.EncoderUtils;

import java.io.File;

public class WDownLoadFileInfo {
    private String url;          // 文件链接
    private String cacheDir = "WCache";    // 文件缓存目录
    private WFileSuffix suffix;  // 文件后缀
    private long startPosition, endPosition; // 需要下载的起始位置和结束位置
    private long totalSize;  // 文件总大小
    private long currentPosition; // 当前下载到什么地方
    private Context context;
    private SharedPreferences preferences;
    private int index;
    private WDownLoadFileInfo(){}

    public WDownLoadFileInfo(Context context, String url,
                             WFileSuffix suffix, long startPosition,
                             long endPosition, long totalSize, int index){
        this.context = context;
        this.url = url;
        this.suffix = suffix;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.totalSize = totalSize;
        this.currentPosition = 0;
        preferences = WDownloadSpHelper.getSharedPreferences(context);
        this.index = index;
    }

    public void setCacheDir(String cacheDir){
        this.cacheDir = cacheDir;
    }

    public String getCacheDir(){
        return cacheDir;
    }

    public void addCurrentPosition(int index, long increment){
        // todo 存储当前的线程下载的位置
        this.currentPosition += increment;
        WDownloadSpHelper.storageDownloadPosition(index, this.currentPosition);
    }

    public long getCurrentPosition(int index){
        // todo 从sharedpreference中读取数据
        long position = WDownloadSpHelper.readDownloadPosition(index);
        this.currentPosition = position;
        return position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WFileSuffix getSuffix() {
        return suffix;
    }

    public void setSuffix(WFileSuffix suffix) {
        this.suffix = suffix;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(long endPosition) {
        this.endPosition = endPosition;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * 获取存储文件的File对象
     * @return File
     */
    public File getFile(){
        File file = buildPath(cacheDir);
        String fileName = EncoderUtils.hashKeyFromUrl(this.url) + "." + suffix.getValue();
        return new File(file, fileName);
    }





    /**
     * 判断应用缓存目录下是否存在这个cacheDir目录，没有就创建。
     * 同时，如果有SD卡，就优先存储在SD卡中。
     * @param cacheDir 缓存目录
     * @return 缓存目录File对象
     */
    private File buildPath(String cacheDir) {
        // 是否有SD卡
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        // 如果有SD卡就存在外存，否则就位于这个应用的data/package name/cache目录下
        final String cachePath;
        if(flag) cachePath = context.getExternalCacheDir().getPath();
        else cachePath = context.getCacheDir().getPath();

        File directory = new File(cachePath + File.separator + cacheDir);
        // 目录不存在就创建
        if(!directory.exists()) directory.mkdirs();
        return directory;
    }
}
