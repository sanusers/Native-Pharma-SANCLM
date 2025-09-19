package saneforce.sanzen.activity.reports.missedReport;

public class MissedReportItem {
    private String name;
    private String cluster;
    private String totalDoctors;
    private String visited;
    private String missed;
    private String sfCode;

    public MissedReportItem(String name, String cluster, String totalDoctors, String visited, String missed , String sfCode) {
        this.name = name;
        this.cluster = cluster;
        this.totalDoctors = totalDoctors;
        this.visited = visited;
        this.missed = missed;
        this.sfCode = sfCode;
    }

    public String getName() {
        return name;
    }

    public String getCluster() {
        return cluster;
    }

    public String getTotalDoctors() {
        return totalDoctors;
    }

    public String getVisited() {
        return visited;
    }

    public String getMissed() {
        return missed;
    }

    public String getSfCode(){
        return sfCode;
    }
}
