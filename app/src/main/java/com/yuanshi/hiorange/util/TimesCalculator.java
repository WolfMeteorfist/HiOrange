package com.yuanshi.hiorange.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/2.
 */

public class TimesCalculator {

    public static long calculateSeconds(String afDate, String preDate) {
        DateFormat formart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {

            Date date1 = formart.parse(afDate);

            Date date2 = formart.parse(preDate);

            long l = date1.getTime() - date2.getTime();
            return l;//这样得到的差值是秒级别

//            long days = times / (1000 * 60 * 60 * 24); //换算成天数
//
//            long hours = (times - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60); //换算成小时
//
//            long minutes = (times - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60); //换算成分钟

//根据自己所需要的进行换算，一定要注意日期格式。

        } catch (Exception e) {

        }
        return 0;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

}

