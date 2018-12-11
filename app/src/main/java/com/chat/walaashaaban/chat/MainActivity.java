package com.chat.walaashaaban.chat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;
    private RelativeLayout mRel;
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    private Boolean is_search_name = false;
    private String search_name;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.toolbar_mainPage);
        setSupportActionBar(mToolbar);

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
        addFragment(new MainFragment(), true, R.id.container);
        getSupportActionBar().setTitle("Chat");


        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);

        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mRel = (RelativeLayout) findViewById(R.id.rel_main);

        boolean check_internet = isNetworkAvailable(getApplicationContext());

        if (!check_internet) {
            Toast.makeText(MainActivity.this, "Error in Network ... please try again ", Toast.LENGTH_LONG).show();

        }




    }




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToStart();

        } else {

            mUserRef.child("online").setValue("true");

        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }



    private void sendToStart() {
        Intent startIntent = new Intent(this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//
//       return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//
//        switch (item.getItemId()){
//            case R.id.menu_logout_btn:
//                FirebaseAuth.getInstance().signOut();
//                sendToStart();
//                break;
//            case R.id.menu_setting:
//                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(intentSettings);
//                finish();
//                break;
//            case R.id.menu_all_users:
//                Intent intentUsers = new Intent(MainActivity.this, UsersActivity.class);
//
//                startActivity(intentUsers);
//
//                finish();
//                break;
//
//
//        }
//
//
//
//        return true;
//    }





    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(MainActivity.this);



    }

    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.menu_three);
        close.setBgResource(R.color.color11);

        MenuObject send = new MenuObject("Account Settings");
        send.setResource(R.drawable.ic_settings_white_18dp);
        send.setBgResource(R.color.color7);

        MenuObject like = new MenuObject("All Users");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_supervisor_account_white_24dp_1x);
        like.setBitmap(b);
        like.setBgResource(R.color.color1);


        MenuObject addFr = new MenuObject("Logout");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.white_logout_24));
        addFr.setDrawable(bd);
        addFr.setBgResource(R.color.color9);




        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        return menuObjects;
    }




    protected void addFragment(android.support.v4.app.Fragment fragment, boolean addToBackStack, int containerId) {
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

    /*
    public void showPopup(View view) {
        ImageView close;
        final Button search;
        final EditText etSearch;
        final CircleImageView imgProf;

        final Dialog myDialog;
        myDialog = new Dialog(MainActivity.this);
        myDialog.setContentView(R.layout.custompopup);
        close = (ImageView) myDialog.findViewById(R.id.closeDialog);
        search = (Button) myDialog.findViewById(R.id.btnSearch);
        imgProf = (CircleImageView) myDialog.findViewById(R.id.myPic);
        etSearch = (EditText) myDialog.findViewById(R.id.etSearchName);

        mAuth = FirebaseAuth.getInstance();
       String mCurrent_user_id = mAuth.getCurrentUser().getUid();

       DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mUsersDatabase.child(mCurrent_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("thumb_image").getValue().toString();
                if (!image.equals("defult")) {
                    Picasso.with(MainActivity.this).load(image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile)
                            .into(imgProf, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    Picasso.with(MainActivity.this).load(image)
                                            .placeholder(R.drawable.profile)
                                            .into(imgProf);

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_search_name = true;
                search_name = changeLetterString(etSearch.getText().toString().trim());
                onStart();
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
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
*/

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





        public void backMenu(){

        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }


    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {

            case 3:
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            case 1:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                finish();
                break;
            case 2:
                Intent intentUsers = new Intent(MainActivity.this, UsersActivity.class);

                startActivity(intentUsers);

                finish();
                break;

        }
    }
}
