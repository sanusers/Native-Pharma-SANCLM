package saneforce.sanclm.activity.presentation;

public class PresentationStoredModel {

    public String getPresentation_name() {
        return presentation_name;
    }

    public void setPresentation_name(String presentation_name) {
        this.presentation_name = presentation_name;
    }

    public String getCounts() {
        return Counts;
    }

    public void setCounts(String counts) {
        Counts = counts;
    }

    String presentation_name;
    String Counts;

    public PresentationStoredModel(String presentation_name, String counts) {
        this.presentation_name = presentation_name;
        Counts = counts;
    }
}
