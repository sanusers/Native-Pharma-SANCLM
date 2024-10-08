package saneforce.sanzen.roomdatabase.STPOfflineTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;

@Entity(tableName = "stp_offline_table")
public class STPOfflineDataTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "day_id")
    private String dayID = "";

    @ColumnInfo(name = "day_caption")
    private String dayCaption = "";

    @ColumnInfo(name = "cluste_code")
    private String clusterCode = "";

    @ColumnInfo(name = "cluster_name")
    private String clusterName = "";

    @ColumnInfo(name = "doctor_code")
    private String doctorCode = "";

    @ColumnInfo(name = "doctor_name")
    private String doctorName = "";

    @ColumnInfo(name = "chemist_code")
    private String chemistCode = "";

    @ColumnInfo(name = "chemist_name")
    private String chemistName = "";

    @ColumnInfo(name = "stp_data")
    private String stpData;

    @ColumnInfo(name = "sync_status")
    private String syncStatus; // 0 -> true, 1 -> false

    public STPOfflineDataTable() {}

    @Ignore
    public STPOfflineDataTable(@NonNull String dayID, String dayCaption, String clusterCode, String clusterName, String doctorCode, String doctorName, String chemistCode, String chemistName, String stpData, String syncStatus) {
        this.dayID = dayID;
        this.dayCaption = dayCaption;
        this.clusterCode = clusterCode;
        this.clusterName = clusterName;
        this.doctorCode = doctorCode;
        this.doctorName = doctorName;
        this.chemistCode = chemistCode;
        this.chemistName = chemistName;
        this.stpData = stpData;
        this.syncStatus = syncStatus;
    }

    @NonNull
    public String getDayID() {
        return dayID;
    }

    public void setDayID(@NonNull String dayID) {
        this.dayID = dayID;
    }

    public String getDayCaption() {
        return dayCaption;
    }

    public void setDayCaption(String dayCaption) {
        this.dayCaption = dayCaption;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public void setClusterCode(String clusterCode) {
        this.clusterCode = clusterCode;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getChemistCode() {
        return chemistCode;
    }

    public void setChemistCode(String chemistCode) {
        this.chemistCode = chemistCode;
    }

    public String getChemistName() {
        return chemistName;
    }

    public void setChemistName(String chemistName) {
        this.chemistName = chemistName;
    }

    public String getStpData() {
        return stpData;
    }

    public void setStpData(String stpData) {
        this.stpData = stpData;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public JSONArray getSTPDataJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            if (stpData != null && !stpData.isEmpty()) jsonArray = new JSONArray(stpData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public String getSTPSyncedOrDefault() {
        return syncStatus != null ? syncStatus : "1";
    }

}
