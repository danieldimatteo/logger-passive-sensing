package ca.utoronto.ece.cimsah.logger.model;

import java.util.Date;

public class PermissionsState {
    // doesn't contain UID because the object is created with the UID as its key
    private Date timestamp;
    private Boolean recordAudioGranted;
    private Boolean readCalendarGranted;
    private Boolean readContactsGranted;
    private Boolean accessFineLocationGranted;

    public PermissionsState() {}

    public PermissionsState(Date timestamp, Boolean audio, Boolean calendar,
                            Boolean contacts, Boolean location) {
        this.timestamp = timestamp;
        this.recordAudioGranted = audio;
        this.readCalendarGranted = calendar;
        this.readContactsGranted = contacts;
        this.accessFineLocationGranted = location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getRecordAudioGranted() {
        return recordAudioGranted;
    }

    public void setRecordAudioGranted(Boolean recordAudioGranted) {
        this.recordAudioGranted = recordAudioGranted;
    }

    public Boolean getReadCalendarGranted() {
        return readCalendarGranted;
    }

    public void setReadCalendarGranted(Boolean readCalendarGranted) {
        this.readCalendarGranted = readCalendarGranted;
    }

    public Boolean getReadContactsGranted() {
        return readContactsGranted;
    }

    public void setReadContactsGranted(Boolean readContactsGranted) {
        this.readContactsGranted = readContactsGranted;
    }

    public Boolean getAccessFineLocationGranted() {
        return accessFineLocationGranted;
    }

    public void setAccessFineLocationGranted(Boolean accessFineLocationGranted) {
        this.accessFineLocationGranted = accessFineLocationGranted;
    }

    @Override
    public String toString() {
        return "PermissionsState{" +
                "timestamp=" + timestamp +
                ", recordAudioGranted=" + recordAudioGranted +
                ", readCalendarGranted=" + readCalendarGranted +
                ", readContactsGranted=" + readContactsGranted +
                ", accessFineLocationGranted=" + accessFineLocationGranted +
                '}';
    }
}
