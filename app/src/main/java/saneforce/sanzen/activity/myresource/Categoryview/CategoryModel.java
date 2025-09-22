package saneforce.sanzen.activity.myresource.Categoryview;

public class CategoryModel {
    private final String name, categoryName, noOfVisits;

    public CategoryModel(String name, String categoryName, String noOfVisits) {
        this.name = name;
        this.categoryName = categoryName;
        this.noOfVisits = noOfVisits;
    }

    public String getName() {
        return name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getNoOfVisits() {
        return noOfVisits;
    }
}
