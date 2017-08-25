package com.amigos.sindhusha.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.adapter.TopicPrefAdapter;
import com.amigos.sindhusha.dao.UserPrefsDAO;
import com.amigos.sindhusha.vo.TopicInfo;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TopicPrefActivity extends Activity {




    ListView topics_listView;
    private String TAG="TopicPrefActivity";
    private Firebase topicsInfo_FR;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_pref);


        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        topics_listView=(ListView)findViewById(R.id.topics_listView);
        Firebase.setAndroidContext(this);
        topicsInfo_FR = new Firebase("https://amigos-d1502.firebaseio.com/topics_info/");

        Log.i(TAG, "topicsInfo_FR set");


        progressDialog = new ProgressDialog(TopicPrefActivity.this);
        progressDialog.setMessage("loading....");
        progressDialog.setTitle(" Topics List");
        progressDialog.show();


    }

    private void changeFirebaseTopicsInfo(String topicName,int addRemovFlag){
        SharedPreferences sp=getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        String uuid=sp.getString("uuid", "");
        Firebase curUser_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/topic_prefs/" );
        curUser_FR.child(topicName).setValue(addRemovFlag);
    }
    private void updateTopicAdapter(final Map<String,String> topicDescMap){

        Log.i(TAG,"updateTopicAdapter");
        ArrayList<TopicInfo> topicsArrList=new ArrayList<TopicInfo>();
        int i=0;
        for(String s:topicDescMap.keySet()){
            topicsArrList.add(new TopicInfo(i,s,topicDescMap.get(s)));
            i++;
        }

        TopicPrefAdapter adapter = new TopicPrefAdapter(getApplicationContext(), topicsArrList);
        topics_listView.setAdapter(adapter);

    }
    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "onResume()::START");
        Map<String,String> topicDescMap;
        topicsInfo_FR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> topicDescMap = dataSnapshot.getValue(Map.class);
                Log.i(TAG, "" + topicDescMap);
                updateTopicAdapter(topicDescMap);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    private ArrayAdapter<String> getPrefsAdapter(final ArrayList<TopicInfo> topicsList){

        String[] topicsArray=new String[topicsList.size()];
        int i=0;
        for(TopicInfo t:topicsList){
            topicsArray[i]=t.toString();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,topicsArray){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);

                UserPrefsDAO userPrefsDAO=new UserPrefsDAO(getApplicationContext());
                if(userPrefsDAO.getPrefFromTopicName(topicsList.get(position).getTopicName())!=null) {

                    textView.setTextColor(0xff3b5998);
                }
                else{
                    textView.setTextColor(0xff000000);
                }
                return textView;
            }
        };
        return adapter;
    }



}
