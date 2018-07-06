package com.mqfcu7.caisong.gyroscope;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ControlPadFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.gyroscope_layout)
    LinearLayout mGyroscopeLayout;
    @BindView(R.id.history_layout)
    LinearLayout mHistoryLayout;
    @BindView(R.id.setting_layout)
    LinearLayout mSettingLayout;
    @BindView(R.id.game_layout)
    LinearLayout mGameLayout;

    @BindView(R.id.gyroscope_image)
    ImageView mGyroscopeImage;
    @BindView(R.id.history_image)
    ImageView mHistoryImage;
    @BindView(R.id.setting_image)
    ImageView mSettingImage;
    @BindView(R.id.game_image)
    ImageView mGameImage;

    @BindView(R.id.gyroscope_text)
    TextView mGyroscopeText;
    @BindView(R.id.history_text)
    TextView mHistoryText;
    @BindView(R.id.setting_text)
    TextView mSettingText;
    @BindView(R.id.game_text)
    TextView mGameText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_control_pad, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void resetUI() {
        mGyroscopeImage.setImageResource(R.drawable.gyroscope);
        mHistoryImage.setImageResource(R.drawable.history);
        mSettingImage.setImageResource(R.drawable.setting);
        mGameImage.setImageResource(R.drawable.game);

        mGyroscopeText.setTextColor(getResources().getColor(R.color.colorControlPadText));
        mHistoryText.setTextColor(getResources().getColor(R.color.colorControlPadText));
        mSettingText.setTextColor(getResources().getColor(R.color.colorControlPadText));
        mGameText.setTextColor(getResources().getColor(R.color.colorControlPadText));
    }

    @OnClick(R.id.gyroscope_layout)
    public void onGyroscopeLayoutClick() {
        resetUI();

        mGyroscopeImage.setImageResource(R.drawable.gyroscope_active);
        mGyroscopeText.setTextColor(getResources().getColor(R.color.colorControlPadTextActive));
    }

    @OnClick(R.id.history_layout)
    public void onHistoryLayoutClick() {
        resetUI();

        mHistoryImage.setImageResource(R.drawable.history_active);
        mHistoryText.setTextColor(getResources().getColor(R.color.colorControlPadTextActive));
    }

    @OnClick(R.id.setting_layout)
    public void onSettingLayoutClick() {
        resetUI();

        mSettingImage.setImageResource(R.drawable.setting_active);
        mSettingText.setTextColor(getResources().getColor(R.color.colorControlPadTextActive));
    }

    @OnClick(R.id.game_layout)
    public void onGameLayoutClick() {
        resetUI();

        mGameImage.setImageResource(R.drawable.game_active);
        mGameText.setTextColor(getResources().getColor(R.color.colorControlPadTextActive));
    }
}
