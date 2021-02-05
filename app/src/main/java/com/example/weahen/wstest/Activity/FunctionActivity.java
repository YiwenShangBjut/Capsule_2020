package com.example.weahen.wstest.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.weahen.wstest.Adapter.MerchantAdapter;
import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.Model.ChatRoom;
import com.example.weahen.wstest.Model.Merchant;
import com.example.weahen.wstest.MyApplication;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.BitmapUtil;
import com.example.weahen.wstest.Utils.HttpUtils;
import com.example.weahen.wstest.Utils.MyBitmapUtils;
import com.example.weahen.wstest.Utils.NetCacheUtils;
import com.example.weahen.wstest.db.MyDbOpenHelper;
import com.example.weahen.wstest.widget.SetPermissionDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.StompClient;

import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_ENDTIME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_ID;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_NAME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_STARTTIME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.TABLE_NAME_ROOM;


/**
 * 这是从聊天室列表进入具体聊天室的中间页面，显示聊天按钮和其他聊天室所在区域信息
 */
public class FunctionActivity extends BaseActivity implements OnBannerListener {

    protected static final String TAG = "FunctionActivity";

    //轮播图相关
    private Banner mBanner;
    private MyImageLoader mMyImageLoader;
    private ArrayList<Integer> imagePath;
    private ArrayList<String> imageTitle;
    private StompClient mStompClient;

    //聊天按钮
    private Button button1;

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

    private AppBarLayout mAppBarLayout;
    private RecyclerView recyclerView;
    //商家属性
    private List<Bitmap> bitmapArrayList = new ArrayList<>();

    private List<Merchant> merchantList = new ArrayList<>();

    private MerchantAdapter merchantAdapter;

    private MyBitmapUtils myBitmapUtils;


    //处理商家信息的异步消息
    private Handler handlerImg = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                ArrayList<Bitmap> bitmapList = (ArrayList<Bitmap>) msg.obj;
                recyclerView = findViewById(R.id.recycler_View);
                GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 2);
                recyclerView.setLayoutManager(layoutManager);
                merchantAdapter = new MerchantAdapter(merchantList, bitmapList);
                recyclerView.setAdapter(merchantAdapter);

            }

        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function2);
        myDbHelper = MyDbOpenHelper.getInstance(this);
        getRoomList();

        myBitmapUtils = new MyBitmapUtils(MyApplication.getContext());

        Intent intent = getIntent();
        reserve = intent.getIntExtra("reserve",0);
        path = intent.getStringExtra("path");
        field = intent.getStringExtra("field");
        location = intent.getStringExtra("location");
        name = intent.getStringExtra("name");
        ID = intent.getIntExtra("ID", 0);
        merchantList = intent.getParcelableArrayListExtra("merchantList");

        Log.e("Function---","---path:"+path+"---name:"+name+"---field:"+field+"---location:"+location);
        Log.e("merchantList----", merchantList.size()+"");


        //加载轮播图
        initTitle(name);
        initData();
        initView();


        button1 = findViewById(R.id.button1);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(1);



        //field= 0  意思是这是合生汇那种大群，进去以后还有小群
        if(field.equals(0) ) {
            button1.setOnClickListener(new View.OnClickListener() {
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
            button1.setOnClickListener(new View.OnClickListener() {
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


        //商家部分
        new GetPicture(merchantList).start();


        mAppBarLayout = findViewById(R.id.appBarLayout);
        //悬浮按钮
        FloatingActionButton fab = findViewById(R.id.toTop);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回顶部
                CoordinatorLayout.Behavior behavior =
                        ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();
                if (behavior instanceof AppBarLayout.Behavior) {
                    AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
                    int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
                    if (topAndBottomOffset != 0) {
                        appBarLayoutBehavior.setTopAndBottomOffset(0);
                    }
                }
                recyclerView.scrollToPosition(0);

                Toast.makeText(FunctionActivity.this, "置顶", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //从后台获取商家照片流数据，接口url：/get_AD_Img/{path}/{pic_name}
    // http://39.106.39.49:8888/get_AD_Img/1478602/猫2.jpg
    class GetPicture extends Thread {
        List<Merchant> merchantList;

        GetPicture(List<Merchant> merchants) {
            this.merchantList = merchants;
        }

        @Override
        public void run() {
            super.run();

            try {
                Log.e("GetPicture---------", merchantList.size()+"");

                for (int i = 0; i < merchantList.size(); i++) {

                    Merchant merchant = merchantList.get(i);
                    String path = "http://39.106.39.49:8888/get_AD_Img" +
                            merchant.getPath() + "/" + merchant.getPicName();

                    Bitmap bm = myBitmapUtils.getBitmap(path); // 三级缓存
                    bitmapArrayList.add(bm);
                }

                //发送更新UI的消息
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bitmapArrayList;
                handlerImg.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 删除聊天室
     * @param position
     */
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
