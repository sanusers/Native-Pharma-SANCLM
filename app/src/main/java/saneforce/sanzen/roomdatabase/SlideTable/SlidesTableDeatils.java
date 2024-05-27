package saneforce.sanzen.roomdatabase.SlideTable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName ="SlidesTableDeatils" )

public class SlidesTableDeatils {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "SlideId")
    private String SlideId;
    @ColumnInfo(name = "SlideName")

    private String SlideName;
    @ColumnInfo(name = "SlideSize")

    private String SlideSize;
    @ColumnInfo(name = "DownloadingStaus")
    private String DownloadingStaus;
    @ColumnInfo(name = "Progress")
    private String Progress;

    @ColumnInfo(name = "Background task")
    private String Backgroundtask;

    @ColumnInfo(name=("FilePosition"))
    private String ListSlidePosition;
    public SlidesTableDeatils( String slideId, String slideName, String slideSize, String downloadingStaus,String Progress,String Backgroundtask,String ListSlidePosition) {
        SlideId = slideId;
        SlideName = slideName;
        SlideSize = slideSize;
        DownloadingStaus = downloadingStaus;
        this.Progress = Progress;
        this.Backgroundtask = Backgroundtask;
        this.ListSlidePosition = ListSlidePosition;
    }
    public SlidesTableDeatils() {
    }

    public String getListSlidePosition() {
        return ListSlidePosition;
    }

    public void setListSlidePosition(String listSlidePosition) {
        ListSlidePosition = listSlidePosition;
    }

    public String getProgress() {
        return Progress;
    }

    public void setProgress(String progress) {
        Progress = progress;
    }

    public String getBackgroundtask() {
        return Backgroundtask;
    }

    public void setBackgroundtask(String backgroundtask) {
        Backgroundtask = backgroundtask;
    }

    @NonNull
    public String getSlideId() {
        return SlideId;
    }

    public void setSlideId(@NonNull String slideId) {
        SlideId = slideId;
    }

    public String getSlideName() {
        return SlideName;
    }

    public void setSlideName(String slideName) {
        SlideName = slideName;
    }

    public String getSlideSize() {
        return SlideSize;
    }

    public void setSlideSize(String slideSize) {
        SlideSize = slideSize;
    }

    public String getDownloadingStaus() {
        return DownloadingStaus;
    }

    public void setDownloadingStaus(String downloadingStaus) {
        DownloadingStaus = downloadingStaus;
    }
}
