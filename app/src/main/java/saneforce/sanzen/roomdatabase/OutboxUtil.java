package saneforce.sanzen.roomdatabase;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import saneforce.sanzen.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataTable;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDao;
import saneforce.sanzen.roomdatabase.QuizOfflineTableDetails.QuizOfflineDataDao;

public class OutboxUtil {
    private final CallOfflineECDataDao callOfflineECDataDao;
    private final CallOfflineDataDao callOfflineDataDao;
    private final CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao;
    private final OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private final OfflineDaySubmitDao offlineDaySubmitDao;
    private final ActivityOfflineDataDao activityOfflineDataDao;
    private final ActivityUploadDataDao activityUploadDataDao;
    private final QuizOfflineDataDao quizOfflineDataDao;
    private final QuizAssertsDao quizAssertsDao;
    private final CallOfflineSignDataDao callOfflineSignDataDao;

    public OutboxUtil(Context context) {
        RoomDB roomDB = RoomDB.getDatabase(context);
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
        callOfflineWorkTypeDataDao = roomDB.callOfflineWorkTypeDataDao();
        offlineCheckInOutDataDao = roomDB.offlineCheckInOutDataDao();
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
        activityOfflineDataDao = roomDB.activityOfflineDataDao();
        activityUploadDataDao = roomDB.activityUploadDataDao();
        quizOfflineDataDao = roomDB.quizOfflineDataDao();
        quizAssertsDao = roomDB.quizAssertsDao();
        callOfflineSignDataDao = roomDB.callOfflineSignDataDao();
    }

    public void deleteOfflineCalls() {
        callOfflineDataDao.deleteAllData();
        callOfflineECDataDao.deleteAllData();
        callOfflineWorkTypeDataDao.deleteAllData();
        offlineCheckInOutDataDao.deleteAllData();
        offlineDaySubmitDao.deleteAllData();
        activityOfflineDataDao.deleteAllData();
        activityUploadDataDao.deleteAllData();
        callOfflineSignDataDao.deleteAllSignData();
//        quizOfflineDataDao.deleteAllData();
    }

    public void deleteOfflineCalls(String cusCode, String cusName, String date) {
        callOfflineDataDao.deleteOfflineCalls(cusCode, cusName, date);
        CallOfflineECDataTable callOfflineECDataTable = callOfflineECDataDao.getCallOfflineECDate(cusCode, date);
        if (callOfflineECDataTable != null && callOfflineECDataTable.getCallSyncStatusEC() != 0) {
            callOfflineECDataDao.deleteOfflineCalls(cusCode, cusName, date);
        }
        CallOfflineSignDataTable callOfflineSignDataTable = callOfflineSignDataDao.getCallOfflineSignData(cusCode, date);
        if (callOfflineSignDataTable != null && callOfflineSignDataTable.getCallSignSyncStatus() != 0) {
            callOfflineSignDataDao.deleteOfflineSign(cusCode, cusName, date);
        }
    }

    public void deleteOfflineCallsWithActivity(String cusCode, String cusName, String date) {
        callOfflineDataDao.deleteOfflineCalls(cusCode, cusName, date);
        callOfflineECDataDao.deleteOfflineCalls(cusCode, cusName, date);
        callOfflineSignDataDao.deleteOfflineSign(cusCode, cusName, date);
        deleteOfflineActivityUpload(cusCode, date);
//        deleteOfflineActivity(cusCode, date);
    }

    public void deleteOfflineActivityUpload(String cusCode, String date) {
        activityUploadDataDao.deleteUploadActivity(Integer.parseInt(cusCode), date);
    }

    public void deleteOfflineActivity(String cusCode, String date) {
        List<ActivityOfflineDataTable> activityOfflineDataTableList = activityOfflineDataDao.getActivityOfflineData(date, cusCode);
        if (activityOfflineDataTableList != null && !activityOfflineDataTableList.isEmpty()) {
            for (ActivityOfflineDataTable activityOfflineDataTable : activityOfflineDataTableList) {
                deleteOfflineActivity(activityOfflineDataTable.getId());
            }
        }
        deleteOfflineActivityUpload(cusCode, date);
    }

