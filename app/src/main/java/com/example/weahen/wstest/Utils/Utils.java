package com.example.weahen.wstest.Utils;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static android.content.Context.WIFI_SERVICE;

public class Utils {

    private Context context;
    private SharedPreferences sharedPref;

    private static final String KEY_SHARED_PREF = "ANDROID_WEB_CHAT";
    private static final int KEY_MODE_PRIVATE = 0;
    private static final String KEY_UID = "sessionId", FLAG_MESSAGE = "message";

    public Utils(Context context) {
        this.context = context;
        sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF, KEY_MODE_PRIVATE);
    }

    public void storeUID(String UID) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_UID, UID);
        editor.commit();
    }

    public String getUID() {
        return sharedPref.getString(KEY_UID, null);
    }


    public String getTime() {
        long currentTime = System.currentTimeMillis();
        String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
        return timeNow;
    }





    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String getSendMessageJSON(String time,String macAddress,String name, String path, int id, String uid, String mid, String content) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();

            jObj.put("path", path);
            jObj.put("id", id);
            jObj.put("name", name);
            jObj.put("uid", uid);
            jObj.put("mid", mid);
            jObj.put("content", content);
            jObj.put("time", getTime());
            jObj.put("mac", macAddress);


            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

}