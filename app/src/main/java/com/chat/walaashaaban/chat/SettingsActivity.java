package com.chat.walaashaaban.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.chat.walaashaaban.chat.ProfileActivity.getId;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private TextView txtDisplayName;
    private TextView txtStatus;
    private CircleImageView mImageView;
    private Button mChangeStatus, mChangeName;
    private ImageView mSecurity, mEditImage, mFlag, mBloking;
    private RelativeLayout rel_settings;


    private static final int GALLERY_PICK = 1;

    //storage
    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtDisplayName = (TextView) findViewById(R.id.txt_display_name);
        rel_settings = (RelativeLayout) findViewById(R.id.rel_setting);
        txtStatus = (TextView) findViewById(R.id.txt_status);
        mChangeStatus = (Button) findViewById(R.id.btn_change_status);
        mChangeName = (Button) findViewById(R.id.btn_change_name);
        mImageView = (CircleImageView) findViewById(R.id.user_single_image);
        mSecurity = (ImageView) findViewById(R.id.img_security);
        mEditImage = (ImageView) findViewById(R.id.editImage);
        mFlag = (ImageView) findViewById(R.id.flag);
        mBloking = (ImageView) findViewById(R.id.img_block_list);



        rel_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditImage.setVisibility(View.INVISIBLE);
            }
        });


        mBloking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBlock = new Intent(SettingsActivity.this, BlockActivity.class);
                startActivity(intentBlock);
            }
        });

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabase.keepSynced(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("flag").exists()) {
                    String flag = "flag_" + dataSnapshot.child("flag").getValue().toString();
                    mFlag.setBackgroundResource(getId(flag, R.drawable.class));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                txtDisplayName.setText(name);
                txtStatus.setText(status);

                if(!image.equals("defult")) {
                    Picasso.with(SettingsActivity.this).load(image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile)
                            .into(mImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    Picasso.with(SettingsActivity.this).load(image)
                                            .placeholder(R.drawable.profile)
                                            .into(mImageView);

                                }
                            });
                }



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        mChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nameIntent = new Intent(SettingsActivity.this, UserNameActivity.class);
                nameIntent.putExtra("username", txtDisplayName.getText().toString().trim());
                startActivity(nameIntent);
                finish();
            }
        });

        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("status", txtStatus.getText().toString().trim());
                startActivity(statusIntent);
                finish();
            }
        });

        mSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent passwordIntent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(passwordIntent);
                finish();

            }
        });

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
                        mEditImage.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mEditImage.setVisibility(View.INVISIBLE);
                        break;
                }

                return true;
            }


        });

        mEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mEditImage.setVisibility(View.INVISIBLE);
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Upload Image");
                mProgressDialog.setMessage("please wait");
                mProgressDialog.setCanceledOnTouchOutside(true);
                mProgressDialog.show();

                final String currentUser = mCurrentUser.getUid().toString();


                //compress Image
                File thumb_path = new File(resultUri.getPath());

                Bitmap thumb_image = null;
                try {
                    thumb_image = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_path);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                //upload image firebase
                final StorageReference filePath = mStorageRef.child("chat_images").child(currentUser + ".jpg");

                //upload image compress
                final StorageReference filePath_thumb = mStorageRef.child("chat_images").child("thumbs").child(currentUser + ".jpg");


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = filePath_thumb.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    if (thumb_task.isSuccessful()) {

                                        String downloadUrl_thumb = thumb_task.getResult().getDownloadUrl().toString();

                                        Map update_data = new HashMap<>();
                                        update_data.put("image", downloadUrl);
                                        update_data.put("thumb_image", downloadUrl_thumb);




                                        mDatabase.updateChildren(update_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    mProgressDialog.dismiss();
                                                } else {

                                                    Toast.makeText(SettingsActivity.this, "Error for uploading Image", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });


                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SettingsActivity.this, "Error for uploading Image", Toast.LENGTH_LONG).show();
                        }


                    }
                });
            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
     @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(backIntent);
        finish();
    }



}
