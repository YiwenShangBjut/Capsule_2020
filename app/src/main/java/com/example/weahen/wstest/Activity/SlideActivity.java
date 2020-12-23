package com.example.weahen.wstest.Activity;

import static com.example.weahen.wstest.db.DBContract.ChatEntry.TABLE_NAME_CHAT;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_ENDTIME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_STARTTIME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.TABLE_NAME_ROOM;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_FIELD;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_LOCATION;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_NAME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_PATH;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_RESRTVE;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.RID;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_ID;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.weahen.wstest.Adapter.MyListAdapter;
import com.example.weahen.wstest.Config.GlobalConfig;
import com.example.weahen.wstest.Config.URLConfig;
import com.example.weahen.wstest.Model.ChatRoom;
import com.example.weahen.wstest.Model.RefreshableView;
import com.example.weahen.wstest.Model.Tidings;
import com.example.weahen.wstest.MyApplication;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.AppUtils;
import com.example.weahen.wstest.Utils.NetUtils;
import com.example.weahen.wstest.Utils.ScreenUtils;
import com.example.weahen.wstest.Utils.SharedPrefsStrListUtil;
import com.example.weahen.wstest.db.ChatContent;
import com.example.weahen.wstest.db.ChatContentDao;
import com.example.weahen.wstest.db.MyDbOpenHelper;

import android.content.Intent;

import android.support.v4.view.GravityCompat;

import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xutils.x;
import okhttp3.internal.Version;

import static com.example.weahen.wstest.Utils.FastJsonTools.createJsonBean;


public class SlideActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    RefreshableView refreshableView;
    ListView lv;
    List<Map<String, String>> list;
    MyListAdapter aaAdapter;
    public static SharedPrefsStrListUtil spListUtils;
    String  name[] = new String[100];
    String path[] = new String[100];
    String location[] = new String[100];
    int ID[] = new int[100];
    int RESERVE[] = new int[100];
    String FIELD[] = new String[100];

    String getSSID;
    String getBSSID;

    String startTime;
    String endTime;

    ArrayAdapter<ChatRoom> adapter;

    private ScreenUtils utils;
    public static String NAME = "";          //连接WiFi名称
    public static String macAddress;     //连接WiFi的MAC

    private static ListView lvContacts;
    private ChatContent clickedRoom;
    private ChatContentDao chatContentDao;
    private  List<ChatContent> chatContents;

    private MyDbOpenHelper myDbHelper;
    ArrayList<ChatRoom> chatRoomList;
//    ArrayList<String> roomNameList;
//    ArrayList<String> roomIdList;
    private AlertDialog historyDialog;


