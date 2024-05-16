package saneforce.santrip.roomdatabase.SlideTable;

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

    public SlidesTableDeatils( String slideId, String slideName, String slideSize, String downloadingStaus) {
        SlideId = slideId;
        SlideName = slideName;
        SlideSize = slideSize;
        DownloadingStaus = downloadingStaus;
    }
    public SlidesTableDeatils() {
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
