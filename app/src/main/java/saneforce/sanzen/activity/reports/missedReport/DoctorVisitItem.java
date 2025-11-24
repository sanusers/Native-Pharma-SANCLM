package saneforce.sanzen.activity.reports.missedReport;

import java.io.Serializable;
public class DoctorVisitItem implements Serializable {
    private String name;
    private String code;
    private String territory;
    private String qualification;
    private String category;
    private String speciality;
    private String className;

    //private String monthKey;
    public DoctorVisitItem(String name, String territory,String code, String qualification,
                           String category, String speciality, String className) {

        this.name = name;
        this.code = code;
        this.territory = territory;
        this.qualification = qualification;
        this.category = category;
        this.speciality = speciality;
        this.className = className;


       //this.monthKey =monthKey;
    }
    public DoctorVisitItem(){

    }



    public String getName() {
        return name;
    }

    public String getcode() {
        return code;
    }

    public String getTerritory() {
        return territory;
    }

    public String getQualification() {
        return qualification;
    }

    public String getCategory() {
        return category;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getClassName() {
        return className;
    }

    public void setName(String name) {this.name = name; }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    public void setClassName(String className) {
        this.className = className;
    }

}
