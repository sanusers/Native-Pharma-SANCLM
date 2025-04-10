package saneforce.sanzen.activity.approvals.stp.model;

import org.json.JSONObject;

public class STPDetailedModel {
    private final String transNo;
    private final String sfCode;
    private final String divisionCode;
    private final String dayPlanName;
    private final String dayPlanShortName;
    private final String dayPlanCode;
    private final String clusterCode;
    private final String clusterName;
    private final String doctorCode;
    private final String doctorName;
    private final String chemistCode;
    private final String chemistName;
    private final String ActiveFlag;
    private final String createdDate;
    private String date;

    public STPDetailedModel(String transNo, String sfCode, String divisionCode, String dayPlanName, String dayPlanShortName, String dayPlanCode, String clusterCode, String clusterName, String doctorCode, String doctorName, String chemistCode, String chemistName, String activeFlag, String createdDate) {
        this.transNo = transNo;
        this.sfCode = sfCode;
        this.divisionCode = divisionCode;
        this.dayPlanName = dayPlanName;
        this.dayPlanShortName = dayPlanShortName;
        this.dayPlanCode = dayPlanCode;
        this.clusterCode = clusterCode;
        this.clusterName = clusterName;
        this.doctorCode = doctorCode;
        this.doctorName = doctorName;
        this.chemistCode = chemistCode;
        this.chemistName = chemistName;
        ActiveFlag = activeFlag;
        this.createdDate = createdDate;
    }

    public String getTransNo() {
        return transNo;
    }

    public String getSfCode() {
        return sfCode;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public String getDayPlanName() {
        return dayPlanName;
    }

    public String getDayPlanShortName() {
        return dayPlanShortName;
    }

    public String getDayPlanCode() {
        return dayPlanCode;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getChemistCode() {
        return chemistCode;
    }

    public String getActiveFlag() {
        return ActiveFlag;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getChemistName() {
        return chemistName;
    }

    public String getDate() {
        try {
            JSONObject jsonObject = new JSONObject(createdDate);
            return jsonObject.optString("date", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
