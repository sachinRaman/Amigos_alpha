package com.amigos.sindhusha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.Values.PeevesList;
import com.amigos.sindhusha.dao.InterestsDAO;
import com.amigos.sindhusha.vo.InterestsVO;
import com.firebase.client.Firebase;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class PreferenceTags extends Activity {

    String TAG = "TopicTagsActivity";

    String uuid = "";
    boolean firstTimeLoad;

    Button interestsButton;
    public static final String PREFS_NAME = "MyPrefsFile";
    String s = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_tags);

        firstTimeLogin();


        TagContainerLayout mTagContainerLayoutLifestyle = (TagContainerLayout) findViewById(R.id.tagPrefContainerLifeStyle);
        TagContainerLayout mTagContainerLayoutArts = (TagContainerLayout) findViewById(R.id.tagPrefContainerArts);
        TagContainerLayout mTagContainerLayoutEntertainment = (TagContainerLayout) findViewById(R.id.tagPrefContainerEntertainment);
        TagContainerLayout mTagContainerLayoutBusiness = (TagContainerLayout) findViewById(R.id.tagPrefContainerBusiness);
        TagContainerLayout mTagContainerLayoutSports = (TagContainerLayout) findViewById(R.id.tagPrefContainerSports);
        TagContainerLayout mTagContainerLayoutMusic = (TagContainerLayout) findViewById(R.id.tagPrefContainerMusic);
        TagContainerLayout mTagContainerLayoutTechnology = (TagContainerLayout) findViewById(R.id.tagPrefContainerTechnology);


        SharedPreferences sp=getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        uuid=sp.getString("uuid", "");


        ArrayList<String> lifestyle = PeevesList.getAllLifestyleInterests();
        ArrayList<String> arts = PeevesList.getAllArtsInterests();
        ArrayList<String> entertainment = PeevesList.getAllEntertainmentInterests();
        ArrayList<String> business = PeevesList.getAllBusinessInterests();
        ArrayList<String> sports = PeevesList.getAllSportsInterests();
        ArrayList<String> music = PeevesList.getAllMusicInterests();
        ArrayList<String> technology = PeevesList.getAllTechnologyInterests();


        InterestsDAO interestsDAO = new InterestsDAO(getApplicationContext());
        // interestsDAO.clearTableData();
        ArrayList<InterestsVO> interestsArrList = interestsDAO.getAllInterests();
        if (interestsArrList.isEmpty()){
            firstTimeLoad = true;
        }


        createInterestTags(mTagContainerLayoutLifestyle,lifestyle, "lifestyle");
        createInterestTags(mTagContainerLayoutArts,arts, "arts");
        createInterestTags(mTagContainerLayoutEntertainment,entertainment, "entertainment");
        createInterestTags(mTagContainerLayoutBusiness,business, "business");
        createInterestTags(mTagContainerLayoutSports,sports, "sports");
        createInterestTags(mTagContainerLayoutMusic,music, "music");
        createInterestTags(mTagContainerLayoutTechnology,technology, "technology");

        interestsButton = (Button) findViewById(R.id.buttonInterests);
        interestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreferenceTags.this,MainActivity.class);
                startActivity(intent);
            }
        });



    }

    private void createInterestTags(final TagContainerLayout mTagContainerLayout,ArrayList<String> tags, String topic) {

        mTagContainerLayout.setRippleDuration(100);
        mTagContainerLayout.setTags(tags);
        mTagContainerLayout.setBackgroundColor(Color.WHITE);
        mTagContainerLayout.setVerticalInterval(5.0f);
        mTagContainerLayout.setHorizontalInterval(5.0f);
        mTagContainerLayout.setBorderColor(Color.WHITE);


        updateTagColors(mTagContainerLayout,tags,topic);

        final InterestsDAO interestsDAO = new InterestsDAO(getApplicationContext());
        final Firebase usersInterestsRef = new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/interests_list/"+topic+"/");

        mTagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagView tag = mTagContainerLayout.getTagView(position);
                if(interestsDAO.getInterestPref(text) == 0){

                    usersInterestsRef.child(tag.getText()).setValue("1");
                    interestsDAO.changePetInterest(text,1);
                    tag.setTagBackgroundColor(Color.WHITE);
                    tag.setTagTextColor(Color.parseColor("#F4514E"));
                    tag.setTagBorderColor(Color.parseColor("#F4514E"));

                }else if(interestsDAO.getInterestPref(text) == 1){

                    usersInterestsRef.child(tag.getText()).setValue("0");
                    interestsDAO.changePetInterest(text,0);
                    tag.setTagBackgroundColor(Color.WHITE);
                    tag.setTagTextColor(Color.GRAY);
                    tag.setTagBorderColor(Color.GRAY);

                }

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    public void updateTagColors(TagContainerLayout mTagContainerLayout, ArrayList<String> list,String topic){
        InterestsDAO interestsDAO = new InterestsDAO(getApplicationContext());

        if (firstTimeLoad){
            //addInterestsToDB(list);
            //addInterestsToCloud(uuid,topic,list);
        }
        int size = list.size();
        for (int i = 0; i<size; i++){
            TagView tag = mTagContainerLayout.getTagView(i);
            if (interestsDAO.getInterestPref(tag.getText()) == 0){
                tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(Color.GRAY);
                tag.setTagBorderColor(Color.GRAY);
            }else if(interestsDAO.getInterestPref(tag.getText()) == 1){
                tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(Color.parseColor("#F4514E"));
                tag.setTagBorderColor(Color.parseColor("#F4514E"));
            }
            tag.setHorizontalPadding(18);
            tag.setVerticalPadding(14);
            tag.setTextSize(40.0f);
            tag.setBorderWidth(3.0f);
        }
    }

    public void addInterestsToCloud(String uuid, String topic, ArrayList<String> list){
        Firebase interestsRef = new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/interests_list/" + topic + "/");
        for (String s : list){
            interestsRef.child(s).setValue("0");
        }
    }

    public void addInterestsToDB(ArrayList<String> list){
        InterestsDAO interestsDAO = new InterestsDAO(getApplicationContext());
        //interestsDAO.clearTableData();
        for (String s : list){
            interestsDAO.addInterestsToDB(s,0);
        }
    }

    public void firstTimeLogin(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // 0 - for private mode
        SharedPreferences.Editor editor = settings.edit();

        //Set "hasLoggedIn" to true
        editor.putBoolean("hasLoggedIn", true);
        editor.putBoolean("isLoggedIn", true);

        // Commit the edits!
        editor.commit();
    }
}
