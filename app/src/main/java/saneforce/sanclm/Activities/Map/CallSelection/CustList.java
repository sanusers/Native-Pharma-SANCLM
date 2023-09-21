package saneforce.sanclm.Activities.Map.CallSelection;

public class CustList {
    String name;
    String category;

    public CustList(String name, String category, String specialist, String area, String tagCount) {
        this.name = name;
        this.category = category;
        this.specialist = specialist;
        this.area = area;
        this.tagCount = tagCount;
    }

    String specialist;
    String area;
    String tagCount;

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
