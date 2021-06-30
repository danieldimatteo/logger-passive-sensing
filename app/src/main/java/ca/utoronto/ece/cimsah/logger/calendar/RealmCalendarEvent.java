package ca.utoronto.ece.cimsah.logger.calendar;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dandm on 2016-11-16.
 */

public class RealmCalendarEvent extends RealmObject {
    @PrimaryKey
    private long id;
    private long startTimestamp;
    private long endTimestamp;

    public RealmCalendarEvent(long id, long startTimestamp, long endTimestamp) {
        this.id = id;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public RealmCalendarEvent() {}

    public long getId() {
        return id;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    @Override
    public String toString() {
        return "RealmCalendarEvent{" +
                "id=" + id +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                '}';
    }
}
