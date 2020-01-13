package com.stoptherape.stoptherape;

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

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
//        app bar nav drawer xml er moddhe bhore de
//        <!--androidx.appcompat.widget.Toolbar
//        android:id="@+id/toolbar"
//        android:layout_width="match_parent"
//        android:layout_height="?attr/actionBarSize"
//        android:background="?attr/colorPrimary"
//        app:popupTheme="@style/AppTheme.PopupOverlay" />


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();


        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                startActivity(new Intent(NavDrawerActivity.this, ProfileActivity.class));
            }
        });

        Button logoutButton = findViewById(R.id.logOutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(200);
                Toast.makeText(NavDrawerActivity.this, "Press and Hold Button To Logout", Toast.LENGTH_SHORT).show();
            }
        });
        logoutButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                signOut();
                return false;
            }
        });


        Button policeHelplineButton = findViewById(R.id.policeHelplineButton);
        policeHelplineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);

                startActivity(new Intent(NavDrawerActivity.this, HelplineActivity.class));

            }
        });

        Button offlineHelpButton = findViewById(R.id.offlineHelpButton);
        offlineHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);


                Intent i = new Intent(NavDrawerActivity.this, LocationActivity.class);
                startActivity(i);
                /*Snackbar.make(view, "Your current Location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        Button anonymousHelpButton = findViewById(R.id.anonymousHelpButton);
        anonymousHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);

                Toast.makeText(NavDrawerActivity.this, "Loading Maps...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NavDrawerActivity.this, MapsActivity.class));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out) {

            //startActivity(new Intent(NavDrawerActivity.this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.user_profile) {
            startActivity(new Intent(NavDrawerActivity.this, ProfileActivity.class));

        } else if (id == R.id.log_out) {
            signOut();
        } //else if (id == R.id.nav_slideshow) {

        //} else if (id == R.id.nav_manage) {

        //}
        else if (id == R.id.nav_share) {

            //dev phase will redirect to github repository

        } else if (id == R.id.nav_send) {

            //dev phase will open mail to send mail

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void signOut() {

        auth.signOut();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
