package com.yuanshi.hiorange.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanshi.hiorange.MyApplication;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.CheckPermissionsActivity;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.MySharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Zyc
 * @date 2018/2/5
 */

public class LoginActivity extends CheckPermissionsActivity implements ILoginView {

    @BindView(R.id.et_login_phone_number)
    EditText mEtLoginPhoneNumber;
    @BindView(R.id.iv_login_phone_clean)
    ImageView mIvLoginPhoneClean;
    @BindView(R.id.et_login_phone_password)
    EditText mEtLoginPassword;
    @BindView(R.id.tv_login_forget_password)
    TextView mTvLoginForgetPassword;
    @BindView(R.id.btn_login_login)
    Button mBtnLoginLogin;
    @BindView(R.id.btn_login_register)
    Button mBtnLoginRegister;
    @BindView(R.id.iv_login_passsword_clean)
    ImageView mIvLoginPassswordClean;
    public static Context mContext;
    private long mLastTime = 0;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        MyApplication.getInstance().addActivity(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statue_bar));
        mContext = this;
        initView();
    }

    private void initView() {
        final Animation animCleanMiss = AnimationUtils.loadAnimation(this, R.anim.clean_icon_scale_down);
        final Animation animCleanShow = AnimationUtils.loadAnimation(this, R.anim.clean_icon_scale_up);

        dialog = new AlertDialog.Builder(mContext).setMessage("正在登陆").create();

        String phoneString = MySharedPreference.getString(mContext, FinalString.PHONE, "");
        String passWordString = MySharedPreference.getString(mContext, FinalString.PASSWORD, "");
        if ("".equals(phoneString)) {
            mEtLoginPhoneNumber.setText(phoneString);
        }
        if ("".equals(passWordString)) {
            mEtLoginPassword.setText(passWordString);
        }

        if (mEtLoginPhoneNumber.getText().length() != 0) {
            mIvLoginPhoneClean.setVisibility(View.VISIBLE);
        }

        mEtLoginPassword.addTextChangedListener(new TextWatcher() {
            boolean isShow = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mIvLoginPassswordClean.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged", s.length() + "  " + start + "  " + before + " " + count);
                if (count == 0 && s.length() == 0) {
                    mIvLoginPassswordClean.startAnimation(animCleanMiss);
                    isShow = false;
                    mIvLoginPassswordClean.setVisibility(View.GONE);
                } else if (count == 1 && !isShow) {
                    isShow = true;
                    mIvLoginPassswordClean.startAnimation(animCleanShow);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        mEtLoginPhoneNumber.addTextChangedListener(new TextWatcher() {
            boolean isShow = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mIvLoginPhoneClean.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged", s.length() + "  " + start + "  " + before + " " + count);
                if (count == 0 && s.length() == 0) {
                    mIvLoginPhoneClean.startAnimation(animCleanMiss);
                    isShow = false;
                    mIvLoginPhoneClean.setVisibility(View.GONE);
                } else if (count == 1 && !isShow) {
                    isShow = true;
                    mIvLoginPhoneClean.startAnimation(animCleanShow);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

    }

    @OnClick({R.id.btn_login_login, R.id.btn_login_register, R.id.tv_login_forget_password, R.id.iv_login_phone_clean})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login:

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(mEtLoginPassword.getWindowToken(), 0);
                }


                String phoneNumber = mEtLoginPhoneNumber.getText().toString();
                String passWord = mEtLoginPassword.getText().toString();
                if ("".equals(phoneNumber) || "".equals(passWord)) {
                    showToast(this, "输入不完整，请重新确认");
                } else {
                    if (isNetWork()) {
                        dialog.show();

                        //创建并执行登录执行者
                        PresenterFactory
                                .createLoginPresenter(phoneNumber, passWord)
                                .doRequest( this);
                    }
                }


                break;
            case R.id.btn_login_register:
                if (isNetWork()) {
                    startActivitiesForResult(RegisterActivity.class, null, REQUEST_CODE_FROM_LOGIN);
                }

                break;
            case R.id.iv_login_phone_clean:
                mEtLoginPhoneNumber.setText("");
                break;
            case R.id.iv_login_passsword_clean:
                mEtLoginPassword.setText("");
                break;
            case R.id.tv_login_forget_password:
                startActivities(MainActivity.class, null);
                if (isNetWork()) {

                }
                break;
            default:
                break;
        }
    }

    /**
     * 登录成功后操作
     *
     * @param result box_id
     */
    @Override
    public void onLoginSucceed(String result) {
        MySharedPreference.saveString(this, FinalString.PHONE, mEtLoginPhoneNumber.getText().toString());
        MySharedPreference.saveString(this, FinalString.PASSWORD, mEtLoginPassword.getText().toString());
        MySharedPreference.saveString(this, FinalString.BOX_ID, result);
        startActivities(MainActivity.class, null);
        finish();
    }

    @Override
    public void onLoginFailed(String result) {
        showToast(this, result);
    }

    @Override
    public void onNewUserLogin(String result) {
        startActivities(AddBoxActivity.class, null);
        MySharedPreference.saveString(this, FinalString.PHONE, mEtLoginPhoneNumber.getText().toString());
        MySharedPreference.saveString(this, FinalString.PASSWORD, mEtLoginPassword.getText().toString());
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FROM_LOGIN && data != null) {
            mEtLoginPhoneNumber.setText(data.getStringExtra(FinalString.PHONE));
            mEtLoginPassword.setText(data.getStringExtra(FinalString.PASSWORD));
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
