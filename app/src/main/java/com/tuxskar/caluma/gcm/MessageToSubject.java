package com.tuxskar.caluma.gcm;

/**
 * Created by tuxskar on 17/05/15.
 */
public class MessageToSubject {
    String receiver;
    String message;

    public MessageToSubject(String receiver, String message) {
        this.receiver = receiver;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
