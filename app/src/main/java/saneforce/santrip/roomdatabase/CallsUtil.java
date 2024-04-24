package saneforce.santrip.roomdatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import saneforce.santrip.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.santrip.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataTable;
import saneforce.santrip.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.santrip.roomdatabase.CallOfflineTableDetails.CallOfflineDataTable;
import saneforce.santrip.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.santrip.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;

public class CallsUtil {
    private final CallOfflineECDataDao callOfflineECDataDao;
    private final CallOfflineDataDao callOfflineDataDao;
    private final CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao;
    private final OfflineCheckInOutDataDao offlineCheckInOutDataDao;

    public CallsUtil(Context context) {
        RoomDB roomDB = RoomDB.getDatabase(context);
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
        callOfflineWorkTypeDataDao = roomDB.callOfflineWorkTypeDataDao();
        offlineCheckInOutDataDao = roomDB.offlineCheckInOutDataDao();
    }

    public void deleteOfflineCalls() {
        callOfflineDataDao.deleteAllData();
        callOfflineECDataDao.deleteAllData();
        callOfflineWorkTypeDataDao.deleteAllData();
        offlineCheckInOutDataDao.deleteAllData();
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
        return !callOfflineDataDao.isAvailableCall(Constants.DUPLICATE_CALL) || callOfflineECDataDao.isAvailableEc() || offlineCheckInOutDataDao.isAvailableCheckInOut();
    }

    public ArrayList<GroupModelClass> getOutBoxDate() {
        Set<String> dates = new HashSet<>();
        dates.addAll(callOfflineDataDao.getAllCallOfflineDates());
        dates.addAll(callOfflineECDataDao.getAllCallOfflineECDates());
        dates.addAll(callOfflineWorkTypeDataDao.getAllCallOfflineWTDates());
        dates.addAll(offlineCheckInOutDataDao.getAllOfflineCheckInOutDates());
        ArrayList<GroupModelClass> listData = new ArrayList<>();
        ArrayList<ChildListModelClass> groupNamesList;
        if (dates.size() > 0) {
            for (String date : dates) {
                groupNamesList = new ArrayList<>();
                groupNamesList.add(new ChildListModelClass("Checking In/Out", 0, false, true, offlineCheckInOutDataDao.getCheckInOutTime(date), "", ""));
                groupNamesList.add(new ChildListModelClass(callOfflineWorkTypeDataDao.getListOfflineWT(date), 1, false));
                groupNamesList.add(new ChildListModelClass("Calls", 2, false, true, getOutBoxCallsList(date), ""));
                groupNamesList.add(new ChildListModelClass("Event Captured", 3, false, true, callOfflineECDataDao.getEcList(date)));
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
