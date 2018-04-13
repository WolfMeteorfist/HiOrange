package com.yuanshi.hiorange.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BoxInfo implements Parcelable {

    private String weight;
    private String lifeTime;
    private String isLocked;
    private String isOpened;
    private String percent;

    public BoxInfo(String weight, String lifeTime, String isLocked, String isOpened, String percent) {
        this.weight = weight;
        this.lifeTime = lifeTime;
        this.isLocked = isLocked;
        this.isOpened = isOpened;
        this.percent = percent;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(String lifeTime) {
        this.lifeTime = lifeTime;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getIsOpened() {
        return isOpened;
    }

    public void setIsOpened(String isOpened) {
        this.isOpened = isOpened;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.weight);
        dest.writeString(this.lifeTime);
        dest.writeString(this.isLocked);
        dest.writeString(this.isOpened);
        dest.writeString(this.percent);
    }

    protected BoxInfo(Parcel in) {
        this.weight = in.readString();
        this.lifeTime = in.readString();
        this.isLocked = in.readString();
        this.isOpened = in.readString();
        this.percent = in.readString();
    }

    public static final Creator<BoxInfo> CREATOR = new Creator<BoxInfo>() {
        @Override
        public BoxInfo createFromParcel(Parcel source) {
            return new BoxInfo(source);
        }

        @Override
        public BoxInfo[] newArray(int size) {
            return new BoxInfo[size];
        }
    };
}
