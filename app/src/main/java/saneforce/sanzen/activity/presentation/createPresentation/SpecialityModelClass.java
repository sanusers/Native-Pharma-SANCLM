package saneforce.sanzen.activity.presentation.createPresentation;

public class SpecialityModelClass {
    private String code;
    private String name;
    private String docSpecialName;
    private String divisionCode;

    public SpecialityModelClass(String code, String name, String docSpecialName, String divisionCode) {
        this.code = code;
        this.name = name;
        this.docSpecialName = docSpecialName;
        this.divisionCode = divisionCode;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDocSpecialName() { return docSpecialName; }
    public String getDivisionCode() { return divisionCode; }
}
