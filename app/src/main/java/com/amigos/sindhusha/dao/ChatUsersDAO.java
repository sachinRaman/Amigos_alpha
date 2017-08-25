package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sachin on 7/8/2017.
 */

public class ChatUsersDAO extends SQLiteOpenHelper {
    private String TAG="ChatUsersDAO";
    SQLiteDatabase usersDB;
    Context context;
    public static final String DATABASE_NAME = "chatUsersNew.db";


    public ChatUsersDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS chat_users (to_id varchar,from_id varchar,time varchar, last_message varchar) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE chat_users ADD COLUMN last_message varchar DEFAULT ''");
        }
    }


    public boolean addToChatList(String toId, String fromId, String lastMessage){
        Log.i(TAG," ChatDAO::addChat");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="delete from chat_users where to_id in (?) and from_id in (?) ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, toId);
            statement.bindString(2, fromId);
            statement.execute();

            String sql1="delete from chat_users where to_id in (?) and from_id in (?) ";
            SQLiteStatement statement1=db.compileStatement(sql1);
            statement.bindString(1, fromId);
            statement.bindString(2, toId);
            statement1.execute();

            ContentValues contentValues = new ContentValues();
            contentValues.put("to_id", toId);
            contentValues.put("from_id", fromId);
            contentValues.put("time",timeStamp);
            contentValues.put("last_message", lastMessage);

            ContentValues contentValues1 = new ContentValues();
            contentValues1.put("to_id", fromId);
            contentValues1.put("from_id", toId);
            contentValues1.put("time",timeStamp);
            contentValues1.put("last_message", lastMessage);


            db.insert("chat_users", null, contentValues);
            db.insert("chat_users", null, contentValues1);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public Set<String> getMyChatList(String myId){
        Set<String> distinctUsers = new HashSet<String>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from chat_users where from_id in('"+myId+"'"+") ",null);


        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                String toId=res.getString(res.getColumnIndex("to_id"));
                distinctUsers.add(toId);
                res.moveToNext();
            }
        }finally{
            res.close();
            db.close();
        }
        SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");
        distinctUsers.remove(senderUuid);
        return distinctUsers;
    }

    public Set<String> getAllChatList(String myId){
        Set<String> distinctUsers = new HashSet<String>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from chat_users",null);


        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                String toId=res.getString(res.getColumnIndex("to_id"));
                distinctUsers.add(toId);
                res.moveToNext();
            }
        }finally{
            res.close();
            db.close();
        }
        SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");
        distinctUsers.remove(senderUuid);
        return distinctUsers;
    }

    public String getTimeStamp(String receiverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select time from chat_users where to_id in('" + receiverId + "')", null);

        String timeStamp = "";
        try {
            res.moveToFirst();
            while (res.isAfterLast() == false) {

                timeStamp = res.getString(res.getColumnIndex("time"));
                res.moveToNext();
            }
        } finally {
            res.close();
            db.close();
        }
        return timeStamp;
    }

    public String getLastMessage(String receiverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String msg = "";
        try {
            Cursor res = db.rawQuery("select last_message from chat_users where to_id in('" + receiverId + "')", null);

            try {

                res.moveToFirst();
                while (res.isAfterLast() == false) {

                    msg = res.getString(res.getColumnIndex("last_message"));
                    res.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                res.close();
                db.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return msg;
    }
}
