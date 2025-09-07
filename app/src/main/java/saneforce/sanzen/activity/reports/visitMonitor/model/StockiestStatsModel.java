package saneforce.sanzen.activity.reports.visitMonitor.model;

public class StockiestStatsModel {

    public String totalStockiest;
    public String visitedStockiest;
    public String missedStockiest;
    public String fwDays;
    public String callAvg;
    public String coverage;

    public StockiestStatsModel(String totalStockiest, String visitedStockiest, String missedStockiest, String fwDays, String callAvg, String coverage) {
        this.totalStockiest = totalStockiest;
        this.visitedStockiest = visitedStockiest;
        this.missedStockiest = missedStockiest;
        this.fwDays = fwDays;
        this.callAvg = callAvg;
        this.coverage = coverage;
    }

    public String getTotalStockiest() {
        return totalStockiest;
    }

    public void setTotalStockiest(String totalStockiest) {
        this.totalStockiest = totalStockiest;
    }

    public String getVisitedStockiest() {
        return visitedStockiest;
    }

    public void setVisitedStockiest(String visitedStockiest) {
        this.visitedStockiest = visitedStockiest;
    }

    public String getMissedStockiest() {
        return missedStockiest;
    }

    public void setMissedStockiest(String missedStockiest) {
        this.missedStockiest = missedStockiest;
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
