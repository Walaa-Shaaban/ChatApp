package com.chat.walaashaaban.chat;

import android.*;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;
import petrov.kristiyan.colorpicker.ColorPicker;
import pl.droidsonroids.gif.GifImageView;




public class ChatActivity extends AppCompatActivity implements OnMenuItemClickListener {

    public static String mChatUser;
    public static String mCurrentUserId;
    private Toolbar mChatToolbar;

    public static Boolean isUpdate = false;

    private DatabaseReference mRootRef;
    private DatabaseReference mNotificationMessage;

    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;


    private FloatingActionButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EmojiconEditText mChatMessageView;
    private String mGetMessage;
    private RelativeLayout relativeChat;
    private GifImageView gifImageView;
    private DatabaseReference mDatabase, mBlockDatabaseCurrent, mBlockDatabaseUid;
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    public static MessageAdapter mAdapter;

    //storage
    private StorageReference mStorageRef;
    private static final int GALLERY_PICK_IMAGES_CHAT = 1;
    private Boolean isImageChat = true;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1, position = -1;

    String userName;
    private ImageView emojiButton;
    private EmojIconActions emojIcon;
    private EmojiconEditText emojiconEditText;
    View rootView;
    private LinearLayout lnBlock;

    //New Solution
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";

    private Boolean changeImageBackground = false;
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;



    private RelativeLayout layoutMain;
    private RelativeLayout layoutButtons;
    private RelativeLayout layoutContent;
    private boolean isOpen = false;
    private ImageButton downCircle, location, gallery;

    private String thumb_image;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        relativeChat = (RelativeLayout) findViewById(R.id.relative_chat);
        gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        mChatAddBtn = (FloatingActionButton) findViewById(R.id.chat_add_btn);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.chat_message_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        lnBlock = (LinearLayout) findViewById(R.id.linear_unblock);
        rootView = findViewById(R.id.relative_chat);
        emojIcon = new EmojIconActions(ChatActivity.this, rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("messages");
        mBlockDatabaseCurrent = FirebaseDatabase.getInstance().getReference().child("block_chat");
        mBlockDatabaseUid = FirebaseDatabase.getInstance().getReference().child("block_chat");

        position = getIntent().getIntExtra("position", -1);
        layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
        layoutButtons = (RelativeLayout) findViewById(R.id.layoutButtons);
        layoutContent = (RelativeLayout) findViewById(R.id.layoutContent);
        downCircle = (ImageButton) findViewById(R.id.down_btn);
        gallery = (ImageButton) findViewById(R.id.gallery_img_btn);



        mBlockDatabaseCurrent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(mCurrentUserId).exists()){
                    if(dataSnapshot.child(mCurrentUserId).child(mChatUser).exists()){
                        lnBlock.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBlockDatabaseUid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(mChatUser).exists()){
                    if(dataSnapshot.child(mChatUser).child(mCurrentUserId).exists()){
                        lnBlock.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );



        downCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOpen = true;
                viewMenu();
            }
        });


