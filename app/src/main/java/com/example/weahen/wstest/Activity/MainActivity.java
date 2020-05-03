package com.example.weahen.wstest.Activity;

import static android.view.View.VISIBLE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_AVATARID;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_NICKNAME;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_SHACODE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_TIMESTAMP;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.TABLE_NAME_CHAT;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_CHATROOMID;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_CONTENT;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_ISPICTURE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_ISSELF;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_PICTURE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_UID;

import static com.example.weahen.wstest.db.DBContract.RoomEntry.TABLE_NAME_ROOM;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_STARTTIME;
import static com.example.weahen.wstest.db.DBContract.RoomEntry.COLUMN_NAME_ENDTIME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weahen.wstest.Adapter.ContentListAdapter;
import com.example.weahen.wstest.Adapter.ContentListAdapterInMain;
import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.Model.Content;
import com.example.weahen.wstest.Model.RefreshableView;

import com.example.weahen.wstest.Obj.ImageInfoObj;
import com.example.weahen.wstest.Obj.ImageWidgetInfoObj;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.NetworkUtility;
import com.example.weahen.wstest.Utils.PictureUtil;
import com.example.weahen.wstest.Utils.SharedPreferencesUtil;
import com.example.weahen.wstest.Utils.Utils;
import com.example.weahen.wstest.db.ChatContent;
import com.example.weahen.wstest.db.MyDbOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

/*
MainActivity是从functionActivity来的
 */
public class MainActivity extends BaseActivity {
    private static final String TAG1 = "MainActivity";
    private boolean enter;
    RefreshableView refreshableView;
    private com.example.weahen.wstest.widget.CircleImageView avatar;
    ImageView imageView;
    private float beforeScale = 1.0f;
    private float nowScale;
    private static Bitmap bm;
    private static ImageView iv;

    private Button btnSend;
    private ImageButton btnCancelRefer;
    private EditText inputMsg;
    private ListView listViewMessages;
    private Utils utils;

    String content;
    private List<Content> listContent;
    private ContentListAdapter adapter2;

    private StompClient mStompClient;
    String time;
    int id;
    String name, path;
    String uid;
    String field;
    String mid;
    public static String NAME = "";          //连接WiFi名称
    public static String macAddress;     //连接WiFi的MAC

    private Timer timer;
    private String onClickedMid;

    String getMacresult = "";
    String uploadfile_result;
    File file;//上传的图片
    String pictureName;//下载的图片名

    String RecevierUID;
    boolean isSelf;
    boolean isPicture;
    String currTime;
    String online;

    String headImage;
    String userName;
    private ImageInfoObj imageInfoObj;
    private ImageWidgetInfoObj imageWidgetInfoObj;
    private AlertDialog picSelectDialog;
    private AlertDialog textSelectDialog;
    private Content clickedItem;
    private Content repliedItem;
    private int clickedItemPosition;
    private String withdrawedMid;

    private String enterTime;
    private String exitTime;
    private SimpleDateFormat format;

    private TextView referText;
    private LinearLayout referLayout;

    private LinearLayout mesTypeLayout;

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * 存放拍摄图片的文件夹
     */
    private static final String FILES_NAME = "/MyPhoto";
    /**
     * 获取的时间格式
     */
    public static final String TIME_STYLE = "yyyy-MM-dd HH:mm:ss";
    /**
     * 图片种类
     */
    public static final String IMAGE_TYPE = ".png";

    private MyDbOpenHelper myDbHelper;

    private Handler handlerRecv = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //    Content c = new Content(nick.isEmpty() ? RecevierUID.substring(0, 5) : nick, (Bitmap) msg.obj, isSelf, isPicture,headImage,userName);
            Content c = new Content(currTime, nick.isEmpty() ? RecevierUID.substring(0, 5) : nick, (Bitmap) msg.obj, isSelf, isPicture, headImage, userName,RecevierUID);
            String path = storePic((Bitmap) msg.obj);
            Log.e("Main", "handle Message: store a pic into db");
            Log.e("Main", "path is " + path);
           // if(uid)
            store2Db(c, path);
            //   chatContentDao.insert(new ChatContent(c.getContent(), "", c.isSelf(), c.getUid(), pictureName, true, name));
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String macNow = getNewMac();
                String macNowR1 = macNow.replace(":", "-");
                String macNowR2 = macNowR1.toUpperCase();//转成大写
               // Log.e("10秒获取mac", macNowR2);

