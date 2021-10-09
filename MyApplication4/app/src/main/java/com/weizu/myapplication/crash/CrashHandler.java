package com.weizu.myapplication.crash;


import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final Context mContext;
    public CrashHandler(Context context){
        this.mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e("TAG", "uncaughtException");
        FileOutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;
        String info = ex.getLocalizedMessage().toString();
        try {
            outputStream = this.mContext.openFileOutput("text.md", Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(new
                    OutputStreamWriter(outputStream));
            bufferedWriter.write(info);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bufferedWriter != null;
                bufferedWriter.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}