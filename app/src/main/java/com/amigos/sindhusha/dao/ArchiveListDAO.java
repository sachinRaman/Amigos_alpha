package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sachin on 6/19/2017.
 */

public class ArchiveListDAO extends SQLiteOpenHelper {

    private String TAG="ArchiveListDAO";
    SQLiteDatabase usersDB;


    Context context;
    public static final String DATABASE_NAME = "archive_list.db";


    public ArchiveListDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS archive_list (to_id varchar,from_id varchar) "
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Set<String> getArchiveUsers(String fromId) {
        Set<String> archiveUsers = new HashSet<String>();

        if(fromId == null){
            fromId = "null";
        }




        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from archive_list where from_id in ("+fromId+") ",null);

        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){
                String toId=res.getString(res.getColumnIndex("to_id"));
                archiveUsers.add(toId);
                res.moveToNext();
            }
        }finally{

            res.close();
            db.close();
        }
        //archiveUsers.add(fromId);
        return archiveUsers;
    }

    public Set<String> getAllArchiveUsers() {
        Set<String> archiveUsers = new HashSet<String>();

        SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from archive_list",null);


        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                String toId=res.getString(res.getColumnIndex("to_id"));
                String fromId=res.getString(res.getColumnIndex("from_id"));
                archiveUsers.add(toId);
                archiveUsers.add(fromId);
                res.moveToNext();
            }
        }finally{
            res.close();
            db.close();
        }

        return archiveUsers;
    }

    public boolean addUserToArchive(String toId,String fromId) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("to_id", toId);
            contentValues.put("from_id", fromId);
            db.insert("archive_list", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public void removeUserFromArchive(String toId,String fromId) {



        SQLiteDatabase db = this.getWritableDatabase();
        try{

            String sql="delete from archive_list where to_id in (?) and from_id in (?) ";
            String sql1="delete from archive_list where to_id in (?) and from_id in (?) ";
            //String sql="delete from archive_list WHERE EXISTS(Select * from archive_list where to_id in ("+toId+") and from_id in ("+fromId+") )";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, toId);
            statement.bindString(2, fromId);
            statement.execute();
            SQLiteStatement statement1=db.compileStatement(sql1);
            statement1.bindString(1, fromId);
            statement1.bindString(2, toId);
            statement1.execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally {
            db.close();
        }

    }

}
