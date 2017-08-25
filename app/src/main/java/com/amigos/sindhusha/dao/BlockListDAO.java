package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sachin on 6/21/2017.
 */

public class BlockListDAO extends SQLiteOpenHelper {

    private String TAG="BlockListDAO";
    SQLiteDatabase usersDB;


    Context context;
    public static final String DATABASE_NAME = "block_list.db";

    public BlockListDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS block_list (to_id varchar,from_id varchar) "
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Set<String> getBlockUsers(String fromId) {
        Set<String> blockUsers = new HashSet<String>();

        if(fromId == null){
            fromId = "null";
        }




        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from block_list where from_id in ("+fromId+") ",null);

        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){
                String toId=res.getString(res.getColumnIndex("to_id"));
                blockUsers.add(toId);
                res.moveToNext();
            }
        }finally{

            res.close();
            db.close();
        }

        return blockUsers;
    }

    public Set<String> getFromBlockUsers(String toId) {
        final Set<String> blockUsers = new HashSet<String>();
        SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");
        Firebase blockListRef = new Firebase("https://amigos-d1502.firebaseio.com/block_list");

        blockListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blockUsers.clear();
                for (DataSnapshot curuserShot : dataSnapshot.getChildren()) {
                    if(curuserShot.getKey().contains(senderUuid)) {
                        String receiverId = curuserShot.getKey().toString().replace(senderUuid, "");
                        receiverId = receiverId.replace("-", "");
                        blockUsers.add(receiverId);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        if(toId == null){
            toId = "null";
        }




        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from block_list where to_id in ("+toId+") ",null);

        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){
                String id=res.getString(res.getColumnIndex("from_id"));
                blockUsers.add(id);
                res.moveToNext();
            }
        }finally{

            res.close();
            db.close();
        }

        return blockUsers;
    }

    public HashMap<String,String> getAllBlockUsers() {
        HashMap<String,String> blockUsers = new HashMap<String,String>();

        SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from block_list",null);


        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                String toId=res.getString(res.getColumnIndex("to_id"));
                String fromId=res.getString(res.getColumnIndex("from_id"));
                blockUsers.put(toId,fromId);
                res.moveToNext();
            }
        }finally{
            res.close();
            db.close();
        }

        return blockUsers;
    }

    public boolean addUserToBLock(String toId,String fromId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String sql="delete from block_list where to_id in (?) and from_id in (?) ";
        SQLiteStatement statement=db.compileStatement(sql);
        statement.bindString(1, toId);
        statement.bindString(2, fromId);
        statement.execute();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("to_id", toId);
            contentValues.put("from_id", fromId);
            db.insert("block_list", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public void removeUserFromBlock(String toId) {



        SQLiteDatabase db = this.getWritableDatabase();
        try{

            SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
            final String senderUuid=sp.getString("uuid", "");

            String sql="delete from block_list where to_id in (?) and from_id in (?) ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, toId);
            statement.bindString(2, senderUuid);
            statement.execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally {
            db.close();
        }

    }

    public void clearTableData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from block_list");
        return;
    }

}
