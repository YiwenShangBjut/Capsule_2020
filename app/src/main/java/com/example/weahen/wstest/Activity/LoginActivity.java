package com.example.weahen.wstest.Activity;


import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


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


public class LoginActivity extends BaseActivity {

  String name;
  String password;
  int id;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initTitle("登录");

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new os().start();

            }
        });
    }


    class os extends Thread {

        EditText logname = findViewById(R.id.logname);
        EditText password = findViewById(R.id.password);

        String id = logname.getText().toString().trim();
        String psw = password.getText().toString().trim();

        @Override
        public void run() {
            super.run();
            try {
// 请求的地址
                String spec = " http://39.106.39.49:8888/loginAPP";
// 根据地址创建URL对象
                URL url = new URL(spec);
// 根据URL对象打开链接
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
// 设置请求的方式
                urlConnection.setRequestMethod("POST");
// 设置请求的超时时间
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(5000);
// 传递的数据
                String data = "ID=" + URLEncoder.encode(id, "UTF-8")
                        + "&PSW=" + URLEncoder.encode(psw, "UTF-8");
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
                    Log.e("Result", result);

                    parseMessage(result);


                }else {
                    showDialog();
                }

            } catch (Exception e) {
                showDialog();
                e.printStackTrace();
            }


        }


    }

    private void showDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                Log.e("这里是ws", "已经到达showDialog");
                builder.setTitle("温馨提示");
                builder.setMessage("用户名或密码不正确");
                builder.setPositiveButton("重新输入", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    private void parseMessage(final String msg) {

        try {
            JSONObject jObj = new JSONObject(msg);

            id = jObj.getInt("id");
           name = jObj.getString("name");
           password = jObj.getString("password");
            Log.e("这里是name", name + "");
            Log.e("这里是password", password + "");

            if(id==0){
                showDialog();
            }else{
                Intent intent = new Intent(LoginActivity.this, GiveNumberActivity.class);
                startActivity(intent);

            }

        } catch (JSONException e) {
            showDialog();
            e.printStackTrace();
        }




    }



}