                //      Log.e("manNow在result里面吗", getMacresult.contains(macNowR2) + "");
                if (!getMacresult.contains(macNowR2) && !enter) {
                    try {
                        Log.e("Handler", "now enter is true, no dialog");
                        showDialog2();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (getMacresult.contains(macNowR2)) {
                    //Log.e("Handler", "now enter is false");
                    enter = false;
                }


            }
        }
    };
    //  private ChatContentDao chatContentDao;
    private String nick;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);
        enter = true;

        SharedPreferencesUtil sp = new SharedPreferencesUtil(MainActivity.this.getApplicationContext());
        nick = sp.readData("nick", "");

        format = new SimpleDateFormat(TIME_STYLE, Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        enterTime = format.format(date);

        myDbHelper = MyDbOpenHelper.getInstance(this);

        btnSend = findViewById(R.id.btnSend);
        inputMsg = findViewById(R.id.inputMsg);
        utils = new Utils(getApplicationContext());

        btnCancelRefer=findViewById(R.id.referButton);
        referText=findViewById(R.id.referMsg);
        referLayout=findViewById(R.id.referLayout);

        mesTypeLayout=findViewById(R.id.mesTypeLayout);

        listViewMessages = findViewById(R.id.list_view_messages);

        btnCancelRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referLayout.setVisibility(View.INVISIBLE);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bun");
        name = bundle.getString("name");
        path = bundle.getString("path");
        id = bundle.getInt("id");
        field = bundle.getString("field");
        Log.e("MainActivity", "Id recevied by main is " + id);

        new os().start();

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
        }, 0, 10000);


        String MYIMEI;           //本机IMEI
        MYIMEI = NetworkUtility.getIMEI(MainActivity.this);
        Log.e("这里是imei", MYIMEI);
        try {
            uid = shaEncode(MYIMEI);
        } catch (Exception e) {
            e.printStackTrace();
        }


        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://39.106.39.49:8888/endpoint-websocket/websocket");
        String topicpath = "/chat" + path;

        //重复了
        mStompClient.topic(topicpath).subscribe(topicMessage -> {
            Log.e("WSWSonLine", topicMessage.getPayload());

            //new onLine().start();//获取在线人数
            parseMessage(topicMessage.getPayload());

        });

        mStompClient.connect();

        SharedPreferences pre = getSharedPreferences("MyChange", MODE_PRIVATE);
        uid = uid + pre.getString("MyChange", "2131230974");
        Log.e("Tag1", "拼接头像后的uid  " + uid);

        SharedPreferences idpre = getSharedPreferences("id", MODE_PRIVATE);

        String t = idpre.getString("id", uid.substring(0, 5));
        Log.e("Tag1", "main中拼接前键值对中的值  " + t);
        uid = uid + idpre.getString("id", uid.substring(0, 5));
        Log.e("Tag1", "main中拼接用户昵称uid的值  " + uid);

        //设置发送按钮的点击状态和事件
        btnSend.setEnabled(false);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                btnSend.setEnabled(false);
                if (inputMsg.getText().toString().trim().length() > 0) {
                    btnSend.setEnabled(true);
                    btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String inputText=inputMsg.getText().toString();
                            String replyText="";
                            if(referLayout.getVisibility()==VISIBLE){

                                clickedItem.setHasReply(true);
                                repliedItem=clickedItem;

//                                clickedItem.setContent(replyText);
//                                adapter2.notifyDataSetChanged();
                            }
                            time = String.valueOf(new Date().getTime());
                            mid=uid+currTime;
                            mStompClient.send("/app/chatroom" + path, utils.getSendMessageJSON(currTime, mac, name, path, id, uid, mid, inputText)).subscribe();
                            Log.e("btnSendOnClick", utils.getSendMessageJSON(currTime, mac, name, path, id, uid, mid, inputText));

                            inputMsg.setText("");
                            referLayout.setVisibility(View.INVISIBLE);



                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        inputMsg.addTextChangedListener(textWatcher);
        listContent = new ArrayList<Content>();
        adapter2 = new ContentListAdapter(this, listContent,this);
        listViewMessages.setAdapter(adapter2);


        listViewMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return true;
            }
        });
        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("Click", "ContentLisAdapter onClick, position is" + i + "------------------------------------");
                //判断点击类型
                clickedItem = listContent.get(i);
                clickedItemPosition = i;
                onClickedMid=uid+clickedItem.getTime();
                if (clickedItem.isPicture()) {
                    if (picSelectDialog == null) {
                        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.select_pic_dialog, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        rootView.findViewById(R.id.show_photo).setOnClickListener(onShowPhotoListener);
                        rootView.findViewById(R.id.save_photo).setOnClickListener(onSavePhotoListener);
                        rootView.findViewById(R.id.delete_photo).setOnClickListener(onDeletePhotoListener);
                        builder.setView(rootView);
                        picSelectDialog = builder.create();
                        picSelectDialog.show();
                    } else {
                        picSelectDialog.show();
                    }
                } else {
  //                  if (textSelectDialog == null) {
                        @SuppressLint("InflateParams") View rootView;
                        if(clickedItem.getWithdraw()){
                            rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.select_text_withdraw_dialog, null);
                            rootView.findViewById(R.id.withdraw_text).setOnClickListener(onWithdrawTextListener);

                        }else{
                            rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.select_text_dialog, null);

                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        rootView.findViewById(R.id.delete_text).setOnClickListener(onDeleteTextListener);
                        rootView.findViewById(R.id.reply_text).setOnClickListener(onReplyTextListener);
                        builder.setView(rootView);
                        textSelectDialog = builder.create();
                        textSelectDialog.show();
