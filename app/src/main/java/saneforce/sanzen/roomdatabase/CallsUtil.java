package saneforce.sanzen.roomdatabase;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import saneforce.sanzen.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;

public class CallsUtil {
    private final CallOfflineECDataDao callOfflineECDataDao;
    private final CallOfflineDataDao callOfflineDataDao;
    private final CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao;
    private final OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private final OfflineDaySubmitDao offlineDaySubmitDao;

    public CallsUtil(Context context) {
        RoomDB roomDB = RoomDB.getDatabase(context);
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
        callOfflineWorkTypeDataDao = roomDB.callOfflineWorkTypeDataDao();
        offlineCheckInOutDataDao = roomDB.offlineCheckInOutDataDao();
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
    }

    public void deleteOfflineCalls() {
        callOfflineDataDao.deleteAllData();
        callOfflineECDataDao.deleteAllData();
        callOfflineWorkTypeDataDao.deleteAllData();
        offlineCheckInOutDataDao.deleteAllData();
        offlineDaySubmitDao.deleteAllData();
    }

    public void deleteOfflineCalls(String cusCode, String cusName, String date) {
        callOfflineDataDao.deleteOfflineCalls(cusCode, cusName, date);
        callOfflineECDataDao.deleteOfflineCalls(cusCode, cusName, date);
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

    public boolean isOutBoxDataAvailable() {
        return callOfflineDataDao.isAvailableCall(Constants.DUPLICATE_CALL) || callOfflineECDataDao.isAvailableEc() || offlineCheckInOutDataDao.isAvailableCheckInOut() || offlineDaySubmitDao.isAvailableDaySubmit() || callOfflineDataDao.isAvailableCall() || callOfflineWorkTypeDataDao.isAvailableWT();
    }

    public ArrayList<GroupModelClass> getOutBoxDate() {
        Set<String> dates = new HashSet<>();
        dates.addAll(callOfflineDataDao.getAllCallOfflineDates());
        dates.addAll(callOfflineECDataDao.getAllCallOfflineECDates());
        dates.addAll(callOfflineWorkTypeDataDao.getAllCallOfflineWTDates());
        dates.addAll(offlineCheckInOutDataDao.getAllOfflineCheckInOutDates());
        dates.addAll(offlineDaySubmitDao.getAllOfflineDaySubmitDates());
        ArrayList<GroupModelClass> listData = new ArrayList<>();
        ArrayList<ChildListModelClass> groupNamesList;
        if (!dates.isEmpty()) {
            for (String date : dates) {
                groupNamesList = new ArrayList<>();
                groupNamesList.add(new ChildListModelClass("Checking In/Out", 0, false, true, offlineCheckInOutDataDao.getCheckInOutTime(date), "", ""));
                groupNamesList.add(new ChildListModelClass("Work Plan", 1, false, callOfflineWorkTypeDataDao.getWorkPlanModelClass(date)));
                groupNamesList.add(new ChildListModelClass("Calls", 2, false, true, getOutBoxCallsList(date), ""));
                groupNamesList.add(new ChildListModelClass("Event Captured", 3, false, true, callOfflineECDataDao.getEcList(date)));
                groupNamesList.add(new ChildListModelClass("Day Submit", 4, false, offlineDaySubmitDao.getDaySubmitModelClass(date)));
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
