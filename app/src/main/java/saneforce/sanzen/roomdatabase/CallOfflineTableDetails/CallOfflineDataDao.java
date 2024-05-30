package saneforce.sanzen.roomdatabase.CallOfflineTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;

@Dao
public interface CallOfflineDataDao {
    @Insert
    void insert(CallOfflineDataTable callOfflineDataTable);

    @Update
    void update(CallOfflineDataTable callOfflineDataTable);

    @Delete
    void delete(CallOfflineDataTable callOfflineDataTable);

    @Query("DELETE FROM `CALL_OFFLINE_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCallOfflineData(CallOfflineDataTable callOfflineDataTable);

    @Query("SELECT COUNT(1) > 0 FROM CALL_OFFLINE_TABLE")
    boolean isAvailableCall();

    @Query("SELECT EXISTS(SELECT 1 FROM `CALL_OFFLINE_TABLE` WHERE `CALL_SYNC_STATUS` = :status)")
    boolean isAvailableCall(String status);

    @Query("SELECT * FROM `CALL_OFFLINE_TABLE` WHERE `CALL_CUS_CODE` = :cusCode AND `CALL_DATE` = :date")
    CallOfflineDataTable getCallOfflineData(String cusCode, String date);

    @Query("UPDATE `CALL_OFFLINE_TABLE` SET `CALL_JSON_VALUES` = :jsonValue WHERE `CALL_DATE` = :date AND `CALL_CUS_CODE` = :cusCode")
    void saveOfflineUpdateJson(String date, String cusCode, String jsonValue);

    @Query("SELECT `CALL_JSON_VALUES` FROM `CALL_OFFLINE_TABLE` WHERE `CALL_DATE` = :date AND `CALL_CUS_CODE` = :cusCode")
    String getJsonCallList(String date, String cusCode);

    @Query("DELETE FROM `CALL_OFFLINE_TABLE` WHERE `CALL_CUS_CODE` = :cusCode AND `CALL_CUS_NAME` = :cusName AND `CALL_DATE` = :date")
    void deleteOfflineCalls(String cusCode, String cusName, String date);

    @Query("SELECT * FROM `CALL_OFFLINE_TABLE` WHERE `CALL_DATE` = :date ")
    List<CallOfflineDataTable> getOutBoxCallList(String date);

    @Query("SELECT * FROM `CALL_OFFLINE_TABLE`")
    List<CallOfflineDataTable> getAllOutBoxCallList();

    @Query("SELECT `CALL_DATE` FROM `CALL_OFFLINE_TABLE`")
    List<String> getAllCallOfflineDates();

    default void saveOfflineCallIN(String date, String inTime, String cusCode, String cusName, String cusType) {
        CallOfflineDataTable callOfflineDataTable = getCallOfflineData(cusCode, date);
        CallOfflineDataTable callOfflineDataTable1 = new CallOfflineDataTable(date, null, inTime, null, cusCode, cusName, cusType, null, 0, "");
        if(callOfflineDataTable != null){
            callOfflineDataTable.setCallDate(date);
            callOfflineDataTable.setCallInTime(inTime);
            callOfflineDataTable.setCallCustomerCode(cusCode);
            callOfflineDataTable.setCallCustomerName(cusName);
            callOfflineDataTable.setCallCustomerType(cusType);
            update(callOfflineDataTable);
        } else {
            insert(callOfflineDataTable1);
        }
    }

    default void saveOfflineCallOut(String date, String time, String outTime, String cusCode, String cusName, String cusType, String values, String status) {
        CallOfflineDataTable callOfflineDataTable = getCallOfflineData(cusCode, date);
        CallOfflineDataTable callOfflineDataTable1 = new CallOfflineDataTable(date, time, null, outTime, cusCode, cusName, cusType, values, 0, status);
        if(callOfflineDataTable != null){
            callOfflineDataTable.setCallDate(date);
            callOfflineDataTable.setCallTime(time);
            callOfflineDataTable.setCallOutTime(outTime);
            callOfflineDataTable.setCallCustomerCode(cusCode);
            callOfflineDataTable.setCallCustomerName(cusName);
            callOfflineDataTable.setCallCustomerType(cusType);
            callOfflineDataTable.setCallJsonValues(values);
            callOfflineDataTable.setCallSyncStatus(status);
            update(callOfflineDataTable);
        } else {
            insert(callOfflineDataTable1);
        }
    }

    default ArrayList<OutBoxCallList> getOutBoxCallsList(String date) {
        ArrayList<OutBoxCallList> outBoxCallLists = new ArrayList<>();
        List<CallOfflineDataTable> list = getOutBoxCallList(date);
        for (CallOfflineDataTable callOfflineDataTable : list) {
            if (callOfflineDataTable.getCallSyncStatus().isEmpty()) {
//                deleteOfflineCalls(cusCode, cusName, dates);
            }
            if (callOfflineDataTable.getCallDate() != null && !callOfflineDataTable.getCallSyncStatus().isEmpty()) {
                outBoxCallLists.add(new OutBoxCallList(callOfflineDataTable.getCallCustomerName(), callOfflineDataTable.getCallCustomerCode(), callOfflineDataTable.getCallDate(), callOfflineDataTable.getCallInTime(), callOfflineDataTable.getCallOutTime(), callOfflineDataTable.getCallJsonValues(), callOfflineDataTable.getCallCustomerType(), callOfflineDataTable.getCallSyncStatus(), callOfflineDataTable.getCallSyncCount()));
            }
        }
        return outBoxCallLists;
    }

}
