package com.unideb.tesla.timesync;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_IP_ADDRESS = "com.unideb.tesla.timesync.IP_ADDRESS";

    public static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5]):" +
            "\\d{1,5}$";

    private TextInputEditText inputIpAddress;
    private TextView infoLastSynchronization;
    private TextView infoDelay;

    private Pattern pattern;
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputIpAddress = findViewById(R.id.inputIpAddress);
        infoLastSynchronization = findViewById(R.id.infoLastSynchronization);
        infoDelay = findViewById(R.id.infoDelay);

        pattern = Pattern.compile(IP_ADDRESS_PATTERN);

    }

    @Override
    protected void onResume() {

        super.onResume();

        // refresh ui
        SharedPreferences sharedPreferences = getSharedPreferences("timesync", Context.MODE_PRIVATE);

        String resultAsJson = sharedPreferences.getString("TIME_SYNCHRONIZATION_RESULT", "");

        if(!resultAsJson.isEmpty()){

            Gson gson = new Gson();
            TimeSynchronizationResult timeSynchronizationResult = gson.fromJson(resultAsJson, TimeSynchronizationResult.class);

            if(timeSynchronizationResult.isSuccessful()){

                infoLastSynchronization.setText(timeSynchronizationResult.getDate().toString());
                infoDelay.setText(Long.toString(timeSynchronizationResult.getDelay()));

            }

        }

    }

    public void synchronize(View view){

        // get server address input
        String ipAddress = inputIpAddress.getText().toString().trim();

        // start new activity if the address is valid
        if(validateIpAddress(ipAddress)){

            Intent intent = new Intent(this, TimeSynchronizationActivity.class);
            intent.putExtra(EXTRA_IP_ADDRESS, ipAddress);
            startActivity(intent);

        }else{

            Toast.makeText(this, "Invalid address!", Toast.LENGTH_LONG).show();

        }

    }

    public boolean validateIpAddress(String ipAddress){

        matcher = pattern.matcher(ipAddress);

        return matcher.matches();

    }

}
