package com.stoptherape.stoptherape;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

public class FeedbackAboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_about);

        Button SendFeedback = findViewById(R.id.feedbackSend);
        SendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibe.vibrate(20);
                String feedbackForm = "https://docs.google.com/forms/d/e/1FAIpQLSc5DylEqdr5HkzcYKFqSxRTZKCfyeKVKZuTL26HLWyYOMUeOA/viewform?usp=sf_link";
                openWebPage(feedbackForm);
            }
        });

        Button Backbutton = findViewById(R.id.backButton);
        Backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibe.vibrate(20);
                finish();
            }
        });

    }

    public void openWebPage(String url){
        Uri openInBrowser = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW,openInBrowser);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }
}
