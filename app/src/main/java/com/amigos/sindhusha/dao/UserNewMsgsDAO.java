package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by sindhusha on 10/4/17.
 */
public class UserNewMsgsDAO extends SQLiteOpenHelper {

    private String TAG="UserNewMsgsDAO";
    SQLiteDatabase usersDB;

    public static final String DATABASE_NAME = "user_new_msgs.db";
    public UserNewMsgsDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS user_new_msgs (user_id varchar,new_msg_status INTEGER ) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getCurrentUserStatus(String uuid) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user_new_msgs where user_id='"+uuid+"'", null );
        res.moveToFirst();

        int status=0;
        try{
            while(res.isAfterLast() == false){

                status=res.getInt(res.getColumnIndex("new_msg_status"));

                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return status;
    }


    public boolean changeUserNewMsgStatus (String uuid,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            //First delete
            String sql="delete from user_new_msgs where user_id=? ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, uuid);
            statement.execute();
            //Then add
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", uuid);
            contentValues.put("new_msg_status", status);
            db.insert("user_new_msgs", null, contentValues);
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
