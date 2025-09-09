package saneforce.sanzen.activity.reports.visitMonitor.model;

public class VisitStatsModel {
    public String totalCustomers;
    public String visitedCustomers;
    public String missedCustomers;
    public String fwDays;
    public String callAvg;
    public String coverage;
    public String monthName;

    private int oneVisitCount;
    private int twoVisitCount;
    private int threeVisitCount;
    private int threePlusVisitCount;


    public VisitStatsModel(String totalCustomers, String visitedCustomers, String missedCustomers, String fwDays, String callAvg, String coverage, int oneVisitCount, int twoVisitCount, int threeVisitCount, int threePlusVisitCount) {
        this.totalCustomers = totalCustomers;
        this.visitedCustomers = visitedCustomers;
        this.missedCustomers = missedCustomers;
        this.fwDays = fwDays;
        this.callAvg = callAvg;
        this.coverage = coverage;
        this.oneVisitCount = oneVisitCount;
        this.twoVisitCount = twoVisitCount;
        this.threeVisitCount = threeVisitCount;
        this.threePlusVisitCount = threePlusVisitCount;
    }

    public VisitStatsModel(String totalCustomers, String visitedCustomers, String missedCustomers, String fwDays, String callAvg, String coverage,String monthName) {
        this.totalCustomers = totalCustomers;
        this.visitedCustomers = visitedCustomers;
        this.missedCustomers = missedCustomers;
        this.fwDays = fwDays;
        this.callAvg = callAvg;
        this.coverage = coverage;
        this.monthName = monthName;
    }

    public String getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(String totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public String getVisitedCustomers() {
        return visitedCustomers;
    }

    public void setVisitedCustomers(String visitedCustomers) {
        this.visitedCustomers = visitedCustomers;
    }

    public String getMissedCustomers() {
        return missedCustomers;
    }

    public void setMissedCustomers(String missedCustomers) {
        this.missedCustomers = missedCustomers;
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

    public int getOneVisitCount() {
        return oneVisitCount;
    }

    public void setOneVisitCount(int oneVisitCount) {
        this.oneVisitCount = oneVisitCount;
    }

    public int getTwoVisitCount() {
        return twoVisitCount;
    }

    public void setTwoVisitCount(int twoVisitCount) {
        this.twoVisitCount = twoVisitCount;
    }

    public int getThreeVisitCount() {
        return threeVisitCount;
    }

    public void setThreeVisitCount(int threeVisitCount) {
        this.threeVisitCount = threeVisitCount;
    }

    public int getThreePlusVisitCount() {
        return threePlusVisitCount;
    }

    public void setThreePlusVisitCount(int threePlusVisitCount) {
        this.threePlusVisitCount = threePlusVisitCount;
    }
}
