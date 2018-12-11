package com.chat.walaashaaban.chat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class showImageProfileActivity extends AppCompatActivity {

    private CircleImageView mProfilePic;
    private DatabaseReference mUsersDatabase;
    private String mUser_id;
    //private ProgressBar progressBar;
    private ImageView saveImage, mSaveAllImage;
    private Button mClose;
    private String isUsersActivity, isChatActivity, isMainActivity ;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_profile);
        mProfilePic = (CircleImageView) findViewById(R.id.profile_pic);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        saveImage = (ImageView) findViewById(R.id.save_image_profile);
        mSaveAllImage = (ImageView) findViewById(R.id.imageSaveAll);
        mClose = (Button) findViewById(R.id.close);


        isUsersActivity = getIntent().getStringExtra("UsersActivity");
        isMainActivity = getIntent().getStringExtra("MainActivity");
        isChatActivity = getIntent().getStringExtra("chatActivity");
        position = getIntent().getIntExtra("position", 0);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        mUser_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser_id);


        byte[] bytes = getIntent().getByteArrayExtra("bitmapbytes");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        mProfilePic.setImageBitmap(bmp);



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
                mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").getValue().toString();

                Picasso.with(showImageProfileActivity.this).load(image).into(mSaveAllImage);
                Picasso.with(showImageProfileActivity.this).load(image).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap = ((BitmapDrawable)mSaveAllImage.getDrawable()).getBitmap();
                        saveImage(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Toast.makeText(showImageProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        Intent backIntent = new Intent(showImageProfileActivity.this, ProfileActivity.class);
        backIntent.putExtra("user_id", mUser_id);
        backIntent.putExtra("UsersActivity", isUsersActivity);
        backIntent.putExtra("MainActivity", isMainActivity);
        backIntent.putExtra("chatActivity", isChatActivity);
        backIntent.putExtra("isMale", getIntent().getBooleanExtra("isMale", false));
        backIntent.putExtra("isFemale", getIntent().getBooleanExtra("isFemale", false));
        backIntent.putExtra("isMenu", getIntent().getBooleanExtra("isMenu", false));
        backIntent.putExtra("position", position);
        backIntent.putExtra("isSelectCountry", getIntent().getBooleanExtra("isSelectCountry", false));
        backIntent.putExtra("country_iso", getIntent().getStringExtra("country_iso"));

        Pair[] pairs = new Pair[1];

        pairs[0] = new Pair<View, String>(mProfilePic, "imageTransition");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(showImageProfileActivity.this, pairs);
            startActivity(backIntent , options.toBundle());
        }else {
            startActivity(backIntent);
        }

        finish();
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

        Toast.makeText(showImageProfileActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
    }


}
