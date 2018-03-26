package com.yuanshi.hiorange.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanshi.hiorange.BaseActivity;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.MySharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/7.
 */

public class UnbindActivity extends BaseActivity implements IUnbindView {


    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_unbind_box_id)
    TextView mTvUnbindBoxId;
    @BindView(R.id.btn_unbind_unbind)
    Button mBtnUnbindUnbind;


    private AlertDialog mDialog;
    private String mPhoneNumber;
    private String mPassWord;
    private String mBoxId;
    public static final int UNBIND_RESULT_CODE_OK = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statue_bar));
        setContentView(R.layout.unbind_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mPhoneNumber = getIntent().getExtras().getString(FinalString.PHONE);
        mPassWord = getIntent().getExtras().getString(FinalString.PASSWORD);
        mBoxId = getIntent().getExtras().getString(FinalString.BOX_ID);
        mDialog = new AlertDialog.Builder(this)
                .setMessage("解绑中")
                .create();
        mTvUnbindBoxId.setText(mBoxId);
        mTvTitle.setText(R.string.unbind);
    }

    @OnClick({R.id.back, R.id.btn_unbind_unbind})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_unbind_unbind:
                if (isNetWork()) {
                    mDialog.show();
                    PresenterFactory
                            .createUnbindBoxPresenter(mPhoneNumber, mBoxId)
                            .doRequest(this);
                }
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void unbindSucceed(String result) {
        showToast(this, "解绑成功");
        setResult(UNBIND_RESULT_CODE_OK);
        MySharedPreference.cleanSharedPreferences(this);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    finish();
                }
            }
        });
    }

    @Override
    public void unbindFailed(String result) {
        showToast(this, "解绑失败.");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
    }

}
