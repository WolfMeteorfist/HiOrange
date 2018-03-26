package com.yuanshi.hiorange.fragment;

/**
 *
 * @author Zyc
 * @date 2018/3/1
 */

public interface IBoxView {

    /**
     * @param result 服务器返回数据 {"error_code":"0", "error_msg":"ok","time":"2017-01-01 01:01:01"}
     */
    void onReadSucceed(String result);

    /**
     * @param result 服务器返回数据 {"error_code":"1", "error_msg":"???","time":"2017-01-01 01:01:01"}
     */
    void onReadFailed(String result);

}
