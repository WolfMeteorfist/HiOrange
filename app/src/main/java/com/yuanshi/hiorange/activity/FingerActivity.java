package com.yuanshi.hiorange.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yuanshi.hiorange.BaseActivity;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.Command;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.TimesCalculator;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Zyc
 * @date 2018/3/8
 */

public class FingerActivity extends BaseActivity implements IFingerView {

    private static final String TAG = "FingerActivity";

    //指纹命名,暂不提供修改
    private ArrayList<String> fingerString;

    //初始状态,均为未使用
    private String fingerUsable = "00000";

    //Command类型
    String typeDelete = "03";
    String typeRegister = "02";
    String typeCancel = "04";
    String typeGetFinger = "01";

    private String commandDelete;
    private String commandRegister;
    private String commandCancel;
    private String commandGet;

    //申请数据的类型
    private int requestType;

    private String mPhoneNumber;
    private String mBoxId;

    private MaterialDialog mRegisterDialog;
    private MaterialDialog mDeleteDialog;
    private MaterialDialog mReadDialog;
    private MyAdapter mAdapter;
    private String mGetTime;
    private ThreadPoolExecutor mThreadPoolExecutor;

    class ViewHolder1 {
        @BindView(R.id.back)
        ImageView mBack;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.finger_list)
        RecyclerView mRecyclerView;
    }

    class ViewHolder2 {
        @BindView(R.id.finger_image_dialog)
        ImageView mFingerImageDialog;
        @BindView(R.id.finger_btn_cancel)
        Button mFingerBtnCancel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_activity);
        mReadDialog = new MaterialDialog.Builder(this)
                .content(R.string.GetFinger)
                .cancelable(false)
                .build();
        initData();
        initView();
    }

    private void initView() {

        ViewHolder1 viewActivity = new ViewHolder1();
        ViewHolder2 viewDialog = new ViewHolder2();

        //指纹录取
        View view = LayoutInflater.from(this).inflate(R.layout.finger_dialog, null);
        view.findViewById(R.id.finger_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRegisterDialog != null && mRegisterDialog.isShowing()) {
                    mRegisterDialog.dismiss();
                }
            }
        });

        ButterKnife.bind(viewActivity, this);
        ButterKnife.bind(viewDialog, view);

        viewActivity.mTvTitle.setText(R.string.Fingerprintmanagement);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        viewActivity.mRecyclerView.setLayoutManager(manager);
        mAdapter = new MyAdapter();
        viewActivity.mRecyclerView.setAdapter(mAdapter);

        //初始化两个dialog，其中删除的dialog还需要获取position
        //所以在点击的时候定义positiveButton并create
        mRegisterDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .cancelable(false)
                .build();

        mDeleteDialog = new MaterialDialog.Builder(this)
                .content(R.string.isDelete)
                .negativeText(R.string.no)
                .cancelable(false)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mDeleteDialog.dismiss();
                    }
                })
                .build();


        //首次进入fingerActivity时跟服务器要数据
        mAdapter.updateData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPhoneNumber = getIntent().getExtras().getString(FinalString.PHONE);
            mBoxId = getIntent().getExtras().getString(FinalString.BOX_ID);
        }

        fingerString = new ArrayList<>(5);
        fingerString.add(0, "指纹1");
        fingerString.add(1, "指纹2");
        fingerString.add(2, "指纹3");
        fingerString.add(3, "指纹4");
        fingerString.add(4, "指纹5");
    }

    @OnClick({R.id.back, R.id.finger_btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.finger_btn_cancel:
                requestType = FinalString.FINGER_CANCEL;
                commandCancel = getCommand(typeCancel, "01");
                mGetTime = TimesCalculator.getStringDate();
                PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId)
                        .doRequest(FingerActivity.this, mGetTime, requestType, commandCancel, this);
                break;
            default:
                break;
        }
    }


    private String getCommand(String type, String finger) {
        if (type.equals(typeGetFinger)) {
            return Command.getCommand(Command.TYPE_FINGER, type, "00", "");
        }
        return Command.getCommand(Command.TYPE_FINGER, type, "02", finger);
    }


    @Override
    public void onReadSucceed(String result) {
        //取得command,判断当前指纹录入情况
        Log.e(TAG, "onReadSucceed: " + result);
        dissmissDialog();
        if (requestType == FinalString.FINGER_DELETE) {
            showToast(this, getString(R.string.deleteSucceed));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateData();
                }
            });
        } else if (requestType == FinalString.READ_FINGER) {
            fingerUsable = result.substring(10, 15);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();

                }
            });
        }

    }


    @Override
    public void onReadFailed(String result) {

        Log.e(TAG, "onReadFailed: " + result);
        dissmissDialog();
        if (requestType != FinalString.FINGER_REGISTER) {
            showToast(this, getString(R.string.operateFailed) + result);
        }
    }

    @Override
    public void onRegisterSucceed(int regCout) {
        //录入成功反馈，可以用于添加录入成功后的动画效果或UI更新
        Log.e(TAG, "onRegisterSucceed: " + regCout);
        if (regCout < 3) {
            showToast(this, "录入成功，请再次录入");
            return;
        }
        dissmissDialog();
        mAdapter.updateData();

    }

    private void dissmissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mReadDialog != null && mReadDialog.isShowing()) {
                    mReadDialog.dismiss();
                }
                if (mRegisterDialog != null && mRegisterDialog.isShowing()) {
                    mRegisterDialog.dismiss();
                }
                if (mDeleteDialog != null && mDeleteDialog.isShowing()) {
                    mDeleteDialog.dismiss();
                }
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        void updateData() {

            mReadDialog.show();
            commandGet = getCommand(typeGetFinger, null);
            mGetTime = TimesCalculator.getStringDate();
            requestType = FinalString.READ_FINGER;
            PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId)
                    .doRequest(FingerActivity.this, mGetTime, requestType, commandGet, FingerActivity.this);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 实例化展示的view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.finger_list_item, parent, false);
            // 实例化viewHolder
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            //监听点击事件,判断当前fingerLinearlayout有没有录入指纹
            // if(isFingerUsable){ } 若录入,弹框询问是否delete
            // else{ } 若未录入,弹出开始录制指纹,可选择停止录制
            holder.mLlBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    StringBuilder ps = new StringBuilder(String.valueOf(position + 1));
                    final String psString = ps.insert(0, "0").toString();

                    if (isFingerUse(position)) {
                        //已使用,弹出询问是否删除框
                        mDeleteDialog = new MaterialDialog.Builder(FingerActivity.this)
                                .content(R.string.isDelete)
                                .negativeText(R.string.yes)
                                .cancelable(false)
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        requestType = FinalString.FINGER_DELETE;
                                        mGetTime = TimesCalculator.getStringDate();
                                        commandDelete = getCommand(typeDelete, psString);
                                        PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId)
                                                .doRequest(FingerActivity.this, mGetTime, requestType, commandDelete, FingerActivity.this);
                                    }
                                })
                                .show();

                    } else {
                        //未使用，开始录制
                        mRegisterDialog.show();
                        requestType = FinalString.FINGER_REGISTER;
                        mGetTime = TimesCalculator.getStringDate();
                        commandRegister = getCommand(typeRegister, ps.toString());
                        PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId)
                                .doRequest(FingerActivity.this, mGetTime, requestType, commandRegister, FingerActivity.this);
                    }
                }
            });

            if (isFingerUse(position)) {
                holder.mIvIcon.setVisibility(View.GONE);
                holder.mTvFinger.setVisibility(View.VISIBLE);
//                holder.mTvFinger.setText(fingerString.get(position));
            } else {
                holder.mIvIcon.setVisibility(View.VISIBLE);
                holder.mTvFinger.setVisibility(View.GONE);
            }
        }

        //判断finger是否使用
        boolean isFingerUse(int fingerNo) {
            //"00010", fingerNo = 1. 则查询"0 0010"中的第一位是否等于1 ，不等于则未使用
            return fingerUsable.charAt(fingerNo) == '1';
        }

        @Override
        public int getItemCount() {
            return fingerString.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView mTvFinger;
            ImageView mIvIcon;
            LinearLayout mLlBg;

            ViewHolder(View view) {
                super(view);
                mTvFinger = view.findViewById(R.id.finger_text);
                mIvIcon = view.findViewById(R.id.finger_icon);
                mLlBg = view.findViewById(R.id.finger_ll);
            }
        }


    }

}
