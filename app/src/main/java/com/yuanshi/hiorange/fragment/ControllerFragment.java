package com.yuanshi.hiorange.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuanshi.hiorange.R;

/**
 * Created by Administrator on 2018/2/9.
 */

public class ControllerFragment extends Fragment {

    private static final String TAG = "ControllerFragment";
    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.controller_fragment, null);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (mActivity != null) {
            mActivity.setRequestedOrientation(hidden ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
