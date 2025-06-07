package saneforce.sanzen.activity.tourPlan.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OneBuildModelClass implements Serializable {

    private String id = "0";
    private String dayNo = "";
    private String date ="";
    private String day ="";
    private String month ="" ;
    private String year = "";
    private boolean onEdit = false;
    private String submittedTime = "";
    private String syncStatus = ""; // 0 - Success(Data successfully sent to Remote data base), 1 - Failed(failed to send Remote data base for any reason like no internet or api called failed)
    private String STP_Code = "";
    private String STP_Name = "";
    private ArrayList<SessionList> sessionList;

    public OneBuildModelClass(){
    }


    public OneBuildModelClass(String id,String dayNo, String date, String day, String month, String year, boolean onEdit, ArrayList<OneBuildModelClass.SessionList> sessionList){
        this.id = id;
        this.dayNo = dayNo;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.onEdit = onEdit;
        this.sessionList = sessionList;

    }
    public OneBuildModelClass(String id,String dayNo, String date, String day, String month, String year, boolean onEdit, ArrayList<OneBuildModelClass.SessionList> sessionList, String STP_Code, String STP_Name) {
        this.id = id;
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
    public OneBuildModelClass(OneBuildModelClass oneBuildmodelClass){
        this.id = oneBuildmodelClass.getId();
        this.dayNo = oneBuildmodelClass.getDayNo();
        this.date = oneBuildmodelClass.getDate();
        this.day = oneBuildmodelClass.getDay();
        this.month = oneBuildmodelClass.getMonth();
        this.year = oneBuildmodelClass.getYear();
        this.onEdit = oneBuildmodelClass.getOnEdit();
        this.STP_Name = oneBuildmodelClass.getSTP_Name();
        this.STP_Code = oneBuildmodelClass.getSTP_Code();

        this.sessionList = new ArrayList<>();
        for(OneBuildModelClass.SessionList sessionListOneBuild : oneBuildmodelClass.sessionList){
            OneBuildModelClass.SessionList copySessionOneBuild = new OneBuildModelClass.SessionList(sessionListOneBuild);
            this.sessionList.add(copySessionOneBuild);
        }
    }
    //id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //day no
    public String getDayNo() {
        return dayNo;
    }

    public void setDayNo(String dayNo) {
        this.dayNo = dayNo;
    }

    //date

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //day

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    //month

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    //year

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    //onEdit

    public boolean getOnEdit() {
        return onEdit;
    }

    public void setOnEdit(boolean onEdit) {
        this.onEdit = onEdit;
    }

    //submitted time
    public String getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(String submittedTime) {
        this.submittedTime = submittedTime;
    }

    //Sync status

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    //SessionList

    public ArrayList<OneBuildModelClass.SessionList> getSessionList() {
        return sessionList;
    }

    public void setSessionList(ArrayList<OneBuildModelClass.SessionList> sessionList) {
        this.sessionList = sessionList;
    }

    //STP code

    public String getSTP_Code() {
        return STP_Code;
    }
    public void setSTP_Code(String STP_Code) {
        this.STP_Code = STP_Code;
    }

    //STP name

    public String getSTP_Name() {
        return STP_Name;
    }
    public void setSTP_Name(String STP_Name) {
        this.STP_Name = STP_Name;
    }


    //SESSION LIST

    public static class SessionList implements Serializable {
        private String layoutVisible = "";
        private Boolean isVisible = false;
        private String remarks = "";
        private String sessionId = "";

        private OneBuildModelClass.SessionList.WorkType workType;
        private OneBuildModelClass.SessionList.SubClass hq;
        private List<OneBuildModelClass.SessionList.SubClass> Cluster;
        private List<OneBuildModelClass.SessionList.SubClass> JC;
        private List<OneBuildModelClass.SessionList.SubClass> listedDr;
        private List<OneBuildModelClass.SessionList.SubClass> chemist;
        private List<OneBuildModelClass.SessionList.SubClass> StockList;
        private List<OneBuildModelClass.SessionList.SubClass> unlistedDr;
        private List<OneBuildModelClass.SessionList.SubClass> Cip;
        private List<OneBuildModelClass.SessionList.SubClass> hospital;

        public SessionList() {
        }

        public SessionList(String layoutVisible, Boolean isVisible, String remarks, String sessionId, OneBuildModelClass.SessionList.WorkType workType,
                           OneBuildModelClass.SessionList.SubClass hq, List<OneBuildModelClass.SessionList.SubClass> cluster, List<OneBuildModelClass.SessionList.SubClass> JC,
                           List<OneBuildModelClass.SessionList.SubClass> listedDr, List<OneBuildModelClass.SessionList.SubClass> chemist, List<OneBuildModelClass.SessionList.SubClass> stockList,
                           List<OneBuildModelClass.SessionList.SubClass> unlistedDr, List<OneBuildModelClass.SessionList.SubClass> cip, List<OneBuildModelClass.SessionList.SubClass> hospital) {
            this.layoutVisible = layoutVisible;
            this.isVisible = isVisible;
            this.remarks = remarks;
            this.sessionId = sessionId;
            this.workType = workType;
            this.hq = hq;
            this.Cluster = cluster;
            this.JC = JC;
            this.listedDr = listedDr;
            this.chemist = chemist;
            this.StockList = stockList;
            this.unlistedDr = unlistedDr;
            Cip = cip;
            this.hospital = hospital;
        }

        public SessionList (SessionList sessionList){
            this.layoutVisible  = sessionList.getLayoutVisible();
            this.isVisible = sessionList.getVisible();
            this.remarks = sessionList.getRemarks();
            this.sessionId = sessionList.getSessionId();
            this.workType = new WorkType(sessionList.getWorkType());
            if(sessionList.getHq() != null) {
                this.hq = new SubClass(sessionList.getHq());
            }else{
                Log.d("HQ", "SessionList: "+"HQ is NULL");
            }

            this.Cluster = new ArrayList<>();
            this.JC = new ArrayList<>();
            this.listedDr = new ArrayList<>();
            this.chemist = new ArrayList<>();
            this.StockList = new ArrayList<>();
            this.unlistedDr = new ArrayList<>();
            this.Cip = new ArrayList<>();
            this.hospital = new ArrayList<>();

            if(sessionList.getCluster() != null) {
                for (OneBuildModelClass.SessionList.SubClass Cluster : sessionList.getCluster()) {
                    OneBuildModelClass.SessionList.SubClass copyCluster = new OneBuildModelClass.SessionList.SubClass(Cluster);
                    this.Cluster.add(copyCluster);
                }
            }else{
                Log.d("Cluster", "SessionList: "+"Cluster is NULL");
            }
            if(sessionList.getJC() != null) {
                for (OneBuildModelClass.SessionList.SubClass JC : sessionList.getJC()) {
                    OneBuildModelClass.SessionList.SubClass copyJC = new OneBuildModelClass.SessionList.SubClass(JC);
                    this.JC.add(copyJC);
                }
            }else{
                Log.d("JC", "SessionList: "+"JC is NULL");
            }
            if(sessionList.listedDr != null) {
                for (OneBuildModelClass.SessionList.SubClass listedDr : sessionList.listedDr) {
                    OneBuildModelClass.SessionList.SubClass copyListedDr = new OneBuildModelClass.SessionList.SubClass(listedDr);
                    this.listedDr.add(copyListedDr);
                }
            }else{
                Log.d("listedDr", "SessionList: "+"listedDr is Null");
            }
            if(sessionList.getChemist() != null) {
                for (OneBuildModelClass.SessionList.SubClass chemist : sessionList.chemist) {
                    OneBuildModelClass.SessionList.SubClass copyChemist = new OneBuildModelClass.SessionList.SubClass(chemist);
                    this.chemist.add(copyChemist);
                }
            }else{
                Log.d("Chemist", "SessionList: "+"Chemist is NULL");
            }
            if(sessionList.getStockList() != null) {
                for (OneBuildModelClass.SessionList.SubClass stockList : sessionList.StockList) {
                    OneBuildModelClass.SessionList.SubClass copyStockList = new OneBuildModelClass.SessionList.SubClass(stockList);
                    this.StockList.add(copyStockList);
                }
            }else{
                Log.d("StockList", "SessionList: "+"StockList is NULL");
            }
            if(sessionList.getUnlistedDr() != null) {
                for (OneBuildModelClass.SessionList.SubClass unListedDr : sessionList.unlistedDr) {
                    OneBuildModelClass.SessionList.SubClass copyUnListedDr = new OneBuildModelClass.SessionList.SubClass(unListedDr);
                    this.unlistedDr.add(copyUnListedDr);
                }
            }else{
                Log.d("unListedDr", "SessionList: "+"unListedDr is NULL");
            }
            if(sessionList.getCip() != null) {
                for (OneBuildModelClass.SessionList.SubClass cip : sessionList.Cip) {
                    OneBuildModelClass.SessionList.SubClass copyCip = new OneBuildModelClass.SessionList.SubClass(cip);
                    this.Cip.add(copyCip);
                }
            }else{
                Log.d("Cip", "SessionList: "+"CIP is NULL");
            }
            if(sessionList.getHospital() != null) {
                for (OneBuildModelClass.SessionList.SubClass hospital : sessionList.hospital) {
                    OneBuildModelClass.SessionList.SubClass copyHospital = new OneBuildModelClass.SessionList.SubClass(hospital);
                    this.hospital.add(copyHospital);
                }
            }else{
                Log.d("Hospital", "SessionList: "+"Hospital is NULL");
            }
        }


        public String getLayoutVisible() {
            return layoutVisible;
        }

        public void setLayoutVisible(String layoutVisible) {
            this.layoutVisible = layoutVisible;
        }

        public Boolean getVisible() {
            return isVisible;
        }

        public void setVisible(Boolean visible) {
            isVisible = visible;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public OneBuildModelClass.SessionList.WorkType getWorkType() {
            return workType;
        }

        public void setWorkType(OneBuildModelClass.SessionList.WorkType workType) {
            this.workType = workType;
        }

        public OneBuildModelClass.SessionList.SubClass getHq() {
            return hq;
        }

        public void setHq(OneBuildModelClass.SessionList.SubClass hq) {
            this.hq = hq;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getCluster() {
            return Cluster;
        }

        public void setCluster(List<OneBuildModelClass.SessionList.SubClass> cluster) {
            Cluster = cluster;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getJC() {
            return JC;
        }

        public void setJC(List<OneBuildModelClass.SessionList.SubClass> JC) {
            this.JC = JC;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getListedDr() {
            return listedDr;
        }

        public void setListedDr(List<OneBuildModelClass.SessionList.SubClass> listedDr) {
            this.listedDr = listedDr;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getChemist() {
            return chemist;
        }

        public void setChemist(List<OneBuildModelClass.SessionList.SubClass> chemist) {
            this.chemist = chemist;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getStockList() {
            return StockList;
        }

        public void setStockList(List<OneBuildModelClass.SessionList.SubClass> stockList) {
            StockList = stockList;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getUnlistedDr() {
            return unlistedDr;
        }

        public void setUnlistedDr(List<OneBuildModelClass.SessionList.SubClass> unlistedDr) {
            this.unlistedDr = unlistedDr;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getCip() {
            return Cip;
        }

        public void setCip(List<OneBuildModelClass.SessionList.SubClass> cip) {
            Cip = cip;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getHospital() {
            return hospital;
        }

        public void setHospital(List<OneBuildModelClass.SessionList.SubClass> hospital) {
            this.hospital = hospital;
        }

        //SUB CLASS

        public static class SubClass implements Serializable {
            private String name = "";

            private String Code = "";

            public SubClass() {
            }

            public SubClass(String name, String code) {
                this.name = name;
                Code = code;
            }

            public SubClass(SubClass subClass) {
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

        //WORK TYPE

        public static class WorkType implements Serializable {
            private String FWFlg = "";

            private String name = "";

            private String TerrSlFlg = "";

            private String Code = "";

            public WorkType() {
            }

            public WorkType(String FWFlg, String name, String terrSlFlg, String code) {
                this.FWFlg = FWFlg;
                this.name = name;
                TerrSlFlg = terrSlFlg;
                Code = code;
            }

            public WorkType(WorkType workType) {
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

        public CountModel() {
        }

        public CountModel(String name, int count) {
            this.name = name;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}

