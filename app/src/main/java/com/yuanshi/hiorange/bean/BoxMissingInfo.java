package com.yuanshi.hiorange.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/7.
 */

public class BoxMissingInfo {

    private List<String> missTime;
    private List<String> openTime;

    public List<String> getMissTime() {
        return missTime == null ? new LinkedList<String>() : missTime;
    }

    public void setMissTime(List<String> missTime) {
        this.missTime = missTime;
    }

    public List<String> getOpenTime() {
        return openTime == null ? new LinkedList<String>() : missTime;

    }

    public void setOpenTime(List<String> openTime) {
        this.openTime = openTime;
    }


    public BoxMissingInfo() {
        missTime = new LinkedList<>();
        openTime = new LinkedList<>();
    }

}
