package saneforce.sanclm.activity.tourPlan;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class ModelClass implements Serializable {

  private String date = "";

  private Boolean onEdit = false;

  private ArrayList<ModelClass.SessionList> sessionList;

  public ModelClass () {
  }

  public ModelClass (String date, Boolean onEdit, ArrayList<ModelClass.SessionList> sessionList) {
    this.date = date;
    this.onEdit = onEdit;
    this.sessionList = sessionList;
  }

  public String getDate() {
    return this.date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Boolean getOnEdit() {
    return this.onEdit;
  }

  public void setOnEdit(Boolean onEdit) {
    this.onEdit = onEdit;
  }

  public ArrayList<ModelClass.SessionList> getSessionList() {
    return this.sessionList;
  }

  public void setSessionList(ArrayList<ModelClass.SessionList> sessionList) {
    this.sessionList = sessionList;
  }


  public static class SessionList implements Serializable {
    private String layoutVisible = "";

    private Boolean isVisible = false;

    private ModelClass.SessionList.WorkType workType;

    private ModelClass.SessionList.SubClass HQ;

    private List<ModelClass.SessionList.SubClass> cluster;

    private List<ModelClass.SessionList.SubClass> JC;

    private List<ModelClass.SessionList.SubClass> listedDr;

    private List<ModelClass.SessionList.SubClass> chemist;

    private List<ModelClass.SessionList.SubClass> stockiest;

    private List<ModelClass.SessionList.SubClass> unListedDr;

    private List<ModelClass.SessionList.SubClass> Cip;

    private List<ModelClass.SessionList.SubClass> hospital;

    public SessionList () {
    }

    public SessionList (String layoutVisible, Boolean isVisible, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass HQ, List<ModelClass.SessionList.SubClass> subClass, List<ModelClass.SessionList.SubClass> JC, List<ModelClass.SessionList.SubClass> listedDr, List<ModelClass.SessionList.SubClass> chemist, List<ModelClass.SessionList.SubClass> stockiest, List<ModelClass.SessionList.SubClass> unListedDr, List<ModelClass.SessionList.SubClass> cip, List<ModelClass.SessionList.SubClass> hospital) {
      this.layoutVisible = layoutVisible;
      this.isVisible = isVisible;
      this.workType = workType;
      this.HQ = HQ;
      this.cluster = subClass;
      this.JC = JC;
      this.listedDr = listedDr;
      this.chemist = chemist;
      this.stockiest = stockiest;
      this.unListedDr = unListedDr;
      Cip = cip;
      this.hospital = hospital;
    }


    public String getLayoutVisible () {
      return layoutVisible;
    }

    public void setLayoutVisible (String layoutVisible) {
      this.layoutVisible = layoutVisible;
    }

    public Boolean getVisible () {
      return isVisible;
    }

    public void setVisible (Boolean visible) {
      isVisible = visible;
    }

    public ModelClass.SessionList.WorkType getWorkType () {
      return workType;
    }

    public void setWorkType (ModelClass.SessionList.WorkType workType) {
      this.workType = workType;
    }

    public ModelClass.SessionList.SubClass getHQ () {
      return HQ;
    }

    public void setHQ (ModelClass.SessionList.SubClass HQ) {
      this.HQ = HQ;
    }

    public List<ModelClass.SessionList.SubClass> getCluster () {
      return cluster;
    }

    public void setCluster (List<ModelClass.SessionList.SubClass> cluster) {
      this.cluster = cluster;
    }

    public List<ModelClass.SessionList.SubClass> getJC () {
      return JC;
    }

    public void setJC (List<ModelClass.SessionList.SubClass> JC) {
      this.JC = JC;
    }

    public List<ModelClass.SessionList.SubClass> getListedDr () {
      return listedDr;
    }

    public void setListedDr (List<ModelClass.SessionList.SubClass> listedDr) {
      this.listedDr = listedDr;
    }

    public List<ModelClass.SessionList.SubClass> getChemist () {
      return chemist;
    }

    public void setChemist (List<ModelClass.SessionList.SubClass> chemist) {
      this.chemist = chemist;
    }

    public List<ModelClass.SessionList.SubClass> getStockiest () {
      return stockiest;
    }

    public void setStockiest (List<ModelClass.SessionList.SubClass> stockiest) {
      this.stockiest = stockiest;
    }

    public List<ModelClass.SessionList.SubClass> getUnListedDr () {
      return unListedDr;
    }

    public void setUnListedDr (List<ModelClass.SessionList.SubClass> unListedDr) {
      this.unListedDr = unListedDr;
    }

    public List<ModelClass.SessionList.SubClass> getCip () {
      return Cip;
    }

    public void setCip (List<ModelClass.SessionList.SubClass> cip) {
      Cip = cip;
    }

    public List<ModelClass.SessionList.SubClass> getHospital () {
      return hospital;
    }

    public void setHospital (List<ModelClass.SessionList.SubClass> hospital) {
      this.hospital = hospital;
    }



    public static class SubClass implements Serializable {
      private String name = "";

      private String Code = "";

      public SubClass (String name, String code) {
        this.name = name;
        Code = code;
      }

      public String getName() {
        return this.name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getCode() {
        return this.Code;
      }

      public void setCode(String Code) {
        this.Code = Code;
      }
    }

    public static class WorkType implements Serializable {
      private String FWFlg = "";

      private String name = "";

      private String TerrSlFlg = "";

      private String Code = "";

      public WorkType (String FWFlg, String name, String terrSlFlg, String code) {
        this.FWFlg = FWFlg;
        this.name = name;
        TerrSlFlg = terrSlFlg;
        Code = code;
      }

      public String getFWFlg() {
        return this.FWFlg;
      }

      public void setFWFlg(String FWFlg) {
        this.FWFlg = FWFlg;
      }

      public String getName() {
        return this.name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getTerrSlFlg() {
        return this.TerrSlFlg;
      }

      public void setTerrSlFlg(String TerrSlFlg) {
        this.TerrSlFlg = TerrSlFlg;
      }

      public String getCode() {
        return this.Code;
      }

      public void setCode(String Code) {
        this.Code = Code;
      }

    }

  }

}
