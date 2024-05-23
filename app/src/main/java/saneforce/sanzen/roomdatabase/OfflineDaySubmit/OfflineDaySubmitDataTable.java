package saneforce.sanzen.roomdatabase.OfflineDaySubmit;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "offline_day_submit_table")
public class OfflineDaySubmitDataTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "json_day_submit")
    private String jsonDaySubmit;

    @ColumnInfo(name = "sync_status")
    private int syncStatus;

    public OfflineDaySubmitDataTable() {}

    @Ignore
    public OfflineDaySubmitDataTable(String date, String jsonDaySubmit) {
        this.date = date;
        this.jsonDaySubmit = jsonDaySubmit;
        this.syncStatus = 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJsonDaySubmit() {
        return jsonDaySubmit;
    }

    public void setJsonDaySubmit(String jsonDaySubmit) {
        this.jsonDaySubmit = jsonDaySubmit;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
