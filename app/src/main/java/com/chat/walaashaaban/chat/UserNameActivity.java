package com.chat.walaashaaban.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserNameActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mUserName;
    private Button mSaveUserName;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        mToolbar = (Toolbar) findViewById(R.id.user_name_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Username");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSaveUserName = (Button) findViewById(R.id.btnSaveUserName);

        mUserName = (EditText) findViewById(R.id.change_user_name);
        mUserName.setText(getIntent().getStringExtra("username"));

        //update status
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mSaveUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(mUserName.getText().toString().trim())) {
                    mDatabase.child("name").setValue(changeLetterString(mUserName.getText().toString().trim()));
                    onBackPressed();
                }else{
                    Toast.makeText(UserNameActivity.this, "UserName field is empty",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private String changeLetterString (String name){


        char c=Character.toUpperCase(name.charAt(0));
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        for (int i=1 ; i<name.length() ; i++){
            if(String.valueOf(name.charAt(i)).equals(" ")){
                sb.append(Character.toUpperCase(name.charAt(i)));
                i++;
                sb.append(Character.toUpperCase(name.charAt(i)));
            }else {
                sb.append(Character.toLowerCase(name.charAt(i)));
            }
        }

        return sb.toString();
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
        Intent backIntent = new Intent(UserNameActivity.this, SettingsActivity.class);
        startActivity(backIntent);
        finish();
    }
}
