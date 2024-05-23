package saneforce.sanzen.roomdatabase.CallOfflineECTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "call_offline_ec_table")
public class CallOfflineECDataTable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "call_date_ec")
    private String callDateEC;

    @ColumnInfo(name = "call_cus_code_ec")
    private String callCusCodeEC;

    @ColumnInfo(name = "call_cus_name_ec")
    private String callCusNameEC;

    @ColumnInfo(name = "call_image_name_ec")
    private String callImageNameEC;

    @ColumnInfo(name = "call_file_path")
    private String callFilePath;

    @ColumnInfo(name = "call_json_values_ec")
    private String callJsonValuesEC;

    @ColumnInfo(name = "call_status_ec")
    private String callStatusEC;

    @ColumnInfo(name = "call_sync_status_ec")
    private int callSyncStatusEC;

    public CallOfflineECDataTable() {
    }

    @Ignore
    public CallOfflineECDataTable(String callDateEC, String callCusCodeEC, String callCusNameEC, String callImageNameEC, String callFilePath, String callJsonValuesEC, String callStatusEC, int callSyncStatusEC) {
        this.callDateEC = callDateEC;
        this.callCusCodeEC = callCusCodeEC;
        this.callCusNameEC = callCusNameEC;
        this.callImageNameEC = callImageNameEC;
        this.callFilePath = callFilePath;
        this.callJsonValuesEC = callJsonValuesEC;
        this.callStatusEC = callStatusEC;
        this.callSyncStatusEC = callSyncStatusEC;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallDateEC() {
        return callDateEC;
    }

    public void setCallDateEC(String callDateEC) {
        this.callDateEC = callDateEC;
    }

    public String getCallCusCodeEC() {
        return callCusCodeEC;
    }

    public void setCallCusCodeEC(String callCusCodeEC) {
        this.callCusCodeEC = callCusCodeEC;
    }

    public String getCallCusNameEC() {
        return callCusNameEC;
    }

    public void setCallCusNameEC(String callCusNameEC) {
        this.callCusNameEC = callCusNameEC;
    }

    public String getCallImageNameEC() {
        return callImageNameEC;
    }

    public void setCallImageNameEC(String callImageNameEC) {
        this.callImageNameEC = callImageNameEC;
    }

    public String getCallFilePath() {
        return callFilePath;
    }

    public void setCallFilePath(String callFilePath) {
        this.callFilePath = callFilePath;
    }

    public String getCallJsonValuesEC() {
        return callJsonValuesEC;
    }

    public void setCallJsonValuesEC(String callJsonValuesEC) {
        this.callJsonValuesEC = callJsonValuesEC;
    }

    public String getCallStatusEC() {
        return callStatusEC;
    }

    public void setCallStatusEC(String callStatusEC) {
        this.callStatusEC = callStatusEC;
    }

    public int getCallSyncStatusEC() {
        return callSyncStatusEC;
    }

    public void setCallSyncStatusEC(int callSyncStatusEC) {
        this.callSyncStatusEC = callSyncStatusEC;
    }
}
