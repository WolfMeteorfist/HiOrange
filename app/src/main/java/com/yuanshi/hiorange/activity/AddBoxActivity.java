package com.yuanshi.hiorange.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanshi.hiorange.BaseActivity;
import com.yuanshi.hiorange.MyApplication;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.MySharedPreference;
import com.yuanshi.hiorange.view.RippleView;
import com.yuanshi.hiorange.zxing.activity.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.yuanshi.hiorange.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;

/**
 * Created by Administrator on 2018/2/5.
 */

public class AddBoxActivity extends BaseActivity implements IAddBoxView {

    @BindView(R.id.tv_title)
    TextView mTbTitle;
    @BindView(R.id.view2)
    View mView2;
    @BindView(R.id.btn_addbox_scan)
    Button mBtnAddboxScan;
    @BindView(R.id.btn_addbox_input)
    Button mBtnAddboxInput;
    @BindView(R.id.ripple_view)
    RippleView mRippleView;
    @BindView(R.id.back)
    ImageView mBack;

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;
    private long mLastTime = 0;


    Unbinder unbinder;
    private String mBoxId;
    private String mPhoneNumber;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statue_bar));
        setContentView(R.layout.add_box_activity);
        unbinder = ButterKnife.bind(this);
        mBack.setVisibility(View.GONE);
        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setMessage("注册中...");
        mTbTitle.setText("添加设备");
        mPhoneNumber = MySharedPreference.getString(getApplicationContext(), FinalString.PHONE, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.btn_addbox_input, R.id.btn_addbox_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_addbox_scan:
                startActivitiesForResult(CaptureActivity.class, null, REQUEST_CODE);
                break;
            case R.id.btn_addbox_input:
                startActivitiesForResult(InputIdActivity.class, null, REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            //获取 扫描信息或者从InputIdActivity返回的数据
            mBoxId = bundle.getString(INTENT_EXTRA_KEY_QR_SCAN);

            if (!"".equals(mPhoneNumber)) {

                mAlertDialog.show();
                PresenterFactory
                        .createAddBoxPresenter(mPhoneNumber, mBoxId)
                        .doRequest( this);
            } else {

                showToast(this, "登录失效,请重新登录");
                startActivities(LoginActivity.class, null);
                finish();
            }

            //将扫描出的信息显示出来
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastTime) < 2000) {
                MyApplication.getInstance().exit();
            } else {
                Toast.makeText(getApplicationContext(), "连续按两次退出程序", Toast.LENGTH_SHORT).show();
            }
            mLastTime = curTime;
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRippleView.stopAnim();
        unbinder.unbind();
    }

    @Override
    public void onAddSucceed(String result) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
            }
        });
        showToast(this, "添加成功");
        MySharedPreference.saveString(getApplicationContext(), FinalString.BOX_ID, mBoxId);
        startActivities(MainActivity.class, null);
        finish();
    }

    @Override
    public void onAddFailed(String result) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
            }
        });
        showToast(this, "添加失败:" + result);
    }
}
