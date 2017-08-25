package com.amigos.sindhusha.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.adapter.BlockLVAdapter;
import com.amigos.sindhusha.dao.BlockListDAO;
import com.amigos.sindhusha.vo.UserVO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import android.support.v7.app.AppCompatActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BlockListActivity extends AppCompatActivity {

    public static final String TAG = "BlockListActivity";
    public static ListView blockListView;
    ArrayList<UserVO> arrList = new ArrayList<UserVO>();
    private static Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        blockListView = (ListView)findViewById(R.id.blockList);
        context = getApplicationContext();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Block List");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        //getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setTitle("Block List");

        blockListView = (ListView)findViewById(R.id.blockList);
        //registerForContextMenu(blockListView);

        SharedPreferences sp=getApplicationContext().getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");

        //final BlockListDAO blockListDAO = new BlockListDAO(this);
        //HashMap<String,String> blockUsers = new HashMap<String,String>();
        //blockUsers= blockListDAO.getAllBlockUsers();
        //Toast.makeText(BlockListActivity.this, blockUsers.toString(),Toast.LENGTH_LONG).show();

        populateBlockList(senderUuid);

        //final Set<String> archiveList = (blockListDAO.getBlockUsers(senderUuid));
        //setBlockList(archiveList);
    }

    /*public void onClickOfBlockedProfile(View view){


        //Toast.makeText(this, "Profile clicked"+view.getTag(), Toast.LENGTH_SHORT).show();
        Intent profileUserInfoActivity = new Intent(getApplicationContext(), ProfileUserInfoActivity.class);
        profileUserInfoActivity.putExtra("uuid",view.getTag().toString());
        startActivity(profileUserInfoActivity);

    }*/

    private void populateBlockList(final String senderUuid) {
        final BlockListDAO blockListDAO = new BlockListDAO(context);
        Firebase blockListFirebase = new Firebase("https://amigos-d1502.firebaseio.com/users/block_list/");
        blockListFirebase.addValueEventListener(new ValueEventListener() {
            Set<String> blockList = new HashSet<String>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blockList.clear();
                blockListDAO.clearTableData();
                for (DataSnapshot curuserShot : dataSnapshot.getChildren()){
                    if (curuserShot.getKey().toString().contains(senderUuid)) {
                        if (curuserShot.getKey().toString().startsWith(senderUuid)) {
                            String recieverId = curuserShot.getKey().toString().replace(senderUuid, "");
                            recieverId = recieverId.replace("-", "");
                            blockListDAO.addUserToBLock(recieverId,senderUuid);
                            blockList.add(recieverId);
                        }else{
                            String recieverId = curuserShot.getKey().toString().replace(senderUuid, "");
                            recieverId = recieverId.replace("-", "");
                            blockListDAO.addUserToBLock(senderUuid,recieverId);
                        }
                    }
                }
                setBlockList(blockList);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static void setBlockList(final Set<String> blockList){
        ArrayList<UserVO> blockArrList = new ArrayList<UserVO>();

        for(String s:blockList){
            UserVO uv=new UserVO(s,"","","1",new HashMap<String,Integer>());
            blockArrList.add(uv);
        }
        BlockLVAdapter blockLVAdapter = new BlockLVAdapter(context, blockArrList);
        blockListView.setAdapter(blockLVAdapter);
    }


//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        if (v.getId()==R.id.blockList) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.unblock_popup_menu, menu);
//        }
//    }
//
//   @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        SharedPreferences sp=getApplicationContext().getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
//        final String senderUuid=sp.getString("uuid", "");
//        final Firebase blockListRef = new Firebase("https://amigos-d1502.firebaseio.com/users/block_list/");
//        Firebase curUser;
//        switch(item.getItemId()){
//            case R.id.unblock:
//                int index = info.position;
//                View view = info.targetView;
//                BlockLVAdapter.BlockListViewHolder holder = (BlockLVAdapter.BlockListViewHolder) view.getTag();
//                BlockListDAO blockListDAO = new BlockListDAO(getApplicationContext());
//                String uuid = holder.getUuid();
//                blockListDAO.removeUserFromBlock(uuid);
//                curUser = blockListRef.child(senderUuid+"-"+uuid);
//                curUser.setValue(null);
//                populateBlockList(senderUuid);
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }
}
