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

    @Query("SELECT * FROM missed_table WHERE `key` = :key")
    MissedTable getMissedByKey(String key);

    @Query("SELECT `values` FROM missed_table WHERE `key` = :key")
    String getMissedValues(String key);

    @Query("DELETE FROM missed_table")
    void deleteAll();

    @Query("UPDATE missed_table SET `values` = :values WHERE `key` = :key")
    int updateMissedValues(String key, String values);

    @Query("SELECT EXISTS(SELECT 1 FROM missed_table WHERE `key` = :key)")
    boolean isMissedDataAvailable(String key);


    default MissedTable getOrCreate(String key) {
        MissedTable data = getMissedByKey(key);
        if (data == null) data = new MissedTable(key, null);
        return data;
    }


    default void saveMissedJson(String key, String json) {
        MissedTable data = getMissedByKey(key);
        if (data != null) {
            data.setValues(json);
            updateMissed(data);
        } else {
            insertMissed(new MissedTable(key, json));
        }
    }
}
