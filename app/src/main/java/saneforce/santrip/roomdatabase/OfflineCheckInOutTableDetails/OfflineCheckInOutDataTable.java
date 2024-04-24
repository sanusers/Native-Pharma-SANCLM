package saneforce.santrip.roomdatabase.OfflineCheckInOutTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "offline_check_in_out_table")
public class OfflineCheckInOutDataTable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "offline_check_in_out_date")
    private String offlineCheckInOutDate;

    @ColumnInfo(name = "offline_check_in_time")
    private String offlineCheckInTime;

    @ColumnInfo(name = "offline_check_out_time")
    private String offlineCheckOutTime;

    @ColumnInfo(name = "offline_check_in_json")
    private String offlineCheckInJson;

    @ColumnInfo(name = "offline_check_out_json")
    private String offlineCheckOutJson;

    @ColumnInfo(name = "offline_date_count")
    private int offlineDateCount;

    @ColumnInfo(name = "offline_sync_status")
    private int offlineSyncStatus;

    public OfflineCheckInOutDataTable() {}

    @Ignore
    public OfflineCheckInOutDataTable(String offlineCheckInOutDate, String offlineCheckInTime, String offlineCheckOutTime, String offlineCheckInJson, String offlineCheckOutJson, int offlineDateCount, int offlineSyncStatus) {
        this.offlineCheckInOutDate = offlineCheckInOutDate;
        this.offlineCheckInTime = offlineCheckInTime;
        this.offlineCheckOutTime = offlineCheckOutTime;
        this.offlineCheckInJson = offlineCheckInJson;
        this.offlineCheckOutJson = offlineCheckOutJson;
        this.offlineDateCount = offlineDateCount;
        this.offlineSyncStatus = offlineSyncStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOfflineCheckInOutDate() {
        return offlineCheckInOutDate;
    }

    public void setOfflineCheckInOutDate(String offlineCheckInOutDate) {
        this.offlineCheckInOutDate = offlineCheckInOutDate;
    }

    public String getOfflineCheckInTime() {
        return offlineCheckInTime;
    }

    public void setOfflineCheckInTime(String offlineCheckInTime) {
        this.offlineCheckInTime = offlineCheckInTime;
    }

    public String getOfflineCheckOutTime() {
        return offlineCheckOutTime;
    }

    public void setOfflineCheckOutTime(String offlineCheckOutTime) {
        this.offlineCheckOutTime = offlineCheckOutTime;
    }

    public String getOfflineCheckInJson() {
        return offlineCheckInJson;
    }

    public void setOfflineCheckInJson(String offlineCheckInJson) {
        this.offlineCheckInJson = offlineCheckInJson;
    }

    public String getOfflineCheckOutJson() {
        return offlineCheckOutJson;
    }

    public void setOfflineCheckOutJson(String offlineCheckOutJson) {
        this.offlineCheckOutJson = offlineCheckOutJson;
    }

    public int getOfflineDateCount() {
        return offlineDateCount;
    }

    public void setOfflineDateCount(int offlineDateCount) {
        this.offlineDateCount = offlineDateCount;
    }

    public int getOfflineSyncStatus() {
        return offlineSyncStatus;
    }

    public void setOfflineSyncStatus(int offlineSyncStatus) {
        this.offlineSyncStatus = offlineSyncStatus;
    }
}
