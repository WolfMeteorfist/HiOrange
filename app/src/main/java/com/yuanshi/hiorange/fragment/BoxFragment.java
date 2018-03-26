package com.yuanshi.hiorange.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.Command;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.TimesCalculator;
import com.yuanshi.hiorange.view.BatteryView;
import com.yuanshi.hiorange.view.BoxInfoView;
import com.yuanshi.hiorange.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

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
    protected Activity mActivity;
    @BindView(R.id.batteryView_box)
    BatteryView mBatteryViewBox;
    @BindView(R.id.boxview_box_weight)
    BoxInfoView mBoxviewBoxWeight;
    @BindView(R.id.boxview_box_battery)
    BoxInfoView mBoxviewBoxBattery;
    @BindView(R.id.boxview_box_closed)
    BoxInfoView mBoxviewBoxClosed;
    @BindView(R.id.boxview_box_locking)
    BoxInfoView mBoxviewBoxLocked;
    @BindView(R.id.btn_box_unlocking)
    Button mBtnBoxUnlocking;
    Unbinder unbinder;


    private String mPhoneNumber;
    private String mBoxId;

    private AlertDialog mDialogGetInfo;
    private AlertDialog mDialogLock;
    private int requestType;

    //网上获取数据失效
    private final long DISABLE_TIME = 60 * 1000;
    //数据申请超时
    private final long TIME_OUT = 10 * 1000;

    private final String commandReadInfo = Command.getCommand(Command.TYPE_READ_BOX, "00", "00", "");
    private final String commandUnLock = Command.getCommand(Command.TYPE_LOCK, "02", "02", "02");
    private final String commandLock = Command.getCommand(Command.TYPE_LOCK, "02", "02", "01");
    private final String commandLockedState = Command.getCommand(Command.TYPE_LOCK, "01", "00", "");
    private PresenterFactory.GetInfoPresenter mGetInfoPresenter;
    private PresenterFactory.ReadOrSetBoxPresenter mLockedStatePresenter;
    private PresenterFactory.ReadOrSetBoxPresenter mReadInfoPresenter;
    private PresenterFactory.ReadOrSetBoxPresenter mLockPresenter;
    private PresenterFactory.ReadOrSetBoxPresenter mUnLockPresenter;

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

        mUnLockPresenter = PresenterFactory.createReadOrSetBoxPresenter(mPhoneNumber, mBoxId, commandUnLock);

        mLockPresenter = PresenterFactory.createReadOrSetBoxPresenter(mPhoneNumber, mBoxId, commandLock);

        mReadInfoPresenter = PresenterFactory.createReadOrSetBoxPresenter(mPhoneNumber, mBoxId, commandReadInfo);

        mLockedStatePresenter = PresenterFactory.createReadOrSetBoxPresenter(mPhoneNumber, mBoxId, commandLockedState);

        mGetInfoPresenter = PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId);

        requestType = FinalString.READ_BOX;

        //开始获取Info之前记录当前时间
        getTime = TimesCalculator.getStringDate();
        mDialogGetInfo = new AlertDialog
                .Builder(mActivity).setMessage("箱子信息获取中").setCancelable(false).create();

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
                        mDialogLock = new AlertDialog.Builder(mActivity).setMessage("远程解锁中").setCancelable(false).create();
                        mDialogLock.show();
                        requestType = FinalString.SET_UNLOCK;
                        mGetInfoPresenter.doRequest(mActivity, getTime, requestType, commandUnLock, this);
                    } else {

                        mDialogLock = new AlertDialog.Builder(mActivity).setMessage("远程上锁中").setCancelable(false).create();
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
        Log.e(TAG, "onDestroyView: ");
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReadSucceed(String result) {
        Log.e(TAG, "onReadSucceed: " + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            StringBuilder command = new StringBuilder(jsonObject.getString(FinalString.COMMAND));
            //当获取时长大于十秒后，停止获取，提醒超时

            String type = command.substring(4, 6);
            //判断 得到数据类型 与 当前申请数据类型  是否一致
            switch (requestType) {
                case FinalString.READ_BOX:
                    //获得的数据是箱子信息
                    //5555  01  00  13  15.25  0.98  01  02  32F4
                    final String weight = command.substring(10, 15);
                    final String percent = command.substring(15, 18);
                    final String closed = command.substring(18, 20);
                    final String locked = command.substring(20, 22);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialogGetInfo != null && mDialogGetInfo.isShowing()) {
                                mDialogGetInfo.dismiss();
                            }
                            mBoxviewBoxWeight.setValue(weight);
                            mBoxviewBoxBattery.setValue("0");
                            mBatteryViewBox.setPercent(percent);
                            if ("01".equals(closed)) {
                                mBoxviewBoxClosed.setValue("开启");
                            } else if ("02".equals(closed)) {
                                mBoxviewBoxClosed.setValue("关闭");
                            }

                            mBtnBoxUnlocking.setEnabled(true);
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
        ((MainActivity) mActivity).showToast(mActivity, result);
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


}
