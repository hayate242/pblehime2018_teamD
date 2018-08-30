package com.example.owner.googlemapstest;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class HelpEntryform extends AppCompatActivity{

    LocationManager locationManager;
    TextView statusView;
    private final int REQUEST_PERMISSION = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_entryform);

        //  ボタンにイベントリスナ
        Button button1 = (Button) findViewById(R.id.save);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(true) {
//                    Toast.makeText(this, "hello 日本語テスト" , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HelpEntryform.this, MapsActivity.class);
                    startActivity(intent);
                }

            }
        });

        Button button2 = (Button) findViewById(R.id.cansel);

        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpEntryform.this, MapsActivity.class);
                startActivity (intent);
            }
        });

    }

}

