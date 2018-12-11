package com.chat.walaashaaban.chat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowImageInChatActivity extends AppCompatActivity {

    private ImageView mProfilePic;
    private ImageView saveImage, mSaveAllImage;
    private Button mClose;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_in_chat);
        mProfilePic = (ImageView) findViewById(R.id.profile_pic);
        saveImage = (ImageView) findViewById(R.id.save_image_profile);
        mSaveAllImage = (ImageView) findViewById(R.id.imageSaveAll);
        mClose = (Button) findViewById(R.id.close);


        byte[] bytes = getIntent().getByteArrayExtra("bitmapbytes");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        mProfilePic.setImageBitmap(bmp);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClose.setRotation(45);
                onBackPressed();
            }
        });

        mProfilePic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                saveImage.setVisibility(View.VISIBLE);
                return false;
            }
        });


        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveImage.setEnabled(false);

                        Picasso.with(ShowImageInChatActivity.this).load(getIntent().getStringExtra("image_code")).into(mProfilePic);
                        Picasso.with(ShowImageInChatActivity.this).load(getIntent().getStringExtra("image_code")).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                bitmap = ((BitmapDrawable)mProfilePic.getDrawable()).getBitmap();
                                saveImage(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                Toast.makeText(ShowImageInChatActivity.this, "Failed", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }





                    });




            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        FirebaseDatabase.getInstance().getReference().child("Users").child(ChatActivity.mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                Intent backIntent = new Intent(ShowImageInChatActivity.this, ChatActivity.class);
                backIntent.putExtra("user_id", ChatActivity.mChatUser);
                backIntent.putExtra("user_name", name);
                Log.e("hghghghhg", String.valueOf(getIntent().getIntExtra("position", 0)));
                backIntent.putExtra("position", getIntent().getIntExtra("position", 0));
                startActivity(backIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    private void saveImage(Bitmap bmap) {
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bmap,
                "chat",
                ""
        );

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(savedImageURL);

        Toast.makeText(ShowImageInChatActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
    }
}

