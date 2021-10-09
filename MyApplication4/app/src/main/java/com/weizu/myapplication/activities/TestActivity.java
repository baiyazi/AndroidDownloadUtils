package com.weizu.myapplication.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.weizu.myapplication.MainActivity;
import com.weizu.myapplication.R;
import com.weizu.myapplication.custom.ImageLoader;
import com.weizu.myapplication.custom.test.ImageAdapter;
import com.weizu.myapplication.views.WImageView;
import com.weizu.myapplication.views.interfaces.WOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        GridView gridView = findViewById(R.id.gridView);

        // 请求读写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }


        List<String> urls = new ArrayList<>();
        urls.add("https://img0.baidu.com/it/u=3299185958,3315182329&fm=26&fmt=auto");
        urls.add("https://s2.ax1x.com/2019/05/06/EDt7JU.jpg");
        urls.add("https://img1.baidu.com/it/u=1392895402,4020675965&fm=26&fmt=auto");
        urls.add("https://img1.baidu.com/it/u=3813130746,376843079&fm=26&fmt=auto");
        urls.add("https://img1.baidu.com/it/u=3699549905,669894451&fm=26&fmt=auto");
        urls.add("https://img1.baidu.com/it/u=2422611511,3998750314&fm=26&fmt=auto");
        urls.add("https://img1.baidu.com/it/u=1007713492,1060045714&fm=26&fmt=auto");
        urls.add("https://img0.baidu.com/it/u=3915198515,645722238&fm=26&fmt=auto");
        urls.add("https://img0.baidu.com/it/u=3218460389,4174616389&fm=26&fmt=auto");
        urls.add("https://img2.baidu.com/it/u=2875414781,1848488770&fm=26&fmt=auto");
        urls.add("https://img0.baidu.com/it/u=284739302,460288971&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=750");
        urls.add("https://img2.baidu.com/it/u=1399377131,2012790155&fm=26&fmt=auto");

        ImageAdapter imageAdapter = new ImageAdapter(this, urls, R.layout.gridview_item);
        gridView.setAdapter(imageAdapter);
    }
}























