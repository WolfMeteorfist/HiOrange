package com.yuanshi.hiorange.activity;

/**
 * Created by Administrator on 2018/3/15.
 */

public interface IFingerView {

    void onReadSucceed(String result);

    void onReadFailed(String result);

    void onRegisterSucceed(int regCount);


}
