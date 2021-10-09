package com.weizu.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.weizu.myapplication.R;
import com.weizu.mylibrary.downloader.MultiThreadDownLoader;
import com.weizu.mylibrary.downloader.SingleThreadBreakpointDownloader;
import com.weizu.mylibrary.downloader.WDownLoadListenerImpl;
import com.weizu.mylibrary.downloader.WMultiThreadBreakpointDownloader;
import com.weizu.mylibrary.downloader.WFileSuffix;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class ThreeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button start;
    private ProgressBar progressbar;
    private MultiThreadDownLoader downloader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        start = findViewById(R.id.start);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setMax(100);

        // MultiThreadDownLoader 不提供进度，因为不需要断点下载
        downloader = new MultiThreadDownLoader.Builder(this)
                .url("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
                .suffix(MultiThreadDownLoader.FileSuffix.MP4)
                .method("GET")
                .cacheDirName("MP4")
                .build();

        start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        downloader.download(new MultiThreadDownLoader.DownloadListener() {
            @Override
            public void onSuccess(File file) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ThreeActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String msg) {

            }
        });
    }
}
