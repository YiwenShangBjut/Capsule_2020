package com.example.weahen.wstest.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 三级缓存之网络缓存
 */
public class NetCacheUtils {

    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private Bitmap bitmap;

    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    /**
     * 从网络下载图片
     *
     * @param url 下载图片的网络地址
     */
    public Bitmap getBitmapFromNet(String url) {
        bitmap = HttpUtils.decodeUriAsBitmapFromNet(url);
        setCache(url);
        return this.bitmap;
    }

    // 设置内存和本地缓存
    private void setCache(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
                mLocalCacheUtils.setBitmapToLocal(url, bitmap);

            }
        }).start();
    }
}
