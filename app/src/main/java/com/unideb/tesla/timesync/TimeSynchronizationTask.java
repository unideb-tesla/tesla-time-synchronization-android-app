package com.unideb.tesla.timesync;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

public class TimeSynchronizationTask extends AsyncTask<String, TimeSynchronizationProgressUnit, TimeSynchronizationResult> {

    public static final String SUBTASK_READ_KEY = "Read bytes of the public key file";
    public static final String SUBTASK_CONNECT_TO_SERVER = "Connect to the server";
    public static final String SUBTASK_OPEN_IO_STREAMS = "Open I/O streams";
    public static final String SUBTASK_GENERATE_NONCE = "Generate nonce";
    public static final String SUBTASK_SAVE_RECEIVER_TIMESTAMP = "Save our own timestamp";
    public static final String SUBTASK_SEND_NONCE_TO_SERVER = "Send nonce to the server";
    public static final String SUBTASK_RECEIVE_TIMESTAMP_AND_SIGNATURE = "Receive server timestamp and nonce signature";
    public static final String SUBTASK_VERIFY_SIGNATURE = "Verify nonce signature";
    public static final String SUBTASK_CALCULATE_DELAY = "Calculate delay between sender and receiver";

    public static final int NONCE_SIZE = 128;

    private Activity activity;
    private ContentResolver contentResolver;
    private TaskRecyclerViewAdapter taskRecyclerViewAdapter;

    private Socket socket;
    private DataInputStream serverInputStream;
    private DataOutputStream serverOutputStream;
    private byte[] publicKey;
    private byte[] nonce;
    private long receiverTimestamp;
    private byte[] nonceSignature;
    private long senderTimeStamp;
    private boolean verifies;

    private String publicKeyFileUriAsString;
    private String publicKeyFileName;
    private String serverAddress;

    public TimeSynchronizationTask(Activity activity, ContentResolver contentResolver, TaskRecyclerViewAdapter taskRecyclerViewAdapter) {
        this.activity = activity;
        this.contentResolver = contentResolver;
        this.taskRecyclerViewAdapter = taskRecyclerViewAdapter;
    }

    @Override
    protected TimeSynchronizationResult doInBackground(String... params) {

        // reusable progress
        TimeSynchronizationProgressUnit progress;

        // save some data
        publicKeyFileUriAsString = params[1];
        publicKeyFileName = Uri.parse(publicKeyFileUriAsString).getPath().split(":")[1];
        serverAddress = params[0];

        // read key file
        progress = readKey(params[1]);
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());

