package saneforce.sanzen.activity.homeScreen.modelClass;

public class MultiHQClusterItem {
    private final String name, code, hqCode;
    private boolean isChecked;

    public MultiHQClusterItem(String name, String code, String hqCode, boolean isChecked) {
        this.name = name;
        this.code = code;
        this.hqCode = hqCode;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getHqCode() {
        return hqCode;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
