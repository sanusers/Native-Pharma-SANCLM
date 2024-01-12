package saneforce.santrip.activity.slideDownloaderAlertBox;

public class SlideModelClass {

    public String ImageName;
    public boolean DownloadStatus;

    public  String DownloadSizeStatus;
    public String progressValue;

    public String slideid;
    public SlideModelClass(String imageName, boolean downloadStatus, String downloadSizeStatus, String progressValue) {
        ImageName = imageName;
        DownloadStatus = downloadStatus;
        DownloadSizeStatus = downloadSizeStatus;
        this.progressValue = progressValue;
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
