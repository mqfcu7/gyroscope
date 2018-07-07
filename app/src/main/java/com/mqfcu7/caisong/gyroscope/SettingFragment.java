package com.mqfcu7.caisong.gyroscope;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

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

    Database mDatabase;

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
        Database.GyroscopeData data = mDatabase.getSettingData();
        mSeekBar.setProgress(data.sectionsNum - MIN_SECTIONS_NUM);
        mSectionsNumText.setText("块数：" + data.sectionsNum);

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

        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }



}
