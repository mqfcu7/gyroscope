package com.mqfcu7.caisong.gyroscope;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GyroscopeActivity extends Activity {

    @BindView(R.id.rotate_button)
    public Button mRotateButton;
    @BindView(R.id.gyroscope_surface_view)
    public GyroscopeSurfaceView mGyroscopeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        ButterKnife.bind(this);

        mGyroscopeView.setZOrderOnTop(true);
    }

    @OnClick(R.id.rotate_button)
    public void onRotateButtonClick() {
        mGyroscopeView.onRotate();
    }
}
