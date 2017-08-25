package com.amigos.sindhusha.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.activity.ChatActivity;
import com.amigos.sindhusha.activity.ProfileUserInfoActivity;
import com.amigos.sindhusha.dao.ChatUsersDAO;
import com.amigos.sindhusha.dao.ImageCacheDAO;
import com.amigos.sindhusha.dao.UserNewMsgsDAO;
import com.amigos.sindhusha.util.FileCache;
import com.amigos.sindhusha.util.ImageLoader;
import com.amigos.sindhusha.util.MemoryCache;
import com.amigos.sindhusha.vo.UserVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class UsersLVAdapter extends RecyclerView.Adapter<UsersLVAdapter.UsersListViewHolder>{
    ArrayList<UserVO> userVoList;
    Context context;
    boolean chatAdapter;

    private static LayoutInflater inflater = null;

    public UsersLVAdapter(Context ctx, ArrayList<UserVO> userVoList,boolean chatAdapter) {
        this.userVoList=userVoList;
        context=ctx;
        this.chatAdapter=chatAdapter;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public UsersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list,parent,false);
        return new UsersListViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final UsersListViewHolder holder, final int position) {

        final UsersListViewHolder curHolder=holder;
        if(userVoList.get(position).getNickName() != null && !userVoList.get(position).getNickName().isEmpty()) {
            holder.tv.setText(userVoList.get(position).getNickName());
        }else{
            holder.tv.setText("User");
        }
        holder.tvStatus.setText(userVoList.get(position).getInfo());
        UserNewMsgsDAO userNewMsgsDAO=new UserNewMsgsDAO(context);

        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);

        if(userNewMsgsDAO.getCurrentUserStatus(userVoList.get(position).getUuid())==1&&chatAdapter==true){
            holder.tv.setTextColor(0xff3b5998);
            holder.tvMatch.setText("New message");
            holder.tvMatch.setTextColor(0xff3b5998);
            //holder.tvStatus.setText("");
            holder.tvStatus.setText(chatUsersDAO.getLastMessage(userVoList.get(position).getUuid()));
        }
        else{
            holder.tv.setTextColor(0xff000000);
            holder.tvMatch.setText("");
            //holder.tvStatus.setText("");
            holder.tvStatus.setText(chatUsersDAO.getLastMessage(userVoList.get(position).getUuid()));
        }
        if(chatAdapter==false) {
            holder.tvMatch.setText(userVoList.get(position).getMatchCount() + "% CLICK");
            holder.tvStatus.setText(userVoList.get(position).getInfo());
        }
        else{
            Firebase curUser_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/"+userVoList.get(position).getNickName()+"/" );
            curUser_FR.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String,String> userInfo=dataSnapshot.getValue(HashMap.class);
                    try {
                        if(userInfo.get("nickname") != null && !userInfo.get("nickname").isEmpty()) {
                            curHolder.tv.setText(userInfo.get("nickname"));
                            userVoList.get(position).setNickName(userInfo.get("nickname"));
                        }else{
                            curHolder.tv.setText("User");
                            userVoList.get(position).setNickName("User");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

        final String uuid = userVoList.get(position).getUuid();

        // Loads image from database to imageView
        //loadImageViaImageLoader(holder, uuid);

        final ImageCacheDAO imageCacheDAO = new ImageCacheDAO(context);

        if("".equalsIgnoreCase(imageCacheDAO.getUserUrl(uuid))){
            Glide.with(context).load(R.drawable.ic_user)
                    .bitmapTransform(new CropSquareTransformation(context), new RoundedCornersTransformation(context,20,5))
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.profilePicImageView);
        }else {
            Glide.with(context).load(imageCacheDAO.getUserUrl(uuid))
                    .bitmapTransform(new CropSquareTransformation(context), new RoundedCornersTransformation(context, 20, 5))
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.profilePicImageView);
        }

        //https://github.com/wasabeef/glide-transformations
        //holder.profilePicImageView.setTag(userVoList.get(position).getUuid());

        holder.profilePicImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileUserInfoActivity = new Intent(context, ProfileUserInfoActivity.class);
                profileUserInfoActivity.putExtra("uuid",userVoList.get(position).getUuid());
                profileUserInfoActivity.putExtra("match",userVoList.get(position).getMatchCount());
                context.startActivity(profileUserInfoActivity);
            }
        });

        holder.view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                curHolder.tv.setTextColor(0xff000000);
                if(chatAdapter==true){
                    curHolder.tvMatch.setText("");
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("receiverId", userVoList.get(position).getUuid());
                    intent.putExtra("userName", userVoList.get(position).getNickName());
                    context.startActivity(intent);
                }else{
                    Intent profileUserInfoActivity = new Intent(context, ProfileUserInfoActivity.class);
                    profileUserInfoActivity.putExtra("uuid",userVoList.get(position).getUuid());
                    profileUserInfoActivity.putExtra("match",userVoList.get(position).getMatchCount());
                    context.startActivity(profileUserInfoActivity);
                }


            }
        });
    }


    /*private void loadImageViaImageLoader(final UsersListViewHolder holder, final String uuid) {
        MemoryCache memoryCache = new MemoryCache();
        final ImageCacheDAO imageCacheDAO = new ImageCacheDAO(context);
        String url = "";
        url = imageCacheDAO.getUserUrl(uuid);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap == null){
            File f=fileCache.getFile(url);
            bitmap = ImageLoader.decodeFile(f);
        }
        if(bitmap != null) {
            bitmap = ImageLoader.getSquareCroppedBitmap(bitmap);
            bitmap = ImageLoader.getRoundedCornerBitmap(bitmap, 150);
            holder.profilePicImageView.setImageBitmap(bitmap);
        }else{
            imgLoader = new ImageLoader(context);
            imgLoader.flag = 2;
            imgLoader.DisplayImage(url, holder.profilePicImageView, uuid);
        }
    }*/


    @Override
    public int getItemCount() {
        return userVoList.size();
    }


    public  class UsersListViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        TextView tvMatch;
        TextView tvStatus;
        ImageView profilePicImageView;
        public View view;
        public UsersListViewHolder(View itemView) {
            super(itemView);
            tv=(TextView) itemView.findViewById(R.id.tv_name);
            tvMatch=(TextView) itemView.findViewById(R.id.tv_match_info);
            profilePicImageView=(ImageView) itemView.findViewById(R.id.iv_profile);
            tvStatus = (TextView)itemView.findViewById(R.id.tv_status);
            this.view = itemView;
        }
    }
}
