package prod.tuxskar.caluma.ws.models;

public class SubjectSimple {
    private String description, title;
    private int code, level;
    private long id;
    private SimpleInfo t_subject[];
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SimpleInfo[] getT_subject() {
        return t_subject;
    }

    public void setT_subject(SimpleInfo[] t_subject) {
        this.t_subject = t_subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String toString() {
        return this.title;
    }
}
