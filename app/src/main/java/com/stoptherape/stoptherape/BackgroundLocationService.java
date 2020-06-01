package com.stoptherape.stoptherape;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BackgroundLocationService extends Service {

    private static final String TAG = BackgroundLocationService.class.getSimpleName();

    private boolean isBackground = true;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private String currentTime;
    private double currentLat, currentLng;

    @Override
    public void onCreate(){
        super.onCreate();
        //Toast.makeText(this, "Location Service Enabled. ", Toast.LENGTH_SHORT).show();
        if(isBackground) {
            updateLocationInDatabase();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        return START_STICKY;
    }

    private void connectToFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UID = firebaseUser.getUid();
        databaseReference.child(UID).child("lat").setValue(currentLat);
        databaseReference.child(UID).child("lng").setValue(currentLng);
        databaseReference.child(UID).child("lastUpdated").setValue(currentTime);
        //Toast.makeText(this, "Location Updated in Firebase.", Toast.LENGTH_SHORT).show();
    }

    private void updateLocationInDatabase(){

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            client.requestLocationUpdates(locationRequest,new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult){
                    Location location = locationResult.getLastLocation();
                    if(location != null){
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        currentTime = getCurrentDateTime();
                        if (checkIfLocationShared()){
                            shareLocationToFirebase();
                        }
                        //if(isBackground)
                            connectToFirebase();
                    }
                }
            }, null);
        }
    }

    private boolean checkIfLocationShared(){
        boolean sharedLoc = false;
        return sharedLoc;
    }

    private void shareLocationToFirebase(){

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
        isBackground = false;
        Toast.makeText(this, "Background Services Stopped. ", Toast.LENGTH_SHORT).show();
        stopSelf();
    }

}
