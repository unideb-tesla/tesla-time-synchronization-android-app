package com.unideb.tesla.timesync;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
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
    public static final String EXTRA_PUBLIC_KEY_URI_AS_STRING = "com.unideb.tesla.timesync.PUBLIC_KEY_URI_AS_STRING";

    public static final int REQUEST_CODE_SELECT_PUBLIC_KEY_FILE = 11;

    public static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5]):" +
            "\\d{1,5}$";

    private TextInputEditText inputIpAddress;
    private TextView infoLastSynchronization;
    private TextView infoDelay;
    private TextView inputPublicKeyFileName;
    private TextView infoSuccessfulSynchronization;

    private Pattern pattern;
    private Matcher matcher;
    private Uri publicKeyUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputIpAddress = findViewById(R.id.inputIpAddress);
        infoLastSynchronization = findViewById(R.id.infoLastSynchronization);
        infoDelay = findViewById(R.id.infoDelay);
        inputPublicKeyFileName = findViewById(R.id.inputPublicKeyFileName);
        infoSuccessfulSynchronization = findViewById(R.id.infoSuccessfulSynchronization);

        pattern = Pattern.compile(IP_ADDRESS_PATTERN);

    }

    @Override
    protected void onResume() {

        super.onResume();

        refreshUi();

    }

    public void synchronize(View view){

        // get server address input
        String ipAddress = inputIpAddress.getText().toString().trim();

        // validate ip address
        if(!validateIpAddress(ipAddress)){
            Toast.makeText(this, "Invalid address!", Toast.LENGTH_LONG).show();
            return;
        }

        // check if public key file is selected
        if(publicKeyUri == null){
            Toast.makeText(this, "No public key file has been selected!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, TimeSynchronizationActivity.class);
        intent.putExtra(EXTRA_IP_ADDRESS, ipAddress);
        intent.putExtra(EXTRA_PUBLIC_KEY_URI_AS_STRING, publicKeyUri.toString());
        startActivity(intent);

    }

    public boolean validateIpAddress(String ipAddress){

        matcher = pattern.matcher(ipAddress);

        return matcher.matches();

    }

    public void selectPublicKeyFile(View view){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_PUBLIC_KEY_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == REQUEST_CODE_SELECT_PUBLIC_KEY_FILE && resultCode == RESULT_OK){

            // overwrite previous configuration
            TimeSynchronizationConfiguration timeSynchronizationConfiguration = getConfiguration();

            String uri = data.getData().toString();
            String fileName = Uri.parse(uri).getPath().split(":")[1];

            timeSynchronizationConfiguration.setPublicKeyFileUriAsString(uri);
            timeSynchronizationConfiguration.setPublicKeyFileName(fileName);

            SharedPreferences sharedPreferences = getSharedPreferences("timesync", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();

            editor.putString("TIME_SYNCHRONIZATION_CONFIGURATION", gson.toJson(timeSynchronizationConfiguration));

            editor.commit();

        }else{
            Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

    }

    private TimeSynchronizationResult getResult(){

        SharedPreferences sharedPreferences = getSharedPreferences("timesync", Context.MODE_PRIVATE);

        String resultAsJson = sharedPreferences.getString("TIME_SYNCHRONIZATION_RESULT", "");

        if(!resultAsJson.isEmpty()){

            Gson gson = new Gson();
            TimeSynchronizationResult timeSynchronizationResult = gson.fromJson(resultAsJson, TimeSynchronizationResult.class);

            return timeSynchronizationResult;

        }

        return null;

    }

    private TimeSynchronizationConfiguration getConfiguration(){

        // refresh ui
        SharedPreferences sharedPreferences = getSharedPreferences("timesync", Context.MODE_PRIVATE);

        String resultAsJson = sharedPreferences.getString("TIME_SYNCHRONIZATION_CONFIGURATION", "");

        if(!resultAsJson.isEmpty()){

            Log.d("WHAT", resultAsJson);

            Gson gson = new Gson();
            TimeSynchronizationConfiguration timeSynchronizationConfiguration = gson.fromJson(resultAsJson, TimeSynchronizationConfiguration.class);

            return timeSynchronizationConfiguration;

        }

        return null;

    }

    private void refreshUi(){

        // load objects
        TimeSynchronizationConfiguration timeSynchronizationConfiguration = getConfiguration();
        TimeSynchronizationResult timeSynchronizationResult = getResult();

        // refresh configuration parts
        if(timeSynchronizationConfiguration != null){

            publicKeyUri = Uri.parse(timeSynchronizationConfiguration.getPublicKeyFileUriAsString());

            inputPublicKeyFileName.setText(timeSynchronizationConfiguration.getPublicKeyFileName());
            inputIpAddress.setText(timeSynchronizationConfiguration.getServerAddress());

        }

        // refresh result parts
        if(timeSynchronizationResult != null){

            infoLastSynchronization.setText(timeSynchronizationResult.getDate().toString());
            infoDelay.setText(Long.toString(timeSynchronizationResult.getDelay()));
            infoSuccessfulSynchronization.setText(Boolean.toString(timeSynchronizationResult.isSuccessful()));

        }

    }

}
