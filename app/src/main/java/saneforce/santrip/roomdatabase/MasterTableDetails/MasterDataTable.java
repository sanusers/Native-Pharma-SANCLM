package saneforce.santrip.roomdatabase.MasterTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MASTER DATA")
public class MasterDataTable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "Key")
    private String MasterKey;
    @ColumnInfo(name = "Values")
    private String MasterValuse;
    @ColumnInfo(name = "Syncstatus")
    private int Syncstatus;

    public int getSyncstatus() {
        return Syncstatus;
    }

    public void setSyncstatus(int syncstatus) {
        Syncstatus = syncstatus;
    }

    public String getMasterKey() {
        return MasterKey;
    }

    public void setMasterKey(String masterKey) {
        MasterKey = masterKey;
    }

    public String getMasterValuse() {
        return MasterValuse;
    }

    public void setMasterValuse(String masterValuse) {
        MasterValuse = masterValuse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
