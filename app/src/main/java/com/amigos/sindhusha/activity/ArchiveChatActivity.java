package com.amigos.sindhusha.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.adapter.ArchiveLVAdapter;
import com.amigos.sindhusha.dao.ArchiveListDAO;
import com.amigos.sindhusha.dao.BlockListDAO;
import com.amigos.sindhusha.vo.UserVO;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ArchiveChatActivity extends Activity {


    public static final String TAG = "ArchiveChatActivity";
    public static ListView archiveListView;
    private static Context context;
    HashMap<String, UserVO> archiveUsersMap = new HashMap<String, UserVO>();
    private static ArchiveLVAdapter archiveLVAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_chat);

        getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setTitle("Archive List");

        context = getApplicationContext();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);


        archiveListView = (ListView)findViewById(R.id.archiveChatList);
        //registerForContextMenu(archiveListView);

        setArchiveList(archiveListView);



        //Toast.makeText(ArchiveChatActivity.this,archiveList.toString(),Toast.LENGTH_LONG).show();
        //Toast.makeText(ArchiveChatActivity.this,archiveListDAO.getAllArchiveUsers().toString(),Toast.LENGTH_LONG).show();



    }

    public void removeUser(int position){
        archiveListView.removeViewAt(position);
    }



    public static void setArchiveList(final View view){

        SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");

        BlockListDAO blockListDAO = new BlockListDAO(context);
        final Set<String> blockList = (blockListDAO.getBlockUsers(senderUuid));
        final Set<String> fromBlockList = (blockListDAO.getFromBlockUsers(senderUuid));

        ArchiveListDAO archiveListDAO = new ArchiveListDAO(context);
        final Set<String> archiveList = (archiveListDAO.getArchiveUsers(senderUuid));
        archiveList.removeAll(blockList);
        archiveList.removeAll(fromBlockList);
        //Toast.makeText(ArchiveChatActivity.this,archiveList.toString(),Toast.LENGTH_LONG).show();

        final ArrayList<UserVO> archiveArrList = new ArrayList<UserVO>();

       for(final String s:archiveList) {
           UserVO uv = new UserVO(s, "", "", "1", new HashMap<String, Integer>());
           archiveArrList.add(uv);
       }
        archiveLVAdapter = new ArchiveLVAdapter(context, archiveArrList, archiveListView);
        archiveListView.setAdapter(archiveLVAdapter);

//        archiveListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                final PopupMenu popup = new PopupMenu(getApplicationContext(), view);
//                popup.getMenuInflater().inflate(R.menu.unarchive_popup_menu, popup.getMenu());
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        if (item.getItemId() == R.id.unarchive) {
//                            String recId=archiveArrList.get(position).getUuid();
//                            SharedPreferences sp=getApplicationContext().getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
//                            String senderUuid=sp.getString("uuid", "");
//                            ArchiveListDAO archiveListDAO = new ArchiveListDAO(getApplicationContext());
//                            archiveListDAO.removeUserFromArchive(recId,senderUuid);
//                            archiveArrList.remove(archiveArrList.get(position));
//                            archiveLVAdapter.notifyDataSetChanged();
//                            //archiveVoList.remove(position);
//
//                            //ArchiveChatActivity.setArchiveList(archiveListView);
//                            return true;
//                        }
//                        return false;
//                    }
//                });
//                popup.show();
//            }
//        });
    }

    public void updateArchiveUsersList(HashMap<String,UserVO> allUsersMap,View view) {
        //Calculating Self-Prefs List START
        SharedPreferences sp=getApplicationContext().getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");
        List<String> curUserPrefsList=new ArrayList<String>();
        if(!allUsersMap.isEmpty()) {
            for (String topicName : allUsersMap.get(senderUuid).getTopicsPrefs().keySet()) {
                int status = allUsersMap.get(senderUuid).getTopicsPrefs().get(topicName);
                Log.i(TAG, "****" + topicName + "::" + status);
                if (status == 1)
                    curUserPrefsList.add(topicName);
            }
            //Calculating Self-Prefs List END

            Log.i(TAG, "allUsersMap::" + allUsersMap);
            ArrayList<UserVO> usersArrList = new ArrayList<UserVO>();
            for (String s : allUsersMap.keySet()) {
                if (s.equalsIgnoreCase(senderUuid))
                    continue;
                int matchCount = calculateMatchCount(allUsersMap.get(s).getTopicsPrefs(), curUserPrefsList);
                allUsersMap.get(s).setMatchCount(matchCount);
                usersArrList.add(allUsersMap.get(s));
            }

            Collections.sort(usersArrList, new Comparator<UserVO>() {
                @Override
                public int compare(UserVO lhs, UserVO rhs) {
                    if (lhs.getMatchCount() > rhs.getMatchCount())
                        return -1;
                    return 1;
                }
            });
        }
    }

    public int calculateMatchCount(Map<String,Integer> oppPrefs,List<String> myPrefs){

        int matchCount=0;
        int myTotal = myPrefs.size();
        Log.i(TAG,"opp prefs:"+oppPrefs);
        Log.i(TAG,"my prefs:"+myPrefs);
        for(String topic:oppPrefs.keySet()){
            if(oppPrefs.get(topic)!=1)
                continue;
            if(myPrefs.contains(topic)){
                matchCount++;
            }
        }
        Log.i(TAG, "matchcount::" + matchCount);

        if (myTotal == 0){
            return 0;
        }
        return Math.round(((float)matchCount/myTotal)*100);
    }
/*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.archiveChatList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.unarchive_popup_menu, menu);
//            final PopupMenu popup = new PopupMenu(getApplicationContext(), v);
//            popup.getMenuInflater().inflate(R.menu.unarchive_popup_menu, popup.getMenu());
//            popup.show();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        SharedPreferences sp=getApplicationContext().getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");
        switch(item.getItemId()){
            case R.id.unarchive:
                int index = info.position;
                View view = info.targetView;
                ArchiveLVAdapter.ArchiveListViewHolder holder = (ArchiveLVAdapter.ArchiveListViewHolder) view.getTag();
                String uuid = holder.getUuid();
                ArchiveListDAO archiveListDAO = new ArchiveListDAO((getApplication()));
                archiveListDAO.removeUserFromArchive(uuid,senderUuid);
                setArchiveList(archiveListView);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }*/
}
