package com.weizu.myapplication.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.weizu.myapplication.okhttp.TestNotification;
import com.weizu.myapplication.utils.ShowNotification;

public class NotificatioinService extends IntentService {

    private static final String TAG = "NotificatioinService";

    public NotificatioinService(){
        this("Defalut");
    }

    public NotificatioinService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // todo  请求最新消息
        // ShowNotification.show(getApplicationContext(), "NotificatioinService Message!");
        while (true){
            TestNotification.add();

            TestNotification.get(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if(msg.what == 1){
                        int val = msg.arg1;
                        if(val % 20 == 0){
                            ShowNotification.show(NotificatioinService.this, 1, "" + msg.arg1);
                        }
                    }
                }
            });
            try{
                Thread.sleep(50);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
