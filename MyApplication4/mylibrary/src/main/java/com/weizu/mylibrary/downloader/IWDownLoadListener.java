package com.weizu.mylibrary.downloader;

import java.io.File;

public interface IWDownLoadListener {
    void onSuccess(File file); // 下载成功
    void onError(String msg); // 下载失败
    void onProgress(long currentPos, long totalLength); // 监听下载进度【外部-用户】
    void onListener(long currentPos, long totalLength); // 监听下载进度【内部-代码逻辑】
}