package ca.utoronto.ece.cimsah.logger.model;

public class Contact {
    private String uid;
    private long id;

    public Contact() {}

    public Contact(String uid, long id) {
        this.uid = uid;
        this.id = id;
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

    @Override
    public String toString() {
        return "Contact{" +
                "uid='" + uid + '\'' +
                ", id=" + id +
                '}';
    }
}
