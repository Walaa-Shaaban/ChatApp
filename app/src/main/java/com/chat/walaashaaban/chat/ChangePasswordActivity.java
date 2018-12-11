package com.chat.walaashaaban.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mNewPassword, mCurrentPassword;
    private Button mSavePassword;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private ImageView imgCurrentPassword, imgNewPassword;
    private Boolean isHideCurrentPassword = true, isHideNewPassword = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mToolbar = (Toolbar) findViewById(R.id.change_password_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSavePassword = (Button) findViewById(R.id.btnSavePassword);

        mNewPassword = (EditText) findViewById(R.id.change_password);
        mCurrentPassword = (EditText) findViewById(R.id.current_password);
        imgCurrentPassword = (ImageView) findViewById(R.id.hideCurrentPassword);
        imgNewPassword = (ImageView) findViewById(R.id.hideNewPassword);

        //update status
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(mCurrentPassword.getText().toString()) || !TextUtils.isEmpty(mNewPassword.getText().toString())) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mCurrentUser.getEmail(), mCurrentPassword.getText().toString().trim());


                    mCurrentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mCurrentUser.updatePassword(mNewPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ChangePasswordActivity.this, "Password is updated!", Toast.LENGTH_SHORT).show();
                                                    onBackPressed();
                                                } else {
                                                    Toast.makeText(ChangePasswordActivity.this, "Error Password must be at least 6 characters", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Error Current Password", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }else {

                    Toast.makeText(ChangePasswordActivity.this, "Field is empty", Toast.LENGTH_SHORT).show();
                }



            }

        });


        imgNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHideNewPassword) {
                    isHideNewPassword = false;
                    imgNewPassword.setImageResource(R.drawable.hide);
                    mNewPassword.setInputType(InputType.TYPE_CLASS_TEXT);

                }else {
                    isHideNewPassword = true;
                    imgNewPassword.setImageResource(R.drawable.view);
                    mNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


        imgCurrentPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHideCurrentPassword) {
                    isHideCurrentPassword = false;
                    imgCurrentPassword.setImageResource(R.drawable.hide);

                    mCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT);

                }else {
                    isHideCurrentPassword = true;
                    imgCurrentPassword.setImageResource(R.drawable.view);
                    mCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                }
            }
        });



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
        Intent backIntent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
        startActivity(backIntent);
        finish();
    }
}
