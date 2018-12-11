package com.chat.walaashaaban.chat;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;
    private SwipeRefreshLayout mRefreshLayout;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;
    private FrameLayout mFrameLayout;
    private Boolean mSetAlpha = true;
    private Boolean is_search_name = false;
    private String search_name;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mFrameLayout = (FrameLayout) mMainView.findViewById(R.id.main_fragment);

        mRefreshLayout = (SwipeRefreshLayout) mMainView.findViewById(R.id.message_swipe_layout);


        //---------------------Floating action button-----------------------


        /*
        final ImageView icon = new ImageView(getActivity());
        icon.setImageResource(R.drawable.ic_add_white_24dp);
        final FloatingActionButton fab = new FloatingActionButton.Builder(getActivity()).setContentView(icon).setBackgroundDrawable(R.drawable.circle_big).build();
        SubActionButton.Builder builder = new SubActionButton.Builder(getActivity());
        ImageView searchIcon = new ImageView(getActivity());
        searchIcon.setImageResource(R.drawable.ic_search_white_18dp);
        searchIcon.setBackground(getResources().getDrawable(R.drawable.circle_small_1));
        SubActionButton searchBtn = builder.setContentView(searchIcon).build();

        ImageView settingIcon = new ImageView(getActivity());
        settingIcon.setImageResource(R.drawable.ic_settings_white_18dp);
        settingIcon.setBackground(getResources().getDrawable(R.drawable.circle_small_2));
        SubActionButton settingBtn = builder.setContentView(settingIcon).build();

        CircleImageView usersIcon = new CircleImageView(getActivity());
        usersIcon.setImageResource(R.drawable.ic_supervisor_account_white_18dp);
        usersIcon.setBackground(getResources().getDrawable(R.drawable.circle_small_3));
        SubActionButton usersBtn = builder.setContentView(usersIcon).build();

        final FloatingActionMenu fam = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(searchBtn)
                .addSubActionView(settingBtn)
                .addSubActionView(usersBtn)
                .attachTo(fab)
                .build();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mRel.setAlpha((float) 0.3);
//                showPopup(view);
                fam.close(true);
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
                fam.close(true);
                startActivity(intentSettings);


            }
        });
        usersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentUsers = new Intent(getActivity(), UsersActivity.class);
                startActivity(intentUsers);


            }
        });


        //final RelativeLayout mRel = (RelativeLayout) view.findViewById(R.id.rel_main);

        fam.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                icon.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(icon, pvhR);
                final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.sound);

                mp.setVolume(0.5f, 0.5f);
                mp.start();

                View view = getActivity().getWindow().getDecorView();
                view.setBackgroundResource(R.color.colorAccent);

                animation.start();


            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {

                icon.setRotation(45);

                View view = getActivity().getWindow().getDecorView();
                view.setAlpha((float) 1.0);
               final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.sound);
                mp.setVolume(0.05f, 0.05f);
                mp.start();
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(icon, pvhR);
                animation.start();
            }
        });
*/

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);


        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                is_search_name = false;
                onStart();


            }
        });


        // Inflate the layout for this fragment
        return mMainView;

    }


    private String changeLetterString(String name) {


        char c = Character.toUpperCase(name.charAt(0));
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        for (int i = 1; i < name.length(); i++) {
            if (String.valueOf(name.charAt(i)).equals(" ")) {
                sb.append(Character.toUpperCase(name.charAt(i)));
                i++;
                sb.append(Character.toUpperCase(name.charAt(i)));
            } else {
                sb.append(Character.toLowerCase(name.charAt(i)));
            }
        }

        return sb.toString();
    }





    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = null;

        if (!is_search_name) {

            friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                    Friends.class,
                    R.layout.users_single_layout,
                    FriendsViewHolder.class,
                    mFriendsDatabase


            ) {
                @Override
                protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int i) {


                    //friendsViewHolder.setDate(friends.getDate());

                    final String list_user_id = getRef(i).getKey();

                    mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String userName = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                            String userStatus = dataSnapshot.child("status").getValue().toString();


                            if (userStatus.length() > 30) {
                                friendsViewHolder.setDate(userStatus.substring(0, 30));
                            } else {
                                friendsViewHolder.setDate(userStatus);
                            }

                            friendsViewHolder.setName(userName);
                            friendsViewHolder.setUserImage(userThumb, getContext());
                            //friendsViewHolder.setDate(userStatus);

                            friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void onClick(View view) {


                                    Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                    profileIntent.putExtra("user_id", list_user_id);
                                    profileIntent.putExtra("UsersActivity", "false");
                                    profileIntent.putExtra("MainActivity", "true");
                                    profileIntent.putExtra("chatActivity", "false");

                                    Pair[] pairs = new Pair[3];

                                    pairs[0] = new Pair<View, String>(friendsViewHolder.userImageView, "imageTransition");
                                    pairs[1] = new Pair<View, String>(friendsViewHolder.userNameView, "nameTransition");
                                    pairs[2] = new Pair<View, String>(friendsViewHolder.userStatusView, "statusTransition");

                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
                                        startActivity(profileIntent, options.toBundle());
                                    } else {
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
            };
        } else {


            friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                    Friends.class,
                    R.layout.users_single_layout,
                    FriendsViewHolder.class,
                    mUsersDatabase.orderByChild("name").startAt(search_name).endAt(search_name + "\uf8ff")


            ) {


                @Override
                protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int i) {


                    //friendsViewHolder.setDate(friends.getDate());


                    final String list_user_id = getRef(i).getKey();

                    mFriendsDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                        String userStatus = dataSnapshot.child("status").getValue().toString();


                                        if (userStatus.length() > 30) {
                                            friendsViewHolder.setDate(userStatus.substring(0, 30));
                                        } else {
                                            friendsViewHolder.setDate(userStatus);
                                        }

                                        friendsViewHolder.setName(userName);
                                        friendsViewHolder.setUserImage(userThumb, getContext());
                                        //friendsViewHolder.setDate(userStatus);

                                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                            @Override
                                            public void onClick(View view) {


                                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                profileIntent.putExtra("user_id", list_user_id);
                                                profileIntent.putExtra("UsersActivity", "false");
                                                profileIntent.putExtra("MainActivity", "true");
                                                profileIntent.putExtra("chatActivity", "false");

                                                Pair[] pairs = new Pair[3];

                                                pairs[0] = new Pair<View, String>(friendsViewHolder.userImageView, "imageTransition");
                                                pairs[1] = new Pair<View, String>(friendsViewHolder.userNameView, "nameTransition");
                                                pairs[2] = new Pair<View, String>(friendsViewHolder.userStatusView, "statusTransition");

                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
                                                    startActivity(profileIntent, options.toBundle());
                                                } else {
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
                            else{

                               friendsViewHolder.hideItemList();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            };

        }
        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
        mRefreshLayout.setRefreshing(false);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView userNameView, userStatusView;
        CircleImageView userImageView;


        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date) {

            userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name) {

            userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx) {

            userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.profile).into(userImageView);

        }


        public void hideItemList() {

            RelativeLayout rel = (RelativeLayout) mView.findViewById(R.id.rel_chat);

            rel.setVisibility(View.INVISIBLE);
            rel.getLayoutParams().height = 1;


        }


    }
}





