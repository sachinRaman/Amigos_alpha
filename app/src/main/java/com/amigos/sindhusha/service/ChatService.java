package com.amigos.sindhusha.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.activity.ChatActivity;
import com.amigos.sindhusha.dao.BlockListDAO;
import com.amigos.sindhusha.dao.ChatUsersDAO;
import com.amigos.sindhusha.dao.UserNewMsgsDAO;
import com.amigos.sindhusha.slidingtabsbasic.SlidingTabsBasicFragment;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by sindhusha on 22/5/17.
 */
public class ChatService extends  Service{

    Firebase allUsers_FR;
    ChildEventListener childEventListener;
    private String TAG="ChatService";

    public static int numMessages = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Firebase.setAndroidContext(this);
        Log.i(TAG,"onStartCommand of chatservice");
        SharedPreferences sp=this.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");

        updateBlockList(senderUuid);

        allUsers_FR= new Firebase("https://amigos-d1502.firebaseio.com/chats/" );
        childEventListener=new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot != null) {
                    if (ChatActivity.currentUserChattingId.length()>0&&
                             (dataSnapshot.getKey().toString()).contains(ChatActivity.currentUserChattingId)) {
                        //do nothing
                    }
                    else if ((dataSnapshot.getKey().toString()).contains(senderUuid)) {

                        String currentKey = dataSnapshot.getKey().toString();


                        String receiverId=dataSnapshot.getKey().toString().replace(senderUuid,"");
                        receiverId=receiverId.replace("-","");
                        Firebase curChat = new Firebase("https://amigos-d1502.firebaseio.com/chats/"+currentKey+"/"+receiverId+"/" );
                        String message="";
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if(child.getKey().equalsIgnoreCase(receiverId)) {
                                message = child.getValue().toString();
                                curChat.setValue("");
                            }
                        }

                        Firebase allUsers_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/"+receiverId+"/" );
                        final String finalReceiverId = receiverId;
                        final String finalMessage = message;
                        if(finalMessage.length()>0){
                            final String finalReceiverId1 = receiverId;
                            allUsers_FR.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    Map<String, String> parms = dataSnapshot.getValue(Map.class);
                                    Log.i(TAG,"params are:"+parms.get("nickname"));
                                    changeNewMsgStatusInDb(finalReceiverId);
                                    createNewMessageNotification(finalReceiverId, parms.get("nickname"), finalMessage);

                                    ChatUsersDAO chatUsersDAO = new ChatUsersDAO(getApplicationContext());
                                    chatUsersDAO.addToChatList(finalReceiverId1,senderUuid,finalMessage );

                                    Set<String> myChatList = new HashSet<String>();
                                    myChatList = chatUsersDAO.getMyChatList(senderUuid);

                                    SlidingTabsBasicFragment.setAllChatsList(myChatList, SlidingTabsBasicFragment.instantiatedView);
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }



                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        allUsers_FR.addChildEventListener(childEventListener);


        return START_STICKY;
    }

    private void changeNewMsgStatusInDb(String uuid){
        UserNewMsgsDAO userNewMsgsDAO=new UserNewMsgsDAO(this);
        userNewMsgsDAO.changeUserNewMsgStatus(uuid,1);

    }
    private void createNewMessageNotification(String uuid,String userName,String message){
        Log.i(TAG, "uuid is::" + uuid);
        Log.i(TAG, "userName is::" + userName);


                Log.i(TAG, "createAlarmNotification::START");
        //Creating Notification

        Intent notificationIntent = new Intent(getApplicationContext(), ChatActivity.class);
        notificationIntent.putExtra("receiverId", uuid);
        notificationIntent.putExtra("userName", userName);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        //int id = (int) System.currentTimeMillis();
        int id = 12345;

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Amigos:"+ userName)
                    .setContentText("Message :"+message)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            alarmNotificationBuilder.setContentText("Message :" + message).setNumber(++numMessages);

            alarmNotificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
            Notification newMessageNotification=alarmNotificationBuilder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            newMessageNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(id, newMessageNotification);

        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.i(TAG,"onTaskRemoved of chatservice");
       Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
       restartServiceIntent.setPackage(getPackageName());

       PendingIntent restartServicePendingIntent =  PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
       AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
       alarmService.set(
          AlarmManager.ELAPSED_REALTIME,
          SystemClock.elapsedRealtime() + 1000,
          restartServicePendingIntent);

       super.onTaskRemoved(rootIntent);
    }


    @Override
    public void onDestroy() {

        Log.i(TAG, "onCreate() , service stopped...");
        allUsers_FR.removeEventListener(childEventListener);
        super.onDestroy();

    }

    private void updateBlockList(final String senderUuid) {
        final BlockListDAO blockListDAO = new BlockListDAO(getApplicationContext());
        Firebase blockListFirebase = new Firebase("https://amigos-d1502.firebaseio.com/users/block_list/");
        blockListFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blockListDAO.clearTableData();
                for (DataSnapshot curuserShot : dataSnapshot.getChildren()){
                    if (curuserShot.getKey().toString().contains(senderUuid)) {
                        if (curuserShot.getKey().toString().startsWith(senderUuid)) {
                            String recieverId = curuserShot.getKey().toString().replace(senderUuid, "");
                            recieverId = recieverId.replace("-", "");
                            blockListDAO.addUserToBLock(recieverId,senderUuid);
                        }else{
                            String recieverId = curuserShot.getKey().toString().replace(senderUuid, "");
                            recieverId = recieverId.replace("-", "");
                            blockListDAO.addUserToBLock(senderUuid,recieverId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
