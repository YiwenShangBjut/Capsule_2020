package com.example.weahen.wstest.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class BusinessNumber2Activity extends BaseActivity {

    Button giveNumberButton;
    String path;
    private StompClient mStompClient;
    int number;

    private Handler handler1 = new Handler() {

        @Override

        public void handleMessage(Message msg) {

            if (msg.what == 300) {

                TextView numberText = findViewById(R.id.number);

                numberText.setText( number +  "");

            }

        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_number2);


        initTitle("叫号");

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bun");
        path = bundle.getString("path");


        giveNumberButton = findViewById(R.id.take_number_button);
        giveNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToast("叫号成功！");
                new os().start();


            }
        });

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,"ws://39.106.39.49:8888/endpoint-websocket/websocket");
        String topicpath = "/chat"+ path;
        mStompClient.topic(topicpath).subscribe(topicMessage -> {
            Log.e("这里是getPayload", topicMessage.getPayload());
            parseMessage(topicMessage.getPayload());


        });
        mStompClient.connect();

    }

    class os extends Thread {

        String urla = "http://39.106.39.49:8888/processNumber";

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


                Log.e("这里是path", path );
                // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
                String content = "path=" + URLEncoder.encode(path, "utf-8");

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
                    Log.e("MY NO Result",result);


                } else {
                    Log.e("失败了","失败了");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void showToast ( final String message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

    }


    private void parseMessage(final String msg) {

        try {
            JSONObject jObj = new JSONObject(msg);

            number = jObj.getInt("current_No");

            Log.e("这里是currentNo", number + "hh");


            Message msg1 = new Message();

            msg1.what = 300;

            handler1.sendMessage(msg1);

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }


}
