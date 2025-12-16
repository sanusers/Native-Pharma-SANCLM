package saneforce.sanzen.activity.forms.birthdayAnniversary;

import java.io.Serializable;

public class BirthdayModel implements Serializable {
//public class birthdayModel {

    String doctorName;
    String birthDate;
    //String Place;
    // private String name;
    private String code;
    private String territory;
    private String qualification;
    private String category;
    private String speciality;
    private String className;
    // String color;

    public BirthdayModel(String doctorName, String birthDate,
                         String code, String territory, String qualification,
                         String category, String speciality, String className) {
        this.doctorName = doctorName;
        this.birthDate = birthDate;
        this.code = code;
        this.territory = territory;
        this.qualification = qualification;
        this.category = category;
        this.speciality = speciality;
        this.className = className;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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
//    public String getAnniversaryDate() {
//        return anniversaryDate;
//    }
//
//    public void setAnniversaryDate(String anniversaryDate) {
//        this.anniversaryDate = anniversaryDate;
//    }
}
