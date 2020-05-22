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
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CustomMessageActivity extends AppCompatActivity {

    TextInputEditText EditCustomMessageBox;
    Button SaveDefaultMessage;
    Button SaveCustomMessage;
    Button PreviewCustomMessage;
    String customMessage;
    String file = "customMessageFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_message);

        EditCustomMessageBox = findViewById(R.id.CustomMessageTextBox);
        SaveCustomMessage = findViewById(R.id.SaveCustomMessage);
        SaveDefaultMessage = findViewById(R.id.SaveDefaultMessage);
        PreviewCustomMessage = findViewById(R.id.PreviewCustomMessage);

        SaveCustomMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(20);

                customMessage = EditCustomMessageBox.getText().toString();
                try{
                    FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
                    fout.write(customMessage.getBytes());
                    fout.close();
                    File fileDir = new File(getFilesDir(),file);
                    Toast.makeText(CustomMessageActivity.this, "Custom Message Stored in path: "+fileDir, Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Toast.makeText(CustomMessageActivity.this, "ERROR Saving Custom Message", Toast.LENGTH_SHORT).show();
                    vibe.vibrate(50);
                    e.printStackTrace();
                }
                EditCustomMessageBox.setText("");

            }
        });

        SaveDefaultMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(20);

                String defaultHelpMessageString = getResources().getString(R.string.defaultHelpMessageString);
                try{
                    FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
                    fout.write(defaultHelpMessageString.getBytes());
                    fout.close();
                    File fileDir = new File(getFilesDir(),file);
                    Toast.makeText(CustomMessageActivity.this, "Default Message Stored in path: "+fileDir, Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Toast.makeText(CustomMessageActivity.this, "ERROR Saving Default Message", Toast.LENGTH_SHORT).show();
                    vibe.vibrate(50);
                    e.printStackTrace();
                }
                EditCustomMessageBox.setText("");
            }
        });

        PreviewCustomMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(20);
                try{
                    FileInputStream fin = openFileInput(file);
                    int c;
                    String tempMessage = "";
                    while((c=fin.read())!=-1){
                        tempMessage += Character.toString((char)c);
                    }
                    EditCustomMessageBox.setText(tempMessage);
                }catch (Exception e){
                    Toast.makeText(CustomMessageActivity.this, "Error Loading Saved Message", Toast.LENGTH_SHORT).show();
                    vibe.vibrate(50);
                    e.printStackTrace();
                }
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

