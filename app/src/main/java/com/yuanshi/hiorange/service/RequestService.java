package com.yuanshi.hiorange.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.yuanshi.hiorange.activity.BoxMissingDialog;
import com.yuanshi.hiorange.bean.BoxInfo;
import com.yuanshi.hiorange.bean.BoxMissingInfo;
import com.yuanshi.hiorange.fragment.BoxFragment;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.Command;
import com.yuanshi.hiorange.util.FileUtils;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.MySharedPreference;
import com.yuanshi.hiorange.util.TimesCalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/4/4.
 */

public class RequestService extends Service implements IServiceView {


    private static final String TAG = "RequestService";
    public static final String ACTION_STOP_SERVICE = "com.yuanshi.hiorange.action.stopservice";
    private final String commandReadInfo = Command.getCommand(Command.TYPE_READ_BOX, "00", "00", "");

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> mTest;

    @Override
    public void onCreate() {
        super.onCreate();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == ACTION_STOP_SERVICE) {
                    stopSelf();
                    unregisterReceiver(this);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOP_SERVICE);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mTest = executor.scheduleAtFixedRate(
                new Runnable() {

                    @Override
                    public void run() {

                        String mBoxId;
                        String mPhone;
                        mPhone = MySharedPreference.getString(RequestService.this, FinalString.PHONE, "");
                        mBoxId = MySharedPreference.getString(RequestService.this, FinalString.BOX_ID, "");
                        PresenterFactory.createBoxMissingPresenter(mPhone, mBoxId)
                                .doRequest(RequestService.this, TimesCalculator.getStringDate(), FinalString.BOX_MISS, commandReadInfo, RequestService.this);
                        PresenterFactory.createBoxMissingPresenter(mPhone, mBoxId)
                                .doRequest(RequestService.this, TimesCalculator.getStringDate(), FinalString.READ_BOX, commandReadInfo, RequestService.this);
                    }

                }, 0, 2, TimeUnit.SECONDS);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mTest != null && !mTest.isCancelled()) {
            mTest.cancel(true);
            mTest = null;
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void showBoxDialog(String result, String time) {

        if (result.equals("箱子丢失")) {
            writeToFile(time, true);
        } else if (result.equals("箱子已打开")) {
            writeToFile(time, false);
        }
        Intent intent = new Intent();
        intent.setClass(RequestService.this, BoxMissingDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("text", result);
        startActivity(intent);
    }

    @Override
    public void updateBoxInfo(BoxInfo boxInfo) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("boxInfo", boxInfo);
        intent.putExtra("boxInfoBundle", bundle);
        intent.setAction(BoxFragment.INFO_UPDATE_ACTION);
        Log.e(TAG, "发送updateBox广播 ");
        sendBroadcast(intent);
    }

    private void writeToFile(String time, boolean isMiss) {
        StringBuilder sb = new StringBuilder();
        BoxMissingInfo boxMissingInfo;
        FileOutputStream fos = null;
        try {
            File file = FileUtils.getBoxInfoPath();
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while (((line = reader.readLine()) != null)) {
                    sb.append(line);
                }
                boxMissingInfo = new Gson().fromJson(sb.toString(), BoxMissingInfo.class);
                if (isMiss) {
                    List<String> missTime = boxMissingInfo.getMissTime();
                    missTime.add(time);
                } else {
                    List<String> openTime = boxMissingInfo.getOpenTime();
                    openTime.add(time);
                }

                sb.delete(0, sb.length());
                sb.append(new Gson().toJson(boxMissingInfo));

                reader.close();
            } else {
                file.createNewFile();
//                file.
                boxMissingInfo = new BoxMissingInfo();
                if (isMiss) {
                    boxMissingInfo.getMissTime().add(time);
                } else {
                    boxMissingInfo.getOpenTime().add(time);
                }
                sb.append(new Gson().toJson(boxMissingInfo));
            }

            fos = new FileOutputStream(file, false);
            fos.write(sb.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            FileUtils.deleteBoxInfoFile();
            writeToFile(time, isMiss);
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
