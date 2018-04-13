package com.yuanshi.hiorange.bean;

/**
 * Created by Administrator on 2018/4/4.
 */

public class BoxCommand {


    /**
     * error_code : 0
     * error_msg : ok
     * command : xxxxxxxx
     */

    private String error_code;
    private String error_msg;
    private String command;
    private String time;

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