    private void deleteOfflineActivity(int id) {
        activityOfflineDataDao.deleteOfflineActivity(id);
        activityUploadDataDao.deleteUploadActivity(id);
    }

    public void updateOfflineUpdateStatusEC(String date, String cusCode, int count, String status, int ecSynced) {
        CallOfflineDataTable callOfflineDataTable = callOfflineDataDao.getCallOfflineData(cusCode, date);
        if(callOfflineDataTable != null) {
            callOfflineDataTable.setCallSyncCount(count);
            callOfflineDataTable.setCallSyncStatus(status);
            callOfflineDataDao.update(callOfflineDataTable);
        }
        CallOfflineECDataTable callOfflineECDataTable = callOfflineECDataDao.getCallOfflineECDate(cusCode, date);
        if(callOfflineECDataTable != null) {
            callOfflineECDataTable.setCallStatusEC(status);
            callOfflineECDataTable.setCallSyncStatusEC(ecSynced);
            callOfflineECDataDao.update(callOfflineECDataTable);
        }
        CallOfflineSignDataTable callOfflineECDate = callOfflineSignDataDao.getCallOfflineSignData(cusCode, date);
        if(callOfflineECDate != null) {
            callOfflineECDate.setCallSignStatus(status);
            callOfflineECDate.setCallSignSyncStatus(ecSynced);
            callOfflineSignDataDao.update(callOfflineECDate);
        }
    }

    public void updateStatusActivity(int id, int count, String status) {
        ActivityOfflineDataTable activityOfflineDataTable = activityOfflineDataDao.getActivityOfflineData(id);
        if(activityOfflineDataTable != null) {
            activityOfflineDataTable.setSyncCount(count);
            activityOfflineDataTable.setSyncStatus(status);
            activityOfflineDataDao.update(activityOfflineDataTable);
        }
        ActivityUploadDataTable activityUploadDataTable = activityUploadDataDao.getActivityUploadData(id);
        if(activityUploadDataTable != null) {
            activityUploadDataTable.setSyncCount(count);
            activityUploadDataTable.setSyncStatus(status);
            activityUploadDataDao.update(activityUploadDataTable);
        }
    }

    public boolean isOutBoxDataAvailable() {
        return callOfflineDataDao.isAvailableCallOnStatus(Constants.DUPLICATE_CALL) || callOfflineECDataDao.isAvailableEc() || callOfflineSignDataDao.isSignDataAvailable() || offlineCheckInOutDataDao.isAvailableCheckInOut() || offlineDaySubmitDao.isAvailableDaySubmit() || callOfflineDataDao.isAvailableCall() || callOfflineWorkTypeDataDao.isAvailableWT() || activityOfflineDataDao.isActivityAvailable() || activityUploadDataDao.isActivityUploadAvailable();
    }

    public boolean isOutBoxNonSyncDataAvailable() {
        return callOfflineECDataDao.isNonSyncAvailableEc() || callOfflineSignDataDao.isNonSyncSignDataAvailable() || offlineCheckInOutDataDao.isNonSyncAvailableCheckInOut() || offlineDaySubmitDao.isNonSyncAvailableDaySubmit() || callOfflineDataDao.isNonSyncAvailableCall() || callOfflineWorkTypeDataDao.isNonSyncAvailableWT() || activityOfflineDataDao.isNonSyncActivityAvailable() || activityUploadDataDao.isNonSyncActivityUploadAvailable();
    }

