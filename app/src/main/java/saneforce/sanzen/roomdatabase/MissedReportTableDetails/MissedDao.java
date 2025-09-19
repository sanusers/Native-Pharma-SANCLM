package saneforce.sanzen.roomdatabase.MissedReportTableDetails;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MissedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMissed(MissedTable missedTable);

    @Update
    void updateMissed(MissedTable missedTable);

    @Query("SELECT * FROM missed_table WHERE sfcode = :sfcode AND ID = :key")
    MissedTable getMissedByKey(String sfcode , String key);

    @Query("SELECT `values` FROM missed_table WHERE  sfcode = :sfcode AND ID = :key")
    String getMissedValues(String sfcode , String key);

    @Query("DELETE FROM missed_table")
    void deleteAll();

    @Query("UPDATE missed_table SET `values` = :values WHERE  sfcode = :sfcode AND ID = :key")
    int updateMissedValues(String sfcode ,String key, String values);

    @Query("SELECT EXISTS(SELECT 1 FROM missed_table WHERE  sfcode = :sfcode AND ID = :key) ")
    boolean isMissedDataAvailable(String sfcode ,String key);



//    default MissedTable getOrCreate(String key) {
//        MissedTable data = getMissedByKey(key);
//        if (data == null) data = new MissedTable(key, null);
//        return data;
//    }


    default void saveMissedJson(String key, String sfcode, String json) {
        MissedTable data = getMissedByKey(sfcode,key);
        if (data != null) {
            data.setValues(json);
            updateMissed(data);
        } else {
            insertMissed(new MissedTable(key,sfcode, json));
        }
    }
}