//    Handler hisHandler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if(msg.what==0){
//                int position=(int)msg.obj;
//                myDbHelper.deleteRoomFromTable(chatRoomList.get(position).getRoomId());
//                chatRoomList.remove(position);
//                adapter.notifyDataSetChanged();
//                Toast.makeText(SlideActivity.this, "已删除聊天室", Toast.LENGTH_LONG).show();
//            }
//        }
//    };

      //解析服务器返回的json
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 270) {
                String json = (String) msg.obj;
                Log.e("这里是json",json);
                Log.e("json.length",json.length()+"");

                if(json.length()==0){
                    showDialog();//服务器返回的列表为空，弹窗提示用户换个位置重新刷新
                }

                try {
                    SQLiteDatabase dbw = myDbHelper.getWritableDatabase();
                    JSONArray been = new JSONArray(json);
                    for (int i = 0; i < been.length(); i++) {
                        JSONObject TopicListBean = been.getJSONObject(i);
                        name[i] = TopicListBean.getString("name");
                        path[i] = TopicListBean.getString("path");
                        ID[i] = TopicListBean.getInt("id");
                        RESERVE[i] = TopicListBean.getInt("reserve");
                        FIELD[i] = TopicListBean.getString("field");
                        location[i] = TopicListBean.getString("location");
                        startTime= TopicListBean.getString("start_TIME");
                        endTime= TopicListBean.getString("end_TIME");

                        Map<String, String> map = new HashMap<>();
                        map.put("name", name[i]); //把解析出来的名字放到map里
                        Log.e("这里是map.toString()", map.toString());
                        Log.e("这里是map.size", map.size()+"");
                        list.add(map);  //显示map

                        if(!ifRoomExist(String.valueOf(ID[i]))){
                            Log.e("IfRoomExist","Room is not exist");
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(COLUMN_NAME_NAME, name[i]);
                            contentValues.put(COLUMN_NAME_PATH, path[i]);
                            contentValues.put(COLUMN_NAME_ID, String.valueOf(ID[i]));
                            contentValues.put(COLUMN_NAME_RESRTVE, String.valueOf(RESERVE[i]));
                            contentValues.put(COLUMN_NAME_FIELD, FIELD[i]);
                            contentValues.put(COLUMN_NAME_LOCATION, location[i]);
                            contentValues.put(COLUMN_NAME_STARTTIME, "");
                            contentValues.put(COLUMN_NAME_ENDTIME, "");

                            myDbHelper.insertRoomData(contentValues, dbw);
                            getRoomList();
                        }

                    }

                    aaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
    };

    private boolean ifRoomExist(String id){
        for(int i=0;i<chatRoomList.size();i++){
            if(id.equals(chatRoomList.get(i).getRoomId())){
                Log.e("IfRoomExist","Room exist, room id is "+chatRoomList.get(i).getRoomId());
                return true;
            }
        }
//        for(String i:chatRoomList.get(i).getRoomId()){
//            if(id.equals(i)){
//                Log.e("IfRoomExist","Room exist");
//                return true;
//            }
//        }
        Log.e("IfRoomExist","Room is not exist, room id is "+id);
        return false;
    }

    public void onButtonClick(View view){
        Intent intent = new Intent(SlideActivity.this, LoginActivity.class);

        startActivity(intent);

    }

    public void outputChatList(){
        for(ChatContent c:chatContents){
            Log.e("ChatContent",c.getChatRoom()+" "+c.getContent()+" "+c.getTime()+" "+c.getUid());
        }
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slide);

        myDbHelper = MyDbOpenHelper.getInstance(this);


        getRoomList();
        //创建toolbar工具栏

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        //获得抽屉布局

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //在布局文件中生命DrawerLayout后，即可从边缘滑出抽屉了

        //ActionBarDrawerToggle作用是在toolbar上创建一个点击弹出drawer的按钮而已

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        //不写这句话，是没有按钮显示的

        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

                list.clear(); //清空列表

                new os().start();  //刷新列表，重新获取

                refreshableView.finishRefreshing();  //结束下拉操作

            }
        }, 0);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               ArrayList<String> pic_list = new ArrayList<>();
                Intent intent = new Intent(SlideActivity.this, FunctionActivity.class);
                //给functionActivity传值，传点击的聊天室的信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        getImg(position,pic_list);

                    }
                }).start();

                Bundle bundle = new Bundle();
                bundle.putInt("reserve", RESERVE[position]);
                bundle.putString("path", path[position]);
                bundle.putString("field", FIELD[position]);
                bundle.putInt("ID", ID[position]);
                bundle.putString("location", location[position]);
                bundle.putString("name", name[position]);
                bundle.putStringArrayList("pic_list",pic_list);



                intent.putExtra("bun", bundle);

                startActivity(intent);

            }
        });
        //chatContentDao= MyApplication.getInstances().getDaoSession().getChatContentDao();
       // chatContents = chatContentDao.loadAll();

