package saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.SignModelClass;


@Dao
public interface CallOfflineSignDataDao {

    @Insert
    void insert(CallOfflineSignDataTable callOfflineSignDataTable);

    @Update
    void update(CallOfflineSignDataTable callOfflineSignDataTable);

    @Delete
    void delete(CallOfflineSignDataTable callOfflineSignDataTable);

    @Query("DELETE FROM CALL_OFFLINE_SIGN_TABLE")
    void deleteAllSignData();
    @Query("DELETE FROM `CALL_OFFLINE_SIGN_TABLE` WHERE `CALL_FILE_PATH_SIGN` = :filePath")
    void deleteOfflineSignImage(String filePath);
    @Query("DELETE FROM `CALL_OFFLINE_SIGN_TABLE` WHERE `ID` = :id")
    void deleteOfflineSignId(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveOfflineSignData(CallOfflineSignDataTable callOfflineSignDataTable);

    @Query("SELECT * FROM `CALL_OFFLINE_SIGN_TABLE` WHERE `CALL_IMAGE_NAME_SIGN` = :imageName")  // CALL_IMAGE_NAME_SIGN
    List<CallOfflineSignDataTable> getSignDataByImageName(String imageName);

    @Query("SELECT * FROM `CALL_OFFLINE_SIGN_TABLE` WHERE `CALL_DATE_SIGN` = :date")
    List<CallOfflineSignDataTable> getSignDataByDate(String date);

    @Query("SELECT EXISTS(SELECT 1 FROM `CALL_OFFLINE_SIGN_TABLE`)")
    boolean isSignDataAvailable();

    @Query("SELECT EXISTS(SELECT 1 FROM `CALL_OFFLINE_SIGN_TABLE` WHERE `CALL_CUS_CODE_SIGN` = :customerCode AND `CALL_DATE_SIGN` = :date)")
    boolean isSignDataAvailable(String date,String customerCode);

    @Query("UPDATE `CALL_OFFLINE_SIGN_TABLE` SET `CALL_STATUS_SIGN` = :status, `CALL_SYNC_STATUS_SIGN` = :checkSynced WHERE `ID` = :id") // CALL_STATUS_SIGN      CALL_SYNC_STATUS_SIGN
    void updateSignStatus(String id, String status, int checkSynced);


    @Query("SELECT * FROM `CALL_OFFLINE_SIGN_TABLE`")
    List<CallOfflineSignDataTable> getAllSignData();
    @Query("SELECT * FROM `CALL_OFFLINE_SIGN_TABLE` WHERE `CALL_DATE_SIGN` = :date AND `CALL_CUS_CODE_SIGN` = :cusCode")
    CallOfflineSignDataTable getCallOfflineSignData(String cusCode, String date);

    @Query("DELETE FROM `CALL_OFFLINE_SIGN_TABLE` WHERE `CALL_IMAGE_NAME_SIGN` = :imageName")
    void deleteSignDataByImageName(String imageName);
    @Query("SELECT `CALL_DATE_SIGN` FROM `CALL_OFFLINE_SIGN_TABLE`")
    List<String> getCallOfflineSignDate();

default void saveOfflineSign(String imageName, String filePath, String jsonValues, String status, int syncStatus, String date,String cusCode,String cusName)    {
        List<CallOfflineSignDataTable> existingData = getSignDataByImageName(imageName);
        CallOfflineSignDataTable signData = new CallOfflineSignDataTable(imageName, filePath, jsonValues, status, syncStatus, date,cusCode,cusName);

        if (existingData != null && !existingData.isEmpty() ) {
            signData.id = existingData.get(0).id;
            update(signData);
        } else {
            saveOfflineSignData(signData);
        }
    }

    default ArrayList<SignModelClass> getSign(String date) {
        ArrayList<SignModelClass> signModelClassList = new ArrayList<>();
        List<CallOfflineSignDataTable> list = getSignDataByDate(date);
        for(CallOfflineSignDataTable callOfflineSignDataTable : list) {
            signModelClassList.add(new SignModelClass(callOfflineSignDataTable.getId(), callOfflineSignDataTable.getCallSignImageName(), callOfflineSignDataTable.getCallSignFilePath(),callOfflineSignDataTable.getCallSignJsonValues(),callOfflineSignDataTable.getCallSignStatus(), callOfflineSignDataTable.getCallSignSyncStatus(),callOfflineSignDataTable.getCallCusNameSign(), callOfflineSignDataTable.getCallCusCodeSign(),callOfflineSignDataTable.getCallDateSign()));
        }
        return signModelClassList;
    }
}

// check for the doc code in the room db and delete it