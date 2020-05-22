package com.stoptherape.stoptherape;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread mThread = new Thread(){

            @Override
            public void run() {
                try {
                    sleep(400);
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    finish();
                    startActivity(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

    }
}
