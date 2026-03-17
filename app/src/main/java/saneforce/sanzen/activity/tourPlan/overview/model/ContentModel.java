package saneforce.sanzen.activity.tourPlan.overview.model;

import android.text.SpannableString;

public class ContentModel {
    private String content, subContent, sideContent;
    private SpannableString spannableString;
    private boolean isNoData;

    public ContentModel(String content, String subContent, String sideContent) {
        this.content = content;
        this.subContent = subContent;
        this.sideContent = sideContent;
        isNoData = false;
    }

    public ContentModel(String content) {
        this.content = content;
        isNoData = true;
        subContent = "";
        sideContent = "";
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

    public SpannableString getSpannableString() {
        return spannableString;
    }

    public void setSpannableString(SpannableString spannableString) {
        this.spannableString = spannableString;
    }

    public boolean isNoData() {
        return isNoData;
    }

    public void setNoData(boolean noData) {
        isNoData = noData;
    }
}
