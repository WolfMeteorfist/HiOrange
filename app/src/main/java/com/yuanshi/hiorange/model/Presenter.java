package com.yuanshi.hiorange.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yuanshi.hiorange.activity.IAddBoxView;
import com.yuanshi.hiorange.activity.ILoginView;
import com.yuanshi.hiorange.activity.IRegisterView;
import com.yuanshi.hiorange.activity.IUnbindView;
import com.yuanshi.hiorange.fragment.ILocationView;
import com.yuanshi.hiorange.util.FinalString;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.yuanshi.hiorange.util.Codec.getSign;

/**
 * @author Administrator
 * @date 2018/2/26
 */

public class Presenter implements IPresenter {

    private static final String TAG = "Presenter";

    private String mPhoneNumber = "";
    private String mPassWord = "";
    private String mVerifyCode = "";
    private String mBoxId = "";
    private String mCommand = "";


    /**
     * 1.登陆
     * 2.添加箱子设备
     * 3.获取箱子信息
     *
     * @param phoneNumber 手机账号
     * @param pwOrbox     1.密码 2.箱子ID
     * @param type
     */
    Presenter(String phoneNumber, String pwOrbox, int type) {
        switch (type) {
            case FinalString.TYPE_LOGIN:
                mPhoneNumber = phoneNumber;
                mPassWord = pwOrbox;
                break;
            case FinalString.TYPE_BIND_BOX:
                mPhoneNumber = phoneNumber;
                mBoxId = pwOrbox;
                break;
            case FinalString.TYPE_GET_INFO:
                mPhoneNumber = phoneNumber;
                mBoxId = pwOrbox;
                break;
            case FinalString.TYPE_UNBIND_BOX:
                mPhoneNumber = phoneNumber;
                mBoxId = pwOrbox;
                break;
            default:
                break;
        }
    }

    /**
     * 1.注册
     * 2.要求终端上传箱子状态
     *
     * @param phoneNumber 手机账号
     * @param pwOrbox     1.密码 2.箱子ID
     * @param vcOrCmd     1.验证码 2.箱子指令
     * @param type
     */
    Presenter(String phoneNumber, String pwOrbox, String vcOrCmd, int type) {
        switch (type) {
            case FinalString.TYPE_REGISTER:
                mPhoneNumber = phoneNumber;
                mPassWord = pwOrbox;
                mVerifyCode = vcOrCmd;
                break;
            case FinalString.TYPE_READ_BOX:
                mPhoneNumber = phoneNumber;
                mBoxId = pwOrbox;
                mCommand = vcOrCmd;
                break;
            case FinalString.TYPE_GPS:
                mPhoneNumber = phoneNumber;
                mBoxId = pwOrbox;
                break;
            default:
                break;
        }
    }

