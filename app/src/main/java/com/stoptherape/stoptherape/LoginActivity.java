package com.stoptherape.stoptherape;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button signInButton, signUpButton, forgotPassButton;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, NavDrawerActivity.class));
            finish();
        }

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        signUpButton = (Button) findViewById(R.id.signUpButtonReg);
        signInButton = (Button) findViewById(R.id.signInButton);
        forgotPassButton = (Button) findViewById(R.id.forgotPassButton);

        signInButton.setVisibility(View.VISIBLE);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgotPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPassActivity.class));
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)){

                    Toast.makeText(getApplicationContext(),"Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (TextUtils.isEmpty(password)){

                    Toast.makeText(getApplicationContext(),"Enter Password", Toast.LENGTH_SHORT).show();
                    return;

                }

                signInButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                /* If sign in fails, display a message to the user. If sign in succeeds
                                 the auth state listener will be notified and logic to handle the
                                 signed in user can be handled in the listener.*/
                                progressBar.setVisibility(View.GONE);
                                signInButton.setVisibility(View.VISIBLE);

                                if (!task.isSuccessful()) {

                                    //there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Intent i = new Intent(LoginActivity.this, NavDrawerActivity.class);
                                    startActivity(i);
                                    finish();
                                }

                            }
                        });
            }
        });
    }
}
