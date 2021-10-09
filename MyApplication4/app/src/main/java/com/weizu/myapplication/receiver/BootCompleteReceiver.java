package com.weizu.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.weizu.myapplication.service.NotificatioinService;
import com.weizu.myapplication.utils.ShowNotification;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "START", Toast.LENGTH_LONG).show();
        Intent service = new Intent();
        service.setClass(context, NotificatioinService.class);
        context.startService(service);

    }
}
