package saneforce.sanzen.activity.tourPlan.overview.model;

import java.util.ArrayList;
import java.util.List;

public class HeaderModel {
    private final String title;
    private List<ContentModel> contentModelList;

    public HeaderModel(String title) {
        this.title = title;
        this.contentModelList = new ArrayList<>();
    }

    public List<ContentModel> getContentModelList() {
        return contentModelList;
    }

    public void addContent(ContentModel contentModel) {
        if (contentModelList == null) contentModelList = new ArrayList<>();
        contentModelList.add(contentModel);
    }

    public void setContentModelList(List<ContentModel> contentModelList) {
        this.contentModelList = contentModelList;
    }

    public String getTitle() {
        return title;
    }
}
