package saneforce.sanzen.activity.tourPlan.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OneBuildModelClass implements Serializable {

//    private String tpId ;
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


    public OneBuildModelClass(/*String tpId,*/String dayNo, String date, String day, String month, String year, boolean onEdit, ArrayList<OneBuildModelClass.SessionList> sessionList){
//        this.tpId = tpId;
        this.dayNo = dayNo;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.onEdit = onEdit;
        this.sessionList = sessionList;

    }
    public OneBuildModelClass(/*String tpId,*/String dayNo, String date, String day, String month, String year, boolean onEdit, ArrayList<OneBuildModelClass.SessionList> sessionList, String STP_Code, String STP_Name) {
//        this.tpId = tpId;
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
//        this.tpId = oneBuildmodelClass.getTpId();
        this.dayNo = oneBuildmodelClass.getDayNo();
        this.date = oneBuildmodelClass.getDate();
        this.day = oneBuildmodelClass.getDay();
        this.month = oneBuildmodelClass.getMonth();
        this.year = oneBuildmodelClass.getYear();
        this.onEdit = oneBuildmodelClass.getOnEdit();
        this.STP_Name = oneBuildmodelClass.getSTP_Name();
        this.STP_Code = oneBuildmodelClass.getSTP_Code();

        this.sessionList = new ArrayList<OneBuildModelClass.SessionList>();
        for(OneBuildModelClass.SessionList sessionListOneBuild : oneBuildmodelClass.sessionList){
            OneBuildModelClass.SessionList copySessionOneBuild = new OneBuildModelClass.SessionList(sessionListOneBuild);
            this.sessionList.add(copySessionOneBuild);
        }
    }
    //id

//    public String getTpId() {
//        return tpId;
//    }
//
//    public void setId(String TpId) {
//        this.tpId = tpId;
//    }

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

        private OneBuildModelClass.SessionList.WorkType workType = new WorkType();
        private OneBuildModelClass.SessionList.SubClass Headquarters = new SubClass();
        private List<OneBuildModelClass.SessionList.SubClass> Territories  = new ArrayList<>();
        private List<OneBuildModelClass.SessionList.SubClass> JointWorks = new ArrayList<>() ;
        private List<OneBuildModelClass.SessionList.SubClass> Doctors = new ArrayList<>();
        private List<OneBuildModelClass.SessionList.SubClass> Chemists = new ArrayList<>();
        private List<OneBuildModelClass.SessionList.SubClass> StockLists = new ArrayList<>();
        private List<OneBuildModelClass.SessionList.SubClass> UnlistedDoctors = new ArrayList<>();
        private List<OneBuildModelClass.SessionList.SubClass> Cip;
        private List<OneBuildModelClass.SessionList.SubClass> Hospitals = new ArrayList<>();

        public SessionList() {
        }

        public SessionList(String layoutVisible, Boolean isVisible, String remarks, String sessionId, OneBuildModelClass.SessionList.WorkType workType,
                           OneBuildModelClass.SessionList.SubClass Headquarters, List<OneBuildModelClass.SessionList.SubClass> Territories , List<OneBuildModelClass.SessionList.SubClass> JointWorks ,
                           List<OneBuildModelClass.SessionList.SubClass> Doctors, List<OneBuildModelClass.SessionList.SubClass> Chemists, List<OneBuildModelClass.SessionList.SubClass> StockLists,
                           List<OneBuildModelClass.SessionList.SubClass> UnlistedDoctors, List<OneBuildModelClass.SessionList.SubClass> cip, List<OneBuildModelClass.SessionList.SubClass> Hospitals) {
            this.layoutVisible = layoutVisible;
            this.isVisible = isVisible;
            this.remarks = remarks;
            this.sessionId = sessionId;
            this.workType = workType;
            this.Headquarters = Headquarters;
            this.Territories  = Territories ;
            this.JointWorks  = JointWorks ;
            this.Doctors = Doctors;
            this.Chemists = Chemists;
            this.StockLists = StockLists;
            this.UnlistedDoctors = UnlistedDoctors;
            Cip = cip;
            this.Hospitals = Hospitals;
        }

        public SessionList (SessionList sessionList){
            this.layoutVisible  = sessionList.getLayoutVisible();
            this.isVisible = sessionList.getVisible();
            this.remarks = sessionList.getRemarks();
            this.sessionId = sessionList.getSessionId();
            this.workType = new WorkType(sessionList.getWorkType());
            this.Headquarters = new SubClass(sessionList.getHeadquarters());
            this.Territories  = new ArrayList<>();
            this.JointWorks  = new ArrayList<>();
            this.Doctors = new ArrayList<>();
            this.Chemists = new ArrayList<>();
            this.StockLists = new ArrayList<>();
            this.UnlistedDoctors = new ArrayList<>();
            this.Cip = new ArrayList<>();
            this.Hospitals = new ArrayList<>();

            if(sessionList.getTerritories () != null) {
                for (OneBuildModelClass.SessionList.SubClass Territories : sessionList.getTerritories ()) {
                    OneBuildModelClass.SessionList.SubClass copyTerritories = new OneBuildModelClass.SessionList.SubClass(Territories);
                    this.Territories .add(copyTerritories);
                }
            }else{
                Log.d("Cluster", "SessionList: "+"Cluster is NULL");
            }
            if(sessionList.getJointWorks() != null) {
                for (OneBuildModelClass.SessionList.SubClass JointWorks : sessionList.getJointWorks()) {
                    OneBuildModelClass.SessionList.SubClass copyJointWorks = new OneBuildModelClass.SessionList.SubClass(JointWorks);
                    this.JointWorks .add(copyJointWorks);
                }
            }else{
                Log.d("JC", "SessionList: "+"JC is NULL");
            }
            if(sessionList.Doctors != null) {
                for (OneBuildModelClass.SessionList.SubClass listedDr : sessionList.Doctors) {
                    OneBuildModelClass.SessionList.SubClass copyListedDr = new OneBuildModelClass.SessionList.SubClass(listedDr);
                    this.Doctors.add(copyListedDr);
                }
            }else{
                Log.d("listedDr", "SessionList: "+"listedDr is Null");
            }
            if(sessionList.getChemists() != null) {
                for (OneBuildModelClass.SessionList.SubClass chemists : sessionList.Chemists) {
                    OneBuildModelClass.SessionList.SubClass copyChemists = new OneBuildModelClass.SessionList.SubClass(chemists);
                    this.Chemists.add(copyChemists);
                }
            }else{
                Log.d("Chemist", "SessionList: "+"Chemist is NULL");
            }
            if(sessionList.getStockLists() != null) {
                for (OneBuildModelClass.SessionList.SubClass stockLists : sessionList.StockLists) {
                    OneBuildModelClass.SessionList.SubClass copyStockLists = new OneBuildModelClass.SessionList.SubClass(stockLists);
                    this.StockLists.add(copyStockLists);
                }
            }else{
                Log.d("StockList", "SessionList: "+"StockList is NULL");
            }
            if(sessionList.getUnlistedDoctors() != null) {
                for (OneBuildModelClass.SessionList.SubClass UnlistedDoctors : sessionList.UnlistedDoctors) {
                    OneBuildModelClass.SessionList.SubClass copyUnlistedDoctors = new OneBuildModelClass.SessionList.SubClass(UnlistedDoctors);
                    this.UnlistedDoctors.add(copyUnlistedDoctors);
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
            if(sessionList.getHospitals() != null) {
                for (OneBuildModelClass.SessionList.SubClass Hospitals : sessionList.Hospitals) {
                    OneBuildModelClass.SessionList.SubClass copyHospitals = new OneBuildModelClass.SessionList.SubClass(Hospitals);
                    this.Hospitals.add(copyHospitals);
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

        public OneBuildModelClass.SessionList.SubClass getHeadquarters() {
            return Headquarters;
        }

        public void setHeadquarters(OneBuildModelClass.SessionList.SubClass Headquarters) {
            this.Headquarters = Headquarters;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getTerritories () {
            return Territories ;
        }

        public void setTerritories (List<OneBuildModelClass.SessionList.SubClass> cluster) {
            Territories  = cluster;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getJointWorks() {
            return JointWorks;
        }

        public void setJointWorks(List<OneBuildModelClass.SessionList.SubClass> JointWorks) {
            this.JointWorks = JointWorks;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getDoctors() {
            return Doctors;
        }

        public void setDoctors(List<OneBuildModelClass.SessionList.SubClass> Doctors) {
            this.Doctors = Doctors;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getChemists() {
            return Chemists;
        }

        public void setChemists(List<OneBuildModelClass.SessionList.SubClass> Chemists) {
            this.Chemists = Chemists;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getStockLists() {
            return StockLists;
        }

        public void setStockLists(List<OneBuildModelClass.SessionList.SubClass> stockList) {
            StockLists = stockList;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getUnlistedDoctors() {
            return UnlistedDoctors;
        }

        public void setUnlistedDoctors(List<OneBuildModelClass.SessionList.SubClass> UnlistedDoctors) {
            this.UnlistedDoctors = UnlistedDoctors;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getCip() {
            return Cip;
        }

        public void setCip(List<OneBuildModelClass.SessionList.SubClass> cip) {
            Cip = cip;
        }

        public List<OneBuildModelClass.SessionList.SubClass> getHospitals() {
            return Hospitals;
        }

        public void setHospitals(List<OneBuildModelClass.SessionList.SubClass> Hospitals) {
            this.Hospitals = Hospitals;
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

