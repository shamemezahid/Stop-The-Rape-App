package com.stoptherape.stoptherape;

import android.content.BroadcastReceiver;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NavDrawerActivity extends AppCompatActivity{

    // used to implement(s) NavigationView.OnNavigationItemSelectedListener

    FirebaseAuth auth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();;
    String UID = firebaseUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nav_drawer);
        //setContentView(R.layout.app_bar_nav_drawer);

        //helps app to close from the profile page
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(NavDrawerActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                startActivity(new Intent(NavDrawerActivity.this, ProfileActivity.class));
            }
        });

//        Button logoutButton = findViewById(R.id.logOutButton);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                vibe.vibrate(50);
//                Toast.makeText(NavDrawerActivity.this, "Press and Hold Button To Logout", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        logoutButton.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                vibe.vibrate(20);
//                signOut();
//                return false;
//            }
//        });

        Button FeedbackButton = findViewById(R.id.feedbackButton);
        FeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                startActivity(new Intent(NavDrawerActivity.this, FeedbackAboutActivity.class));
            }
        });


        Button policeHelplineButton = findViewById(R.id.policeHelplineButton);
        policeHelplineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);

                startActivity(new Intent(NavDrawerActivity.this, HelplineActivity.class));
            }
        });

        Button offlineHelpButton = findViewById(R.id.offlineHelpButton);
        offlineHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.vibrate(20);
                Intent i = new Intent(NavDrawerActivity.this, LocationActivity.class);
                startActivity(i);
            }
        });

        Button anonymousHelpButton = findViewById(R.id.anonymousHelpButton);
        anonymousHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);

                Toast.makeText(NavDrawerActivity.this, "Loading Maps...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NavDrawerActivity.this, MapsActivity.class));
            }
        });

        Button HelpButton = findViewById(R.id.getHelp);
        HelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(50);
                Toast.makeText(NavDrawerActivity.this, "Press and Hold !", Toast.LENGTH_SHORT).show();
            }
        });

        HelpButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibe.vibrate(1000);
                databaseReference.child(UID).child("shareLoc").setValue(true);
                databaseReference.child(UID).child("shareLoc").setValue(true);
                Toast.makeText(NavDrawerActivity.this, "Location Shared!", Toast.LENGTH_SHORT).show();
                startService(new Intent(NavDrawerActivity.this, BackgroundLocationService.class));
                startService(new Intent(NavDrawerActivity.this, BackgroundLocationService.class));
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(NavDrawerActivity.this, LoginActivity.class));
                finish();
            }
        }
    };

//    private void signOut() {
//        //signing out from firebase an stopping the background location service
//        auth.signOut();
//        stopService(new Intent(NavDrawerActivity.this, BackgroundLocationService.class));
//    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }




}


