package saneforce.sanzen.activity.reports.dayReport.model;

public class EventCaptureModelClass {
    String sf_code,Eventimg,title,remarks;
    public EventCaptureModelClass(String sf_code, String eventimg, String title, String remarks) {
        this.sf_code = sf_code;
        Eventimg = eventimg;
        this.title = title;
        this.remarks = remarks;
    }

    public String getSf_code() {
        return sf_code;
    }

    public void setSf_code(String sf_code) {
        this.sf_code = sf_code;
    }

    public String getEventimg() {
        return Eventimg;
    }

    public void setEventimg(String eventimg) {
        Eventimg = eventimg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
