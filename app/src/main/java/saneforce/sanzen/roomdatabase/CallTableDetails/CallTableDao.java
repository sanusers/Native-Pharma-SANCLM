package saneforce.sanzen.roomdatabase.CallTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CallTableDao {

    @Insert
    void insert(CallsLinechartTable data);

    @Update
    void update(CallsLinechartTable data);

    @Delete
    void delete(CallsLinechartTable data);


    @Query("DELETE FROM LINE_CHAT_DATA_TABLE")
    void deleteAllData();

    @Query("SELECT COUNT(*) FROM LINE_CHAT_DATA_TABLE WHERE LINECHAR_MNTH = :currentMonth AND LINECHAR_CUSTTYPE = :custType")
    int getCurrentMonthCallsCount(String currentMonth, String custType);


    @Query("SELECT COUNT(*) FROM LINE_CHAT_DATA_TABLE WHERE LINECHAR_DATE_FLOG = :Flag AND LINECHAR_CUSTTYPE = :custType")
    int getCallsCountByRange(String Flag, String custType);


    @Query("SELECT EXISTS (SELECT 1 FROM LINE_CHAT_DATA_TABLE WHERE LINECHAR_CUSTTYPE = :custType AND LINECHAR_MNTH = :month)")
    boolean isMonthDataAvailableForCustType(String custType, String month);


    @Query("SELECT COUNT(DISTINCT LINECHAR_DCR_DT) FROM LINE_CHAT_DATA_TABLE WHERE LINECHAR_DATE_FLOG = :Flag AND LINECHAR_FM_INDICATOR = 'F' AND LINECHAR_CUSTTYPE = :cutype")
    int getFieldworkCount(String cutype,String Flag);
    @Query("DELETE FROM LINE_CHAT_DATA_TABLE")
    void deleteAllLineData();

}