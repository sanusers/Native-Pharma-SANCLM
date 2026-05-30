package saneforce.sanzen.activity.call.pojo.detailing;

public class HtmlViewSession {
    private String fileName;
    private long startTime;
    private long endTime;
    private long duration;

    public HtmlViewSession(String fileName, long startTime, long endTime, long duration) {
        this.fileName = fileName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}