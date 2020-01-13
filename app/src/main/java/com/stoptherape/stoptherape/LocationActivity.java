package com.stoptherape.stoptherape;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationActivity extends AppCompatActivity implements LocationListener {

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

    TextView tv_mainActivity_latitude;
    TextView tv_mainActivity_longitude;
    TextView tv_mainActivity_time;
    TextView tv_mainActivity_phoneId;
    TextView tv_mainActivity_response;
    Button shareMyLocationButton;
    Button editCustomMessageButton;
    Button editTrustedContactsButton;

    String latitude = "";
    String longitude = "";
    String phoneId ;
    String[] phoneNumbers;

    LocationManager locationManager;
    Location loc;
    TelephonyManager tm;
    RequestQueue rq;

    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        tv_mainActivity_latitude = findViewById(R.id.tv_mainActivity_latitude);
        tv_mainActivity_longitude = findViewById(R.id.tv_mainActivity_longitude);
        tv_mainActivity_time = findViewById(R.id.tv_mainActivity_time);
        tv_mainActivity_phoneId = findViewById(R.id.tv_mainActivity_phoneId);
        shareMyLocationButton = findViewById(R.id.ShareLocationButton);
        editCustomMessageButton = findViewById(R.id.EditCustomHelpMessageButton);
        editTrustedContactsButton = findViewById(R.id.EditTrustedContactsButton);
        //tv_mainActivity_response = findViewById(R.id.tv_mainActivity_response);


        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        assert locationManager != null;
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.SEND_SMS);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //Toast.makeText(this, "Your Location", Toast.LENGTH_SHORT).show();

        String IMEINumber = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        phoneId = IMEINumber;
        tv_mainActivity_phoneId.setText(IMEINumber);

        if (!isGPS && !isNetwork) {
            Log.d(TAG, "GPS connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "GPS connection on");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }

            getLocation();
        }

        Button refreshLocationButton = findViewById(R.id.refreshLocationButton);
        refreshLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getLocation();
                    updateUI(loc);
                    Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibe.vibrate(100);
                    Toast.makeText(LocationActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                    Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibe.vibrate(400);
                    Toast.makeText(LocationActivity.this, "Error Updating Location!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        shareMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(400);

                Toast.makeText(LocationActivity.this, "PRESS AND HOLD TO SEND HELP SMS", Toast.LENGTH_SHORT).show();
            }
        });

        shareMyLocationButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(1000);

                //String defaultHelpMessageString = getResources().getString(R.string.defaultHelpMessageString);
                String messageString = "";
                String numberString = "";
                String timeRN = DateFormat.getTimeInstance().format(loc.getTime());
                String messageFile = "customMessageFile";
                String numberFile = "trustedNumberFile";

                try{
                    FileInputStream fin = openFileInput(messageFile);
                    int c;
                    String tempMessage = "";
                    while((c=fin.read())!=-1){
                        tempMessage += Character.toString((char)c);
                    }
                    messageString = tempMessage;

                }catch (Exception e){
                    Toast.makeText(LocationActivity.this, "Error Sending Message (Text File Error)", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                try{
                    FileInputStream fin = openFileInput(numberFile);
                    int c;
                    String tempNumber = "";
                    while((c=fin.read())!=-1){
                        tempNumber += Character.toString((char)c);
                    }
                    numberString = tempNumber;

                    phoneNumbers = numberString.split(", *");

                }catch (Exception e){
                    Toast.makeText(LocationActivity.this, "Error Sending Message (Number File Error)", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                try{

                    for(String number: phoneNumbers){
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null,
                                messageString+"\n"+"My Location: : "+"https://www.google.com/maps/place/"+latitude+","+longitude+" ,"+
                                        "\n"+"Time: "+timeRN+"\n"+"HELP!",
                                null, null);
                    }

                    Toast.makeText(LocationActivity.this, "HELP SMS SENT TO YOUR TRUSTED CONTACTS", Toast.LENGTH_SHORT).show();

//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(numberString, null,
//                                messageString+"\n"+"My Location: : "+"https://www.google.com/maps/place/"+latitude+","+longitude+" ,"+
//                                        "\n"+"Time: "+timeRN+"\n"+"HELP!",
//                                null, null);

                }catch(Exception e){
                    e.printStackTrace();

                    vibe.vibrate(100);
                    Toast.makeText(LocationActivity.this, "SET TRUSTED PHONE NUMBER FIRST!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        editCustomMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                startActivity(new Intent(LocationActivity.this, CustomMessageActivity.class));
            }
        });

        editTrustedContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                startActivity(new Intent(LocationActivity.this, TrustedContactsActivity.class));
            }
        });

    }



    @Override
    public void onLocationChanged(Location location) {
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }


    private String getEnabledLocationProvider() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        // Returns the name of the provider that best meets the given criteria.
        // ==> "gps", "network",...
        String bestProvider = null;
        if (locationManager != null) {
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            try {
                for (String provider : providers) {
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = l;
                        bestProvider = provider;
                    }
                }
                if (bestProvider == null)
                    return null;
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        boolean enabled = false;
        if (locationManager != null) {
            enabled = locationManager.isProviderEnabled(bestProvider);
        }

        if (!enabled) {
            return null;
        }

        return bestProvider;
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                if (isGPS) {
                    Log.d(TAG, "GPS on");
                    String locationProvider = this.getEnabledLocationProvider();
                    try {
                        // This code need permissions, asked before
                        locationManager.requestLocationUpdates(
                                locationProvider,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        loc = locationManager
                                .getLastKnownLocation(locationProvider);
                        if (loc != null)
                            updateUI(loc);

                    }
                    // With Android API >= 23, need to catch SecurityException.
                    catch (SecurityException e) {
                        e.printStackTrace();
                        return;
                    }

                /*
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);*/

               /* if (locationManager != null) {
                    Log.d(TAG, "locationManager != null");
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc != null) {
                        Log.d(TAG, "loc != null");
                        updateUI(loc);
                    }
                }*/
                }

                if (isNetwork) {
                    Log.d(TAG, "Network on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
//    private void getLocation() {
//        try {
//            if (canGetLocation) {
//                if (isGPS) {
//                    Log.d(TAG, "GPS on");
//                    locationManager.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//
//                    if (locationManager != null) {
//                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                        if (loc != null)
//                            updateUI(loc);
//                    }
//                } else if (isNetwork) {
//                    Log.d(TAG, "Network on");
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//
//                    if (locationManager != null) {
//                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (loc != null)
//                            updateUI(loc);
//                    }
//                } else {
//                    loc.setLatitude(0);
//                    loc.setLongitude(0);
//                    updateUI(loc);
//                }
//            }
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//    }


    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.gps_enabled);
        alertDialog.setMessage(R.string.gps_enabled_question);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LocationActivity.this)
                .setMessage("These permissions are mandatory for the application. Please allow access.")
                .setPositiveButton(R.string.yes, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(Location loc) {
        Log.d(TAG, "update");
        tv_mainActivity_latitude.setText(Double.toString(loc.getLatitude()));
        tv_mainActivity_longitude.setText(Double.toString(loc.getLongitude()));
        tv_mainActivity_time.setText(DateFormat.getTimeInstance().format(loc.getTime()));
        longitude = Double.toString(loc.getLongitude());
        latitude = Double.toString(loc.getLatitude());

        //Toast.makeText(this, "UI Updated.", Toast.LENGTH_SHORT).show();

        rq = Volley.newRequestQueue(this);
        String url ="http://5.51.221.85:1337/track/";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("response", response);
                        tv_mainActivity_response.setText(response);
                        Log.d("longitude", longitude);
                        Log.d("latitude", latitude);
                        Log.d("response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("longitude", longitude);
                        Log.d("latitude", latitude);
                        Log.d("phoneId", phoneId);
                        //Log.d("Error.Response",response );
                    }
                }
        ) {
            @Override
            protected Map< String, String > getParams()
            {
                Map< String, String > params;
                params = new HashMap<>();

                params.put("identification", phoneId);
                params.put("longitude", longitude);
                params.put("latitude", latitude);

                return params;
            }
        };
        rq.add(postRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}

