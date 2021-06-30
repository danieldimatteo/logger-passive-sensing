package ca.utoronto.ece.cimsah.logger.model;

import java.util.Date;

public class ScreenState {
    private String uid;
    private Date timestamp;
    private Boolean isScreenOn;

    public ScreenState() {}

    public ScreenState(String uid, Date timestamp, Boolean isScreenOn) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.isScreenOn = isScreenOn;
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

    public Boolean getScreenOn() {
        return isScreenOn;
    }

    public void setScreenOn(Boolean screenOn) {
        isScreenOn = screenOn;
    }

    @Override
    public String toString() {
        return "ScreenState{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                ", isScreenOn=" + isScreenOn +
                '}';
    }
}