    /**
     * 用户登录
     */
    @Override
    public void login(ILoginView mILoginView) {

        JSONObject jsonObject = new JSONObject();

        try {
            Map<String, String> userMap = new HashMap<>();
            userMap.put(FinalString.PHONE, mPhoneNumber);
            userMap.put(FinalString.PASSWORD, mPassWord);
            JSONObject userJson = new JSONObject(userMap);

            jsonObject.put(FinalString.ACT, "login");
            jsonObject.put(FinalString.USER, userJson);
            jsonObject.put(FinalString.VERSION, "1.0");

            String sign = getSign(jsonObject.toString());

            jsonObject.put(FinalString.SIGN, sign);

            ResultRequest resultRequest = new ResultRequest();
            resultRequest.executeLoginTask(jsonObject, mILoginView);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 绑定箱子
     */
    @Override
    public void bindBox(IAddBoxView mIAddBoxView) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(FinalString.PHONE, mPhoneNumber);
            jsonObject.put(FinalString.BOX_ID, mBoxId);
            jsonObject.put(FinalString.ACT, "bind");
            String sign = getSign(jsonObject.toString());
            jsonObject.put(FinalString.SIGN, sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ResultRequest resultRequest = new ResultRequest();
        resultRequest.executeBindBoxTask(jsonObject, mIAddBoxView);


    }

    /**
     * 用户注册
     */
    @Override
    public void register(IRegisterView mIRegisterView) {
        JSONObject jsonObject = new JSONObject();
        try {

            Map<String, String> userMap = new HashMap<>();
            userMap.put(FinalString.PHONE, mPhoneNumber);
            userMap.put(FinalString.PASSWORD, mPassWord);
            JSONObject userJson = new JSONObject(userMap);


            Map<String, String> verifyMap = new HashMap<>();
            verifyMap.put(FinalString.VALUE, mVerifyCode);
            JSONObject verifyJson = new JSONObject(verifyMap);


            jsonObject.put(FinalString.USER, userJson);
            jsonObject.put(FinalString.ACT, "register");
            jsonObject.put(FinalString.VERIFICATIONCODE, verifyJson);

            String sign = getSign(jsonObject.toString());
            jsonObject.put(FinalString.SIGN, sign);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        ResultRequest resultRequest = new ResultRequest();
        resultRequest.executeRegisterTask(jsonObject, mIRegisterView);
    }


    /**
     * 要求终端上传箱子状态
     */
    @Override
    public void readOrSetBox() {
        JSONObject jsonObject = new JSONObject();
        try {
            //参数请参考协议

            jsonObject.put(FinalString.PHONE, mPhoneNumber);
            jsonObject.put(FinalString.BOX_ID, mBoxId);
            jsonObject.put(FinalString.COMMAND, mCommand);
            jsonObject.put(FinalString.ACT, "set");

            String sign = getSign(jsonObject.toString());
            jsonObject.put(FinalString.SIGN, sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ResultRequest resultRequest = new ResultRequest();
        resultRequest.setCommand(mCommand);
        resultRequest.executeReadOrSetBoxTask(jsonObject);
    }

    /**
     * 获取终端箱子信息
     *  @param context
     * @param objectView 泛型*/
    @Override
    public void getInfo(Context context, @NonNull String getTime, @NonNull int getType, String command, Object objectView) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(FinalString.PHONE, mPhoneNumber);
            jsonObject.put(FinalString.BOX_ID, mBoxId);
            jsonObject.put(FinalString.ACT, "query");

            String sign = getSign(jsonObject.toString());
            jsonObject.put(FinalString.SIGN, sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ResultRequest resultRequest = new ResultRequest(context);
        resultRequest.setGetTime(getTime);
        resultRequest.setGetType(getType);
        resultRequest.setCommand(command);
        resultRequest.executeGetInfoTask(context, jsonObject, objectView);
    }

    /**
     * 获取终端箱子信息
     *  @param context
     * @param objectView 泛型*/
    @Override
    public void getBoxMiss(Context context, @NonNull String getTime, @NonNull int getType, String command, Object objectView) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(FinalString.PHONE, mPhoneNumber);
            jsonObject.put(FinalString.BOX_ID, mBoxId);
            jsonObject.put(FinalString.ACT, "query");

            String sign = getSign(jsonObject.toString());
            jsonObject.put(FinalString.SIGN, sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ResultRequest resultRequest = new ResultRequest(context);
        resultRequest.setGetTime(getTime);
        resultRequest.setGetType(getType);
        resultRequest.setCommand(command);
        resultRequest.excuteGetInfoAuto(jsonObject, objectView);
    }

    @Override
    public void getGPS(ILocationView iLocationView) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(FinalString.PHONE, mPhoneNumber);
            jsonObject.put(FinalString.BOX_ID, mBoxId);
            jsonObject.put(FinalString.ACT, "query_gps");

            String sign = getSign(jsonObject.toString());
            jsonObject.put(FinalString.SIGN, sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ResultRequest resultRequest = new ResultRequest();
        resultRequest.executeGetGpsTask(jsonObject, iLocationView);

    }

    @Override
    public void unbindBox(IUnbindView iUnbindView) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(FinalString.PHONE, mPhoneNumber);
            jsonObject.put(FinalString.BOX_ID, mBoxId);
            jsonObject.put(FinalString.ACT, "unbind");

            String sign = getSign(jsonObject.toString());
            jsonObject.put(FinalString.SIGN, sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ResultRequest resultRequest = new ResultRequest();
        resultRequest.executeUnbindBoxTask(jsonObject, iUnbindView);
    }

}