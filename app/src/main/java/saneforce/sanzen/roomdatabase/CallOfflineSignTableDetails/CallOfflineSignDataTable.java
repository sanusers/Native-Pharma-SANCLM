package saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "call_offline_sign_table")
public class CallOfflineSignDataTable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "call_image_name_sign")
    public String callSignImageName;

    @ColumnInfo(name = "call_file_path_sign")
    private String callSignFilePath;

    @ColumnInfo(name = "call_json_values_sign")
    private String callSignJsonValues;

    @ColumnInfo(name = "call_status_sign")
    private String callSignStatus;

    @ColumnInfo(name = "call_sync_status_sign")
    private int callSignSyncStatus;
    @ColumnInfo(name = "call_date_sign")
    private String callDateSign;
    @ColumnInfo(name = "call_cus_code_sign")
    private String callCusCodeSign;
    @ColumnInfo(name = "call_cus_name_sign")
    private String callCusNameSign;

    public CallOfflineSignDataTable(String callSignImageName, String callSignFilePath, String callSignJsonValues, String callSignStatus, int callSignSyncStatus, String callDateSign, String callCusCodeSign, String callCusNameSign) {
        this.callSignImageName = callSignImageName;
        this.callSignFilePath = callSignFilePath;
        this.callSignJsonValues = callSignJsonValues;
        this.callSignStatus = callSignStatus;
        this.callSignSyncStatus = callSignSyncStatus;
        this.callDateSign = callDateSign;
        this.callCusCodeSign = callCusCodeSign;
        this.callCusNameSign = callCusNameSign;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCallSignImageName() {
        return callSignImageName;
    }

    public void setCallSignImageName(String callSignImageName) {
        this.callSignImageName = callSignImageName;
    }

    public String getCallSignFilePath() {
        return callSignFilePath;
    }

    public void setCallSignFilePath(String callSignFilePath) {
        this.callSignFilePath = callSignFilePath;
    }

    public String getCallSignJsonValues() {
        return callSignJsonValues;
    }

    public void setCallSignJsonValues(String callSignJsonValues) {
        this.callSignJsonValues = callSignJsonValues;
    }

    public String getCallSignStatus() {
        return callSignStatus;
    }

    public String getCallDateSign() {
        return callDateSign;
    }

    public void setCallDateSign(String callDateSign) {
        this.callDateSign = callDateSign;
    }

    public String getCallCusCodeSign() {
        return callCusCodeSign;
    }

    public void setCallCusCodeSign(String callCusCodeSign) {
        this.callCusCodeSign = callCusCodeSign;
    }

    public void setCallSignStatus(String callSignStatus) {
        this.callSignStatus = callSignStatus;
    }

    public int getCallSignSyncStatus() {
        return callSignSyncStatus;
    }

    public void setCallSignSyncStatusEC(int callSignSyncStatusEC) {
        this.callSignSyncStatus = callSignSyncStatusEC;
    }

    public String getCallCusNameSign() {
        return callCusNameSign;
    }

    public void setCallCusNameSign(String callCusNameSign) {
        this.callCusNameSign = callCusNameSign;
    }
}
