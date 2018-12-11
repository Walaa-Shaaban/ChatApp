package com.chat.walaashaaban.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsersDatabaseReference, mBlockDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        mToolbar = (Toolbar) findViewById(R.id.block_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Blocking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mBlockDatabase = FirebaseDatabase.getInstance().getReference().child("block_chat").child(uid);
    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Users, BlocksViewHolder> firebaseRecyclerAdapter = null;
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, BlocksViewHolder>(

                Users.class,
                R.layout.block_single_layout,
                BlocksViewHolder.class,
                mBlockDatabase

        ) {
            @Override
            protected void populateViewHolder(BlocksViewHolder viewHolder, Users model, int position) {
                final String user_id = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setImage(model.getImage(), BlockActivity.this);
                viewHolder.unblock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final android.support.v7.app.AlertDialog.Builder builder_block = new android.support.v7.app.AlertDialog.Builder(BlockActivity.this);

                        builder_block.setTitle(" Are you sure you want to Unblock this person? ! ")
                                .setPositiveButton(" Unblock ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mBlockDatabase.child(user_id).removeValue();
                                        onBackPressed();

                                    }
                                }).setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        builder_block.show();
                    }
                });


            }
        };




        RecyclerView mUsersList;
        mUsersList = (RecyclerView) findViewById(R.id.block_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(BlockActivity.this));


        mUsersList.setAdapter(firebaseRecyclerAdapter);


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

            Intent thisIntent = new Intent(BlockActivity.this, SettingsActivity.class);
            startActivity(thisIntent);

    }



        public static class BlocksViewHolder extends RecyclerView.ViewHolder {

            View mView;
            TextView txtName;
            ImageView imgView;
            Button unblock;




            public BlocksViewHolder(View itemView) {
                super(itemView);

                mView = itemView;
                unblock = (Button) mView.findViewById(R.id.unblock_btn);
            }



            public void setName(String name) {

                txtName = (TextView) mView.findViewById(R.id.block_single_name);
                txtName.setText(name);
            }



            public void setImage(String image, BlockActivity blocksActivity) {

                imgView = (ImageView) mView.findViewById(R.id.block_single_image);

                Picasso.with(blocksActivity).load(image).placeholder(R.drawable.profile).into(imgView);

            }

        }
    }
