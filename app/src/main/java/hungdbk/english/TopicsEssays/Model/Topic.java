package hungdbk.english.TopicsEssays.Model;

import java.util.ArrayList;

public class Topic {
    private ArrayList<Essay> arrEssays;
    private String detail;
    private int order;
    private String title;

    public Topic(int order, String title, String detail) {
        this.order = order;
        this.title = title;
        this.detail = detail;
    }

    public Topic(int order, String title, String detail, ArrayList<Essay> arrEssays) {
        this.order = order;
        this.title = title;
        this.detail = detail;
        this.arrEssays = arrEssays;
    }

    public int getOrder() {
        return this.order;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDetail() {
        return this.detail;
    }

    public ArrayList<Essay> getArrEssay() {
        return this.arrEssays;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setArrEssay(ArrayList<Essay> arrEssay) {
        this.arrEssays = arrEssay;
    }
}
