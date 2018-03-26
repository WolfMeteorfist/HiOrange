package com.yuanshi.hiorange.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.activity.FingerActivity;
import com.yuanshi.hiorange.activity.MainActivity;
import com.yuanshi.hiorange.activity.UnbindActivity;
import com.yuanshi.hiorange.activity.VoiceActivity;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.view.MyImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Zyc
 * @date 2018/2/7
 */

public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    protected Activity mActivity;
    @BindView(R.id.img_mine_head)
    MyImageView mImgMineHead;
    @BindView(R.id.ll_mine_unbinding)
    LinearLayout mLlMineBinding;
    @BindView(R.id.ll_mine_voice)
    LinearLayout mLlMineVoice;
    @BindView(R.id.ll_mine_finger)
    LinearLayout mLlMineFinger;
    @BindView(R.id.ll_mine_firware_update)
    LinearLayout mLlMineFirwareUpdate;
    @BindView(R.id.ll_mine_alarm_info)
    LinearLayout mLlMineAlarmInfo;
    @BindView(R.id.ll_mine_about_app)
    LinearLayout mLlMineAboutApp;
    Unbinder unbinder;
    private String mPhoneNumber;
    private String mPassWord;
    private String mBoxId;
    private Bundle mBundle;
    private int REQUEST_CODE = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity != null) {
            mPhoneNumber = ((MainActivity) mActivity).getPhoneNumber();
            mPassWord = ((MainActivity) mActivity).getPassWord();
            mBoxId = ((MainActivity) mActivity).getBoxId();
            mBundle = new Bundle();
            mBundle.putString(FinalString.PHONE, mPhoneNumber);
            mBundle.putString(FinalString.PASSWORD, mPassWord);
            mBundle.putString(FinalString.BOX_ID, mBoxId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.ll_mine_unbinding, R.id.ll_mine_about_app, R.id.ll_mine_alarm_info,
            R.id.ll_mine_finger, R.id.ll_mine_firware_update, R.id.ll_mine_voice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_mine_about_app:
                break;
            case R.id.ll_mine_alarm_info:
                break;
            case R.id.ll_mine_finger:
                ((MainActivity) mActivity).startActivities(FingerActivity.class, mBundle);
                break;
            case R.id.ll_mine_firware_update:
                break;
            case R.id.ll_mine_voice:
                ((MainActivity) mActivity).startActivities(VoiceActivity.class, mBundle);
                break;
            case R.id.ll_mine_unbinding:
                Intent intent = new Intent(mActivity,UnbindActivity.class);
                intent.putExtras(mBundle);
                mActivity.startActivityForResult(intent,1);
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: ");
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
