package saneforce.sanzen.roomdatabase;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDao;
import saneforce.sanzen.roomdatabase.QuizOfflineTableDetails.QuizOfflineDataDao;

public class CallsUtil {
    private final CallOfflineECDataDao callOfflineECDataDao;
    private final CallOfflineDataDao callOfflineDataDao;
    private final CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao;
    private final OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private final OfflineDaySubmitDao offlineDaySubmitDao;
    private final ActivityOfflineDataDao activityOfflineDataDao;
    private final ActivityUploadDataDao activityUploadDataDao;
    private final QuizOfflineDataDao quizOfflineDataDao;
    private final QuizAssertsDao quizAssertsDao;

    public CallsUtil(Context context) {
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
    }

    public void deleteOfflineCalls() {
        callOfflineDataDao.deleteAllData();
        callOfflineECDataDao.deleteAllData();
        callOfflineWorkTypeDataDao.deleteAllData();
        offlineCheckInOutDataDao.deleteAllData();
        offlineDaySubmitDao.deleteAllData();
        activityOfflineDataDao.deleteAllData();
        activityUploadDataDao.deleteAllData();
//        quizOfflineDataDao.deleteAllData();
    }

    public void deleteOfflineCalls(String cusCode, String cusName, String date) {
        callOfflineDataDao.deleteOfflineCalls(cusCode, cusName, date);
        callOfflineECDataDao.deleteOfflineCalls(cusCode, cusName, date);
        deleteOfflineActivity(cusCode, date);
    }

    public void deleteOfflineActivity(String cusCode, String date) {
        List<ActivityOfflineDataTable> activityOfflineDataTableList = activityOfflineDataDao.getActivityOfflineData(date, cusCode);
        if (activityOfflineDataTableList != null && !activityOfflineDataTableList.isEmpty()) {
            for (ActivityOfflineDataTable activityOfflineDataTable : activityOfflineDataTableList) {
                deleteOfflineActivity(activityOfflineDataTable.getId());
            }
        }
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
        return callOfflineDataDao.isAvailableCall(Constants.DUPLICATE_CALL) || callOfflineECDataDao.isAvailableEc() || offlineCheckInOutDataDao.isAvailableCheckInOut() || offlineDaySubmitDao.isAvailableDaySubmit() || callOfflineDataDao.isAvailableCall() || callOfflineWorkTypeDataDao.isAvailableWT() || activityOfflineDataDao.isActivityAvailable() || activityUploadDataDao.isActivityUploadAvailable();
//                || quizOfflineDataDao.isQuizAvailable();
    }

    public Set<String> getOutboxDates() {
        Set<String> dates = new HashSet<>();
        dates.addAll(callOfflineDataDao.getAllCallOfflineDates());
        dates.addAll(callOfflineECDataDao.getAllCallOfflineECDates());
        dates.addAll(callOfflineWorkTypeDataDao.getAllCallOfflineWTDates());
        dates.addAll(offlineCheckInOutDataDao.getAllOfflineCheckInOutDates());
        dates.addAll(offlineDaySubmitDao.getAllOfflineDaySubmitDates());
        dates.addAll(activityOfflineDataDao.getAllActivityOfflineDates());
        dates.addAll(activityUploadDataDao.getAllActivityUploadDates());
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
//                groupNamesList.add(new ChildListModelClass("Quiz", 7, false, quizOfflineDataDao.getQuizModelClass(date)));
                groupNamesList.add(new ChildListModelClass("Work Plan - " + Arrays.toString(callOfflineWorkTypeDataDao.getListOfflineWTNames(date).toArray()).replace("[", "").replace("]", ""), 1, false, callOfflineWorkTypeDataDao.getWorkPlanModelClass(date)));
                groupNamesList.add(new ChildListModelClass("Calls", 2, false, true, getOutBoxCallsList(date), ""));
                groupNamesList.add(new ChildListModelClass("Event Captured", 3, false, true, callOfflineECDataDao.getEcList(date)));
                groupNamesList.add(new ChildListModelClass("Activity", 4, false, true, activityOfflineDataDao.getActivityList(date), null));
                groupNamesList.add(new ChildListModelClass("Activity Upload", 5, false, true, activityUploadDataDao.getActivityUploadList(date), null));
                groupNamesList.add(new ChildListModelClass("Day Submit", 6, false, offlineDaySubmitDao.getDaySubmitModelClass(date)));
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
