package saneforce.sanzen.roomdatabase.CallOfflineTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "call_offline_table")
public class CallOfflineDataTable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "call_date")
    private String callDate;

    @ColumnInfo(name = "call_time")
    private String callTime;

    @ColumnInfo(name = "call_in_time")
    private String callInTime;

    @ColumnInfo(name = "call_out_time")
    private String callOutTime;

    @ColumnInfo(name = "call_cus_code")
    private String callCustomerCode;

    @ColumnInfo(name = "call_cus_name")
    private String callCustomerName;

    @ColumnInfo(name = "call_cus_type")
    private String callCustomerType;

    @ColumnInfo(name = "call_json_values")
    private String callJsonValues;

    @ColumnInfo(name = "call_sync_count")
    private int callSyncCount;

    @ColumnInfo(name = "call_sync_status")
    private String callSyncStatus;

    public CallOfflineDataTable() {}

    @Ignore
    public CallOfflineDataTable(String callDate, String callTime, String callInTime, String callOutTime, String callCustomerCode, String callCustomerName, String callCustomerType, String callJsonValues, int callSyncCount, String callSyncStatus) {
        this.callDate = callDate;
        this.callTime = callTime;
        this.callInTime = callInTime;
        this.callOutTime = callOutTime;
        this.callCustomerCode = callCustomerCode;
        this.callCustomerName = callCustomerName;
        this.callCustomerType = callCustomerType;
        this.callJsonValues = callJsonValues;
        this.callSyncCount = callSyncCount;
        this.callSyncStatus = callSyncStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallInTime() {
        return callInTime;
    }

    public void setCallInTime(String callInTime) {
        this.callInTime = callInTime;
    }

    public String getCallOutTime() {
        return callOutTime;
    }

    public void setCallOutTime(String callOutTime) {
        this.callOutTime = callOutTime;
    }

    public String getCallCustomerCode() {
        return callCustomerCode;
    }

    public void setCallCustomerCode(String callCustomerCode) {
        this.callCustomerCode = callCustomerCode;
    }

    public String getCallCustomerName() {
        return callCustomerName;
    }

    public void setCallCustomerName(String callCustomerName) {
        this.callCustomerName = callCustomerName;
    }

    public String getCallCustomerType() {
        return callCustomerType;
    }

    public void setCallCustomerType(String callCustomerType) {
        this.callCustomerType = callCustomerType;
    }

    public String getCallJsonValues() {
        return callJsonValues;
    }

    public void setCallJsonValues(String callJsonValues) {
        this.callJsonValues = callJsonValues;
    }

    public int getCallSyncCount() {
        return callSyncCount;
    }

    public void setCallSyncCount(int callSyncCount) {
        this.callSyncCount = callSyncCount;
    }

    public String getCallSyncStatus() {
        return callSyncStatus;
    }

    public void setCallSyncStatus(String callSyncStatus) {
        this.callSyncStatus = callSyncStatus;
    }
}
