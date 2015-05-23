/**
 * Created by tuxskar on 20/05/15.
 */
package com.tuxskar.caluma.ws;

import java.util.Date;

public class MessageToSubject {
    private String status, message;
    private Date created, modified, status_changed;
    private long id, receiver;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getStatus_changed() {
        return status_changed;
    }

    public void setStatus_changed(Date status_changed) {
        this.status_changed = status_changed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReceiver() {
        return receiver;
    }

    public void setReceiver(long receiver) {
        this.receiver = receiver;
    }
}
