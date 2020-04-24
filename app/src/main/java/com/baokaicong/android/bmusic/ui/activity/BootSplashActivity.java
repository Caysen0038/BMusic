package com.baokaicong.android.bmusic.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;

public class BootSplashActivity extends AppCompatActivity {
    private static final String NAME="boot";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_splash);
        BMContext.builder().addActivity(NAME,this);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BMContext.builder().removeActivity(NAME);
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
