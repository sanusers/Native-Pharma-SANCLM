package saneforce.sanzen.activity.reports.dayReport.model;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;

public class DayReportModel implements Serializable {
  private String Udr="";

  private String intime="";

  private String Drs="";

  private String inaddress="";

  private String HalfDay_FW_Type="";

  private String outtime="";

  private String Chm="";

  private String Desig_Code="";

  private String SF_Code="";

  private String Stk="";

  private String Cip="";

  private String Adate="";

  private String Hos="";

  private String SF_Name="";

  private String Rmdr="";

  private String rptdate="";

  private String wtype="";

  private String FWFlg="";

  private Activity_Date Activity_Date;

  private String outaddress="";

  private String ACode="";

  private String remarks="";

  private String TerrWrk="";

  private int Typ = -1;
  private String Confirmed = "";

  public DayReportModel(String udr, String intime, String drs, String inaddress, String halfDay_FW_Type, String outtime, String chm, String desig_Code, String SF_Code, String stk, String cip, String adate, String hos, String SF_Name, String rmdr, String rptdate, String wtype, String FWFlg, DayReportModel.Activity_Date activity_Date, String outaddress, String ACode, String remarks, String terrWrk, int typ, String confirmed) {
    Udr = udr;
    this.intime = intime;
    Drs = drs;
    this.inaddress = inaddress;
    HalfDay_FW_Type = halfDay_FW_Type;
    this.outtime = outtime;
    Chm = chm;
    Desig_Code = desig_Code;
    this.SF_Code = SF_Code;
    Stk = stk;
    Cip = cip;
    Adate = adate;
    Hos = hos;
    this.SF_Name = SF_Name;
    Rmdr = rmdr;
    this.rptdate = rptdate;
    this.wtype = wtype;
    this.FWFlg = FWFlg;
    Activity_Date = activity_Date;
    this.outaddress = outaddress;
    this.ACode = ACode;
    this.remarks = remarks;
    TerrWrk = terrWrk;
    Typ = typ;
    Confirmed = confirmed;
  }

  public String getConfirmed() {
    return Confirmed;
  }

  public void setConfirmed(String confirmed) {
    Confirmed = confirmed;
  }

  public String getUdr() {
    return this.Udr;
  }

  public void setUdr(String Udr) {
    this.Udr = Udr;
  }

  public String getIntime() {
    return this.intime;
  }

  public void setIntime(String intime) {
    this.intime = intime;
  }

  public String getDrs() {
    return this.Drs;
  }

  public void setDrs(String Drs) {
    this.Drs = Drs;
  }

  public String getInaddress() {
    return this.inaddress;
  }

  public void setInaddress(String inaddress) {
    this.inaddress = inaddress;
  }

  public String getHalfDay_FW_Type() {
    return this.HalfDay_FW_Type;
  }

  public void setHalfDay_FW_Type(String HalfDay_FW_Type) {
    this.HalfDay_FW_Type = HalfDay_FW_Type;
  }

  public String getOuttime() {
    return this.outtime;
  }

  public void setOuttime(String outtime) {
    this.outtime = outtime;
  }

  public String getChm() {
    return this.Chm;
  }

  public void setChm(String Chm) {
    this.Chm = Chm;
  }

  public String getDesig_Code() {
    return this.Desig_Code;
  }

  public void setDesig_Code(String Desig_Code) {
    this.Desig_Code = Desig_Code;
  }

  public String getSF_Code() {
    return this.SF_Code;
  }

  public void setSF_Code(String SF_Code) {
    this.SF_Code = SF_Code;
  }

  public String getStk() {
    return this.Stk;
  }

  public void setStk(String Stk) {
    this.Stk = Stk;
  }

  public String getCip() {
    return this.Cip;
  }

  public void setCip(String Cip) {
    this.Cip = Cip;
  }

  public String getAdate() {
    return this.Adate;
  }

  public void setAdate(String Adate) {
    this.Adate = Adate;
  }

  public String getHos() {
    return this.Hos;
  }

  public void setHos(String Hos) {
    this.Hos = Hos;
  }

  public String getSF_Name() {
    return this.SF_Name;
  }

  public void setSF_Name(String SF_Name) {
    this.SF_Name = SF_Name;
  }

  public String getRmdr() {
    return this.Rmdr;
  }

  public void setRmdr(String Rmdr) {
    this.Rmdr = Rmdr;
  }

  public String getRptdate() {
    return this.rptdate;
  }

  public void setRptdate(String rptdate) {
    this.rptdate = rptdate;
  }

  public String getWtype() {
    return this.wtype;
  }

  public void setWtype(String wtype) {
    this.wtype = wtype;
  }

  public String getFWFlg() {
    return this.FWFlg;
  }

  public void setFWFlg(String FWFlg) {
    this.FWFlg = FWFlg;
  }

  public Activity_Date getActivity_Date() {
    return this.Activity_Date;
  }

  public void setActivity_Date(Activity_Date Activity_Date) {
    this.Activity_Date = Activity_Date;
  }

  public String getOutaddress() {
    return this.outaddress;
  }

  public void setOutaddress(String outaddress) {
    this.outaddress = outaddress;
  }

  public String getACode() {
    return this.ACode;
  }

  public void setACode(String ACode) {
    this.ACode = ACode;
  }

  public String getRemarks() {
    return this.remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getTerrWrk() {
    return this.TerrWrk;
  }

  public void setTerrWrk(String TerrWrk) {
    this.TerrWrk = TerrWrk;
  }

  public int getTyp() {
    return Typ;
  }

  public void setTyp(int typ) {
    Typ = typ;
  }

  public static class Activity_Date implements Serializable {
    private String date="";

    private String timezone="";

    private Integer timezone_type;

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

    public Integer getTimezone_type() {
      return this.timezone_type;
    }

    public void setTimezone_type(Integer timezone_type) {
      this.timezone_type = timezone_type;
    }
  }
}
