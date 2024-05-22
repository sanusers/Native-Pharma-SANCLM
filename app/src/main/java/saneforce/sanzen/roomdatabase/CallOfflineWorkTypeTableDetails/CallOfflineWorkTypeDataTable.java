package saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "call_offline_work_type_table")
public class CallOfflineWorkTypeDataTable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "call_offline_wt_date")
    private String callOfflineWTDate;

    @ColumnInfo(name = "call_offline_wt_name")
    private String callOfflineWTName;

    @ColumnInfo(name = "call_offline_wt_code")
    private String callOfflineWTCode;

    @ColumnInfo(name = "call_offline_wt_json")
    private String callOfflineWTJson;

    @ColumnInfo(name = "call_offline_wt_status")
    private String callOfflineWTStatus;

    @ColumnInfo(name = "call_offline_wt_sync_status")
    private int callOfflineWTSyncStatus;

    public CallOfflineWorkTypeDataTable() {}

    @Ignore
    public CallOfflineWorkTypeDataTable(String callOfflineWTDate, String callOfflineWTName, String callOfflineWTCode, String callOfflineWTJson, String callOfflineWTStatus, int callOfflineWTSyncStatus) {
        this.callOfflineWTDate = callOfflineWTDate;
        this.callOfflineWTName = callOfflineWTName;
        this.callOfflineWTCode = callOfflineWTCode;
        this.callOfflineWTJson = callOfflineWTJson;
        this.callOfflineWTStatus = callOfflineWTStatus;
        this.callOfflineWTSyncStatus = callOfflineWTSyncStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallOfflineWTDate() {
        return callOfflineWTDate;
    }

    public void setCallOfflineWTDate(String callOfflineWTDate) {
        this.callOfflineWTDate = callOfflineWTDate;
    }

    public String getCallOfflineWTName() {
        return callOfflineWTName;
    }

    public void setCallOfflineWTName(String callOfflineWTName) {
        this.callOfflineWTName = callOfflineWTName;
    }

    public String getCallOfflineWTCode() {
        return callOfflineWTCode;
    }

    public void setCallOfflineWTCode(String callOfflineWTCode) {
        this.callOfflineWTCode = callOfflineWTCode;
    }

    public String getCallOfflineWTJson() {
        return callOfflineWTJson;
    }

    public void setCallOfflineWTJson(String callOfflineWTJson) {
        this.callOfflineWTJson = callOfflineWTJson;
    }

    public String getCallOfflineWTStatus() {
        return callOfflineWTStatus;
    }

    public void setCallOfflineWTStatus(String callOfflineWTStatus) {
        this.callOfflineWTStatus = callOfflineWTStatus;
    }

    public int getCallOfflineWTSyncStatus() {
        return callOfflineWTSyncStatus;
    }

    public void setCallOfflineWTSyncStatus(int callOfflineWTSyncStatus) {
        this.callOfflineWTSyncStatus = callOfflineWTSyncStatus;
    }

}
