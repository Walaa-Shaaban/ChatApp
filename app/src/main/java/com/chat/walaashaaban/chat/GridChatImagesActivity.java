package com.chat.walaashaaban.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GridChatImagesActivity extends AppCompatActivity {

    public GridView gridView;
    public GridChatImagesAdapter gridAdapterImages;
    private DatabaseReference mRootRef;
    private DatabaseReference mDatabaseChatImages;
    public  static List<String> lst = new ArrayList<String>();
    public static ArrayList<ImageItemChat> imageItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_chat_images);
        gridView = (GridView) findViewById(R.id.gridViewImagesChat);
        imageItems.clear();
        gridAdapterImages = new GridChatImagesAdapter(this, R.layout.grid_item_images_chat, getData());
        //imageItems.remove(getIntent().getIntExtra("position", 0));
        gridView.setAdapter(gridAdapterImages);


    }

    private ArrayList<ImageItemChat> getData() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseChatImages = mRootRef.child("messages").child(ChatActivity.mCurrentUserId)
                .child(ChatActivity.mChatUser);

        mDatabaseChatImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dsp : dataSnapshot.getChildren()){
                        String type = dataSnapshot.child(dsp.getKey()).child("type").getValue().toString();
                        if(type.equals("image")){
                            if(!lst.contains(dataSnapshot.child(dsp.getKey()).child("thumb_image_chat").getValue().toString())) {
                                lst.add(dataSnapshot.child(dsp.getKey()).child("thumb_image_chat").getValue().toString());
                            }


                        }

                    }
                    for (int i = 0; i < lst.size(); i++) {
                        imageItems.add(new ImageItemChat(lst.get(i)));


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