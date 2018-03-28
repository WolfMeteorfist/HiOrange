package com.yuanshi.hiorange.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yuanshi.hiorange.activity.IAddBoxView;
import com.yuanshi.hiorange.activity.ILoginView;
import com.yuanshi.hiorange.activity.IRegisterView;
import com.yuanshi.hiorange.activity.IUnbindView;
import com.yuanshi.hiorange.fragment.ILocationView;
import com.yuanshi.hiorange.util.FinalString;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Zyc
 * @date 2018/2/26
 */

public class PresenterFactory {

    private static final String TAG = "Presenter";

    public static class LoginPresenter extends Presenter {

        LoginPresenter(String phoneNumber, String passWord) {
            //type为 LOGIN
            super(phoneNumber, passWord, FinalString.TYPE_LOGIN);
        }

        public void doRequest(ILoginView iLoginView) {
            login(iLoginView);
        }
    }

    public static class RegisterPresenter extends Presenter {

        RegisterPresenter(String phoneNumber, String passWord, String verifyCode) {
            //type为 REGISTER
            super(phoneNumber, passWord, verifyCode, FinalString.TYPE_REGISTER);
        }

        public void doRequest(IRegisterView iRegisterView) {
            register(iRegisterView);
        }
    }

    public static class AddBoxPresenter extends Presenter {

        AddBoxPresenter(String phoneNumber, String boxId) {
            //type为 ADD BOX
            super(phoneNumber, boxId, FinalString.TYPE_BIND_BOX);
        }

        public void doRequest(IAddBoxView iAddBoxView) {
            bindBox(iAddBoxView);
        }
    }

    public static class GetInfoPresenter extends Presenter {

        GetInfoPresenter(String phoneNumber, String boxId) {
            //type为 ADD BOX
            super(phoneNumber, boxId, FinalString.TYPE_GET_INFO);
        }

        public void doRequest(Context context, @NonNull String getTime, @NonNull int getType, String command, Object object) {
            getInfo(context, getTime, getType, command, object);
        }
    }

    public static class ReadOrSetBoxPresenter extends Presenter {

        ReadOrSetBoxPresenter(String phoneNumber, String boxId, String command) {
            //type为 ADD BOX
            super(phoneNumber, boxId, command, FinalString.TYPE_READ_BOX);
        }

        public void doRequest() {
            readOrSetBox();
        }
    }

    public static class GetGPSPresenter extends Presenter {

        GetGPSPresenter(String phoneNumber, String pwOrbox) {
            super(phoneNumber, pwOrbox, FinalString.TYPE_GPS);
        }

        public void doRequest(ILocationView iLocationView) {
            getGPS(iLocationView);
        }
    }

    public static class UnbindBoxPresenter extends Presenter {

        UnbindBoxPresenter(String phoneNumber, String pwOrbox) {
            super(phoneNumber, pwOrbox, FinalString.TYPE_UNBIND_BOX);
        }

        public void doRequest(IUnbindView iUnbindView) {
            unbindBox(iUnbindView);
        }
    }

    /**
     * 创建登录执行者
     *
     * @param phoneNumber 手机账号
     * @param passWord    密码
     * @return 登录Presenter
     */
    public static LoginPresenter createLoginPresenter(String phoneNumber, String passWord) {
        return new LoginPresenter(phoneNumber, passWord);
    }

    /**
     * 创建注册执行者
     *
     * @param phoneNumber 手机账号
     * @param passWord    密码
     * @param veriftyCode 验证码
     * @return 注册Presenter
     */
    public static RegisterPresenter createRegisterPresenter(String phoneNumber, String passWord, String veriftyCode) {
        return new RegisterPresenter(phoneNumber, passWord, veriftyCode);
    }

    /**
     * 创建添加箱子执行者
     *
     * @param phoneNumber 手机账号
     * @param boxId       箱子ID
     * @return 添加箱子Presenter
     */
    public static AddBoxPresenter createAddBoxPresenter(String phoneNumber, String boxId) {
        return new AddBoxPresenter(phoneNumber, boxId);
    }

    /**
     * 创建添加 获取箱子信息 执行者
     *
     * @param phoneNumber 手机账号
     * @param boxId       箱子ID
     * @return 添加箱子Presenter
     */
    public static GetInfoPresenter createGetInfoPresenter(String phoneNumber, String boxId) {
        return new GetInfoPresenter(phoneNumber, boxId);
    }

    /**
     * 创建添加 要求上传箱子信息 执行者
     *
     * @param phoneNumber 手机账号
     * @param boxId       箱子ID
     * @return 添加箱子Presenter
     */
    public static ReadOrSetBoxPresenter createReadOrSetBoxPresenter(String phoneNumber, String boxId, String command) {
        return new ReadOrSetBoxPresenter(phoneNumber, boxId, command);
    }

    /**
     * 创建添加 获取gps信息 执行者
     *
     * @param phoneNumber 手机账号
     * @param boxId       箱子ID
     * @return GetGPSPresenter
     */
    public static GetGPSPresenter createGetGPSPresenter(String phoneNumber, String boxId) {
        return new GetGPSPresenter(phoneNumber, boxId);
    }

    public static UnbindBoxPresenter createUnbindBoxPresenter(String phoneNumber, String boxId) {
        return new UnbindBoxPresenter(phoneNumber, boxId);
    }

}