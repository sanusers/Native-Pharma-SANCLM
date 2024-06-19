package saneforce.sanzen.roomdatabase.MasterTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;

@Entity(tableName = "master_table")
public class MasterDataTable {
    @PrimaryKey
    @NonNull
//            (autoGenerate = true)
//    private int id;
    @ColumnInfo(name = "key")
    private String masterKey;
    @ColumnInfo(name = "values")
    private String masterValues;
    @ColumnInfo(name = "sync_status")
    private int syncStatus;

    public MasterDataTable() {
    }

    @Ignore
    public MasterDataTable(@NonNull String masterKey, String masterValues, int syncStatus) {
        this.masterKey = masterKey;
        this.masterValues = masterValues;
        this.syncStatus = syncStatus;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    @NonNull
    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public String getMasterValues() {
        return masterValues;
    }

    public void setMasterValues(String masterValues) {
        this.masterValues = masterValues;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public JSONArray getMasterSyncDataJsonArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            if(masterValues != null && !masterValues.isEmpty()) return jsonArray = new JSONArray(masterValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}