package saneforce.sanclm.activity.reports.dayReport.model;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;

public class DayReportDetailModel implements Serializable {
  private String Territory="";

  private String Trans_Detail_Slno="";

  private String code="";

  private Integer pob_value;

  private String WWith="";

  private String NextVstDate="";

  private String products="";

  private String vstloc="";

  private String dcr_dt="";

  private String visitTime="";

  private String checkin="";

  private String Dcr_addr="";

  private String name="";

  private String Call_Fdback="";

  private String ModTime="";

  private String checkout="";

  private String remarks="";

  private String gifts="";

  public String getTerritory() {
    return this.Territory;
  }

  public void setTerritory(String Territory) {
    this.Territory = Territory;
  }

  public String getTrans_Detail_Slno() {
    return this.Trans_Detail_Slno;
  }

  public void setTrans_Detail_Slno(String Trans_Detail_Slno) {
    this.Trans_Detail_Slno = Trans_Detail_Slno;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Integer getPob_value() {
    return this.pob_value;
  }

  public void setPob_value(Integer pob_value) {
    this.pob_value = pob_value;
  }

  public String getWWith() {
    return this.WWith;
  }

  public void setWWith(String WWith) {
    this.WWith = WWith;
  }

  public String getNextVstDate() {
    return this.NextVstDate;
  }

  public void setNextVstDate(String NextVstDate) {
    this.NextVstDate = NextVstDate;
  }

  public String getProducts() {
    return this.products;
  }

  public void setProducts(String products) {
    this.products = products;
  }

  public String getVstloc() {
    return this.vstloc;
  }

  public void setVstloc(String vstloc) {
    this.vstloc = vstloc;
  }

  public String getDcr_dt() {
    return this.dcr_dt;
  }

  public void setDcr_dt(String dcr_dt) {
    this.dcr_dt = dcr_dt;
  }

  public String getVisitTime() {
    return this.visitTime;
  }

  public void setVisitTime(String visitTime) {
    this.visitTime = visitTime;
  }

  public String getCheckin() {
    return this.checkin;
  }

  public void setCheckin(String checkin) {
    this.checkin = checkin;
  }

  public String getDcr_addr() {
    return this.Dcr_addr;
  }

  public void setDcr_addr(String Dcr_addr) {
    this.Dcr_addr = Dcr_addr;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCall_Fdback() {
    return this.Call_Fdback;
  }

  public void setCall_Fdback(String Call_Fdback) {
    this.Call_Fdback = Call_Fdback;
  }

  public String getModTime() {
    return this.ModTime;
  }

  public void setModTime(String ModTime) {
    this.ModTime = ModTime;
  }

  public String getCheckout() {
    return this.checkout;
  }

  public void setCheckout(String checkout) {
    this.checkout = checkout;
  }

  public String getRemarks() {
    return this.remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getGifts() {
    return this.gifts;
  }

  public void setGifts(String gifts) {
    this.gifts = gifts;
  }
}
