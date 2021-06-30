package ca.utoronto.ece.cimsah.logger.model;

import java.util.Date;

import ca.utoronto.ece.cimsah.logger.calendar.CalendarProvider;

public class CalendarEvent {
    private String uid;
    private long id;
    private Date start;
    private Date end;

    public CalendarEvent() {}

    public CalendarEvent(String uid, Long id, Date start, Date end) {
        this.uid = uid;
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "CalendarEvent{" +
                "uid='" + uid + '\'' +
                ", id=" + id +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
