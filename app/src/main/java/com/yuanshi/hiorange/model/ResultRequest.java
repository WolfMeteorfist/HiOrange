package com.yuanshi.hiorange.model;

import android.content.Context;

import com.yuanshi.hiorange.activity.IAddBoxView;
import com.yuanshi.hiorange.activity.IFingerView;
import com.yuanshi.hiorange.activity.ILoginView;
import com.yuanshi.hiorange.activity.IRegisterView;
import com.yuanshi.hiorange.activity.IUnbindView;
import com.yuanshi.hiorange.activity.IVoiceView;
import com.yuanshi.hiorange.fragment.IBoxView;
import com.yuanshi.hiorange.fragment.ILocationView;
import com.yuanshi.hiorange.util.Codec;
import com.yuanshi.hiorange.util.Command;
import com.yuanshi.hiorange.util.FinalString;
import com.yuanshi.hiorange.util.MySharedPreference;
import com.yuanshi.hiorange.util.TimesCalculator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Zyc
 * @date 2018/2/6
 */

public class ResultRequest implements IResultModel {

    private final String ERROR_OK = "0";
    private final String ERROR_OFFLINE = "11";


//    private final String commandReadInfo = Command.getCommand(Command.TYPE_READ_BOX, "00", "00", "00");
//    private final String commandUnLock = Command.getCommand(Command.TYPE_LOCK, "02", "02", "02");
//    private final String commandLock = Command.getCommand(Command.TYPE_LOCK, "02", "02", "01");
//    private final String commandLockedState = Command.getCommand(Command.TYPE_LOCK, "01", "00", "");
//    private final String commandReadVoice = Command.getCommand(Command.TYPE_VOICE, Command.VOICE_READ, "00", "");
//    private final String commandSetVoice = Command.getCommand(Command.TYPE_VOICE, Command.VOICE_SET, "12", );


    private PresenterFactory.GetInfoPresenter mGetInfoPresenter;
    private PresenterFactory.ReadOrSetBoxPresenter mSetPresenter;


    //申请超时时间
    private long REQUEST_TIME_OUT = 10 * 1000;

    //命令有效时间
    private final long CMD_TIME_OUT = 60 * 1000;

    //请求时间，针对GetInfo使用
    private String getTime;

    /**
     * 请求类型，针对GetInfo使用
     */
    private int getType;

    private String mPhoneNumber;
    private String mPassWord;
    private String mBoxId;
    private boolean locked;
    private String mCommand;
    private int regCount;
    private boolean isStop;


    ResultRequest() {

    }

    ResultRequest(Context mActivityContext) {

        mPhoneNumber = MySharedPreference.getString(mActivityContext, FinalString.PHONE, "");
        mPassWord = MySharedPreference.getString(mActivityContext, FinalString.PASSWORD, "");
        mBoxId = MySharedPreference.getString(mActivityContext, FinalString.BOX_ID, "");

    }


    public void setGetTime(String getTime) {
        this.getTime = getTime;
    }

    public void setGetType(int getType) {
        this.getType = getType;
    }

    public void setCommand(String command) {
        mCommand = command;
        mSetPresenter = PresenterFactory.createReadOrSetBoxPresenter(mPhoneNumber, mBoxId, mCommand);

    }

