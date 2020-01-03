package com.example.weahen.wstest.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.weahen.wstest.Config.GlobalConfig;
import com.example.weahen.wstest.Config.URLConfig;
import com.example.weahen.wstest.Model.Tidings;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.AppUtils;
import com.example.weahen.wstest.Utils.NetUtils;
import com.example.weahen.wstest.Utils.NetworkUtility;
import com.example.weahen.wstest.Utils.ScreenUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import okhttp3.internal.Version;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.example.weahen.wstest.Utils.FastJsonTools.createJsonBean;

public class WelcomeActivity extends AppCompatActivity {
    private static final long DELAY_TIME =500L;
    LinearLayout layout;
    private ScreenUtils utils;

    String result;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 270) {
                showDialog_new();
            }
        }
    };

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 270) {
                //showDialog();
                redirectByTime();
            }
        }
    };

    //TODO 要换掉欢迎背景图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        layout = findViewById(R.id.welcome);
        utils = new ScreenUtils(this);
        AlphaAnimation Animation = new AlphaAnimation(0.1f, 1f);
        Animation.setDuration(1000);
        Animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {

                switch (NetworkUtility.getNetworkType(getBaseContext())) {

                    default:
//                        //如果没有链接wifi的情况下就会去打开wifi
//                        if (!NetworkUtility.WifiState(getBaseContext())) {
//                            utils.createConfirmDialog("提示", "请授予开启wifi权限 并链接上wifi 保证软件正常使用", "确定",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
////                                            NetworkUtility.OpenWifi(getBaseContext());
//                                            dialog.dismiss();
//                                            redirectByTime();
//
//
//                                        }
//                                    });
//                        }
//
//
//                        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//                        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
//                        redirectByTime();
//                        }


                }
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
                // TODO 自动生成的方法存

            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {


            }

        });
        layout.setAnimation(Animation);

       new check_version().start();


    }

    private void redirectByTime() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, SlideActivity.class));
                finish();
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

            }
        }, DELAY_TIME);
    }

    @Override
    protected void onStart() {

        HiPermission.create(WelcomeActivity.this).checkSinglePermission(Manifest.permission.READ_PHONE_STATE, new PermissionCallback() {
            @Override
            public void onClose() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {

            }

            @Override
            public void onGuarantee(String permission, int position) {

            }
        });
        super.onStart();
    }


    class check_version extends Thread {

        @Override
        public void run() {
            super.run();
            try {

                String spec = "http://39.106.39.49:8888/check_version";
                URL url = new URL(spec);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(5000);

                String data = "versioncode=" + URLEncoder.encode(String.valueOf(AppUtils.getAppVersionCode(getBaseContext())), "UTF-8");

                Log.e("更新",data);

                urlConnection.setRequestProperty("Connection", "keep-alive");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                OutputStream os = urlConnection.getOutputStream();
                os.write(data.getBytes());
                os.flush();

                if (urlConnection.getResponseCode() == 200) {
                    InputStream is = urlConnection.getInputStream();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    is.close();
                    baos.close();

                    result = new String(baos.toByteArray());
                    Log.e("检查版本方法返回的Result", result);

                    if (result.contains("new")) {
                        Message message = new Message();
                        message.what = 270;
                        message.obj = result;
                        handler.sendMessage(message);
                    }else{
                        Message message = new Message();
                        message.what = 270;
                        message.obj = result;
                        handler1.sendMessage(message);
                    }


                }else {
                    Log.e("检查版本的Result响应码", urlConnection.getResponseCode()+"");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }
    public void showDialog_new() {
        new AlertDialog.Builder(WelcomeActivity.this)
                .setTitle("发现新版本")
                .setCancelable(false)//在弹框弹出来的时触发其他地方不让他消失
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (NetUtils.isWifi(WelcomeActivity.this)) {
                            //wifi下载
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("http://www.microsk.com:8888/downloadapp"));
                            WelcomeActivity.this.startActivity(intent);
                            dialog.dismiss();

                        } else {
                            utils.createConfirmDialog(
                                    0,
                                    "提示：", "当前处于非wifi状态下是否继续下载？",
                                    "确定继续下载", "下次再说"
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //下载
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("http://www.microsk.com:8888/downloadapp"));
                                            WelcomeActivity.this.startActivity(intent);
                                            dialog.dismiss();

                                        }
                                    }, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            //当点击了下次再说的时候就可以允许继续跳转
                                            redirectByTime();
                                        }
                                    });

                        }
                    }
                })
                .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //当点击了以后再说的时候就可以允许继续跳转
                        redirectByTime();
                    }
                })
                .show();

    }
    public void showDialog() {
        utils.createConfirmDialog("提示：", "目前已经是最新版本\n",
                "好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //当目前已经是最新版本可以允许继续跳转
                        redirectByTime();
                    }
                });
    }

}
