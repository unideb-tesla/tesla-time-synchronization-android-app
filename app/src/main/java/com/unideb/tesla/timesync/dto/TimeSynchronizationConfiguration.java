package com.unideb.tesla.timesync.dto;

import android.net.Uri;

public class TimeSynchronizationConfiguration {

    private String publicKeyFileUriAsString;
    private String publicKeyFileName;
    private String serverAddress;

    public TimeSynchronizationConfiguration(String publicKeyFileUriAsString, String publicKeyFileName, String serverAddress) {
        this.publicKeyFileUriAsString = publicKeyFileUriAsString;
        this.publicKeyFileName = publicKeyFileName;
        this.serverAddress = serverAddress;
    }

    public String getPublicKeyFileUriAsString() {
        return publicKeyFileUriAsString;
    }

    public void setPublicKeyFileUriAsString(String publicKeyFileUriAsString) {
        this.publicKeyFileUriAsString = publicKeyFileUriAsString;
    }

    public String getPublicKeyFileName() {
        return publicKeyFileName;
    }

    public void setPublicKeyFileName(String publicKeyFileName) {
        this.publicKeyFileName = publicKeyFileName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

}
