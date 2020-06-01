package com.stoptherape.stoptherape;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class TrustedContactsActivity extends AppCompatActivity {

    TextInputEditText TrustedNumberTextBox;
    Button SaveTrustedNumber;
    Button PreviewTrustedNumber;
    Button DeleteTrustedNumber;
    String TrustedContactNumberString;
    String file = "trustedNumberFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        TrustedNumberTextBox = findViewById(R.id.TrustedNumberTextBox);
        SaveTrustedNumber = findViewById(R.id.SaveTrustedNumber);
        DeleteTrustedNumber = findViewById(R.id.DeleteTrustedNumber);
        PreviewTrustedNumber = findViewById(R.id.PreviewTrustedNumber);

        SaveTrustedNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrustedNumber();
            }
        });

        DeleteTrustedNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrustedNumber();
            }
        });


        PreviewTrustedNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewTrustedNumber();
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

    private void saveTrustedNumber(){
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(20);

        TrustedContactNumberString = TrustedNumberTextBox.getText().toString().trim();
        TrustedContactNumberString = TrustedContactNumberString.replaceAll("[\\s]","");
        TrustedContactNumberString = TrustedContactNumberString.replaceAll("[^,\\d]","");
        if(TrustedContactNumberString.isEmpty()){
            Toast.makeText(this, "No Numbers to Save!", Toast.LENGTH_SHORT).show();
            vibe.vibrate(50);
        }else{
            try{
                FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
                fout.write(TrustedContactNumberString.getBytes());
                fout.close();
                File fileDir = new File(getFilesDir(),file);
                //Toast.makeText(TrustedContactsActivity.this, "Trusted Number Stored in path: "+fileDir, Toast.LENGTH_SHORT).show();
                Toast.makeText(TrustedContactsActivity.this, "Trusted Number Stored ", Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                Toast.makeText(TrustedContactsActivity.this, "ERROR Saving Trusted Number", Toast.LENGTH_SHORT).show();
                vibe.vibrate(50);
                e.printStackTrace();
            }
        }
        TrustedNumberTextBox.setText("");
    }

    private void deleteTrustedNumber(){
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(20);

        String defaultStringEmptyNumber = "";
        try{
            FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
            fout.write(defaultStringEmptyNumber.getBytes());
            fout.close();
            File fileDir = new File(getFilesDir(),file);
            //Toast.makeText(TrustedContactsActivity.this, "Trusted Numbers Stored in path: "+fileDir, Toast.LENGTH_SHORT).show();
            Toast.makeText(TrustedContactsActivity.this, "Trusted Numbers Removed", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(TrustedContactsActivity.this, "ERROR Deleting Saved Number", Toast.LENGTH_SHORT).show();
            vibe.vibrate(50);
            e.printStackTrace();
        }
        TrustedNumberTextBox.setText("");
    }

    private void previewTrustedNumber(){
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(20);

        try{
            FileInputStream fin = openFileInput(file);
            int c;
            String tempMessage = "";
            while((c=fin.read())!=-1){
                tempMessage += Character.toString((char)c);
            }
            TrustedNumberTextBox.setText(tempMessage.trim());
            if(tempMessage==""){
                vibe.vibrate(50);
                Toast.makeText(TrustedContactsActivity.this, "No Numbers Saved. MAKE SURE TO SAVE A FEW TRUSTED NUMBERS RIGHT NOW", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(TrustedContactsActivity.this, "Error Loading Saved Numbers", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
