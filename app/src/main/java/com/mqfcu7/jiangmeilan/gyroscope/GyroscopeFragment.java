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

import com.dd.CircularProgressButton;

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

    @BindView(R.id.gyroscope_surface_view)
    GyroscopeSurfaceView mGyroscope;
    @BindView(R.id.rotate_button)
    CircularProgressButton mRotateButton;

    ControlPadFragment mControlPad;

    private ScheduledExecutorService mScheduledExecutor;

    public void setControlPad(ControlPadFragment controlPad) {
        mControlPad = controlPad;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gyroscope, container, false);
        unbinder = ButterKnife.bind(this, v);

        mGyroscope.setZOrderOnTop(true);
        mGyroscope.setControlPad(mControlPad);

        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnTouch({R.id.rotate_button, R.id.rotate_button_layout})
    public boolean onRotateTouch(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("TAG", "ACTION_DOWN");
            if (mGyroscope.isRotating()) {
                return true;
            }
            Log.d("TAG", "setProgress");
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
            Log.d("TAG", "ACTION_UP");
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
}
