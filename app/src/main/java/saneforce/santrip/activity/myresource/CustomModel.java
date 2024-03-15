package saneforce.santrip.activity.myresource;

public class CustomModel {

    private String title;
    private String description;

    public CustomModel( String description) {//String title,
//        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
