package com.mqfcu7.jiangmeilan.gyroscope;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GameFragment extends Fragment {
    public static final int UPDATE_TEXT = 1;
    private static final float FACTOR = 60;

    private Unbinder unbinder;

    @BindView(R.id.game_gyroscope_surface_view)
    GyroscopeSurfaceView mGyroscope;
    @BindView(R.id.game_score_text)
    TextView mScoreText;
    @BindView(R.id.game_cash_text)
    TextView mCashText;

    Database mDatabase;
    ControlPadFragment mControlPad;

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    onUpdateGameState(msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    public void setControlPad(ControlPadFragment controlPad) {
        mControlPad = controlPad;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        unbinder = ButterKnife.bind(this, v);

        mDatabase = new Database(getContext());
        mGyroscope.setRecordEnable(false);
        mGyroscope.setZOrderOnTop(true);
        mGyroscope.setGameFragment(this);
        mGyroscope.setControlPad(mControlPad);
        mGyroscope.setGyroscopeData(initGyroscopeData());

        Database.GameData gameData = mDatabase.getGameData();
        onUpdateGameState(gameData.score, Math.min(gameData.score, 100));

        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    private Database.GyroscopeData initGyroscopeData() {
        Database.GameData gameData = mDatabase.getGameData();
        Database.GyroscopeData data = new Database.GyroscopeData();
        data.sectionsAngle = new float[] {
                120, 7.5f, 60, 15, 30, 15, 30, 15, 60, 7.5f};
        data.sectionsNum = data.sectionsAngle.length;
        data.sectionsName = new String[data.sectionsNum];
        for (int i = 0; i < data.sectionsNum; ++ i) {
            data.sectionsName[i] = sectionName(data.sectionsAngle[i]);
        }
        data.arrowAngle = gameData.arrowAngle;

        return data;
    }

    private String sectionName(float angle) {
        if (angle == 120) {
            return "0";
        }
        if (FACTOR % angle == 0 && FACTOR / angle > 2) {
            return "x " + (int)(FACTOR / angle);
        } else {
            return "0";
        }
    }

    public void onUpdateGameState(int score, int cash) {
        if (score == Integer.MAX_VALUE) return;
        mScoreText.setText("奖金：" + comdify(String.valueOf(score)));
        mCashText.setText("押注：" + comdify(String.valueOf(cash)));
    }

    private String comdify(String value) {
        DecimalFormat df = null;
        if (value.indexOf(".") > 0) {
            int i = value.length() - value.indexOf(".") - 1;
            switch (i) {
                case 0:
                    df = new DecimalFormat("###,##0");
                    break;
                case 1:
                    df = new DecimalFormat("###,##0.0");
                    break;
                case 2:
                    df = new DecimalFormat("###,##0.00");
                    break;
                case 3:
                    df = new DecimalFormat("###,##0.000");
                    break;
                case 4:
                    df = new DecimalFormat("###,##0.0000");
                    break;
                default:
                    df = new DecimalFormat("###,##0.00000");
                    break;
            }
        } else {
            df = new DecimalFormat("###,##0");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(value);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }
}
