package saneforce.sanclm.activity.slideDownloaderAlertBox;

public class SlideModelClass {

    public String ImageName;
    public String DownloadStatus;

    public  String DownloadSizeStatus;
    public String progressValue;

    public SlideModelClass(String imageName, String downloadStatus, String downloadSizeStatus, String progressValue) {
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

    public String getDownloadStatus () {
        return DownloadStatus;
    }

    public void setDownloadStatus (String downloadStatus) {
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
