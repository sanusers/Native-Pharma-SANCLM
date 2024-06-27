package saneforce.sanzen.activity.myresource.myresourcemodel;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class DateSyncModel {
    private String sfCode;
    private String flg;
    private String tbname;
    private String reason;
    private String date;
    public DateSyncModel(String sfCode, String flg, String tbname, String reason, String date) {
        this.sfCode = sfCode;
        this.flg = flg;
        this.tbname = tbname;
        this.reason = reason;
        this.date = date;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSfCode() {
        return sfCode;
    }

    public void setSfCode(String sfCode) {
        this.sfCode = sfCode;
    }

    public String getFlg() {
        return flg;
    }

    public void setFlg(String flg) {
        this.flg = flg;
    }

    public String getTbname() {
        return tbname;
    }

    public void setTbname(String tbname) {
        this.tbname = tbname;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}

