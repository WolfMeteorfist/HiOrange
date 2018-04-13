package com.yuanshi.hiorange.util;

/**
 * Created by Administrator on 2018/2/6.
 */

public class FinalString {

    public static final String DB = "box_db";
    public static final String URL = "http://54.222.203.152:9090/plugins/SmartBox/sev";

    public static final String REGISTER = "register";
    public static final String USER = "user";
    public static final String ACT = "act";
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";
    public static final String VERIFICATIONCODE = "verificationCode";
    public static final String VALUE = "value";
    public static final String SIGN = "sign";
    public static final String ERROR_CODE = "error_code";
    public static final String ERROR_MSG = "error_msg";
    public static final String VERSION = "version";
    public static final String COMMAND = "command";
    public static final String GPS = "gps";
    public static final String RESULT = "result";
    public static final String TIME = "time";

    public static final String BOX_INFO = "box_info";
    public static final String BOX_ID = "box_id";
    public static final String MODE = "mode";
    public static final String WEIGHT = "weight";
    public static final String POWER = "power";
    public static final String IS_OPEN = "is_open";
    public static final String OBSTACLE_STATE = "obstacle_state";
    public static final String LOST_STATE = "lost_state";
    public static final String CONTROL_STATE = "control_state";
    public static final String FOLLOW_STATE = "follow_state";
    public static final String SHUTDOWN_STATE = "shutdown_state";
    public static final String START_STATE = "start_state";


    //Presenter类型
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_REGISTER = 2;
    public static final int TYPE_BIND_BOX = 3;
    public static final int TYPE_FORGET = 4;
    public static final int TYPE_GET_INFO = 5;
    public static final int TYPE_READ_BOX = 6;
    public static final int TYPE_GPS = 7;
    public static final int TYPE_UNBIND_BOX = 8;

    //当前申请类型
    public static final int READ_BOX = 0x01;
    public static final int SET_LOCK = 0x02;
    public static final int SET_UNLOCK = 0x03;
    public static final int SET_VOICE = 0x05;
    public static final int READ_VOICE = 0x06;
    public static final int FINGER_CANCEL = 0x07;
    public static final int FINGER_REGISTER = 0x08;
    public static final int FINGER_DELETE = 0x09;
    public static final int READ_FINGER = 0x11;
    public static final int BOX_MISS = 0X12;

}