//        getRoomList();


        }
        //从接口获取商家图片信息。url：/getRoom_ADList
    String getMacresult = "";
    public  ArrayList<String> getImg(int position,ArrayList<String>res){
        //ArrayList<String> res = new ArrayList<>();
        String murl="http://39.106.39.49:8888/getRoom_ADList";
        try{
            URL url = new URL(murl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            String content = "path=" + URLEncoder.encode(path[position], "utf-8");

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
                getMacresult = new String(baos.toByteArray());
                Log.e("MY Result", getMacresult);
                //JSONObject jObj = new JSONObject(getMacresult);
                JSONArray job = new JSONArray(getMacresult);
                for(int i=0;i<job.length();++i)
                {
                    JSONObject temp = job.getJSONObject(i);
                    res.add(temp.getString("pic_name"));
                }


            } else {
                Log.e("失败了", "失败了");
            }
        }catch(Exception e){

            e.printStackTrace();
        }
        return res;
    }
    @Override

    public void onBackPressed() {


        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();

        }

    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        return true;

    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent intent = new Intent(SlideActivity.this, PersonalizedActivity.class);

            startActivity(intent);

        }else if(id==R.id.action_settings_history){
            showHistoryDialog();
        }

        return super.onOptionsItemSelected(item);

    }
    public void getRoomList(){
        Log.e("getRoomList","enter get room list");
        chatRoomList=new ArrayList<>();
        chatRoomList.clear();
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME_ROOM,null,null,null,null,null,null);

        while(cursor.moveToNext()){
            chatRoomList.add(new ChatRoom(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID)),cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NAME)),cursor.getString(cursor.getColumnIndex(COLUMN_NAME_STARTTIME)),cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ENDTIME))));
        }
        cursor.close();
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SlideActivity.this);
        builder.setTitle("删除此聊天室");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              //  myDbHelper.deleteRoomFromTable(chatRoomList.get(position).getRoomId());
                deleteHistory(position);
//                chatRoomList.remove(position);
//                adapter.notifyDataSetChanged();
//                Toast.makeText(SlideActivity.this, "已删除聊天室", Toast.LENGTH_LONG).show();

