package saneforce.sanzen.activity.reports.dayReport.model;

public class DayReportRcpaModelClass {


    private String DrName;
    private String ChmName;
    private String OPName;
    private String OPQty;
    private String OPUnit;
    private String CompName;
    private String CompPName;
    private String CPQty;
    private String CPUnit;
    private  String  OPRate;
    private String OPValue;
    private  String CPRate;
    private String CPValue;
    private String CPRemarks;
    private boolean isSelected= false;

    public DayReportRcpaModelClass(String drName, String chmName, String OPName, String OPQty, String OPUnit, String compName, String compPName, String CPQty, String CPUnit, String OPRate, String OPValue, String CPRate, String CPValue,boolean isSelected) {
        DrName = drName;
        ChmName = chmName;
        this.OPName = OPName;
        this.OPQty = OPQty;
        this.OPUnit = OPUnit;
        CompName = compName;
        CompPName = compPName;
        this.CPQty = CPQty;
        this.CPUnit = CPUnit;
        this.OPRate = OPRate;
        this.OPValue = OPValue;
        this.CPRate = CPRate;
        this.CPValue = CPValue;
        this.isSelected = isSelected;

    }

    public String getCPRemarks() {
        return CPRemarks;
    }

    public void setCPRemarks(String CPRemarks) {
        this.CPRemarks = CPRemarks;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public String getOPRate() {
        return OPRate;
    }

    public void setOPRate(String OPRate) {
        this.OPRate = OPRate;
    }

    public String getOPValue() {
        return OPValue;
    }

    public void setOPValue(String OPValue) {
        this.OPValue = OPValue;
    }

    public String getCPRate() {
        return CPRate;
    }

    public void setCPRate(String CPRate) {
        this.CPRate = CPRate;
    }

    public String getCPValue() {
        return CPValue;
    }

    public void setCPValue(String CPValue) {
        this.CPValue = CPValue;
    }

    public String getDrName() {
        return DrName;
    }

    public void setDrName(String drName) {
        DrName = drName;
    }

    public String getChmName() {
        return ChmName;
    }

    public void setChmName(String chmName) {
        ChmName = chmName;
    }

    public String getOPName() {
        return OPName;
    }

    public void setOPName(String OPName) {
        this.OPName = OPName;
    }

    public String getOPQty() {
        return OPQty;
    }

    public void setOPQty(String OPQty) {
        this.OPQty = OPQty;
    }

    public String getOPUnit() {
        return OPUnit;
    }

    public void setOPUnit(String OPUnit) {
        this.OPUnit = OPUnit;
    }

    public String getCompName() {
        return CompName;
    }

    public void setCompName(String compName) {
        CompName = compName;
    }

    public String getCompPName() {
        return CompPName;
    }

    public void setCompPName(String compPName) {
        CompPName = compPName;
    }

    public String getCPQty() {
        return CPQty;
    }

    public void setCPQty(String CPQty) {
        this.CPQty = CPQty;
    }

    public String getCPUnit() {
        return CPUnit;
    }

    public void setCPUnit(String CPUnit) {
        this.CPUnit = CPUnit;
    }
}
