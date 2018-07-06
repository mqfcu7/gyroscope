package com.mqfcu7.caisong.gyroscope;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GyroscopeActivity extends FragmentActivity {

    @BindView(R.id.gyroscope_surface_view)
    public GyroscopeSurfaceView mGyroscopeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        ButterKnife.bind(this);

        mGyroscopeView.setZOrderOnTop(true);

        /*
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.control_pad_container);
        Log.d("TAG", fragment.toString());
        if (fragment != null) {
            fragment = new ControlPadFragment();
            fm.beginTransaction().add(R.id.control_pad_container, fragment).commit();
        }
        */
    }

}
