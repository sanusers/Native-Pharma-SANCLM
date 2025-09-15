package saneforce.sanzen.activity.reports.dayReport.model;

public class SignatureModelClass {
    String sf_code,SignImg;

    public SignatureModelClass(String sf_code, String signImg) {
        this.sf_code = sf_code;
        SignImg = signImg;
    }

    public String getSf_code() {
        return sf_code;
    }

    public void setSf_code(String sf_code) {
        this.sf_code = sf_code;
    }

    public String getSignImg() {
        return SignImg;
    }

    public void setSignImg(String signImg) {
        SignImg = signImg;
    }
}
