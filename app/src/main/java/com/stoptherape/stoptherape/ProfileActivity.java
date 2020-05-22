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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

public class ProfileActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String gender;

    private TextView userName;
    private TextView userEmail;
    private TextView userGender;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference().child(firebaseAuth.getCurrentUser().getUid());
    //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userGender = (TextView) findViewById(R.id.userGender);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull @NonNull DataSnapshot dataSnapshot) {
                try{
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    name = userProfile.getName();
                    userName.setText(name);
                    email = userProfile.getEmail();
                    userEmail.setText(email);
                    gender = userProfile.getGender();
                    userGender.setText(gender);
                    Toast.makeText(ProfileActivity.this, "Profile Data Loaded Successfully",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ProfileActivity.this, firebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Profile Data Load Unsuccessful :"+e.toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ProfileActivity.this, firebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Unable To Load Profile Data",Toast.LENGTH_SHORT).show();
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

    }
}

