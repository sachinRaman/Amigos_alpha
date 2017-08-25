package com.amigos.sindhusha.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.activity.ArchiveChatActivity;
import com.amigos.sindhusha.activity.ProfileUserInfoActivity;
import com.amigos.sindhusha.dao.ArchiveListDAO;
import com.amigos.sindhusha.dao.ImageCacheDAO;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * Created by Sachin on 6/19/2017.
 */

public class ArchiveLVAdapter extends ArrayAdapter<UserVO> implements View.OnClickListener {


    ArrayList<UserVO> archiveVoList;
    Context context;
    ListView archiveListView;
    static FileCache fileCache;

    private ImageLoader imgLoader;

    private static LayoutInflater inflater=null;

    public ArchiveLVAdapter(Context ctx, ArrayList<UserVO> archiveVoList, ListView archiveListView) {
        super(ctx,R.layout.item_user_list,archiveVoList);
        this.archiveVoList=archiveVoList;
        this.archiveListView = archiveListView;
        context=ctx;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }





    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ArchiveListViewHolder holder;


        if(convertView == null) {
            holder = new ArchiveListViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_user_list, parent, false);



            holder.tv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvMatch = (TextView) convertView.findViewById(R.id.tv_match_info);
            holder.profilePicImageView = (ImageView) convertView.findViewById(R.id.iv_profile);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            holder.view = (LinearLayout)convertView.findViewById(R.id.holder_LinearLayout);
            convertView.setTag(holder);
        }else{
            holder = (ArchiveListViewHolder) convertView.getTag();
        }

        fileCache=new FileCache(context);
        final ArchiveListViewHolder curHolder=holder;

        holder.tvStatus.setText("");

        Firebase curUser_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/"+archiveVoList.get(position).getUuid()+"/" );
        holder.setUuid(archiveVoList.get(position).getUuid());
        curUser_FR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equalsIgnoreCase("nickname")) {
                        if(child.getValue().toString()!= null && !child.getValue().toString().isEmpty()) {
                            holder.tv.setText(child.getValue().toString());
                        }else{
                            holder.tv.setText("User");
                        }
                        holder.tv.setTextColor(Color.BLACK);
                        archiveVoList.get(position).setNickName(child.getValue().toString());
                    }
                    if (child.getKey().equalsIgnoreCase("info")) {
                        if(child.getValue().toString() == null || child.getValue().toString().isEmpty()){
                            holder.tvStatus.setText("");
                            archiveVoList.get(position).setInfo("");
                        }else {
                            holder.tvStatus.setText("");
                            archiveVoList.get(position).setInfo(child.getValue().toString());
                        }
                    }

                    if (child.getKey().equalsIgnoreCase("topics_prefs")) {
                        HashMap<String, Integer> topics = new HashMap<String, Integer>();
                        archiveVoList.get(position).setTopicsPrefs(child.getValue(HashMap.class));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        holder.tvMatch.setText("");

        final String uuid = archiveVoList.get(position).getUuid();
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


        //Setting tag on profilePicImage
        holder.profilePicImageView.setTag(archiveVoList.get(position).getUuid());
        holder.profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileUserInfoActivity = new Intent(context, ProfileUserInfoActivity.class);
                profileUserInfoActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                profileUserInfoActivity.putExtra("uuid",v.getTag().toString());
                context.startActivity(profileUserInfoActivity);
            }
        });

        final View finalConvertView = convertView;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("receiverId", archiveVoList.get(position).getUuid());
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("userName", archiveVoList.get(position).getNickName());
//                context.startActivity(intent);
                final PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.unarchive_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.unarchive) {
                            String recId=archiveVoList.get(position).getUuid();
                            SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
                            String senderUuid=sp.getString("uuid", "");
                            ArchiveListDAO archiveListDAO = new ArchiveListDAO(context);
                            archiveListDAO.removeUserFromArchive(recId,senderUuid);
                            Toast.makeText(context,"You have removed "+archiveVoList.get(position).getNickName()+" from hold.",Toast.LENGTH_SHORT).show();
                            ArchiveChatActivity.setArchiveList(ArchiveChatActivity.archiveListView);
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();


            }
        });

        convertView.setTag(holder);


        return convertView;
    }

    private void loadImageViaImageLoader(final ArchiveListViewHolder holder, final String uuid) {
        MemoryCache memoryCache = new MemoryCache();
        final ImageCacheDAO imageCacheDAO = new ImageCacheDAO(context);
        String url = "";
        url = imageCacheDAO.getUserUrl(uuid);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap == null) {
            File f = fileCache.getFile(url);
            bitmap = ImageLoader.decodeFile(f);
        }
        if (bitmap != null) {
            bitmap = ImageLoader.getSquareCroppedBitmap(bitmap);
            bitmap = ImageLoader.getRoundedCornerBitmap(bitmap, 150);
            holder.profilePicImageView.setImageBitmap(bitmap);
        } else {
            imgLoader = new ImageLoader(context);
            imgLoader.flag = 2;
            imgLoader.DisplayImage(url, holder.profilePicImageView, uuid);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public static class ArchiveListViewHolder {
        TextView tv;
        TextView tvMatch;
        TextView tvStatus;
        ImageView profilePicImageView;
        View view;

        String uuid;

        public String getUuid(){
            return uuid;
        }

        private void setUuid(String uuid) {
            this.uuid = uuid;
        }

    }
}
