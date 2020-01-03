package com.example.weahen.wstest.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weahen.wstest.Adapter.ContentListAdapter;
import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.Model.Content;
import com.example.weahen.wstest.Model.RefreshableView;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.NetworkUtility;
import com.example.weahen.wstest.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class MainActivity2 extends BaseActivity {

    RefreshableView refreshableView;
    private static final String TAG = MainActivity2.class.getSimpleName();
    private Button btnSend;
    private EditText inputMsg;
    private ListView listViewMessages;
    private Utils utils;
 String headImage;
    String content;
    String userId;
    private List<Content> listContent;
    private ContentListAdapter adapter2;


    private StompClient mStompClient;

    int id;
    String name,path;
    String uid;
    public static String NAME = "";          //连接WiFi名称
    public static String macAddress;     //连接WiFi的MAC

    private Timer timer;
    boolean isPicture;


    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String macNow = getNewMac();
                Log.e("10秒获取mac",macNow);

            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        btnSend = findViewById(R.id.btnSend);
        inputMsg = findViewById(R.id.inputMsg);
        utils = new Utils(getApplicationContext());

       listViewMessages = findViewById(R.id.list_view_messages);


        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bun");
        name = bundle.getString("name");
        path = bundle.getString("path");

        initTitleASK(name);

        String mac = getNewMac();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message timerMessage = new Message();
                timerMessage.what = 0;
                mHandler.sendMessage(timerMessage);
            }
        },0,10000);

        String MYIMEI;           //本机IMEI
        MYIMEI = NetworkUtility.getIMEI(MainActivity2.this);
        Log.e("这里是imei", MYIMEI);
        try {
//            uid =  md5(MYIMEI);
            uid = shaEncode(MYIMEI);
        } catch (Exception e) {
            e.printStackTrace();
        }



        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,"ws://39.106.39.49:8888/endpoint-websocket/websocket");
        String topicpath = "/chat"+ path;
        mStompClient.topic(topicpath).subscribe(topicMessage -> {
            Log.e("WSWS", topicMessage.getPayload());
            parseMessage(topicMessage.getPayload());


        });

        mStompClient.connect();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStompClient.send("/app/chatroom" + path, utils.getSendMessageJSON( mac,name,path,id,uid,inputMsg.getText().toString())).subscribe();
                Log.e("WSWS",  utils.getSendMessageJSON(mac,name,path,id,uid,inputMsg.getText().toString()));

                inputMsg.setText("");


            }
        });


        listContent  = new ArrayList<Content>();
        adapter2 = new ContentListAdapter(this, listContent);
        listViewMessages.setAdapter(adapter2);


        refreshableView = findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                refreshableView.finishRefreshing();

            }
        }, 0);


    };



    private void parseMessage(final String msg) {
        String RecevierUID;
        String content;
        String time;

        try {
            JSONObject jObj = new JSONObject(msg);

            content = jObj.getString("content");

            RecevierUID = jObj.getString("uid");

//            time = jObj.getString("time");

            boolean isSelf = true;
            isPicture = false;

            //检查聊天室是不是到时间了
            String time_is_up = "0";
            if(RecevierUID.equals(time_is_up)){
                Log.e("这里是=0",RecevierUID );
                showDialog();
                mStompClient.disconnect();

            }

                // 检查消息是不是有自己发出的
                if (! (uid.equals(RecevierUID))) {
                    isSelf = false;
                }

//           Content c = new Content(time,content,isSelf);
//            Content c = new Content(content,isSelf);
            Content c = new Content(RecevierUID.substring(0,10),content,isSelf,isPicture,headImage,userId);
            appendMessage(c);


        } catch (JSONException e) {
            e.printStackTrace();
        }




    }




    /**
     * 把消息放到listView里
     * */
    private void appendMessage ( final Content c){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listContent.add(c);
                adapter2.notifyDataSetChanged();
                playBeep();
            }
        });
    }



    private void showToast ( final String message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * 播放默认的通知声音
     * */
    public void playBeep () {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String md5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for(int i=0;i<bits.length;i++){
            int a = bits[i];
            if(a<0) a+=256;
            if(a<16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

    public static String increase(String value){
        int index=1;
        int n=Integer.parseInt(value.substring(index))+1;
        String newValue=String.valueOf(n);
        int len=value.length()- newValue.length() -index;

        for(int i=0;i<len;i++){
            newValue="0"+newValue;
        }
        return value.substring(0,index)+ newValue;
    }


    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String shaEncode(String inStr) throws Exception {

        MessageDigest sha = null;

        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {

            System.out.println(e.toString());

            e.printStackTrace();

            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {

            int val = ((int) md5Bytes[i]) & 0xff;

            if (val < 16) {

                hexValue.append("0");
            }

            hexValue.append(Integer.toHexString(val));

        }

        return hexValue.toString();

    }

    private void showDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity2.this);
                Log.e("这里是ws", "已经到达showDialog");
                builder.setTitle("温馨提示");
                builder.setMessage("聊天室到时间啦");
                builder.setPositiveButton("我知道了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mStompClient.disconnect();
                                Intent intent = new Intent(MainActivity2.this, RefreshViewActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog dialog=builder.create();
                dialog.show();

            }
        });

    }



    public String getNewMac() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission Not Granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            WifiManager wifimanage = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiinfo = wifimanage.getConnectionInfo();
            macAddress = wifiinfo.getBSSID();//获取当前连接网络的mac地址;
        }
        return macAddress;
    }


    //是否退出聊天室
    AlertDialog builder = null;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            builder = new AlertDialog.Builder(MainActivity2.this)
                    .setTitle("温馨提示：")
                    .setMessage("您确定要离开当前时空吗，您将无法接收到实时信息")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    MainActivity2.this.finish();
                                }
                            }).setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    builder.dismiss();
                                }
                            }).show();
        }
        return true;
    }



}
