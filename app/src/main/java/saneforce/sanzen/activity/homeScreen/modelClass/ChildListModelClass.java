package saneforce.sanzen.activity.homeScreen.modelClass;

import java.util.ArrayList;

import saneforce.sanzen.roomdatabase.QuizOfflineTableDetails.QuizOfflineDataTable;

public class ChildListModelClass {
    private String childName;
    private String groupName;
    private int childId;
    private ArrayList<OutBoxCallList> outBoxCallLists;
    private ArrayList<EcModelClass> ecModelClasses;
    private ArrayList<ActivityModelClass> activityModelClasses;
    private ArrayList<ActivityUploadModelClass> activityUploadModelClasses;
    private ArrayList<CheckInOutModelClass> checkInOutModelClasses;
    private WorkPlanModelClass workPlanModelClass;
    private DaySubmitModelClass daySubmitModelClass;
    private QuizModelClass quizModelClass;
    private boolean isAvailableList;
    private boolean isExpanded;
    private int Counts;
    private String otherContents;

    public ChildListModelClass(String childName, int childId, boolean isExpanded, boolean isAvailableList, ArrayList<OutBoxCallList> outBoxCallLists, String dummy) {
        this.childName = childName;
        this.childId = childId;
        this.isExpanded = isExpanded;
        this.isAvailableList = isAvailableList;
        this.outBoxCallLists = outBoxCallLists;
    }

    public ChildListModelClass(String childName, int childId, boolean isExpanded, boolean isAvailableList, ArrayList<EcModelClass> ecModelClasses) {
        this.childName = childName;
        this.childId = childId;
        this.isExpanded = isExpanded;
        this.isAvailableList = isAvailableList;
        this.ecModelClasses = ecModelClasses;
    }

    public ChildListModelClass(String childName, int childId, boolean isExpanded, boolean isAvailableList, ArrayList<ActivityModelClass> activityModelClasses, ActivityModelClass activityModelClass) {
        this.childName = childName;
        this.childId = childId;
        this.isExpanded = isExpanded;
        this.isAvailableList = isAvailableList;
        this.activityModelClasses = activityModelClasses;
    }

    public ChildListModelClass(String childName, int childId, boolean isExpanded, boolean isAvailableList, ArrayList<ActivityUploadModelClass> activityUploadModelClasses, ActivityUploadModelClass activityUploadModelClass) {
        this.childName = childName;
        this.childId = childId;
        this.isExpanded = isExpanded;
        this.isAvailableList = isAvailableList;
        this.activityUploadModelClasses = activityUploadModelClasses;
    }

    public ChildListModelClass(String childName, int childId, boolean isExpanded,boolean isAvailableList, ArrayList<CheckInOutModelClass> checkInOutModelClasses,String dummy,String dummmy2) {
        this.childName = childName;
        this.childId = childId;
        this.isExpanded = isExpanded;
        this.isAvailableList = isAvailableList;
        this.checkInOutModelClasses = checkInOutModelClasses;
    }

    public ChildListModelClass(String childName, int childId, boolean isAvailableList, WorkPlanModelClass workPlanModelClass) {
        this.childName = childName;
        this.childId = childId;
        this.isAvailableList = isAvailableList;
        this.workPlanModelClass = workPlanModelClass;
    }

    public ChildListModelClass(String childName, int childId, boolean isAvailableList, DaySubmitModelClass daySubmitModelClass) {
        this.childName = childName;
        this.childId = childId;
        this.isAvailableList = isAvailableList;
        this.daySubmitModelClass = daySubmitModelClass;
    }

    public ChildListModelClass(String childName, int childId) {
        this.childName = childName;
        this.childId = childId;
    }

    public ChildListModelClass(String childName, int childId, boolean isAvailableList, QuizModelClass quizModelClass) {
        this.childName = childName;
        this.childId = childId;
        this.isAvailableList = isAvailableList;
        this.quizModelClass = quizModelClass;
    }

    public ArrayList<CheckInOutModelClass> getCheckInOutModelClasses() {
        return checkInOutModelClasses;
    }

    public void setCheckInOutModelClasses(ArrayList<CheckInOutModelClass> checkInOutModelClasses) {
        this.checkInOutModelClasses = checkInOutModelClasses;
    }

    public ArrayList<EcModelClass> getEcModelClasses() {
        return ecModelClasses;
    }

    public void setEcModelClasses(ArrayList<EcModelClass> ecModelClasses) {
        this.ecModelClasses = ecModelClasses;
    }

    public String getOtherContents() {
        return otherContents;
    }

    public void setOtherContents(String otherContents) {
        this.otherContents = otherContents;
    }

    public int getCounts() {
        return Counts;
    }

    public void setCounts(int counts) {
        Counts = counts;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isAvailableList() {
        return isAvailableList;
    }

    public void setAvailableList(boolean availableList) {
        isAvailableList = availableList;
    }

    public ArrayList<OutBoxCallList> getOutBoxCallLists() {
        return outBoxCallLists;
    }

    public void setOutBoxCallLists(ArrayList<OutBoxCallList> outBoxCallLists) {
        this.outBoxCallLists = outBoxCallLists;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public WorkPlanModelClass getWorkPlanModelClass() {
        return workPlanModelClass;
    }

    public void setWorkPlanModelClass(WorkPlanModelClass workPlanModelClass) {
        this.workPlanModelClass = workPlanModelClass;
    }

    public DaySubmitModelClass getDaySubmitModelClass() {
        return daySubmitModelClass;
    }

    public void setDaySubmitModelClass(DaySubmitModelClass daySubmitModelClass) {
        this.daySubmitModelClass = daySubmitModelClass;
    }

    public ArrayList<ActivityModelClass> getActivityModelClasses() {
        return activityModelClasses;
    }

    public void setActivityModelClasses(ArrayList<ActivityModelClass> activityModelClasses) {
        this.activityModelClasses = activityModelClasses;
    }

    public ArrayList<ActivityUploadModelClass> getActivityUploadModelClasses() {
        return activityUploadModelClasses;
    }

    public void setActivityUploadModelClasses(ArrayList<ActivityUploadModelClass> activityUploadModelClasses) {
        this.activityUploadModelClasses = activityUploadModelClasses;
    }

    public QuizModelClass getQuizModelClass() {
        return quizModelClass;
    }

    public void setQuizModelClass(QuizModelClass quizModelClass) {
        this.quizModelClass = quizModelClass;
    }
}
