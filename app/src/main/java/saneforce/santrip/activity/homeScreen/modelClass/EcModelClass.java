package saneforce.santrip.activity.homeScreen.modelClass;

public class EcModelClass {
    private String dates;
    private String id;
    private String name;
    private String filePath;
    private String json_values;

    public EcModelClass(String id, String dates, String name, String filePath, String json_values) {
        this.id = id;
        this.dates = dates;
        this.name = name;
        this.filePath = filePath;
        this.json_values = json_values;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getJson_values() {
        return json_values;
    }

    public void setJson_values(String json_values) {
        this.json_values = json_values;
    }
}