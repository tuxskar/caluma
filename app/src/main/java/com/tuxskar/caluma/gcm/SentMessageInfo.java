package com.tuxskar.caluma.gcm;

/**
 * Created by tuxskar on 17/05/15.
 */
public class SentMessageInfo {

    String status;
    int n_devices;

    public SentMessageInfo(String status, int n_devices) {
        this.status = status;
        this.n_devices = n_devices;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getN_devices() {
        return n_devices;
    }

    public void setN_devices(int n_devices) {
        this.n_devices = n_devices;
    }

}
