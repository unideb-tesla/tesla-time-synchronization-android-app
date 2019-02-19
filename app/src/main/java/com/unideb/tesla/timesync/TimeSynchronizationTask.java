package com.unideb.tesla.timesync;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

// TODO: the weak part of this task is that we can not interrupt it
// TODO: maybe we should figure out a different solution, or do we actually need it?
public class TimeSynchronizationTask extends AsyncTask<String, TimeSynchronizationProgressUnit, TimeSynchronizationResult> {

    public static final int NONCE_SIZE = 128;

    private Activity activity;
    private ContentResolver contentResolver;

    private Socket socket;
    private DataInputStream serverInputStream;
    private DataOutputStream serverOutputStream;
    private byte[] publicKey;
    private byte[] nonce;
    private long receiverTimestamp;
    private byte[] nonceSignature;
    private long senderTimeStamp;
    private boolean verifies;

    public TimeSynchronizationTask(Activity activity, ContentResolver contentResolver) {
        this.activity = activity;
        this.contentResolver = contentResolver;
    }

    @Override
    protected TimeSynchronizationResult doInBackground(String... params) {

        // reusable progress
        TimeSynchronizationProgressUnit progress;

        // check sd card
        progress = checkSdCard();
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());

        /*
        // check key file
        progress = checkKeyFile();
        publishProgress(progress);
        if(!progress.isSuccessful()) return new TimeSynchronizationResult(false, -1, new Date());
        Toast.makeText(activity, Boolean.toString(progress.isSuccessful()), Toast.LENGTH_LONG).show();
        */

        // read key file
        // TODO: maybe move it to the end of the task?
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
        publishProgress(new TimeSynchronizationProgressUnit("SAVE_RECEIVER_TIMESTAMP", true, null));

        // send nonce to the server
        progress = sendNonceToServer();
        publishProgress(progress);
        if(!progress.isSuccessful()) return null;

        // receive timestamp and signature
        progress = receiveTimestampAndSignature();
        publishProgress(progress);
        if(!progress.isSuccessful()) return null;

        // verify signature
        progress = verifySignature();
        publishProgress(progress);
        if(!progress.isSuccessful()) return null;

        // calculate delay
        long delay = -receiverTimestamp + senderTimeStamp;
        publishProgress(new TimeSynchronizationProgressUnit("CALCULATE_DELAY", true, null));

        // close streams and connection
        closeConnection();

