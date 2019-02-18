package com.unideb.tesla.timesync;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SdCardUtils {

    private SdCardUtils(){
    }

    public static boolean isSdCardAvailable(){

        String state = Environment.getExternalStorageState();

        if(state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            return true;
        }

        return false;

    }

    public static boolean isFileExistsOnSdCard(String fileName){

        File sdCardDir = Environment.getExternalStorageDirectory();

        File fileToCheck = new File(sdCardDir, fileName);

        return fileToCheck.exists() && fileToCheck.isFile();

    }

    public static byte[] readFileAsBytesFromSdCard(String fileName) throws IOException {

        File sdCardDir = Environment.getExternalStorageDirectory();

        File fileToRead = new File(sdCardDir, fileName);

        FileInputStream fileInputStream = new FileInputStream(fileToRead);

        byte[] fileAsBytes = new byte[fileInputStream.available()];
        fileInputStream.read(fileAsBytes);

        fileInputStream.close();

        return fileAsBytes;

    }

}
