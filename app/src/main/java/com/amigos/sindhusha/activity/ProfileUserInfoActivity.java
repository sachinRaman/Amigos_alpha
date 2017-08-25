package com.amigos.sindhusha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.dao.ArchiveListDAO;
import com.amigos.sindhusha.dao.ImageCacheDAO;
import com.amigos.sindhusha.util.FileCache;
import com.amigos.sindhusha.util.ImageLoader;
import com.amigos.sindhusha.util.MemoryCache;
import com.amigos.sindhusha.util.RoundedImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

public class ProfileUserInfoActivity extends Activity {

    private String TAG="ProfileUserInfoActivity";
    TextView tv_display_name;
    //TextView tv_topics_of_interest;
    TextView tv_info;
    TextView tv_extra_info;
    TextView aboutMe;
    RoundedImageView imageView;
    ImageView profileImage;
    ImageView tickIcon;
    ImageView crossIcon;
    static FileCache fileCache;

    int matchCount = 0;

    MemoryCache memoryCache=new MemoryCache();

    private ImageLoader imgLoader;
    ArrayList<Tag> tags = new ArrayList<Tag>();
    TagView tagGroup;
    //TextView tvMatchCount;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user_info);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        fileCache=new FileCache(getApplicationContext());

        Typeface typeFaceCalibri = Typeface.createFromAsset(getAssets(),"fonts/Calibri/Calibri.ttf");
        Typeface typeFaceCalibriBold = Typeface.createFromAsset(getAssets(),"fonts/Calibri/CALIBRIB.TTF");

        tv_display_name=(TextView)findViewById(R.id.tv_display_name);
        tv_display_name.setTypeface(typeFaceCalibriBold);

        tv_info = (TextView)findViewById(R.id.textView_info);
        tv_info.setTypeface(typeFaceCalibri);

        aboutMe = (TextView)findViewById(R.id.textView_aboutMe);
        aboutMe.setTypeface(typeFaceCalibri);

        tv_extra_info = (TextView)findViewById(R.id.tv_extra_info);
        tv_extra_info.setTypeface(typeFaceCalibri);

        tagGroup = (TagView)findViewById(R.id.user_tag_group);
        //tvMatchCount = (TextView)findViewById(R.id.tv_user_match_count);

        profileImage = (ImageView)findViewById(R.id.imageView_profileImage);

        final String[] nickname = new String[1];
        final String[] ageStr = new String[1];
        final String[] sexStr = new String[1];
        final String[] placeStr = new String[1];
        final String[] aboutMeStr = new String[1];
        ageStr[0] = "";
        sexStr[0] = "";
        placeStr[0] = "";
        aboutMeStr[0] = "";

        final List<String> myTopicList=new ArrayList<String>();

        final ArrayList<String> myInterests = new ArrayList<String>();

        SharedPreferences sp=getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");
        Firebase thisUsers= new Firebase("https://amigos-d1502.firebaseio.com/users/"+senderUuid+"/" );

        thisUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if(snap.getKey().equalsIgnoreCase("nickname")){
                        //code
                    }
                    if(snap.getKey().equalsIgnoreCase("info")){
                        //code
                    }
                    /*if (snap.getKey().equalsIgnoreCase("topics_prefs")) {
                        HashMap<String, Integer> mytopics = new HashMap<String, Integer>();
                        mytopics = snap.getValue(HashMap.class);


                        for (String topic : mytopics.keySet()) {
                            if(mytopics.get(topic) == 1){
                                myTopicList.add(topic);
                            }
                        }
                    }*/

