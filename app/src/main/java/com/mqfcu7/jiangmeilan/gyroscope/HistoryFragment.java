package com.mqfcu7.jiangmeilan.gyroscope;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.history_recycler_view)
    RecyclerView mHistoryRecyclerView;

    Database mDatabase;
    private HistoryAdapter mAdapter;

    public class HistoryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.history_gyroscope_view)
        GyroscopeView mGyroscopeView;
        @BindView(R.id.history_title_text)
        TextView mTitleText;
        @BindView(R.id.history_desc_text)
        TextView mDescText;

        Database.GyroscopeData mGyroscope;

        public HistoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bindGyroscope(Database.GyroscopeData gyroscope) {
            mGyroscope = gyroscope;

            SimpleDateFormat titleDateFormat = new SimpleDateFormat("HH:mm:ss");
            mTitleText.setText(titleDateFormat.format(new Date(gyroscope.time)));

            SimpleDateFormat descDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA);
            mDescText.setText(descDateFormat.format(new Date(gyroscope.time)));

            mGyroscopeView.setGyroscopeData(gyroscope);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {
        private List<Database.GyroscopeData> mGyroscopes;

        public HistoryAdapter(List<Database.GyroscopeData> gyroscopes) {
            mGyroscopes = gyroscopes;
            Log.d("TAG", "size: " + mGyroscopes.size());
        }

        @NonNull
        @Override
        public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_gyroscope, parent, false);
            return new HistoryHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
            Database.GyroscopeData gyroscope = mGyroscopes.get(position);
            holder.bindGyroscope(gyroscope);
        }

        @Override
        public int getItemCount() {
            return mGyroscopes.size();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, v);

        mDatabase = new Database(getContext());
        mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new HistoryAdapter(mDatabase.getAllHistoryGyroscope());
            mHistoryRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
