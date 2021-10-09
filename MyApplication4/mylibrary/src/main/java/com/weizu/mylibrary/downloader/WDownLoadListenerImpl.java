package com.weizu.mylibrary.downloader;

public abstract class WDownLoadListenerImpl implements IWDownLoadListener {
    private static final String TAG = "DownLoadListenerImpl";

    @Override
    public void onListener(long currentPos, long totalLength) {
        // todo 删除sharedpreferences文件
        if(currentPos == totalLength){
            WDownloadSpHelper.deleteSpFile();
        }
        onProgress(currentPos, totalLength);
    }
}