    public boolean checkSyncAvailable(String date, int type) {
        boolean result = false;
        boolean checkInOutDataAvailable = offlineCheckInOutDataDao.isAvailableCheckInOut(date);
        boolean workPlanDataAvailable = callOfflineWorkTypeDataDao.isAvailableWT(date);
        boolean callDataAvailable = callOfflineDataDao.isAvailableCall(date);
        boolean eventCaptureDataAvailable = callOfflineECDataDao.isAvailableEc(date);
        boolean signDataAvailable = callOfflineSignDataDao.isSignDataAvailable(date);
        boolean activityDataAvailable = activityOfflineDataDao.isActivityAvailable(date);
        boolean activityUploadDataAvailable = activityUploadDataDao.isActivityUploadAvailable(date);
        switch (type) {
            case 0:
                break;
            case 1:
                result = checkInOutDataAvailable;
                break;
            case 2:
                result = checkInOutDataAvailable||workPlanDataAvailable;
                break;
            case 3:
//                result = checkInOutDataAvailable||workPlanDataAvailable||callDataAvailable||eventCaptureDataAvailable||signDataAvailable||activityDataAvailable||activityUploadDataAvailable;
                break;
            case 4:
//                result = checkInOutDataAvailable||workPlanDataAvailable||callDataAvailable||eventCaptureDataAvailable||signDataAvailable||activityDataAvailable||activityUploadDataAvailable;
                break;
            case 5:
                result = checkInOutDataAvailable||workPlanDataAvailable||callDataAvailable;
                break;
            case 6:
//                result = checkInOutDataAvailable||workPlanDataAvailable||callDataAvailable||eventCaptureDataAvailable||signDataAvailable||activityDataAvailable||activityUploadDataAvailable;
                break;
            case 7:
                result = checkInOutDataAvailable||workPlanDataAvailable||callDataAvailable||eventCaptureDataAvailable||signDataAvailable||activityDataAvailable||activityUploadDataAvailable;
                break;
        }
        return result;
    }

    public boolean checkIsDataAvailable(GroupModelClass groupModelClass) {
        boolean isDataAvailable = false;
        ArrayList<ChildListModelClass> dataList = groupModelClass.getChildItems();
        if (!dataList.get(0).getCheckInOutModelClasses().isEmpty()) isDataAvailable = true;
        if (dataList.get(1).getWorkPlanModelClass() != null) isDataAvailable = true;
        if (!dataList.get(2).getOutBoxCallLists().isEmpty()) isDataAvailable = true;
        if (!dataList.get(3).getEcModelClasses().isEmpty()) isDataAvailable = true;
        if (!dataList.get(4).getSignModelClasses().isEmpty()) isDataAvailable = true;
        if (!dataList.get(5).getActivityModelClasses().isEmpty()) isDataAvailable = true;
        if (!dataList.get(6).getActivityUploadModelClasses().isEmpty()) isDataAvailable = true;
        if (dataList.get(7).getDaySubmitModelClass() != null) isDataAvailable = true;
        return  isDataAvailable;
    }

    public Set<String> getOutboxDates() {
        Set<String> dates = new TreeSet<>();
        dates.addAll(callOfflineDataDao.getAllCallOfflineDates());
        dates.addAll(callOfflineECDataDao.getAllCallOfflineECDates());
        dates.addAll(callOfflineWorkTypeDataDao.getAllCallOfflineWTDates());
        dates.addAll(offlineCheckInOutDataDao.getAllOfflineCheckInOutDates());
        dates.addAll(offlineDaySubmitDao.getAllOfflineDaySubmitDates());
        dates.addAll(activityOfflineDataDao.getAllActivityOfflineDates());
        dates.addAll(activityUploadDataDao.getAllActivityUploadDates());
        dates.addAll(callOfflineSignDataDao.getCallOfflineSignDate());
//        dates.addAll(quizOfflineDataDao.getAllQuizOfflineDates());
        return dates;
    }

