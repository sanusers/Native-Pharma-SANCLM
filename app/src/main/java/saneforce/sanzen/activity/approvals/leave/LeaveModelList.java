package saneforce.sanzen.activity.approvals.leave;

public class LeaveModelList {
    String leave_id;
    String sf_code;

    public String getSf_code() {
        return sf_code;
    }

    public void setSf_code(String sf_code) {
        this.sf_code = sf_code;
    }

    String name;
    String from_date;
    String to_date;
    String reason;
    String addr;
    String leave_type;
    String available_leave;
    String no_of_days;

    public LeaveModelList(String leave_id,String sf_code,String name, String from_date, String to_date, String reason, String addr, String leave_type, String available_leave, String no_of_days) {
        this.leave_id = leave_id;
        this.sf_code = sf_code;
        this.name = name;
        this.from_date = from_date;
        this.to_date = to_date;
        this.reason = reason;
        this.addr = addr;
        this.leave_type = leave_type;
        this.available_leave = available_leave;
        this.no_of_days = no_of_days;
    }

    public String getLeave_id() {
        return leave_id;
    }

    public void setLeave_id(String leave_id) {
        this.leave_id = leave_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public String getAvailable_leave() {
        return available_leave;
    }

    public void setAvailable_leave(String available_leave) {
        this.available_leave = available_leave;
    }

    public String getNo_of_days() {
        return no_of_days;
    }

    public void setNo_of_days(String no_of_days) {
        this.no_of_days = no_of_days;
    }
}
