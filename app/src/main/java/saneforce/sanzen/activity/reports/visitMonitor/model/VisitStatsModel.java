package saneforce.sanzen.activity.reports.visitMonitor.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VisitStatsModel implements Parcelable {
    private String totalCustomers;
    private String visitedCustomers;
    private String missedCustomers;
    private String fwDays;
    private String callAvg;
    private String coverage;
    private String monthName;

    private int oneVisitCount;
    private int twoVisitCount;
    private int threeVisitCount;
    private int threePlusVisitCount;

    // Full constructor
    public VisitStatsModel(String totalCustomers, String visitedCustomers, String missedCustomers,
                           String fwDays, String callAvg, String coverage,
                           int oneVisitCount, int twoVisitCount, int threeVisitCount, int threePlusVisitCount) {
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

    // Constructor without visit counts
    public VisitStatsModel(String totalCustomers, String visitedCustomers, String missedCustomers,
                           String fwDays, String callAvg, String coverage) {
        this(totalCustomers, visitedCustomers, missedCustomers, fwDays, callAvg, coverage,
                0, 0, 0, 0);
    }

    // Parcelable constructor
    protected VisitStatsModel(Parcel in) {
        totalCustomers = in.readString();
        visitedCustomers = in.readString();
        missedCustomers = in.readString();
        fwDays = in.readString();
        callAvg = in.readString();
        coverage = in.readString();
        monthName = in.readString();
        oneVisitCount = in.readInt();
        twoVisitCount = in.readInt();
        threeVisitCount = in.readInt();
        threePlusVisitCount = in.readInt();
    }

    public static final Creator<VisitStatsModel> CREATOR = new Creator<VisitStatsModel>() {
        @Override
        public VisitStatsModel createFromParcel(Parcel in) {
            return new VisitStatsModel(in);
        }

        @Override
        public VisitStatsModel[] newArray(int size) {
            return new VisitStatsModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(totalCustomers);
        dest.writeString(visitedCustomers);
        dest.writeString(missedCustomers);
        dest.writeString(fwDays);
        dest.writeString(callAvg);
        dest.writeString(coverage);
        dest.writeString(monthName);
        dest.writeInt(oneVisitCount);
        dest.writeInt(twoVisitCount);
        dest.writeInt(threeVisitCount);
        dest.writeInt(threePlusVisitCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters
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

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
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
