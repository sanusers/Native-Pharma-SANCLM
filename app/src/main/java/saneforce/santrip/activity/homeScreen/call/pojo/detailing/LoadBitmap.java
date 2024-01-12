package saneforce.santrip.activity.homeScreen.call.pojo.detailing;

import android.graphics.Bitmap;

public class LoadBitmap
{
    Bitmap bitmaps;
    int indexVal;
    String timing;
    String dateVal;
    String slideName;
    String slideUrl;

    public String getSlideUrl() {
        return slideUrl;
    }

    public void setSlideUrl(String slideUrl) {
        this.slideUrl = slideUrl;
    }

    public String getSlideType() {
        return slideType;
    }

    public void setSlideType(String slideType) {
        this.slideType = slideType;
    }

    String slideType;
    String brandName;
    String brandCode;

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    boolean isCheck;
    String scribble;

    public String getScribble() {
        return scribble;
    }

    public void setScribble(String scribble) {
        this.scribble = scribble;
    }

    public LoadBitmap(String slideName, String timing)
    {
        this.slideName = slideName;
        this.timing=timing;
    }

    public LoadBitmap(String timing)
    {
        this.slideName = slideName;
        this.timing=timing;
        this.dateVal=dateVal;
    }

    public LoadBitmap(Bitmap bitmaps, int indexVal)
    {
        this.bitmaps = bitmaps;
        this.indexVal = indexVal;
    }

    public LoadBitmap(String scribble, String timing, int indexVal, String dateVal, String slideName,String slideType,String slideUrl,String brandName,String brandCode)
    {
        this.scribble = scribble;
        this.timing=timing;
        this.indexVal=indexVal;
        this.dateVal=dateVal;
        this.slideName = slideName;
        this.slideType = slideType;
        this.brandName = brandName;
        this.brandCode = brandCode;
        this.slideUrl = slideUrl;

    }

    public LoadBitmap(String name, boolean isCheck)
    {
        slideName =name;
        this.isCheck=isCheck;
    }

    public String getSlideName() {
        return slideName;
    }

    public void setSlideName(String slideName) {
        this.slideName = slideName;
    }

    public String getDateVal() {
        return dateVal;
    }

    public void setDateVal(String dateVal) {
        this.dateVal = dateVal;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public Bitmap getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(Bitmap bitmaps) {
        this.bitmaps = bitmaps;
    }

    public int getIndexVal() {
        return indexVal;
    }

    public void setIndexVal(int indexVal) {
        this.indexVal = indexVal;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
