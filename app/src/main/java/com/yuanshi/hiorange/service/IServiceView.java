package com.yuanshi.hiorange.service;

import com.yuanshi.hiorange.bean.BoxInfo;

/**
 * 创建业务
 * Created by Administrator on 2018/4/4.
 */

public interface IServiceView {

    void showBoxDialog(String result, String time);

    void updateBoxInfo(BoxInfo boxInfo);
}
