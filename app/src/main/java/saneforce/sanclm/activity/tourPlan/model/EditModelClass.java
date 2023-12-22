package saneforce.sanclm.activity.tourPlan.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.String;

public class EditModelClass implements Serializable {

  private String Code;

  @SerializedName (value="name",alternate = "Name")
  private String Name;
  private String ETabs;
  private String FWFlg;
  private String SF_Code;
  private String TerrSlFlg;
  private String TP_DCR;
  private String Town_Code = "";
  boolean isChecked = false;


  public EditModelClass(String code, String name, String ETabs, String FWFlg, String SF_Code, String terrSlFlg, String TP_DCR) {
    Code = code;
    Name = name;
    this.ETabs = ETabs;
    this.FWFlg = FWFlg;
    this.SF_Code = SF_Code;
    TerrSlFlg = terrSlFlg;
    this.TP_DCR = TP_DCR;
  }

  public EditModelClass(String code, String name, boolean isChecked) {
    Code = code;
    Name = name;
    this.isChecked = isChecked;
  }

  public EditModelClass(String code, String name, String townCode, boolean isChecked) {
    Code = code;
    Name = name;
    Town_Code = townCode;
    this.isChecked = isChecked;
  }

  public String getName() {
    return this.Name;
  }

  public void setName(String Name) {
    this.Name = Name;
  }

  public String getCode() {
    return this.Code;
  }

  public void setCode(String Code) {
    this.Code = Code;
  }

  public String getETabs() {
    return this.ETabs;
  }

  public void setETabs(String ETabs) {
    this.ETabs = ETabs;
  }

  public String getFWFlg() {
    return this.FWFlg;
  }

  public void setFWFlg(String FWFlg) {
    this.FWFlg = FWFlg;
  }

  public String getSF_Code() {
    return this.SF_Code;
  }

  public void setSF_Code(String SF_Code) {
    this.SF_Code = SF_Code;
  }

  public String getTerrSlFlg() {
    return this.TerrSlFlg;
  }

  public void setTerrSlFlg(String TerrSlFlg) {
    this.TerrSlFlg = TerrSlFlg;
  }

  public String getTP_DCR() {
    return this.TP_DCR;
  }

  public void setTP_DCR(String TP_DCR) {
    this.TP_DCR = TP_DCR;
  }

  public String getTown_Code() {
    return Town_Code;
  }

  public void setTown_Code(String town_Code) {
    Town_Code = town_Code;
  }

  public boolean isChecked() {
    return isChecked;
  }

  public void setChecked(boolean checked) {
    isChecked = checked;
  }

}
