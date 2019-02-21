package com.unideb.tesla.timesync.utils;

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

public class FileUtils {

    private FileUtils(){
    }

    public static byte[] readFileAsBytesFromUri(ContentResolver contentResolver, Uri uri) throws IOException {

        InputStream inputStream = contentResolver.openInputStream(uri);

        byte[] fileAsBytes = new byte[inputStream.available()];

        inputStream.read(fileAsBytes);

        return fileAsBytes;

    }

}
