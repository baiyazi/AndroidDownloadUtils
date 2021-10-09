package com.weizu.mylibrary.downloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class WDownloadSpHelper {
    private static final String TAG = "DownloadSpHelper";
    private static Context context = null;
    private static volatile SharedPreferences preferences = null;

    private WDownloadSpHelper(){}

    public static SharedPreferences getSharedPreferences(Context c){
        if(preferences == null){
            synchronized (WDownloadSpHelper.class){
                if(preferences == null){
                    if(null == context && null != c){
                        preferences = c.getApplicationContext().
                                getSharedPreferences("WDownload", Context.MODE_PRIVATE);
                        context = c.getApplicationContext();
                    }else{
                        preferences = context.getApplicationContext().
                                getSharedPreferences("WDownload", Context.MODE_PRIVATE);
                    }
                }
            }
        }
        return preferences;
    }

    public static void storageDownloadPosition(int index, long pos){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong("" + index, pos);
        edit.apply();
    }

    public static long readDownloadPosition(int index){
        return preferences.getLong("" + index, 0);
    }

    public static void deleteSpFile(){
        Log.e(TAG, "正在删除Sharedpreferences文件。");
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.apply();
    }
}
