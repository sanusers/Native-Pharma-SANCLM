package saneforce.sanzen.activity.forms.birthdayAnniversary;

public class anniversaryModel {

    String doctorName;
    String anniversaryDate;

    String color;

    public anniversaryModel(String doctorName, String anniversaryDate) {
        this.doctorName = doctorName;
        this.anniversaryDate = anniversaryDate;
        this.color = color;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
