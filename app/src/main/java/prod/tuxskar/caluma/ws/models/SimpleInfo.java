package prod.tuxskar.caluma.ws.models;

public class SimpleInfo implements Comparable<SimpleInfo> {
    private String title;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return this.title;
    }

    @Override
    public int compareTo(SimpleInfo another) {
        return getTitle().compareTo(another.getTitle());
    }
}