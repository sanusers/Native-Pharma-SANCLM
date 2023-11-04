package saneforce.sanclm.activity.tourPlan;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class ObjectModelClass implements Serializable {
  private String SFCode;
  private String SFName;
  private String Div;
  private Integer tpMonth;
  private String tpMonthName;
  private Integer tpYear;
  private ArrayList<TPData> tpData;

  public ObjectModelClass () {
  }

  public ObjectModelClass (String SFCode, String SFName, String div, Integer tpMonth, String tpMonthName, Integer tpYear, ArrayList<TPData> tpData) {
    this.SFCode = SFCode;
    this.SFName = SFName;
    Div = div;
    this.tpMonth = tpMonth;
    this.tpMonthName = tpMonthName;
    this.tpYear = tpYear;
    this.tpData = tpData;
  }

  public String getDiv() {
    return this.Div;
  }

  public void setDiv(String Div) {
    this.Div = Div;
  }

  public ArrayList<TPData> getTPData() {
    return this.tpData;
  }

  public void setTPData(ArrayList<TPData> tpData) {
    this.tpData = tpData;
  }

  public Integer getTpMonth() {
    return this.tpMonth;
  }

  public void setTpMonth(Integer tpMonth) {
    this.tpMonth = tpMonth;
  }

  public String getTpMonthName() {
    return this.tpMonthName;
  }

  public void setTpMonthName(String tpMonthName) {
    this.tpMonthName = tpMonthName;
  }

  public String getSFCode() {
    return this.SFCode;
  }

  public void setSFCode(String SFCode) {
    this.SFCode = SFCode;
  }

  public String getSFName() {
    return this.SFName;
  }

  public void setSFName(String SFName) {
    this.SFName = SFName;
  }

  public Integer getTpYear() {
    return this.tpYear;
  }

  public void setTpYear(Integer tpYear) {
    this.tpYear = tpYear;
  }

  public static class TPData implements Serializable {
    private String date;
    private String dayNo;
    private String day;
    private String changeStatus;
    private String rejectionReason;
    private String entryMode;
    private SubmittedTime submittedTime;
    private List<Sessions> sessions;


    public TPData () {
    }

    public TPData (String date, String dayNo, String day, String changeStatus, String rejectionReason, String entryMode, SubmittedTime submittedTime, List<Sessions> sessions) {
      this.date = date;
      this.dayNo = dayNo;
      this.day = day;
      this.changeStatus = changeStatus;
      this.rejectionReason = rejectionReason;
      this.entryMode = entryMode;
      this.submittedTime = submittedTime;
      this.sessions = sessions;
    }

    public String getDate () {
      return date;
    }

    public void setDate (String date) {
      this.date = date;
    }

    public String getDayNo () {
      return dayNo;
    }

    public void setDayNo (String dayNo) {
      this.dayNo = dayNo;
    }

    public String getDay () {
      return day;
    }

    public void setDay (String day) {
      this.day = day;
    }

    public String getChangeStatus () {
      return changeStatus;
    }

    public void setChangeStatus (String changeStatus) {
      this.changeStatus = changeStatus;
    }

    public String getRejectionReason () {
      return rejectionReason;
    }

    public void setRejectionReason (String rejectionReason) {
      this.rejectionReason = rejectionReason;
    }

    public String getEntryMode () {
      return entryMode;
    }

    public void setEntryMode (String entryMode) {
      this.entryMode = entryMode;
    }

    public SubmittedTime getSubmittedTime () {
      return submittedTime;
    }

    public void setSubmittedTime (SubmittedTime submittedTime) {
      this.submittedTime = submittedTime;
    }

    public List<Sessions> getSessions () {
      return sessions;
    }

    public void setSessions (List<Sessions> sessions) {
      this.sessions = sessions;
    }

    public static class Sessions implements Serializable {
      private String WTCode;
      private String WTName;
      private String FWFlg;
      private String HQCodes;
      private String HQNames;
      private String clusterCode;
      private String clusterName;
      private String jwCode;
      private String jwName;
      private String drCode;
      private String drName;
      private String chemCode;
      private String chemName;
      private String stockistCode;
      private String stockistName;
      private String unListedDrCode;
      private String unListedDrName;
      private String cipCode;
      private String cipName;
      private String hospCode;
      private String hospName;
      private String remarks;

      public Sessions () {
      }

      public Sessions (String WTCode, String WTName, String FWFlg, String HQCodes, String HQNames, String clusterCode, String clusterName, String jwCode,
                       String jwName, String drCode, String drName, String chemCode, String chemName, String stockistCode, String stockistName, String unListedDrCode,
                       String unListedDrName, String cipCode, String cipName, String hospCode, String hospName, String remarks) {
        this.WTCode = WTCode;
        this.WTName = WTName;
        this.FWFlg = FWFlg;
        this.HQCodes = HQCodes;
        this.HQNames = HQNames;
        this.clusterCode = clusterCode;
        this.clusterName = clusterName;
        this.jwCode = jwCode;
        this.jwName = jwName;
        this.drCode = drCode;
        this.drName = drName;
        this.chemCode = chemCode;
        this.chemName = chemName;
        this.stockistCode = stockistCode;
        this.stockistName = stockistName;
        this.unListedDrCode = unListedDrCode;
        this.unListedDrName = unListedDrName;
        this.cipCode = cipCode;
        this.cipName = cipName;
        this.hospCode = hospCode;
        this.hospName = hospName;
        this.remarks = remarks;
      }

      public String getWTCode () {
        return WTCode;
      }

      public void setWTCode (String WTCode) {
        this.WTCode = WTCode;
      }

      public String getWTName () {
        return WTName;
      }

      public void setWTName (String WTName) {
        this.WTName = WTName;
      }

      public String getFWFlg () {
        return FWFlg;
      }

      public void setFWFlg (String FWFlg) {
        this.FWFlg = FWFlg;
      }

      public String getHQCodes () {
        return HQCodes;
      }

      public void setHQCodes (String HQCodes) {
        this.HQCodes = HQCodes;
      }

      public String getHQNames () {
        return HQNames;
      }

      public void setHQNames (String HQNames) {
        this.HQNames = HQNames;
      }

      public String getClusterCode () {
        return clusterCode;
      }

      public void setClusterCode (String clusterCode) {
        this.clusterCode = clusterCode;
      }

      public String getClusterName () {
        return clusterName;
      }

      public void setClusterName (String clusterName) {
        this.clusterName = clusterName;
      }

      public String getJwCode () {
        return jwCode;
      }

      public void setJwCode (String jwCode) {
        this.jwCode = jwCode;
      }

      public String getJwName () {
        return jwName;
      }

      public void setJwName (String jwName) {
        this.jwName = jwName;
      }

      public String getDrCode () {
        return drCode;
      }

      public void setDrCode (String drCode) {
        this.drCode = drCode;
      }

      public String getDrName () {
        return drName;
      }

      public void setDrName (String drName) {
        this.drName = drName;
      }

      public String getChemCode () {
        return chemCode;
      }

      public void setChemCode (String chemCode) {
        this.chemCode = chemCode;
      }

      public String getChemName () {
        return chemName;
      }

      public void setChemName (String chemName) {
        this.chemName = chemName;
      }

      public String getStockistCode () {
        return stockistCode;
      }

      public void setStockistCode (String stockistCode) {
        this.stockistCode = stockistCode;
      }

      public String getStockistName () {
        return stockistName;
      }

      public void setStockistName (String stockistName) {
        this.stockistName = stockistName;
      }

      public String getUnListedDrCode () {
        return unListedDrCode;
      }

      public void setUnListedDrCode (String unListedDrCode) {
        this.unListedDrCode = unListedDrCode;
      }

      public String getUnListedDrName () {
        return unListedDrName;
      }

      public void setUnListedDrName (String unListedDrName) {
        this.unListedDrName = unListedDrName;
      }

      public String getCipCode () {
        return cipCode;
      }

      public void setCipCode (String cipCode) {
        this.cipCode = cipCode;
      }

      public String getCipName () {
        return cipName;
      }

      public void setCipName (String cipName) {
        this.cipName = cipName;
      }

      public String getHospCode () {
        return hospCode;
      }

      public void setHospCode (String hospCode) {
        this.hospCode = hospCode;
      }

      public String getHospName () {
        return hospName;
      }

      public void setHospName (String hospName) {
        this.hospName = hospName;
      }

      public String getRemarks () {
        return remarks;
      }

      public void setRemarks (String remarks) {
        this.remarks = remarks;
      }
    }

    public static class SubmittedTime implements Serializable {
      private String date;

      private String timezone;

      private String timezone_type;

      public SubmittedTime () {
      }

      public SubmittedTime (String date, String timezone, String timezone_type) {
        this.date = date;
        this.timezone = timezone;
        this.timezone_type = timezone_type;
      }

      public String getDate() {
        return this.date;
      }

      public void setDate(String date) {
        this.date = date;
      }

      public String getTimezone() {
        return this.timezone;
      }

      public void setTimezone(String timezone) {
        this.timezone = timezone;
      }

      public String getTimezone_type() {
        return this.timezone_type;
      }

      public void setTimezone_type(String timezone_type) {
        this.timezone_type = timezone_type;
      }
    }
  }
}