//        location.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onClick(View view) {
//
//                String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
//                String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;
//
//                DatabaseReference user_message_push = mRootRef.child("messages")
//                        .child(mCurrentUserId).child(mChatUser).push();
//
//                String push_id = user_message_push.getKey();
//
//                Map messageMap = new HashMap();
//                messageMap.put("message", "Location has been sent");
//                messageMap.put("seen", false);
//                messageMap.put("type", "location");
//                messageMap.put("time", ServerValue.TIMESTAMP);
//                messageMap.put("from", mCurrentUserId);
//                messageMap.put("push_id", push_id);
//                messageMap.put("thumb_image_chat", "");
//                messageMap.put("latitude", getBestLastKnownLocation(ChatActivity.this).getLatitude());
//                messageMap.put("longitude", getBestLastKnownLocation(ChatActivity.this).getLongitude());
//
//                Map messageUserMap = new HashMap();
//                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
//                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
//
//                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                        if (databaseError != null) {
//
//                            Log.d("CHAT_LOG", databaseError.getMessage().toString());
//
//                        }
//
//                    }
//                });
//
//
//                    }
//
//
//        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                isImageChat = true;
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK_IMAGES_CHAT);
            }
        });






        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mChatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backMenu();
            }
        });

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mNotificationMessage = mRootRef.child("Notification_message");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();


        mChatUser = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----

        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EmojiconEditText) findViewById(R.id.chat_message_view);


        mChatMessageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:

                        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child(("isKeyboardOpen")).setValue("true");

                        break;

                    case MotionEvent.ACTION_CANCEL:

                        break;
                }

                return false;
            }

        });


        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                Picasso.with(ChatActivity.this).load(thumb_image).placeholder(R.drawable.profile).into(mProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
        addFragment(new MainFragment(), true, R.id.container);
        mGetMessage = mChatMessageView.getText().toString().trim();
        mAdapter = new MessageAdapter(messagesList);


        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child(("isKeyboardOpen")).setValue("false");


        mMessagesList.setAdapter(mAdapter);


        loadMessages();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();


            }
        });


        mTitleView.setText(userName);

        boolean check_internet = isNetworkAvailable(getApplicationContext());

        if (check_internet) {

            final Handler mHandler = new Handler();
            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    String online = dataSnapshot.child("online").getValue().toString();
                                    String image = dataSnapshot.child("image").getValue().toString();

                                    if (online.equals("true")) {

                                        mLastSeenView.setText("Online");

                                    } else {

                                        GetTimeAgo getTimeAgo = new GetTimeAgo();

                                        long lastTime = Long.parseLong(online);

                                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                                        mLastSeenView.setText(lastSeenTime);

                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }


                            });


                            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child(("isKeyboardOpen")).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (dataSnapshot.getValue().equals("true")) {
                                            gifImageView.setVisibility(View.VISIBLE);

                                        } else {
                                            gifImageView.setVisibility(View.INVISIBLE);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            if(changeImageBackground){
                                setBackground();
                                changeImageBackground = false;
                            }


//                            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.child("is_update").exists()) {
//                                        String is_update = dataSnapshot.child("is_update").getValue().toString();
//                                        if (is_update.equals("true")) {
//                                            startActivity(new Intent(ChatActivity.this, MainActivity.class));
//
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.child("is_update").exists()) {
//                                        String is_update = dataSnapshot.child("is_update").getValue().toString();
//                                        if (is_update.equals(true)) {
//                                            startActivity(new Intent(ChatActivity.this, MainActivity.class));
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });

                        }
                    });
                }
            }, 0, 100);
        } else {
            mLastSeenView.setText("");
            gifImageView.setVisibility(View.INVISIBLE);
        }


        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setBackground();


        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean check_internet = isNetworkAvailable(getApplicationContext());

                if (check_internet) {

                    sendMessage();


                    HashMap<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("show_list", "true");
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child(("isKeyboardOpen")).setValue("false");

                    mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).setValue(chatAddMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                            }
                        }
                    });


                    mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).setValue(chatAddMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                            }
                        }
                    });

                } else {
                    Toast.makeText(ChatActivity.this, "Error in Network ... please try again ", Toast.LENGTH_LONG).show();
                }
            }
        });


        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMenu();

            }
        });
    }
    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);

    }

    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.menu_three);
        close.setBgResource(R.color.color11);

        MenuObject send = new MenuObject("Open Profile");
        send.setResource(R.drawable.ic_account_box_white_18dp);
        send.setBgResource(R.color.color14);



        MenuObject addFr = new MenuObject("Change background color");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_color_lens_white_24dp));
        addFr.setDrawable(bd);
        addFr.setBgResource(R.color.color1);

        MenuObject addColorConv = new MenuObject("Chatting color change");
        addColorConv.setResource(R.drawable.color_conversation);
        addColorConv.setBgResource(R.color.color13);



        MenuObject addFav = new MenuObject("All Images");
        addFav.setResource(R.drawable.ic_camera_front_white_24dp);
        addFav.setBgResource(R.color.color7);

        MenuObject delete_conv = new MenuObject("Delete Conversation");
        delete_conv.setResource(R.drawable.icons8_delete_message_26);
        delete_conv.setBgResource(R.color.color2);

        MenuObject block = new MenuObject("Block ");
        block.setResource(R.drawable.ic_block_white_24dp);
        block.setBgResource(R.color.color9);


        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(addFr);
        menuObjects.add(addColorConv);
        menuObjects.add(addFav);
        menuObjects.add(delete_conv);
        menuObjects.add(block);
        return menuObjects;
    }




    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }


    private void setBackground() {
        mRootRef.child("background_chat").child(mCurrentUserId).child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {

                    if (!dataSnapshot.child("background_upload").exists() || dataSnapshot.child("background_upload").getValue().equals("Empty")) {
                        if (dataSnapshot.child("is_image").exists()) {
                            if (dataSnapshot.child("is_image").getValue().equals(false)) {
                                relativeChat.setBackgroundColor(Color.parseColor(dataSnapshot.child("background").getValue().toString()));
                            }

                        } else {
                            relativeChat.setBackgroundResource(R.color.white);
                        }
                    } else{
                        Picasso.with(ChatActivity.this).load(dataSnapshot.child("background_upload").getValue().toString()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                relativeChat.setBackgroundDrawable(new BitmapDrawable(
                                        bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }



                }else {
                        relativeChat.setBackgroundResource(R.color.white);
                    }




                }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if (itemPos == 1) {

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
                if (position != -1) {

                    mMessagesList.smoothScrollToPosition(position);
                } else {

                    mMessagesList.scrollToPosition(messagesList.size() - 1);
                }

                mRefreshLayout.setRefreshing(false);

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


    }

    private String setDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH);
        Date date = new Date();
        String strDate = dateFormat.format(date).toString();
        return strDate;
    }

    private void sendMessage() {


        String message = mChatMessageView.getText().toString().trim();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();



            String push_id = user_message_push.getKey();

            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);
            messageMap.put("push_id", push_id);
            messageMap.put("init_date", setDate());

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK_IMAGES_CHAT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();


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

                if (isImageChat) {

                    //upload image compress
                    final StorageReference filePath_thumb = mStorageRef.child("chat_images_messages").child("thumbs").child(random() + ".jpg");


                    filePath_thumb.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                //final String downloadUrl = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = filePath_thumb.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        if (thumb_task.isSuccessful()) {

                                            String downloadUrl_thumb = thumb_task.getResult().getDownloadUrl().toString();


                                            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                                            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

                                            DatabaseReference user_message_push = mRootRef.child("messages")
                                                    .child(mCurrentUserId).child(mChatUser).push();

                                            String push_id = user_message_push.getKey();

                                            Map messageMap = new HashMap();
                                            messageMap.put("message", "A photo has been sent");
                                            messageMap.put("seen", false);
                                            messageMap.put("type", "image");
                                            messageMap.put("time", ServerValue.TIMESTAMP);
                                            messageMap.put("from", mCurrentUserId);
                                            messageMap.put("push_id", push_id);
                                            messageMap.put("thumb_image_chat", downloadUrl_thumb);

                                            Map messageUserMap = new HashMap();
                                            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                                            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                    if (databaseError != null) {

                                                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                                    }

                                                }
                                            });


                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(ChatActivity.this, "Error for uploading Image", Toast.LENGTH_LONG).show();
                            }


                        }
                    });
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                } else {


                    //upload image compress
                    final StorageReference filePath_thumb = mStorageRef.child("chat_background").child("thumbs").child(random() + ".jpg");


                    filePath_thumb.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                //final String downloadUrl = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = filePath_thumb.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        if (thumb_task.isSuccessful()) {

                                            String downloadUrl_thumb = thumb_task.getResult().getDownloadUrl().toString();

                                            mRootRef.child("background_chat").child(mCurrentUserId).child(mChatUser).child("background_upload").setValue(downloadUrl_thumb);
                                            changeImageBackground = true;




                                        }
                                    }
                                });
                                setBackground();


                            } else {
                                Toast.makeText(ChatActivity.this, "Error for uploading Image", Toast.LENGTH_LONG).show();
                            }


                        }
                    });
                }





                }

            }
        }



    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void backMenu(){

        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {


        Intent backIntent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(backIntent);
        finish();

    }





    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(15);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)) {

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if (itemPos == 1) {

                    mLastKey = messageKey;

                }


                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

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

    }



    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(100);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMenuItemClick(View clickedView, int position) {

        switch (position){

            case 6:

                final android.support.v7.app.AlertDialog.Builder builder_block = new android.support.v7.app.AlertDialog.Builder(this);

                builder_block.setTitle(" Are you sure you want to block this person? ! ")
                        .setPositiveButton(" Block ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mRootRef.child("block_chat").child(mCurrentUserId).child(mChatUser).child("name").setValue(userName);
                                mRootRef.child("block_chat").child(mCurrentUserId).child(mChatUser).child("thumb_image").setValue(thumb_image);



                            }
                        }).setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder_block.show();





                break;

            case 5:


                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

                builder.setTitle(" Are you sure you want to delete this conversation? ! ")
                        .setPositiveButton(" Delete ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).removeValue();

                                mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("show_list").setValue("false");


                                Intent intent = new Intent(ChatActivity.this, MainActivity.class);

                                startActivity(intent);


                            }
                        }).setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();


                break;

            case 1:

                Intent chatIntent = new Intent(ChatActivity.this, ProfileActivity.class);
                chatIntent.putExtra("user_id", mChatUser);
                chatIntent.putExtra("UsersActivity", "false");
                chatIntent.putExtra("MainActivity", "false");
                chatIntent.putExtra("chatActivity", "true");


                Pair[] pairs = new Pair[2];

                pairs[0] = new Pair<View, String>(mProfileImage, "imageTransition");
                pairs[1] = new Pair<View, String>(mTitleView, "nameTransition");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this, pairs);
                    startActivity(chatIntent, options.toBundle());
                } else {
                    startActivity(chatIntent);
                }


                break;


            case 3:
                final ColorPicker colorPickerConv = new ColorPicker(ChatActivity.this);
                ArrayList<String> colorsConv = new ArrayList<>();
                colorsConv.add("#82C2BB");
                colorsConv.add("#F8C805");
                colorsConv.add("#F9374C");
                colorsConv.add("#C59BF7");
                colorsConv.add("#7EA1D3");
                colorsConv.add("#60D306");
                colorsConv.add("#F5830A");
                colorsConv.add("#4F6AE7");
                colorsConv.add("#27B4FA");
                colorsConv.add("#9CBF36");
                colorsConv.add("#F2E2BA");
                colorsConv.add("#F961DD");
                colorsConv.add("#572DF1");
                colorsConv.add("#FA4C3B");
                colorsConv.add("#3BFAEE");
                colorsConv.add("#C6747F");
                colorsConv.add("#F96E80");


                colorPickerConv
                        .setDefaultColorButton(Color.parseColor("#BFF9D4"))
                        .setColors(colorsConv)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {


                                mRootRef.child("color_conversation").child(mCurrentUserId).child(mChatUser).child("background").setValue(colorsConv.get(position));
                                mRootRef.child("color_conversation").child(mChatUser).child(mCurrentUserId).child("background").setValue(colorsConv.get(position));
                                Intent intentChat = new Intent(ChatActivity.this, ChatActivity.class);
                                intentChat.putExtra("user_id", mChatUser);
                                intentChat.putExtra("user_name", userName);
                                startActivity(intentChat);


                            }

                            @Override
                            public void onCancel() {

                            }
                        })

                        .show();

                break;

            case 4:


                Intent all_images = new Intent(ChatActivity.this, GridChatImagesActivity.class);
                GridChatImagesActivity.lst.clear();
                startActivity(all_images);

                break;


            case 2:

                final ColorPicker colorPicker = new ColorPicker(ChatActivity.this);
                ArrayList<String> colors = new ArrayList<>();
                colors.add("#82C2BB");
                colors.add("#F8C805");
                colors.add("#F9374C");
                colors.add("#C59BF7");
                colors.add("#7EA1D3");
                colors.add("#60D306");
                colors.add("#F5830A");
                colors.add("#4F6AE7");
                colors.add("#27B4FA");
                colors.add("#9CBF36");
                colors.add("#F2E2BA");
                colors.add("#F961DD");
                colors.add("#572DF1");
                colors.add("#FA4C3B");
                colors.add("#3BFAEE");
                colors.add("#C6747F");
                colors.add("#F96E80");


                colors.add("#318AF3");
                colors.add("#B8E1C6");
                colors.add("#FFFFFF");
                colors.add("#F7CD98");
                colors.add("#F9D3F4");
                colors.add("#B8B7F5");
                colors.add("#7E7CFA");
                colors.add("#FAA3AE");
                colors.add("#000000");
                colors.add("#C9FAF0");
                colors.add("#8FC3FC");

                colorPicker
                        .setDefaultColorButton(Color.parseColor("#BFF9D4"))
                        .setColors(colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                Log.e("position", "" + colors.get(position));// will be fired only when OK button was tapped
                                relativeChat.setBackgroundColor(color);
                                mRootRef.child("background_chat").child(mCurrentUserId).child(mChatUser).child("is_image").setValue(false);
                                mRootRef.child("background_chat").child(mCurrentUserId).child(mChatUser).child("background").setValue(colors.get(position));
                                mRootRef.child("background_chat").child(mCurrentUserId).child(mChatUser).child("background_upload").setValue("Empty");

                                mRootRef.child("background_chat").child(mChatUser).child(mCurrentUserId).child("is_image").setValue(false);
                                mRootRef.child("background_chat").child(mChatUser).child(mCurrentUserId).child("background").setValue(colors.get(position));
                                mRootRef.child("background_chat").child(mChatUser).child(mCurrentUserId).child("background_upload").setValue("Empty");

                                mRootRef.child("background_chat").child(ChatActivity.mCurrentUserId).child(ChatActivity.mChatUser).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        String background = dataSnapshot.child("background").getValue().toString();
                                        if (background.equals(colors.get(position))) {
                                            setBackground();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                mRootRef.child("background_chat").child(ChatActivity.mChatUser).child(ChatActivity.mCurrentUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        String background = dataSnapshot.child("background").getValue().toString();
                                        if (background.equals(colors.get(position))) {
                                            setBackground();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                Intent intentChat1 = new Intent(ChatActivity.this, ChatActivity.class);
                                intentChat1.putExtra("user_id", mChatUser);
                                intentChat1.putExtra("user_name", userName);
                                startActivity(intentChat1);
                            }

                            @Override
                            public void onCancel() {

                            }
                        })

                        .show();



                break;

        }

    }




    private void viewMenu() {

        if (!isOpen) {

            int x = 320;
            int y = 300;

            int startRadius = 0;
            int endRadius = (int) Math.hypot(layoutMain.getWidth(), layoutMain.getHeight());

//            fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(),android.R.color.white,null)));
//            fab.setImageResource(R.drawable.pic1);

            Animator anim = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = android.view.ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
            }

            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();

            isOpen = true;

        } else {

            int x = layoutButtons.getRight();
            int y = layoutButtons.getBottom();

            int startRadius = Math.max(300, 300);
            int endRadius = 0;

//            fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(),R.color.colorAccent,null)));
//            fab.setImageResource(R.drawable.add);

            Animator anim = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = android.view.ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
            }
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    layoutButtons.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();

            isOpen = false;
        }
    }

    @SuppressLint("MissingPermission")
    public static Location getBestLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getAllProviders();
        Location location = null, temLocation;
        for (String provider : providers) {
            temLocation = locationManager.getLastKnownLocation(provider);

            if (location == null
                    || (temLocation != null && location.getTime() < temLocation
                    .getTime()))
                location = temLocation;
        }

        return location;
    }

}
