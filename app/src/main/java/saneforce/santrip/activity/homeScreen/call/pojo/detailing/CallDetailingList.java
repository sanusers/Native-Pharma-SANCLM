package saneforce.santrip.activity.homeScreen.call.pojo.detailing;

import java.util.ArrayList;
import java.util.Comparator;

public class CallDetailingList {
    String brandName;
    String brandCode;

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    String st_end_time;
    String slideName;

    public String getSlideUrl() {
        return slideUrl;
    }

    public void setSlideUrl(String slideUrl) {
        this.slideUrl = slideUrl;
    }

    String slideUrl;
    int rating;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    String feedback, date;
    String sample;
    String remTiming;
    String rxQty;
    String stk_name;
    String error;
    String slideType;

    public String getSlideType() {
        return slideType;
    }

    public void setSlideType(String slideType) {
        this.slideType = slideType;
    }

    public ArrayList<PopFeed> getProdFb() {
        return prodFb;
    }

    public CallDetailingList(String brandName, String brandCode, String slideName,String slideType,String slideUrl, String timing, int rating, String feedback, String currentDate) {
        this.brandName = brandName;
        this.brandCode = brandCode;
        this.slideName = slideName;
        this.slideType = slideType;
        this.slideUrl = slideUrl;
        this.st_end_time = timing;
        this.rating = rating;
        this.feedback = feedback;
        this.date = currentDate;
    }

    public CallDetailingList(String brandName, String slideName,String slideType,String slideUrl, String timing, int rating, String feedback, String currentDate) {
        this.brandName = brandName;
        this.slideName = slideName;
        this.slideType = slideType;
        this.slideUrl = slideUrl;
        this.st_end_time = timing;
        this.rating = rating;
        this.feedback = feedback;
        this.date = currentDate;
    }

    public static class StoreImageComparator implements Comparator<CallDetailingList>
    {
        public int compare(CallDetailingList left, CallDetailingList right)
        {
            return left.brandName.compareTo(right.brandName);
        }
    }



    public String getPrdfdbck() {
        return prdfdbck;
    }

    public void setPrdfdbck(String prdfdbck) {
        this.prdfdbck = prdfdbck;
    }

    String prdfdbck;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    String productCode;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStk_code() {
        return stk_code;
    }

    public void setStk_code(String stk_code) {
        this.stk_code = stk_code;
    }

    String stk_code;
    ArrayList<PopFeed> prodFb;


    public void setProdFb(ArrayList<PopFeed> prodFb) {
        this.prodFb = prodFb;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemTiming() {
        return remTiming;
    }

    public void setRemTiming(String remTiming) {
        this.remTiming = remTiming;
    }

    public CallDetailingList(String brandName, String st_end_time, int rating, String feedback, String date, String sample, String remTiming, String rxQty, ArrayList<PopFeed> prodFb, String stk_name, String stkcode) {
        this.brandName = brandName;
        this.st_end_time = st_end_time;
        this.rating = rating;
        this.feedback = feedback;
        this.date = date;
        this.sample = sample;
        this.remTiming = remTiming;
        this.rxQty = rxQty;
        this.prodFb = prodFb;
        this.stk_name = stk_name;
        this.stk_code = stkcode;
    }

    public CallDetailingList(String brandName, String ProductCode, String st_end_time, int rating, String feedback, String date, String sample, String remTiming, String rxQty, ArrayList<PopFeed> prodFb, String stk_name, String stkcode) {
        this.brandName = brandName;
        this.productCode = ProductCode;
        this.st_end_time = st_end_time;
        this.rating = rating;
        this.feedback = feedback;
        this.date = date;
        this.sample = sample;
        this.remTiming = remTiming;
        this.rxQty = rxQty;
        this.prodFb = prodFb;
        this.stk_name = stk_name;
        this.stk_code = stkcode;
    }

    public CallDetailingList(String brandName, String rxQty, String productCode, String bool) {
        this.brandName = brandName;
        this.rxQty = rxQty;
        this.productCode = productCode;
    }

    public CallDetailingList(String sample, String feedback, int rating) {
        this.sample = sample;
        this.feedback = feedback;
        this.rating = rating;
    }

  /*  public FeedbackProductDetail(String prdNAme, String st_end_time,String slideName) {
        this.prdNAme = prdNAme;
        this.st_end_time = st_end_time;
        this.slideName=slideName;
    }*/

    public CallDetailingList(String brandName) {
        this.brandName = brandName;
    }

    @Override
    public boolean equals(Object obj) {
        CallDetailingList dt = (CallDetailingList) obj;
        if (brandName == null)
            return false;
        if (dt.brandName.equals(brandName))
            return true;
        return false;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSt_end_time() {
        return st_end_time;
    }

    public void setSt_end_time(String st_end_time) {
        this.st_end_time = st_end_time;
    }

    public String getSlideName() {
        return slideName;
    }

    public void setSlideName(String slideName) {
        this.slideName = slideName;
    }


    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getRxQty() {
        return rxQty;
    }

    public void setRxQty(String rxQty) {
        this.rxQty = rxQty;
    }

    public String getStk_name() {
        return stk_name;
    }

    public void setStk_name(String stk_name) {
        this.stk_name = stk_name;
    }
}

