package com.stoptherape.stoptherape;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText inputUserName, inputEmail, inputPassword;
    private String gender, userName, email, password;
    private Button signInbutton, signUpButton;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private String UID;

    Button emergencyButton;

    private Spinner genderSpinner;
    private static final String[]paths = {"Choose", "Female", "Male"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        signInbutton = (Button) findViewById(R.id.logInbutton);
        signUpButton = (Button) findViewById(R.id.signUpButtonReg);
        inputUserName = (EditText) findViewById(R.id.inputUserName);
        inputEmail = (EditText) findViewById(R.id.emailReg);
        inputPassword = (EditText) findViewById(R.id.passwordReg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emergencyButton = (Button) findViewById(R.id.emergencyButton);
        genderSpinner = (Spinner)findViewById(R.id.genderSpinner);

        signUpButton.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        signInbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = inputUserName.getText().toString().trim();
                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                }

                else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                }

                else if (password.length() < 6) {
                    inputPassword.setError(getString(R.string.minimum_password));
                }

                else if (gender.equals("choose")) {

                    Toast.makeText(RegisterActivity.this,"You must choose your gender", Toast.LENGTH_SHORT).show();

                } else {

                    signUpButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    //previous implementation of db entry
                    //DatabaseReference newUser = databaseReference.push();
                    //newUser.setValue(new UserProfile(userName,email,gender));

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(RegisterActivity.this, "New Account Created Successfully", Toast.LENGTH_SHORT).show();

                                    //starting new implementation of database entry
                                    //databaseReference = databaseReference.child("user");
                                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    UID = firebaseUser.getUid();
                                    UserProfile userProfile = new UserProfile(userName,email,gender);
                                    databaseReference.child(UID).setValue(userProfile);
                                    //databaseReference.child(firebaseUser.getUid()).setValue(userProfile);

                                    progressBar.setVisibility(View.GONE);
                                    signInbutton.setVisibility(View.VISIBLE);
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                        signInbutton.setVisibility(View.VISIBLE);
                                    } else {
                                        startActivity(new Intent(RegisterActivity.this, NavDrawerActivity.class));
                                        finish();
                                    }
                                }
                            });

                }
            }
        });

    }

    /////////////////////////////////////
    /// this code is for spinner item ///
    /////////////////////////////////////

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {



        switch (position) {
            case 0:

                gender = "choose";

                Toast.makeText(this,"You must choose your gender", Toast.LENGTH_SHORT).show();

                break;
            case 1:

                gender = "female";

                //emergencyButton.setVisibility(View.VISIBLE);

                break;

            case 2:

                gender = "male";

                //emergencyButton.setVisibility(View.INVISIBLE);

                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
