package com.amigos.sindhusha.OfflineCapabilities;

import android.app.Application;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.util.FontsOverride;
import com.amigos.sindhusha.util.TypefaceUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Sachin on 6/27/2017.
 */

public class Amigos extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //TypefaceUtil.overrideFont(getApplicationContext(), "DEFAULT", "fonts/Calibri/Calibri.ttf");
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                        .setDefaultFontPath("fonts/Calibri/Calibri.ttf")
//                        .setFontAttrId(R.attr.fontPath)
//                        .build()
//        );
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Calibri/Calibri.ttf");


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chats");
//        mDatabase.keepSynced(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
