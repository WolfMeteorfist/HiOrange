package com.yuanshi.hiorange.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.yuanshi.hiorange.BaseActivity;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.FinalString;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author zyc
 * @date 2018/2/5
 */

public class RegisterActivity extends BaseActivity implements IRegisterView {

    @BindView(R.id.tv_title)
    TextView mTbTitle;
    @BindView(R.id.et_register_phonenumber)
    EditText mEtRegisterPhoneNumber;
    @BindView(R.id.et_register_identify_code)
    EditText mEtRegisterIdentifyCode;
    @BindView(R.id.btn_register_identify_send)
    Button mBtnRegisterIdentifySend;
    @BindView(R.id.btn_register_register)
    Button mBtnRegister;
    @BindView(R.id.et_register_password)
    EditText mEtRegisterPassword;
    @BindView(R.id.et_register_password_confirm)
    EditText mEtRegisterPasswordConfirm;
    private String mPassWord;
    private String mPassWordConfirm;
    private MaterialDialog mAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statue_bar));
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        mAlertDialog = new MaterialDialog.Builder(this).content(R.string.registing).build();

        mTbTitle.setText(R.string.user_register);

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mPassWord = mEtRegisterPassword.getText().toString();
                mPassWordConfirm = mEtRegisterPasswordConfirm.getText().toString();
                if (mPassWord.length() > 0 && mPassWordConfirm.length() > 0) {
                    if (!mPassWord.equals(mPassWordConfirm)) {

                        mEtRegisterPasswordConfirm.setBackground(getResources().getDrawable(R.drawable.register_et_border_error, null));
                        showToast(RegisterActivity.this, getString(R.string.password_different));
                    } else {
                        mEtRegisterPasswordConfirm.setBackground(getResources().getDrawable(R.drawable.register_et_border));
                    }
                } else {
                    mEtRegisterPasswordConfirm.setBackground(getResources().getDrawable(R.drawable.register_et_border));
                }
            }
        };

        mEtRegisterPasswordConfirm.setOnFocusChangeListener(onFocusChangeListener);
        mEtRegisterPassword.setOnFocusChangeListener(onFocusChangeListener);
    }

    @OnClick({R.id.btn_register_register, R.id.back})
    public void onClick(View view) {

        mAlertDialog.show();
        String phoneNumber = mEtRegisterPhoneNumber.getText().toString();
        String veriftyCode = mEtRegisterIdentifyCode.getText().toString();
        mPassWord = mEtRegisterPassword.getText().toString();
        mPassWordConfirm = mEtRegisterPasswordConfirm.getText().toString();
        switch (view.getId()) {
            case R.id.btn_register_register:
                if (isNetWork()) {
                    if ("".equals(mPassWord) || "".equals(phoneNumber) || "".equals(veriftyCode)) {
                        showToast(this, getString(R.string.input_incomplete));
                    } else {

                        PresenterFactory
                                .createRegisterPresenter(phoneNumber, mPassWord, veriftyCode)
                                .doRequest( this);
                    }
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
    public void onRegisterSucceed(String result) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
            }
        });
        Intent intent = new Intent();
        intent.putExtra(FinalString.PHONE, mEtRegisterPhoneNumber.getText().toString());
        intent.putExtra(FinalString.PASSWORD, mEtRegisterPassword.getText().toString());
        setResult(RESULT_CODE_FROM_REGISTER, intent);
        finish();
    }

    @Override
    public void onRegisterFailed(String result) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
            }
        });
        showToast(this, result);
    }
}
