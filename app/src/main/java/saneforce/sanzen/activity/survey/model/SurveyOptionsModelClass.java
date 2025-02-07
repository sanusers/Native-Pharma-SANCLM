package saneforce.sanzen.activity.survey.model;

public class SurveyOptionsModelClass {
    private final String name;
    private final String id;
    private final boolean isChecked;

    public SurveyOptionsModelClass(String name, String id, boolean isChecked) {
        this.name = name;
        this.id = id;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
