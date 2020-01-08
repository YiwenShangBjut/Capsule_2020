package com.example.weahen.wstest.db;

import android.provider.BaseColumns;

public class DBContract {
    private DBContract(){}
    public static class RoomEntry implements BaseColumns{
        public static final String TABLE_NAME_ROOM="Room";
        public static final String RID="_id";
        public static final String COLUMN_NAME_ID="id";
        public static final String COLUMN_NAME_NAME="Name";
        public static final String COLUMN_NAME_PATH="Path";
        public static final String COLUMN_NAME_RESRTVE="Reserve";
        public static final String COLUMN_NAME_FIELD="Field";
        public static final String COLUMN_NAME_LOCATION="Location";
        public static final String COLUMN_NAME_STARTTIME="StartTime";
        public static final String COLUMN_NAME_ENDTIME="EndTime";
    }

    public static class ChatEntry implements BaseColumns{
        public static final String TABLE_NAME_CHAT="Chat";
        public static final String CID="_id";
        public static final String COLUMN_NAME_UID="Uid";
        public static final String COLUMN_NAME_CONTENT="Content";
        public static final String COLUMN_NAME_TIME="Time";
        public static final String COLUMN_NAME_ISSELF="IsSelf";
        public static final String COLUMN_NAME_PICTURE="Picture";
        public static final String COLUMN_NAME_SHACODE="ShaCode";
        public static final String COLUMN_NAME_TIMESTAMP="TimeStamp";
        public static final String COLUMN_NAME_ISPICTURE="IsPicture";
        public static final String COLUMN_NAME_AVATARID="AvatarId";
        public static final String COLUMN_NAME_NICKNAME="NickName";
        public static final String COLUMN_NAME_CHATROOMID="ChatRoomId";
    }
}
