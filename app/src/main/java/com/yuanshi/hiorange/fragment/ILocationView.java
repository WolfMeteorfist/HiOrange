package com.yuanshi.hiorange.fragment;

/**
 * Created by Administrator on 2018/3/5.
 */

public interface ILocationView {

    void getGPSSucceed(String result, String lat, String lng,String cmdTime);

    void getGPSFailed(String result);

}
