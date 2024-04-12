package saneforce.santrip.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomSetupResponse implements Serializable {
    @SerializedName("hosp_filter")
    private String HospNeed;
    @SerializedName("addDr")
    private String AddDoctor;
    @SerializedName("showDelete")
    private String ShowDelete;
    @SerializedName("Detailing_chem")
    private String DetailingChemSkip;
    @SerializedName("Detailing_stk")
    private String DetailingStkSkip;
    @SerializedName("Detailing_undr")
    private String DetailingUndrSkip;
    @SerializedName("addChm")
    private String AddChemist;
    @SerializedName("DcrapprvNd")
    private String DCRApprovedNeed;
    @SerializedName("Product_Stockist")
    private String StockistPrdNeed;
    @SerializedName("undr_hs_nd")
    private String UndrHsNeed;
    @SerializedName("Target_sales")
    private String TargetSalesNeed;
    @SerializedName("yetrdy_call_del_Nd")
    private String CallDCRDirectly;
    @SerializedName("addAct")
    private String ActivityNeed;

    public String getRCPAWOSample() {
        return RCPAWOSample;
    }

    public void setRCPAWOSample(String RCPAWOSample) {
        this.RCPAWOSample = RCPAWOSample;
    }

    @SerializedName("Alba_Nd")
    private String RCPAWOSample;
    @SerializedName("PresentNd")
    private String PresentationNeed;
    @SerializedName("CustNd")
    private String CustomizationPrsNeed;
    @SerializedName("theraptic")
    private String TherapaticNeed;
    @SerializedName("addCoach")
    private String CallCoachingFormNeed;
    @SerializedName("DayCoach_Md")
    private String DayCoachingFormNeed;
    @SerializedName("Detailing_rpt")
    private String DetailingReportNeed;
    @SerializedName("productFB")
    private String DrFeedbackNeed;
    @SerializedName("DrProfile")
    private String DrProfileNeed;
    @SerializedName("Product_Stockist_Md")
    private String DrPrdStkMandatory;
    @SerializedName("Tourplan_MGR")
    private String MgrTourPlan;
    @SerializedName("addAct_Md")
    private String ActivityMandatory;
    @SerializedName("add_dashboard")
    private String AddDashboardNeed;
    @SerializedName("Pob_Stockist_Nd")
    private String StockistPobNeed;
    @SerializedName("Pob_Unlstdr_Nd")
    private String UndrPobNeed;
    @SerializedName("Pob_Stockist_Mandatory_Need")
    private String StockistPobMandatory;
    @SerializedName("Pob_Unlstdr_Mandatory_Need")
    private String UndrPobMandatory;

    public String getAddDoctor() {
        return AddDoctor;
    }

    public void setAddDoctor(String addDoctor) {
        AddDoctor = addDoctor;
    }

    public String getShowDelete() {
        return ShowDelete;
    }

    public void setShowDelete(String showDelete) {
        ShowDelete = showDelete;
    }

    public String getDetailingChemSkip() {
        return DetailingChemSkip;
    }

    public void setDetailingChemSkip(String detailingChemSkip) {
        DetailingChemSkip = detailingChemSkip;
    }

    public String getDetailingStkSkip() {
        return DetailingStkSkip;
    }

    public void setDetailingStkSkip(String detailingStkSkip) {
        DetailingStkSkip = detailingStkSkip;
    }

    public String getDetailingUndrSkip() {
        return DetailingUndrSkip;
    }

    public void setDetailingUndrSkip(String detailingUndrSkip) {
        DetailingUndrSkip = detailingUndrSkip;
    }

    public String getAddChemist() {
        return AddChemist;
    }

    public void setAddChemist(String addChemist) {
        AddChemist = addChemist;
    }

    public String getDCRApprovedNeed() {
        return DCRApprovedNeed;
    }

    public void setDCRApprovedNeed(String DCRApprovedNeed) {
        this.DCRApprovedNeed = DCRApprovedNeed;
    }

    public String getStockistPrdNeed() {
        return StockistPrdNeed;
    }

    public void setStockistPrdNeed(String stockistPrdNeed) {
        StockistPrdNeed = stockistPrdNeed;
    }

    public String getUndrHsNeed() {
        return UndrHsNeed;
    }

    public void setUndrHsNeed(String undrHsNeed) {
        UndrHsNeed = undrHsNeed;
    }

    public String getTargetSalesNeed() {
        return TargetSalesNeed;
    }

    public void setTargetSalesNeed(String targetSalesNeed) {
        TargetSalesNeed = targetSalesNeed;
    }

    public String getCallDCRDirectly() {
        return CallDCRDirectly;
    }

    public void setCallDCRDirectly(String callDCRDirectly) {
        CallDCRDirectly = callDCRDirectly;
    }

    public String getActivityNeed() {
        return ActivityNeed;
    }

    public void setActivityNeed(String activityNeed) {
        ActivityNeed = activityNeed;
    }

    public String getPresentationNeed() {
        return PresentationNeed;
    }

    public void setPresentationNeed(String presentationNeed) {
        PresentationNeed = presentationNeed;
    }

    public String getCustomizationPrsNeed() {
        return CustomizationPrsNeed;
    }

    public void setCustomizationPrsNeed(String customizationPrsNeed) {
        CustomizationPrsNeed = customizationPrsNeed;
    }

    public String getTherapaticNeed() {
        return TherapaticNeed;
    }

    public void setTherapaticNeed(String therapaticNeed) {
        TherapaticNeed = therapaticNeed;
    }

    public String getCallCoachingFormNeed() {
        return CallCoachingFormNeed;
    }

    public void setCallCoachingFormNeed(String callCoachingFormNeed) {
        CallCoachingFormNeed = callCoachingFormNeed;
    }

    public String getDayCoachingFormNeed() {
        return DayCoachingFormNeed;
    }

    public void setDayCoachingFormNeed(String dayCoachingFormNeed) {
        DayCoachingFormNeed = dayCoachingFormNeed;
    }

    public String getDetailingReportNeed() {
        return DetailingReportNeed;
    }

    public void setDetailingReportNeed(String detailingReportNeed) {
        DetailingReportNeed = detailingReportNeed;
    }

    public String getDrFeedbackNeed() {
        return DrFeedbackNeed;
    }

    public void setDrFeedbackNeed(String drFeedbackNeed) {
        DrFeedbackNeed = drFeedbackNeed;
    }

    public String getDrProfileNeed() {
        return DrProfileNeed;
    }

    public void setDrProfileNeed(String drProfileNeed) {
        DrProfileNeed = drProfileNeed;
    }

    public String getDrPrdStkMandatory() {
        return DrPrdStkMandatory;
    }

    public void setDrPrdStkMandatory(String drPrdStkMandatory) {
        DrPrdStkMandatory = drPrdStkMandatory;
    }

    public String getMgrTourPlan() {
        return MgrTourPlan;
    }

    public void setMgrTourPlan(String mgrTourPlan) {
        MgrTourPlan = mgrTourPlan;
    }

    public String getActivityMandatory() {
        return ActivityMandatory;
    }

    public void setActivityMandatory(String activityMandatory) {
        ActivityMandatory = activityMandatory;
    }

    public String getAddDashboardNeed() {
        return AddDashboardNeed;
    }

    public void setAddDashboardNeed(String addDashboardNeed) {
        AddDashboardNeed = addDashboardNeed;
    }

    public String getStockistPobNeed() {
        return StockistPobNeed;
    }

    public void setStockistPobNeed(String stockistPobNeed) {
        StockistPobNeed = stockistPobNeed;
    }

    public String getUndrPobNeed() {
        return UndrPobNeed;
    }

    public void setUndrPobNeed(String undrPobNeed) {
        UndrPobNeed = undrPobNeed;
    }

    public String getStockistPobMandatory() {
        return StockistPobMandatory;
    }

    public void setStockistPobMandatory(String stockistPobMandatory) {
        StockistPobMandatory = stockistPobMandatory;
    }

    public String getUndrPobMandatory() {
        return UndrPobMandatory;
    }

    public void setUndrPobMandatory(String undrPobMandatory) {
        UndrPobMandatory = undrPobMandatory;
    }

    public String getHospNeed() {
        return HospNeed;
    }

    public void setHospNeed(String hospNeed) {
        this.HospNeed = hospNeed;
    }
}
