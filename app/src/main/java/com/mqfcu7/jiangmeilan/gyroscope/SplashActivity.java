package com.mqfcu7.jiangmeilan.gyroscope;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends Activity {

    @BindView(R.id.splash_image)
    ImageView mSplashImage;
    @BindView(R.id.ad_flag_text)
    TextView mAdFlagText;


    public static String permissionArray[] = {
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private IFLYNativeAd fullScreenAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        requestPermission();
        createFullScreenAd();
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

    private void createFullScreenAd() {
        String adSlotId = "4A8970AE045BF02CACB834D60E7E58A8";

        fullScreenAd = new IFLYNativeAd(this, adSlotId, mAdListener);
        fullScreenAd.loadAd(1);
    }

    IFLYNativeListener mAdListener = new IFLYNativeListener() {
        @Override
        public void onADLoaded(List<NativeADDataRef> list) {
            if (list.size() == 0) {
                jump();
                return;
            }

            NativeADDataRef adItem = list.get(0);
            Glide.with(getApplicationContext())
                    .load(adItem.getImage())
                    .apply(new RequestOptions().override(mSplashImage.getWidth(), mSplashImage.getHeight()))
                    .into(mSplashImage);

            CountDownTimer timer = new CountDownTimer(2000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    jump();
                }
            }.start();
        }

        @Override
        public void onAdFailed(AdError adError) {
            jump();
        }
    };

    public void jump() {
        this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
        this.finish();
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
