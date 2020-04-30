package com.krunal_shah.emapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.krunal_shah.emapp.R;

public class SplashScreenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreenActivity.this, IntroMainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}