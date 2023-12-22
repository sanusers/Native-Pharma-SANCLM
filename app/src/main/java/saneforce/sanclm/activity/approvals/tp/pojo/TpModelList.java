package saneforce.sanclm.activity.approvals.tp.pojo;

public class TpModelList {
    String code,name,month,year,mn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMn() {
        return mn;
    }

    public void setMn(String mn) {
        this.mn = mn;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public TpModelList(String code,String name, String month, String year,String mn) {
        this.code = code;
        this.name = name;
        this.month = month;
        this.year = year;
        this.mn = mn;
    }
}
