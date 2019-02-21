package com.unideb.tesla.timesync;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5]):" +
                    "\\d{1,5}$";

    private static Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);

    private ValidationUtils(){
    }

    public static boolean validateIpAddress(String ipAddress){

        Matcher matcher = pattern.matcher(ipAddress);

        return matcher.matches();

    }

}
