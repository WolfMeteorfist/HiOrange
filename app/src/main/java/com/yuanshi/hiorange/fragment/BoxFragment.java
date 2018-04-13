package com.yuanshi.hiorange.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.activity.MainActivity;
import com.yuanshi.hiorange.bean.BoxInfo;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.Command;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.TimesCalculator;
import com.yuanshi.hiorange.view.BatteryView;
import com.yuanshi.hiorange.view.BoxInfoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Zyc
 * @date 2018/2/7
 */

public class BoxFragment extends Fragment implements IBoxView {

    private static final String TAG = "BoxFragment";
    public static final String INFO_UPDATE_ACTION = "com.yuanshi.hiorange.action.infoupdate";
    protected Activity mActivity;
    @BindView(R.id.batteryView_box)
    BatteryView mBatteryView;
    @BindView(R.id.boxview_box_weight)
    BoxInfoView mBoxviewBoxWeight;
    @BindView(R.id.boxview_box_battery)
    BoxInfoView mBoxviewBoxLifeTime;
    @BindView(R.id.boxview_box_closed)
    BoxInfoView mBoxviewBoxOpened;
    @BindView(R.id.boxview_box_locking)
    BoxInfoView mBoxviewBoxLocked;
    @BindView(R.id.btn_box_unlocking)
    Button mBtnBoxUnlocking;
    Unbinder unbinder;


    private String mPhoneNumber;
    private String mBoxId;

    private MaterialDialog mDialogGetInfo;
    private MaterialDialog mDialogLock;
    private int requestType;
    private boolean isRegisterReceiver = false;

    private final String commandReadInfo = Command.getCommand(Command.TYPE_READ_BOX, "00", "00", "");
    private final String commandUnLock = Command.getCommand(Command.TYPE_LOCK, "02", "02", "02");
    private final String commandLock = Command.getCommand(Command.TYPE_LOCK, "02", "02", "01");
    private final String commandLockedState = Command.getCommand(Command.TYPE_LOCK, "01", "00", "");
    private PresenterFactory.GetInfoPresenter mGetInfoPresenter;

