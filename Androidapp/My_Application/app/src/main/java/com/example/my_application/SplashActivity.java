package com.example.my_application;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // 使用 Handler 在一段时间后启动 MainActivity 并关闭此 SplashActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 创建一个意图，用于启动 MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                // 关闭当前活动
                finish();
            }
        }, 2000); // 2秒后启动主活动
    }
}