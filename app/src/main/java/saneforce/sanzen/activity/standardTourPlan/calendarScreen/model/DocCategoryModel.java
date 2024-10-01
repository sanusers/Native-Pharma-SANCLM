package saneforce.sanzen.activity.standardTourPlan.calendarScreen.model;

public class DocCategoryModel {

    private final int categoryID;
    private final String categoryName;
    private final String doc_cat_name;
    private int visitCount;
    private int docCount;

    public DocCategoryModel(int categoryID, String categoryName, String doc_cat_name, int visitCount, int docCount) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.doc_cat_name = doc_cat_name;
        this.visitCount = visitCount;
        this.docCount = docCount;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDoc_cat_name() {
        return doc_cat_name;
    }

    public int getDocCount() {
        return docCount;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public void incrementDocCount() {
        docCount++;
    }
}
