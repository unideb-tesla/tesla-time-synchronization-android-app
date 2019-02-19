package com.unideb.tesla.timesync;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeSynchronizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_synchronization);

        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra(MainActivity.EXTRA_IP_ADDRESS);
        String publicKeyUriAsString = intent.getStringExtra(MainActivity.EXTRA_PUBLIC_KEY_URI_AS_STRING);

        // temporary RecyclerView test
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        TaskRecyclerViewAdapter adapter = new TaskRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new TimeSynchronizationTask(this, getContentResolver(), adapter).execute(ipAddress, publicKeyUriAsString);

    }

}
