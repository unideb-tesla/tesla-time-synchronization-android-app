package com.unideb.tesla.timesync.dto;

public class TimeSynchronizationProgressUnit {

    private String name;
    private boolean isSuccessful;
    private Exception exception;

    public TimeSynchronizationProgressUnit(String name, boolean isSuccessful, Exception exception) {
        this.name = name;
        this.isSuccessful = isSuccessful;
        this.exception = exception;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
