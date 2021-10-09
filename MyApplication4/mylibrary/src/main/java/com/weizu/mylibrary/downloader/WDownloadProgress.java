package com.weizu.mylibrary.downloader;

public class WDownloadProgress {
    private static volatile long progressVal = 0;
    private WDownloadProgress(){}

    public static void resetVal(){
        synchronized (WDownloadThread.class){
            progressVal = 0;
        }
    }

    public static void addProgressVal(long val) {
        synchronized (WDownloadThread.class){
            progressVal = progressVal + val;
        }
    }

    public static long getProgressVal(){
        return progressVal;
    }

    public static void init(){
        progressVal = 0;
    }
}
