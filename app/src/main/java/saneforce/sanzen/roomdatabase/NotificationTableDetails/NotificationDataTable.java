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

    @Ignore
    public NotificationDataTable() {
    }

    public NotificationDataTable(String title, String message, String dateTime) {
        this.title = title;
        this.message = message;
        this.dateTime = dateTime;
    }

    @Ignore
    public NotificationDataTable(int id, String title, String message, String dateTime, int isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.dateTime = dateTime;
        this.isRead = isRead;
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

}
