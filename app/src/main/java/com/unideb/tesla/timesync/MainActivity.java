package com.unideb.tesla.timesync;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5]):" +
            "\\d{1,5}$";

    private TextInputEditText inputIpAddress;

    private Pattern pattern;
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputIpAddress = findViewById(R.id.inputIpAddress);

        pattern = Pattern.compile(IP_ADDRESS_PATTERN);

    }

    public void synchronize(View view){

        // TODO

        String ipAddress = inputIpAddress.getText().toString().trim();

        if(validateIpAddress(ipAddress)){
            Toast.makeText(this, "Good", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Bad", Toast.LENGTH_LONG).show();
        }

    }

    public boolean validateIpAddress(String ipAddress){

        matcher = pattern.matcher(ipAddress);

        return matcher.matches();

    }

}