//                Message message = new Message();
//                message.what = 0;
//                message.obj =position;
//                hisHandler.sendMessage(message);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    private void deleteHistory(final int position) {
        new Thread(new Runnable(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myDbHelper.deleteRoomFromTable(chatRoomList.get(position).getRoomId());
                        chatRoomList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(SlideActivity.this, "已删除聊天室", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }


    public void showHistoryDialog(){
        View view=View.inflate(SlideActivity.this, R.layout.history_listview, null);
        lvContacts = (ListView) view.findViewById(R.id.history_contacts);

        adapter = new ArrayAdapter<ChatRoom>(this, R.layout.history_listview, chatRoomList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ChatRoom selectedRoom=getItem(position);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.history_list_item, parent, false);
                TextView roomName=view.findViewById(R.id.room_name);
                TextView startTime=view.findViewById(R.id.startTime);
                TextView endTime=view.findViewById(R.id.endTime);
                roomName.setText(selectedRoom.getRoomName());
                startTime.setText(selectedRoom.getStartTime());
                Log.e("SlideActivity","startTime is "+selectedRoom.getStartTime()+"-----------------------------");
                endTime.setText(selectedRoom.getEndTime());
                return view;
            }
        };
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String checkedRoomId=chatRoomList.get(position).getRoomId();
                String checkedRoomName=chatRoomList.get(position).getRoomName();
                Intent intent=new Intent(SlideActivity.this,HistoryActivity.class);
                intent.putExtra("id", checkedRoomId);
                intent.putExtra("roomName", checkedRoomName);
                historyDialog.dismiss();
                startActivity(intent);
               // clickedRoom=chatContents.get(position);
            }
        });

        lvContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });

        if(historyDialog==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(SlideActivity.this);
            builder.setView(view);
            historyDialog=builder.create();
            historyDialog.show();
        }else{
            historyDialog.show();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override

    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.

        int id = item.getItemId();


        if (id == R.id.introduce) {

            Intent intent = new Intent(SlideActivity.this, AboutActivity.class);

            startActivity(intent);

        } else if (id == R.id.service) {

            Intent intent = new Intent(SlideActivity.this, FeedbackActivity.class);

            startActivity(intent);


        } else if (id == R.id.checkupdate) {

           showDialog_restart();


        } else if (id == R.id.set) {

            Intent intent = new Intent(SlideActivity.this, SettingActivity.class);

            startActivity(intent);


        } else if (id == R.id.QRCode) {

            Intent intent = new Intent(SlideActivity.this, QRCodeActivity.class);

            startActivity(intent);

        } else if (id == R.id.nav_send) {


            Uri uri = Uri.parse("https://dl.reg.163.com/ydzj/maildl?product=mail163&pdconf=yddl_mail163_conf&mc=0F6099&curl=https%3A%2F%2Fmail.163.com%2Fentry%2Fcgi%2Fntesdoor%3Ffrom%3Dsmart%26language%3D0%26style%3D11%26destip%3D192.168.193.48%26df%3Dsmart_android\n");//要跳转的网址
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);


        }


        drawer.closeDrawer(GravityCompat.START);

        return true;

    }


     //是否退出程序
    AlertDialog builder = null;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            builder = new AlertDialog.Builder(SlideActivity.this)
                    .setTitle("温馨提示：")
                    .setMessage("您将退出程序")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    SlideActivity.this.finish();
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


    //获取首页的聊天室
    class os extends Thread {


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
                try{
                    conn.connect();
                }catch (Exception e){
                    checkWIFI(); //连上了wifi但是网络没有成功连接，弹窗提示
                }


                DataOutputStream out = new DataOutputStream(conn.getOutputStream());

                macAddress = getNewMac();
                Log.e("WSWSWSWS", "COME HERE------------macAddress！！！！" + macAddress);

               //给服务器发当前正连接的mac地址
                String content = "mac=" + URLEncoder.encode(macAddress, "utf-8");
                out.writeBytes(content);
                out.flush();
                out.close();

                if (conn.getResponseCode() == 200) {

                    Log.e("WS","getResponseCode为200");

                    InputStream is = conn.getInputStream();

                    Log.e("这里是is",is.toString()+" -------------------------------InputStream");//服务器返回的聊天室的json列表

                    byte[] bytes = new byte[1024];
                    int i = 0;
                    StringBuffer sb = new StringBuffer();
                    while ((i = is.read(bytes)) != -1) {
                        sb.append(new String(bytes, 0, i));
                    }

                   Log.e("这里是sb",sb.toString()+" -------------------------------string buffer");//服务器返回的聊天室的json列表

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


    //获取wifi的mac地址
    public String getNewMac() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission Not Granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else{
            WifiManager wifimanage = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiinfo = wifimanage.getConnectionInfo();


//            Log.e("startScan():", wifimanage.startScan() + "");//检查是否开始扫描，这是boolean类型的
//            Log.e("getScanResults:",wifimanage.getScanResults()+"");//显示扫描结果，这是list类型的
//
//
//            //显示所有SSID和BSSID
//            StringBuilder scanBuilder= new StringBuilder();
//            List<ScanResult> scanResults = wifimanage.getScanResults();
//
//            //一个一个的扫描，重复打印
//             for(ScanResult scanResult :scanResults){
//                 scanBuilder.append("\nSSID:"+scanResult.SSID
//                         +"\nBSSID:" + scanResult.BSSID);
//                 getSSID = scanResult.SSID;
//                 getBSSID = scanResult.BSSID;
////                 new sendMAC().start();
//             }
//            Log.e(" scanBuilder:",scanBuilder+"");

            ////获取当前连接网络的mac地址;
            try {
                macAddress = wifiinfo.getBSSID();
                Log.e("这里是else的macAddress", macAddress);
            }catch (Exception e){
                checkWIFI();//没获取到弹框让用用户连接wifi
            }

        }
        return macAddress;
    }

    //弹窗没有聊天室
    private void showDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SlideActivity.this);
                Log.e("这里是ws", "已经到达showDialog");
                builder.setTitle("温馨提示");
                builder.setMessage("您当前所处的位置没有聊天室，请换个位置刷新一下");
                builder.setPositiveButton("确定", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    //弹窗让用户检查wifi
    private void checkWIFI() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SlideActivity.this);
                Log.e("这里是ws", "已经到达checkWIFI");
                builder.setTitle("温馨提示");
                builder.setMessage("请连接附近wifi");
                builder.setPositiveButton("确定", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    //给服务器发送SSID和BSSID
    class sendMAC extends Thread {

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

                String data = "SSID=" + URLEncoder.encode(getSSID, "UTF-8")
                        + "&BSSID=" + URLEncoder.encode(getBSSID, "UTF-8");
                Log.e("data",data);
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

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    public void showDialog_restart(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SlideActivity.this);
        builder.setTitle("温馨提示");
        builder.setMessage("请您重启app检查更新");
        builder.setPositiveButton("确定", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
