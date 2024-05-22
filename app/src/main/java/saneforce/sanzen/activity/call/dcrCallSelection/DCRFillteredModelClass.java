package saneforce.sanzen.activity.call.dcrCallSelection;

public class DCRFillteredModelClass {

    private String Name,Code;

    public DCRFillteredModelClass(String name, String code) {
        Name = name;
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
