package xyz.zzulu.savethecolor.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.zzulu.savethecolor.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();

        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goLogin = new Intent(SplashActivity.this,IntroActivity.class);
                startActivity(goLogin);
                finish();
            }
        }, 1000);

    }
}
