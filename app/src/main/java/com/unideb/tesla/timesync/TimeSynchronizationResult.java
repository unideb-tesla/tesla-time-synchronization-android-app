package com.unideb.tesla.timesync;

import java.util.Date;

public class TimeSynchronizationResult {

    private boolean isSuccessful;
    private long delay;
    private Date date;

    public TimeSynchronizationResult(boolean isSuccessful, long delay, Date date) {
        this.isSuccessful = isSuccessful;
        this.delay = delay;
        this.date = date;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
