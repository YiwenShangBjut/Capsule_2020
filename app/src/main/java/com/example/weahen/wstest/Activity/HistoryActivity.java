package com.example.weahen.wstest.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.example.weahen.wstest.Adapter.ContentListAdapter;
import com.example.weahen.wstest.Model.Content;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.db.MyDbOpenHelper;
import com.example.weahen.wstest.BaseActivity;

import java.util.ArrayList;

import static com.example.weahen.wstest.db.DBContract.ChatEntry.CID;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_AVATARID;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_CHATROOMID;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_NICKNAME;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_SHACODE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.TABLE_NAME_CHAT;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_CONTENT;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_ISPICTURE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_ISSELF;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_PICTURE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_TIME;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_UID;

public class HistoryActivity extends BaseActivity {
    private String id;
    private String roomName;
    private MyDbOpenHelper myDbHelper;
    private ArrayList<Content> contentList;
    private ContentListAdapter adapter;
    private ListView lvContracts;
    String curTime;
    String headImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        roomName=intent.getStringExtra("roomName");
        initTitle(roomName);

        contentList=new ArrayList<Content>();
        contentList.clear();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(1);

        myDbHelper = MyDbOpenHelper.getInstance(this);
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        //get all chat information with this chatroom id from the db
        Log.e("History","room id is "+id);
        Cursor cursor=db.query(TABLE_NAME_CHAT,null,"ChatRoomId=?",new String[]{id},null,null,null);
        Log.e("History","cursor row num is "+cursor.getCount());
       if(cursor==null){
           Log.e("History","cursor is null");
       }else {
           Log.e("History", "cursor is not null");
           while (cursor.moveToNext()) {
               String uid = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UID));
               String Content = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CONTENT));
               String IsSelf = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ISSELF));
              // String IsSelf="true";
               // Log.e("History", "is self is " + IsSelf);
               String IsPic = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ISPICTURE));
               String Pic = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PICTURE));
               String shaCode = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SHACODE));
               Log.e("Tag1","history里的头像"+cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATARID)));
               String imageId=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATARID));
               String nickName=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICKNAME));
               Boolean isself=true;
               if(IsSelf.equals("1")){
                   isself=true;
               }else{
                   isself=false;
               }
                Log.e("History","isSelf is "+isself);
               if (Integer.valueOf(IsPic).intValue() == 0) {//text
                   Log.e("History", "text");
                   Content content = new Content(curTime,uid, Content, isself, false,imageId,nickName);
                   content.setShaCode(shaCode);
                   contentList.add(content);
               } else { //image
                   Log.e("History", "pic");
                   Bitmap bp = BitmapFactory.decodeFile(Pic);
                   Content content = new Content(curTime,uid, bp, isself, true,imageId,nickName);
                   content.setShaCode(shaCode);
                   contentList.add(content);
               }
           }
       }

        cursor.close();
     //   Log.e("History","content list column num is "+contentList.size());
        adapter=new ContentListAdapter(this, contentList);
        lvContracts=(ListView)findViewById(R.id.history_activity_contacts);
        lvContracts.setAdapter(adapter);
        lvContracts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Content clickedContent=contentList.get(position);
                String shaCode=clickedContent.getShaCode();
              //  Log.e("History","onItem click, shacode is "+shaCode);
                showDeleteDialog(shaCode,position);

            }
        });

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.history, menu);

        return true;

    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.action_delete_room_history) {

            showDeleteRoomDialog();

        }

        return super.onOptionsItemSelected(item);

    }
    private void showDeleteDialog (String shaCode, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
        builder.setTitle("删除此聊天记录");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("History","click positive button");
                myDbHelper.deleteChatData(shaCode, TABLE_NAME_CHAT);
                contentList.remove(position);
                Toast.makeText(HistoryActivity.this, "已删除聊天记录", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
               // recreate();
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

    private void showDeleteRoomDialog (){
        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
        builder.setTitle("删除此聊天室记录");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("History","click positive button");
                myDbHelper.deleteRoom(id);
                //myDbHelper.deleteChatData(shaCode, TABLE_NAME_CHAT);
               // contentList.remove(position);
                Toast.makeText(HistoryActivity.this, "已删除该聊天室记录", Toast.LENGTH_LONG).show();
               // adapter.notifyDataSetChanged();
                // recreate();
                Intent intent=new Intent(HistoryActivity.this,SlideActivity.class);
                startActivity(intent);
                myDbHelper.deleteRoomFromTable(id);
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
}
