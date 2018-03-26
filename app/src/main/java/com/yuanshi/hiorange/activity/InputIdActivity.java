package com.yuanshi.hiorange.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.yuanshi.hiorange.BaseActivity;
import com.yuanshi.hiorange.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yuanshi.hiorange.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;

/**
 * Created by Administrator on 2018/2/25.
 */

public class InputIdActivity extends BaseActivity {

    @BindView(R.id.et_input_id)
    EditText mEtInputId;
    @BindView(R.id.btn_input_binding)
    Button mBtnInputBinding;
    private int RESULT_OK = 0xA1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statue_bar));
        setContentView(R.layout.input_activity);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_input_binding})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_input_binding:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(mEtInputId.getWindowToken(), 0);
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, mEtInputId.getText().toString());
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }


}
