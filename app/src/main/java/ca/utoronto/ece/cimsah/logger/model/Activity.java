package ca.utoronto.ece.cimsah.logger.model;

import com.google.android.gms.location.ActivityRecognitionResult;

import java.util.Date;

import ca.utoronto.ece.cimsah.logger.util.SnapshotUtil;

public class Activity {
    private String uid;
    private Date timestamp;
    private String type;
    private int confidence;

    public Activity() {}

    public Activity(String uid, ActivityRecognitionResult ar) {
        this.uid = uid;
        this.timestamp = new Date(ar.getTime());
        this.type = SnapshotUtil.getActivityTypeName(ar.getMostProbableActivity());
        this.confidence = ar.getMostProbableActivity().getConfidence();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
