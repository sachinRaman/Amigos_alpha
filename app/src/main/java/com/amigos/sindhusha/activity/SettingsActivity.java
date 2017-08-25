package com.amigos.sindhusha.activity;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.Values.PeevesList;
import com.amigos.sindhusha.dao.ImageCacheDAO;
import com.amigos.sindhusha.dao.InterestsDAO;
import com.amigos.sindhusha.util.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends Activity {


    private static final int PICK_IMAGE_REQUEST = 234;
    //a Uri object to store file path
    private Uri filePath;
    EditText et_display_name;
    EditText info;
    EditText age,place;
    EditText aboutMe;
    private String TAG = "SettingsActivity";
    private ImageView imageView;
    public static StorageReference storageReference;
    public RadioGroup radioSexGroup;
    public RadioButton radioSexButton;
    public ImageLoader imgLoader;
    String myUuid = "";

    public static ProgressDialog progress;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);


        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));

        Firebase.setAndroidContext(this);
        SharedPreferences sp = getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid = sp.getString("uuid", "");
        myUuid = senderUuid;
        Log.i(TAG, "senderUuid:" + senderUuid);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        et_display_name = (EditText) findViewById(R.id.et_display_name);
        info = (EditText) findViewById(R.id.editText_info);
        age = (EditText) findViewById(R.id.age);
        //sex = (EditText) findViewById(R.id.sex);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        place = (EditText) findViewById(R.id.place);
        aboutMe = (EditText) findViewById(R.id.editText_bio);
        imageView = (ImageView) findViewById(R.id.iv_profile_pic);


        final ImageCacheDAO imageCacheDAO = new ImageCacheDAO(getApplicationContext());
        if ("".equalsIgnoreCase(imageCacheDAO.getUserUrl(senderUuid))){
            Glide.with(getApplicationContext()).load(R.drawable.ic_user)
                    .bitmapTransform(new CropSquareTransformation(getApplicationContext()), new CropCircleTransformation(getApplicationContext()))
                    .into(imageView);
        }else{
            Glide.with(getApplicationContext()).load(imageCacheDAO.getUserUrl(senderUuid))
                    .bitmapTransform(new CropSquareTransformation(getApplicationContext()),
                            new CropCircleTransformation(getApplicationContext()))
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }


        Firebase curUser_FR = new Firebase("https://amigos-d1502.firebaseio.com/users/" + senderUuid + "/");

        curUser_FR.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildAdded");
                Log.i(TAG, "" + dataSnapshot.getValue());
                if (dataSnapshot.getKey().equalsIgnoreCase("nickname")) {
                    et_display_name.setText(dataSnapshot.getValue().toString());
                }
                if (dataSnapshot.getKey().equalsIgnoreCase("info")) {
                    info.setText(dataSnapshot.getValue().toString());
                }
                if (dataSnapshot.getKey().equalsIgnoreCase("age")) {
                    age.setText(dataSnapshot.getValue().toString());
                }
                if (dataSnapshot.getKey().equalsIgnoreCase("sex")) {
                    if("Male".equalsIgnoreCase(dataSnapshot.getValue().toString())){
                        radioSexGroup.check(R.id.radioMale);
                    }else if("Female".equalsIgnoreCase(dataSnapshot.getValue().toString())){
                        radioSexGroup.check(R.id.radioFemale);
                    }
                    //sex.setText(dataSnapshot.getValue().toString());
                }
                if (dataSnapshot.getKey().equalsIgnoreCase("place")) {
                    place.setText(dataSnapshot.getValue().toString());
                }
                if (dataSnapshot.getKey().equalsIgnoreCase("aboutMe")) {
                    aboutMe.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.i(TAG, "onChildChanged");
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
        });





    }

    public void updateNickName(View view) {


        SharedPreferences sp = getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        final String senderUuid = sp.getString("uuid", "");
        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) findViewById(selectedId);
        String sexStr = radioSexButton.getText().toString();
        Firebase curUser_FR = new Firebase("https://amigos-d1502.firebaseio.com/users/" + senderUuid + "/");
        if (info.getText().toString().length() <= 100) {
            curUser_FR.child("nickname").setValue(et_display_name.getText().toString());
            curUser_FR.child("info").setValue(info.getText().toString());
            curUser_FR.child("age").setValue(age.getText().toString());
            //curUser_FR.child("sex").setValue(sex.getText().toString());
            curUser_FR.child("sex").setValue(sexStr);
            curUser_FR.child("place").setValue(place.getText().toString());
            curUser_FR.child("aboutMe").setValue(aboutMe.getText().toString());
            Toast.makeText(this, "Data updated successfully.", Toast.LENGTH_LONG).show();
            uploadFile();
            addUrlToProfile(curUser_FR, myUuid);
            SharedPreferences settings = getSharedPreferences(PreferenceTags.PREFS_NAME, 0);
            boolean isLoggedIn = settings.getBoolean("isLoggedIn",false);
            if(!isLoggedIn){
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> lifestyle = PeevesList.getAllLifestyleInterests();
                        ArrayList<String> arts = PeevesList.getAllArtsInterests();
                        ArrayList<String> entertainment = PeevesList.getAllEntertainmentInterests();
                        ArrayList<String> business = PeevesList.getAllBusinessInterests();
                        ArrayList<String> sports = PeevesList.getAllSportsInterests();
                        ArrayList<String> music = PeevesList.getAllMusicInterests();
                        ArrayList<String> technology = PeevesList.getAllTechnologyInterests();

                        addInterestsToCloud(senderUuid,"lifestyle",lifestyle);
                        addInterestsToCloud(senderUuid,"arts",arts);
                        addInterestsToCloud(senderUuid,"entertainment",entertainment);
                        addInterestsToCloud(senderUuid,"business",business);
                        addInterestsToCloud(senderUuid,"sports",sports);
                        addInterestsToCloud(senderUuid,"music",music);
                        addInterestsToCloud(senderUuid,"technology",technology);

                        addInterestsToDB(lifestyle);
                        addInterestsToDB(arts);
                        addInterestsToDB(entertainment);
                        addInterestsToDB(business);
                        addInterestsToDB(sports);
                        addInterestsToDB(music);
                        addInterestsToDB(technology);

                    }
                });

                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this, PreferenceTags.class);
                startActivity(intent);

            }else {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }

        } else {
            Toast.makeText(this, "Status should not be more than 100 characters", Toast.LENGTH_LONG).show();
        }


    }

    public static void addUrlToProfile(final Firebase curUser_FR, final String uuid){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        storageReference.child("images/" + uuid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    curUser_FR.child("imageUrl").child(uuid).setValue((new URL(uri.toString())).toString());
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

    //method to show file chooser
    public void showFileChooser(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);

        } else {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted.
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        bitmap = getCircularCroppedBitmap(bitmap);
                        imageView.setImageBitmap(bitmap);


                        //imageView.setImageURI(data.getData());
                        Log.i(TAG, "image set");
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                } else {


                    // Permission denied - Show a message to inform the user that this app only works
                    // with these permissions granted

                }
                return;
            }

        }
    }

    public static Bitmap getCircularCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "inside onActivityResult");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.i(TAG, "inside filepath::" + filePath);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissions();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadFile() {
        try {
            //if there is a file to upload
            if (filePath != null) {
                //displaying a progress dialog while upload is going on
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                SharedPreferences sp = this.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
                String uuid = sp.getString("uuid", "");
                StorageReference riversRef = storageReference.child("images/" + uuid + ".jpg");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

                byte[] byteArray = out.toByteArray();


                //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                riversRef.putBytes(byteArray)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying a success toast
                                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying error message
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
            }
            //if there is not any file
            else {
                //you can display an error toast
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addInterestsToCloud(String uuid, String topic, ArrayList<String> list){
        Firebase interestsRef = new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/interests_list/" + topic + "/");
        for (String s : list){
            interestsRef.child(s).setValue("0");
        }
    }

    public void addInterestsToDB(ArrayList<String> list){
        InterestsDAO interestsDAO = new InterestsDAO(getApplicationContext());
        //interestsDAO.clearTableData();
        for (String s : list){
            interestsDAO.addInterestsToDB(s,0);
        }
    }


}