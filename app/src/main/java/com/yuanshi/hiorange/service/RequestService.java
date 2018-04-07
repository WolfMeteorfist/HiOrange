package com.yuanshi.hiorange.service;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018/4/4.
 */

public class RequestService extends IntentService  implements IServiceView {


    private AlertDialog mAlertDialog;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RequestService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    private void startRequest() {
//        PresenterFactory
//                .createGetInfoPresenter(MySharedPreference.getString(this, FinalString.PHONE, ""), MySharedPreference.getString(this, FinalString.BOX_ID, ""))
//                .do
//            ;
    }

    @Override
    public void showBoxDialog(String result) {
        mAlertDialog = new AlertDialog.Builder(this)
                .setMessage(result)
                .setCancelable(true)
                .create();
    }

}
