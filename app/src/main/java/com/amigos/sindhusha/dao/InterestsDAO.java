package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.amigos.sindhusha.vo.InterestsVO;

import java.util.ArrayList;

/**
 * Created by Sachin on 8/14/2017.
 */

public class InterestsDAO extends SQLiteOpenHelper {

    private String TAG="InterestsDAO";
    SQLiteDatabase usersDB;

    public static final String DATABASE_NAME = "interests.db";
    public InterestsDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS my_interests (interest varchar,pref INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<InterestsVO> getAllInterests() {
        ArrayList<InterestsVO> interestsList = new ArrayList<InterestsVO>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from my_interests", null );
        res.moveToFirst();

        try {
            while (res.isAfterLast() == false) {
                int pref = res.getInt(res.getColumnIndex("pref"));
                String interest = res.getString(res.getColumnIndex("interest"));
                InterestsVO curInterest = new InterestsVO(interest, pref);
                interestsList.add(curInterest);
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return interestsList;
    }

    public boolean changePetInterest (String interest , int pref) {
        Log.i(TAG, "changePetInterest():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="UPDATE my_interests set pref = ? where interest = ? ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindDouble(1,pref);
            statement.bindString(2,interest);


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


    public boolean dropTable () {
        Log.i(TAG, "dropTable():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="DROP TABLE IF EXISTS my_interests;";
            SQLiteStatement statement=db.compileStatement(sql);
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



    public boolean addInterestsToDB (String interest, int pref) {
        Log.i(TAG, "deleteContact():: Entered");

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS my_interests (interest varchar,pref INTEGER)"
        );
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("interest", interest);
            contentValues.put("pref", pref);
            db.insert("my_interests", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public int getInterestPref(String interest){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from my_interests where interest in ('"+interest+"')", null );
        res.moveToFirst();
        int pref = 2;
        try {
            while (res.isAfterLast() == false) {
                pref = res.getInt(res.getColumnIndex("pref"));
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return pref;
    }

    public void clearTableData(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from my_interests");
    }
}
