package com.example.chen.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import chen.upload.UploadService;

public class CachingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caching);

        IntentFilter filter = new IntentFilter(UploadService.ACTION_START);
        filter.addAction(UploadService.ACTION_START);
        filter.addAction(UploadService.ACTION_ONPROGRESS);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(CachingActivity.this,"广播启动",Toast.LENGTH_SHORT).show();
            }
        },filter);
    }

}
