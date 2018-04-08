package com.yuanshi.hiorange.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.yuanshi.hiorange.activity.BoxMissingDialog;
import com.yuanshi.hiorange.bean.BoxMissingInfo;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.Command;
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

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final String commandReadInfo = Command.getCommand(Command.TYPE_READ_BOX, "00", "00", "");
    private ScheduledFuture<?> mTest;
    private Handler mHandler = new Handler();
    private int count;

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        count = 0;
        Log.e(TAG, "onStartCommand: ");

        mTest = executor.scheduleAtFixedRate(
                new Runnable() {

                    @Override
                    public void run() {

                        String mBoxId;
                        String mPhone;
                        Log.e(TAG, "run: " + count++);
                        mPhone = MySharedPreference.getString(RequestService.this, FinalString.PHONE, "");
                        mBoxId = MySharedPreference.getString(RequestService.this, FinalString.BOX_ID, "");
                        PresenterFactory.createBoxMissingPresenter(mPhone, mBoxId)
                                .doRequest(RequestService.this, TimesCalculator.getStringDate(), FinalString.BOX_MISS, commandReadInfo, RequestService.this);
                    }

                }, 0, 2000, TimeUnit.MILLISECONDS);
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

    private boolean writeToFile(String time, boolean isMiss) {
        String fileName = "boxInfo.txt";
        StringBuilder sb = new StringBuilder();
        BoxMissingInfo boxMissingInfo;
        try {
            File file = new File(Environment.getDataDirectory(), fileName);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "";
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
                file.mkdirs();
                boxMissingInfo = new BoxMissingInfo();
                if (isMiss) {
                    boxMissingInfo.getMissTime().add(time);
                } else {
                    boxMissingInfo.getOpenTime().add(time);
                }
                sb.append(new Gson().toJson(boxMissingInfo));
            }

            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(sb.toString().getBytes("UTF-8"));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
