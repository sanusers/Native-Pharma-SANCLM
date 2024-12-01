package saneforce.sanzen.roomdatabase.SlideTable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "welcome_slides_table")
public class WelcomeSlidesDataTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name = "";

    @ColumnInfo(name = "slide_size")
    private String slideSize;

    @ColumnInfo(name = "downloading_status")
    private String downloadingStatus;

    @ColumnInfo(name = "progress")
    private String progress;

    @ColumnInfo(name = "background_task")
    private String backgroundTask;

    @ColumnInfo(name=("file_position"))
    private String listSlidePosition;

    public WelcomeSlidesDataTable(String name, String slideSize, String downloadingStatus, String progress, String backgroundTask, String listSlidePosition) {
        this.name = name;
        this.slideSize = slideSize;
        this.downloadingStatus = downloadingStatus;
        this.progress = progress;
        this.backgroundTask = backgroundTask;
        this.listSlidePosition = listSlidePosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlideSize() {
        return slideSize;
    }

    public void setSlideSize(String slideSize) {
        this.slideSize = slideSize;
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

    public String getListSlidePosition() {
        return listSlidePosition;
    }

    public void setListSlidePosition(String listSlidePosition) {
        this.listSlidePosition = listSlidePosition;
    }
}
