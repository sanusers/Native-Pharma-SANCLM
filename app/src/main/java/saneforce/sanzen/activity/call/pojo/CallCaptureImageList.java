package saneforce.sanzen.activity.call.pojo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CallCaptureImageList implements Parcelable {
    String img_name;
    String img_description;
    String filePath;
    String SystemImgName;
    Bitmap img_view;
    boolean isNewlyAdded;
    boolean showPreview;

    protected CallCaptureImageList(Parcel in) {
        img_name = in.readString();
        img_description = in.readString();
        filePath = in.readString();
        SystemImgName = in.readString();
        img_view = in.readParcelable(Bitmap.class.getClassLoader());
        isNewlyAdded = in.readByte() != 0;
        showPreview = in.readByte() != 0;
    }

    public static final Creator<CallCaptureImageList> CREATOR = new Creator<CallCaptureImageList>() {
        @Override
        public CallCaptureImageList createFromParcel(Parcel in) {
            return new CallCaptureImageList(in);
        }

        @Override
        public CallCaptureImageList[] newArray(int size) {
            return new CallCaptureImageList[size];
        }
    };

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

    public  CallCaptureImageList(String img_name, String img_description, Bitmap img_view, String filePath, String SystemImgName,boolean isNewlyAdded, boolean showPreview) {
        this.img_name = img_name;
        this.img_description = img_description;
        this.img_view = img_view;
        this.filePath = filePath;
        this.SystemImgName = SystemImgName;
        this.isNewlyAdded = isNewlyAdded;
        this.showPreview = showPreview;
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

    public boolean isShowPreview() {
        return showPreview;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(img_name);
        dest.writeString(img_description);
        dest.writeString(filePath);
        dest.writeString(SystemImgName);
        dest.writeParcelable(img_view, flags);
        dest.writeByte((byte) (isNewlyAdded ? 1 : 0));
        dest.writeByte((byte) (showPreview ? 1 : 0));
    }
}
