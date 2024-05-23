package saneforce.sanzen.roomdatabase.CallOfflineECTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;

@Dao
public interface CallOfflineECDataDao {
    @Insert
    void insert(CallOfflineECDataTable callOfflineECDataTable);

    @Update
    void update(CallOfflineECDataTable callOfflineECDataTable);

    @Delete
    void delete(CallOfflineECDataTable callOfflineECDataTable);

    @Query("DELETE FROM `CALL_OFFLINE_EC_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveOfflineECData(CallOfflineECDataTable callOfflineECDataTable);

    @Query("SELECT * FROM `CALL_OFFLINE_EC_TABLE` WHERE `CALL_CUS_CODE_EC` = :cusCode AND `CALL_IMAGE_NAME_EC` = :imgName")
    List<CallOfflineECDataTable> getCallData(String cusCode, String imgName);

    @Query("SELECT EXISTS(SELECT 1 FROM `CALL_OFFLINE_EC_TABLE`)")
    boolean isAvailableEc();

    @Query("SELECT EXISTS(SELECT 1 FROM `CALL_OFFLINE_EC_TABLE` WHERE `CALL_CUS_CODE_EC` = :customerCode AND `CALL_DATE_EC` = :date)")
    boolean isAvailableEc(String date, String customerCode);

    @Query("UPDATE `CALL_OFFLINE_EC_TABLE` SET `CALL_STATUS_EC` = :status, `CALL_SYNC_STATUS_EC` = :checkSynced WHERE `ID` = :id")
    void updateECStatus(String id, String status, int checkSynced);

    @Query("SELECT * FROM `CALL_OFFLINE_EC_TABLE` WHERE `CALL_DATE_EC` = :date AND `CALL_CUS_CODE_EC` = :cusCode")
    CallOfflineECDataTable getCallOfflineECDate(String cusCode, String date);

    @Query("SELECT * FROM `CALL_OFFLINE_EC_TABLE` WHERE `CALL_DATE_EC` = :date")
    List<CallOfflineECDataTable> getCallOfflineECDate(String date);

    @Query("SELECT * FROM `CALL_OFFLINE_EC_TABLE`")
    List<CallOfflineECDataTable> getAllCallOfflineEC();

    @Query("SELECT `CALL_DATE_EC` FROM `CALL_OFFLINE_EC_TABLE`")
    List<String> getAllCallOfflineECDates();

    @Query("DELETE FROM `CALL_OFFLINE_EC_TABLE` WHERE `ID` = :id")
    void deleteOfflineEC(String id);

    @Query("DELETE FROM `CALL_OFFLINE_EC_TABLE` WHERE `CALL_IMAGE_NAME_EC` = :imageName")
    void deleteOfflineECImage(String imageName);

    @Query("DELETE FROM `CALL_OFFLINE_EC_TABLE` WHERE `CALL_CUS_CODE_EC` = :cusCode AND `CALL_CUS_NAME_EC` = :cusName AND `CALL_DATE_EC` = :date")
    void deleteOfflineCalls(String cusCode, String cusName, String date);

    default void saveOfflineEC(String date, String cusCode, String cusName, String img_name, String filepath, String jsonValues, String status, Integer sync) {
        List<CallOfflineECDataTable> list = getCallData(cusCode, img_name);
        CallOfflineECDataTable callOfflineECDataTable = new CallOfflineECDataTable(date, cusCode, cusName, img_name, filepath, jsonValues, status, sync);
        if(list != null && list.size() > 0){
            callOfflineECDataTable.id = list.get(0).id;
            update(callOfflineECDataTable);
        }else {
            saveOfflineECData(callOfflineECDataTable);
        }
    }

    default ArrayList<EcModelClass> getEcList(String date) {
        ArrayList<EcModelClass> ecModelClassList = new ArrayList<>();
        List<CallOfflineECDataTable> list = getCallOfflineECDate(date);
        for(CallOfflineECDataTable callOfflineECDataTable : list) {
            ecModelClassList.add(new EcModelClass(callOfflineECDataTable.getId(), callOfflineECDataTable.getCallDateEC(), callOfflineECDataTable.getCallCusCodeEC(), callOfflineECDataTable.getCallCusNameEC(), callOfflineECDataTable.getCallImageNameEC(), callOfflineECDataTable.getCallFilePath(), callOfflineECDataTable.getCallJsonValuesEC(), callOfflineECDataTable.getCallStatusEC(), callOfflineECDataTable.getCallSyncStatusEC()));
        }
        return ecModelClassList;
    }

    default ArrayList<EcModelClass> getAllEcList() {
        ArrayList<EcModelClass> ecModelClassList = new ArrayList<>();
        List<CallOfflineECDataTable> list = getAllCallOfflineEC();
        for(CallOfflineECDataTable callOfflineECDataTable : list) {
            ecModelClassList.add(new EcModelClass(callOfflineECDataTable.getId(), callOfflineECDataTable.getCallDateEC(), callOfflineECDataTable.getCallCusCodeEC(), callOfflineECDataTable.getCallCusNameEC(), callOfflineECDataTable.getCallImageNameEC(), callOfflineECDataTable.getCallFilePath(), callOfflineECDataTable.getCallJsonValuesEC(), callOfflineECDataTable.getCallStatusEC(), callOfflineECDataTable.getCallSyncStatusEC()));
        }
        return ecModelClassList;
    }
}
