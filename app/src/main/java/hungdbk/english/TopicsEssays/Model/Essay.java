package hungdbk.english.TopicsEssays.Model;

public class Essay {
    private String content;
    private String note;
    private int order;
    private String time;

    public Essay(int order, String content, String time, String note) {
        this.order = order;
        this.content = content;
        this.time = time;
        this.note = note;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
