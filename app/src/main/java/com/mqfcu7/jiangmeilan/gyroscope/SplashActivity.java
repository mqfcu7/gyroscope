package com.mqfcu7.jiangmeilan.gyroscope;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
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
    @BindView(R.id.splash_ad_container)
    RelativeLayout mSplashAdContainer;

    private boolean mFlag = false;


    public static String permissionArray[] = {
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private IFLYNativeAd fullScreenAd;
    private NativeADDataRef adItem;
    private CountDownTimer timer;

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
        if (mFlag) {
            jump();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void createFullScreenAd() {
        String adSlotId = "BF50952F34DDAACDAE1FCD6A696D10B5";

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

            adItem = list.get(0);
            Glide.with(getApplicationContext())
                    .load(adItem.getImage())
                    .apply(new RequestOptions().override(mSplashImage.getWidth(), mSplashImage.getHeight()))
                    .into(mSplashImage);

            mAdFlagText.setText(adItem.getAdSourceMark() + "广告");
            timer = new CountDownTimer(3000, 3000) {
                @Override
                public void onTick(long millisUntilFinished) { }

                @Override
                public void onFinish() {
                    jump();
                }
            }.start();

            boolean isExposure = adItem.onExposured(mSplashAdContainer);

            mSplashImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adItem != null) {
                        boolean isClicked = adItem.onClicked(v);
                        mFlag  = true;
                        if (timer != null) {
                            timer.cancel();
                        }
                    }
                }
            });
            mSplashImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            fullScreenAd.setParameter(AdKeys.CLICK_POS_DX, event.getX() + "");
                            fullScreenAd.setParameter(AdKeys.CLICK_POS_DY, event.getY() + "");
                            break;
                        case MotionEvent.ACTION_UP:
                            fullScreenAd.setParameter(AdKeys.CLICK_POS_UX, event.getX() + "");
                            fullScreenAd.setParameter(AdKeys.CLICK_POS_UY, event.getY() + "");
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

        @Override
        public void onAdFailed(AdError adError) {
            jump();
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onConfirm() {
        }
    };

    public void jump() {
        if (timer != null) {
            timer.cancel();
        }
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
