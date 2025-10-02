package saneforce.sanzen.activity.call.pojo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class CallSignCaptureImageList implements Parcelable {
    String id;
    String img_Name;
    String filepath;
    Bitmap sign_view;
    boolean isNewlyAdded;


    public CallSignCaptureImageList(Parcel in){
        id = in.readString();
        img_Name = in.readString();
        filepath = in.readString();
        sign_view = in.readParcelable(Bitmap.class.getClassLoader());

    }

    public static final Creator<CallSignCaptureImageList> CREATOR = new Creator<CallSignCaptureImageList>() {
        @Override
        public CallSignCaptureImageList createFromParcel(Parcel in) {
            return new CallSignCaptureImageList(in);
        }

        @Override
        public CallSignCaptureImageList[] newArray(int size) {
            return new CallSignCaptureImageList[size];
        }
    };
    public CallSignCaptureImageList(String id, String img_Name, String filepath, Bitmap sign_img, boolean isNewlyAdded) {
        this.id = id;
        this.img_Name = img_Name;
        this.filepath = filepath;
        this.sign_view = sign_img;
        this.isNewlyAdded = isNewlyAdded();
    }
    public CallSignCaptureImageList(String filepath, String img_Name){
        this.img_Name = img_Name;
        this.filepath = filepath;
    }
    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_Name() {
        return img_Name;
    }
    public void setImg_Name(String img_Name) {
        this.img_Name = img_Name;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Bitmap getSign_view() {
        return sign_view;
    }

    public void setSign_view(Bitmap sign_view) {
        this.sign_view = sign_view;
    }
    public boolean isNewlyAdded() {
        return isNewlyAdded;
    }

    public void setNewlyAdded(boolean newlyAdded) {
        isNewlyAdded = newlyAdded;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.img_Name);
        dest.writeString(this.filepath);
//        dest.writeParcelable(this.sign_view,flags);
    }
}
