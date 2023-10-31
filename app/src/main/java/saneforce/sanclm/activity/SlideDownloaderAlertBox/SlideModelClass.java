package saneforce.sanclm.activity.SlideDownloaderAlertBox;

public class SlideModelClass {


    public SlideModelClass(String imagename, String downloadstatus, String downloadsizestatus, String progressvalue) {
        Imagename = imagename;
        Downloadstatus = downloadstatus;
        Downloadsizestatus = downloadsizestatus;
        this.progressvalue = progressvalue;
    }

    public String Imagename;
    public String Downloadstatus;

     public  String Downloadsizestatus;
    public String progressvalue;

    public String getImagename() {
        return Imagename;
    }

    public void setImagename(String imagename) {
        Imagename = imagename;
    }

    public String getDownloadstatus() {
        return Downloadstatus;
    }

    public void setDownloadstatus(String downloadstatus) {
        Downloadstatus = downloadstatus;
    }

    public String getDownloadsizestatus() {
        return Downloadsizestatus;
    }

    public void setDownloadsizestatus(String downloadsizestatus) {
        Downloadsizestatus = downloadsizestatus;
    }

    public String getProgressvalue() {
        return progressvalue;
    }

    public void setProgressvalue(String progressvalue) {
        this.progressvalue = progressvalue;
    }
}
