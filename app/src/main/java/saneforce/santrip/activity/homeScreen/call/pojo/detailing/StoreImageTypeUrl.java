package saneforce.santrip.activity.homeScreen.call.pojo.detailing;

import java.io.File;
import java.util.Comparator;

public class StoreImageTypeUrl
{
    String slideNam;
    boolean isLike;
    boolean isDisLike;
    String slideComments;

    public String getSlideComments() {
        return slideComments;
    }

    public void setSlideComments(String slideComments) {
        this.slideComments = slideComments;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isDisLike() {
        return isDisLike;
    }

    public void setDisLike(boolean disLike) {
        isDisLike = disLike;
    }

    String slideTyp;
    String slideUrl;
    String timing;
    String slideid;
    String brdName;
    String brdCode;
    String remTime;
    int columnid;
    File ffile;
    String rating;
    String scribble;

    public String getScribble() {
        return scribble;
    }

    public void setScribble(String scribble) {
        this.scribble = scribble;
    }

    public static class StoreImageComparator implements Comparator<StoreImageTypeUrl>
    {
        public int compare(StoreImageTypeUrl left, StoreImageTypeUrl right)
        {
            return left.brdName.compareTo(right.brdName);
        }
    }

    public StoreImageTypeUrl(String slideNam, String slideTyp, String slideUrl, String brdName, String timing)
    {
        this.slideNam = slideNam;
        this.slideTyp = slideTyp;
        this.slideUrl = slideUrl;
        this.brdName=brdName;
        this.timing=timing;
    }

    public StoreImageTypeUrl(String slideNam, String slideId,boolean like,boolean dislike,String slideComments,String scribbleName)
    {
        this.slideNam = slideNam;
        this.slideid = slideId;
        this.isLike = like;
        this.isDisLike = dislike;
        this.slideComments = slideComments;
        this.scribble = scribbleName;
    }

    public StoreImageTypeUrl(String slideNam){
        this.slideNam=slideNam;
    }

    @Override
    public boolean equals(Object obj)
    {
        StoreImageTypeUrl dt = (StoreImageTypeUrl)obj;
        if(slideNam == null)
            return false;
        if(dt.slideNam.equals(slideNam))
            return true;
        return false;
    }
    /*
        public StoreImageTypeUrl(String timing,String slideFeed,String brdName,String slideNam,String slideTyp,String slideUrl){
            this.slideNam = slideNam;
            this.slideTyp = slideTyp;
            this.slideUrl = slideUrl;
            this.timing=timing;
            this.slideFeed=slideFeed;
            this.brdName=brdName;
        }
    */
    public StoreImageTypeUrl(String scribble, String slideNam, String slideTyp, String slideUrl, String timing, String slideComments, String remTime)
    {
        this.scribble = scribble;
        this.slideNam = slideNam;
        this.slideTyp = slideTyp;
        this.slideUrl = slideUrl;
        this.timing=timing;
        this.slideComments=slideComments;
        this.remTime=remTime;
    }

    public StoreImageTypeUrl(String slideNam, String slideTyp, String slideUrl, String timing, String slideComments, String remTime, int columnid, String rating)
    {
        this.slideNam = slideNam;
        this.slideTyp = slideTyp;
        this.slideUrl = slideUrl;
        this.timing=timing;
        this.slideComments=slideComments;
        this.remTime=remTime;
        this.columnid=columnid;
        this.rating=rating;
    }

    public StoreImageTypeUrl(String scribble, String slideNam, String slideTyp, String slideUrl, String timing, String slideComments, String remTime, String brdName,String brandCode, boolean xx)
    {
        this.scribble = scribble;
        this.slideNam = slideNam;
        this.slideTyp = slideTyp;
        this.slideUrl = slideUrl;
        this.timing=timing;
        this.slideComments=slideComments;
        this.remTime=remTime;
        this.brdName=brdName;
        this.brdCode = brandCode;
    }

    public StoreImageTypeUrl(String scribble, String slideNam, String slideTyp, String slideUrl, String timing, String slideid, String brdName, String brdCode)
    {
        this.scribble = scribble;
        this.slideNam = slideNam;
        this.slideTyp = slideTyp;
        this.slideUrl = slideUrl;
        this.timing=timing;
        this.slideid=slideid;
        this.brdName=brdName;
        this.brdCode=brdCode;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public File getFfile() {
        return ffile;
    }

    public void setFfile(File ffile) {
        this.ffile = ffile;
    }

    public int getColumnid() {
        return columnid;
    }

    public void setColumnid(int columnid) {
        this.columnid = columnid;
    }

    public String getRemTime() {
        return remTime;
    }

    public void setRemTime(String remTime) {
        this.remTime = remTime;
    }

    public String getSlideid() {
        return slideid;
    }

    public void setSlideid(String slideid) {
        this.slideid = slideid;
    }

    public String getBrdName() {
        return brdName;
    }

    public void setBrdName(String brdName) {
        this.brdName = brdName;
    }

    public String getBrdCode() {
        return brdCode;
    }

    public void setBrdCode(String brdCode) {
        this.brdCode = brdCode;
    }

    public String getSlideNam() {
        return slideNam;
    }

    public void setSlideNam(String slideNam) {
        this.slideNam = slideNam;
    }

    public String getSlideTyp() {
        return slideTyp;
    }

    public void setSlideTyp(String slideTyp) {
        this.slideTyp = slideTyp;
    }

    public String getSlideUrl() {
        return slideUrl;
    }

    public void setSlideUrl(String slideUrl) {
        this.slideUrl = slideUrl;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

}

