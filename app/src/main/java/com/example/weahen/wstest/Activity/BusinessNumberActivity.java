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

public class BusinessNumberActivity extends BaseActivity {

    Button take_number_button2;
    Button take_number_button4;
    Button take_number_button6;
    Button take_number_button8;
    Button take_number_button_more;
    String path;
    private StompClient mStompClient;
    int number2,number4,number6,number8,number_more;
    int type;


    private Handler handler1 = new Handler() {

        @Override

        public void handleMessage(Message msg) {

            if (msg.what == 300) {

                TextView textView_number2 = findViewById(R.id.number2);
                textView_number2.setText( number2 +  "");
                TextView textView_number4 = findViewById(R.id.number4);
                textView_number4.setText( number4 +  "");
                TextView textView_number6 = findViewById(R.id.number6);
                textView_number6.setText( number6 +  "");
                TextView textView_number8 = findViewById(R.id.number8);
                textView_number8.setText( number8 +  "");
                TextView textView_number_more = findViewById(R.id.number_more);
                textView_number_more.setText( number_more +  "");


            }

        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_number);


        initTitle("叫号");

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bun");
        path = bundle.getString("path");
        Log.e("这里是上个path",path);



        take_number_button2 = findViewById(R.id.take_number_button2);
        take_number_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("叫号成功！");
                type = 0;
                new os().start();

            }
        });
        take_number_button4 = findViewById(R.id.take_number_button4);
        take_number_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("叫号成功！");
                type = 1;
                new os().start();

            }
        });
        take_number_button6 = findViewById(R.id.take_number_button6);
        take_number_button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("叫号成功！");
                type = 2;
                new os().start();

            }
        });
        take_number_button8 = findViewById(R.id.take_number_button8);
        take_number_button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("叫号成功！");
                type = 3;
                new os().start();

            }
        });
        take_number_button_more = findViewById(R.id.take_number_button_more);
        take_number_button_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("叫号成功！");
                type = 10;
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



        @Override
        public void run() {
            super.run();


            try {
// 请求的地址
                String spec = " http://39.106.39.49:8888/processMultipleNumber";
// 根据地址创建URL对象
                URL url = new URL(spec);
// 根据URL对象打开链接
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
// 设置请求的方式
                urlConnection.setRequestMethod("POST");
// 设置请求的超时时间
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(5000);

                Log.e("这里是path",path);
                Log.e("这里是type",type+"");

// 传递的数据
                String data = "path=" + URLEncoder.encode(path, "UTF-8")
                        + "&type=" + URLEncoder.encode(type+"", "UTF-8");


// 设置请求的头
                urlConnection.setRequestProperty("Connection", "keep-alive");
// 设置请求的头
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
// 设置请求的头
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
// 设置请求的头
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
                urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
                urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
                // setDoInput的默认值就是true
                // 获取输出流
                OutputStream os = urlConnection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                if (urlConnection.getResponseCode() == 200) {
                    // 获取响应的输入流对象
                    InputStream is = urlConnection.getInputStream();
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
                    Log.e("这里是BusinessNumberResult", result);



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

            number2 = jObj.getInt("type_2");
            number4 = jObj.getInt("type_4");
            number6 = jObj.getInt("type_6");
            number8 = jObj.getInt("type_8");
            number_more = jObj.getInt("type_MORE");


            Message msg1 = new Message();
            msg1.what = 300;
            handler1.sendMessage(msg1);

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }


}
