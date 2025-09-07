package saneforce.sanzen.activity.reports.visitMonitor.model;

public class UnlistedStatsModel {

    public String totalUnlisted;
    public String visitedUnlisted;
    public String missedUnlisted;
    public String fwDays;
    public String callAvg;
    public String coverage;

    public UnlistedStatsModel(String totalUnlisted, String visitedUnlisted, String missedUnlisted, String fwDays, String callAvg, String coverage) {
        this.totalUnlisted = totalUnlisted;
        this.visitedUnlisted = visitedUnlisted;
        this.missedUnlisted = missedUnlisted;
        this.fwDays = fwDays;
        this.callAvg = callAvg;
        this.coverage = coverage;
    }

    public String getTotalUnlisted() {
        return totalUnlisted;
    }

    public void setTotalUnlisted(String totalUnlisted) {
        this.totalUnlisted = totalUnlisted;
    }

    public String getVisitedUnlisted() {
        return visitedUnlisted;
    }

    public void setVisitedUnlisted(String visitedUnlisted) {
        this.visitedUnlisted = visitedUnlisted;
    }

    public String getMissedUnlisted() {
        return missedUnlisted;
    }

    public void setMissedUnlisted(String missedUnlisted) {
        this.missedUnlisted = missedUnlisted;
    }

    public String getFwDays() {
        return fwDays;
    }

    public void setFwDays(String fwDays) {
        this.fwDays = fwDays;
    }

    public String getCallAvg() {
        return callAvg;
    }

    public void setCallAvg(String callAvg) {
        this.callAvg = callAvg;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }
}
