package saneforce.sanzen.activity.leave;

import org.json.JSONObject;

public class LeaveStatusModelClass {
    private final String leaveTypeCode, eligible, taken, available, leaveCode, totalApplied;
    // OLD constructor (5 args) — used for entitlementEnabled = "0"

    public LeaveStatusModelClass(String leaveTypeCode, String eligible, String taken, String available, String leaveCode) {
        this.leaveTypeCode = leaveTypeCode;
        this.eligible = eligible;
        this.taken = taken;
        this.available = available;
        this.leaveCode = leaveCode;
        this.totalApplied="0";
    }

    //entitlementDisabled = "1"
    public LeaveStatusModelClass(String leaveTypeCode,
                                 String eligible,
                                 String taken,
                                 String available,
                                 String leaveCode,
                                 String totalApplied) {

        this.leaveTypeCode = leaveTypeCode;
        this.eligible = eligible;
        this.taken = taken;
        this.available = available;
        this.leaveCode = leaveCode;
        this.totalApplied = totalApplied;
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
    public String getTotalApplied() {
        return totalApplied;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Leave_Type_Code", leaveTypeCode);
            obj.put("Elig", eligible);
            obj.put("Taken", taken);
            obj.put("Avail", available);
            obj.put("Leave_code", leaveCode);
            obj.put("total_Applied", totalApplied);
        } catch (Exception ignored) {}

        return obj;
    }

}
