package ca.utoronto.ece.cimsah.logger.model;

import java.util.Date;

public class BatteryCharge {
    private String uid;
    private Date timestamp;
    private float batteryPercent;

    public BatteryCharge() {}

    public BatteryCharge(String uid, Date timestamp, float chargePercentage) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.batteryPercent = chargePercentage;
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

    public float getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(float batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    @Override
    public String toString() {
        return "BatteryCharge{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                ", batteryPercent=" + batteryPercent +
                '}';
    }
}
