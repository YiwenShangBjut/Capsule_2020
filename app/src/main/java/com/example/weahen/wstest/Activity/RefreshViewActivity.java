package com.example.weahen.wstest.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.weahen.wstest.Adapter.MyListAdapter;
import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Model.RefreshableView;
import com.example.weahen.wstest.Utils.SharedPrefsStrListUtil;
import com.example.weahen.wstest.widget.SetPermissionDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class RefreshViewActivity  extends BaseActivity {

    RefreshableView refreshableView;
    ListView lv;
    List<Map<String, String>> list;
    MyListAdapter aaAdapter;
    public static SharedPrefsStrListUtil spListUtils;
    String  room_PATH[] = new String[100];
    String room_NAME[] = new String[100];

    public static String NAME = "";          //连接WiFi名称
    public static String macAddress;     //连接WiFi的MAC


    String location;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 270) {
                String json = (String) msg.obj;

                if(json.length()==0){
                    showDialog();
                }

                try {

                    JSONArray been = new JSONArray(json);
                    for (int i = 0; i < been.length(); i++) {
                        JSONObject TopicListBean = been.getJSONObject(i);
                        room_NAME[i] = TopicListBean.getString("room_NAME");
                        room_PATH[i] = TopicListBean.getString("room_PATH");

                        Log.e("这里是room_NAME",  room_NAME[i]);

                        Map<String, String> map = new HashMap<>();
                        map.put("name", room_NAME[i]);
                        Log.e("这里是map.toString()", map.toString());
                        list.add(map);

                    }

                    aaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_refresh_view);


        Intent intentRece = getIntent();
        Bundle bundle = intentRece.getBundleExtra("bun");
        location = bundle.getString("location");


        initTitle("聊天室");

        lv =  findViewById(R.id.lv);
        list = new ArrayList<>();
        aaAdapter = new MyListAdapter(this, list);
        lv.setAdapter(aaAdapter);
        new os().start();

        refreshableView = findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                list.clear();

                new os().start();

                refreshableView.finishRefreshing();

            }
        }, 0);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Intent intent = new Intent(RefreshViewActivity.this, MainActivity2.class);

                Bundle bundle = new Bundle();
                bundle.putString("name", room_NAME[position]);
                bundle.putString("path", room_PATH[position]);
                intent.putExtra("bun", bundle);

                requestPermisson();

                startActivity(intent);


            }
        });




    }


    class os extends Thread {


      String urla = "http://39.106.39.49:8888/getUnitInfo";


        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL(urla);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);


                Log.e("这里是location", location );
                // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
                String content = "location=" + URLEncoder.encode(location, "utf-8");

                // 意思是正文是urlencoded编码过的form参数
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

                conn.setDoOutput(true); // 发送POST请求必须设置允许输出
                conn.setDoInput(true);
                conn.connect();

                OutputStream os = conn.getOutputStream();
                os.write(content.getBytes());
                os.flush();


                if (conn.getResponseCode() == 200) {
                    // 获取响应的输入流对象
                    InputStream is = conn.getInputStream();
                    // 创建字节输出流对象
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // 定义读取的长度
                    int len = 0;
                    //定义缓冲区
                    byte buffer[] = new byte[1024];
                    // 按照缓冲区的大小，循环读取
                    while ((len = is.read(buffer)) != -1) {
                        // 根据读取的长度写入到os对象中
                        baos.write(buffer, 0, len);
                    }                // 释放资源
                    is.close();
                    baos.close();
                    // 返回字符串
                    final String result = new String(baos.toByteArray());

                    Log.e("这里是relust", result);

                    Message message = new Message();
                    message.what = 270;
                    message.obj = result;
                    handler.sendMessage(message);


                } else {
                    Log.e("失败了","失败了");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }


    public String getNewMac() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission Not Granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.e("WWSWS", "这是getnewmac的if" );
        }else{
            WifiManager wifimanage = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiinfo = wifimanage.getConnectionInfo();
            macAddress = wifiinfo.getBSSID();//获取当前连接网络的mac地址;
            Log.e("WWSWS", "这是getnewmac的else" );

        }
        return macAddress;
    }

    private void showDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(RefreshViewActivity.this);
                Log.e("这里是ws", "已经到达showDialog");
                builder.setTitle("温馨提示");
                builder.setMessage("您当前所处的位置没有聊天室，请换个位置刷新一下");
                builder.setPositiveButton("确定", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    private void requestPermisson(){
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (! aBoolean) {
                            SetPermissionDialog mSetPermissionDialog = new SetPermissionDialog(RefreshViewActivity.this);
                            mSetPermissionDialog.show();
                            mSetPermissionDialog.setConfirmCancelListener(new SetPermissionDialog.OnConfirmCancelClickListener() {
                                @Override
                                public void onLeftClick() {

                                    finish();
                                }

                                @Override
                                public void onRightClick() {

                                    finish();
                                }
                            });
                        }
                    }
                });
    }

}

