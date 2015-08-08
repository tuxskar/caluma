package com.tuxskar.caluma.ws.models.users;

public class DeviceInfo {
    Boolean active, created;

    public DeviceInfo(Boolean active, Boolean created) {
        this.active = active;
        this.created = created;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

}
