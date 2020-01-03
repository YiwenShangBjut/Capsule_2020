package com.example.weahen.wstest.Activity;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MyNumberActivity extends BaseActivity {

    Button takeNumberButton;
    String path;
    int reserve;
    int number_now;
    int number_mine;
    int number_total;
    int type,type_2,type_4,type_6,type_8,type_more;
    private StompClient mStompClient;
    private boolean isoncl=true;
    int individule;


//    private Handler handler1 = new Handler() {
//
//        @Override
//
//        public void handleMessage(Message msg) {
//
//            if (msg.what == 300) {
//
//                TextView total = findViewById(R.id.number_total);
//
//                total.setText(number_total +  "");
//
//            }
//
//        }
//
//    };



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 270) {
                String json = (String) msg.obj;
                try {

                    JSONObject jsonObject = new JSONObject(json);
                    TextView now = findViewById(R.id.number_now);
                    TextView mine = findViewById(R.id.number_mine);


                    if(reserve == 1) {
                        number_now = jsonObject.getInt("current_No");
                        number_mine = jsonObject.getInt("total_No");
                        Log.e("这里是number_now", number_now + "");
                        Log.e("这里是number_mine", number_mine + "");
                        now.setText(number_now + "");
                        mine.setText(number_mine + "");
                    }
                    if(reserve == 2) {

                        type_2 = jsonObject.getInt("type_2");
                        type_4 = jsonObject.getInt("type_4");
                        type_6 = jsonObject.getInt("type_6");
                        type_8 = jsonObject.getInt("type_8");
                        type_more = jsonObject.getInt("type_MORE");

                        now.setText(type + "");

                        if(individule<=2) {
                            mine.setText(type_2 + "");
                        }
                        if((individule>2)&&(individule<=4)) {
                            mine.setText(type_4 + "");
                        }
                        if((individule>4)&&(individule<=6)) {
                            mine.setText(type_6 + "");
                        }
                        if((individule>6)&&(individule<=8)) {
                            mine.setText(type_8 + "");
                        }
                        if(individule>8) {
                            mine.setText(type_more + "");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_number);
        initTitle("取号");

        //从functionActivity传过来的值
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bun");
        path = bundle.getString("path");
        reserve = bundle.getInt("reserve");


        takeNumberButton = findViewById(R.id.take_number_button);
        takeNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isoncl){


                    if(reserve==1){

                        new os_reserve1().start();
                    }

                    if(reserve==2){
                        new os_reserve2().start();
                    }

//                    isoncl=false; //点击一次后就改成false，这样就实现只点击一次了
                }


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

    class os_reserve1 extends Thread {

        String urla = "http://39.106.39.49:8888/getNumber";

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

                String content = "path=" + URLEncoder.encode(path, "utf-8");

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

    class os_reserve2 extends Thread {


        EditText indi = findViewById(R.id.individule);
        String individule_string = indi.getText().toString().trim();




        @Override
        public void run() {
            super.run();

            try{
                individule = Integer.parseInt(individule_string);
            }catch (Exception e) {
                e.printStackTrace();
                showToast("取号失败，请输入正确的用餐人数");
            }

            try {
// 请求的地址
                String spec = " http://39.106.39.49:8888/getNumber_Multiple";
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
                Log.e("这里是individule",individule+"");

// 传递的数据
                String data = "path=" + URLEncoder.encode(path, "UTF-8")
                        + "&individule=" + URLEncoder.encode(individule+"", "UTF-8");


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
                    Log.e("这里是Result", result);

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

            number_total = jObj.getInt("total_No");

            Log.e("这里是number_total", number_total + "");


            Message msg1 = new Message();

            msg1.what = 300;

//            handler1.sendMessage(msg1);

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }



}
