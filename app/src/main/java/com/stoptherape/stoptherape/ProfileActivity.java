package com.stoptherape.stoptherape;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Vibrator;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import static com.google.common.primitives.Chars.concat;

public class ProfileActivity extends AppCompatActivity {

    private String UID;
    private TextView userNameText;
    private TextView userEmailText;
    private TextView userGenderText;

    //Firebase Database Node Heads are named as FirebaseAuth:UserIDs
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    //And Then DatabaseReference references the FirebaseDatabase with the UID to retrieve profile information
    DatabaseReference databaseReference = firebaseDatabase.getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userNameText = (TextView) findViewById(R.id.userName);
        userEmailText = (TextView) findViewById(R.id.userEmail);
        userGenderText = (TextView) findViewById(R.id.userGender);

        //FirebaseUser Retrieves UID from FirebaseAuth
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            UID = firebaseUser.getUid();
        }

        //DataSnapshot on node.user -> node.uid -> ...
        databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull @NonNull DataSnapshot dataSnapshot) {
                try{
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    String nameLabel = "Name:  ";
                    userNameText.setText(nameLabel.concat(userProfile.getName()));
                    String emailLabel = "Email:  ";
                    userEmailText.setText(emailLabel.concat(userProfile.getEmail()));
                    String genderLabel = "Gender:  ";
                    userGenderText.setText(genderLabel.concat(userProfile.getGender()));
                    //Toast.makeText(ProfileActivity.this, "Profile Data Loaded Successfully",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Unable to Load Profile Data. "/*e.toString()*/, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Unable To Reach Database. ",Toast.LENGTH_SHORT).show();
            }
        });

        Button editProfileData = findViewById(R.id.editProfileDataButton);
        editProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(20);
                Toast.makeText(ProfileActivity.this, "Not Implemented Yet ! ", Toast.LENGTH_SHORT).show();
            }
        });

        Button stopBackgroundService = findViewById(R.id.stopBackgroundServiceButton);
        stopBackgroundService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                databaseReference.child(UID).child("shareLoc").setValue(false);
                databaseReference.child(UID).child("shareLoc").setValue(false);
                databaseReference.child(UID).child("shareLoc").setValue(false);
                Toast.makeText(ProfileActivity.this, "Location Share Stopped!", Toast.LENGTH_SHORT).show();
            }
        });

        Button BackButton = findViewById(R.id.backButton);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(20);
                finish();
            }
        });

        Button logoutButton = findViewById(R.id.logOutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50);
                Toast.makeText(ProfileActivity.this, "Press and Hold Button To Logout", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(20);
                SignOut();
                finish();
                return false;
            }
        });

    }

    private void SignOut(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }

    private void killFirstActivity(){
        try{
            //stopService(new Intent(ProfileActivity.this, BackgroundLocationService.class));
            Intent intent = new Intent(ProfileActivity.this, NavDrawerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(ProfileActivity.this, "Background service not running.", Toast.LENGTH_SHORT).show();
        }
    }

}

