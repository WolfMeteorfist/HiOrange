package com.yuanshi.hiorange.util;

import android.os.Environment;

import java.io.File;

public class FileUtils {

    public static File getBoxInfoPath() {
        String fileName = "boxInfo.txt";
        return new File(getRootPath(), fileName);
    }


    /**
     * SD卡是否可用.
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else {
            return false;
        }
    }

    /**
     * 得到SD卡根目录.
     */
    public static File getRootPath() {
        File path = null;
        if (FileUtils.sdCardIsAvailable()) {
            // 取得sdcard文件路径
            path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        } else {
            path = new File(Environment.getDataDirectory().getAbsolutePath());
        }
        return path;
    }

    public static boolean deleteBoxInfoFile() {
        if (!getBoxInfoPath().exists()) {
            return true;
        }
        return getBoxInfoPath().delete();
    }

}
