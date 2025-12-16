package saneforce.sanzen.roomdatabase.NotificationTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_table")
public class NotificationDataTable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "is_read", defaultValue = "0")
    private int isRead;

    @ColumnInfo(name = "sync_status", defaultValue = "0")
    private int syncStatus;

    @ColumnInfo(name = "is_dialog_shown", defaultValue = "0")
    private int isDialogShown;

    @Ignore
    public NotificationDataTable() {
    }

    public NotificationDataTable(String title, String message, String dateTime) {
        this.title = title;
        this.message = message;
        this.dateTime = dateTime;
    }

    @Ignore
    public NotificationDataTable(String title, String message, String dateTime, int isRead) {
        this.title = title;
        this.message = message;
        this.dateTime = dateTime;
        this.isRead = isRead;
    }

    @Ignore
    public NotificationDataTable(String title, String message, String dateTime, int isRead, int syncStatus, int isDialogShown) {
        this.title = title;
        this.message = message;
        this.dateTime = dateTime;
        this.isRead = isRead;
        this.syncStatus = syncStatus;
        this.isDialogShown = isDialogShown;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getIsDialogShown() {
        return isDialogShown;
    }

    public void setIsDialogShown(int isDialogShown) {
        this.isDialogShown = isDialogShown;
    }
}