    private MyHandler mMyHandler = new MyHandler(this);
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(INFO_UPDATE_ACTION)) {
                Bundle bundle = Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).getBundle("boxInfoBundle"));
                Message msg = mMyHandler.obtainMessage();
                msg.what = 1;
                msg.setData(bundle);
                mMyHandler.sendMessage(msg);
            }
        }
    };

    private String getTime;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity != null) {
            mPhoneNumber = ((MainActivity) mActivity).getPhoneNumber();
            mBoxId = ((MainActivity) mActivity).getBoxId();
        }

        IntentFilter intentFilter = new IntentFilter(INFO_UPDATE_ACTION);
        mActivity.registerReceiver(mReceiver, intentFilter);
        isRegisterReceiver = true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden);

        IntentFilter intentFilter = new IntentFilter(INFO_UPDATE_ACTION);
        if (!hidden) {
            mActivity.registerReceiver(mReceiver, intentFilter);
            isRegisterReceiver = true;
        } else {
            if (isRegisterReceiver) {
                mActivity.unregisterReceiver(mReceiver);
                isRegisterReceiver = false;
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        init();

        return view;
    }

    /**
     * 初始化Presenter，并开启获取箱子信息
     */
    private void init() {

        mGetInfoPresenter = PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId);

        requestType = FinalString.READ_BOX;

        //开始获取Info之前记录当前时间
        getTime = TimesCalculator.getStringDate();
        mDialogGetInfo = new MaterialDialog.Builder(mActivity)
                .content(R.string.getBoxInfo)
                .progress(true,0)
                .progressIndeterminateStyle(true)
                .build();

        mDialogGetInfo.show();

        mGetInfoPresenter.doRequest(mActivity, getTime, requestType, commandReadInfo, this);


    }

    @OnClick({R.id.batteryView_box, R.id.btn_box_unlocking})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.batteryView_box:
                if (((MainActivity) mActivity).isNetWork()) {
                    getTime = TimesCalculator.getStringDate();
                    mDialogGetInfo.show();
                    requestType = FinalString.READ_BOX;
                    mGetInfoPresenter.doRequest(mActivity, getTime, requestType, commandReadInfo, this);
                }
                break;
            case R.id.btn_box_unlocking:
                if (((MainActivity) mActivity).isNetWork()) {
                    getTime = TimesCalculator.getStringDate();
                    if (mBtnBoxUnlocking.getText().equals(getString(R.string.box_info_unlock))) {
                        mDialogLock = new MaterialDialog.Builder(mActivity)
                                .content(R.string.unlocking)
                                .progress(true,0)
                                .progressIndeterminateStyle(true)
                                .build();
                        mDialogLock.show();
                        requestType = FinalString.SET_UNLOCK;
                        mGetInfoPresenter.doRequest(mActivity, getTime, requestType, commandUnLock, this);
                    } else {
                        mDialogLock = new MaterialDialog.Builder(mActivity)
                                .content(R.string.locking)
                                .progress(true,0)
                                .progressIndeterminateStyle(true)
                                .build();
                        requestType = FinalString.SET_LOCK;
                        mDialogLock.show();
                        mGetInfoPresenter.doRequest(mActivity, getTime, requestType, commandLock, this);
                    }
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReadSucceed(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            StringBuilder command = new StringBuilder(jsonObject.getString(FinalString.COMMAND));
            //当获取时长大于十秒后，停止获取，提醒超时

            String type = command.substring(4, 6);
            //判断 得到数据类型 与 当前申请数据类型  是否一致
            switch (requestType) {
                case FinalString.READ_BOX:
                    //获得的数据是箱子信息
                    //5555  01  00  14  15.25  098  01  02 12 32F4
                    final String weight = command.substring(10, 15);
                    final String percent = command.substring(15, 18);
                    final String closed = command.substring(18, 20);
                    final String locked = command.substring(20, 22);
                    final String lifeTime = String.valueOf(Integer.valueOf(command.substring(22, 24)));
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialogGetInfo != null && mDialogGetInfo.isShowing()) {
                                mDialogGetInfo.dismiss();
                            }
                            mBoxviewBoxWeight.setValue(weight);
                            mBoxviewBoxLifeTime.setValue(lifeTime);
                            mBatteryView.setPercent(percent);
                            mBtnBoxUnlocking.setEnabled(true);

                            if ("01".equals(closed)) {
                                mBoxviewBoxOpened.setValue(getString(R.string.opened));
                            } else if ("02".equals(closed)) {
                                mBoxviewBoxOpened.setValue(getString(R.string.closed));
                            }

                            if ("01".equals(locked)) {
                                mBoxviewBoxLocked.setValue(getString(R.string.box_info_lock));
                                mBtnBoxUnlocking.setText(getString(R.string.box_info_unlock));
                            } else if ("02".equals(locked)) {
                                mBoxviewBoxLocked.setValue(getString(R.string.box_info_unlock));
                                mBtnBoxUnlocking.setText(getString(R.string.box_info_lock));
                            }
                        }
                    });

                    break;

                case FinalString.SET_LOCK:
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialogLock != null && mDialogLock.isShowing()) {
                                mDialogLock.dismiss();
                            }
                            mBoxviewBoxLocked.setValue(getString(R.string.box_info_lock));
                            mBtnBoxUnlocking.setText(getString(R.string.box_info_unlock));
                        }
                    });


                    break;
                case FinalString.SET_UNLOCK:
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialogLock != null && mDialogLock.isShowing()) {
                                mDialogLock.dismiss();
                            }
                            mBoxviewBoxLocked.setValue(getString(R.string.box_info_unlock));
                            mBtnBoxUnlocking.setText(getString(R.string.box_info_lock));
                        }
                    });

                    break;
                default:
                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReadFailed(String result) {
        ((MainActivity) mActivity).showToast(mActivity, getString(R.string.checkboxstate));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (requestType == FinalString.READ_BOX) {
                    if (mDialogGetInfo != null && mDialogGetInfo.isShowing()) {
                        mDialogGetInfo.dismiss();
                    }
                } else {
                    if (mDialogLock != null && mDialogLock.isShowing()) {
                        mDialogLock.dismiss();
                    }
                }

            }
        });
    }

    static class MyHandler extends Handler {

        private WeakReference<BoxFragment> mBoxFragmentWeakReference;
        private BoxFragment mFragment;


        public MyHandler(BoxFragment boxFragment) {
            mBoxFragmentWeakReference = new WeakReference<>(boxFragment);
        }

        @Override

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    BoxInfo boxInfo = bundle.getParcelable("boxInfo");
                    mFragment = mBoxFragmentWeakReference.get();
                    mFragment.mBoxviewBoxWeight.setValue(boxInfo != null ? boxInfo.getWeight() : "");
                    mFragment.mBoxviewBoxLifeTime.setValue(boxInfo != null ? boxInfo.getLifeTime() : "");

                    mFragment.mBatteryView.setPercent(boxInfo != null ? boxInfo.getPercent() : "");
                    mFragment.mBtnBoxUnlocking.setEnabled(true);

                    if (boxInfo != null) {
                        if ("01".equals(boxInfo.getIsOpened())) {
                            mFragment.mBoxviewBoxOpened.setValue("开启");
                        } else if ("02".equals(boxInfo.getIsOpened())) {
                            mFragment.mBoxviewBoxOpened.setValue("关闭");
                        }
                    }

                    if (boxInfo != null) {
                        if ("01".equals(boxInfo.getIsLocked())) {
                            mFragment.mBoxviewBoxLocked.setValue(mFragment.getString(R.string.box_info_lock));
                            mFragment.mBtnBoxUnlocking.setText(mFragment.getString(R.string.box_info_unlock));
                        } else if ("02".equals(boxInfo.getIsLocked())) {
                            mFragment.mBoxviewBoxLocked.setValue(mFragment.getString(R.string.box_info_unlock));
                            mFragment.mBtnBoxUnlocking.setText(mFragment.getString(R.string.box_info_lock));
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

}
