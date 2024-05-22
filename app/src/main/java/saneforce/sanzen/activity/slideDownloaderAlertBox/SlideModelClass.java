package saneforce.sanzen.activity.slideDownloaderAlertBox;

public class SlideModelClass {

    public String ImageName;
    public boolean DownloadStatus;

    public  String DownloadSizeStatus;
    public String progressValue;

    public String slideid;

    public String id;
    public SlideModelClass(String imageName, boolean downloadStatus, String downloadSizeStatus, String progressValue,String id) {
        this.ImageName = imageName;
        this.DownloadStatus = downloadStatus;
        this.DownloadSizeStatus = downloadSizeStatus;
        this.progressValue = progressValue;
        this.id = id;
    }

    public boolean isDownloadStatus() {
        return DownloadStatus;
    }

    public String getSlideid() {
        return slideid;
    }

    public void setSlideid(String slideid) {
        this.slideid = slideid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageName () {
        return ImageName;
    }

    public void setImageName (String imageName) {
        ImageName = imageName;
    }

    public boolean getDownloadStatus () {
        return DownloadStatus;
    }

    public void setDownloadStatus (boolean downloadStatus) {
        DownloadStatus = downloadStatus;
    }

    public String getDownloadSizeStatus () {
        return DownloadSizeStatus;
    }

    public void setDownloadSizeStatus (String downloadSizeStatus) {
        DownloadSizeStatus = downloadSizeStatus;
    }

    public String getProgressValue () {
        return progressValue;
    }

    public void setProgressValue (String progressValue) {
        this.progressValue = progressValue;
    }
}
