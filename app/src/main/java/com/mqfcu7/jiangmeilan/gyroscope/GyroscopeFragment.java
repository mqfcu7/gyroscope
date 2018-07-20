package com.mqfcu7.jiangmeilan.gyroscope;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import butterknife.Unbinder;

public class GyroscopeFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.title_flow_layout)
    FlowLayout mFlowLayout;
    @BindView(R.id.gyroscope_surface_view)
    GyroscopeSurfaceView mGyroscope;
    @BindView(R.id.rotate_button)
    CircularProgressButton mRotateButton;
    @BindView(R.id.gyroscope_banner_ad_layout)
    LinearLayout mBannerAdLayout;

    Database mDatabase;
    ControlPadFragment mControlPad;
    List<TextView> mTitles;
    private IFLYBannerAd bannerView;

    private ScheduledExecutorService mScheduledExecutor;

    public void setControlPad(ControlPadFragment controlPad) {
        mControlPad = controlPad;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gyroscope, container, false);
        unbinder = ButterKnife.bind(this, v);
        mDatabase = new Database(getContext());
        createTitle();

        mGyroscope.setZOrderOnTop(true);
        mGyroscope.setControlPad(mControlPad);

        createBannerAd(v);

        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
        super.onDestroyView();
    }

    @OnTouch({R.id.rotate_button, R.id.rotate_button_layout})
    public boolean onRotateTouch(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (mGyroscope.isRotating()) {
                return true;
            }
            mRotateButton.setProgress(1);
            mScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            mScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    if (mRotateButton.getProgress() == 0) {
                        return;
                    }
                    Message msg = new Message();
                    handler.sendMessage(msg);
                }
            }, 0, 20, TimeUnit.MILLISECONDS);
        } else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mScheduledExecutor != null) {
                mScheduledExecutor.shutdownNow();
                mScheduledExecutor = null;
                mGyroscope.onRotate(mRotateButton.getProgress() / 100.0f);
            }
            mRotateButton.setProgress(0);
        }

        return true;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mRotateButton.getProgress() < 99) {
                if (mRotateButton.getProgress() == 0) {
                    return;
                }
                mRotateButton.setProgress(mRotateButton.getProgress() + 1);
            }
        }
    };

    private void createTitle() {
        Database.GyroscopeData gyroscopeData = mDatabase.getSettingData();

        mTitles = new ArrayList<>();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 10, 5);
        ArrayList<String> titles = mDatabase.getQuestionTitle();
        for (int i = 0; i < titles.size(); ++ i) {
            LinearLayout layout = new LinearLayout(getContext());
            TextView view = new TextView(getContext());
            if (gyroscopeData.title.equals(titles.get(i))) {
                view.setBackground(getResources().getDrawable(R.drawable.button_selected_style));
            } else {
                view.setBackground(getResources().getDrawable(R.drawable.button_style));
            }
            view.setTextColor(getResources().getColor(R.color.cpb_white));
            view.setLayoutParams(lp);
            view.setText(titles.get(i));
            view.setTextSize(14);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        String title = ((TextView)v).getText().toString();
                        Database.GyroscopeData data = mDatabase.getQuestionRecord(title);
                        mGyroscope.setNewTitle(data.title, data.sectionsNum, data.sectionsAngle, data.sectionsName);

                        for (int i = 0; i < mTitles.size(); ++ i) {
                            mTitles.get(i).setBackground(getResources().getDrawable(R.drawable.button_style));
                        }
                        v.setBackground(getResources().getDrawable(R.drawable.button_selected_style));
                    }
                    return false;
                }
            });
            mTitles.add(view);
            layout.addView(view);
            mFlowLayout.addView(layout);
        }
    }

    private void createBannerAd(View v) {
        String adUnitId = "413F339B7BAB5A71F6120204DADD162C";

        bannerView = IFLYBannerAd.createBannerAd(getContext(), adUnitId);
        if (bannerView == null) return;
        bannerView.setAdSize(IFLYAdSize.BANNER);

        bannerView.loadAd(mAdListener);

        mBannerAdLayout.removeAllViews();
        mBannerAdLayout.addView(bannerView);
    }

    IFLYAdListener mAdListener = new IFLYAdListener() {
        @Override
        public void onAdReceive() {
            if (bannerView != null) {
                bannerView.showAd();
            }
        }

        @Override
        public void onAdFailed(AdError adError) {

        }

        @Override
        public void onAdClick() {

        }

        @Override
        public void onAdClose() {

        }

        @Override
        public void onAdExposure() {

        }

    };
}
