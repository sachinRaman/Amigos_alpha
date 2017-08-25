package com.amigos.sindhusha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.Values.PeevesList;
import com.amigos.sindhusha.dao.PetPeevesDAO;
import com.amigos.sindhusha.vo.PetPeevesVO;
import com.amigos.sindhusha.vo.TagView;
import com.cunoraz.tagview.Tag;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


public class TopicTagsActivity extends Activity {


    String TAG = "TopicTagsActivity";
    static TagView tagGroup;
    ArrayList<Tag> tags = new ArrayList<Tag>();
    String uuid = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_tags);
        SharedPreferences sp=getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        uuid=sp.getString("uuid", "");



        tagGroup = (TagView)findViewById(R.id.tag_group);
        Firebase usersRef = new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("peeves_list")){

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        getPetPeevesTags(tags);


        final Firebase usersPeevesRef = new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/peeves_list/");
        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(com.cunoraz.tagview.Tag tag, int position) {
                Log.i(TAG, "inside onTagClick for postion:" + position);
                Log.i(TAG, "inside onTagClick for text:" + tag.text);
                PetPeevesDAO petPeevesDAO = new PetPeevesDAO(getApplicationContext());

                if (petPeevesDAO.getPeevePref(tag.text) == 1) {
                    //tag.layoutColor = Color.GRAY;

                    tag.tagTextColor = Color.GRAY;
                    tag.layoutBorderColor = Color.GRAY;
                    tag.layoutColor = Color.parseColor("#FFFFFF");
                    tag.layoutBorderSize = 2.0F;

                    usersPeevesRef.child(tag.text).setValue("0");
                    petPeevesDAO.changePetPeeve(tag.text, 0);
                    tagGroup.removeTagAtPosition(position);
                    tagGroup.addTagAtPosition(position, tag);
                    tagGroup.drawTags();
                    return;
                }
                if (petPeevesDAO.getPeevePref(tag.text) == 0) {
                    //tag.layoutColor = Color.parseColor("#F4514E");

                    tag.tagTextColor = Color.parseColor("#F4514E");
                    tag.layoutBorderColor = Color.parseColor("#F4514E");
                    tag.layoutColor = Color.parseColor("#FFFFFF");
                    tag.layoutBorderSize = 2.0F;

                    usersPeevesRef.child(tag.text).setValue("1");
                    petPeevesDAO.changePetPeeve(tag.text, 1);
                    tagGroup.removeTagAtPosition(position);
                    tagGroup.addTagAtPosition(position, tag);
                    tagGroup.drawTags();
                    return;
                }

            }
        });
    }

    public void getPetPeevesTags(ArrayList<Tag> tags){
        Log.i(TAG,"inside getPetPeevesTags");
        PetPeevesDAO petPeevesDAO = new PetPeevesDAO(getApplicationContext());
        ArrayList<PetPeevesVO> petPeevesVOArrayList = petPeevesDAO.getAllPeeves();
        if(petPeevesVOArrayList.isEmpty()){
            addPetPeevesToDB();
            petPeevesVOArrayList = petPeevesDAO.getAllPeeves();
            addPeevesToCloud(uuid);
        }
        for(PetPeevesVO petPeevesVO: petPeevesVOArrayList){
            Tag tag = new Tag(petPeevesVO.getName());
            if( "0".equalsIgnoreCase(Integer.toString(petPeevesVO.getPref()))) {
                //tag.layoutColor = Color.GRAY;
                tag.tagTextColor = Color.GRAY;
                tag.layoutBorderColor = Color.GRAY;
                tag.layoutColor = Color.parseColor("#FFFFFF");
                tag.layoutBorderSize = 2.0F;

            }else if ("1".equalsIgnoreCase(Integer.toString(petPeevesVO.getPref()))){
                //tag.layoutColor = Color.parseColor("#F4514E");
                tag.tagTextColor = Color.parseColor("#F4514E");
                tag.layoutBorderColor = Color.parseColor("#F4514E");
                tag.layoutColor = Color.parseColor("#FFFFFF");
                tag.layoutBorderSize = 2.0F;
            }
            tags.add(tag);
        }
        tagGroup.addTags(tags);
    }

    public void addPeevesToCloud(String uuid){
        Firebase peevesRef = new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/peeves_list/");
        for (String s:PeevesList.petPeeves){
            peevesRef.child(s).setValue("0");
        }
    }

    public void addPetPeevesToDB(){
        PetPeevesDAO petPeevesDAO = new PetPeevesDAO(getApplicationContext());
        for (String s:PeevesList.petPeeves){
            petPeevesDAO.addPeevesToDB(s,0);
        }
    }


}
