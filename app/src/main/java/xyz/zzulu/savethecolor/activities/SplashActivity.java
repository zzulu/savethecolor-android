package xyz.zzulu.savethecolor.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import xyz.zzulu.savethecolor.R;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Handler hd = new Handler();

        hd.postDelayed(new Runnable() {
            @Override
            public void run() {

                mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

                if (mPreferences.getString("AuthToken","").equals("")){
                    Intent goLogin = new Intent(SplashActivity.this,IntroActivity.class);
                    startActivity(goLogin);
                    finish();
                } else {
                    Intent goMain = new Intent(SplashActivity.this,MainActivity.class);
                    goMain.putExtra("auto",true);
                    startActivity(goMain);
                    finish();
                }

            }
        }, 1000);

    }
}
