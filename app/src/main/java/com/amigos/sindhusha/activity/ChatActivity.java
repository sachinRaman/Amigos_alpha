package com.amigos.sindhusha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.adapter.ChatArrayAdapter;
import com.amigos.sindhusha.dao.ArchiveListDAO;
import com.amigos.sindhusha.dao.BlockListDAO;
import com.amigos.sindhusha.dao.ChatUsersDAO;
import com.amigos.sindhusha.dao.ImageCacheDAO;
import com.amigos.sindhusha.dao.UserNewMsgsDAO;
import com.amigos.sindhusha.service.ChatService;
import com.amigos.sindhusha.util.RoundedImageView;
import com.amigos.sindhusha.vo.ChatMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by sindhusha on 15/5/17.
 */
public class ChatActivity  extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private ImageView imageSend;
    String senderUuid;
    Firebase allUsers_FR;
    ValueEventListener valueEventListener;
    Context context;
    public static String  currentUserChattingId="";

    private String receiverId="";
    LinearLayout chatActionBar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateCalled");
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.chat_main);
        onInitializeFunctionality();
        //stopService(new Intent(this, ChatService.class));

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar_chat);
        getActionBar().getDisplayOptions();

        ChatService.numMessages = 0;

        chatActionBar = (LinearLayout)findViewById(R.id.chatActionBar);


        final ImageView photoIcon = (ImageView)findViewById(R.id.actionBarPhotoIcon);
        final TextView userName = (TextView)findViewById(R.id.actionBarUserName);

        SharedPreferences sp=this.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final Intent intent = getIntent();
        if (intent.getStringExtra("userName")!= null && !intent.getStringExtra("userName").isEmpty()) {
            userName.setText(intent.getStringExtra("userName"));
        }else{
            userName.setText("User");
        }
        receiverId = intent.getStringExtra("receiverId");

        final ImageView optionsButton = (ImageView)findViewById(R.id.options_menu);

        /*photoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileUserInfoActivity = new Intent(getApplicationContext(), ProfileUserInfoActivity.class);
                profileUserInfoActivity.putExtra("uuid",v.getTag().toString());
                startActivity(profileUserInfoActivity);
            }
        });*/
        userName.setTag(receiverId);
        chatActionBar.setTag(receiverId);
        chatActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileUserInfoActivity = new Intent(getApplicationContext(), ProfileUserInfoActivity.class);
                profileUserInfoActivity.putExtra("uuid",v.getTag().toString());
                startActivity(profileUserInfoActivity);
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileUserInfoActivity = new Intent(getApplicationContext(), ProfileUserInfoActivity.class);
                profileUserInfoActivity.putExtra("uuid",v.getTag().toString());
                startActivity(profileUserInfoActivity);
            }
        });

        final BlockListDAO blockListDAO = new BlockListDAO(getApplicationContext());
        final ArchiveListDAO archiveListDAO = new ArchiveListDAO((getApplication()));
        final Firebase blockListRef = new Firebase("https://amigos-d1502.firebaseio.com/users/block_list/");
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ChatActivity.this,optionsButton);
                popup.getMenuInflater().inflate(R.menu.chat_overflow_menu, popup.getMenu());
                //Toast.makeText(ChatActivity.this,"You Clicked : ",Toast.LENGTH_SHORT).show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.block:
                                blockListDAO.addUserToBLock(receiverId,senderUuid);
                                archiveListDAO.removeUserFromArchive(receiverId,senderUuid);
                                Firebase childRef = blockListRef.child(senderUuid+"-"+receiverId);
                                childRef.setValue("");
                                Toast.makeText(ChatActivity.this,"You have blocked "+userName.getText().toString(), Toast.LENGTH_LONG).show();
                                Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent1);
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        //photoIcon.setTag(receiverId);


        final ImageCacheDAO imageCacheDAO = new ImageCacheDAO(getApplicationContext());

        if("".equalsIgnoreCase(imageCacheDAO.getUserUrl(receiverId))){
            Glide.with(getApplicationContext()).load(R.drawable.ic_user)
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(photoIcon);
        }else{
            Glide.with(getApplicationContext()).load(imageCacheDAO.getUserUrl(receiverId))
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(photoIcon);
        }

    }
