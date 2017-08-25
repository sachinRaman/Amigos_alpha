package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.amigos.sindhusha.vo.TopicInfo;


import java.util.ArrayList;

/**
 * Created by sindhusha on 10/4/17.
 */
public class UserPrefsDAO  extends SQLiteOpenHelper {

    private String TAG="UserPrefsDAO";
    SQLiteDatabase usersDB;

    public static final String DATABASE_NAME = "userprefs.db";
    public UserPrefsDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS user_prefs (topic_id INTEGER,topic_name varchar,topic_desc varchar,id INTEGER PRIMARY KEY) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<TopicInfo> getAllPrefs() {
        ArrayList<TopicInfo> prefList = new ArrayList<TopicInfo>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user_prefs", null );
        res.moveToFirst();

        try {
            while (res.isAfterLast() == false) {
                int topicId = res.getInt(res.getColumnIndex("topic_id"));
                String topicName = res.getString(res.getColumnIndex("topic_name"));
                String topicDesc = res.getString(res.getColumnIndex("topic_desc"));
                TopicInfo curPref = new TopicInfo(topicId, topicName, topicDesc);
                prefList.add(curPref);
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return prefList;
    }


    public boolean addPref (int topicId,String topicName, String topicDesc) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("topic_id", topicId);
            contentValues.put("topic_name", topicName);
            contentValues.put("topic_desc", topicDesc);

            db.insert("user_prefs", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public boolean removePrefFromName (String topicName) {
        Log.i(TAG, "deleteContact():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="DELETE FROM user_prefs where topic_name=? ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, "" + topicName);

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

    public TopicInfo getPrefFromTopicName(String topicName) {
        Log.i(TAG,"getPrefFromId()::START");
        ArrayList<TopicInfo> prefList = new ArrayList<TopicInfo>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user_prefs where topic_name='"+topicName+"'", null );
        res.moveToFirst();

        TopicInfo userPref=null;
        try{
            if(res.isAfterLast() == false){

                Log.i(TAG,"getPrefFromId()::Cur ID found");
                int topicId=res.getInt(res.getColumnIndex("topic_id"));
                String topicDesc=res.getString(res.getColumnIndex("topic_desc"));
                userPref=new TopicInfo(topicId,topicName,topicDesc);
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return userPref;
    }


}
