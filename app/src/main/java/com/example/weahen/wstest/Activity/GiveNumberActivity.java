package com.example.weahen.wstest.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.weahen.wstest.Adapter.MyListAdapter;
import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.Model.RefreshableView;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.SharedPrefsStrListUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiveNumberActivity extends BaseActivity {



    RefreshableView refreshableView;
    ListView lv;
    List<Map<String, String>> list;
    MyListAdapter aaAdapter;
    public static SharedPrefsStrListUtil spListUtils;

    String  name[] = new String[100];
    String path[] = new String[100];
    int ID[] = new int[100];
    int RESERVE[] = new int[100];
    String PATH[] = new String[100];
    int RESERVE2[] = new int[100];
    int j=0,k=0;

    public static String NAME = "";          //连接WiFi名称
    public static String macAddress;     //连接WiFi的MAC


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 270) {
                String json = (String) msg.obj;
                try {

                    JSONArray been = new JSONArray(json);

                    for (int i = 0; i < been.length(); i++) {
                        JSONObject TopicListBean = been.getJSONObject(i);
                        Map<String, String> map = new HashMap<>();

                        name[i] = TopicListBean.getString("name");
                        path[i] = TopicListBean.getString("path");
                        ID[i] = TopicListBean.getInt("id");
                        RESERVE[i] = TopicListBean.getInt("reserve");

                        if( (RESERVE[i] == 1 ) || (RESERVE[i] == 2) ){
                            map.put("name", name[i]);

                                PATH[j] = path[i];
                                j=j+1;

                                RESERVE2[k] = RESERVE[i];
                                k=k+1;


                            Log.e("TakeNo map.toString()", map.toString());

                            list.add(map);


                        }



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
        setContentView(R.layout.activity_give_number);

        initTitle("可叫号窗口");

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
                Log.e("这里是RESERVE2【p】", RESERVE2[position]+"" );

                if(RESERVE2[position]==2) {
                    Intent intent = new Intent(GiveNumberActivity.this, BusinessNumberActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("path", PATH[position]);
                    intent.putExtra("bun", bundle);
                    startActivity(intent);
                }
                if(RESERVE2[position]==1) {
                    Intent intent = new Intent(GiveNumberActivity.this, BusinessNumber2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("path", PATH[position]);
                    intent.putExtra("bun", bundle);
                    startActivity(intent);
                }

            }
        });


    }


    class os extends Thread {

        Context context;
        String urla = "http://39.106.39.49:8888/getrooom";


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

                // 意思是正文是urlencoded编码过的form参数
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(5000);
                conn.connect();

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());

                macAddress = getNewMac();
                Log.e("WSWSWSWS", "COME HERE------------macAddress！！！！" + macAddress);

                // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
                String content = "mac=" + URLEncoder.encode(macAddress, "utf-8");
                // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
                out.writeBytes(content);        //流用完记得关
                out.flush();
                out.close();

                if (conn.getResponseCode() == 200) {
                    InputStream is = conn.getInputStream();

                    byte[] bytes = new byte[1024];
                    int i = 0;
                    StringBuffer sb = new StringBuffer();

                    while ((i = is.read(bytes)) != -1) {
                        sb.append(new String(bytes, 0, i));
                    }

                    Log.e("这里是sb.tostring",sb.toString());

                    Message message = new Message();
                    message.what = 270;
                    message.obj = sb.toString();

                    handler.sendMessage(message);
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



}
