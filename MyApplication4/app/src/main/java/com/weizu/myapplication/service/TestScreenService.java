package com.weizu.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.weizu.myapplication.receiver.BootCompleteReceiver;
import com.weizu.myapplication.task.DoAdd;

import java.util.ArrayList;
import java.util.List;

public class TestScreenService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "onStartCommand: ");
        DoAdd doAdd = new DoAdd(getApplicationContext());
        doAdd.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
