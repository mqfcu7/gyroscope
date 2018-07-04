package com.mqfcu7.caisong.gyroscope;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    GyroscopeView mGyroscope;
    Button mRotateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGyroscope = (GyroscopeView) findViewById(R.id.gyroscope_view);
        mRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGyroscope.onRotateArrow();
            }
        });
    }
}
