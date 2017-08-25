package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Sachin on 8/5/2017.
 */

public class ImageCacheDAO extends SQLiteOpenHelper {

    private String TAG="ImageCacheDAO";
    SQLiteDatabase usersDB;
    Context context;
    public static final String DATABASE_NAME = "imageCache.db";

    public ImageCacheDAO(Context context) {
        super(context,DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS uuid_url_map (uuid varchar,url varchar)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addUser(String uuid, String url){
        Log.i(TAG," ImageCacheDAO::addUser");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("uuid", uuid);
            contentValues.put("url", url);
            db.insert("uuid_url_map", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public boolean userExists(String uuid){
        Log.i(TAG," ImageCacheDAO::userExists");
        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = false;
        Cursor res =  db.rawQuery( "select * from uuid_url_map where uuid = '"+uuid+"'", null );
        res.moveToFirst();
        try {
            while (res.isAfterLast() == false) {
                flag = true;
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return flag;
    }

    public String getUserUrl(String uuid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from uuid_url_map where uuid = '"+uuid+"'", null );
        String url = "";
        res.moveToFirst();
        try {
            while (res.isAfterLast() == false) {
                url = res.getString(res.getColumnIndex("url"));
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return url;
    }

    public boolean updateUrl(String uuid, String url){
        Log.i(TAG, "updateUrl():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="UPDATE uuid_url_map set url = ? where uuid = ? ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1,url);
            statement.bindString(2,uuid);
            statement.execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }
}
