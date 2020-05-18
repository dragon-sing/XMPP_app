package com.example.xmpp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.example.xmpp.Utils.ThreadUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //停留三秒 进入登录界面
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);

                //进入登录界面
                Intent intent =new Intent(MainActivity.this,ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
