package com.yuanshi.hiorange.model;

import android.content.Context;

import com.yuanshi.hiorange.activity.IAddBoxView;
import com.yuanshi.hiorange.activity.ILoginView;
import com.yuanshi.hiorange.activity.IRegisterView;
import com.yuanshi.hiorange.activity.IUnbindView;
import com.yuanshi.hiorange.fragment.ILocationView;

/**
 * Created by Administrator on 2018/2/5.
 */

public interface IPresenter {

    void login(ILoginView iLoginView);

    void bindBox(IAddBoxView iAddBoxView);

    void register(IRegisterView iRegisterView);

    void unbindBox(IUnbindView iUnbindView);

    void getGPS(ILocationView iLocationView);

    void readOrSetBox();

    void getInfo(Context context, String getTime, int getType, String command, Object objectView);


}
