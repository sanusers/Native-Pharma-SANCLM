package saneforce.santrip.activity.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DCRLastVisitDetails {
    @SerializedName("CustCode")
    @Expose
    private String custCode;
    @SerializedName("Vst_Date")
    @Expose
    private VstDate vstDate;
    @SerializedName("Prod_Samp")
    @Expose
    private String prodSamp;
    @SerializedName("Prod_Det")
    @Expose
    private String prodDet;
    @SerializedName("Inputs")
    @Expose
    private String inputs;
    @SerializedName("FeedbkCd")
    @Expose
    private String feedbkCd;
    @SerializedName("Feedbk")
    @Expose
    private String feedbk;
    @SerializedName("Remks")
    @Expose
    private String remks;
    @SerializedName("AMSLNo")
    @Expose
    private String aMSLNo;

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public VstDate getVstDate() {
        return vstDate;
    }

    public void setVstDate(VstDate vstDate) {
        this.vstDate = vstDate;
    }

    public String getProdSamp() {
        return prodSamp;
    }

    public void setProdSamp(String prodSamp) {
        this.prodSamp = prodSamp;
    }

    public String getProdDet() {
        return prodDet;
    }

    public void setProdDet(String prodDet) {
        this.prodDet = prodDet;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public String getFeedbkCd() {
        return feedbkCd;
    }

    public void setFeedbkCd(String feedbkCd) {
        this.feedbkCd = feedbkCd;
    }

    public String getFeedbk() {
        return feedbk;
    }

    public void setFeedbk(String feedbk) {
        this.feedbk = feedbk;
    }

    public String getRemks() {
        return remks;
    }

    public void setRemks(String remks) {
        this.remks = remks;
    }

    public String getAMSLNo() {
        return aMSLNo;
    }

    public void setAMSLNo(String aMSLNo) {
        this.aMSLNo = aMSLNo;
    }

    public class VstDate {

        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("timezone_type")
        @Expose
        private Integer timezoneType;
        @SerializedName("timezone")
        @Expose
        private String timezone;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getTimezoneType() {
            return timezoneType;
        }

        public void setTimezoneType(Integer timezoneType) {
            this.timezoneType = timezoneType;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
    }
}
