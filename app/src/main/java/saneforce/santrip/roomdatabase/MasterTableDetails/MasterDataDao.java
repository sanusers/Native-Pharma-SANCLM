package saneforce.santrip.roomdatabase.MasterTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MasterDataDao {


    @Insert
    void insert(MasterDataTable data);

    @Update
    void update(MasterDataTable data);

    @Delete
    void delete(MasterDataTable data);
    @Query("SELECT `Values` FROM `MASTER DATA` WHERE `Key` = :key")
    String getDataByKey(String key);


    @Query("SELECT * FROM `MASTER DATA` WHERE `Key` = :key")
    MasterDataTable getMasterSyncDataByKey(String key);

    @Query("UPDATE `MASTER DATA` SET `Values` = :valuse WHERE `Key` = :key")
    int updatedata(String key, String valuse);

    @Query("DELETE FROM `MASTER DATA`")
    void deleteAllMasterData();
}
