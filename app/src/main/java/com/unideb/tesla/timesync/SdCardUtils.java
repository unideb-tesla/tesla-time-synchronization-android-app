package com.unideb.tesla.timesync;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    @Deprecated
    public static byte[] readFileAsBytesFromSdCard(String fileName) throws IOException {

        File sdCardDir = Environment.getExternalStorageDirectory();

        File fileToRead = new File(sdCardDir, fileName);

        FileInputStream fileInputStream = new FileInputStream(fileToRead);

        byte[] fileAsBytes = new byte[fileInputStream.available()];
        fileInputStream.read(fileAsBytes);

        fileInputStream.close();

        return fileAsBytes;

    }

    public static byte[] readFileAsBytesFromUri(ContentResolver contentResolver, Uri uri) throws IOException {

        InputStream inputStream = contentResolver.openInputStream(uri);

        byte[] fileAsBytes = new byte[inputStream.available()];

        inputStream.read(fileAsBytes);

        return fileAsBytes;

    }

}