    @Override
    public void executeLoginTask(final JSONObject mJSONObject, final ILoginView mILoginView) {
        M_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String result = sendPost(FinalString.URL, Codec.codec(mJSONObject.toString()));
                JSONObject jsonObject;
                try {

                    jsonObject = new JSONObject(result);
                    String errorCode = jsonObject.getString(FinalString.ERROR_CODE);
                    String errorMsg = jsonObject.getString(FinalString.ERROR_MSG);

                    if (errorCode.equals(ERROR_OK)) {
                        String boxId = jsonObject.getString(FinalString.BOX_ID);
                        if (("").equals(boxId)) {
                            mILoginView.onNewUserLogin(errorMsg);
                        } else {
                            mILoginView.onLoginSucceed(boxId);
                        }
                    } else {
                        mILoginView.onLoginFailed(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 获取信息
     *
     * @param mContext    调用类的上下文
     * @param mJSONObject json参数
     * @param mObjectView 回调接口 泛型
     */
    @Override
    public void executeGetInfoTask(final Context mContext, final JSONObject mJSONObject, final Object mObjectView) {

        mGetInfoPresenter = PresenterFactory.createGetInfoPresenter(mPhoneNumber, mBoxId);

        M_POOL_EXECUTOR.execute(new Runnable() {

            @Override
            public void run() {
                String result = sendPost(FinalString.URL, Codec.codec(mJSONObject.toString()));
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    String errorCode = jsonObject.getString(FinalString.ERROR_CODE);
                    //是否与服务器连接OK
                    if (errorCode.equals(ERROR_OK)) {
                        String currentTime = TimesCalculator.getStringDate();
                        long wastedTime = TimesCalculator.calculateSeconds(currentTime, getTime);
                        if (getType == FinalString.FINGER_REGISTER) {
                            REQUEST_TIME_OUT = 20 * 1000;
                        }
                        //是否请求超时，<则未超时
                        if (wastedTime < REQUEST_TIME_OUT) {
                            String cmdTime = jsonObject.getString(FinalString.TIME);
                            long different = TimesCalculator.calculateSeconds(getTime, cmdTime);
                            //是否有效Cmd，<则有效
                            if (different < CMD_TIME_OUT) {
                                //判断当前需要的类型
                                String command = jsonObject.getString(FinalString.COMMAND);
                                String type = command.substring(4, 6);
                                String locked = "";

                                switch (getType) {
                                    case FinalString.READ_BOX:
                                        if (type.equals(Command.TYPE_READ_BOX)) {
                                            //成功!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                            ((IBoxView) (mObjectView)).onReadSucceed(result);
                                        } else {
                                            //重新获取箱子信息
                                            mSetPresenter.doRequest();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);
                                        }
                                        break;
                                    case FinalString.SET_LOCK:
                                        if (command.length() > 12) {
                                            locked = command.substring(10, 12);
                                        }
                                        if (type.equals(Command.TYPE_LOCK) && ("01").equals(locked)) {
                                            //成功!!!!!!!!!!上锁只能在boxView界面
                                            ((IBoxView) (mObjectView)).onReadSucceed(result);
                                        } else {
                                            //重新上锁
                                            mSetPresenter.doRequest();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);
                                        }
                                        break;
                                    case FinalString.SET_UNLOCK:
                                        if (command.length() > 12) {
                                            locked = command.substring(10, 12);
                                        }
                                        if (type.equals(Command.TYPE_LOCK) && ("02").equals(locked)) {
                                            //成功!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                            ((IBoxView) (mObjectView)).onReadSucceed(result);
                                        } else {
                                            //重新解锁
                                            mSetPresenter.doRequest();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);
                                        }
                                        break;
                                    case FinalString.SET_VOICE:
                                        if (type.equals(Command.TYPE_VOICE)
                                                && mCommand.length() > 22
                                                && command.length() > 22
                                                && mCommand.substring(10, 22).equals(command.substring(10, 22))) {
                                            //成功!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                            ((IVoiceView) (mObjectView)).onReadSucceed(result);
                                        } else {
                                            //重新获取
                                            mSetPresenter.doRequest();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);
                                        }
                                        break;
                                    case FinalString.READ_VOICE:
                                        if (type.equals(Command.TYPE_VOICE)) {
                                            //成功!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                            ((IVoiceView) (mObjectView)).onReadSucceed(result);
                                        } else {
                                            //重新获取
                                            mSetPresenter.doRequest();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);
                                        }
                                        break;
                                    case FinalString.FINGER_CANCEL:

                                        break;
                                    case FinalString.FINGER_REGISTER:

                                        //5555080202011xxxx
                                        if (type.equals(Command.TYPE_FINGER)) {

                                            //已经进入了录制状态
                                            if (command.substring(6, 12).equals("020201")) {

                                                regCount = Integer.valueOf(command.substring(14, 15));

                                                //录入成功次数少于3次持续跟服务器要数据,不应该超时
                                                if (regCount < 3) {
                                                    //所以传入currentTime，使得不会超时
                                                    mGetInfoPresenter.doRequest(mContext, currentTime, getType, mCommand, mObjectView);
                                                }
                                                ((IFingerView) mObjectView).onRegisterSucceed(regCount);

                                            } else {
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);

                                            }

                                        } else {

                                            //重新获取
                                            mSetPresenter.doRequest();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);

                                        }
                                        break;
                                    case FinalString.FINGER_DELETE:

                                        if (type.equals(Command.TYPE_FINGER) && command.substring(6, 12).equals("030201")) {
                                            //成功!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                            ((IFingerView) (mObjectView)).onReadSucceed(result);
                                        } else {
                                            //重新获取
                                            mSetPresenter.doRequest();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);
                                        }
                                        break;
                                    case FinalString.READ_FINGER:

                                        if (type.equals(Command.TYPE_FINGER) && command.substring(6, 8).equals("01")) {
                                            //成功!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!5555080105000008091
                                            ((IFingerView) (mObjectView)).onReadSucceed(command);
                                        } else {
                                            //重新获取
                                            mSetPresenter.doRequest();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);
                                        }
                                        break;
                                    default:
                                        break;
                                }

                            } else {
                                //Cmd失效
                                mSetPresenter.doRequest();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mGetInfoPresenter.doRequest(mContext, getTime, getType, mCommand, mObjectView);
                            }
                        } else {
                            //申请时间超过REQUEST_TIME_OUT
                            if (mObjectView instanceof IBoxView) {
                                ((IBoxView) (mObjectView)).onReadFailed(result);
                            } else if (mObjectView instanceof IVoiceView) {
                                ((IVoiceView) (mObjectView)).onReadFailed(result);
                            } else if (mObjectView instanceof IFingerView) {
                                ((IFingerView) (mObjectView)).onReadFailed(result);
                            }
                        }
                    } else {
                        //error_code != "0"
                        if (mObjectView instanceof IBoxView) {
                            ((IBoxView) (mObjectView)).onReadFailed(result);
                        } else if (mObjectView instanceof IVoiceView) {
                            ((IVoiceView) (mObjectView)).onReadFailed(result);
                        } else if (mObjectView instanceof IFingerView) {
                            ((IFingerView) (mObjectView)).onReadFailed(result);
                        }
                    }
                } catch (JSONException e) {

                    if (mObjectView instanceof IBoxView) {
                        ((IBoxView) (mObjectView)).onReadFailed(result);
                    } else if (mObjectView instanceof IVoiceView) {
                        ((IVoiceView) (mObjectView)).onReadFailed(result);
                    } else if (mObjectView instanceof IFingerView) {
                        ((IFingerView) (mObjectView)).onReadFailed(result);
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 执行注册
     *
     * @param mJSONObject    json参数
     * @param mIRegisterView 回调
     */
    @Override
    public void executeRegisterTask(final JSONObject mJSONObject, final IRegisterView mIRegisterView) {
        M_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String result = sendPost(FinalString.URL, Codec.codec(mJSONObject.toString()));
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    String errorCode = jsonObject.getString(FinalString.ERROR_CODE);
                    if (errorCode.equals(ERROR_OK)) {
                        mIRegisterView.onRegisterSucceed(result);
                    } else {
                        mIRegisterView.onRegisterFailed(result);
                    }
                } catch (JSONException e) {
                    mIRegisterView.onRegisterFailed(result);
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 添加行李箱
     *
     * @param mJSONObject json参数
     * @param iAddBoxView 回调
     */
    @Override
    public void executeBindBoxTask(final JSONObject mJSONObject, final IAddBoxView iAddBoxView) {
        M_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String result = sendPost(FinalString.URL, Codec.codec(mJSONObject.toString()));
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    String errorCode = jsonObject.getString(FinalString.ERROR_CODE);
                    if (ERROR_OK.equals(errorCode)) {
                        iAddBoxView.onAddSucceed(errorCode);
                    } else {
                        iAddBoxView.onAddFailed(errorCode);
                    }
                } catch (JSONException e) {
                    iAddBoxView.onAddFailed(result);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取GPS位置
     *
     * @param mJSONObject   json参数
     * @param iLocationView 回调接口视图
     */
    @Override
    public void executeGetGpsTask(final JSONObject mJSONObject, final ILocationView iLocationView) {
        M_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String result = sendPost(FinalString.URL, Codec.codec(mJSONObject.toString()));
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    final String errorCode = jsonObject.getString(FinalString.ERROR_CODE);
                    final JSONObject gpsJson = jsonObject.getJSONObject(FinalString.GPS);
                    final String lat = gpsJson.getString("lat");
                    final String lng = gpsJson.getString("lng");
                    final String time = gpsJson.getString("time");
                    if ((ERROR_OK).equals(errorCode)) {
                        iLocationView.getGPSSucceed(result, lat, lng, time);
                    } else {
                        iLocationView.getGPSFailed(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 解绑操作
     *
     * @param mJSONObject json参数
     * @param iUnbindView 回调
     */
    @Override
    public void executeUnbindBoxTask(final JSONObject mJSONObject, final IUnbindView iUnbindView) {
        M_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String result = sendPost(FinalString.URL, Codec.codec(mJSONObject.toString()));
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    final String errorCode = jsonObject.getString(FinalString.ERROR_CODE);
                    if ((ERROR_OK).equals(errorCode)) {
                        iUnbindView.unbindSucceed(result);
                    } else {
                        iUnbindView.unbindFailed(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 要求终端上传盒子信息
     *
     * @param mJSONObject json参数
     */
    @Override
    public void executeReadOrSetBoxTask(final JSONObject mJSONObject) {
        M_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String result = sendPost(FinalString.URL, Codec.codec(mJSONObject.toString()));
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    String errorCode = jsonObject.getString(FinalString.ERROR_CODE);
//                    if (!(ERROR_OK).equals(errorCode)) {
//
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            String finalParam = new String(param.getBytes(), "utf-8");
            out.print(finalParam);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            System.out.println("result:===================" + "\n" + result);


        } catch (Exception e) {
            result = new StringBuilder("请求出现异常");
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }


}
