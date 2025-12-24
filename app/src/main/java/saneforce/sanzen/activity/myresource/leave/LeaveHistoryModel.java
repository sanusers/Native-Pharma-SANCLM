//package saneforce.sanzen.activity.myresource.leave;
//
//public class LeaveHistoryModel {
//
//    public String fromDate;
//    public String toDate;
//    public String type;
//    public String appliedOn;
//    public String reason;
//    public String rejectedReason;
//    public String status;
//    public int days;
//
//    public LeaveHistoryModel(String fromDate, String toDate, String type,
//                             String appliedOn, String reason,
//                             String rejectedReason, String status, int days) {
//        this.fromDate = fromDate;
//        this.toDate = toDate;
//        this.type = type;
//        this.appliedOn = appliedOn;
//        this.reason = reason;
//        this.rejectedReason = rejectedReason;
//        this.status = status;
//        this.days = days;
//    }
//}
package saneforce.sanzen.activity.myresource.leave;
public class LeaveHistoryModel {
    private String fromDate;
    private String toDate;
    private String leaveType;
    private String createdDate;
    public String status;
    public String reason;
    public String rejectedReason;
    private String days;


    public LeaveHistoryModel(String fromDate, String toDate, String leaveType, String createdDate, String days, String status, String reason, String rejectedReason) {
        this.leaveType = leaveType;
        this.createdDate = createdDate;
        this.days = days;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status= status;
        this.reason=reason;
        this.rejectedReason=rejectedReason;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }
    public String getStatus(){
        return status;
    }
    public String getDays(){
        return days;
    }
    public String getReason(){
        return reason;
    }
    public String getRejectedReason(){
        return rejectedReason;
    }
}

