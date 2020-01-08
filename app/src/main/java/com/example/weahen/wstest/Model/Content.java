package com.example.weahen.wstest.Model;


import android.graphics.Bitmap;
import android.media.Image;

import java.text.SimpleDateFormat;

public class Content {
    private String  content;
    private String shaCode;
    private boolean isSelf;
    private  String uid;
    private Bitmap picture;
    private boolean isPicture;
private String headImage;
private String userName;
private String time;
    public Content() {
    }


//    public Content( String  time,String content,boolean isSelf) {
//        this.time = time;
//        this.content = content;
//        this.isSelf = isSelf;
//    }

//    public Content( String content,boolean isSelf) {
//
//        this.content = content;
//        this.isSelf = isSelf;
//    }

    public Content( String time,String uid,String content,boolean isSelf,boolean isPicture,String headImage,String userName) {
        this.uid = uid;
        this.content = content;
        this.isSelf = isSelf;
        this.isPicture = isPicture;
        this.headImage=headImage;
        this.userName=userName;
        this.time = time;
    }

    public Content( String time,String uid,Bitmap picture,boolean isSelf,boolean isPicture,String headImage,String userName) {
        this.uid = uid;
        this.picture = picture;
        this.isSelf = isSelf;
        this.isPicture = isPicture;
        this.headImage=headImage;
        this.userName=userName;
        this.time = time;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getHeadImage() {
        return headImage;
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

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public String getShaCode() {
        return shaCode;
    }

    public void setShaCode(String time) {
        this.shaCode = time;
    }

   public String getUid(){
        return  uid;
   }
   public void setUid(String uid){
        this.uid = uid;
    }


    public boolean isPicture() {
        return isPicture;
    }
    public void setPicture(boolean picture) {
        isPicture = picture;
    }

}


