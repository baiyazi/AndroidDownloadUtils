package com.weizu.mylibrary.downloader;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WMultiThreadDownloaderConfig {
    private int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;

    public WMultiThreadDownloaderConfig(){
    }

    public WMultiThreadDownloaderConfig(int corePoolSize, int maximumPoolSize){
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public ThreadFactory getmThreadFactory() {
        return mThreadFactory;
    }

    private ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread#" + mCount.getAndIncrement());
        }
    };

    public Executor getExecutor(){
        return new ThreadPoolExecutor(corePoolSize,
                                    maximumPoolSize,
                                    10L,
                                    TimeUnit.SECONDS,
                                    new LinkedBlockingDeque<>(),
                                    mThreadFactory);
    }
}
