package com.yuanshi.hiorange.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.suke.widget.SwitchButton;
import com.yuanshi.hiorange.BaseActivity;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.Command;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.MySharedPreference;
import com.yuanshi.hiorange.util.TimesCalculator;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Zyc
 * @date 2018/3/6
 */

public class VoiceActivity extends BaseActivity implements IVoiceView {

    private static final String TAG = "VoiceActivity";

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.tv_title)
    TextView mTbTitle;
    @BindView(R.id.tb)
    Toolbar mTb;
    @BindView(R.id.btn_voice_obstacle)
    SwitchButton mBtnVoiceObstacle;
    @BindView(R.id.btn_voice_lost)
    SwitchButton mBtnVoiceLost;
    @BindView(R.id.btn_voice_power_on)
    SwitchButton mBtnVoicePowerOn;
    @BindView(R.id.btn_voice_controll)
    SwitchButton mBtnVoiceControll;
    @BindView(R.id.btn_voice_follow)
    SwitchButton mBtnVoiceFollow;
    @BindView(R.id.btn_voice_power_off)
    SwitchButton mBtnVoicePowerOff;
    @BindView(R.id.tv_save)
    TextView mTvSave;

    private String mPhoneNumber;
    private String mPassWord;
    private String mBoxId;
    private final String commandGetVoice = Command.getCommand(Command.TYPE_VOICE, Command.VOICE_READ, "00", "");
    private PresenterFactory.GetInfoPresenter mGetVoiceInfoPresenter;
    private PresenterFactory.ReadOrSetBoxPresenter mSetVoicePresenter;
    private AlertDialog mDialog;
    private String commandSetVoice;

    private String getTime;
    private int requestType;

    private final String OBSTACLE = "1";
    private final String LOST = "2";
    private final String POWER_ON = "3";
    private final String CONTROLL = "4";
    private final String FOLLOW = "5";
    private final String POWER_OFF = "6";

    private String obCheck = "02";
    private String lsCheck = "02";
    private String poCheck = "02";
    private String ctCheck = "02";
    private String flCheck = "02";
    private String pfCheck = "02";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statue_bar));
        setContentView(R.layout.voice_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPhoneNumber = getIntent().getExtras().getString(FinalString.PHONE);
            mPassWord = getIntent().getExtras().getString(FinalString.PASSWORD);
            mBoxId = getIntent().getExtras().getString(FinalString.BOX_ID);
        }

        mTbTitle.setText("声音提醒设置");
        mTvSave.setVisibility(View.VISIBLE);

        getTime = TimesCalculator.getStringDate();
        mDialog = new AlertDialog.Builder(this).setMessage("获取箱子设置中").setCancelable(false).create();
        mDialog.show();
        mGetVoiceInfoPresenter = PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId);
        mBtnVoiceObstacle.setTag(OBSTACLE);
        mBtnVoiceLost.setTag(LOST);
        mBtnVoicePowerOn.setTag(POWER_ON);
        mBtnVoiceControll.setTag(CONTROLL);
        mBtnVoiceFollow.setTag(FOLLOW);
        mBtnVoicePowerOff.setTag(POWER_OFF);

        mBtnVoiceObstacle.setOnCheckedChangeListener(new MyCheckedChangeListener());
        mBtnVoiceLost.setOnCheckedChangeListener(new MyCheckedChangeListener());
        mBtnVoicePowerOn.setOnCheckedChangeListener(new MyCheckedChangeListener());
        mBtnVoiceControll.setOnCheckedChangeListener(new MyCheckedChangeListener());
        mBtnVoiceFollow.setOnCheckedChangeListener(new MyCheckedChangeListener());
        mBtnVoicePowerOff.setOnCheckedChangeListener(new MyCheckedChangeListener());
    }

    @Override
    protected void onResume() {
        requestType = FinalString.READ_VOICE;
        mGetVoiceInfoPresenter.doRequest(VoiceActivity.this, getTime, requestType, commandGetVoice, this);
        super.onResume();
    }

    class MyCheckedChangeListener implements SwitchButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            switch ((String) view.getTag()) {
                case OBSTACLE:
                    obCheck = isChecked ? "01" : "02";
                    break;
                case LOST:
                    lsCheck = isChecked ? "01" : "02";
                    break;
                case POWER_ON:
                    poCheck = isChecked ? "01" : "02";
                    break;
                case CONTROLL:
                    ctCheck = isChecked ? "01" : "02";
                    break;
                case FOLLOW:
                    flCheck = isChecked ? "01" : "02";
                    break;
                case POWER_OFF:
                    pfCheck = isChecked ? "01" : "02";
                    break;
                default:
                    break;
            }
        }
    }


    @OnClick({R.id.tv_save, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                StringBuilder builder = new StringBuilder();
                builder.append(obCheck).append(lsCheck).append(poCheck).append(ctCheck).append(flCheck).append(pfCheck);
                commandSetVoice = Command.getCommand(Command.TYPE_VOICE, Command.VOICE_READ, "12", builder.toString());
                requestType = FinalString.SET_VOICE;
                getTime = TimesCalculator.getStringDate();
                mDialog.setMessage("保存中");
                mDialog.show();
                mGetVoiceInfoPresenter.doRequest(this, getTime, requestType, commandSetVoice, this);
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    private void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onReadSucceed(String result) {

        if (requestType == FinalString.SET_VOICE) {
            showToast(this, "保存成功");
        } else if (requestType == FinalString.READ_VOICE) {
            showToast(this, "获取成功");
        }

        dismissDialog();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            StringBuilder command = new StringBuilder(jsonObject.getString(FinalString.COMMAND));
            commandSetVoice = command.substring(10, 22);
            MySharedPreference.saveString(this, FinalString.COMMAND, commandSetVoice);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBtnVoiceObstacle.setChecked(commandSetVoice.substring(0, 2).equals("01"));
                    mBtnVoiceLost.setChecked(commandSetVoice.substring(2, 4).equals("01"));
                    mBtnVoicePowerOn.setChecked(commandSetVoice.substring(4, 6).equals("01"));
                    mBtnVoiceControll.setChecked(commandSetVoice.substring(6, 8).equals("01"));
                    mBtnVoiceFollow.setChecked(commandSetVoice.substring(8, 10).equals("01"));
                    mBtnVoicePowerOff.setChecked(commandSetVoice.substring(10, 12).equals("01"));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReadFailed(String result) {

        dismissDialog();

        if (requestType == FinalString.SET_VOICE) {
            showToast(this, "保存失败");
        } else if (requestType == FinalString.READ_VOICE) {
            showToast(this, "获取失败,取得最近数据");
        }

        commandSetVoice = MySharedPreference.getString(this, FinalString.COMMAND, "020202020202");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mBtnVoiceObstacle.setChecked(commandSetVoice.substring(0, 2).equals("01"));
                mBtnVoiceLost.setChecked(commandSetVoice.substring(2, 4).equals("01"));
                mBtnVoicePowerOn.setChecked(commandSetVoice.substring(4, 6).equals("01"));
                mBtnVoiceControll.setChecked(commandSetVoice.substring(6, 8).equals("01"));
                mBtnVoiceFollow.setChecked(commandSetVoice.substring(8, 10).equals("01"));
                mBtnVoicePowerOff.setChecked(commandSetVoice.substring(10, 12).equals("01"));
            }
        });
    }
}
