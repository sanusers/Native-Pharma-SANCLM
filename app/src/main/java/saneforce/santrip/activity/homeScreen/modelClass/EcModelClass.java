package saneforce.santrip.activity.homeScreen.modelClass;

public class EcModelClass {
    private String cusCode;
    private String cusName;
    private String sync_status;
    private int synced;
    private String dates;
    private int id;
    private String img_name;
    private String filePath;
    private String json_values;
    public EcModelClass(int id, String dates, String cusCode, String cusName, String img_name, String filePath, String json_values, String sync_status, int synced) {
        this.id = id;
        this.dates = dates;
        this.img_name = img_name;
        this.cusCode = cusCode;
        this.cusName = cusName;
        this.filePath = filePath;
        this.json_values = json_values;
        this.sync_status = sync_status;
        this.synced = synced;
    }

    public String getCusCode() {
        return cusCode;
    }

    public void setCusCode(String cusCode) {
        this.cusCode = cusCode;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
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