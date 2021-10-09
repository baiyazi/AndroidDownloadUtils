package com.weizu.myapplication.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.weizu.myapplication.okhttp.TestNotification;
import com.weizu.myapplication.service.NotificatioinService;
import com.weizu.myapplication.utils.ShowNotification;

public class DoAdd extends AsyncTask<Void, Void, Void> {

    private Context context;
    public DoAdd(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (true){
            TestNotification.add();

            TestNotification.get(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if(msg.what == 1){
                        int val = msg.arg1;
                        if(val % 200 == 0){
                            ShowNotification.show(context, 1,"" + msg.arg1);
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("TAG", "onPreExecute: ");
    }
}
