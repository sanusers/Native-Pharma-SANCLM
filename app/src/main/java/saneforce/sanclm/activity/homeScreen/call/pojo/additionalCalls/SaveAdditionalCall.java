package saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls;

public class SaveAdditionalCall {
    String name;
    String code;
    String town_code,town_name;

    public String getTown_code() {
        return town_code;
    }

    public void setTown_code(String town_code) {
        this.town_code = town_code;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
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

    public SaveAdditionalCall(String name,String code,String town_name,String town_code) {
        this.name = name;
        this.code = code;
        this.town_code = town_code;
        this.town_name = town_name;
    }
}
