package com.amigos.sindhusha.Values;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.amigos.sindhusha.dao.ImageCacheDAO;
import com.amigos.sindhusha.util.ImageLoader;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sachin on 8/5/2017.
 */

public class PutImagesToCache {

    Context context;
    private ImageLoader imgLoader;
    ImageView imageView;

    public PutImagesToCache(Context context) {
        this.context = context;
        this.imageView = new ImageView(context);
    }

    public void getAllUsers(){
        Firebase users= new Firebase("https://amigos-d1502.firebaseio.com/users/");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> usersArrList = new ArrayList<String>();
                for (DataSnapshot curuserShot : dataSnapshot.getChildren()) {
                    String userUUID = curuserShot.getKey();
                    usersArrList.add(userUUID);
                }
                getImagesFromFirebase(usersArrList);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getImagesFromFirebase(ArrayList<String> usersArrList ){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        final ImageCacheDAO imageCacheDAO = new ImageCacheDAO(context);

        for (final String uuid : usersArrList){
            storageReference.child("images/" + uuid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        String url = (new URL(uri.toString())).toString();
                        if(imageCacheDAO.userExists(uuid)){
                            String urlFromDB = imageCacheDAO.getUserUrl(uuid);
                            imgLoader = new ImageLoader(context);
                            if(!urlFromDB.equals(url)){
                                imageCacheDAO.updateUrl(uuid,url);
                                imgLoader.queuePhoto(url, imageView, uuid);
                            }
                        }else{
                            imageCacheDAO.addUser(uuid,url);
                            imgLoader = new ImageLoader(context);
                            imgLoader.queuePhoto(url, imageView, uuid);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}
