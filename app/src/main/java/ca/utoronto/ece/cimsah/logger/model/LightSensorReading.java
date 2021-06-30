package ca.utoronto.ece.cimsah.logger.model;

import java.util.Date;

public class LightSensorReading {
    private String uid;
    private Date timestamp;
    private float illuminance;

    public LightSensorReading() {}

    public LightSensorReading(String uid, Date timestamp, float illuminance) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.illuminance = illuminance;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getIlluminance() {
        return illuminance;
    }

    public void setIlluminance(float illuminance) {
        this.illuminance = illuminance;
    }

    @Override
    public String toString() {
        return "LightSensorReading{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                ", illuminance=" + illuminance +
                '}';
    }
}
