package com.example.weahen.wstest;

import android.app.Application;
import android.content.Context;
import android.content.MutableContextWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.example.weahen.wstest.Config.DbConfig;
import com.example.weahen.wstest.db.DaoMaster;
import com.example.weahen.wstest.db.DaoSession;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.x;


public class MyApplication extends Application {
    public static Context mContext;
    private static DbManager.DaoConfig mDaoConfig;
    public static Application mApplication;
    private static DbManager sDbManager;

    private static  DaoMaster.DevOpenHelper helper;
    private static SQLiteDatabase db;
    private static  DaoMaster daoMaster;
    private static  DaoSession daoSession;
    private static MyApplication instances;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
        mContext = this;
        mApplication=this;
        //   setDatabase();
    }

    public static DbManager.DaoConfig getDaoConfig() {
        if (mDaoConfig == null) {
            mDaoConfig = new DbManager.DaoConfig()
                    .setDbName(DbConfig.DB_NAME)//
                    .setTableCreateListener(new DbManager.TableCreateListener() {
                        @Override
                        public void onTableCreated(DbManager db, TableEntity<?> table) {

                        }
                    })
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            db.getDatabase().enableWriteAheadLogging();
                        }
                    })
                    .setDbVersion(DbConfig.DB_VERSION)
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            // TODO: ...
                            // db.addColumn(...);
                            // db.dropTable(...);
                            // ...
                        }
                    });//数据库更新操作

        }
        return mDaoConfig;
    }

    public static DbManager getDbManager() {
        if (sDbManager == null) {
            sDbManager = x.getDb(getDaoConfig());
        }
        return sDbManager;
    }

    // 将获取到的Context进行返回
    public static Context getContext() {
        return mContext;
    }

//    public void setDatabase(){
//        helper = new DaoMaster.DevOpenHelper(this, "chat.db");
//        db = helper.getWritableDatabase();
//       daoMaster = new DaoMaster(db);
//        daoSession = daoMaster.newSession();
//    }
//
//    public static MyApplication getInstances(){
//        if(instances==null){
//            instances=new MyApplication();
//        }
//        return instances;
//    }
//    public DaoSession getDaoSession(){
//        return daoSession;
//    }

}
