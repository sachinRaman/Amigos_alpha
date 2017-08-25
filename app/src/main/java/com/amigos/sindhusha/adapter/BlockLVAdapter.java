package com.amigos.sindhusha.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.activity.BlockListActivity;
import com.amigos.sindhusha.activity.ProfileUserInfoActivity;
import com.amigos.sindhusha.dao.BlockListDAO;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Sachin on 6/21/2017.
 */

public class BlockLVAdapter extends ArrayAdapter<UserVO> implements View.OnClickListener {

    ArrayList<UserVO> blockVoList;
    Context context;

    static FileCache fileCache;

    private ImageLoader imgLoader;

    private static LayoutInflater inflater=null;

    public BlockLVAdapter(Context ctx, ArrayList<UserVO> blockVoList) {
        super(ctx, R.layout.item_user_block_list,blockVoList);
        this.blockVoList=blockVoList;
        context=ctx;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BlockLVAdapter.BlockListViewHolder holder;


        if(convertView == null) {
            holder = new BlockLVAdapter.BlockListViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_user_block_list, parent, false);

            holder.tv = (TextView) convertView.findViewById(R.id.tv_blockName);
            //holder.tvMatch = (TextView) convertView.findViewById(R.id.tv_blockMatch_info);
            holder.profilePicImageView = (ImageView) convertView.findViewById(R.id.iv_blockProfile);
            //holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_blockStatus);
            holder.view = (LinearLayout)convertView.findViewById(R.id.holder_blockList_LinearLayout);
            convertView.setTag(holder);
        }else{
            holder = (BlockLVAdapter.BlockListViewHolder) convertView.getTag();
        }

        fileCache=new FileCache(context);

        final BlockLVAdapter.BlockListViewHolder curHolder=holder;

        Firebase curUser_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/"+blockVoList.get(position).getUuid()+"/" );
        holder.setUuid(blockVoList.get(position).getUuid());
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
                        blockVoList.get(position).setNickName(child.getValue().toString());
                    }
                    if (child.getKey().equalsIgnoreCase("info")) {
                        //holder.tvStatus.setText(child.getValue().toString());
                        blockVoList.get(position).setInfo(child.getValue().toString());
                    }

                    if (child.getKey().equalsIgnoreCase("topics_prefs")) {
                        HashMap<String, Integer> topics = new HashMap<String, Integer>();
                        blockVoList.get(position).setTopicsPrefs(child.getValue(HashMap.class));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final String uuid = blockVoList.get(position).getUuid();
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

        //Setting tag on profilePicImage`
        holder.profilePicImageView.setTag(blockVoList.get(position).getUuid());
        convertView.setTag(holder);

        holder.profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileUserInfoActivity = new Intent(context, ProfileUserInfoActivity.class);
                profileUserInfoActivity.putExtra("uuid",blockVoList.get(position).getUuid());
                profileUserInfoActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(profileUserInfoActivity);
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.unblock_popup_menu, popup.getMenu());
                final Firebase blockListRef = new Firebase("https://amigos-d1502.firebaseio.com/users/block_list/");
                final Firebase[] curUser = new Firebase[1];
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.unblock) {
                            String recId=blockVoList.get(position).getUuid();
                            SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
                            String senderUuid=sp.getString("uuid", "");
                            BlockListDAO blockListDAO = new BlockListDAO(context);
                            blockListDAO.removeUserFromBlock(uuid);
                            curUser[0] = blockListRef.child(senderUuid+"-"+recId);
                            curUser[0].setValue(null);
                            BlockListActivity.setBlockList(blockListDAO.getBlockUsers(senderUuid));
                            Toast.makeText(context,"You have unblocked "+blockVoList.get(position).getNickName(), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });


        return convertView;
    }

    private void loadImageViaImageLoader(final BlockListViewHolder holder, final String uuid) {
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

    public static class BlockListViewHolder {
        TextView tv;
        //TextView tvMatch;
        //TextView tvStatus;
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
