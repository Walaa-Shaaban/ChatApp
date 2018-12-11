package com.chat.walaashaaban.chat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikelau.countrypickerx.Country;
import com.mikelau.countrypickerx.CountryPickerCallbacks;
import com.mikelau.countrypickerx.CountryPickerDialog;

import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayUser;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateButton;
    private TextView mSelectCountry;

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mProgress;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private ImageView imgRegPassword;
    private Boolean isHideRegPassword = true;

    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDisplayUser = (EditText) findViewById(R.id.reg_displayName);
        mEmail = (EditText) findViewById(R.id.reg_Email);
        mPassword = (EditText) findViewById(R.id.reg_Password);
        mCreateButton = (Button) findViewById(R.id.reg_create_btn);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_createPage);
        imgRegPassword = (ImageView) findViewById(R.id.imgPassword);
        mSelectCountry = (TextView) findViewById(R.id.selectCountry);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup_gender);


        mProgress = new ProgressDialog(this);


        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean check_internet = isNetworkAvailable(getApplicationContext());
                if (check_internet) {


                    final String getDisplayUser = mDisplayUser.getText().toString().trim();
                    final String getEmail = mEmail.getText().toString().trim();
                    final String getPassword = mPassword.getText().toString().trim();
                    final String getCountry = mSelectCountry.getText().toString().trim();
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);

                    if (!TextUtils.isEmpty(getDisplayUser)) {
                        if (!TextUtils.isEmpty(getEmail)) {
                            if (!TextUtils.isEmpty(getPassword)) {
                                if (!TextUtils.isEmpty(getCountry)) {
                                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {

                                        mAuth.fetchProvidersForEmail(getEmail).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                                                if (!(task.getResult().getProviders().size() > 0)) {


                                                    if (getPassword.length() >= 6) {

                                                        if (!TextUtils.isEmpty(getDisplayUser) || !TextUtils.isEmpty(getEmail) || !TextUtils.isEmpty(getPassword)) {
                                                            mProgress.setTitle("Registering User");
                                                            mProgress.setMessage("Please Wait");
                                                            mProgress.setCanceledOnTouchOutside(false);
                                                            mProgress.show();
                                                            registerUser(changeLetterString(getDisplayUser), getEmail, getPassword);
                                                        }
                                                    } else {
                                                        mProgress.hide();
                                                        Toast.makeText(RegisterActivity.this, " Password must be at least 6 characters ", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    mProgress.hide();
                                                    Toast.makeText(RegisterActivity.this, "This email has been registered Try another email",
                                                            Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        });

                                    } else {
                                        mProgress.hide();
                                        Toast.makeText(RegisterActivity.this, " Email format is incorrect ", Toast.LENGTH_LONG).show();
                                    }

                                } else {

                                    mProgress.hide();
                                    Toast.makeText(RegisterActivity.this, " Select Country field is empty ", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                mProgress.hide();
                                Toast.makeText(RegisterActivity.this, " Password field is empty ", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            mProgress.hide();
                            Toast.makeText(RegisterActivity.this, " Email field is empty ", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        mProgress.hide();
                        Toast.makeText(RegisterActivity.this, " Username field is empty ", Toast.LENGTH_LONG).show();
                    }


                } else {
                    mProgress.hide();

                    Toast.makeText(RegisterActivity.this, " Network Connection Error ", Toast.LENGTH_LONG).show();
                }


            }
        });

        mSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(RegisterActivity.this, new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country countryNow, int flagResId) {
                                mSelectCountry.setText(countryNow.getCountryName(RegisterActivity.this));
                                flag = countryNow.getIsoCode().toLowerCase().toString();
                            }
                        }, false, 0);
                countryPicker.show();;

            }
        });

        imgRegPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHideRegPassword) {
                    isHideRegPassword = false;
                    imgRegPassword.setImageResource(R.drawable.hide);
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT);

                } else {
                    isHideRegPassword = true;
                    imgRegPassword.setImageResource(R.drawable.view);
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
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
        Intent backIntent = new Intent(RegisterActivity.this, StartActivity.class);
        startActivity(backIntent);
        finish();
    }

    private void registerUser(final String getDisplayUser, String getEmail, String getPassword) {

        mAuth.createUserWithEmailAndPassword(getEmail, getPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid = currentUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance();
                            mDatabaseReference = mDatabase.getReference().child("Users").child(uid);
                            HashMap<String, String> rowMap = new HashMap<>();
                            rowMap.put("name", getDisplayUser);
                            rowMap.put("country", mSelectCountry.getText().toString());
                            rowMap.put("flag", flag);
                            rowMap.put("image", "defult");
                            rowMap.put("gender", radioButton.getText().toString());
                            rowMap.put("status", "Hi there, I'm using chat App.");
                            rowMap.put("thumb_image", "defult");
                            mDatabaseReference.setValue(rowMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        mProgress.dismiss();
                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.

                            mProgress.hide();
                            Toast.makeText(RegisterActivity.this, "Cant Sign in please check the form and try again",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }


}
