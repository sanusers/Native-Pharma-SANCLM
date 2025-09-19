package saneforce.sanzen.activity.reports.missedReport;

public class GraphReport {

    private String doctorName;
    private String missedDate;

    // Constructor
    public GraphReport(String doctorName, String missedDate) {
        this.doctorName = doctorName;
        this.missedDate = missedDate;
    }

    // Getter for doctorName
    public String getDoctorName() {
        return doctorName;
    }

    // Setter for doctorName
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    // Getter for missedDate
    public String getMissedDate() {
        return missedDate;
    }

    // Setter for missedDate
    public void setMissedDate(String missedDate) {
        this.missedDate = missedDate;
    }
}
