package ca.utoronto.ece.cimsah.logger.model;

import java.util.Date;

public class AudioRecording {
    private String userId;
    private Date timestamp;
    private String filename;    // e.g. "v3eOOVNwp7WYzaxudOkRujJNIK62/1548707771935.3gp.encrypted"

    public AudioRecording() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
