package saneforce.sanzen.activity.tourPlan.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelClass implements Serializable {

  private String dayNo = "";
  private String date = "";
  private String day = "";
  private String month = "";
  private String year = "";
  private Boolean onEdit = false;
  private String submittedTime = "";
  private String syncStatus = ""; // 0 - Success(Data successfully sent to Remote data base), 1 - Failed(failed to send Remote data base for any reason like no internet or api called failed)
  private String STP_Code = "";
  private String STP_Name = "";
  private ArrayList<SessionList> sessionList;

  public ModelClass () {
  }

  public ModelClass (String dayNo,String date,String day,String month,String year, Boolean onEdit, ArrayList<ModelClass.SessionList> sessionList) {
    this.dayNo = dayNo;
    this.date = date;
    this.day = day;
    this.month = month;
    this.year = year;
    this.onEdit = onEdit;
    this.sessionList = sessionList;
  }


  public ModelClass (String dayNo, String date, String day, String month, String year, Boolean onEdit, ArrayList<ModelClass.SessionList> sessionList, String STP_Code, String STP_Name) {
    this.dayNo = dayNo;
    this.date = date;
    this.day = day;
    this.month = month;
    this.year = year;
    this.onEdit = onEdit;
    this.sessionList = sessionList;
    this.STP_Code = STP_Code;
    this.STP_Name = STP_Name;
  }



  public ModelClass (ModelClass modelClass) {
    this.dayNo = modelClass.getDayNo();
    this.date = modelClass.getDate();
    this.day = modelClass.getDay();
    this.month = modelClass.getMonth();
    this.year = modelClass.getYear();
    this.onEdit = modelClass.getOnEdit();
    this.STP_Name = modelClass.getSTP_Name();
    this.STP_Code = modelClass.getSTP_Code();

    this.sessionList = new ArrayList<ModelClass.SessionList>();
    for (ModelClass.SessionList session : modelClass.sessionList) {
      ModelClass.SessionList copySession = new ModelClass.SessionList(session);
      this.sessionList.add(copySession);
    }

  }


  public String getDayNo () {
    return dayNo;
  }

  public void setDayNo (String dayNo) {
    this.dayNo = dayNo;
  }

  public String getDate() {
    return this.date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getDay () {
    return day;
  }

  public void setDay (String day) {
    this.day = day;
  }

  public String getMonth () {
    return month;
  }

  public void setMonth (String month) {
    this.month = month;
  }

  public String getYear () {
    return year;
  }

  public void setYear (String year) {
    this.year = year;
  }

  public Boolean getOnEdit() {
    return this.onEdit;
  }

  public void setOnEdit(Boolean onEdit) {
    this.onEdit = onEdit;
  }

  public String getSubmittedTime () {
    return submittedTime;
  }

  public void setSubmittedTime (String submittedTime) {
    this.submittedTime = submittedTime;
  }

  public String getSyncStatus () {
    return syncStatus;
  }

  public void setSyncStatus(String syncStatus) {
    this.syncStatus = syncStatus;
  }

  public ArrayList<ModelClass.SessionList> getSessionList() {
    return this.sessionList;
  }

  public void setSessionList(ArrayList<ModelClass.SessionList> sessionList) {
    this.sessionList = sessionList;
  }

  public String getSTP_Code() {
    return STP_Code;
  }

  public String getSTP_Name() {
    return STP_Name;
  }

  public void setSTP_Code(String STP_Code) {
    this.STP_Code = STP_Code;
  }

  public void setSTP_Name(String STP_Name) {
    this.STP_Name = STP_Name;
  }

  public static class SessionList implements Serializable {
    private String layoutVisible = "";

    private Boolean isVisible = false;

    private String remarks = "";

    private ModelClass.SessionList.WorkType workType;

    private ModelClass.SessionList.SubClass HQ;

    private List<ModelClass.SessionList.SubClass> HQs = new ArrayList<>();

    private List<ModelClass.SessionList.SubClass> cluster;

    private List<ModelClass.SessionList.SubClass> JC;

    private List<ModelClass.SessionList.SubClass> listedDr;

    private List<ModelClass.SessionList.SubClass> chemist;

    private List<ModelClass.SessionList.SubClass> stockiest;

    private List<ModelClass.SessionList.SubClass> unListedDr;

    private List<ModelClass.SessionList.SubClass> Cip;

    private List<ModelClass.SessionList.SubClass> hospital;

    private ArrayList<MultiHQHeaderModelClass> clusters = new ArrayList<>();

    private ArrayList<MultiHQHeaderModelClass> JCs = new ArrayList<>();

    private ArrayList<MultiHQHeaderModelClass> listedDrs = new ArrayList<>();

    private ArrayList<MultiHQHeaderModelClass> chemists = new ArrayList<>();

    private ArrayList<MultiHQHeaderModelClass> stockiests = new ArrayList<>();

    private ArrayList<MultiHQHeaderModelClass> unListedDrs = new ArrayList<>();

    private ArrayList<MultiHQHeaderModelClass> cips = new ArrayList<>();

    private ArrayList<MultiHQHeaderModelClass> hospitals = new ArrayList<>();

    public SessionList () {
    }

    public SessionList (String layoutVisible, Boolean isVisible,String remarks, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass HQ,
                        List<ModelClass.SessionList.SubClass> subClass, List<ModelClass.SessionList.SubClass> JC, List<ModelClass.SessionList.SubClass> listedDr,
                        List<ModelClass.SessionList.SubClass> chemist, List<ModelClass.SessionList.SubClass> stockiest, List<ModelClass.SessionList.SubClass> unListedDr,
                        List<ModelClass.SessionList.SubClass> cip, List<ModelClass.SessionList.SubClass> hospital) {
      this.layoutVisible = layoutVisible;
      this.isVisible = isVisible;
      this.remarks = remarks;
      this.workType = workType;
      this.HQ = HQ;
      this.cluster = subClass;
      this.JC = JC;
      this.listedDr = listedDr;
      this.chemist = chemist;
      this.stockiest = stockiest;
      this.unListedDr = unListedDr;
      this.Cip = cip;
      this.hospital = hospital;
    }

    public SessionList (String layoutVisible, Boolean isVisible,String remarks, ModelClass.SessionList.WorkType workType, ModelClass.SessionList.SubClass HQ,
                        List<ModelClass.SessionList.SubClass> HQs, List<ModelClass.SessionList.SubClass> subClass, List<ModelClass.SessionList.SubClass> JC, List<ModelClass.SessionList.SubClass> listedDr,
                        List<ModelClass.SessionList.SubClass> chemist, List<ModelClass.SessionList.SubClass> stockiest, List<ModelClass.SessionList.SubClass> unListedDr,
                        List<ModelClass.SessionList.SubClass> cip, List<ModelClass.SessionList.SubClass> hospital,
                        ArrayList<MultiHQHeaderModelClass> clusters, ArrayList<MultiHQHeaderModelClass> JCs, ArrayList<MultiHQHeaderModelClass> listedDrs,
                        ArrayList<MultiHQHeaderModelClass> chemists, ArrayList<MultiHQHeaderModelClass> stockiests, ArrayList<MultiHQHeaderModelClass> unListedDrs,
                        ArrayList<MultiHQHeaderModelClass> cips, ArrayList<MultiHQHeaderModelClass> hospitals) {
      this.layoutVisible = layoutVisible;
      this.isVisible = isVisible;
      this.remarks = remarks;
      this.workType = workType;
      this.HQ = HQ;
      this.HQs = HQs;
      this.cluster = subClass;
      this.JC = JC;
      this.listedDr = listedDr;
      this.chemist = chemist;
      this.stockiest = stockiest;
      this.unListedDr = unListedDr;
      this.Cip = cip;
      this.hospital = hospital;
      this.clusters = clusters;
      this.JCs = JCs;
      this.listedDrs = listedDrs;
      this.chemists = chemists;
      this.stockiests = stockiests;
      this.unListedDrs = unListedDrs;
      this.cips = cips;
      this.hospitals = hospitals;
    }

    public SessionList (SessionList sessionList) {
      this.layoutVisible = sessionList.getLayoutVisible();
      this.isVisible = sessionList.getVisible();
      this.remarks = sessionList.getRemarks();
      this.workType = new WorkType(sessionList.getWorkType());
      this.HQ = new SubClass(sessionList.getHQ());
      this.HQs = new ArrayList<ModelClass.SessionList.SubClass>();
      this.cluster = new ArrayList<ModelClass.SessionList.SubClass>();
      this.JC = new ArrayList<ModelClass.SessionList.SubClass>();
      this.listedDr = new ArrayList<ModelClass.SessionList.SubClass>();
      this.chemist = new ArrayList<ModelClass.SessionList.SubClass>();
      this.stockiest = new ArrayList<ModelClass.SessionList.SubClass>();
      this.unListedDr = new ArrayList<ModelClass.SessionList.SubClass>();
      this.Cip = new ArrayList<ModelClass.SessionList.SubClass>();
      this.hospital = new ArrayList<ModelClass.SessionList.SubClass>();
      this.clusters = new ArrayList<>();
      this.JCs = new ArrayList<>();
      this.listedDrs = new ArrayList<>();
      this.chemists = new ArrayList<>();
      this.stockiests = new ArrayList<>();
      this.unListedDrs = new ArrayList<>();
      this.cips = new ArrayList<>();
      this.hospitals = new ArrayList<>();

//      for (ModelClass.SessionList.SubClass hq : sessionList.getHQs()) {
//        ModelClass.SessionList.SubClass copyHQ = new ModelClass.SessionList.SubClass(hq);
//        this.HQs.add(copyHQ);
//      }
//
//      for (ModelClass.SessionList.SubClass cluster : sessionList.getCluster()) {
//        ModelClass.SessionList.SubClass copyCluster = new ModelClass.SessionList.SubClass(cluster);
//        this.cluster.add(copyCluster);
//      }
//
//      for (ModelClass.SessionList.SubClass jc : sessionList.getJC()) {
//        ModelClass.SessionList.SubClass copyJc = new ModelClass.SessionList.SubClass(jc);
//        this.JC.add(copyJc);
//      }
//
//      for (ModelClass.SessionList.SubClass listedDr : sessionList.getListedDr()) {
//        ModelClass.SessionList.SubClass copyListedDr = new ModelClass.SessionList.SubClass(listedDr);
//        this.listedDr.add(copyListedDr);
//      }
//
//      for (ModelClass.SessionList.SubClass chemist : sessionList.getChemist()) {
//        ModelClass.SessionList.SubClass copyChemist = new ModelClass.SessionList.SubClass(chemist);
//        this.chemist.add(copyChemist);
//      }
//
//      for (ModelClass.SessionList.SubClass stockiest : sessionList.getStockiest()) {
//        ModelClass.SessionList.SubClass copyStockiest = new ModelClass.SessionList.SubClass(stockiest);
//        this.stockiest.add(copyStockiest);
//      }
//
//      for (ModelClass.SessionList.SubClass unListedDr : sessionList.getUnListedDr()) {
//        ModelClass.SessionList.SubClass copyUnListedDr = new ModelClass.SessionList.SubClass(unListedDr);
//        this.unListedDr.add(copyUnListedDr);
//      }
//
//      for (ModelClass.SessionList.SubClass cip : sessionList.getCip()) {
//        ModelClass.SessionList.SubClass copyCip = new ModelClass.SessionList.SubClass(cip);
//        this.Cip.add(copyCip);
//      }
//
//      for (ModelClass.SessionList.SubClass hospital : sessionList.getHospital()) {
//        ModelClass.SessionList.SubClass copyHospital = new ModelClass.SessionList.SubClass(hospital);
//        this.hospital.add(copyHospital);
//      }

      createCopy(this.cluster, sessionList.getCluster());
      createCopy(this.JC, sessionList.getJC());
      createCopy(this.listedDr, sessionList.getListedDr());
      createCopy(this.chemist, sessionList.getChemist());
      createCopy(this.stockiest, sessionList.getStockiest());
      createCopy(this.unListedDr, sessionList.getUnListedDr());
      createCopy(this.Cip, sessionList.getCip());
      createCopy(this.hospital, sessionList.getHospital());

      createCopy(this.HQs, sessionList.getHQs());
      createCopyMGR(this.clusters, sessionList.getClusters());
      createCopyMGR(this.JCs, sessionList.getJCs());
      createCopyMGR(this.listedDrs, sessionList.getListedDrs());
      createCopyMGR(this.chemists, sessionList.getChemists());
      createCopyMGR(this.stockiests, sessionList.getStockiests());
      createCopyMGR(this.unListedDrs, sessionList.getUnListedDrs());
      createCopyMGR(this.cips, sessionList.getCips());
      createCopyMGR(this.hospitals, sessionList.getHospitals());

    }

    private void createCopy(List<SubClass> resultArray, List<SubClass> data) {
      for (SubClass subClass : data) {
        resultArray.add(new SubClass(subClass));
      }
    }

    private void createCopyMGR(ArrayList<MultiHQHeaderModelClass> resultArray, ArrayList<MultiHQHeaderModelClass> data) {
      for (MultiHQHeaderModelClass header : data) {
        resultArray.add(new MultiHQHeaderModelClass(header));
      }
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

    public String getRemarks () {
      return remarks;
    }

    public void setRemarks (String remarks) {
      this.remarks = remarks;
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

    public List<SubClass> getHQs() {
      return HQs;
    }

    public void setHQs(List<SubClass> HQs) {
      this.HQs = HQs;
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
      this.Cip = cip;
    }

    public List<ModelClass.SessionList.SubClass> getHospital () {
      return hospital;
    }

    public void setHospital (List<ModelClass.SessionList.SubClass> hospital) {
      this.hospital = hospital;
    }

    public ArrayList<MultiHQHeaderModelClass> getClusters() {
      return clusters;
    }

    public void setClusters(ArrayList<MultiHQHeaderModelClass> clusters) {
      this.clusters = clusters;
    }

    public ArrayList<MultiHQHeaderModelClass> getJCs() {
      return JCs;
    }

    public void setJCs(ArrayList<MultiHQHeaderModelClass> JCs) {
      this.JCs = JCs;
    }

    public ArrayList<MultiHQHeaderModelClass> getListedDrs() {
      return listedDrs;
    }

    public void setListedDrs(ArrayList<MultiHQHeaderModelClass> listedDrs) {
      this.listedDrs = listedDrs;
    }

    public ArrayList<MultiHQHeaderModelClass> getChemists() {
      return chemists;
    }

    public void setChemists(ArrayList<MultiHQHeaderModelClass> chemists) {
      this.chemists = chemists;
    }

    public ArrayList<MultiHQHeaderModelClass> getStockiests() {
      return stockiests;
    }

    public void setStockiests(ArrayList<MultiHQHeaderModelClass> stockiests) {
      this.stockiests = stockiests;
    }

    public ArrayList<MultiHQHeaderModelClass> getUnListedDrs() {
      return unListedDrs;
    }

    public void setUnListedDrs(ArrayList<MultiHQHeaderModelClass> unListedDrs) {
      this.unListedDrs = unListedDrs;
    }

    public ArrayList<MultiHQHeaderModelClass> getCips() {
      return cips;
    }

    public void setCips(ArrayList<MultiHQHeaderModelClass> cips) {
      this.cips = cips;
    }

    public ArrayList<MultiHQHeaderModelClass> getHospitals() {
      return hospitals;
    }

    public void setHospitals(ArrayList<MultiHQHeaderModelClass> hospitals) {
      this.hospitals = hospitals;
    }

    public static class SubClass implements Serializable {
      private String name = "";

      private String Code = "";

      public SubClass () {
      }

      public SubClass (String name, String code) {
        this.name = name;
        Code = code;
      }

      public SubClass (SubClass subClass) {
        this.name = subClass.getName();
        Code = subClass.getCode();
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

      public WorkType () {
      }

      public WorkType (String FWFlg, String name, String terrSlFlg, String code) {
        this.FWFlg = FWFlg;
        this.name = name;
        TerrSlFlg = terrSlFlg;
        Code = code;
      }

      public WorkType (WorkType workType) {
        this.FWFlg = workType.getFWFlg();
        this.name = workType.getName();
        this.TerrSlFlg = workType.getTerrSlFlg();
        this.Code = workType.getCode();
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

  public static class CountModel implements Serializable {

    String name = "";
    int count = 0;

    public CountModel () {
    }

    public CountModel (String name, int count) {
      this.name = name;
      this.count = count;
    }

    public String getName () {
      return name;
    }

    public void setName (String name) {
      this.name = name;
    }

    public int getCount () {
      return count;
    }

    public void setCount (int count) {
      this.count = count;
    }
  }

}
