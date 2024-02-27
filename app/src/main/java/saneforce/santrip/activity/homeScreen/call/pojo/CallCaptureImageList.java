package saneforce.santrip.activity.homeScreen.call.pojo;

import android.graphics.Bitmap;

public class CallCaptureImageList {
    String img_name;
    String img_description;
    String filePath;
    String SystemImgName;
    Bitmap img_view;
    boolean isNewlyAdded;

    public boolean isNewlyAdded() {
        return isNewlyAdded;
    }

    public void setNewlyAdded(boolean newlyAdded) {
        isNewlyAdded = newlyAdded;
    }

    public  CallCaptureImageList(String img_name, String img_description, Bitmap img_view, String filePath, String SystemImgName,boolean isNewlyAdded) {
        this.img_name = img_name;
        this.img_description = img_description;
        this.img_view = img_view;
        this.filePath = filePath;
        this.SystemImgName = SystemImgName;
        this.isNewlyAdded = isNewlyAdded;
    }



    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSystemImgName() {
        return SystemImgName;
    }

    public void setSystemImgName(String systemImgName) {
        SystemImgName = systemImgName;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }

    public String getImg_description() {
        return img_description;
    }

    public void setImg_description(String img_description) {
        this.img_description = img_description;
    }

    public Bitmap getImg_view() {
        return img_view;
    }

    public void setImg_view(Bitmap img_view) {
        this.img_view = img_view;
    }
}
