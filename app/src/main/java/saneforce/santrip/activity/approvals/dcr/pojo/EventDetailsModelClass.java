package saneforce.santrip.activity.approvals.dcr.pojo;

public class EventDetailsModelClass {

    String Name ;
    String Description;

    public String getName() {

        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public EventDetailsModelClass(String name, String description) {
        Name = name;
        Description = description;
    }
}
