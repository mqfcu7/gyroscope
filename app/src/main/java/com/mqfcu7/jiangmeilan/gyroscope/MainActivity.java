package com.mqfcu7.jiangmeilan.gyroscope;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    ControlPadFragment mControlPad;

    GyroscopeFragment mGyroscopeFragment;
    HistoryFragment mHistoryFragment;
    SettingFragment mSettingFragment;
    GameFragment mGameFragment;
    Fragment mCurrentFragment;

    public static String permissionArray[] = {
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        requestPermission();

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
                mGyroscopeFragment.setControlPad(mControlPad);
                fm.beginTransaction().add(R.id.main_container, mGyroscopeFragment).commit();
                mCurrentFragment = mGyroscopeFragment;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void onGyroscopeFragment() {
        if (mCurrentFragment == mGyroscopeFragment) {
            return;
        }

        mGyroscopeFragment = new GyroscopeFragment();
        mGyroscopeFragment.setControlPad(mControlPad);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_container, mGyroscopeFragment);
        transaction.commit();
        mCurrentFragment = mGyroscopeFragment;
    }

    public void onHistoryFragment() {
        if (mCurrentFragment == mHistoryFragment) {
            return;
        }

        mHistoryFragment = new HistoryFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_container, mHistoryFragment);
        transaction.commit();
        mCurrentFragment = mHistoryFragment;
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

    public void onGameFragment() {
        if (mCurrentFragment == mGameFragment) {
            return;
        }

        mGameFragment = new GameFragment();
        mGameFragment.setControlPad(mControlPad);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_container, mGameFragment);
        transaction.commit();
        mCurrentFragment = mGameFragment;
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionList = new ArrayList<>();
            for (String permission : permissionArray) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            if (permissionList.size() > 0) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), 100);
            }
            if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
            }
        }
    }
}
