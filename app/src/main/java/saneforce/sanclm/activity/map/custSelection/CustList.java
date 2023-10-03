package saneforce.sanclm.activity.map.custSelection;

public class CustList {
    String name;
    String type;
    String code;
    String town_code;
    String category;
    String specialist;
    String position;
    String town_name;
    String maxTag;
    String Tag;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CustList(String name, String category, String specialist, String town_name) {
        this.name = name;
        this.category = category;
        this.specialist = specialist;
        this.town_name = town_name;
    }

    public CustList(String name, String code, String type, String category, String specialist, String town_name, String towncode, String tag, String maxTag, String position) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.specialist = specialist;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTown_code() {
        return town_code;
    }

    public void setTown_code(String town_code) {
        this.town_code = town_code;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public String getMaxTag() {
        return maxTag;
    }

    public void setMaxTag(String maxTag) {
        this.maxTag = maxTag;
    }
}
