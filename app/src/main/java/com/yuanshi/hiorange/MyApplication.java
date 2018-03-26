package com.yuanshi.hiorange;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.yuanshi.hiorange.receiver.NetworkStateReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zyc
 * @date 2018/2/6
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private List<Activity> mActivityList = new ArrayList<>();
    private static MyApplication application = null;
    private NetworkStateReceiver mNetworkStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mNetworkStateReceiver = new NetworkStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, intentFilter);
    }

    public static MyApplication getInstance() {
        return application;
    }

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void cleanActivitys() {
        for (Activity mActivity :
                mActivityList) {
            mActivity.finish();
        }
    }

    public void exit() {
        for (Activity mActivity :
                mActivityList) {
            mActivity.finish();
        }
        if (mNetworkStateReceiver != null) {
            unregisterReceiver(mNetworkStateReceiver);

        }
        System.exit(0);
    }


}
