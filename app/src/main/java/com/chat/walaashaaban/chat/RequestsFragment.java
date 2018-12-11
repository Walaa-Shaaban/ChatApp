package com.chat.walaashaaban.chat;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    private  RecyclerView mRequestList;

    private DatabaseReference mRequestsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestList = (RecyclerView) mMainView.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mRequestsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));



        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        final ImageView imgReq;

        final FirebaseRecyclerAdapter<Requests, RequestsFragment.RequestViewHolder> requestsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Requests, RequestsFragment.RequestViewHolder>(

                Requests.class,
                R.layout.request_single_layout,
                RequestsFragment.RequestViewHolder.class,
                mRequestsDatabase


        ) {




            @Override
            protected void populateViewHolder(final RequestsFragment.RequestViewHolder requestsViewHolder, final Requests requests, int i) {




                final String list_user_id = getRef(i).getKey();



                mRequestsDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists() && dataSnapshot.child("request_type").exists()) {
                            String type = dataSnapshot.child("request_type").getValue().toString();
                            if (type.equals("sent")) {


                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();


                                        requestsViewHolder.setName(userName);
                                        requestsViewHolder.setUserImage(userThumb, getContext());
                                        requestsViewHolder.setImageReq("sent");
                                        requestsViewHolder.setButtons("sent");


                                        requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {


                                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                profileIntent.putExtra("user_id", list_user_id);
                                                startActivity(profileIntent);


                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }


                                });
                            } else {
                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();


                                        requestsViewHolder.setName(userName);
                                        requestsViewHolder.setUserImage(userThumb, getContext());
                                        requestsViewHolder.setImageReq("received");
                                        requestsViewHolder.setButtons("received");


                                        requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {


                                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                profileIntent.putExtra("user_id", list_user_id);
                                                profileIntent.putExtra("UsersActivity", "false");
                                                profileIntent.putExtra("MainActivity", "true");
                                                profileIntent.putExtra("chatActivity", "false");

                                                Pair[] pairs = new Pair[2];

                                                pairs[0] = new Pair<View, String>(requestsViewHolder.userImageView, "imageTransition");
                                                pairs[1] = new Pair<View, String>(requestsViewHolder.userNameView, "nameTransition");

                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
                                                    startActivity(profileIntent , options.toBundle());
                                                }else {
                                                    startActivity(profileIntent);
                                                }



                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }


                                });

                            }


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        }
        };


        mRequestList.setAdapter(requestsRecyclerViewAdapter);
    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView userNameView;
        CircleImageView userImageView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }



        public void setName(String name){

            userNameView = (TextView) mView.findViewById(R.id.req_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            userImageView = (CircleImageView) mView.findViewById(R.id.req_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.profile).into(userImageView);

        }

        public void setImageReq(String type){

            ImageView imgReq = (ImageView) mView.findViewById(R.id.imgRequest);
            if(type.equals("sent")) {
                imgReq.setImageResource(R.drawable.ic_trending_up_black_24dp);
            }else {
                imgReq.setImageResource(R.drawable.request_add);
            }


        }

        public void setButtons(String type){

            RelativeLayout r = (RelativeLayout) mView.findViewById(R.id.req_relative);
            if(type.equals("sent")){
                r.setVisibility(View.INVISIBLE);
                r.getLayoutParams().height = 1;

            }else {
                r.setVisibility(View.VISIBLE);
            }


        }


        }



        }


