package com.weizu.myapplication.okhttp;

import android.os.Handler;
import android.os.Message;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestNotification {

    private static OkHttpClient client;
    static {
        client = new OkHttpClient();
    }

    public static void add(){


        Request request = new Request.Builder()
                .url("http://192.168.1.102:90/addNumber")
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure( Call call,  IOException e) {

            }

            @Override
            public void onResponse( Call call,  Response response) throws IOException {

            }
        });

    }

    public static void get(Handler handler){
        Request request = new Request.Builder()
                .url("http://192.168.1.102:90/getNumber")
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure( Call call,  IOException e) {
            }

            @Override
            public void onResponse(Call call,  Response response) throws IOException {
                String result = response.body().string();
                int s = Integer.parseInt(result);
                Message message = new Message();
                message.what = 1;
                message.arg1 = s;
                handler.sendMessage(message);
            }
        });
    }
}
