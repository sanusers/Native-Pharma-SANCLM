package saneforce.sanzen.activity.forms.birthdayAnniversary;

public class AnniversaryModel {

    String doctorName;
    String anniversaryDate;
    private String code;
    private String territory;
    private String qualification;
    private String category;
    private String speciality;
    private String className;
    //String color;

    public AnniversaryModel(String doctorName, String anniversaryDate, String code, String territory, String qualification,
                            String category, String speciality , String className) {
        this.doctorName = doctorName;
        this.anniversaryDate = anniversaryDate;
        this.code = code;
        this.territory = territory;
        this.qualification = qualification;
        this.category = category;
        this.speciality = speciality;
        this.className = className;
        //this.color = color;

    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getAnniversaryDate() {
        return anniversaryDate;
    }

    public void setAnniversaryDate(String birthDate) {
        this.anniversaryDate = birthDate;
    }
    public String getcode() {
        return code;
    }

    public String getTerritoryWeds() {
        return territory;
    }

    public String getQualificationWeds() {
        return qualification;
    }

    public String getCategoryWeds() {
        return category;
    }

    public String getSpecialityWeds() {
        return speciality;
    }

    public String getClassNameWeds() {
        return className;
    }

    public void setCategoryWeds(String category) {
        this.category = category;
    }

    public void setSpecialityWeds(String speciality) {
        this.speciality = speciality;
    }

    public void setQualificationWeds(String qualification) {
        this.qualification = qualification;
    }

    public void setClassNameWeds(String className) {
        this.className = className;
    }

//    public String getColor() {
//        return color;
//    }
//
//    public void setColor(String color) {
//        this.color = color;
//    }

}
