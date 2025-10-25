package saneforce.sanzen.activity.forms.birthdayAnniversary;

public class birthdayModel {

    String doctorName;
    String birthDate;

  String color;

    public birthdayModel(String doctorName, String birthDate ) {
        this.doctorName = doctorName;
        this.birthDate = birthDate;
       this.color = color;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

//    public String getAnniversaryDate() {
//        return anniversaryDate;
//    }
//
//    public void setAnniversaryDate(String anniversaryDate) {
//        this.anniversaryDate = anniversaryDate;
//    }
}
