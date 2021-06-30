package ca.utoronto.ece.cimsah.logger.model;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class LocationState {
    private String uid;
    private Date timestamp;
    private GeoPoint location;

    public LocationState() {}

    public LocationState(String uid, Date timestamp, Location androidLocation) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.location = new GeoPoint(androidLocation.getLatitude(), androidLocation.getLongitude());
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

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "LocationState{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
