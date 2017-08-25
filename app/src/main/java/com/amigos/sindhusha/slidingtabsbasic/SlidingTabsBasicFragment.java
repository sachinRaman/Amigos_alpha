

package com.amigos.sindhusha.slidingtabsbasic;


import com.amigos.sindhusha.R;
import com.amigos.sindhusha.activity.MainActivity;

import com.amigos.sindhusha.adapter.UsersLVAdapter;
import com.amigos.sindhusha.common.view.SlidingTabLayout;
import com.amigos.sindhusha.dao.ArchiveListDAO;
import com.amigos.sindhusha.dao.BlockListDAO;
import com.amigos.sindhusha.dao.ChatDAO;
import com.amigos.sindhusha.dao.ChatUsersDAO;
import com.amigos.sindhusha.dao.ImageCacheDAO;
import com.amigos.sindhusha.vo.UserVO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SlidingTabsBasicFragment extends Fragment {

    static final String TAG = "SlidingTabsBasicFragment";
    static String senderUuid;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    public static View instantiatedView=null;
    HashMap<String, UserVO> allUsersMap = new HashMap<String, UserVO>();
    static HashMap<String, UserVO> allUsersMapChats = new HashMap<String, UserVO>();
    Activity mActivity;
    Set<String> chatList = new HashSet<String>();
    Set<String> myChatList = new HashSet<String>();
    boolean flag = false;
    int tab = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sliding, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        setRetainInstance(true);


        SharedPreferences sp=getActivity().getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        senderUuid=sp.getString("uuid", "");


        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new TabsPageAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.i(TAG, "onpageselected position:" + position);
                if (position == 0) {
                    // Users tab
                    tab = 0;
                    setAllUsersList(instantiatedView);

                } else if (position == 1) {
                    // Chats tab
                    tab = 1;
                    ChatDAO chatDAO = new ChatDAO(instantiatedView.getContext());
                    Log.i(TAG, "distinct users::" + chatDAO.getDistinctChatUsers());
                    final Set<String> allChats = (chatDAO.getDistinctChatUsers());
                    //myChatList(senderUuid);
                    ChatUsersDAO chatUsersDAO = new ChatUsersDAO(instantiatedView.getContext());
                    myChatList = chatUsersDAO.getMyChatList(senderUuid);
                    setAllChatsList(myChatList, instantiatedView);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);



    }
    public static void setAllChatsList(final Set<String> allChats,final View view){

        //View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item, container, false);
        if (instantiatedView == null){
            return;
        }

        ArchiveListDAO archiveListDAO = new ArchiveListDAO(instantiatedView.getContext());
        final Set<String> archiveList = (archiveListDAO.getArchiveUsers(senderUuid));

        BlockListDAO blockListDAO = new BlockListDAO(instantiatedView.getContext());
        final Set<String> blockList = (blockListDAO.getBlockUsers(senderUuid));
        final Set<String> fromBlockList = (blockListDAO.getFromBlockUsers(senderUuid));



        ArrayList<UserVO> usersArrList = new ArrayList<UserVO>();
        for(String s:allChats){
            if (s.equalsIgnoreCase("block_list"))
                continue;
            if(archiveList.contains(s))
                continue;
            if(blockList.contains(s))
                continue;
            if(fromBlockList.contains(s))
                continue;
            UserVO uv=new UserVO(s,s,s,"1",new HashMap<String,Integer>());
            usersArrList.add(uv);
        }

        Collections.sort(usersArrList, new Comparator<UserVO>() {
            @Override
            public int compare(UserVO lhs, UserVO rhs) {
                ChatUsersDAO chatUsersDAO = new ChatUsersDAO(instantiatedView.getContext());
                if ( chatUsersDAO.getTimeStamp(lhs.getUuid()).compareTo(chatUsersDAO.getTimeStamp(rhs.getUuid())) > 0 )
                    return -1;
                return 1;
            }
        });

        RecyclerView usersListrecyclerView = (RecyclerView) view.findViewById(R.id.all_users_recycleView);
        UsersLVAdapter chatsAdapter = new UsersLVAdapter(view.getContext(), usersArrList,true);
        RecyclerView.LayoutManager usersListLayoutMgr = new LinearLayoutManager(view.getContext());
        //usersListrecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        usersListrecyclerView.setAdapter(chatsAdapter);
        chatsAdapter.notifyDataSetChanged();
        usersListrecyclerView.setLayoutManager(usersListLayoutMgr);
    }

    private void setAllUsersList(final View view){
        Log.i(TAG, "instantiateItem()::Position 0");

        SharedPreferences sp=mActivity.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        senderUuid=sp.getString("uuid", "");
        getAllUser(view, senderUuid);
    }

    private void getAllUser(final View view, final String senderUuid) {
        Firebase allUsers_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/" );
        allUsers_FR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange" + dataSnapshot.getKey());
                Set<String> blockUsers = new HashSet<String>();
                if(tab == 0) {
                    for (DataSnapshot curuserShot : dataSnapshot.getChildren()) {
                        String userUUID = curuserShot.getKey();
                        if (userUUID.equalsIgnoreCase("block_list")) {
                            for (DataSnapshot child : curuserShot.getChildren()) {
                                String receiverId = child.getKey().toString().replace(senderUuid, "");
                                receiverId = receiverId.replace("-", "");
                                blockUsers.add(receiverId);
                            }
                            continue;
                        }
                        String nickName = "";
                        int status = 0;
                        String info = "";
                        String url = "";
                        Map<String, Integer> topicsPrefs = new HashMap<String, Integer>();
                        ArrayList<String> interestArray = new ArrayList<String>();
                        for (DataSnapshot child : curuserShot.getChildren()) {
                            if (child.getKey().equalsIgnoreCase("status"))
                                status = child.getValue(Integer.class);
                            else if (child.getKey().equalsIgnoreCase("nickname"))
                                nickName = child.getValue(String.class);
                            /*else if (child.getKey().equalsIgnoreCase("topics_prefs")) {
                                topicsPrefs = child.getValue(Map.class);
                            }*/ else if (child.getKey().equalsIgnoreCase("info")) {
                                info = child.getValue(String.class);
                            }else if(child.getKey().equalsIgnoreCase("imageUrl")){
                                for(DataSnapshot urls : child.getChildren()){
                                    if (urls.getKey().equalsIgnoreCase(userUUID)){
                                        url = urls.getValue(String.class);
                                        ImageCacheDAO imageCacheDAO = new ImageCacheDAO(getContext());
                                        if (!imageCacheDAO.userExists(userUUID)) {
                                            imageCacheDAO.addUser(userUUID, url);
                                        } else {
                                            imageCacheDAO.updateUrl(userUUID, url);
                                        }
                                    }
                                }
                            }
                            else if(child.getKey().equalsIgnoreCase("interests_list")){
                                for (DataSnapshot interests : child.getChildren()){
                                    String key = interests.getKey();
                                    Map<String, String> topicInterests = interests.getValue(Map.class);
                                    for (String s: topicInterests.keySet()){
                                        if("1".equalsIgnoreCase(topicInterests.get(s))){
                                            interestArray.add(s);
                                        }
                                    }
                                }
                            }

                        }

                        Log.i(TAG, "Status:" + "" + status);
                        Log.i(TAG, "nickname:" + "" + nickName);
                        UserVO thisUser = new UserVO(userUUID, nickName, info, Integer.toString(status), topicsPrefs);
                        thisUser.setTopicsOfInterest(interestArray);
                        allUsersMap.put(userUUID, thisUser);
                    }

                    Iterator iterator = blockUsers.iterator();
                    while (iterator.hasNext()) {
                        allUsersMap.remove(iterator.next());
                    }

                    updateAllUsersList(allUsersMap, view);
                    MainActivity.progressDioalog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public void updateAllUsersList(HashMap<String,UserVO> allUsersMap, View view){


        //Calculating Self-Prefs List START

        SharedPreferences sp=mActivity.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");
        Log.i(TAG,"****UUID"+senderUuid);
        /*Log.i(TAG,"myprefs map in start::"+allUsersMap.get(senderUuid).getTopicsPrefs());
        //List<String> curUserPrefsList=new ArrayList<String>();
        for(String topicName:allUsersMap.get(senderUuid).getTopicsPrefs().keySet()){
            int status=allUsersMap.get(senderUuid).getTopicsPrefs().get(topicName);
            Log.i(TAG,"****"+topicName+"::"+status);
            if(status == 1)
                curUserPrefsList.add(topicName);
        }*/

        ArrayList<String> myInterests = allUsersMap.get(senderUuid).getTopicsOfInterest();
        //Calculating Self-Prefs List END

        ArchiveListDAO archiveListDAO = new ArchiveListDAO(instantiatedView.getContext());
        final Set<String> archiveList = (archiveListDAO.getArchiveUsers(senderUuid));

        BlockListDAO blockListDAO = new BlockListDAO(instantiatedView.getContext());
        final Set<String> blockList = (blockListDAO.getBlockUsers(senderUuid));
        final Set<String> fromBlockList = (blockListDAO.getFromBlockUsers(senderUuid));

        ChatUsersDAO chatUserstDAO = new ChatUsersDAO(instantiatedView.getContext());
        myChatList = chatUserstDAO.getMyChatList(senderUuid);

//        ChatDAO chatDAO = new ChatDAO(instantiatedView.getContext());
//        final Set<String> allChatsList = (chatDAO.getDistinctChatUsers());

        Log.i(TAG, "allUsersMap::" + allUsersMap);
        ArrayList<UserVO> usersArrList=new ArrayList<UserVO>();
        for(String s:allUsersMap.keySet()){
            if(s.equalsIgnoreCase("block_list"))
                continue;
            if(s.equalsIgnoreCase(senderUuid))
                continue;
            if(archiveList.contains(s))
                continue;
            if(blockList.contains(s))
                continue;
            if(fromBlockList.contains(s))
                continue;
            if(myChatList.contains(s))
                continue;
            int matchCount = getMatchesCount(myInterests,allUsersMap.get(s).getTopicsOfInterest());
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

        RecyclerView usersListrecyclerView = (RecyclerView) view.findViewById(R.id.all_users_recycleView);
        UsersLVAdapter usersAdapter = new UsersLVAdapter(view.getContext(),usersArrList,false);
        RecyclerView.LayoutManager usersListLayoutMgr = new LinearLayoutManager(view.getContext());
        //usersListrecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        usersListrecyclerView.setAdapter(usersAdapter);
        usersListrecyclerView.setLayoutManager(usersListLayoutMgr);
    }

    public static int getMatchesCount(ArrayList<String> myInterests, ArrayList<String> userInterests){
        if(myInterests.isEmpty()){
            return 0;
        }
        int myTotal = myInterests.size();
        int match = 0;
        for (String s: userInterests){
            if (myInterests.contains(s)){
                match++;
            }
        }
        return Math.round(((float)match/myTotal)*100);
    }

    //*********** Inner-Class for Tab-Pages
    class TabsPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position==0){
                return "All Users";
            }
            else if(position==1){
                return "Chats";
            }
            /*else if(position==2){
                return "Blocked";
            }*/
            return "Item " + (position + 1);

        }
        @Override
        public Object instantiateItem(final ViewGroup container, int position) {

            Log.i(TAG, "instantiateItem()::START");
            final View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item, container, false);
            container.addView(view);
            instantiatedView=view;
            if(position==0){
                setAllUsersList(view);
            }
            else if(position==1){
                ChatDAO chatDAO=new ChatDAO(view.getContext());
                chatDAO.getDistinctChatUsers().toArray();
                final Set<String> allChats=(chatDAO.getDistinctChatUsers());

                ChatUsersDAO chatUsersDAO = new ChatUsersDAO(instantiatedView.getContext());
                myChatList = chatUsersDAO.getMyChatList(senderUuid);

                setAllChatsList(myChatList,view);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}
