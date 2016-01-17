package com.zhou.MyBrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    private static int VERSION = 1;
    //创建构造函数
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public MyDBHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }
    public MyDBHelper(Context context, String name) {
        this(context, name,VERSION);
    }

    //初始化数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建用户表格
        db.execSQL("create table user(name CHAR(20), password CHAR(20))");
        //设定当前用户
        ContentValues contentValues = new ContentValues();
        contentValues.put("name","currentUser");
        contentValues.put("password","guest");
        db.insert("user",null,contentValues);
        //创建游客用户
        contentValues = new ContentValues();
        contentValues.put("name","guest");
        contentValues.put("password","guest");
        db.insert("user",null,contentValues);

        //创建游客收藏夹表格
        db.execSQL("create table guest(URLName VARCHAR(20), URL VARCHAR(100))");
        //添加默认收藏
        contentValues = new ContentValues();
        contentValues.put("URLName","百度");
        contentValues.put("URL","http://www.baidu.com");
        db.insert("guest",null,contentValues);

        contentValues = new ContentValues();
        contentValues.put("URLName","QQ");
        contentValues.put("URL","http://www.qq.com");
        db.insert("guest",null,contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