//                    } else {
//                        textSelectDialog.show();
//                    }
                }

            }
        });

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
    }

    public void withdraw(){
        mStompClient.send("/app/chatroom" + path, utils.getSendMessageJSON("", "", "", path, 0, String.valueOf(-2), "", onClickedMid)).subscribe();
    }

    public void reply(String text){
        referText.setText(text);
        referLayout.setVisibility(VISIBLE);
    }

    private void dismissTextSelectDialog() {
        if (textSelectDialog != null && textSelectDialog.isShowing()) {
            textSelectDialog.dismiss();
        }
    }

    private View.OnClickListener onWithdrawTextListener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            dismissTextSelectDialog();
            withdraw();
        }
    };
    private View.OnClickListener onReplyTextListener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            dismissTextSelectDialog();
            String text=clickedItem.getContent();

            reply(text);
        }
    };

    private View.OnClickListener onDeleteTextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissTextSelectDialog();
            showDeleteDialog(clickedItem.getShaCode(), clickedItemPosition);
        }
    };

    private void showDeleteDialog(String shaCode, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("删除此聊天会话");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myDbHelper.deleteChatData(shaCode, TABLE_NAME_CHAT);
                listContent.remove(position);
                Toast.makeText(MainActivity.this, "已删除聊天会话", Toast.LENGTH_LONG).show();
                adapter2.notifyDataSetChanged();
                //recreate();
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

    private void dismissPicSelectDialog() {
        if (picSelectDialog != null && picSelectDialog.isShowing()) {
            picSelectDialog.dismiss();
        }
    }

    private View.OnClickListener onShowPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissPicSelectDialog();
//            avatar=listViewMessages.findViewById(R.id.chat_item_header);
//            avatar.setImageResource(R.drawable.a1);

            imageView = listViewMessages.findViewById(R.id.bivPic);
            //  bm = clickedItem.getPicture();
            String shacode = clickedItem.getShaCode();

            String picPath = myDbHelper.getPicPath(shacode);
            //imageView.setImageBitmap(bm);

            imageInfoObj = new ImageInfoObj();
            // imageInfoObj.imageUrl = uri.toString();
            imageInfoObj.imageWidth = 1280;
            imageInfoObj.imageHeight = 720;

            imageWidgetInfoObj = new ImageWidgetInfoObj();
            imageWidgetInfoObj.x = imageView.getLeft();
            imageWidgetInfoObj.y = imageView.getTop();
            imageWidgetInfoObj.width = imageView.getLayoutParams().width;
            imageWidgetInfoObj.height = imageView.getLayoutParams().height;

            // showImageDialog(bm);
            Intent intent = new Intent(MainActivity.this, PicDisplayActivity.class);
            intent.putExtra("picPath", picPath);
            startActivity(intent);
        }
    };
    private View.OnClickListener onSavePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissPicSelectDialog();

            imageView = listViewMessages.findViewById(R.id.bivPic);
            bm = clickedItem.getPicture();
            showSaveImageDialog(bm);


        }
    };
    private View.OnClickListener onDeletePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissPicSelectDialog();
            showDeleteDialog(clickedItem.getShaCode(), clickedItemPosition);
        }
    };

    private String storePic(Bitmap photo) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(PictureUtil.getMyDictRootDirectory(), "capsule_picture");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.d(TAG, "in setPicToView->文件夹创建失败");
                } else {
                    Log.d(TAG, "in setPicToView->文件夹创建成功");
                }
            }
            File file_p = new File(dirFile, System.currentTimeMillis() + ".jpg");

            // InfoPrefs.setData(SyncStateContract.Constants.UserInfo.HEAD_IMAGE,file.getPath());
            //Log.d("result",file.getPath());
            // Log.d("result",file.getAbsolutePath());
            // 保存图片
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file_p);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return file_p.getPath();
        }
        return "";
    }

    private void parseMessage(final String msg) {

        String content;


        try {
            JSONObject jObj = new JSONObject(msg);

            content = jObj.getString("content");

            RecevierUID = jObj.getString("uid");
            if (RecevierUID.equals("-2")) {
                withdrawedMid=content;
                for(int i=0;i<listContent.size();i++){
                    if(withdrawedMid.equals(listContent.get(i).getUid()+listContent.get(i).getTime())){
                        myDbHelper.deleteChatData(listContent.get(i).getShaCode(), TABLE_NAME_CHAT);
                        listContent.remove(i);
                        adapter2.notifyDataSetChanged();
                        //adapter2.notifyDataSetInvalidated();
                    }
                }

            } else {

                Log.e("Tag1", "RecevierUID  " + RecevierUID);
                headImage = RecevierUID.substring(40, 50);

                Log.e("Tag1", "headImage   " + headImage);
//           time = jObj.getString("time");
                currTime = jObj.getString("time");
                userName = RecevierUID.substring(50);

                isSelf = true;

                //检查聊天室是不是到时间了
                String time_is_up = "0";
                if (RecevierUID.equals(time_is_up)) {
                    Log.e("这里的uid为0", RecevierUID);
                    showDialog();
                    mStompClient.disconnect();

                }

                //聊天室在线人数
                String ONLINE = "-1";
                if (RecevierUID.equals(ONLINE)) {
                    Log.e("这里的uid为-1", RecevierUID);
                    online = content;

                }

                // 检查消息是不是有自己发出的
                if (!(uid.equals(RecevierUID))) {
                    isSelf = false;
                }


                Log.e("这里是content", content);

                //如果content是图片消息
                if (content.contains("#")) {
                    Log.e("wswsPic", "这里是图片消息");
                    isPicture = true;
                    pictureName = content.substring(2, content.length() - 1);
                    time = String.valueOf(new Date().getTime());
                    Log.e("wswsPic", pictureName);
                    new download_picture().start();
                } else { //如果congtent是文字消息
                    isPicture = false;
                    Log.e("wswsText", "这里是文字消息");
                    Content c = new Content(currTime, nick.isEmpty() ? RecevierUID.substring(0, 5) : nick, content, isSelf, isPicture, headImage, userName,RecevierUID);

                    if(clickedItem!=null){//收到的这条消息是repliedItem的reply,要对repliedItem进行操作
                        if(clickedItem.getReplyContent().size()==0){//让自己成为回复列表里的第一行
                            listContent.get(clickedItemPosition).addReply(clickedItem);

                        }
                        c.setReply(true);//把回复设置成回复
                        listContent.get(clickedItemPosition).addReply(c);//把回复衔接在repliedItem上
//                        listContent.get(clickedItemPosition).addReply(c);//把回复衔接在repliedItem上
//                        listContent.get(clickedItemPosition).addReply(c);//把回复衔接在repliedItem上
                        for(int i=0;i<listContent.get(clickedItemPosition).getReplyContent().size();i++){
                            Log.e("Receive Reply ","i is "+i+" reply content is"+listContent.get(clickedItemPosition).getReplyContent().get(i).getContent());
                        }
                      //  adapter2.notifyDataSetChanged();
                        clickedItem=null;

                    }
                    store2Db(c, "");//把回复内容存进数据库

                }
            }
            } catch(JSONException e){
                e.printStackTrace();
            }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("MainActivity", "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainActivity", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainActivity", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MainActivity", "onPause");
        Date date = new Date(System.currentTimeMillis());
        exitTime = format.format(date);

        SQLiteDatabase dbw = myDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_STARTTIME,enterTime);
        contentValues.put(COLUMN_NAME_ENDTIME,exitTime);
        dbw.update(TABLE_NAME_ROOM,contentValues,"id=?",new String[]{String.valueOf(id)});
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("MainActivity", "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MainActivity", "onStart");
    }

    private boolean stringExist(String sc, ArrayList<String> list) {
        for (String i : list) {
            if (i.equals(sc)) {
                return true;
            }
        }
        return false;
    }

    private void store2Db(Content c, String picturePath) {
        SQLiteDatabase dbw = myDbHelper.getWritableDatabase();
        ArrayList<String> shaCodeList = new ArrayList<>();
        ArrayList<String> picPathList = new ArrayList<>();
        shaCodeList.clear();
        picPathList.clear();
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_CHAT, null, "ChatRoomId=?", new String[]{String.valueOf(id)}, null, null, null);
        while (cursor.moveToNext()) {
            String shaCode = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SHACODE));
            String picpath = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PICTURE));
            shaCodeList.add(shaCode);
            picPathList.add(picpath);
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_CONTENT, c.getContent());
        contentValues.put(COLUMN_NAME_ISSELF, c.isSelf());
        contentValues.put(COLUMN_NAME_ISPICTURE, c.isPicture());
        contentValues.put(COLUMN_NAME_UID, c.getUid());
        contentValues.put(COLUMN_NAME_PICTURE, picturePath);
        contentValues.put(COLUMN_NAME_TIMESTAMP, time);
        if (headImage == null) {
            headImage = "2131230974";
        }
        contentValues.put(COLUMN_NAME_AVATARID, headImage);
        if (userName == null) {
            userName = uid.substring(0, 5);
        }
        contentValues.put(COLUMN_NAME_NICKNAME, userName);
        Log.e("Tag1", "数据库中存储的头象" + headImage);
        contentValues.put(COLUMN_NAME_CHATROOMID, id);
        String s = c.getUid() + time;
        try {
            String code = shaEncode(s);
            contentValues.put(COLUMN_NAME_SHACODE, code);
            if (!stringExist(code, shaCodeList) && time != "") {
                Log.e("MainActivity", "store text chat content into db");
                myDbHelper.insertChatData(contentValues, dbw);
                Log.e("MainActivity", "Before appendMessage, set shacode is " + code);
                c.setShaCode(code);
                appendMessage(c);//存进数据库后判断要不要展示在列表里，如果是回复就不展示在主列表里
                changeWithdraw(listContent.size());
            } else {
                Log.e("MainActivity", "shacode exist!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 把消息放到listView里
     */
    private void appendMessage(final Content c) {
        Log.e("TAG",TAG1+"  再次进入调用appendMessage");
        Log.e("Main","appendMessage----------");
        new Thread(new Runnable(){

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!c.isReply()){
                            listContent.add(c);
                            adapter2.notifyDataSetChanged();
                        }


                        Log.e("MainActivity","index of c is "+listContent.indexOf(c));
                       // updatedItem(listContent.size()-1);
                        Log.e("MainActivity","appendMessage, after notifyDataSetChanged");
                        playBeep();
                    }
                });
            }
        }).start();

    }

    private void updatedItem(int position){
        int firstVisiblePosition=listViewMessages.getFirstVisiblePosition();
        int lastVisiblePosition=listViewMessages.getLastVisiblePosition();
        Log.e("MainActiviy","position is "+position+" firstVisiblePosition "+firstVisiblePosition+" lastVisiblePosition "+lastVisiblePosition);
        if(position>=firstVisiblePosition && position<=lastVisiblePosition){
            View view=listViewMessages.getChildAt(position-firstVisiblePosition);
            adapter2.getView(position,view, listViewMessages);
        }
    }

    private void changeWithdraw(int i){
        Log.e("Main","change withdraw----------");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(i<listContent.size()){
                            listContent.get(i).setWithdraw(false);
                            adapter2.notifyDataSetChanged();
                        }

                    }
                });

            }
        }).start();

    }


    private void showToast(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * 播放默认的通知声音
     */
    public void playBeep() {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //对imei用SHA算法加密
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

    //聊天室到时提醒
    private void showDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                Log.e("这里是ws", "已经到达showDialog");
                builder.setTitle("温馨提示");
                builder.setMessage("聊天室到时间啦");
                builder.setPositiveButton("我知道了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mStompClient.disconnect();
                                Intent intent = new Intent(MainActivity.this, FunctionActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    //离开区域提醒
    private void showDialog2() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                Log.e("这里是ws", "已经到达showDialog2");
                builder.setTitle("温馨提示");
                builder.setMessage("您已离开此聊天室所在区域");
                builder.setPositiveButton("我知道了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mStompClient.disconnect();
                                Intent intent = new Intent(MainActivity.this, FunctionActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }


    public String getNewMac() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission Not Granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            WifiManager wifimanage = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiinfo = wifimanage.getConnectionInfo();
            macAddress = wifiinfo.getBSSID();//获取当前连接网络的mac地址;
            //   macAddress = "DC:72:9B:62:2B:64";
        }
        return macAddress;
    }


    //是否退出聊天室
    AlertDialog builder = null;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("温馨提示：")
                    .setMessage("确认离开")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    MainActivity.this.finish();
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


    public void changeTX(View view) {
        Intent intent = new Intent(MainActivity.this, PersonalizedActivity.class);
        startActivity(intent);
    }


    public void Picture(View view) {
// 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

    }

    public void showMesType(View view){
        if(mesTypeLayout.getVisibility()==View.GONE){
            mesTypeLayout.setVisibility(VISIBLE);
        }else{
            mesTypeLayout.setVisibility(View.GONE);
        }

    }

    public void Marquee(View view){
        mesTypeLayout.setVisibility(View.GONE);
    }


    //获取当前聊天室的mac列表，用于每10s检查实时mac是否在列表里
    class os extends Thread {

        String urla = "http://39.106.39.49:8888/getLocationMacList";

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


                Log.e("这里是field", field);
                // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
                String content = "fieldID=" + URLEncoder.encode(field, "utf-8");

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
                    getMacresult = new String(baos.toByteArray());
                    Log.e("MY Result", getMacresult);


                } else {
                    Log.e("失败了", "失败了");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //发送图片
    class os_file extends Thread {

        String urla = "http://39.106.39.49:8888/uploadImg/" + file.getName();

        @Override
        public void run() {
            super.run();
            try {

                try {

                    int degree = readPictureDegree(file.getPath());
                    Log.e("手机相册的degree", degree + "");

                    String resized_file = amendRotatePhoto(file.getPath(), getBaseContext());
                    Log.e("resized_file", resized_file);

                    URL url = new URL(urla);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(TIME_OUT);
                    conn.setConnectTimeout(TIME_OUT);
                    conn.setDoInput(true); // 允许输入流
                    conn.setDoOutput(true); // 允许输出流
                    conn.setUseCaches(false); // 不允许使用缓存
                    conn.setRequestMethod("POST"); // 请求方式
                    conn.setRequestProperty("Charset", CHARSET); // 设置编码
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", "file/*");
                    if (file != null) {
                        /**
                         * 当文件不为空，把文件包装并且上传
                         */
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                        Log.e("file", file.toString());

                        FileInputStream is = new FileInputStream(new File(resized_file));

                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = is.read(bytes)) != -1) {
                            dos.write(bytes, 0, len);
                        }
                        is.close();

                        dos.close();
                        /**
                         * 获取响应码 200=成功 当响应成功，获取响应的流
                         */
                        int res = conn.getResponseCode();
                        Log.e(TAG, "response code:" + res);

                        if (res == 200) {
                            time = String.valueOf(new Date().getTime());
                            Log.e(TAG, "request success");
                            InputStream input = conn.getInputStream();
                            StringBuffer sb1 = new StringBuffer();
                            int ss;
                            while ((ss = input.read()) != -1) {
                                sb1.append((char) ss);
                            }
                            uploadfile_result = sb1.toString();
                            Log.e(TAG, "result : " + uploadfile_result);

                            String pictureCONTENT = "#(" + file.getName() + ")";
                            Log.e(TAG, pictureCONTENT);
                            mid=uid+currTime;
                            mStompClient.send("/app/chatroom" + path, utils.getSendMessageJSON(currTime, getNewMac(), name, path, id, uid, mid, pictureCONTENT)).subscribe();

                        } else {
                            Log.e(TAG, "request error");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //将URI类型转换为File类型
    public static File getFileByUri(Uri uri, Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    class download_picture extends Thread {

        @Override
        public void run() {
            super.run();

            try {

                Log.e(TAG, "到达download函数");
                String path = "http://39.106.39.49:8888/getImg/" + pictureName;
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
                    Bitmap resized_bm = rotateImageView(degree, bm);

                    Message msg = new Message();
                    msg.obj = resized_bm;
                    handlerRecv.sendMessage(msg);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param file_path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String file_path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(file_path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 旋转图片
     * * @param angle 被旋转角度
     * * @param bitmap 图片对象
     * * @return 旋转后的图片
     */
    public static Bitmap rotateImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {

        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 处理旋转后的图片
     * * @param originpath 原图路径
     * * @param context 上下文
     * * @return 返回修复完毕后的图片路径
     */
    public static String amendRotatePhoto(String originpath, Context context) {
        // 取得图片旋转角度
        int angle = readPictureDegree(originpath);
        // 把原图压缩后得到Bitmap对象
        Bitmap bmp = getCompressPhoto(originpath);
        // 修复图片被旋转的角度
        Bitmap bitmap = rotateImageView(angle, bmp);
        // 保存修复后的图片并返回保存后的图片路径
        return savePhotoToSD(bitmap, context);
    }

    /**
     * 把原图按1/10的比例压缩     *
     * * @param path 原图的路径
     * * @return 压缩后的图片
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 5;  // 图片的大小设置为原来的十分之一
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }

    /**
     * 保存Bitmap图片在SD卡中
     * * 如果没有SD卡则存在手机中     *
     * * @param mbitmap 需要保存的Bitmap图片
     * * @return 保存成功时返回图片的路径，失败时返回null
     */
    public static String savePhotoToSD(Bitmap mbitmap, Context context) {
        FileOutputStream outStream = null;
        String fileName = getPhotoFileName(context);
        try {
            outStream = new FileOutputStream(fileName);
            // 把数据写入文件，100表示不压缩
            mbitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    // 记得要关闭流！
                    outStream.close();
                }
                if (mbitmap != null) {
                    mbitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取手机可存储路径     *
     * * @param context 上下文
     * * @return 手机可存储路径
     */
    private static String getPhoneRootPath(Context context) {
        // 是否有SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            // 获取SD卡根目录
            return context.getExternalCacheDir().getPath();
        } else {            // 获取apk包下的缓存路径
            return context.getCacheDir().getPath();
        }
    }

    /**
     * 使用当前系统时间作为上传图片的名称     *
     * * @return 存储的根路径+图片名称
     */
    public static String getPhotoFileName(Context context) {
        File file = new File(getPhoneRootPath(context) + FILES_NAME);
        // 判断文件是否已经存在，不存在则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 设置图片文件名称
        SimpleDateFormat format = new SimpleDateFormat(TIME_STYLE, Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        String photoName = "/" + time + IMAGE_TYPE;
        return file + photoName;
    }


    class onLine extends Thread {

        String urla = "http://39.106.39.49:8888/onLine";

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

                Log.e("online的path", path);
                String content = "path=" + URLEncoder.encode("/chat" + path, "utf-8");

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
                    online = new String(baos.toByteArray());
                    Log.e("返回的online", online);
                    initTitleASK(name + "(" + online + ")");


                } else {
                    Log.e("失败了", "失败了");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//    private void myScale(){
//        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
//            @Override
//            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//                nowScale = scaleGestureDetector.getScaleFactor() * beforeScale;
//                if (nowScale > 3 || nowScale < 1.0) {
//                    beforeScale = nowScale;
//                    return true;
//                }
//                Matrix matrix = new Matrix();
//                matrix.setScale(nowScale, nowScale);
//                Bitmap bm2 = bm;
//                bm2 = Bitmap.createBitmap(bm2, 0, 0, bm2.getWidth(), bm2.getHeight(), matrix, true);
//                iv.setImageBitmap(bm2);
//                beforeScale = nowScale;
//                return false;
//            }
//
//            @Override
//            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
//                return true;
//            }
//
//            @Override
//            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
//
//            }
//        });
//        iv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                scaleGestureDetector.onTouchEvent(motionEvent);
//                return true;
//            }
//        });
//
//    }
//    private void showImageDialog (Bitmap bm){
//        View view = View.inflate(MainActivity.this, R.layout.simple_image_view, null);
//        iv = view.findViewById(R.id.imageView);
//        iv.setImageBitmap(bm);
//
//        myScale();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setView(view);
//        builder.show();
//    }

    private void showSaveImageDialog(Bitmap bm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("保存图片至相册");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveBmpToGallery(MainActivity.this, bm, System.currentTimeMillis());
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

    public static void saveBmpToGallery(Context context, Bitmap bm, long picName) {
        Log.i(TAG, "enter save to gallery");
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator;

        File file = null;
        String fileName = null;
        FileOutputStream outputStream = null;
        try {
            File dirFile = new File(galleryPath, "时空小胶囊");//create new file under album

            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.d(TAG, "in setPicToView->文件夹创建失败");
                } else {
                    Log.d(TAG, "in setPicToView->文件夹创建成功");
                }
            }
            file = new File(dirFile, picName + ".jpg");
            fileName = file.toString();
            outputStream = new FileOutputStream(fileName);
            if (null != outputStream) {
                bm.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);

        Toast.makeText(context, "Download success", Toast.LENGTH_LONG).show();
    }

    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
            imageCapture();
        } else {
            imageCapture();
        }

    }

    public void Camera(View view) {
        checkCameraPermission();
    }

    private void imageCapture() {
        Intent intent;
        Uri pictureUri;
        //saveBmpToGallery(ShowCapturedPhotoActivity.this,photoPath,System.currentTimeMillis());
        File pictureFile = new File(PictureUtil.getMyDictRootDirectory(), "时空小胶囊");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureUri = FileProvider.getUriForFile(this, "com.example.weahen.wstest", pictureFile);

        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        Log.e(TAG, "before take photo " + pictureUri.toString());
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_GALLERY:
                    Uri uri = data.getData();
                    Log.e("uri", uri.toString());

                    file = getFileByUri(uri, getBaseContext());
                    new os_file().start();
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    File pictureFile = new File(PictureUtil.getMyDictRootDirectory(), "时空小胶囊");
                    Uri pictureUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pictureUri = FileProvider.getUriForFile(this, "com.example.weahen.wstest", pictureFile);
                        Log.e(TAG, "picURI=" + pictureUri.toString());
                    } else {
                        pictureUri = Uri.fromFile(pictureFile);
                        Log.e(TAG, "picURI=" + pictureUri.toString());
                    }
                    //startPhotoZoom(pictureUri);
                    file = pictureFile;
                    new os_file().start();
                    break;
            }
        }
    }


}


