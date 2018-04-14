package com.yuanshi.hiorange.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import butterknife.Optional;

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
    @BindView(R.id.tv_save)
    TextView mTvSave;
    @BindView(R.id.view_stub_voice)
    ViewStub mViewStubVoice;

    private MyStubView stubView;


    private String mPhoneNumber;
    private String mPassWord;
    private String mBoxId;
    private final String commandGetVoice = Command.getCommand(Command.TYPE_VOICE, Command.VOICE_READ, "00", "");
    private PresenterFactory.GetInfoPresenter mGetVoiceInfoPresenter;
    private PresenterFactory.ReadOrSetBoxPresenter mSetVoicePresenter;
    private MaterialDialog mDialog;
    private String commandSetVoice;
    private boolean isShow = false;

    private String getTime;
    private int requestType;

    private final String OBSTACLE = "1";
    private final String FOLLOWLOST = "2";
    private final String POWER_ON = "3";
    private final String CONTROLL = "4";
    private final String FOLLOW = "5";
    private final String POWER_OFF = "6";
    private final String BT = "7";
    private final String HELP = "8";
    private final String LEAN = "9";
    private final String BOXLOST = "10";
    private final String LOCK = "11";

    private String obCheck = "02";
    private String lsCheck = "02";
    private String poCheck = "02";
    private String ctCheck = "02";
    private String flCheck = "02";
    private String pfCheck = "02";
    private String btCheck = "02";
    private String helpCheck = "02";
    private String leanCheck = "02";
    private String boxLostCheck = "02";
    private String lockCheck = "02";

    public class MyStubView {

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
        @BindView(R.id.btn_bt_power_off)
        SwitchButton mBtnBtPowerOff;
        @BindView(R.id.btn_help_power_off)
        SwitchButton mBtnHelpPowerOff;
        @BindView(R.id.btn_lean_power_off)
        SwitchButton mBtnLeanPowerOff;
        @BindView(R.id.btn_box_lost_power_off)
        SwitchButton mBtnBoxLostPowerOff;
        @BindView(R.id.btn_lock_power_off)
        SwitchButton mBtnLockPowerOff;

        public MyStubView(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statue_bar));
        setContentView(R.layout.voice_idle_activity);
        ButterKnife.bind(this);
        init();
    }

    @Optional
    private void init() {
        mTbTitle.setText(R.string.Soundreminder);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPhoneNumber = getIntent().getExtras().getString(FinalString.PHONE);
            mPassWord = getIntent().getExtras().getString(FinalString.PASSWORD);
            mBoxId = getIntent().getExtras().getString(FinalString.BOX_ID);
        }

        mTvSave.setVisibility(View.GONE);

        getTime = TimesCalculator.getStringDate();
        mDialog = new MaterialDialog.Builder(this).content(R.string.GetAlarmSettings)
                .progress(true, 0).cancelable(false)
                .keyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dismissDialog();
                            return true;
                        }
                        return false;
                    }
                })
                .build();
        mDialog.show();
        mGetVoiceInfoPresenter = PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId);


    }

    private void initSwitchButton() {
        stubView.mBtnVoiceObstacle.setTag(OBSTACLE);
        stubView.mBtnVoiceLost.setTag(FOLLOWLOST);
        stubView.mBtnVoicePowerOn.setTag(POWER_ON);
        stubView.mBtnVoiceControll.setTag(CONTROLL);
        stubView.mBtnVoiceFollow.setTag(FOLLOW);
        stubView.mBtnVoicePowerOff.setTag(POWER_OFF);

        stubView.mBtnBtPowerOff.setTag(BT);
        stubView.mBtnHelpPowerOff.setTag(HELP);
        stubView.mBtnLeanPowerOff.setTag(LEAN);
        stubView.mBtnBoxLostPowerOff.setTag(BOXLOST);
        stubView.mBtnLockPowerOff.setTag(LOCK);

        stubView.mBtnVoiceObstacle.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnVoiceLost.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnVoicePowerOn.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnVoiceControll.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnVoiceFollow.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnVoicePowerOff.setOnCheckedChangeListener(new MyCheckedChangeListener());

        stubView.mBtnBtPowerOff.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnHelpPowerOff.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnLeanPowerOff.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnBoxLostPowerOff.setOnCheckedChangeListener(new MyCheckedChangeListener());
        stubView.mBtnLockPowerOff.setOnCheckedChangeListener(new MyCheckedChangeListener());
    }

    @Override
    protected void onResume() {
        requestType = FinalString.READ_VOICE;
        mGetVoiceInfoPresenter.doRequest(getApplicationContext(), getTime, requestType, commandGetVoice, this);
        super.onResume();
    }

    class MyCheckedChangeListener implements SwitchButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            switch ((String) view.getTag()) {
                case OBSTACLE:
                    obCheck = isChecked ? "01" : "02";
                    break;
                case FOLLOWLOST:
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
                case BT:
                    btCheck = isChecked ? "01" : "02";
                    break;
                case HELP:
                    helpCheck = isChecked ? "01" : "02";
                    break;
                case LEAN:
                    leanCheck = isChecked ? "01" : "02";
                    break;
                case BOXLOST:
                    boxLostCheck = isChecked ? "01" : "02";
                    break;
                case LOCK:
                    lockCheck = isChecked ? "01" : "02";
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
                builder.append(obCheck).append(lsCheck).append(poCheck).append(ctCheck).append(flCheck).append(pfCheck)
                        .append(btCheck).append(helpCheck).append(leanCheck).append(boxLostCheck).append(lockCheck);
                commandSetVoice = Command.getCommand(Command.TYPE_VOICE, Command.VOICE_SET, "22", builder.toString());
                requestType = FinalString.SET_VOICE;
                getTime = TimesCalculator.getStringDate();
                mDialog.setContent(R.string.saving);
                mDialog.show();
                mGetVoiceInfoPresenter.doRequest(getApplicationContext(), getTime, requestType, commandSetVoice, this);
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

        mTvSave.setVisibility(View.VISIBLE);
        View view = mViewStubVoice.inflate();
        stubView = new MyStubView(view);


        initSwitchButton();
        isShow = true;
        if (requestType == FinalString.SET_VOICE) {
            showToast(this, getString(R.string.saveSucceed));
        } else if (requestType == FinalString.READ_VOICE) {
            showToast(this, getString(R.string.getSucceed));
        }

        dismissDialog();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            StringBuilder command = new StringBuilder(jsonObject.getString(FinalString.COMMAND));
            commandSetVoice = command.substring(10, 32);
            MySharedPreference.saveString(getApplicationContext(), FinalString.COMMAND, commandSetVoice);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCheckFromCommand(commandSetVoice);


                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReadFailed(String result) {

        if (VoiceActivity.this.isFinishing()) {
            Log.e(TAG, "isDestroyed： " + isDestroyed());
            Log.e(TAG, "isFinishing： " + isFinishing());
            return;
        }
        dismissDialog();

        if (requestType == FinalString.SET_VOICE) {
            showToast(this, getString(R.string.SaveFailed));
        } else if (requestType == FinalString.READ_VOICE) {
            showToast(this, getString(R.string.GetFailed));
        }

        if (isShow) {
            commandSetVoice = MySharedPreference.getString(getApplicationContext(), FinalString.COMMAND, "0202020202020202020202");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCheckFromCommand(commandSetVoice);
                }
            });
        }
    }

    private void setCheckFromCommand(String command) {
        stubView.mBtnVoiceObstacle.setChecked(command.substring(0, 2).equals("01"));
        stubView.mBtnVoiceLost.setChecked(command.substring(2, 4).equals("01"));
        stubView.mBtnVoicePowerOn.setChecked(command.substring(4, 6).equals("01"));
        stubView.mBtnVoiceControll.setChecked(command.substring(6, 8).equals("01"));
        stubView.mBtnVoiceFollow.setChecked(command.substring(8, 10).equals("01"));
        stubView.mBtnVoicePowerOff.setChecked(command.substring(10, 12).equals("01"));

        stubView.mBtnBtPowerOff.setChecked(command.substring(12, 14).equals("01"));
        stubView.mBtnHelpPowerOff.setChecked(command.substring(14, 16).equals("01"));
        stubView.mBtnLeanPowerOff.setChecked(command.substring(16, 18).equals("01"));
        stubView.mBtnBoxLostPowerOff.setChecked(command.substring(18, 20).equals("01"));
        stubView.mBtnLockPowerOff.setChecked(command.substring(20, 22).equals("01"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
