package com.stoptherape.stoptherape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.rpc.Help;

import java.util.ArrayList;
import java.util.List;


public class HelplineActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private ArrayList<String> data = new ArrayList<String>();

    String policePhoneNumberString = "999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);
        ListView lv = findViewById(R.id.thanaListView);

        generateListContent();

        lv.setAdapter(new MyListAdaper(HelplineActivity.this, R.layout.thana_list_item, data));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(HelplineActivity.this, "List item was clicked at " + position, Toast.LENGTH_SHORT).show();
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

    private void generateListContent() {
        for (int i = 1; i <= 20; i++) {
            data.add("Thana " + i + "\n" + "Information");
        }
    }

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;

        private MyListAdaper(HelplineActivity context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                //viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.thanaItemText);
                viewHolder.button = (Button) convertView.findViewById(R.id.thanaCallButton);

                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(20);
                    //Ask For Permission Required HERE
                    callPoliceWithPermission();
                }
            });
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }
    }

    private void callPoliceWithPermission(){
        if (ActivityCompat.checkSelfPermission(HelplineActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HelplineActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
        }
        else{
            callPolice();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPolice();
            } else {
                Toast.makeText(this, "Please Grant Permission to Make The Call!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callPolice(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + policePhoneNumberString));
        startActivity(callIntent);
    }

    public class ViewHolder {
        TextView title;
        Button button;
    }

}

