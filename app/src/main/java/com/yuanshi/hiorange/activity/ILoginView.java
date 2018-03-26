package com.yuanshi.hiorange.activity;

/**
 * Created by Administrator on 2018/2/5.
 */

public interface ILoginView {

    /**
     * 登录成功，result为网络请求后结果
     */
    void onLoginSucceed(String result);

    /**
     * 登录失败，result为网络请求后结果
     */
    void onLoginFailed(String result);

    /**
     * 首次登录，result为网络请求后结果
     */
    void onNewUserLogin(String result);

}
