package com.weizu.mylibrary.utils;

import android.os.Build;
import android.os.StatFs;

import java.io.File;

public class MemoryUtils {

    /**
     * 获取可用的空间大小
     * @param path 文件目录
     * @return
     */
    public static long getUsableSpace(File path){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){ // API 9
            return path.getUsableSpace();
        }
        StatFs statFs = new StatFs(path.getPath());
        return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
    }
}
