package com.example.weahen.wstest.Activity;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.Model.ChatRoom;
import com.example.weahen.wstest.MyApplication;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.db.ChatContent;
import com.example.weahen.wstest.db.ChatContentDao;
import com.example.weahen.wstest.db.DaoMaster;
import com.example.weahen.wstest.db.DaoSession;
import com.example.weahen.wstest.db.MyDbOpenHelper;
import com.example.weahen.wstest.widget.SetPermissionDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.StompClient;

import static com.example.weahen.wstest.Activity.MainActivity.readPictureDegree;
import static com.example.weahen.wstest.Activity.MainActivity.rotateImageView;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_ENDTIME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_ID;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_NAME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_STARTTIME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.TABLE_NAME_ROOM;

//这是从聊天室列表进入具体聊天室的中间页面，显示聊天按钮和其他聊天室所在区域信息
public class FunctionActivity extends BaseActivity implements OnBannerListener {

    //轮播图相关
    private Banner mBanner;
    private MyImageLoader mMyImageLoader;
    private ArrayList<Integer> imagePath;
    private ArrayList<String> imageTitle;
    private StompClient mStompClient;


    //功能按钮
    Button button1_1;
    ImageView button2_1;

    TextView textView1_1;
    TextView textView2_1;


    int reserve;
    String path;

    String field;
    String location;

    int ID;
    String name;

    private static ListView lvContacts;

    //ArrayList<String> roomNameList;
    //ArrayList<String> roomIdList;
    ArrayList<ChatRoom> chatRoomList;
    private AlertDialog historyDialog;

    private MyDbOpenHelper myDbHelper;
    ArrayAdapter<ChatRoom> adapter;

ArrayList<String> pic_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        myDbHelper = MyDbOpenHelper.getInstance(this);
        getRoomList();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bun");
        reserve = bundle.getInt("reserve");
        path = bundle.getString("path");
        field = bundle.getString("field");
        location = bundle.getString("location");
        name = bundle.getString("name");
        pic_list=bundle.getStringArrayList("pic_list");
        ID=bundle.getInt("ID");
        //根据slidectivity返回数据的数量开线程，循环遍历接口返回的图片流数据，得到Bitmap
    for(int i=0;i<pic_list.size();i++){
        GetPicture dp=new GetPicture(path,pic_list.get(i));
    dp.start();
    try {
        dp.join();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    Bitmap res = dp.getimg();
    res.getHeight();
}

        initTitle(name);
        initData();
        initView();

        button1_1 = findViewById(R.id.button1_1);
        button2_1 = findViewById(R.id.img_show_shopinfo1);
        textView1_1 = findViewById(R.id.text1_1);
        textView2_1 = findViewById(R.id.text2_1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(1);
        //button2_1.setText("hello world");

        //field= 0  意思是这是合生汇那种大群，进去以后还有小群
        if(field.equals(0) ) {
            button1_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FunctionActivity.this, RefreshViewActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("location", location);
                    intent.putExtra("bun", bundle);
                    startActivity(intent);
                }
            });
        }else {    //field 不为 0  意思是这是星巴克那种小群，进去以后直接是聊天室
            button1_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FunctionActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", ID);
                    bundle.putString("path", path);
                    bundle.putString("name", name);
                    bundle.putString("field", field);
                    intent.putExtra("bun", bundle);
                    requestPermisson();
                    startActivity(intent);
                }
            });

        }

//        textView1_1.setText("聊天");


        //reserve为1或2意思是可以取号，reserve为0意思是不能取号
        Log.e("这里是reserve", reserve + "" );
        if ((reserve == 1) || (reserve == 2)) {
            button2_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FunctionActivity.this, MyNumberActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("path", path);
                    bundle.putInt("reserve", reserve);
                    intent.putExtra("bun", bundle);
                    startActivity(intent);
                }
            });
            textView2_1.setText("取号");
        }


//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "chat.db");
//        SQLiteDatabase db = helper.getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(db);
//        DaoSession daoSession = daoMaster.newSession();
//        chatContentDao = daoSession.getChatContentDao();

        //chatContentDao= MyApplication.getInstances().getDaoSession().getChatContentDao();
        // chatContents = chatContentDao.loadAll();
        //outputChatList();

    }
