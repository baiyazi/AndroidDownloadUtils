package com.weizu.myapplication.checkservice;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class IsRuning {

    public static boolean checkServiceRun(Context context, String className){
        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = 
                manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
            if(runningService.service.getClassName().equals(className)){
                return true;
            }
        }
        return false;
    }
}
