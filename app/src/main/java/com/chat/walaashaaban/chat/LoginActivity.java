package com.chat.walaashaaban.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginButton;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;

    private ImageView imgRegPassword;
    private Boolean isHideRegPassword = true;
    private TextView mResetPasswordUsingEmail;


    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar = (Toolbar)findViewById(R.id.toolbar_LoginPage);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mEmail = (EditText) findViewById(R.id.reg_Email);
        mPassword = (EditText) findViewById(R.id.reg_Password);
        mLoginButton = (Button) findViewById(R.id.login_btn);
        imgRegPassword = (ImageView) findViewById(R.id.imgPassword);
        mResetPasswordUsingEmail = (TextView) findViewById(R.id.resetPasswordUsingEmail);
        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        mProgress = new ProgressDialog(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean check_internet = isNetworkAvailable(getApplicationContext());

                if (check_internet) {


                    String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    if(!TextUtils.isEmpty(email)) {

                        if (!TextUtils.isEmpty(password)) {

                            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                                    mProgress.setTitle("Login User");
                                    mProgress.setMessage("Please Wait");
                                    mProgress.setCanceledOnTouchOutside(false);
                                    mProgress.show();
                                    LoginUser(email, password);
                                }
                            }
                            else {
                                Toast.makeText(LoginActivity.this, " Email format is incorrect ", Toast.LENGTH_LONG).show();
                            }
                        }




                        else {
                            Toast.makeText(LoginActivity.this, " Password field is empty ", Toast.LENGTH_LONG).show();
                        }
                    }else {

                        Toast.makeText(LoginActivity.this, " Email field is empty ", Toast.LENGTH_LONG).show();

                    }


                } else {

                    Toast.makeText(LoginActivity.this, " Network Connection Error ", Toast.LENGTH_LONG).show();
                }
            }
        });



        imgRegPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHideRegPassword) {
                    isHideRegPassword = false;
                    imgRegPassword.setImageResource(R.drawable.hide);
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT);

                }else {
                    isHideRegPassword = true;
                    imgRegPassword.setImageResource(R.drawable.view);
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


        mResetPasswordUsingEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.sendPasswordResetEmail(mEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        mResetPasswordUsingEmail.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, " Please check your email ", Toast.LENGTH_LONG).show();


                    }
                });
            }
        });
    }



    private void LoginUser(final String email, final String password) {

        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                if (task.getResult().getProviders().size() > 0) {


                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        mProgress.dismiss();

                                        String current_user_id = mAuth.getCurrentUser().getUid();
                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                        mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();


                                            }
                                        });
                                    } else {
                                        mProgress.hide();
                                        Toast.makeText(LoginActivity.this, "Error in password... Please try again",
                                                Toast.LENGTH_SHORT).show();
                                        mResetPasswordUsingEmail.setVisibility(View.VISIBLE);


                                    }
                                }
                            });

                } else {
                    mProgress.hide();
                    Toast.makeText(LoginActivity.this, "This email is not registered",
                            Toast.LENGTH_SHORT).show();
                }

            }


        });
    }





    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
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
        Intent backIntent = new Intent(LoginActivity.this, StartActivity.class);
        startActivity(backIntent);
        finish();
    }

}
