package com.yuanshi.hiorange.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 *
 * @author Administrator
 * @date 2018/2/26
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkState";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "收到广播");
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    break;
                default:
                    break;
            }
        } else {
            Log.i(TAG, "网络异常");
            Toast.makeText(context, "请检查网络设置.", Toast.LENGTH_SHORT).show();
        }
    }


}
