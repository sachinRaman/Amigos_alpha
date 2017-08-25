package com.amigos.sindhusha.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.amigos.sindhusha.vo.PetPeevesVO;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Sachin on 7/18/2017.
 */

public class PetPeevesDAO extends SQLiteOpenHelper {
    private String TAG="PetPeevesDAO";
    SQLiteDatabase usersDB;

    public static final String DATABASE_NAME = "petpeeves.db";
    public PetPeevesDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS pet_peeves (peeve varchar,pref INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<PetPeevesVO> getAllPeeves() {
        ArrayList<PetPeevesVO> peevesList = new ArrayList<PetPeevesVO>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pet_peeves", null );
        res.moveToFirst();

        try {
            while (res.isAfterLast() == false) {
                int pref = res.getInt(res.getColumnIndex("pref"));
                String peeve = res.getString(res.getColumnIndex("peeve"));
                PetPeevesVO curPeeve = new PetPeevesVO(peeve, pref);
                peevesList.add(curPeeve);
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return peevesList;
    }

    public boolean changePetPeeve (String peeve , int pref) {
        Log.i(TAG, "changePetPeeve():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="UPDATE pet_peeves set pref = ? where peeve = ? ";
            SQLiteStatement statement=db.compileStatement(sql);
            //statement.bindString(1,Integer.toString(pref));
            statement.bindDouble(1,pref);
            statement.bindString(2,peeve);


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
            String sql="DROP TABLE IF EXISTS pet_peeves;";
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



    public boolean addPeevesToDB (String peeve, int pref) {
        Log.i(TAG, "deleteContact():: Entered");

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS pet_peeves (peeve varchar,pref INTEGER)"
        );
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("peeve", peeve);
            contentValues.put("pref", pref);
            db.insert("pet_peeves", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public int getPeevePref(String peeve){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pet_peeves where peeve in ('"+peeve+"')", null );
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
}