                    if(snap.getKey().equalsIgnoreCase("interests_list")){
                        myInterests.clear();
                        for (DataSnapshot interests : snap.getChildren()){
                            String key = interests.getKey();
                            Map<String, String> topicInterests = interests.getValue(Map.class);
                            for (String s: topicInterests.keySet()){
                                if("1".equalsIgnoreCase(topicInterests.get(s))){
                                    myInterests.add(s);
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Firebase.setAndroidContext(this);
        Intent intent = getIntent();
        final String uuid = intent.getStringExtra("uuid");

        final ArrayList<String> userInterests = new ArrayList<String>();

        //loadImageViaImageLoader(profileImage,uuid);
        final ImageCacheDAO imageCacheDAO = new ImageCacheDAO(getApplicationContext());

        if("".equalsIgnoreCase(imageCacheDAO.getUserUrl(uuid))){
            Glide.with(getApplicationContext()).load(R.drawable.ic_user).into(profileImage);
        }else{
            Glide.with(getApplicationContext()).load(imageCacheDAO.getUserUrl(uuid))
                    .bitmapTransform(new CropSquareTransformation(getApplicationContext()))
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profileImage);
        }

        Firebase allUsers_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/" );
        allUsers_FR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.i(TAG, "*****" + child.getKey() + "::" + child.getValue());
                    if(child.getKey().equalsIgnoreCase("nickname")){
                        if(child.getValue().toString()!= null && !child.getValue().toString().isEmpty()) {
                            tv_display_name.setText(child.getValue().toString());
                            nickname[0] = child.getValue().toString();
                        }else{
                            tv_display_name.setText("");
                            nickname[0] = "";
                        }
                    }
                    if(child.getKey().equalsIgnoreCase("info")){
                        tv_info.setText(child.getValue().toString());
                    }
                    if(child.getKey().equalsIgnoreCase("age")){
                        ageStr[0] = child.getValue().toString();
                    }
                    if(child.getKey().equalsIgnoreCase("sex")){
                        sexStr[0] = child.getValue().toString();
                    }
                    if(child.getKey().equalsIgnoreCase("place")){
                        placeStr[0] = child.getValue().toString();
                    }
                    if(child.getKey().equalsIgnoreCase("aboutMe")){
                        aboutMeStr[0] = child.getValue().toString();
                        aboutMe.setText(aboutMeStr[0]);
                    }
                    String finalInfo = "";
                    if(!ageStr[0].isEmpty()){
                        finalInfo += ageStr[0]+" / ";
                    }
                    if(!sexStr[0].isEmpty()){
                        finalInfo += sexStr[0]+" / ";
                    }
                    if(!placeStr[0].isEmpty()){
                        finalInfo += placeStr[0];
                    }else if(!finalInfo.isEmpty()){
                        finalInfo = finalInfo.substring(0, finalInfo.length() - 3);
                    }
                    if(child.getKey().equalsIgnoreCase("interests_list")){
                        userInterests.clear();
                        tags.clear();
                        for (DataSnapshot interests : child.getChildren()){
                            String key = interests.getKey();
                            Map<String, String> topicInterests = interests.getValue(Map.class);
                            for (String s: topicInterests.keySet()){
                                if("1".equalsIgnoreCase(topicInterests.get(s))){
                                    userInterests.add(s);
                                }
                            }
                        }

                        int match = 0;

                        for(String s : userInterests){
                            if(myInterests.contains(s)){
                                Tag tag = new Tag(s);
                                tag.tagTextColor = Color.parseColor("#F4514E");
                                tag.layoutBorderColor = Color.parseColor("#F4514E");
                                tag.layoutColor = Color.parseColor("#FFFFFF");
                                tag.layoutBorderSize = 1.0F;
                                tag.layoutColorPress = Color.WHITE;
                                tags.add(tag);
                                match++;
                            }
                        }
                        if(myInterests.isEmpty()){
                            matchCount = 0;
                        }else{
                            matchCount = Math.round(((float)match/(myInterests.size()))*100);
                        }
                        String matchShow = Integer.toString(matchCount)+"% CLICK";
                        //tvMatchCount.setText(matchShow);
                        tagGroup.addTags(tags);
                    }

                    tv_extra_info.setText(finalInfo);
                    /*if(child.getKey().equalsIgnoreCase("topics_prefs")){
                        HashMap<String,Integer> topics=new HashMap<String, Integer>();
                        topics=child.getValue(HashMap.class);
                        tags.clear();
                        for(String t:topics.keySet()){
                            if (topics.get(t) == 1){
                                if (myTopicList.contains(t) ){
                                    Tag tag = new Tag(t);
                                    tag.tagTextColor = Color.parseColor("#F4514E");
                                    tag.layoutBorderColor = Color.parseColor("#F4514E");
                                    tag.layoutColor = Color.parseColor("#FFFFFF");
                                    tag.layoutBorderSize = 1.0F;
                                    tag.layoutColorPress = Color.WHITE;
                                    tags.add(tag);
                                }else{
                                    Tag tag = new Tag(t);
                                    tag.tagTextColor = Color.GRAY;
                                    tag.layoutBorderColor = Color.GRAY;
                                    tag.layoutColor = Color.parseColor("#FFFFFF");
                                    tag.layoutBorderSize = 1.0F;
                                    tag.layoutColorPress = Color.WHITE;
                                    tags.add(tag);
                                }
                            }

                        }

                        tagGroup.addTags(tags);


                    }*/
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        tickIcon = (ImageView)findViewById(R.id.tickIcon);
        tickIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ProfileUserInfoActivity.this, ChatActivity.class);
                intent.putExtra("receiverId", uuid);
                intent.putExtra("userName", nickname[0]);
                startActivity(intent);

            }
        });
        crossIcon = (ImageView)findViewById(R.id.crossIcon);
        crossIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArchiveListDAO archiveListDAO = new ArchiveListDAO(getApplicationContext());
                archiveListDAO.addUserToArchive(uuid,senderUuid);

                Toast.makeText(ProfileUserInfoActivity.this,"You have put "+ nickname[0]+" on hold.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(ProfileUserInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadImageViaImageLoader(final ImageView profileImage, final String uuid) {
        MemoryCache memoryCache = new MemoryCache();
        final ImageCacheDAO imageCacheDAO = new ImageCacheDAO(getApplicationContext());
        String url = "";
        url = imageCacheDAO.getUserUrl(uuid);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap == null) {
            File f = fileCache.getFile(url);
            bitmap = ImageLoader.decodeFile(f);
        }
        if (bitmap != null) {
            bitmap = ImageLoader.getSquareCroppedBitmap(bitmap);
            profileImage.setImageBitmap(bitmap);
        } else {
            imgLoader = new ImageLoader(getApplicationContext());
            imgLoader.flag = 2;
            imgLoader.DisplayImage(url, profileImage, uuid);
        }
    }

}
