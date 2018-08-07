package com.mqfcu7.jiangmeilan.gyroscope;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingFragment extends Fragment {
    private final static int MIN_SECTIONS_NUM = 2;

    private Unbinder unbinder;

    @BindView(R.id.gyroscope_setting_view)
    GyroscopeSurfaceView mGyroscope;
    @BindView(R.id.sections_num_text)
    TextView mSectionsNumText;
    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;
    @BindView(R.id.setting_banner_ad_layout)
    LinearLayout mBannerAdLayout;

    Database mDatabase;

    private IFLYBannerAd bannerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, v);

        mGyroscope.setArrowEnable(false);
        mGyroscope.setZOrderOnTop(true);

        mDatabase = new Database(getContext());
        Database.GyroscopeData data = mDatabase.getQuestionRecord("轮到谁");
        mGyroscope.setGyroscopeData(data);
        mSeekBar.setProgress(data.sectionsNum - MIN_SECTIONS_NUM);
        mSectionsNumText.setText("块数：" + data.sectionsNum);
        mDatabase.updateSettingData(data.title, data.sectionsNum, data.sectionsAngle,
                data.sectionsName, Integer.MAX_VALUE, false);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += MIN_SECTIONS_NUM;
                mSectionsNumText.setText("块数：" + progress);
                mGyroscope.setSectionsNum(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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

    private void createBannerAd(View v) {
        String adUnitId = "883375EA675C2F1550FF04104CDCBD67";

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

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onConfirm() {
            // TODO Auto-generated method stub

        }
    };

}