    public ArrayList<GroupModelClass> getOutBoxDatesWithData() {
        Set<String> dates = getOutboxDates();
        ArrayList<GroupModelClass> listData = new ArrayList<>();
        ArrayList<ChildListModelClass> groupNamesList;
        if (!dates.isEmpty()) {
            for (String date : dates) {
                groupNamesList = new ArrayList<>();
                groupNamesList.add(new ChildListModelClass("Checking In/Out", 0, false, true, offlineCheckInOutDataDao.getCheckInOutTime(date), "", ""));
                groupNamesList.add(new ChildListModelClass("Work Plan - " + Arrays.toString(callOfflineWorkTypeDataDao.getListOfflineWTNames(date).toArray()).replace("[", "").replace("]", ""), 1, false, callOfflineWorkTypeDataDao.getWorkPlanModelClass(date)));
                groupNamesList.add(new ChildListModelClass("Calls", 2, false, true, getOutBoxCallsList(date), ""));
                groupNamesList.add(new ChildListModelClass("Event Captured", 3, false, true, callOfflineECDataDao.getEcList(date)));
                groupNamesList.add(new ChildListModelClass("Signature",4,false,true,callOfflineSignDataDao.getSign(date),"","",""));
                groupNamesList.add(new ChildListModelClass("Activity", 5, false, true, activityOfflineDataDao.getActivityList(date), null));
                groupNamesList.add(new ChildListModelClass("Activity Upload", 6, false, true, activityUploadDataDao.getActivityUploadList(date), null));
                groupNamesList.add(new ChildListModelClass("Day Submit", 7, false, offlineDaySubmitDao.getDaySubmitModelClass(date)));
                listData.add(new GroupModelClass(date, groupNamesList, false, 0));
            }
        }
        return listData;
    }

    public ArrayList<OutBoxCallList> getOutBoxCallsList(String date) {
        ArrayList<OutBoxCallList> outBoxCallLists = new ArrayList<>();
        List<CallOfflineDataTable> list = callOfflineDataDao.getOutBoxCallList(date);
        for (CallOfflineDataTable callOfflineDataTable : list) {
            if (callOfflineDataTable.getCallSyncStatus().isEmpty()) {
                deleteOfflineCalls(callOfflineDataTable.getCallCustomerCode(), callOfflineDataTable.getCallCustomerName(), callOfflineDataTable.getCallDate());
            }
            if (callOfflineDataTable.getCallDate() != null && !callOfflineDataTable.getCallSyncStatus().isEmpty()) {
                outBoxCallLists.add(new OutBoxCallList(callOfflineDataTable.getCallCustomerName(), callOfflineDataTable.getCallCustomerCode(), callOfflineDataTable.getCallDate(), callOfflineDataTable.getCallInTime(), callOfflineDataTable.getCallOutTime(), callOfflineDataTable.getCallJsonValues(), callOfflineDataTable.getCallCustomerType(), callOfflineDataTable.getCallSyncStatus(), callOfflineDataTable.getCallSyncCount()));
            }
        }
        return outBoxCallLists;
    }

    public ArrayList<OutBoxCallList> getAllOutBoxCallsList() {
        ArrayList<OutBoxCallList> outBoxCallLists = new ArrayList<>();
        List<CallOfflineDataTable> list = callOfflineDataDao.getAllOutBoxCallList();
        for (CallOfflineDataTable callOfflineDataTable : list) {
            if (callOfflineDataTable.getCallSyncStatus().isEmpty()) {
                deleteOfflineCalls(callOfflineDataTable.getCallCustomerCode(), callOfflineDataTable.getCallCustomerName(), callOfflineDataTable.getCallDate());
            }
            if (callOfflineDataTable.getCallDate() != null && !callOfflineDataTable.getCallSyncStatus().isEmpty()) {
                outBoxCallLists.add(new OutBoxCallList(callOfflineDataTable.getCallCustomerName(), callOfflineDataTable.getCallCustomerCode(), callOfflineDataTable.getCallDate(), callOfflineDataTable.getCallInTime(), callOfflineDataTable.getCallOutTime(), callOfflineDataTable.getCallJsonValues(), callOfflineDataTable.getCallCustomerType(), callOfflineDataTable.getCallSyncStatus(), callOfflineDataTable.getCallSyncCount()));
            }
        }
        return outBoxCallLists;
    }

}