//    public void onClickPhotoIcon(View view){
//
//        //Toast.makeText(this, "Profile clicked"+view.getTag(), Toast.LENGTH_SHORT).show();
//        Intent profileUserInfoActivity = new Intent(getApplicationContext(), ProfileUserInfoActivity.class);
//        profileUserInfoActivity.putExtra("uuid",view.getTag().toString());
//        startActivity(profileUserInfoActivity);
//    }


    public void onInitializeFunctionality(){
        SharedPreferences sp=this.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        senderUuid=sp.getString("uuid", "");

        Intent intent = getIntent();
        receiverId = intent.getStringExtra("receiverId");
        currentUserChattingId=receiverId;
        String userName=intent.getStringExtra("userName");

        //update new msg status as we have seen
        UserNewMsgsDAO userNewMsgsDAO=new UserNewMsgsDAO(this);
        userNewMsgsDAO.changeUserNewMsgStatus(receiverId, 0);

        Log.i(TAG,"*** receiverId is::"+receiverId);
        Log.i(TAG,"*** userName is::"+userName);
        setTitle(userName);
        imageSend = (ImageView) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String msg = chatText.getText().toString();
                    msg = msg.trim();
                    if(msg == null || msg.isEmpty()){
                        return true;
                    }
                    Firebase curUser_chat_FR= new Firebase("https://amigos-d1502.firebaseio.com/chats/"+getChatId()+"/" );
                    //curUser_chat_FR.child(senderUuid).setValue(chatText.getText().toString());

                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    Date date = new Date();
                    String time = dateFormat.format(date);

                    Firebase newMessageRef = curUser_chat_FR.push();
                    Map<String,Object> message = new HashMap<String,Object>();
                    message.put(senderUuid,msg);
                    message.put("-time",time);
                    newMessageRef.updateChildren(message);
                    chatText.setText("");
                    ArchiveListDAO archiveListDAO = new ArchiveListDAO(getApplicationContext());
                    archiveListDAO.removeUserFromArchive(receiverId,senderUuid);

                    ChatUsersDAO chatUsersDAO = new ChatUsersDAO(getApplicationContext());
                    chatUsersDAO.addToChatList(receiverId,senderUuid,msg);

                    curUser_chat_FR.child(senderUuid).setValue(msg);
                    return true;
//                    boolean readFromDB=false;
//                    return sendChatMessage(receiverId,senderUuid,chatText.getText().toString(),true,readFromDB);
                }
                return false;
            }
        });
        imageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String msg = chatText.getText().toString();
                msg = msg.trim();
                if(msg == null || msg.isEmpty()){
                    return;
                }
                Firebase curUser_chat_FR= new Firebase("https://amigos-d1502.firebaseio.com/chats/"+getChatId()+"/" );
                //curUser_chat_FR.child(senderUuid).setValue(chatText.getText().toString());
                Firebase newMessageRef = curUser_chat_FR.push();
                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                Date date = new Date();
                String time = dateFormat.format(date);
                Map<String,Object> message = new HashMap<String,Object>();
                message.put(senderUuid,msg);
                message.put("-time",time);
                newMessageRef.updateChildren(message);
                chatText.setText("");
                ArchiveListDAO archiveListDAO = new ArchiveListDAO(getApplicationContext());
                archiveListDAO.removeUserFromArchive(receiverId,senderUuid);

                ChatUsersDAO chatUsersDAO = new ChatUsersDAO(getApplicationContext());
                chatUsersDAO.addToChatList(receiverId,senderUuid,msg);

                Set<String> allChats = new HashSet<String>();
                curUser_chat_FR.child(senderUuid).setValue(msg);

