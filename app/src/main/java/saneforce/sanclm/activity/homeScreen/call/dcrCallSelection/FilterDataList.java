package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection;

public class FilterDataList {
    String name;
    String speciality,Territory,Category;

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getTerritory() {
        return Territory;
    }

    public void setTerritory(String territory) {
        Territory = territory;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getIscase() {
        return Iscase;
    }

    public FilterDataList(String name, String iscase) {
        this.name = name;
        Iscase = iscase;
    }

    public void setIscase(String iscase) {
        Iscase = iscase;
    }

    String Iscase;
    String code;
    int pos;

    public FilterDataList(String name, String code, int pos) {
        this.name = name;
        this.code = code;
        this.pos = pos;
    }

    public FilterDataList(String name, int pos) {
        this.name = name;
        this.pos = pos;
    }

    public FilterDataList(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
