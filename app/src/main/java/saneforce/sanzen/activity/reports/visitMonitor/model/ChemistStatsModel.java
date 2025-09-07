package saneforce.sanzen.activity.reports.visitMonitor.model;

public class ChemistStatsModel {

    public String totalChemists;
    public String visitedChemists;
    public String missedChemists;
    public String fwDays;
    public String callAvg;
    public String coverage;

    public ChemistStatsModel(String totalChemists, String visitedChemists, String missedChemists, String fwDays, String callAvg, String coverage) {
        this.totalChemists = totalChemists;
        this.visitedChemists = visitedChemists;
        this.missedChemists = missedChemists;
        this.fwDays = fwDays;
        this.callAvg = callAvg;
        this.coverage = coverage;
    }

    public String getTotalChemists() {
        return totalChemists;
    }

    public void setTotalChemists(String totalChemists) {
        this.totalChemists = totalChemists;
    }

    public String getVisitedChemists() {
        return visitedChemists;
    }

    public void setVisitedChemists(String visitedChemists) {
        this.visitedChemists = visitedChemists;
    }

    public String getMissedChemists() {
        return missedChemists;
    }

    public void setMissedChemists(String missedChemists) {
        this.missedChemists = missedChemists;
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
