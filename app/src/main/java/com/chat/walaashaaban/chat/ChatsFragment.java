package com.chat.walaashaaban.chat;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {

    private RecyclerView mConvList;
    private SwipeRefreshLayout mRefreshLayout;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;
    private Context context;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        mRefreshLayout = (SwipeRefreshLayout) mMainView.findViewById(R.id.message_swipe_layout);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();


        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                onStart();


            }
        });

        // Inflate the layout for this fragment
        return mMainView;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        final Handler mHandler = new Handler();
//        Timer mTimer = new Timer();
//        mTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        onStart();
//
//                    }
//
//                });
//            }
//
//
//            @Override
//            public boolean cancel() {
//                return super.cancel();
//            }
//        }, 0, 6000);
//
//
//    }


    @Override
    public void onStart() {
        super.onStart();


        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.users_single_layout,
                ConvViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {


                final String list_user_id = getRef(i).getKey();
                final Boolean[] isSeen = new Boolean[1];

                mConvDatabase.child(list_user_id).child("show_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            if (dataSnapshot.getValue().equals("true")) {


                                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                                mConvDatabase.child(list_user_id).child("seen").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue().equals(false)) {
                                            isSeen[0] = false;
                                        } else {
                                            isSeen[0] = true;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                        String data = dataSnapshot.child("message").getValue().toString();
                                        convViewHolder.setMessage(data, conv.isSeen());

                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                        if (dataSnapshot.hasChild("online")) {

                                            String userOnline = dataSnapshot.child("online").getValue().toString();

                                            if (userOnline.equals("true")) {

                                                try {
                                                    boolean check_internet = isNetworkAvailable();

                                                    if (check_internet) {


                                                        convViewHolder.setUserOnline(userOnline, null, true);
                                                    } else {
                                                        convViewHolder.setUserOnline(userOnline, null, false);
                                                    }
                                                } catch (Exception ex) {
                                                    convViewHolder.setUserOnline(userOnline, null, false);
                                                }

                                            } else {
                                                GetTimeAgo getTimeAgo = new GetTimeAgo();

                                                long lastTime = Long.parseLong(userOnline);

                                                String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, context);


                                                try {
                                                    boolean check_internet = isNetworkAvailable();

                                                    if (check_internet) {


                                                        convViewHolder.setUserOnline(userOnline, lastSeenTime, true);
                                                    } else {
                                                        convViewHolder.setUserOnline(userOnline, lastSeenTime, false);
                                                    }
                                                } catch (Exception ex) {
                                                    convViewHolder.setUserOnline(userOnline, lastSeenTime, false);
                                                }
                                            }


                                        }


                                        convViewHolder.setName(userName, isSeen[0]);
                                        convViewHolder.setUserImage(userThumb, getContext());


                                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {



                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id).child(list_user_id).child("is_update").setValue("false");
                                                chatIntent.putExtra("user_id", list_user_id);
                                                chatIntent.putExtra("user_name", userName);

                                                Pair[] pairs = new Pair[2];

                                                pairs[0] = new Pair<View, String>(convViewHolder.userImageView, "imageTransition");
                                                pairs[1] = new Pair<View, String>(convViewHolder.userNameView, "nameTransition");

                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
                                                    startActivity(chatIntent , options.toBundle());
                                                }else {
                                                    startActivity(chatIntent);
                                                }



                                            }
                                        });


                                        convViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View view) {

                                                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

                                                builder.setTitle("Are you sure to delete this item from Chats list !   ")

                                                        .setPositiveButton(" Delete ", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {


                                                                mConvDatabase.child(list_user_id).child("show_list").setValue("false");

                                                                onStart();

                                                            }
                                                        }).setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });
                                                AlertDialog alert = builder.create();
                                                alert.show();


                                                return true;
                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            } else {

                                convViewHolder.hideItemList();

                            }
                        } else {
                            convViewHolder.hideItemList();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        mConvList.setAdapter(firebaseConvAdapter);
        mRefreshLayout.setRefreshing(false);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView userNameView;
        CircleImageView userImageView;


        public ConvViewHolder(View itemView) {
            super(itemView);

            mView = itemView;


        }


        public void setMessage(String message, boolean isSeen) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);


            if (message.length() > 25) {
                userStatusView.setText(message.substring(0, 25));
            } else {
                userStatusView.setText(message);
            }

            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }


        }

        public void setName(String name, Boolean isSeen) {

            userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);


            if (!isSeen) {
                userNameView.setTypeface(userNameView.getTypeface(), Typeface.BOLD);
                userNameView.setTextColor(Color.parseColor("#396cc4"));
                userNameView.setTextSize(17);


            } else {
                userNameView.setTypeface(userNameView.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setUserImage(String thumb_image, Context ctx) {

            userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.profile).into(userImageView);


        }

        public void setUserOnline(String online_status, String lastSeenTime, Boolean isConnection) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if (lastSeenTime == null) {
                if (online_status.equals("true") && isConnection) {

                    userOnlineView.setVisibility(View.VISIBLE);

                } else {
                    userOnlineView.setVisibility(View.INVISIBLE);
                }


            } else {
                if (lastSeenTime.equals("just now")&& isConnection) {
                    userOnlineView.setVisibility(View.VISIBLE);

                } else {
                    userOnlineView.setVisibility(View.INVISIBLE);
                }
            }

        }


        public void hideItemList() {

            RelativeLayout rel = (RelativeLayout) mView.findViewById(R.id.rel_chat);

            rel.setVisibility(View.INVISIBLE);
            rel.getLayoutParams().height = 1;


        }


    }


}

