/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.amigos.sindhusha.activity;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.service.ChatService;
import com.amigos.sindhusha.slidingtabsbasic.SlidingTabsBasicFragment;
import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends FragmentActivity {

    public static final String TAG = "MainActivity";


    private Firebase users_FR;

    public static ProgressDialog progressDioalog;
    public static StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar_main_activity);
        getActionBar().getDisplayOptions();


        Firebase.setAndroidContext(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Log.i(TAG, "onCreate()::START");




        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        progressDioalog = new ProgressDialog(MainActivity.this);
        progressDioalog.setMessage("loading....");
        progressDioalog.setTitle(" Users List");
        progressDioalog.show();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }



        users_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/" );
        String id = getRegisterPhoneUUID();
        String uuid;
        if(id != null) {
            uuid = getRegisterPhoneUUID();
        }else{
            uuid = "123456789";
        }
        Firebase curUser_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/" );
        curUser_FR.child("status").setValue("1");
        ActionBarTitleGravity();

        SharedPreferences settings = getSharedPreferences(PreferenceTags.PREFS_NAME, 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        if(!hasLoggedIn){

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

 }

    private void ActionBarTitleGravity() {
        // TODO Auto-generated method stub
        ActionBar actionbar;
        TextView textview;
        RelativeLayout.LayoutParams layoutparams;
        actionbar = getActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));

        textview = new TextView(getApplicationContext());

        layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        textview.setLayoutParams(layoutparams);

        textview.setText("Amigos");

        //chatbubbleleft.setTextColor(Color.BLACK);

        textview.setTextColor(getResources().getColor(R.color.actionBarTextColor));

        textview.setGravity(Gravity.CENTER);

        textview.setTextSize(20);

        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);


        actionbar.setCustomView(textview);

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void CreateActionBar()
    {

        ActionBar actionbar = getActionBar();

        GradientDrawable gradientdrawable = new GradientDrawable();

        gradientdrawable.setColors(new int[]{



                Color.parseColor("#21402C"),
                Color.parseColor("#5A9A7C")

        });

        gradientdrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        gradientdrawable.setShape(GradientDrawable.RECTANGLE);

        actionbar.setBackgroundDrawable(gradientdrawable);

    }
    @Override
    public void onResume(){
        super.onResume();
        //Start ChatService to run in background START
        if(isMyServiceRunning(ChatService.class)==false) {
            Intent chatServiceIntent = new Intent(this, ChatService.class);
            startService(chatServiceIntent);
        }
        //Start ChatService to run in background END
    }
    private String getRegisterPhoneUUID(){
        SharedPreferences sp=this.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        String uuid="";
        try {
            if (sp.getString("uuid", "") == null || sp.getString("uuid", "").length() == 0) {

                TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                uuid = tManager.getDeviceId();
                sp.edit().putString("uuid", "" + uuid).apply();

                Firebase curUser_FR = new Firebase("https://amigos-d1502.firebaseio.com/users/" + uuid + "/");
                //curUser_FR.child("nickname").setValue("Nick-Name"+uuid.substring(uuid.length()-3));
                curUser_FR.child("nickname").setValue("");

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        uuid=sp.getString("uuid", "");
        return uuid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }


    public void onClickOfProfile(View view){
        Intent profileUserInfoActivity = new Intent(getApplicationContext(), ProfileUserInfoActivity.class);
        profileUserInfoActivity.putExtra("uuid",view.getTag().toString());
        startActivity(profileUserInfoActivity);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsActivity);
            return true;
            /*case R.id.action_topic:
                Intent topicPrefActivity = new Intent(getApplicationContext(), TopicPrefActivity.class);
                startActivity(topicPrefActivity);
            return true;*/
            /*case R.id.pref_tags:
                Intent topicTagsActivity = new Intent(getApplicationContext(), TopicTagsActivity.class);
                startActivity(topicTagsActivity);
            return true;*/
            case R.id.pref_tags_new:
                Intent preferenceTagsActivity = new Intent(getApplicationContext(), PreferenceTags.class);
                startActivity(preferenceTagsActivity);
                return true;
            case R.id.action_archive_list:
                Intent archiveChatActivity = new Intent(getApplicationContext(), ArchiveChatActivity.class);
                startActivity(archiveChatActivity);
            return true;
            case R.id.action_block_list:
                Intent blockListActivity = new Intent(getApplicationContext(), BlockListActivity.class);
                startActivity(blockListActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        Log.i(TAG,"isMyServiceRunning():: Entered");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(TAG,"isMyServiceRunning():: TRUE-Service running");
                return true;
            }
        }
        Log.i(TAG, "isMyServiceRunning():: FALSE-Service NOT-running");
        return false;
    }

    /*Runnable run = new Runnable() {
        @Override public void run() {
            PutImagesToCache putImagesToCache = new PutImagesToCache(getApplicationContext());
            putImagesToCache.getAllUsers();
        }
    };*/
}
