package saneforce.sanzen.roomdatabase.MasterTableDetails;

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

    @Query("SELECT `VALUES` FROM `MASTER_TABLE` WHERE `key` = :key")
    String getDataByKey(String key);

    @Query("SELECT * FROM `MASTER_TABLE` WHERE `key` = :key")
    MasterDataTable getMasterSyncDataByKey(String key);

    @Query("UPDATE `MASTER_TABLE` SET `VALUES` = :values WHERE `key` = :key")
    int updateData(String key, String values);

    @Query("DELETE FROM `MASTER_TABLE`")
    void deleteAllMasterData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMasterSyncData(MasterDataTable masterDataTable);

    @Query("SELECT EXISTS(SELECT * FROM `MASTER_TABLE` WHERE `KEY` = :key)")
    boolean getMasterSyncDataOfHQ(String key);

    @Query("SELECT `SYNC_STATUS` FROM `MASTER_TABLE` WHERE `KEY` = :key")
    int getMasterSyncStatusByKey(String key);

    default MasterDataTable getMasterDataTableOrNew(String key) {
        MasterDataTable masterDataTable = getMasterSyncDataByKey(key);
        if(masterDataTable == null) masterDataTable = new MasterDataTable();
        return masterDataTable;
    }

    default void saveMasterSyncStatus(String key, int status) {
        MasterDataTable masterDataTable = getMasterSyncDataByKey(key);
        if(masterDataTable != null) {
            masterDataTable.setSyncStatus(status);
            update(masterDataTable);
        }else {
            insert(new MasterDataTable(key, null, status));
        }
    }

}
