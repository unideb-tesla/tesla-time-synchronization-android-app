package com.unideb.tesla.timesync.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.unideb.tesla.timesync.R;
import com.unideb.tesla.timesync.adapters.TaskRecyclerViewAdapter;
import com.unideb.tesla.timesync.tasks.TimeSynchronizationTask;

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
        TaskRecyclerViewAdapter adapter = new TaskRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new TimeSynchronizationTask(this, getContentResolver(), adapter).execute(ipAddress, publicKeyUriAsString);

    }

}
