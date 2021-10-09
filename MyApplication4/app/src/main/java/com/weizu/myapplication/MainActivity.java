package com.weizu.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weizu.myapplication.activities.TestActivity;
import com.weizu.myapplication.crash.CrashHandler;
import com.weizu.myapplication.fragments.AboutFragment;
import com.weizu.myapplication.fragments.HomeFragment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Fragment[] fragments;
    private FragmentManager fragmentManager;
    private TextView[] tabs;
    private int currentTabIndex = 0;
    private HomeFragment homeFragment;
    private AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerCrashHandler();

        intiViews();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment, "FragmentTag_Home")
                .add(R.id.fragment_container, aboutFragment, "FragmentTag_About")
                .hide(aboutFragment)
                .show(homeFragment)
                .commit();
    }

    // TextView监听调用函数
    public void onTxtClick(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, TestActivity.class);
        startActivity(intent);
    }

    private void registerCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                Log.e("TAG", "uncaughtException: ");
                try {
                    FileOutputStream outputStream = openFileOutput("a.log", Context.MODE_APPEND);
                    StringBuilder info = new StringBuilder();
                    info.append(getTime()).append("\t").append(e.getLocalizedMessage().toString())
                            .append("\n");
                    outputStream.write(info.toString().getBytes());
                    outputStream.close();
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
        });
    }

    private String getTime(){
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // onClick监听函数必须声明为public
    public void onTabClicked(View view){
        int index = 0;
        clearTabStyle();
        switch (view.getId()){
            case R.id.bottom_home:
                index = 0;
                break;
            case R.id.bottom_about:
                index = 1;
                Fragment aboutView = fragmentManager.findFragmentByTag("FragmentTag_About");
                TextView about = aboutView.getView().findViewById(R.id.textView_about);
                about.setText("点击了！");
                break;
        }
        setTabChooseStyle(index);
        if(currentTabIndex != index){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragments[currentTabIndex]);
            if(!fragments[index].isAdded()){ // 如果没有天骄到FrameLayout
                transaction.add(R.id.fragment_container, fragments[index]);
            }
            transaction.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

    private void setTabChooseStyle(int index) {
        TextView tab = tabs[index];
        tab.setTextColor(getColor(R.color.white));
        tab.setBackgroundColor(getColor(R.color.purple_700));
    }

    private void clearTabStyle() {
        for (TextView tab : tabs) {
            tab.setTextColor(getColor(R.color.gray));
            tab.setBackgroundColor(getColor(R.color.white));
        }
    }

    private void intiViews() {
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        aboutFragment = new AboutFragment();
        fragments = new Fragment[]{ homeFragment, aboutFragment };
        tabs = new TextView[]{findViewById(R.id.bottom_home), findViewById(R.id.bottom_about)};
        setTabChooseStyle(currentTabIndex);
    }
}