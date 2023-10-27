package saneforce.sanclm.activity.leave;

public class Leave_modelclass {

    String Dates, Leaveplan;

    String Ltype,Eligable,Takenleave,Avaolable,l_type;
//      "Leave_Type_Code": "CL",
//		"Elig": "0",
//		"Taken": "0",
//		"Avail": "0",

    public String getLtype() {
        return Ltype;
    }

    public void setLtype(String ltype) {
        Ltype = ltype;
    }

    public String getEligable() {
        return Eligable;
    }

    public void setEligable(String eligable) {
        Eligable = eligable;
    }

    public String getTakenleave() {
        return Takenleave;
    }

    public void setTakenleave(String takenleave) {
        Takenleave = takenleave;
    }

    public String getAvaolable() {
        return Avaolable;
    }

    public void setAvaolable(String avaolable) {
        Avaolable = avaolable;
    }

    public String getL_type() {
        return l_type;
    }

    public void setL_type(String l_type) {
        this.l_type = l_type;
    }
//		"Leave_code": "6108"



    public Leave_modelclass(String Ltype, String eligable, String takenleave, String avaolable, String l_type) {
        this.Ltype = Ltype;
        Eligable = eligable;
        Takenleave = takenleave;
        Avaolable = avaolable;
        this.l_type = l_type;
    }

    public Leave_modelclass(String dates, String leaveplan) {
        Dates = dates;
        Leaveplan = leaveplan;
    }


    public String getLeaveDates() {
        return Dates;
    }

    public void setLeaveDates(String LeaveDates) {
        Dates = LeaveDates;
    }

    public String getLeaveplan() {
        return Leaveplan;
    }

    public void setLeaveplan(String Leave_plan) {
        Leaveplan = Leave_plan;
    }



    //---------------------------------------------





}
