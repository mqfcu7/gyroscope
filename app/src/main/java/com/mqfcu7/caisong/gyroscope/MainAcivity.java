package com.mqfcu7.caisong.gyroscope;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAcivity extends FragmentActivity {

    ControlPadFragment mControlPad;

    Fragment mGyroscopeFragment;
    Fragment mSettingFragment;
    Fragment mCurrentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FragmentManager fm = getSupportFragmentManager();
        {
            Fragment fragment = fm.findFragmentById(R.id.control_pad_container);
            if (fragment == null) {
                mControlPad = new ControlPadFragment();
                fm.beginTransaction().add(R.id.control_pad_container, mControlPad).commit();
            }
            mControlPad.setActivity(this);
        }

        {
            Fragment fragment = fm.findFragmentById(R.id.main_container);
            if (fragment == null) {
                mGyroscopeFragment = new GyroscopeFragment();
                fm.beginTransaction().add(R.id.main_container, mGyroscopeFragment).commit();
                mCurrentFragment = mGyroscopeFragment;
            }
        }
    }

    public void onGyroscopeFragment() {
        if (mCurrentFragment == mGyroscopeFragment) {
            return;
        }

        mGyroscopeFragment = new GyroscopeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_container, mGyroscopeFragment);
        transaction.commit();
        mCurrentFragment = mGyroscopeFragment;
    }

    public void onSettingFragment() {
        if (mCurrentFragment == mSettingFragment) {
            return;
        }

        mSettingFragment = new SettingFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_container, mSettingFragment);
        transaction.commit();
        mCurrentFragment = mSettingFragment;
    }

}
