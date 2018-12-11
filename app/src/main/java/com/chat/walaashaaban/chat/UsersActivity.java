package com.chat.walaashaaban.chat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikelau.countrypickerx.Country;
import com.mikelau.countrypickerx.CountryPickerCallbacks;
import com.mikelau.countrypickerx.CountryPickerDialog;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference mUsersDatabaseReference;
    private ProgressDialog mProgressDialog;
    private ImageView btnSearch;
    private EditText getNameSearch;
    private FloatingActionMenu mFloatingMenu;
    private FloatingActionButton mFloatingMale, mFloatingFemale, mFloatingFlag;

    private FirebaseAuth mAuth;
    private String mUser;
    private Boolean isSearch = false;
    private Boolean isMale = false;
    private Boolean isFemale = false;
    private Boolean isMenu = false;
    private Boolean isSelectCountry = false;
    public static String country;

    private int position;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);



        mToolbar = (Toolbar) findViewById(R.id.users_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSearch = (ImageView) findViewById(R.id.imgSearch);
        getNameSearch = (EditText) findViewById(R.id.etSearchName);
        mFloatingMenu = (FloatingActionMenu) findViewById(R.id.menu_filter);
        mFloatingMale = (FloatingActionButton) findViewById(R.id.filterMale);
        mFloatingFemale = (FloatingActionButton) findViewById(R.id.filterFemale);
        mFloatingFlag = (FloatingActionButton) findViewById(R.id.filterFlag);
        position = getIntent().getIntExtra("position", 0);
        isMale = getIntent().getBooleanExtra("isMale", false);
        isFemale = getIntent().getBooleanExtra("isFemale", false);
        isMenu = getIntent().getBooleanExtra("isMenu", false);
        isSelectCountry = getIntent().getBooleanExtra("isSelectCountry", false);
        UsersActivity.country = getIntent().getStringExtra("country_iso");




        mProgressDialog = new ProgressDialog(UsersActivity.this);
        mProgressDialog.setTitle("Loading User Data ...");
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();



        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(getNameSearch.getText().toString().trim())) {
                    isSearch = true;
                    isMenu = false;
                    isFemale = false;
                    isMale = false;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    onStart();
                }
            }


        });



                mFloatingMale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isMale = true;
                        isFemale = false;
                        isMenu = true;
                        isSearch = false;
                        getNameSearch.setText("");
                        mFloatingMenu.close(true);
                        onStart();
                    }
                });

        mFloatingFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFemale = true;
                isMale = false;
                isMenu = true;
                isSearch = false;
                getNameSearch.setText("");
                mFloatingMenu.close(true);
                onStart();

            }
        });

        mFloatingFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(UsersActivity.this, new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country countryNow, int flagResId) {
                                UsersActivity.country = countryNow.getIsoCode().toLowerCase().toString();
                                isFemale = false;
                                isMale = false;
                                isMenu = true;
                                isSearch = false;
                                isSelectCountry = true;
                                mFloatingMenu.close(true);
                                onStart();

                            }
                        }, false, 0);
                countryPicker.show();


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = null;

        if (!isMenu) {
            if (!isSearch) {
                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                        Users.class,
                        R.layout.user_single_layout,
                        UsersViewHolder.class,
                        mUsersDatabaseReference

                ) {
                    @Override
                    protected void populateViewHolder(final UsersViewHolder usersViewHolder, Users users, final int position) {

                        final String user_id = getRef(position).getKey();


                        usersViewHolder.setName(users.getName());

                        if (users.getStatus().length() > 30) {
                            usersViewHolder.setStatus(users.getStatus().substring(0, 30));
                        } else {
                            usersViewHolder.setStatus(users.getStatus());
                        }

                        //usersViewHolder.setStatus(users.getStatus());
                        usersViewHolder.setImage(users.getImage(), UsersActivity.this);

                        // for if click item in user List All List


                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("user_id", user_id);
                                profileIntent.putExtra("UsersActivity", "true");
                                profileIntent.putExtra("MainActivity", "false");
                                profileIntent.putExtra("chatActivity", "false");
                                profileIntent.putExtra("position", position);


                                Pair[] pairs = new Pair[3];

                                pairs[0] = new Pair<View, String>(usersViewHolder.imgView, "imageTransition");
                                pairs[1] = new Pair<View, String>(usersViewHolder.txtName, "nameTransition");
                                pairs[2] = new Pair<View, String>(usersViewHolder.txtStatus, "statusTransition");

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UsersActivity.this, pairs);
                                    startActivity(profileIntent , options.toBundle());
                                }else {
                                    startActivity(profileIntent);
                                }

                            }
                        });


                    }
                };
            } else {


                String nameSearch = changeLetterString(getNameSearch.getText().toString().trim());
                if (!TextUtils.isEmpty(getNameSearch.getText().toString().trim())) {

                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                            Users.class,
                            R.layout.user_single_layout,
                            UsersViewHolder.class,
                            mUsersDatabaseReference.orderByChild("name").startAt(nameSearch).endAt(nameSearch + "\uf8ff")

                    ) {
                        @Override
                        protected void populateViewHolder(final UsersViewHolder usersViewHolder, Users users, final int position) {

                            final String user_id = getRef(position).getKey();

                            usersViewHolder.setName(users.getName());
                            usersViewHolder.setStatus(users.getStatus());
                            usersViewHolder.setImage(users.getImage(), UsersActivity.this);

                            // for if click item in user List All List


                            usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                    profileIntent.putExtra("user_id", user_id);
                                    profileIntent.putExtra("UsersActivity", "true");
                                    profileIntent.putExtra("MainActivity", "false");
                                    profileIntent.putExtra("chatActivity", "false");
                                    profileIntent.putExtra("position", position);
                                    profileIntent.putExtra("isMale", false);
                                    profileIntent.putExtra("isFemale", false);
                                    profileIntent.putExtra("isMenu", false);


                                    Pair[] pairs = new Pair[3];

                                    pairs[0] = new Pair<View, String>(usersViewHolder.imgView, "imageTransition");
                                    pairs[1] = new Pair<View, String>(usersViewHolder.txtName, "nameTransition");
                                    pairs[2] = new Pair<View, String>(usersViewHolder.txtStatus, "statusTransition");

                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UsersActivity.this, pairs);
                                        startActivity(profileIntent , options.toBundle());
                                    }else {
                                        startActivity(profileIntent);
                                    }
                                }
                            });


                        }
                    };

                } else {

                    isSearch = false;
                    onStart();
                }
            }

            RecyclerView mUsersList;
            mUsersList = (RecyclerView) findViewById(R.id.users_list);
            mUsersList.setHasFixedSize(true);
            mUsersList.setLayoutManager(new LinearLayoutManager(UsersActivity.this));

            mUsersList.smoothScrollToPosition(position);

            mUsersList.setAdapter(firebaseRecyclerAdapter);

            mProgressDialog.dismiss();

        } else {
            if (isMale) {

                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                        Users.class,
                        R.layout.user_single_layout,
                        UsersViewHolder.class,
                        mUsersDatabaseReference.orderByChild("gender").equalTo("Male")

                ) {
                    @Override
                    protected void populateViewHolder(final UsersViewHolder usersViewHolder, Users users, final int position) {

                        final String user_id = getRef(position).getKey();

                        usersViewHolder.setName(users.getName());
                        usersViewHolder.setStatus(users.getStatus());
                        usersViewHolder.setImage(users.getImage(), UsersActivity.this);

                        // for if click item in user List All List


                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("user_id", user_id);
                                profileIntent.putExtra("UsersActivity", "true");
                                profileIntent.putExtra("MainActivity", "false");
                                profileIntent.putExtra("chatActivity", "false");
                                profileIntent.putExtra("position", position);
                                profileIntent.putExtra("isMale", true);
                                profileIntent.putExtra("isFemale", false);
                                profileIntent.putExtra("isMenu", true);
                                profileIntent.putExtra("isSelectCountry", false);




                                Pair[] pairs = new Pair[3];

                                pairs[0] = new Pair<View, String>(usersViewHolder.imgView, "imageTransition");
                                pairs[1] = new Pair<View, String>(usersViewHolder.txtName, "nameTransition");
                                pairs[2] = new Pair<View, String>(usersViewHolder.txtStatus, "statusTransition");

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UsersActivity.this, pairs);
                                    startActivity(profileIntent , options.toBundle());
                                }else {
                                    startActivity(profileIntent);
                                }
                            }
                        });


                    }
                };


                RecyclerView mUsersList;
                mUsersList = (RecyclerView) findViewById(R.id.users_list);
                mUsersList.setHasFixedSize(true);
                mUsersList.setLayoutManager(new LinearLayoutManager(UsersActivity.this));

                mUsersList.smoothScrollToPosition(position);

                mUsersList.setAdapter(firebaseRecyclerAdapter);

                mProgressDialog.dismiss();


            }

            if (isFemale) {
                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                        Users.class,
                        R.layout.user_single_layout,
                        UsersViewHolder.class,
                        mUsersDatabaseReference.orderByChild("gender").equalTo("Female")

                ) {
                    @Override
                    protected void populateViewHolder(final UsersViewHolder usersViewHolder, Users users, final int position) {

                        final String user_id = getRef(position).getKey();

                        usersViewHolder.setName(users.getName());
                        usersViewHolder.setStatus(users.getStatus());
                        usersViewHolder.setImage(users.getImage(), UsersActivity.this);

                        // for if click item in user List All List


                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("user_id", user_id);
                                profileIntent.putExtra("UsersActivity", "true");
                                profileIntent.putExtra("MainActivity", "false");
                                profileIntent.putExtra("chatActivity", "false");
                                profileIntent.putExtra("position", position);
                                profileIntent.putExtra("isMale", false);
                                profileIntent.putExtra("isFemale", true);
                                profileIntent.putExtra("isMenu", true);
                                profileIntent.putExtra("isSelectCountry", false);



                                Pair[] pairs = new Pair[3];

                                pairs[0] = new Pair<View, String>(usersViewHolder.imgView, "imageTransition");
                                pairs[1] = new Pair<View, String>(usersViewHolder.txtName, "nameTransition");
                                pairs[2] = new Pair<View, String>(usersViewHolder.txtStatus, "statusTransition");

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UsersActivity.this, pairs);
                                    startActivity(profileIntent , options.toBundle());
                                }else {
                                    startActivity(profileIntent);
                                }
                            }
                        });


                    }
                };


                RecyclerView mUsersList;
                mUsersList = (RecyclerView) findViewById(R.id.users_list);
                mUsersList.setHasFixedSize(true);
                mUsersList.setLayoutManager(new LinearLayoutManager(UsersActivity.this));

                mUsersList.smoothScrollToPosition(position);

                mUsersList.setAdapter(firebaseRecyclerAdapter);

                mProgressDialog.dismiss();
            }

            if(isSelectCountry){

                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                        Users.class,
                        R.layout.user_single_layout,
                        UsersViewHolder.class,
                        mUsersDatabaseReference.orderByChild("flag").equalTo(UsersActivity.country)

                ) {
                    @Override
                    protected void populateViewHolder(final UsersViewHolder usersViewHolder, Users users, final int position) {

                        final String user_id = getRef(position).getKey();

                        usersViewHolder.setName(users.getName());
                        usersViewHolder.setStatus(users.getStatus());
                        usersViewHolder.setImage(users.getImage(), UsersActivity.this);

                        // for if click item in user List All List


                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("user_id", user_id);
                                profileIntent.putExtra("UsersActivity", "true");
                                profileIntent.putExtra("MainActivity", "false");
                                profileIntent.putExtra("chatActivity", "false");
                                profileIntent.putExtra("position", position);
                                profileIntent.putExtra("isMale", false);
                                profileIntent.putExtra("isFemale", false);
                                profileIntent.putExtra("isMenu", true);
                                profileIntent.putExtra("isSelectCountry", true);
                                profileIntent.putExtra("country_iso", UsersActivity.country);



                                Pair[] pairs = new Pair[3];

                                pairs[0] = new Pair<View, String>(usersViewHolder.imgView, "imageTransition");
                                pairs[1] = new Pair<View, String>(usersViewHolder.txtName, "nameTransition");
                                pairs[2] = new Pair<View, String>(usersViewHolder.txtStatus, "statusTransition");

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UsersActivity.this, pairs);
                                    startActivity(profileIntent , options.toBundle());
                                }else {
                                    startActivity(profileIntent);
                                }
                            }
                        });


                    }
                };


                RecyclerView mUsersList;
                mUsersList = (RecyclerView) findViewById(R.id.users_list);
                mUsersList.setHasFixedSize(true);
                mUsersList.setLayoutManager(new LinearLayoutManager(UsersActivity.this));

                mUsersList.smoothScrollToPosition(position);

                mUsersList.setAdapter(firebaseRecyclerAdapter);

                mProgressDialog.dismiss();
            }
        }



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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (isSearch || isFemale || isMale || isSelectCountry) {
            Intent thisIntent = new Intent(UsersActivity.this, UsersActivity.class);
            startActivity(thisIntent);
            finish();
        } else {
            Intent backIntent = new Intent(UsersActivity.this, MainActivity.class);

            startActivity(backIntent);
            finish();
        }
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView txtName, txtStatus;
        CircleImageView imgView;


        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name) {

            txtName = (TextView) mView.findViewById(R.id.user_single_name);
            txtName.setText(name);
        }

        public void setStatus(String status) {

            txtStatus = (TextView) mView.findViewById(R.id.user_single_status);
            txtStatus.setText(status);
        }

        public void setImage(String image, UsersActivity usersActivity) {

            imgView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(usersActivity).load(image).placeholder(R.drawable.profile).into(imgView);

        }
    }
}
