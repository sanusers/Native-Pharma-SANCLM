package saneforce.santrip.roomdatabase.DCRDocDataTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DCRDocDataDao {
    @Insert
    void insert(DCRDocDataTable dcrDocDataTable);

    @Update
    void update(DCRDocDataTable dcrDocDataTable);

    @Delete
    void delete(DCRDocDataTable dcrDocDataTable);

    @Query("DELETE FROM `DCR_DOC_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDCRDocValues(DCRDocDataTable dcrDocDataTable);

    @Query("SELECT * FROM `DCR_DOC_TABLE` WHERE `HQ_VALUES` = :hqValues")
    DCRDocDataTable getDCRDocData(String hqValues);
}
