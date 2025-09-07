package saneforce.sanzen.activity.reports.visitMonitor.model;

public class DoctorStatsModel {
    public String totalDoctors;
    public String visitedDoctors;
    public String missedDoctors;
    public String fwDays;
    public String callAvg;
    public String coverage;

    private float oneVisitCount;
    private float twoVisitCount;
    private float threeVisitCount;
    private float threePlusVisitCount;

    public DoctorStatsModel( String totalDoctors, String visitedDoctors, String missedDoctors, String fwDays, String callAvg, String coverage,float oneVisitCount,float twoVisitCount,float threeVisitCount,float threePlusVisitCount) {

        this.totalDoctors = totalDoctors;
        this.visitedDoctors = visitedDoctors;
        this.missedDoctors = missedDoctors;
        this.fwDays = fwDays;
        this.callAvg = callAvg;
        this.coverage = coverage;
        this.oneVisitCount = oneVisitCount;
        this.twoVisitCount = twoVisitCount;
        this.threeVisitCount = threeVisitCount;
        this.threePlusVisitCount = threePlusVisitCount;
    }



    public String getTotalDoctors() {
        return totalDoctors;
    }

    public String setTotalDoctors(String totalDoctors) {
        this.totalDoctors = totalDoctors;
        return totalDoctors;
    }

    public String getVisitedDoctors() {
        return visitedDoctors;
    }

    public String setVisitedDoctors(String visitedDoctors) {
        this.visitedDoctors = visitedDoctors;
        return visitedDoctors;
    }

    public String getMissedDoctors() {
        return missedDoctors;
    }

    public String setMissedDoctors(String missedDoctors) {
        this.missedDoctors = missedDoctors;
        return missedDoctors;
    }

    public String getFwDays() {
        return fwDays;
    }

    public String setFwDays(String fwDays) {
        this.fwDays = fwDays;
        return fwDays;
    }

    public String getCallAvg() {
        return callAvg;
    }

    public String setCallAvg(String callAvg) {
        this.callAvg = callAvg;
        return callAvg;
    }

    public String getCoverage() {
        return coverage;
    }

    public String setCoverage(String coverage) {
        this.coverage = coverage;
        return coverage;
    }

    public float getOneVisitCount() {
        return oneVisitCount;
    }

    public void setOneVisitCount(float oneVisitCount) {
        this.oneVisitCount = oneVisitCount;
    }

    public float getTwoVisitCount() {
        return twoVisitCount;
    }

    public void setTwoVisitCount(float twoVisitCount) {
        this.twoVisitCount = twoVisitCount;
    }

    public float getThreeVisitCount() {
        return threeVisitCount;
    }

    public void setThreeVisitCount(float threeVisitCount) {
        this.threeVisitCount = threeVisitCount;
    }

    public float getThreePlusVisitCount() {
        return threePlusVisitCount;
    }

    public void setThreePlusVisitCount(float threePlusVisitCount) {
        this.threePlusVisitCount = threePlusVisitCount;
    }
}
