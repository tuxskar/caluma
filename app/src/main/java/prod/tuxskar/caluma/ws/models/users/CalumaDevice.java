package prod.tuxskar.caluma.ws.models.users;

public class CalumaDevice {
    String registration_id, device_id;

    public CalumaDevice(String registration_id, String device_id) {
        this.registration_id = registration_id;
        this.device_id = device_id;
    }

    public String getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(String registration_id) {
        this.registration_id = registration_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }


}
