package saneforce.sanzen.activity.homeScreen.modelClass;

public class MultiHQClusterItem {
    private final String name;
    private String code, hqCode;
    private boolean isChecked, isPlaceholder;;

    public MultiHQClusterItem(String name, String code, String hqCode, boolean isChecked) {
        this.name = name;
        this.code = code;
        this.hqCode = hqCode;
        this.isChecked = isChecked;
    }

    public MultiHQClusterItem(String name, boolean isPlaceholder) {
        this.name = name;
        this.isPlaceholder = isPlaceholder;
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

    public boolean isPlaceholder() {
        return isPlaceholder;
    }

    public void setPlaceholder(boolean placeholder) {
        isPlaceholder = placeholder;
    }

}
