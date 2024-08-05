package saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.WorkPlanModelClass;

@Dao
public interface CallOfflineWorkTypeDataDao {
    @Insert
    void insert(CallOfflineWorkTypeDataTable callOfflineWorkTypeDataTable);

    @Update
    void update(CallOfflineWorkTypeDataTable callOfflineWorkTypeDataTable);

    @Delete
    void delete(CallOfflineWorkTypeDataTable callOfflineWorkTypeDataTable);

    @Query("DELETE FROM `CALL_OFFLINE_WORK_TYPE_TABLE`")
    void deleteAllData();

    @Query("DELETE FROM `CALL_OFFLINE_WORK_TYPE_TABLE` WHERE `CALL_OFFLINE_WT_DATE` = :date")
    void delete(String date);

    @Query("SELECT EXISTS(SELECT 1 FROM `CALL_OFFLINE_WORK_TYPE_TABLE`)")
    boolean isAvailableWT();

    @Query("SELECT `CALL_OFFLINE_WT_NAME` FROM `CALL_OFFLINE_WORK_TYPE_TABLE` WHERE `CALL_OFFLINE_WT_DATE` = :date")
    List<String> getListOfflineWTNames(String date);

    @Query("SELECT `CALL_OFFLINE_WT_DATE` FROM `CALL_OFFLINE_WORK_TYPE_TABLE`")
    List<String> getAllCallOfflineWTDates();

    @Query("SELECT * FROM `CALL_OFFLINE_WORK_TYPE_TABLE` WHERE `CALL_OFFLINE_WT_DATE` = :date")
    List<CallOfflineWorkTypeDataTable> getCallOfflineWorkTypeDataTable(String date);

    @Query("UPDATE `CALL_OFFLINE_WORK_TYPE_TABLE` SET `CALL_OFFLINE_WT_SYNC_STATUS` = :syncStatus WHERE `ID` = :id")
    void updateWorkTypeStatus(int id, int syncStatus);

    default String getListOfflineWT(String date) {
        List<String> list = getListOfflineWTNames(date);
        StringBuilder fieldWork = new StringBuilder();
        for(String wtName : list) {
            if (fieldWork.toString().isEmpty()) {
                fieldWork.append(wtName);
            } else {
                fieldWork.append(",").append(wtName);
            }
        }
        return fieldWork.toString();
    }

    default WorkPlanModelClass getWorkPlanModelClass(String date) {
        WorkPlanModelClass workPlanModelClass = null;
        List<CallOfflineWorkTypeDataTable> callOfflineWorkTypeDataTableList = getCallOfflineWorkTypeDataTable(date);
        if(!callOfflineWorkTypeDataTableList.isEmpty()) {
            CallOfflineWorkTypeDataTable callOfflineWorkTypeDataTable = callOfflineWorkTypeDataTableList.get(callOfflineWorkTypeDataTableList.size() - 1);
            if(callOfflineWorkTypeDataTable != null) {
                workPlanModelClass = new WorkPlanModelClass(callOfflineWorkTypeDataTable.getId(), callOfflineWorkTypeDataTable.getCallOfflineWTDate(), callOfflineWorkTypeDataTable.getCallOfflineWTName(), callOfflineWorkTypeDataTable.getCallOfflineWTCode(), callOfflineWorkTypeDataTable.getCallOfflineWTJson(), callOfflineWorkTypeDataTable.getCallOfflineWTStatus(), callOfflineWorkTypeDataTable.getCallOfflineWTSyncStatus());
            }
        }
//        workPlanModelClass = new WorkPlanModelClass();
        return workPlanModelClass;
    }

}
