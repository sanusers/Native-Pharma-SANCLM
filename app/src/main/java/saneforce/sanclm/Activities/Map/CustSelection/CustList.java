package saneforce.sanclm.Activities.Map.CustSelection;

public class CustList {
    String name;
    String category;
    String specialist;
    String area;
    String tagCount;

    public CustList(String name, String category, String specialist, String area) {
        this.name = name;
        this.category = category;
        this.specialist = specialist;
        this.area = area;
    }

    public CustList(String name, String category, String specialist, String area, String tagCount) {
        this.name = name;
        this.category = category;
        this.specialist = specialist;
        this.area = area;
        this.tagCount = tagCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTagCount() {
        return tagCount;
    }

    public void setTagCount(String tagCount) {
        this.tagCount = tagCount;
    }
}
