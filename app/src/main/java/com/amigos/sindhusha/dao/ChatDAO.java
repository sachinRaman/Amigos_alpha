package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.amigos.sindhusha.vo.ChatMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sindhusha on 10/4/17.
 */
public class ChatDAO extends SQLiteOpenHelper {

    private String TAG="ChatDAO";
    SQLiteDatabase usersDB;


    Context context;
    public static final String DATABASE_NAME = "chats.db";


    public ChatDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS chats (to_id varchar,from_id varchar,message varchar) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<ChatMessage> getAllMessages(String opponentId) {
        ArrayList<ChatMessage> chatsList = new ArrayList<ChatMessage>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from chats where to_id in('"+opponentId+"'"+") OR from_id in('"+opponentId+"'"+")", null );


        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                String toId=res.getString(res.getColumnIndex("to_id"));
                String fromId=res.getString(res.getColumnIndex("from_id"));
                String message=res.getString(res.getColumnIndex("message"));
                ChatMessage chatMessage=new ChatMessage(toId,fromId,message);
                chatsList.add(chatMessage);
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return chatsList;
    }

    public Set<String>  getDistinctChatUsers() {
        Set<String> distinctUsers = new HashSet<String>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from chats ",null);


        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                String toId=res.getString(res.getColumnIndex("to_id"));
                String fromId=res.getString(res.getColumnIndex("from_id"));
                distinctUsers.add(toId);
                distinctUsers.add(fromId);
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

    public boolean addChat (String toId,String fromId,String message) {
        Log.i(TAG," ChatDAO::addChat msg::"+message);

        if(message==null||message.length()==0)
            return true;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("to_id", toId);
            contentValues.put("from_id", fromId);
            contentValues.put("message", message);


            db.insert("chats", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }


    public void delUserChat(String opponentId) {



        SQLiteDatabase db = this.getWritableDatabase();
        try{


            String sql="delete from chats where to_id in (?) or from_id in (?) ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, opponentId);
            statement.bindString(2, opponentId);
            statement.execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally {
            db.close();
        }

    }


}
