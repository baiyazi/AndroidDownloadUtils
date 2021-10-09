package com.weizu.mylibrary.downloader;

public class WDownloadControl {

    private WDownloadControl(){}
    private static volatile boolean isPause = false;

    public static void pause() {
        synchronized (WDownloadControl.class){
            isPause = true;
        }
    }

    public static void restart(){
        synchronized (WDownloadControl.class){
            isPause = false;
        }
    }

    public static boolean isIsPause() {
        return isPause;
    }
}
