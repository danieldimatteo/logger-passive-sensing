package ca.utoronto.ece.cimsah.logger.contacts;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dandm on 2016-04-21.
 */
public class RealmContact extends RealmObject {
    @PrimaryKey
    private long contactId;
    private String name;
    private int timesContacted;

    @Override
    public String toString() {
        return "RealmContact{" +
                "contactId=" + contactId +
                ", timesContacted=" + timesContacted +
                '}';
    }

    public RealmContact() {
        this.contactId = -1;
    }

    public RealmContact(long contactId, int timesContacted) {
        this.contactId = contactId;
        this.timesContacted = timesContacted;
    }

    public long getContactId() {
        return contactId;
    }


    public String getName() {
        return name;
    }

    public int getTimesContacted() {
        return timesContacted;
    }

}
