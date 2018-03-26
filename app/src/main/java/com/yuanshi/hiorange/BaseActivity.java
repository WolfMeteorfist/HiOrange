package com.yuanshi.hiorange;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/2/25.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    public final int REQUEST_CODE_FROM_LOGIN = 0x66;
    public final int RESULT_CODE_FROM_REGISTER = 0x67;
    private static Toast mToast;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void startActivities(Class mClass, Bundle mBundle) {
        Intent intent = new Intent(getApplicationContext(), mClass);
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        startActivity(intent);
    }

    public void startActivitiesForResult(Class mClass, Bundle mBundle, int requestCode) {

        Intent intent = new Intent(getApplicationContext(), mClass);
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public boolean isNetWork() {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (mNetworkInfo != null && mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()) {
            return true;
        }
        new AlertDialog.Builder(this)
                .setMessage(R.string.network_failed)
                .setCancelable(false)
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.e(TAG, "onClick: " + which);
                    }
                }).show();
        return false;

    }

    public void showToast(final Context mContext, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(msg);
                }
                mToast.show();
            }
        });

    }

}
