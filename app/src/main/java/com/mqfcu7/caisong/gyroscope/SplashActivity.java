package com.mqfcu7.caisong.gyroscope;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends Activity {
    GyroscopeView mGyroscope;
    Button mRotateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
