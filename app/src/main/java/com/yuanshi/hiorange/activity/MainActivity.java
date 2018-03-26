package com.yuanshi.hiorange.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yuanshi.hiorange.BaseActivity;
import com.yuanshi.hiorange.MyApplication;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.fragment.BoxFragment;
import com.yuanshi.hiorange.fragment.ControllerFragment;
import com.yuanshi.hiorange.fragment.LocationFragment;
import com.yuanshi.hiorange.fragment.SettingFragment;
import com.yuanshi.hiorange.util.BottomNavigationViewHelper;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.MySharedPreference;

/**
 * @author 大神愁
 */
public class MainActivity extends BaseActivity {


    private ControllerFragment mControllerFragment;
    private long mLastTime = 0;
    private String phoneNumber;
    private String passWord;
    private String boxId;

    private enum FragmentTYPE {
        BOX,
        LOCATION,
        CONTROLL,
        SETTING
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getBoxId() {
        return boxId;
    }

    private FragmentManager mFragmentManager;
    private BoxFragment mBoxFragment;
    private SettingFragment mSettingFragment;
    private LocationFragment mLocationFragment;
    private Fragment curFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_box:
                    changeFragment(mBoxFragment, FragmentTYPE.BOX);
                    return true;
                case R.id.navigation_location:
                    changeFragment(mLocationFragment, FragmentTYPE.LOCATION);
                    return true;
                case R.id.navigation_controller:
                    changeFragment(mControllerFragment, FragmentTYPE.CONTROLL);
                    return true;
                case R.id.navigation_setting:
                    changeFragment(mSettingFragment, FragmentTYPE.SETTING);
                    return true;
                default:
                    return false;
            }
        }
    };

    /**
     * 隐藏当前curfragment，并显示指定fragment
     *
     * @param fragment 指定fragment
     * @param type     Fragment类型
     */
    private void changeFragment(Fragment fragment, FragmentTYPE type) {

        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        if (curFragment == fragment) {
            return;
        }


        if (fragment == null) {
            switch (type) {
                case BOX:
                    fragment = new BoxFragment();
                    break;
                case LOCATION:
                    fragment = new LocationFragment();
                    break;
                case CONTROLL:
                    fragment = new ControllerFragment();
                    break;
                case SETTING:
                    fragment = new SettingFragment();
                    break;
                default:
                    break;
            }
        }

        mFragmentTransaction.hide(curFragment);
        mFragmentTransaction.show(fragment);

        curFragment = fragment;
        mFragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        MyApplication.getInstance().addActivity(this);
        initView(savedInstanceState);
        setDefaultFragment();

    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView(Bundle savedInstanceState) {
        //该接口回调是用于关闭loginActivity中的dialog


        phoneNumber = MySharedPreference.getString(this, FinalString.PHONE, "");
        boxId = MySharedPreference.getString(this, FinalString.BOX_ID, "");
        passWord = MySharedPreference.getString(this, FinalString.PASSWORD, "");

        mFragmentManager = getFragmentManager();

        int option;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(option);
        BottomNavigationView navigation = findViewById(R.id.navigation);

        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };
        int[] colors = new int[]{getResources().getColor(R.color.uncheck_color, null),
                getResources().getColor(R.color.checked_color, null)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            navigation.setItemIconTintList(colorStateList);
            navigation.setItemTextColor(colorStateList);
        }

        BottomNavigationViewHelper.disableShiftMode(navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            mBoxFragment = new BoxFragment();
            mSettingFragment = new SettingFragment();
            mLocationFragment = new LocationFragment();
            mControllerFragment = new ControllerFragment();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("Zyc", "onActivityResult: " + requestCode + resultCode);
        if (requestCode == 1 && resultCode == UnbindActivity.UNBIND_RESULT_CODE_OK) {
            startActivities(LoginActivity.class, null);
            finish();
        }
    }

    /**
     * 设置默认的Fragment
     */
    private void setDefaultFragment() {
        if (mFragmentManager == null) {
            mFragmentManager = getFragmentManager();
        }
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        mFragmentTransaction.add(R.id.frameLayout, mSettingFragment);
        mFragmentTransaction.hide(mSettingFragment);
        mFragmentTransaction.add(R.id.frameLayout, mControllerFragment);
        mFragmentTransaction.hide(mControllerFragment);
        mFragmentTransaction.add(R.id.frameLayout, mLocationFragment);
        mFragmentTransaction.hide(mLocationFragment);
        mFragmentTransaction.add(R.id.frameLayout, mBoxFragment);

        curFragment = mBoxFragment;

        mFragmentTransaction.commit();
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

}
