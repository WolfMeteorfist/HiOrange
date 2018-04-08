package com.yuanshi.hiorange.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/4/7.
 */

public class BoxMissingInfo implements Parcelable {

    private String missTime;

    public BoxMissingInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.missTime);
    }


    protected BoxMissingInfo(Parcel in) {
        this.missTime = in.readString();
    }

    public static final Parcelable.Creator<BoxMissingInfo> CREATOR = new Creator<BoxMissingInfo>() {
        @Override
        public BoxMissingInfo createFromParcel(Parcel source) {
            return new BoxMissingInfo(source);
        }

        @Override
        public BoxMissingInfo[] newArray(int size) {
            return new BoxMissingInfo[size];
        }
    };
}
