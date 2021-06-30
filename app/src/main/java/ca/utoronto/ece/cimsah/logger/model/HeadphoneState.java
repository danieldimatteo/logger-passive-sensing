package ca.utoronto.ece.cimsah.logger.model;

import java.util.Date;

public class HeadphoneState {
    private String uid;
    private Date timestamp;
    private Boolean isPluggedIn;

    public HeadphoneState() {}

    public HeadphoneState(String uid, Date timestamp, Boolean isPluggedIn) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.isPluggedIn = isPluggedIn;
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

    public Boolean getPluggedIn() {
        return isPluggedIn;
    }

    public void setPluggedIn(Boolean pluggedIn) {
        isPluggedIn = pluggedIn;
    }

    @Override
    public String toString() {
        return "HeadphoneState{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                ", isPluggedIn=" + isPluggedIn +
                '}';
    }
}