        // connect to the server
        progress = connectToServer(params[0]);
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());

        // open io streams
        progress = openIoStreams();
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());

        // generate nonce
        progress = generateNonce();
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());

        // save receiver timestamp
        receiverTimestamp = System.currentTimeMillis();
        publishProgress(new TimeSynchronizationProgressUnit(SUBTASK_SAVE_RECEIVER_TIMESTAMP, true, null));

        // send nonce to the server
        progress = sendNonceToServer();
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());

        // receive timestamp and signature
        progress = receiveTimestampAndSignature();
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());

        // verify signature
        progress = verifySignature();
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());

        // calculate delay
        long delay = -receiverTimestamp + senderTimeStamp;
        publishProgress(new TimeSynchronizationProgressUnit(SUBTASK_CALCULATE_DELAY, true, null));

        // close streams and connection
        closeConnection();

        // return result
        return new TimeSynchronizationResult(true, delay, new Date());

    }

    @Override
    protected void onProgressUpdate(TimeSynchronizationProgressUnit... values) {

        String taskName = values[0].getName();
        boolean taskResult = values[0].isSuccessful();

        taskRecyclerViewAdapter.addNewTaskResult(taskName, taskResult);

    }

    @Override
    protected void onPostExecute(TimeSynchronizationResult timeSynchronizationResult) {

        // get shared preferences editor
        SharedPreferences sharedPreferences = activity.getSharedPreferences(MainActivity.SHARED_PREFERENCES_FILE_NAME_TIMESYNC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // create Gson object for serialization
        Gson gson = new Gson();

        // save result
        editor.putString(MainActivity.SHARED_PREFERENCES_KEY_RESULT, gson.toJson(timeSynchronizationResult));

        // save configuration
        TimeSynchronizationConfiguration timeSynchronizationConfiguration = new TimeSynchronizationConfiguration(publicKeyFileUriAsString, publicKeyFileName, serverAddress);
        editor.putString(MainActivity.SHARED_PREFERENCES_KEY_CONFIGURATION, gson.toJson(timeSynchronizationConfiguration));

        // commit changes
        editor.apply();

    }

    public String extractIpFromParam(String param){
        return param.split(":")[0];
    }

    public int extractPortFromParam(String param){
        return Integer.parseInt(param.split(":")[1]);
    }

    public TimeSynchronizationProgressUnit readKey(String uriAsString){

        try {
            publicKey = FileUtils.readFileAsBytesFromUri(contentResolver, Uri.parse(uriAsString));
        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_READ_KEY, false, e);
        }

        return new TimeSynchronizationProgressUnit(SUBTASK_READ_KEY, true, null);

    }

    public TimeSynchronizationProgressUnit connectToServer(String address){

        String ip = extractIpFromParam(address);
        int port = extractPortFromParam(address);

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 2000);
        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_CONNECT_TO_SERVER, false, e);
        }

        return new TimeSynchronizationProgressUnit(SUBTASK_CONNECT_TO_SERVER, true, null);

    }

    public TimeSynchronizationProgressUnit openIoStreams(){

        try {
            serverInputStream = new DataInputStream(socket.getInputStream());
            serverOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_OPEN_IO_STREAMS, false, e);
        }

        return new TimeSynchronizationProgressUnit(SUBTASK_OPEN_IO_STREAMS, true, null);

    }

    public TimeSynchronizationProgressUnit generateNonce(){

        SecureRandom secureRandom = new SecureRandom();
        nonce = new byte[NONCE_SIZE];
        secureRandom.nextBytes(nonce);

        return new TimeSynchronizationProgressUnit(SUBTASK_GENERATE_NONCE, true, null);

    }

    public TimeSynchronizationProgressUnit sendNonceToServer(){

        try {
            serverOutputStream.writeInt(NONCE_SIZE);
            serverOutputStream.write(nonce);
        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_SEND_NONCE_TO_SERVER, false, e);
        }

        return new TimeSynchronizationProgressUnit(SUBTASK_SEND_NONCE_TO_SERVER, true, null);

    }

    public TimeSynchronizationProgressUnit receiveTimestampAndSignature(){

        try {

            senderTimeStamp = serverInputStream.readLong();

            int nonceSignatureSize = serverInputStream.readInt();
            nonceSignature = new byte[nonceSignatureSize];
            serverInputStream.read(nonceSignature, 0, nonceSignatureSize);

        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_RECEIVE_TIMESTAMP_AND_SIGNATURE, false, e);
        }

        return new TimeSynchronizationProgressUnit(SUBTASK_RECEIVE_TIMESTAMP_AND_SIGNATURE, true, null);

    }

    public TimeSynchronizationProgressUnit verifySignature(){

        try {
            verifies = DigitalSignature.verify(nonce, nonceSignature, publicKey);
        } catch (NoSuchAlgorithmException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_VERIFY_SIGNATURE, false, e);
        } catch (InvalidKeyException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_VERIFY_SIGNATURE, false, e);
        } catch (InvalidKeySpecException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_VERIFY_SIGNATURE, false, e);
        } catch (SignatureException e) {
            return new TimeSynchronizationProgressUnit(SUBTASK_VERIFY_SIGNATURE, false, e);
        }

        if(!verifies){
            return new TimeSynchronizationProgressUnit(SUBTASK_VERIFY_SIGNATURE, false, null);
        }

        return new TimeSynchronizationProgressUnit(SUBTASK_VERIFY_SIGNATURE, true, null);

    }

    public void closeConnection(){

        try{
            serverInputStream.close();
            serverOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
