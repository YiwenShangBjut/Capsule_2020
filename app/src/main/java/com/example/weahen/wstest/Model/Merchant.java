package com.example.weahen.wstest.Model;

import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;

/**
 * 若想传递对象集合不能使用Java的序列化（Serializable），只能使用Android的序列化
 * Java的序列化只能传递一个对象不能传递一个对象集合；但是Android的序列化既能传递对象也能传递对象集合
 */
public class Merchant implements Parcelable {


    private String path; //聊天室路径
    private String title; //商家店名
    private String picName; //商家图片名
    private String description; //商家描述

    public Merchant() {
    }

    public Merchant(String path, String title, String picName, String description) {
        this.path = path;
        this.title = title;
        this.picName = picName;
        this.description = description;
    }

    protected Merchant(Parcel in) {
        path = in.readString();
        title = in.readString();
        picName = in.readString();
        description = in.readString();
    }

    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", picName='" + picName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(picName);
        dest.writeString(description);
    }
}
