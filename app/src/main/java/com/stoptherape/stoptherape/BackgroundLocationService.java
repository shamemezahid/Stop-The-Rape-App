package com.stoptherape.stoptherape;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BackgroundLocationService extends Service {

    private static final String TAG = BackgroundLocationService.class.getSimpleName();

    private boolean isBackground = true;
    private boolean sharedLoc;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();;
    String UID = firebaseUser.getUid();

    private String currentTime;
    private double currentLat, currentLng;


    LocationRequest locationRequest = new LocationRequest();

    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(this, "Location Service Enabled. ", Toast.LENGTH_SHORT).show();
        if(checkIfLocationShared()){
            Toast.makeText(this, "oncreate ShareLocation : true", Toast.LENGTH_SHORT).show();
            updateLocationInDatabase();
        }
        else{
            Toast.makeText(this, "oncreate ShareLocation : false", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(checkIfLocationShared()){
            Toast.makeText(this, "onstart ShareLocation : true", Toast.LENGTH_SHORT).show();
            updateLocationInDatabase();
        }
        else{
            Toast.makeText(this, "onstart ShareLocation : false", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    private void connectToFirebase(){
        if(checkIfLocationShared()){
            String UID = firebaseUser.getUid();
            databaseReference.child(UID).child("lat").setValue(currentLat);
            databaseReference.child(UID).child("lng").setValue(currentLng);
            databaseReference.child(UID).child("lastUpdated").setValue(currentTime);
            Toast.makeText(this, "Location Updated in Firebase.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocationInDatabase(){
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            client.requestLocationUpdates(locationRequest, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult){
                    Location location = locationResult.getLastLocation();
                    if(location != null){
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        currentTime = getCurrentDateTime();
                            connectToFirebase();
                    }
                }
            }, null);
        }
    }

    private boolean checkIfLocationShared(){
        String UID = firebaseUser.getUid();
        databaseReference.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    if (userProfile.getShareLoc()) sharedLoc = true;
                    else sharedLoc = false;
                }catch(Exception e){
                    Toast.makeText(BackgroundLocationService.this, "Location Share Error!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BackgroundLocationService.this, "Network Error!", Toast.LENGTH_SHORT).show();
            }
        });

        updateCurrentStatusUI(sharedLoc?"ON":"OFF");

        return sharedLoc;
    }

    private void updateCurrentStatusUI(String OnOrOff){
        Intent intent = new Intent("LocationStatusUpdate");
        Bundle bundle = new Bundle();
        bundle.putString("CurrentStatusString", OnOrOff);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String getCurrentDateTime(){
        return new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public BackgroundLocationService() { }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this, "Background Services Stopped. ", Toast.LENGTH_SHORT).show();
        isBackground = false;
    }

}
