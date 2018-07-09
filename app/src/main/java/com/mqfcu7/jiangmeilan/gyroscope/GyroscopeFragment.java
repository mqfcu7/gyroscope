package com.mqfcu7.jiangmeilan.gyroscope;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GyroscopeFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.gyroscope_surface_view)
    GyroscopeSurfaceView mGyroscope;

    ControlPadFragment mControlPad;

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
}
