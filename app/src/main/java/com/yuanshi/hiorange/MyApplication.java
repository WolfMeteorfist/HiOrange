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

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static MyApplication getInstance() {
        return application;
    }

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void removeActivitys(Activity activity) {
        if (mActivityList.size() > 0) {
            mActivityList.remove(activity);
        }
    }

    public void exit() {
        for (Activity mActivity :
                mActivityList) {
            mActivity.finish();
        }
    }

}