//                boolean readFromDB=false;
//                sendChatMessage(receiverId,senderUuid,chatText.getText().toString(),true,readFromDB);
//                chatText.setText("");
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });



        //****Opposite person pushed messages holding
        final String chatId=getChatId();
        //allUsers_FR= new Firebase("https://amigos-d1502.firebaseio.com/chats/"+chatId+"/"+receiverId+"/" );
        allUsers_FR= new Firebase("https://amigos-d1502.firebaseio.com/chats/"+chatId+"/" );
        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                if (dataSnapshot != null) {
//                    Log.i(TAG, "Key" + dataSnapshot.getKey());
//                    Log.i(TAG, "Value" + dataSnapshot.getValue());
//                    if (!(dataSnapshot.getKey().toString()).equalsIgnoreCase(senderUuid)) {
//                        Log.i(TAG, "not equal to :" + senderUuid);
//                        String receiverMsg = dataSnapshot.getValue(String.class);
//                        boolean readFromDB=false;
//                        sendChatMessage(senderUuid, dataSnapshot.getKey().toString(), receiverMsg, false, readFromDB);
//                    }
//                }
                ChatArrayAdapter chatArrayAdapter1 = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_right);
                for (DataSnapshot cursorShot : dataSnapshot.getChildren()) {

                    ArrayList<ChatMessage> existMsgs= new ArrayList<ChatMessage>();
                    //ChatMessage chatMessage = new ChatMessage(true,"","");
                    String key = "";
                    String receiverMsg = "";
                    String time = "Not available";
                    for(DataSnapshot data: cursorShot.getChildren()){
                        //chatArrayAdapter1.add(new ChatMessage(true, receiverMsg, time));
                        key = "";
                        receiverMsg = "";
                        //time = "Not available";

                        if (senderUuid.equalsIgnoreCase(data.getKey().toString()) || receiverId.equalsIgnoreCase(data.getKey().toString())) {
                            key = data.getKey().toString();
                            receiverMsg = data.getValue(String.class);
//                            if (key.equalsIgnoreCase(receiverId)) {
//                                chatArrayAdapter1.add(new ChatMessage(false, receiverMsg));
//                            } else if (key.equalsIgnoreCase(senderUuid)) {
//                                chatArrayAdapter1.add(new ChatMessage(true, receiverMsg));
//                            }
                        }
                        if(data.getKey().equalsIgnoreCase("-time")){
                            time = data.getValue(String.class);
                        }
                        if (key.equalsIgnoreCase(receiverId)) {
                            chatArrayAdapter1.add(new ChatMessage(false, receiverMsg, time));
                        } else if (key.equalsIgnoreCase(senderUuid)) {
                            chatArrayAdapter1.add(new ChatMessage(true, receiverMsg, time));
                        }
                    }
                    /*
                    if(cursorShot.child(key).getKey().equalsIgnoreCase(receiverId)) {
                        String receiverMsg = cursorShot.child(key).child(receiverId).getValue(String.class);
                        boolean readFromDB=false;
                        sendChatMessage(senderUuid, receiverId, receiverMsg, false, readFromDB);
                    }*/
                }
                listView.setAdapter(chatArrayAdapter1);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };




        //Loading Existing Messges from DB
//        ChatDAO chatDAO=new ChatDAO(this);
//        Log.i(TAG,"initializing from DB");
//        ArrayList<ChatMessage> existMsgs=chatDAO.getAllMessages(receiverId);
//        boolean readFromDB=true;
//        for(ChatMessage msg:existMsgs){
//            boolean flag=false;
//            String toId=msg.toId;
//            String fromId=msg.fromId;
//            if(fromId.equalsIgnoreCase(senderUuid))
//                flag=true;
//            Log.i(TAG,""+msg);
//            sendChatMessage(toId, fromId, msg.message,flag,readFromDB);
//        }
    }
    @Override
    public void onResume(){
        super.onResume();
        allUsers_FR.addValueEventListener(valueEventListener);
    }
    @Override
    public void onBackPressed() {

        Firebase curUser_FR= new Firebase("https://amigos-d1502.firebaseio.com/chats/"+getChatId()+"/" );
        curUser_FR.child(senderUuid).setValue("");
        curUser_FR.child(receiverId).setValue("");

        currentUserChattingId="";
        allUsers_FR.removeEventListener(valueEventListener);

        finish();
        return;
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        Firebase curUser_FR= new Firebase("https://amigos-d1502.firebaseio.com/chats/"+getChatId()+"/" );
        curUser_FR.child(senderUuid).setValue("");
        curUser_FR.child(receiverId).setValue("");
        allUsers_FR.removeEventListener(valueEventListener);


    }


    private String getChatId(){
        SharedPreferences sp=this.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        String senderUuid=sp.getString("uuid", "");
        String chatId="";
        if(senderUuid.compareTo(receiverId)<0){
            chatId=senderUuid+"-"+receiverId;
        }
        else{
            chatId=receiverId+"-"+senderUuid;
        }
        return chatId;
    }

    public  class ChatActionBarHolder extends RecyclerView.ViewHolder{
        TextView tv_username;
        ImageView profilePicImageView;
        public View view;

        public ChatActionBarHolder(View itemView) {
            super(itemView);
            tv_username=(TextView) itemView.findViewById(R.id.actionBarUserName);
            profilePicImageView=(ImageView) itemView.findViewById(R.id.actionBarPhotoIcon);
            this.view = itemView;
        }
    }

}
