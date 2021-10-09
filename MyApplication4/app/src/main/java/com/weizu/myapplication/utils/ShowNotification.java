package com.weizu.myapplication.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.weizu.myapplication.MainActivity;
import com.weizu.myapplication.R;

public class ShowNotification {

    public static String channelId = "weizu"; // 通道ID


    public static void show(Context context, int id, String msg){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Android 8.0 即26之后的创建通知方式
            // 1. 创建通知管理器
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, "simple test",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
            // 通知点击后响应跳转
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            // 2. 创建通知(标题、内容、图标)
            Notification notification = builder.setContentTitle("This is the title.")
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_launcher_foreground))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true) // 设置点击后消失
                    .build();
            // 3. 发送通知
            manager.notify(id, notification);
        }else{
            // 1. 创建通知管理器
            NotificationManager manager = (NotificationManager) context.getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知点击后响应跳转
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            // 2. 创建通知(标题、内容、图标)
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
            Notification notification = builder.setContentTitle("This is the title.")
                    .setContentText("This is the content.")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true) // 设置点击后消失
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_launcher_foreground))
                    .build();

            // 3. 发送通知
            manager.notify(id, notification);
        }
    }
}