//从后台获取照片流数据，接口url：/get_AD_Img/{path}/{name}
    class GetPicture extends Thread {
        String pic_path;
        String pic_name;
        Bitmap m;
        Bitmap getimg(){return m;}
        GetPicture(String path,String name)
        {
            this.pic_path = path.substring(1);
            this.pic_name = name;
        }
        @Override
        public void run() {
            super.run();

            try {

                String path = "http://39.106.39.49:8888/get_AD_Img/" + pic_path+"/"+pic_name;
                //2:把网址封装为一个URL对象
                URL url = new URL(path);
                //3:获取客户端和服务器的连接对象，此时还没有建立连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //4:初始化连接对象
                conn.setRequestMethod("POST");
                //设置连接超时
                conn.setConnectTimeout(8000);
                //设置读取超时
                conn.setReadTimeout(8000);
                //5:发生请求，与服务器建立连接
                conn.connect();
                //如果响应码为200，说明请求成功
                Log.e("responseCode", conn.getResponseCode() + "");

                if (conn.getResponseCode() == 200) {
                    //获取服务器响应头中的流
                    InputStream is = conn.getInputStream();
                    //读取流里的数据，构建成bitmap位图
                    Bitmap bm = BitmapFactory.decodeStream(is);

                    int degree = readPictureDegree(path);
                    Log.e("接收图片的degree", degree + "");
                     m = rotateImageView(degree, bm);

//                    Message msg = new Message();
//                    msg.obj = m;
                    //handlerRecv.sendMessage(msg);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    //设置图片展示
    public  void getImg(){
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

        conn.connect();

    }catch(Exception e){

        e.printStackTrace();
    }
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
        builder.setTitle("删除此聊天室");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myDbHelper.deleteRoomFromTable(chatRoomList.get(position).getRoomId());
                myDbHelper.deleteRoom(chatRoomList.get(position).getRoomId());
                chatRoomList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(FunctionActivity.this, "已删除聊天室", Toast.LENGTH_LONG).show();

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

            Intent intent = new Intent(FunctionActivity.this, PersonalizedActivity.class);

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


    public void showHistoryDialog(){
        View view=View.inflate(FunctionActivity.this, R.layout.history_listview, null);
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
                Intent intent=new Intent(FunctionActivity.this,HistoryActivity.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
            builder.setView(view);
            historyDialog=builder.create();
            historyDialog.show();
        }else{
            historyDialog.show();
        }

    }



    private void initData() {
        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();
        imagePath.add(R.drawable.adv);
        imagePath.add(R.drawable.adv2);
        imagePath.add(R.drawable.adv3);
        imageTitle.add("美味蛋糕大促销");
        imageTitle.add("秋冬男装满1000减100");
        imageTitle.add("北欧风情家居馆");
    }

    private void initView() {
        mMyImageLoader = new MyImageLoader();
        mBanner = findViewById(R.id.banner);
        //设置样式，里面有很多种样式可以自己都看看效果
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(mMyImageLoader);
        //设置轮播的动画效果,里面有很多种特效,可以都看看效果。
        mBanner.setBannerAnimation(Transformer.ZoomOutSlide);
        //轮播图片的文字
        mBanner.setBannerTitles(imageTitle);
        //设置轮播间隔时间
        mBanner.setDelayTime(3000);
        //设置是否为自动轮播，默认是true
        mBanner.isAutoPlay(true);
        //设置指示器的位置，小点点，居中显示
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载地址
        mBanner.setImages(imagePath)
                //轮播图的监听
                .setOnBannerListener(this)
                //开始调用的方法，启动轮播图。
                .start();

    }

    /**
     * 轮播图的监听
     *
     * @param position
     */
    @Override
    public void OnBannerClick(int position) {
        Toast.makeText(this, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();
    }


    /**
     * 图片加载类
     */
    private class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .into(imageView);
        }
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
                            SetPermissionDialog mSetPermissionDialog = new SetPermissionDialog(FunctionActivity.this);
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

}
