package saneforce.sanzen.activity.call.profile.preCallAnalysis;

public class PreCallAnalysisModelClass {


    public String Productname;
    public String Sample;
    public String Rx;
    public String Rcpa;
    public String FeedBack;

    public PreCallAnalysisModelClass(String productname, String sample, String rx, String rcpa, String feedBack) {
        Productname = productname;
        Sample = sample;
        Rx = rx;
        Rcpa = rcpa;
        FeedBack = feedBack;
    }

    public String getProductname() {
        return Productname;
    }

    public void setProductname(String productname) {
        Productname = productname;
    }

    public String getSample() {
        return Sample;
    }

    public void setSample(String sample) {
        Sample = sample;
    }

    public String getRx() {
        return Rx;
    }

    public void setRx(String rx) {
        Rx = rx;
    }

    public String getRcpa() {
        return Rcpa;
    }

    public void setRcpa(String rcpa) {
        Rcpa = rcpa;
    }

    public String getFeedBack() {
        return FeedBack;
    }

    public void setFeedBack(String feedBack) {
        FeedBack = feedBack;
    }
}


