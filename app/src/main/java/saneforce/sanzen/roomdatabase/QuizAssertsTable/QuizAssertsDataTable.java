package saneforce.sanzen.roomdatabase.QuizAssertsTable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_asserts_table")
public class QuizAssertsDataTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name = "";

    @ColumnInfo(name = "assert_size")
    private String assertSize;

    @ColumnInfo(name = "downloading_status")
    private String downloadingStatus;

    @ColumnInfo(name = "progress")
    private String progress;

    @ColumnInfo(name = "background_task")
    private String backgroundTask;

    @ColumnInfo(name=("assert_position"))
    private String listAssertPosition;

    public QuizAssertsDataTable(@NonNull String name, String assertSize, String downloadingStatus, String progress, String backgroundTask, String listAssertPosition) {
        this.name = name;
        this.assertSize = assertSize;
        this.downloadingStatus = downloadingStatus;
        this.progress = progress;
        this.backgroundTask = backgroundTask;
        this.listAssertPosition = listAssertPosition;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getAssertSize() {
        return assertSize;
    }

    public void setAssertSize(String assertSize) {
        this.assertSize = assertSize;
    }

    public String getDownloadingStatus() {
        return downloadingStatus;
    }

    public void setDownloadingStatus(String downloadingStatus) {
        this.downloadingStatus = downloadingStatus;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getBackgroundTask() {
        return backgroundTask;
    }

    public void setBackgroundTask(String backgroundTask) {
        this.backgroundTask = backgroundTask;
    }

    public String getListAssertPosition() {
        return listAssertPosition;
    }

    public void setListAssertPosition(String listAssertPosition) {
        this.listAssertPosition = listAssertPosition;
    }
}
