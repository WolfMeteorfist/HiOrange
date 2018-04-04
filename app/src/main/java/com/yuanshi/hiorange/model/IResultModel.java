package com.yuanshi.hiorange.model;

import android.content.Context;

import com.yuanshi.hiorange.activity.IAddBoxView;
import com.yuanshi.hiorange.activity.ILoginView;
import com.yuanshi.hiorange.activity.IRegisterView;
import com.yuanshi.hiorange.activity.IUnbindView;
import com.yuanshi.hiorange.fragment.ILocationView;

import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ZYc
 * @date 2018/2/6
 */

public interface IResultModel {


    /**
     * 设置核心池大小
     */
    int corePoolSize = 5;


    //设置线程池最大能接受多少线程


    /**
     * 当前线程数大于corePoolSize、小于maximumPoolSize时，超出corePoolSize的线程数的生命周期
     */

    long KEEP_ACTIVE_TIME = 200;

    /**
     * 设置时间单位，秒
     */

    TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 设置线程池缓存队列的排队策略为FIFO，并且指定缓存队列大小为5
     */
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(5);
    /**
     * 创建ThreadPoolExecutor线程池对象，并初始化该对象的各种参数
     */
    ThreadPoolExecutor M_POOL_EXECUTOR = new ThreadPoolExecutor(corePoolSize, 5, KEEP_ACTIVE_TIME, TIME_UNIT, workQueue);


    /**
     * Login界面
     *
     * @param mJSONObject 传入参数
     * @param mIMainView  回调视图
     */
    void executeLoginTask(JSONObject mJSONObject, ILoginView mIMainView);

    void executeGetInfoTask(Context mContext, JSONObject mJSONObject, Object mObjectView);

    void executeRegisterTask(JSONObject mJSONObject, IRegisterView mIRegisterView);

    void executeReadOrSetBoxTask(JSONObject mJSONObject);

    void executeBindBoxTask(JSONObject mJSONObject, IAddBoxView iAddBoxView);

    void executeGetGpsTask(JSONObject mJSONObject, ILocationView iLocationView);

    void executeUnbindBoxTask(JSONObject mJSONObject, IUnbindView iUnbindView);

    void excuteGetInfoAuto(JSONObject mJSONObject, Object mObjectView);


}
