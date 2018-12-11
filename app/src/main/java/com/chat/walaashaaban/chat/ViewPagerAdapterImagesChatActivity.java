package com.chat.walaashaaban.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ViewPagerAdapterImagesChatActivity extends AppCompatActivity {

    private ViewPager mViewPagerImagesChat;
    private PagerAdapter mAdapterImagesChat;
    private Toolbar mAlbumToolbar;
    private DatabaseReference mRootRef;
    private DatabaseReference mDatabaseChatImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_adapter_images_chat);

        mViewPagerImagesChat = (ViewPager) findViewById(R.id.pager_images_chat);
        mAlbumToolbar = (Toolbar) findViewById(R.id.albums_bar);
        setSupportActionBar(mAlbumToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        mAdapterImagesChat = new ViewPagerAdapterImagesChat(ViewPagerAdapterImagesChatActivity.this, GridChatImagesActivity.lst);


        mViewPagerImagesChat.setAdapter(mAdapterImagesChat);
        mViewPagerImagesChat.setCurrentItem(getIntent().getIntExtra("position", 0));


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.activity_album_bar, null);

        actionBar.setCustomView(action_bar_view);

        CircleImageView myProfileImage = (CircleImageView) findViewById(R.id.album_bar_image);


        DatabaseReference mRootRef;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mCurrentUserId = mAuth.getCurrentUser().getUid();

        Log.e(ChatActivity.mChatUser, "");

        mRootRef.child("Users").child(ChatActivity.mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                Picasso.with(ViewPagerAdapterImagesChatActivity.this).load(thumb_image).placeholder(R.drawable.profile).into(myProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.album_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;


            case R.id.menu_save:
                saveImage(GridChatImagesActivity.lst.get(mViewPagerImagesChat.getCurrentItem()));
                break;

            case R.id.menu_share:
                shareItem(GridChatImagesActivity.lst.get(mViewPagerImagesChat.getCurrentItem()));
                break;

            case R.id.menu_delete_image:

                mRootRef = FirebaseDatabase.getInstance().getReference();
                mDatabaseChatImages = mRootRef.child("messages").child(ChatActivity.mCurrentUserId)
                        .child(ChatActivity.mChatUser);

                mDatabaseChatImages.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                String type = dataSnapshot.child(dsp.getKey()).child("type").getValue().toString();
                                final String[] user_name = new String[1];
                                if (type.equals("image")) {
                                    if (dataSnapshot.child(dsp.getKey()).child("thumb_image_chat").getValue().toString().equals(GridChatImagesActivity.lst.get(mViewPagerImagesChat.getCurrentItem()))) {
                                        mDatabaseChatImages.child(dsp.getKey()).removeValue();
                                        mRootRef.child("Users").child(ChatActivity.mChatUser).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                user_name[0] = dataSnapshot.child("name").getValue().toString();
                                                Intent gridBackIntent = new Intent(ViewPagerAdapterImagesChatActivity.this, ChatActivity.class);
                                                gridBackIntent.putExtra("user_id", ChatActivity.mChatUser);
                                                gridBackIntent.putExtra("user_name", user_name[0]);
                                                gridBackIntent.putExtra("position", getIntent().getIntExtra("position", 0));
                                                startActivity(gridBackIntent);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                break;

        }

        return true;
    }


    public void shareItem(String url) {
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void saveImage(String url) {

        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                String savedImageURL = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "chat",
                        ""
                );


                Uri savedImageURI = Uri.parse(savedImageURL);

                Toast.makeText(ViewPagerAdapterImagesChatActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });


    }


    private ArrayList<ImageItemChat> getData() {
        ArrayList<ImageItemChat> imageItems = new ArrayList<>();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseChatImages = mRootRef.child("messages").child(ChatActivity.mCurrentUserId)
                .child(ChatActivity.mChatUser);

        mDatabaseChatImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        String type = dataSnapshot.child(dsp.getKey()).child("type").getValue().toString();
                        if (type.equals("image")) {
                            GridChatImagesActivity.lst.add(dataSnapshot.child(dsp.getKey()).child("thumb_image_chat").getValue().toString());


                        }

                    }
                    for (int i = 0; i < GridChatImagesActivity.lst.size(); i++) {
                        Log.e(GridChatImagesActivity.lst.get(i), "        ");
                        imageItems.add(new ImageItemChat(GridChatImagesActivity.lst.get(i)));


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
        return imageItems;
    }
}