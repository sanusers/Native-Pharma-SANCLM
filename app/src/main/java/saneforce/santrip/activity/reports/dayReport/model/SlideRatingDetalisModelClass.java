package saneforce.santrip.activity.reports.dayReport.model;

public class SlideRatingDetalisModelClass {

    private String Activity_Report_code;
    private String MSL_code;
    private String Product_Code;
    private String Product_Name;
    private String StartTime;
    private String EndTime;
    private String file_type;
    private String   DDSl_No;
    private String   Rating;
    private String   Feedbk;


    public String getActivity_Report_code() {
        return Activity_Report_code;
    }

    public void setActivity_Report_code(String activity_Report_code) {
        Activity_Report_code = activity_Report_code;
    }

    public String getMSL_code() {
        return MSL_code;
    }

    public void setMSL_code(String MSL_code) {
        this.MSL_code = MSL_code;
    }

    public String getProduct_Code() {
        return Product_Code;
    }

    public void setProduct_Code(String product_Code) {
        Product_Code = product_Code;
    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getDDSl_No() {
        return DDSl_No;
    }

    public void setDDSl_No(String DDSl_No) {
        this.DDSl_No = DDSl_No;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getFeedbk() {
        return Feedbk;
    }

    public void setFeedbk(String feedbk) {
        Feedbk = feedbk;
    }
}
