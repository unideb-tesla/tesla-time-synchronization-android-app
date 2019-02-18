package com.unideb.tesla.timesync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class TimeSynchronizationActivity extends AppCompatActivity {

    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_synchronization);

        Intent intent = getIntent();
        ipAddress = intent.getStringExtra(MainActivity.EXTRA_IP_ADDRESS);

        // Toast.makeText(this, ipAddress, Toast.LENGTH_LONG).show();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            Log.d("PERMISSION", "NOT GRANTED YET");

            /*
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            */

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {

            Log.d("PERMISSION", "ALREADY GRANTED!!!");
            // new TimeSynchronizationTask(this).execute(ipAddress);

            // TODO: start an intent to pick file at the main activity
            Intent ii = new Intent(Intent.ACTION_GET_CONTENT);
            ii.setType("file/*");
            startActivityForResult(ii, 2);

        }

        // new TimeSynchronizationTask(this).execute(ipAddress);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    new TimeSynchronizationTask(this).execute(ipAddress);
                }else{
                    Log.d("PERMISSION", "DENIED!");
                }
                break;
        }

    }
}
