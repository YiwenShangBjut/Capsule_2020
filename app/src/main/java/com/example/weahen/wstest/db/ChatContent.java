package com.example.weahen.wstest.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ChatContent {
    private String  content;
    private String time;
    private boolean isSelf;
    private  String uid;
    private String picture;
    private boolean isPicture;
    private String chatRoom;
    private String headImage;

    @Generated(hash = 1218387270)
    public ChatContent(String content, String time, boolean isSelf, String uid,
            String picture, boolean isPicture, String chatRoom, String headImage) {
        this.content = content;
        this.time = time;
        this.isSelf = isSelf;
        this.uid = uid;
        this.picture = picture;
        this.isPicture = isPicture;
        this.chatRoom = chatRoom;
        this.headImage = headImage;
    }

    @Generated(hash = 1788514932)
    public ChatContent() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isPicture() {
        return isPicture;
    }

    public void setPicture(boolean picture) {
        isPicture = picture;
    }

    public boolean getIsSelf() {
        return this.isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public boolean getIsPicture() {
        return this.isPicture;
    }

    public void setIsPicture(boolean isPicture) {
        this.isPicture = isPicture;
    }

    public String getChatRoom() {
        return this.chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public String getHeadImage() {
        return this.headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }
}
