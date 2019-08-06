package hungdbk.english.TopicsEssays.Model;

public class Vocab {
    private String note;
    private int order;
    private String time;
    private String word;

    public Vocab(int order, String word, String time, String note) {
        this.order = order;
        this.word = word;
        this.time = time;
        this.note = note;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
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
