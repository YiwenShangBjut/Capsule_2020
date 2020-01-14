package com.example.weahen.wstest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.weahen.wstest.R;

import static com.example.weahen.wstest.db.DBContract.ChatEntry.CID;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_AVATARID;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_CHATROOMID;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_NICKNAME;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_SHACODE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_TIMESTAMP;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.TABLE_NAME_CHAT;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_CONTENT;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_ISPICTURE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_ISSELF;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_PICTURE;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_TIME;
import static com.example.weahen.wstest.db.DBContract.ChatEntry.COLUMN_NAME_UID;

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

public class MyDbOpenHelper extends SQLiteOpenHelper {
    private final static String TAG= MyDbOpenHelper.class.getSimpleName();
    public static final String DATABASE_NAME="capsuledb.db";
    public static final int DATABASE_VERSION=4;
    private static MyDbOpenHelper myDbOpenHelper;
    private static final String SQL_DELETE_CHAT="DROP TABLE IF EXISTS "+ TABLE_NAME_CHAT;
    private static final String SQL_DELETE_ROOM="DROP TABLE IF EXISTS "+ TABLE_NAME_ROOM;

    private MyDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME,factory,version);
    }

    public static MyDbOpenHelper getInstance(Context context){
        if(null==myDbOpenHelper){
            synchronized (MyDbOpenHelper.class){
                if(null==myDbOpenHelper){
                    myDbOpenHelper=new MyDbOpenHelper(context, DATABASE_NAME,null,DATABASE_VERSION);
                }
            }
        }
        return myDbOpenHelper;
    }
    public void deleteRoom(String roomId){
        SQLiteDatabase database=getWritableDatabase();
        try{
            database.delete(TABLE_NAME_CHAT,"ChatRoomId=?",new String[]{roomId});
            Log.i("Success","Delete room");
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, TAG+"Error in create room table: "+e.toString());
        }
    }

    public void deleteRoomFromTable(String roomId){
        SQLiteDatabase database=getWritableDatabase();
        try{
            database.delete(TABLE_NAME_ROOM,"id=?",new String[]{roomId});
            Log.i("Success","Delete room");
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, TAG+"Error in create room table: "+e.toString());
        }
    }

    public void createRoomTable(SQLiteDatabase database){
        Log.e("MydbHelper","create Room table");
        try{
            database.execSQL(   "CREATE TABLE "+ TABLE_NAME_ROOM+"("+
                    RID+" INTEGER PRIMARY KEY, "+
                    COLUMN_NAME_ID+" VARCHAR(30), "+
                    COLUMN_NAME_NAME+" VARCHAR(30), "+
                    COLUMN_NAME_PATH+" VARCHAR(100), "+
                    COLUMN_NAME_RESRTVE+" VARCHAR(100), "+
                    COLUMN_NAME_FIELD+" VARCHAR(100), "+
                   COLUMN_NAME_LOCATION+" VARCHAR(100), "+
                    COLUMN_NAME_STARTTIME+" VARCHAR(100), "+
                    COLUMN_NAME_ENDTIME+" VARCHAR(100) "+
                    ")" );
            Log.i("Success","Create room table");
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, TAG+"Error in create room table: "+e.toString());
        }
    }

    public void createChatTable(SQLiteDatabase database){
        Log.e("MydbHelper","create Chat table");
        try{
            database.execSQL(   "CREATE TABLE "+ TABLE_NAME_CHAT+"("+
                    CID+" INTEGER PRIMARY KEY, "+
                    COLUMN_NAME_UID+" VARCHAR(30), "+
                    COLUMN_NAME_CONTENT+" VARCHAR(100), "+
                    COLUMN_NAME_TIME+" VARCHAR(100), "+
                    COLUMN_NAME_ISSELF+" VARCHAR(100), "+
                    COLUMN_NAME_ISPICTURE+" VARCHAR(100), "+
                    COLUMN_NAME_PICTURE+" VARCHAR(100), "+
                    COLUMN_NAME_TIMESTAMP+" VARCHAR(100), "+
                    COLUMN_NAME_SHACODE+" VARCHAR(200), "+
                    COLUMN_NAME_AVATARID+" VARCHAR(100), "+
                    COLUMN_NAME_NICKNAME+" VARCHAR(100), "+
                    COLUMN_NAME_CHATROOMID+" VARCHAR(100), "+
                    "FOREIGN KEY ("+ COLUMN_NAME_CHATROOMID+") REFERENCES "+TABLE_NAME_ROOM+"("+COLUMN_NAME_NAME+")"+
                    ")" );
            Log.i("Success","Create chat table");
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, TAG+"Error in create chat table: "+e.toString());
        }
    }


    public void insertRoomData(ContentValues contentValues, SQLiteDatabase database){
        try{
            database.insert(TABLE_NAME_ROOM,"_id",contentValues);

        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG,"Error on insert room data: "+e.toString());
        }finally{
            if(null!=database){

            }
        }
    }

    public void insertTestingRoomData(SQLiteDatabase database){
       // SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(COLUMN_NAME_NAME, "name1");
        contentValues1.put(COLUMN_NAME_PATH, "path1");
        contentValues1.put(COLUMN_NAME_ID, "ID1");
        contentValues1.put(COLUMN_NAME_RESRTVE,"reserve1");
        contentValues1.put(COLUMN_NAME_FIELD,"field1");
        contentValues1.put(COLUMN_NAME_LOCATION,"location1");
        contentValues1.put(COLUMN_NAME_STARTTIME,"starttime1");
        contentValues1.put(COLUMN_NAME_ENDTIME, "endtime1");

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(COLUMN_NAME_NAME, "name2");
        contentValues2.put(COLUMN_NAME_PATH, "path1");
        contentValues2.put(COLUMN_NAME_ID, "ID2");
        contentValues2.put(COLUMN_NAME_RESRTVE,"reserve1");
        contentValues2.put(COLUMN_NAME_FIELD,"field1");
        contentValues2.put(COLUMN_NAME_LOCATION,"location1");
        contentValues2.put(COLUMN_NAME_STARTTIME,"starttime1");
        contentValues2.put(COLUMN_NAME_ENDTIME, "endtime1");

        ContentValues contentValues3 = new ContentValues();
        contentValues3.put(COLUMN_NAME_NAME, "name3");
        contentValues3.put(COLUMN_NAME_PATH, "path1");
        contentValues3.put(COLUMN_NAME_ID, "ID3");
        contentValues3.put(COLUMN_NAME_RESRTVE,"reserve1");
        contentValues3.put(COLUMN_NAME_FIELD,"field1");
        contentValues3.put(COLUMN_NAME_LOCATION,"location1");
        contentValues3.put(COLUMN_NAME_STARTTIME,"starttime1");
        contentValues3.put(COLUMN_NAME_ENDTIME, "endtime1");
        try{
            database.insert(TABLE_NAME_ROOM,"_id",contentValues1);
            database.insert(TABLE_NAME_ROOM,"_id",contentValues2);
            database.insert(TABLE_NAME_ROOM,"_id",contentValues3);

        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG,"Error on insert room data: "+e.toString());
        }finally{
            if(null!=database){

            }
        }
    }

    public void insertChatData(ContentValues contentValues, SQLiteDatabase database) {
        try {
            Log.e("MyDbHelper","insert chat data");
            database.insert(TABLE_NAME_CHAT, "_id", contentValues);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error on insert chat data: " + e.toString());
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    public void insertTestingChatData(SQLiteDatabase database) {
        //SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(COLUMN_NAME_CONTENT, "测试信息");
        contentValues1.put(COLUMN_NAME_ISSELF,1);
        contentValues1.put(COLUMN_NAME_ISPICTURE, 0);
        contentValues1.put(COLUMN_NAME_UID, "");
        contentValues1.put(COLUMN_NAME_PICTURE,"");
        contentValues1.put(COLUMN_NAME_TIMESTAMP, "");
        contentValues1.put(COLUMN_NAME_NICKNAME, "userName");
        contentValues1.put(COLUMN_NAME_CHATROOMID, "ID1");
        contentValues1.put(COLUMN_NAME_AVATARID, R.drawable.s01);
        try {
            Log.e("MyDbHelper","insert chat data");
            database.insert(TABLE_NAME_CHAT, "_id", contentValues1);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error on insert chat data: " + e.toString());
        } finally {
            if (null != database) {
               // database.close();
            }
        }
    }

    public String getPicPath(String shaCode) {
        SQLiteDatabase database=getReadableDatabase();
        String path="";
        try {
            Log.e("MyDbHelper","get pic path");
            Cursor cursor =database.query(TABLE_NAME_CHAT, null, "ShaCode=?", new String[]{shaCode}, null, null, null);
            while(cursor.moveToNext()){
                path=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PICTURE));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error on get pic path: " + e.toString());
        } finally {
            if (null != database) {
                database.close();
            }
        }
        return path;
    }


    public void deleteChatData(String code,String tablename){
        SQLiteDatabase database=getWritableDatabase();
        try{
            String deleteSql="delete from "+tablename+" where ShaCode=?";
            database.execSQL(deleteSql,new String[]{code});
            Log.e(TAG,"delete chat data, shacode is "+code);
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG,"Error on delete chat data: "+e.toString());
        }finally{
            if(null!=database){
                database.close();
            }
        }
    }

    private void initDb(SQLiteDatabase database){
        Log.e("MydbHelper","iniDb");
        createRoomTable(database);
        createChatTable(database);
      //  insertTestingRoomData(database);
      //  insertTestingChatData(database);
    }


        @Override
    public void onCreate(SQLiteDatabase db) {
        initDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CHAT);
        db.execSQL(SQL_DELETE_ROOM);
        onCreate(db);
    }
}
