package saneforce.sanzen.activity.tourPlan.overview.model;

public class ContentModel {
    private String content, subContent, sideContent;

    public ContentModel(String content, String subContent, String sideContent) {
        this.content = content;
        this.subContent = subContent;
        this.sideContent = sideContent;
    }

    public String getContent() {
        return content;
    }

    public String getSubContent() {
        return subContent;
    }

    public String getSideContent() {
        return sideContent;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    public void setSideContent(String sideContent) {
        this.sideContent = sideContent;
    }
}
