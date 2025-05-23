package saneforce.sanzen.activity.leave;

public class LeaveStatusModelClass {
    private final String leaveTypeCode, eligible, taken, available, leaveCode;

    public LeaveStatusModelClass(String leaveTypeCode, String eligible, String taken, String available, String leaveCode) {
        this.leaveTypeCode = leaveTypeCode;
        this.eligible = eligible;
        this.taken = taken;
        this.available = available;
        this.leaveCode = leaveCode;
    }

    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }

    public String getEligible() {
        return eligible;
    }

    public String getTaken() {
        return taken;
    }

    public String getAvailable() {
        return available;
    }

    public String getLeaveCode() {
        return leaveCode;
    }
}