        // return result
        return new TimeSynchronizationResult(true, delay, new Date());

    }

    @Override
    protected void onProgressUpdate(TimeSynchronizationProgressUnit... values) {

        Log.d("PROGRESS_UPDATE", values[0].getName());
        Log.d("PROGRESS_UPDATE", Boolean.toString(values[0].isSuccessful()));

    }

    @Override
    protected void onPostExecute(TimeSynchronizationResult timeSynchronizationResult) {

        // get shared preferences editor
        SharedPreferences sharedPreferences = activity.getSharedPreferences("timesync", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // convert result to JSON and store it
        Gson gson = new Gson();
        String resultAsJson = gson.toJson(timeSynchronizationResult);
        editor.putString("TIME_SYNCHRONIZATION_RESULT", resultAsJson);
        boolean done = editor.commit();
        if(done){
            Log.d("EDITOR", "SAVED SUCCESSFULLY!");
        }else{
            Log.d("EDITOR", "COULDN'T SAVE!!!");
        }

    }

    public String extractIpFromParam(String param){
        return param.split(":")[0];
    }

    public int extractPortFromParam(String param){
        return Integer.parseInt(param.split(":")[1]);
    }

    public TimeSynchronizationProgressUnit checkSdCard(){

        if(!SdCardUtils.isSdCardAvailable()){
            // TODO: exception?
            return new TimeSynchronizationProgressUnit("CHECK_SD_CARD", false, null);
        }

        return new TimeSynchronizationProgressUnit("CHECK_SD_CARD", true, null);

    }

    public TimeSynchronizationProgressUnit checkKeyFile(){

        if(!SdCardUtils.isFileExistsOnSdCard("public.key")){
            // TODO: exception?
            return new TimeSynchronizationProgressUnit("CHECK_KEY_FILE", false, null);
        }

        return new TimeSynchronizationProgressUnit("CHECK_KEY_FILE", true, null);

    }

    public TimeSynchronizationProgressUnit readKey(String uriAsString){

        try {
            // publicKey = SdCardUtils.readFileAsBytesFromSdCard("public.key");
            publicKey = SdCardUtils.readFileAsBytesFromUri(contentResolver, Uri.parse(uriAsString));
        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit("READ_KEY", false, e);
        }

        return new TimeSynchronizationProgressUnit("READ_KEY", true, null);

    }

    public TimeSynchronizationProgressUnit connectToServer(String address){

        String ip = extractIpFromParam(address);
        int port = extractPortFromParam(address);

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 2000);
        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit("CONNECT_TO_SERVER", false, e);
        }

        return new TimeSynchronizationProgressUnit("CONNECT_TO_SERVER", true, null);

    }

    public TimeSynchronizationProgressUnit openIoStreams(){

        try {
            serverInputStream = new DataInputStream(socket.getInputStream());
            serverOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit("OPEN_IO_STREAMS", false, e);
        }

        return new TimeSynchronizationProgressUnit("OPEN_IO_STREAMS", true, null);

    }

    public TimeSynchronizationProgressUnit generateNonce(){

        SecureRandom secureRandom = new SecureRandom();
        nonce = new byte[NONCE_SIZE];
        secureRandom.nextBytes(nonce);

        return new TimeSynchronizationProgressUnit("GENERATE_NONCE", true, null);

    }

    public TimeSynchronizationProgressUnit sendNonceToServer(){

        try {
            serverOutputStream.writeInt(NONCE_SIZE);
            serverOutputStream.write(nonce);
        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit("SEND_NONCE_TO_SERVER", false, e);
        }

        return new TimeSynchronizationProgressUnit("SEND_NONCE_TO_SERVER", true, null);

    }

    public TimeSynchronizationProgressUnit receiveTimestampAndSignature(){

        try {

            senderTimeStamp = serverInputStream.readLong();

            int nonceSignatureSize = serverInputStream.readInt();
            nonceSignature = new byte[nonceSignatureSize];
            serverInputStream.read(nonceSignature, 0, nonceSignatureSize);

        } catch (IOException e) {
            return new TimeSynchronizationProgressUnit("RECEIVE_TIMESTAMP_AND_SIGNATURE", false, e);
        }

        return new TimeSynchronizationProgressUnit("RECEIVE_TIMESTAMP_AND_SIGNATURE", true, null);

    }

    public TimeSynchronizationProgressUnit verifySignature(){

        try {
            verifies = DigitalSignature.verify(nonce, nonceSignature, publicKey);
        } catch (NoSuchAlgorithmException e) {
            return new TimeSynchronizationProgressUnit("VERIFY_SIGNATURE", false, e);
        } catch (InvalidKeyException e) {
            return new TimeSynchronizationProgressUnit("VERIFY_SIGNATURE", false, e);
        } catch (InvalidKeySpecException e) {
            return new TimeSynchronizationProgressUnit("VERIFY_SIGNATURE", false, e);
        } catch (SignatureException e) {
            return new TimeSynchronizationProgressUnit("VERIFY_SIGNATURE", false, e);
        } catch (NoSuchProviderException e) {
            return new TimeSynchronizationProgressUnit("VERIFY_SIGNATURE", false, e);
        }

        if(!verifies){
            // TODO
            return new TimeSynchronizationProgressUnit("VERIFY_SIGNATURE", false, null);
        }

        return new TimeSynchronizationProgressUnit("VERIFY_SIGNATURE", true, null);

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
