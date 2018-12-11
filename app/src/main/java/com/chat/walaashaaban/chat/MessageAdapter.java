package com.chat.walaashaaban.chat;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {



    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mMessageDatabase;
    public FirebaseAuth mAuth;
    Bundle savedInstanceState;
    int view = 0;
    View v = null;
    Context context;
    private String image_chat;


    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;




    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        switch (viewType) {

            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_single_layout_me, parent, false);
                break;

            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_single_layout_other, parent, false);
                break;

        }
        return new MessageViewHolder(v);
    }



    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        public EmojiconTextView messageText;
        public CircleImageView profileImage;
        public ImageView imgChat, zoomMap;
        private RelativeLayout mRel;
        public TextView txtLocation;
        public CardView cardView, cardViewMap;
        public MapView mMapView;

        private int position = 0;
        private MediaController mediaController;
        private String videoUrl;


        public MessageViewHolder(View view) {
            super(view);
            messageText = (EmojiconTextView) view.findViewById(R.id.message_text_layout);
            messageText.setEmojiconSize(50);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            imgChat = (ImageView) view.findViewById(R.id.message_image_layout);
            mRel = (RelativeLayout) view.findViewById(R.id.message_single_layout);
            cardView = (CardView) view.findViewById(R.id.card_view);
            cardViewMap = (CardView) view.findViewById(R.id.card_view_map);
            mMapView = (MapView) view.findViewById(R.id.map);
            zoomMap = (ImageView) view.findViewById(R.id.zoom_map);

            //imgVideo = (ImageView) view.findViewById(R.id.img_video);
            txtLocation = (TextView) view.findViewById(R.id.txtLocation);

        }


    }


    @Override
    public void onViewRecycled(MessageViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onViewAttachedToWindow(MessageViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.mMapView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intentMap = new Intent(holder.mMapView.getContext(), MapsActivity2.class);
                holder.mMapView.getContext().startActivity(intentMap);
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, final int i) {

        final Messages c = mMessageList.get(i);

        final String from_user = c.getFrom();
        String message_type = c.getType();



        context = viewHolder.mMapView.getContext();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.profile)
                        .into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (message_type.equals("image")) {

            viewHolder.messageText.getLayoutParams().height = 0;
            viewHolder.imgChat.getLayoutParams().height = 270;
            viewHolder.cardView.setVisibility(View.VISIBLE);
            viewHolder.cardView.getLayoutParams().height = 270;
            viewHolder.cardViewMap.getLayoutParams().height = 0;
            viewHolder.mMapView.getLayoutParams().height = 0;

            mMessageDatabase.child(ChatActivity.mChatUser).child(c.getPush_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("thumb_image_chat").exists()) {
                        String image_chat = dataSnapshot.child("thumb_image_chat").getValue().toString();

                        Picasso.with(viewHolder.imgChat.getContext()).load(image_chat)
                                .placeholder(R.drawable.profile)
                                .into(viewHolder.imgChat);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            mMessageDatabase.child(ChatActivity.mCurrentUserId).child(c.getPush_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("thumb_image_chat").exists()) {
                        String image_chat = dataSnapshot.child("thumb_image_chat").getValue().toString();

                        Picasso.with(viewHolder.imgChat.getContext()).load(image_chat)
                                .placeholder(R.drawable.profile)
                                .into(viewHolder.imgChat);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        if (message_type.equals("text")) {
            viewHolder.imgChat.getLayoutParams().height = 0;
            viewHolder.cardView.getLayoutParams().height = 0;
            viewHolder.mMapView.getLayoutParams().height = 0;
            viewHolder.cardViewMap.getLayoutParams().height = 0;

            viewHolder.messageText.setPaintFlags(viewHolder.messageText.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            viewHolder.messageText.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;


        }
        if (message_type.equals("location")) {
            viewHolder.imgChat.getLayoutParams().height = 0;
            viewHolder.cardView.getLayoutParams().height = 0;
            viewHolder.messageText.getLayoutParams().height = 0;
            viewHolder.mMapView.setVisibility(View.VISIBLE);
            viewHolder.cardViewMap.setVisibility(View.VISIBLE);
            viewHolder.cardViewMap.getLayoutParams().height = 270;
            viewHolder.mMapView.getLayoutParams().height = 270;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }

            if (viewHolder.mMapView != null) {

                viewHolder.mMapView.onCreate(null);
                viewHolder.mMapView.onResume();
                viewHolder.mMapView.getMapAsync(this);
            }

        }



        int count = 0;
        char[] toChar = c.getMessage().toCharArray();
        for (char c1 : toChar) {
            if (!Character.isLetter(c1)) {
                count++;
            } else {
                break;
            }
        }
        if (c.getMessage().length() == count) {
            viewHolder.messageText.setBackgroundResource(R.color.shafaf);
        } else if(ChatActivity.mChatUser.equals(from_user)){
            viewHolder.messageText.setBackgroundResource(R.drawable.messege_text_background_other);
        }
        else {
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);

            FirebaseDatabase.getInstance().getReference().child("color_conversation").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GradientDrawable drawable = (GradientDrawable) viewHolder.messageText.getBackground();
                    if(dataSnapshot.child(ChatActivity.mCurrentUserId).exists()){
                        if (dataSnapshot.child(ChatActivity.mCurrentUserId).child(ChatActivity.mChatUser).exists()){

                            String color = dataSnapshot.child(ChatActivity.mCurrentUserId).child(ChatActivity.mChatUser)
                                    .child("background").getValue().toString();

                            drawable.setColor(Color.parseColor(color));

                        }else{


                            drawable.setColor(Color.parseColor("#277ae6"));
                        }
                    }
                    else {


                        drawable.setColor(Color.parseColor("#277ae6"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        viewHolder.messageText.setText(c.getMessage());

        viewHolder.messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

//        viewHolder.imgVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewHolder.videoView.start();
//            }
//        });

        viewHolder.imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent showImageChatIntent = new Intent(viewHolder.imgChat.getContext(), ShowImageInChatActivity.class);
                showImageChatIntent.putExtra("image_code", image_chat);
                showImageChatIntent.putExtra("user_id", ChatActivity.mChatUser);
                showImageChatIntent.putExtra("user_name", from_user);
                showImageChatIntent.putExtra("position", i);

                viewHolder.imgChat.buildDrawingCache();
                Bitmap bitmap = viewHolder.imgChat.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                showImageChatIntent.putExtra("bitmapbytes", bytes);


                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<View, String>(viewHolder.imgChat, "imageTransition");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) viewHolder.imgChat.getContext(), pairs);
                    viewHolder.imgChat.getContext().startActivity(showImageChatIntent, options.toBundle());
                } else {
                    viewHolder.imgChat.getContext().startActivity(showImageChatIntent);
                }






            }
        });




        viewHolder.zoomMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMap = new Intent(viewHolder.zoomMap.getContext(), MapsActivity2.class);
                FirebaseDatabase.getInstance().getReference().child("messages")
                        .child(ChatActivity.mCurrentUserId).child(ChatActivity.mChatUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        intentMap.putExtra("latitude", dataSnapshot.child(c.getPush_id()).child("latitude").getValue().toString());
                        intentMap.putExtra("longitude", dataSnapshot.child(c.getPush_id()).child("longitude").getValue().toString());
                        intentMap.putExtra("from", dataSnapshot.child(c.getPush_id()).child("from").getValue().toString());
                        viewHolder.zoomMap.getContext().startActivity(intentMap);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //viewHolder.zoomMap.getContext().startActivity(intentMap);




            }
        });

        viewHolder.txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMap = new Intent(viewHolder.txtLocation.getContext(), MapsActivity2.class);
                FirebaseDatabase.getInstance().getReference().child("messages")
                        .child(ChatActivity.mCurrentUserId).child(ChatActivity.mChatUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        intentMap.putExtra("latitude", dataSnapshot.child(c.getPush_id()).child("latitude").getValue().toString());
                        intentMap.putExtra("longitude", dataSnapshot.child(c.getPush_id()).child("longitude").getValue().toString());
                        intentMap.putExtra("from", dataSnapshot.child(c.getPush_id()).child("from").getValue().toString());
                        viewHolder.txtLocation.getContext().startActivity(intentMap);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //viewHolder.zoomMap.getContext().startActivity(intentMap);




            }
        });


        viewHolder.imgChat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                CharSequence optionsMe[] = new CharSequence[]{"Delete Image"};
                CharSequence optionsOther[] = new CharSequence[]{"Hide Image"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("Select Options");
                if (ChatActivity.mChatUser.equals(from_user)) {
                    builder.setItems(optionsOther, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i2) {

                            //Click Event for each item.
                            if (i2 == 0) {
                                mMessageList.remove(i);
                                FirebaseDatabase.getInstance().getReference().child("messages")
                                        .child(mAuth.getCurrentUser().getUid()).child(from_user).child(c.getPush_id()).removeValue();

                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, mMessageList.size());


                            }
                        }
                    });
                } else {
                    builder.setItems(optionsMe, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i1) {

                            mMessageList.remove(i);
                            ChatActivity.isUpdate = true;
                            FirebaseDatabase.getInstance().getReference().child("messages")
                                    .child(from_user).child(ChatActivity.mChatUser).child(c.getPush_id()).removeValue();

                            FirebaseDatabase.getInstance().getReference().child("messages")
                                    .child(ChatActivity.mChatUser).child(from_user).child(c.getPush_id()).removeValue();

                            notifyItemRemoved(i);
                            notifyItemRangeChanged(i, mMessageList.size());


                        }
                    });
                }

                builder.show();

                return false;
            }
        });

        viewHolder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                CharSequence optionsMe[] = new CharSequence[]{"Edit Message", "Delete Message"};
                CharSequence optionsOther[] = new CharSequence[]{"Hide Message"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("Select Options");


                if (ChatActivity.mChatUser.equals(from_user)) {
                    builder.setItems(optionsOther, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i2) {

                            //Click Event for each item.
                            if (i2 == 0) {
                                mMessageList.remove(i);
                                FirebaseDatabase.getInstance().getReference().child("messages")
                                        .child(mAuth.getCurrentUser().getUid()).child(from_user).child(c.getPush_id()).removeValue();

                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, mMessageList.size());


                            }
                        }
                    });
                } else {
                    builder.setItems(optionsMe, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i1) {

                            //Click Event for each item.
                            if (i1 == 0) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
                                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_edit_message, null, false);
                                dialogBuilder.setView(dialogView);

                                final EditText edt = (EditText) dialogView.findViewById(R.id.msgUpdates);
                                edt.setText(c.getMessage());


                                dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {


                                        FirebaseDatabase.getInstance().getReference().child("messages")
                                                .child(ChatActivity.mChatUser).child(from_user).child(c.getPush_id()).child("message").setValue(edt.getText().toString().trim());

                                        FirebaseDatabase.getInstance().getReference().child("messages")
                                                .child(from_user).child(ChatActivity.mChatUser).child(c.getPush_id()).child("message").setValue(edt.getText().toString().trim());

                                        FirebaseDatabase.getInstance().getReference().child("Chat").child(ChatActivity.mCurrentUserId).child(ChatActivity.mChatUser).child("is_update").setValue("true");

                                        FirebaseDatabase.getInstance().getReference().child("Chat").child(ChatActivity.mChatUser).child(ChatActivity.mCurrentUserId).child("is_update").setValue("true");

                                        ChatActivity.isUpdate = true;
                                        viewHolder.messageText.setText(edt.getText().toString());
                                        viewHolder.messageText.getContext().startActivity(new Intent(viewHolder.messageText.getContext(), MainActivity.class));



                                    }
                                });
                                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //pass
                                    }
                                });
                                AlertDialog b = dialogBuilder.create();
                                b.show();


                            }

                            if (i1 == 1) {

                                mMessageList.remove(i);
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, mMessageList.size());
                                viewHolder.messageText.getLayoutParams().height = 0;
                                ChatActivity.isUpdate = true;
                                FirebaseDatabase.getInstance().getReference().child("messages")
                                        .child(from_user).child(ChatActivity.mChatUser).child(c.getPush_id()).removeValue();

                                FirebaseDatabase.getInstance().getReference().child("messages")
                                        .child(ChatActivity.mChatUser).child(from_user).child(c.getPush_id()).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Chat").child(ChatActivity.mCurrentUserId).child(ChatActivity.mChatUser).child("is_update").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("Chat").child(ChatActivity.mChatUser).child(ChatActivity.mCurrentUserId).child("is_update").setValue("true");
                                viewHolder.messageText.getContext().startActivity(new Intent(viewHolder.messageText.getContext(), MainActivity.class));




                            }


                        }
                    });
                }

                builder.show();

                return false;
            }
        });


    }





    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    @Override
    public int getItemViewType(int position) {


        mAuth = FirebaseAuth.getInstance();
        Messages c = mMessageList.get(position);


        if (!c.getFrom().equals(mAuth.getCurrentUser().getUid())) {
            view = 2;

        } else {
            view = 1;
        }
        return view;

    }





    //__________________Location________________


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            if (mGoogleApiClient != null) {
                mGoogleApiClient.unregisterConnectionCallbacks(this);
                mGoogleApiClient.unregisterConnectionFailedListener(this);

                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }

                mGoogleApiClient.disconnect();
                mGoogleApiClient = null;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        Log.e(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        markerOptions.title("Current Position");
        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pic);

        int height = 50;
        int width = 50;
        BitmapDrawable bitmapdraw= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bitmapdraw = (BitmapDrawable)context.getDrawable(R.drawable.plus);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));


            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(50));

            //stop location updates
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission((Activity) context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);

            }
        }
        else {
            buildGoogleApiClient();
            //mMap.setMyLocationEnabled(true);

        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder((Activity) context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }





    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }


    }

}
