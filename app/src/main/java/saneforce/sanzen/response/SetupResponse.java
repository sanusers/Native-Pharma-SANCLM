package saneforce.sanzen.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SetupResponse implements Serializable {

    @SerializedName("GeoNeed")
    private String GeoNeed;

    @SerializedName("GeoChk")
    private String GeoCheck;
    @SerializedName("ChmNeed")
    private String ChemistNeed;
    @SerializedName("StkNeed")
    private String StockistNeed;
    @SerializedName("UNLNeed")
    private String UnDrNeed;
    @SerializedName("DPNeed")
    private String DrPrdNeed;
    @SerializedName("DINeed")
    private String DrInpNeed;
    @SerializedName("CPNeed")
    private String ChePrdNeed;
    @SerializedName("CINeed")
    private String CheInpNeed;
    @SerializedName("SPNeed")
    private String StkPrdNeed;
    @SerializedName("SINeed")
    private String StkInpNeed;
    @SerializedName("NPNeed")
    private String UndrPrdNeed;
    @SerializedName("NINeed")
    private String UnDrInpNeed;
    @SerializedName("DrCap")
    private String CaptionDr;
    @SerializedName("ChmCap")
    private String CaptionChemist;
    @SerializedName("StkCap")
    private String CaptionStockist;
    @SerializedName("NLCap")
    private String CaptionUndr;
    @SerializedName("Doc_Product_caption")
    private String CaptionDrPrd;
    @SerializedName("Chm_Product_caption")
    private String CaptionChemistPrd;
    @SerializedName("Stk_Product_caption")
    private String CaptionStokistPrd;
    @SerializedName("Ul_Product_caption")
    private String CaptionUndrPrd;
    @SerializedName("Doc_Input_caption")
    private String CaptionDrInp;
    @SerializedName("Chm_Input_caption")
    private String CaptionChemistInp;
    @SerializedName("Stk_Input_caption")
    private String CaptionStokistInp;
    @SerializedName("Ul_Input_caption")
    private String CaptionUndrInp;
    @SerializedName("Doc_jointwork_Need")
    private String DrJwNeed;
    @SerializedName("Chm_jointwork_Need")
    private String ChemistJwNeed;
    @SerializedName("Stk_jointwork_Need")
    private String StockistJwNeed;
    @SerializedName("Ul_jointwork_Need")
    private String UndrJwNeed;
    @SerializedName("ChmSmpCap")
    private String CaptionChemistSamQty;
    @SerializedName("ChmRcpaMd")
    private String ChemistRCPAMandatory;
    @SerializedName("DrRxQCap")
    private String CaptionDrRxQty;
    @SerializedName("DrSmpQCap")
    private String CaptionDrSamQty;
    @SerializedName("ChmQCap")
    private String CaptionChemistRxQty;
    @SerializedName("StkQCap")
    private String CaptionStkRxQty;
    @SerializedName("RcpaMd_Mgr")
    private String MGRDrRcpaMandatory;
    @SerializedName("Cluster_Cap")
    private String CaptionCluster;
    @SerializedName("ChmRcpaMd_Mgr")
    private String MGRCheRcpaMandatory;
    @SerializedName("DrEvent_Md")
    private String DrEveCapMandatory;
    @SerializedName("ChmEvent_Md")
    private String ChemistEveCapMandatory;
    @SerializedName("StkEvent_Md")
    private String StockistEveCapMandatory;
    @SerializedName("UlDrEvent_Md")
    private String UndrEveCapMandatory;
    @SerializedName("NLRxQCap")
    private String CaptionUndrRxQty;
    @SerializedName("NLSmpQCap")
    private String CaptionUndrSamQty;
    @SerializedName("DrRxNd")
    private String DrRxNeed;
    @SerializedName("DrSampNd")
    private String DrSamNeed;
    @SerializedName("DrRxQMd")
    private String DrRxQtyMandatory;
    @SerializedName("DrSmpQMd")
    private String DrSamQtyMandatory;
    @SerializedName("SrtNd")
    private String CheckInOutNeed;
    @SerializedName("deneed")
    private String DrEveCapNeed;
    @SerializedName("ceneed")
    private String ChemistEveCapNeed;
    @SerializedName("seneed")
    private String StockistEveCapNeed;
    @SerializedName("neneed")
    private String UndrEveCapNeed;
    @SerializedName("DrPrdMd")
    private String DrPrdMandatory;
    @SerializedName("DrInpMd")
    private String DrInpMandatory;
    @SerializedName("RcpaNd")
    private String DrRcpaNeed;
    @SerializedName("Chm_RCPA_Need")
    private String ChemistRcpaNeed;
    @SerializedName("GEOTagNeed")
    private String DrGeoTagNeed;
    @SerializedName("GEOTagNeedche")
    private String ChemistGeoTagNeed;
    @SerializedName("GEOTagNeedstock")
    private String StockistGeoTagNeed;
    @SerializedName("GeoTagNeedcip")
    private String CipGeoTagNeed;
    @SerializedName("GEOTagNeedunlst")
    private String UndrGeoTagNeed;
    @SerializedName("DisRad")
    private String MapGeoFenceCircleRad;
    @SerializedName("geoTagImg")
    private String GeoTagImageNeed;
    @SerializedName("DeviceRegId")
    private String DevRegId;
    @SerializedName("MCLDet")
    private String ProfileMclDetailing;
    @SerializedName("NActivityNeed")
    private String PresentationNActNeed;
    @SerializedName("SpecFilter")
    private String PresentationSpecFilter;
    @SerializedName("ChmRxNd")
    private String ChemistRxNeed;
    @SerializedName("addDr")
    private String AddingDr;
    @SerializedName("showDelete")
    private String ShowDelOptionDcr;
    @SerializedName("Detailing_chem")
    private String ChemDetailingSkip;
    @SerializedName("Detailing_stk")
    private String StockistDetailingSkip;
    @SerializedName("Detailing_undr")
    private String UndrDetailingSkip;
    @SerializedName("addChm")
    private String AddingChemist;
    @SerializedName("Stk_Pob_Need")
    private String StockistRxNeed;
    @SerializedName("Ul_Pob_Need")
    private String UndrRxNeed;
    @SerializedName("tp_need")
    private String TpNeed;
    @SerializedName("SurveyNd")
    private String SurveyNeed;
    @SerializedName("past_leave_post")
    private String PastLeavePost;
    @SerializedName("RcpaMd")
    private String RcpaMandatory;
    @SerializedName("quiz_need")
    private String QuizNeed;
    @SerializedName("quiz_need_mandt")
    private String QuizMandatory;
    @SerializedName("MissedDateMand")
    private String MissedDateMandatory;
    @SerializedName("TPDCR_Deviation")
    private String TpDcrDeviation;
    @SerializedName("TPbasedDCR")
    private String TpBasedDcr;
    @SerializedName("TP_Mandatory_Need")
    private String TpMandatoryNeed;
    @SerializedName("Tp_Start_Date")
    private String TpStartDate;
    @SerializedName("Tp_End_Date")
    private String TpEndDate;
    @SerializedName("chmsamQty_need")
    private String ChemistSamNeed;
    @SerializedName("Doc_jointwork_Mandatory_Need")
    private String DrJwMandatory;
    @SerializedName("Chm_jointwork_Mandatory_Need")
    private String ChemistJwMandatory;
    @SerializedName("stk_jointwork_Mandatory_Need")
    private String StockistJwMandatory;
    @SerializedName("Ul_jointwork_Mandatory_Need")
    private String UndrJwMandatory;
    @SerializedName("CIP_ENeed")
    private String DetailingCipSkip;
    @SerializedName("DrFeedMd")
    private String DrFeedbackMandatory;
    @SerializedName("DFNeed")
    private String DrFeedbackNeed;
    @SerializedName("CFNeed")
    private String ChemistFeedbackNeed;
    @SerializedName("SFNeed")
    private String StockistFeedbackNeed;
    @SerializedName("NFNeed")
    private String UndrFeedbackNeed;
    @SerializedName("CIP_FNeed")
    private String CipFeedbackNeed;
    @SerializedName("Doc_Pob_Need")
    private String DrPobNeed;
    @SerializedName("Doc_Pob_Mandatory_Need")
    private String DrPobMandatory;
    @SerializedName("Chm_Pob_Need")
    private String ChemistPobNeed;
    @SerializedName("Chm_Pob_Mandatory_Need")
    private String ChemistPobMandatory;
    @SerializedName("Stk_Pob_Mandatory_Need")
    private String StockistPobMandatory;
    @SerializedName("Ul_Pob_Mandatory_Need")
    private String UndrPobMandatory;
    @SerializedName("CIPPOBNd")
    private String CipPobNeed;
    @SerializedName("CIPPOBMd")
    private String CipPobMandatory;
    @SerializedName("TempNd")
    private String DrRemarksMan_Need;
    @SerializedName("Target_report_Nd")
    private String SalesTargetReportNeed;
    @SerializedName("vstnd")
    private String VisitControl;
    @SerializedName("multi_cluster")
    private String MultiCluster;
    @SerializedName("sample_validation")
    private String SampleValidation;
    @SerializedName("input_validation")
    private String InputValidation;
    @SerializedName("Leave_entitlement_need")
    private String LeaveEntitlementNeed;
    @SerializedName("RmdrNeed")
    private String ReminderCallNeed;
    @SerializedName("Remainder_prd_Md")
    private String ReminderPrdMandatory;
    @SerializedName("cip_need")
    private String CIPNeed;
    @SerializedName("CIP_Caption")
    private String CaptionCip;
    @SerializedName("GeoTagApprovalNeed")
    private String GeoTagApprovalNeed;

    public String getGeoTagImageNeed() {
        return GeoTagImageNeed;
    }

    public void setGeoTagImageNeed(String geoTagImageNeed) {
        GeoTagImageNeed = geoTagImageNeed;
    }

    public String getCIPNeed() {
        return CIPNeed;
    }

    public void setCIPNeed(String CIPNeed) {
        this.CIPNeed = CIPNeed;
    }

    public String getCaptionCip() {
        return CaptionCip;
    }

    public void setCaptionCip(String captionCip) {
        CaptionCip = captionCip;
    }

    public String getGeoNeed() {
        return GeoNeed;
    }

    public void setGeoNeed(String geoNeed) {
        GeoNeed = geoNeed;
    }

    public String getGeoCheck() {
        return GeoCheck;
    }

    public void setGeoCheck(String geoCheck) {
        GeoCheck = geoCheck;
    }

    public String getChemistNeed() {
        return ChemistNeed;
    }

    public void setChemistNeed(String chemistNeed) {
        ChemistNeed = chemistNeed;
    }

    public String getStockistNeed() {
        return StockistNeed;
    }

    public void setStockistNeed(String stockistNeed) {
        StockistNeed = stockistNeed;
    }

    public String getUnDrNeed() {
        return UnDrNeed;
    }

    public void setUnDrNeed(String unDrNeed) {
        UnDrNeed = unDrNeed;
    }

    public String getDrPrdNeed() {
        return DrPrdNeed;
    }

    public void setDrPrdNeed(String drPrdNeed) {
        DrPrdNeed = drPrdNeed;
    }

    public String getDrInpNeed() {
        return DrInpNeed;
    }

    public void setDrInpNeed(String drInpNeed) {
        DrInpNeed = drInpNeed;
    }

    public String getChePrdNeed() {
        return ChePrdNeed;
    }

    public void setChePrdNeed(String chePrdNeed) {
        ChePrdNeed = chePrdNeed;
    }

    public String getCheInpNeed() {
        return CheInpNeed;
    }

    public void setCheInpNeed(String cheInpNeed) {
        CheInpNeed = cheInpNeed;
    }

    public String getStkPrdNeed() {
        return StkPrdNeed;
    }

    public void setStkPrdNeed(String stkPrdNeed) {
        StkPrdNeed = stkPrdNeed;
    }

    public String getStkInpNeed() {
        return StkInpNeed;
    }

    public void setStkInpNeed(String stkInpNeed) {
        StkInpNeed = stkInpNeed;
    }

    public String getUndrPrdNeed() {
        return UndrPrdNeed;
    }

    public void setUndrPrdNeed(String undrPrdNeed) {
        UndrPrdNeed = undrPrdNeed;
    }

    public String getUnDrInpNeed() {
        return UnDrInpNeed;
    }

    public void setUnDrInpNeed(String unDrInpNeed) {
        UnDrInpNeed = unDrInpNeed;
    }

    public String getCaptionChemist() {
        return CaptionChemist;
    }

    public void setCaptionChemist(String captionChemist) {
        CaptionChemist = captionChemist;
    }

    public String getCaptionStockist() {
        return CaptionStockist;
    }

    public void setCaptionStockist(String captionStockist) {
        CaptionStockist = captionStockist;
    }

    public String getCaptionUndr() {
        return CaptionUndr;
    }

    public void setCaptionUndr(String captionUndr) {
        CaptionUndr = captionUndr;
    }

    public String getCaptionDrPrd() {
        return CaptionDrPrd;
    }

    public void setCaptionDrPrd(String captionDrPrd) {
        CaptionDrPrd = captionDrPrd;
    }

    public String getCaptionChemistPrd() {
        return CaptionChemistPrd;
    }

    public void setCaptionChemistPrd(String captionChemistPrd) {
        CaptionChemistPrd = captionChemistPrd;
    }

    public String getCaptionStokistPrd() {
        return CaptionStokistPrd;
    }

    public void setCaptionStokistPrd(String captionStokistPrd) {
        CaptionStokistPrd = captionStokistPrd;
    }

    public String getCaptionUndrPrd() {
        return CaptionUndrPrd;
    }

    public void setCaptionUndrPrd(String captionUndrPrd) {
        CaptionUndrPrd = captionUndrPrd;
    }

    public String getCaptionDrInp() {
        return CaptionDrInp;
    }

    public void setCaptionDrInp(String captionDrInp) {
        CaptionDrInp = captionDrInp;
    }

    public String getCaptionChemistInp() {
        return CaptionChemistInp;
    }

    public void setCaptionChemistInp(String captionChemistInp) {
        CaptionChemistInp = captionChemistInp;
    }

    public String getCaptionStokistInp() {
        return CaptionStokistInp;
    }

    public void setCaptionStokistInp(String captionStokistInp) {
        CaptionStokistInp = captionStokistInp;
    }

    public String getCaptionUndrInp() {
        return CaptionUndrInp;
    }

    public void setCaptionUndrInp(String captionUndrInp) {
        CaptionUndrInp = captionUndrInp;
    }

    public String getDrJwNeed() {
        return DrJwNeed;
    }

    public void setDrJwNeed(String drJwNeed) {
        DrJwNeed = drJwNeed;
    }

    public String getChemistJwNeed() {
        return ChemistJwNeed;
    }

    public void setChemistJwNeed(String chemistJwNeed) {
        ChemistJwNeed = chemistJwNeed;
    }

    public String getStockistJwNeed() {
        return StockistJwNeed;
    }

    public void setStockistJwNeed(String stockistJwNeed) {
        StockistJwNeed = stockistJwNeed;
    }

    public String getUndrJwNeed() {
        return UndrJwNeed;
    }

    public void setUndrJwNeed(String undrJwNeed) {
        UndrJwNeed = undrJwNeed;
    }

    public String getCaptionChemistSamQty() {
        return CaptionChemistSamQty;
    }

    public void setCaptionChemistSamQty(String captionChemistSamQty) {
        CaptionChemistSamQty = captionChemistSamQty;
    }

    public String getChemistRCPAMandatory() {
        return ChemistRCPAMandatory;
    }

    public void setChemistRCPAMandatory(String chemistRCPAMandatory) {
        ChemistRCPAMandatory = chemistRCPAMandatory;
    }

    public String getCaptionDrRxQty() {
        return CaptionDrRxQty;
    }

    public void setCaptionDrRxQty(String captionDrRxQty) {
        CaptionDrRxQty = captionDrRxQty;
    }

    public String getCaptionDrSamQty() {
        return CaptionDrSamQty;
    }

    public void setCaptionDrSamQty(String captionDrSamQty) {
        CaptionDrSamQty = captionDrSamQty;
    }

    public String getCaptionChemistRxQty() {
        return CaptionChemistRxQty;
    }

    public void setCaptionChemistRxQty(String captionChemistRxQty) {
        CaptionChemistRxQty = captionChemistRxQty;
    }

    public String getCaptionStkRxQty() {
        return CaptionStkRxQty;
    }

    public void setCaptionStkRxQty(String captionStkRxQty) {
        CaptionStkRxQty = captionStkRxQty;
    }

    public String getMGRDrRcpaMandatory() {
        return MGRDrRcpaMandatory;
    }

    public void setMGRDrRcpaMandatory(String MGRDrRcpaMandatory) {
        this.MGRDrRcpaMandatory = MGRDrRcpaMandatory;
    }

    public String getCaptionCluster() {
        return CaptionCluster;
    }

    public void setCaptionCluster(String captionCluster) {
        CaptionCluster = captionCluster;
    }

    public String getMGRCheRcpaMandatory() {
        return MGRCheRcpaMandatory;
    }

    public void setMGRCheRcpaMandatory(String MGRCheRcpaMandatory) {
        this.MGRCheRcpaMandatory = MGRCheRcpaMandatory;
    }

    public String getDrEveCapMandatory() {
        return DrEveCapMandatory;
    }

    public void setDrEveCapMandatory(String drEveCapMandatory) {
        DrEveCapMandatory = drEveCapMandatory;
    }

    public String getChemistEveCapMandatory() {
        return ChemistEveCapMandatory;
    }

    public void setChemistEveCapMandatory(String chemistEveCapMandatory) {
        ChemistEveCapMandatory = chemistEveCapMandatory;
    }

    public String getStockistEveCapMandatory() {
        return StockistEveCapMandatory;
    }

    public void setStockistEveCapMandatory(String stockistEveCapMandatory) {
        StockistEveCapMandatory = stockistEveCapMandatory;
    }

    public String getUndrEveCapMandatory() {
        return UndrEveCapMandatory;
    }

    public void setUndrEveCapMandatory(String undrEveCapMandatory) {
        UndrEveCapMandatory = undrEveCapMandatory;
    }

    public String getCaptionUndrRxQty() {
        return CaptionUndrRxQty;
    }

    public void setCaptionUndrRxQty(String captionUndrRxQty) {
        CaptionUndrRxQty = captionUndrRxQty;
    }

    public String getCaptionUndrSamQty() {
        return CaptionUndrSamQty;
    }

    public void setCaptionUndrSamQty(String captionUndrSamQty) {
        CaptionUndrSamQty = captionUndrSamQty;
    }

    public String getDrRxNeed() {
        return DrRxNeed;
    }

    public void setDrRxNeed(String drRxNeed) {
        DrRxNeed = drRxNeed;
    }

    public String getDrSamNeed() {
        return DrSamNeed;
    }

    public void setDrSamNeed(String drSamNeed) {
        DrSamNeed = drSamNeed;
    }

    public String getDrRxQtyMandatory() {
        return DrRxQtyMandatory;
    }

    public void setDrRxQtyMandatory(String drRxQtyMandatory) {
        DrRxQtyMandatory = drRxQtyMandatory;
    }

    public String getDrSamQtyMandatory() {
        return DrSamQtyMandatory;
    }

    public void setDrSamQtyMandatory(String drSamQtyMandatory) {
        DrSamQtyMandatory = drSamQtyMandatory;
    }

    public String getCheckInOutNeed() {
        return CheckInOutNeed;
    }

    public void setCheckInOutNeed(String checkInOutNeed) {
        CheckInOutNeed = checkInOutNeed;
    }

    public String getDrEveCapNeed() {
        return DrEveCapNeed;
    }

    public void setDrEveCapNeed(String drEveCapNeed) {
        DrEveCapNeed = drEveCapNeed;
    }

    public String getChemistEveCapNeed() {
        return ChemistEveCapNeed;
    }

    public void setChemistEveCapNeed(String chemistEveCapNeed) {
        ChemistEveCapNeed = chemistEveCapNeed;
    }

    public String getStockistEveCapNeed() {
        return StockistEveCapNeed;
    }

    public void setStockistEveCapNeed(String stockistEveCapNeed) {
        StockistEveCapNeed = stockistEveCapNeed;
    }

    public String getUndrEveCapNeed() {
        return UndrEveCapNeed;
    }

    public void setUndrEveCapNeed(String undrEveCapNeed) {
        UndrEveCapNeed = undrEveCapNeed;
    }

    public String getDrPrdMandatory() {
        return DrPrdMandatory;
    }

    public void setDrPrdMandatory(String drPrdMandatory) {
        DrPrdMandatory = drPrdMandatory;
    }

    public String getDrInpMandatory() {
        return DrInpMandatory;
    }

    public void setDrInpMandatory(String drInpMandatory) {
        DrInpMandatory = drInpMandatory;
    }

    public String getDrRcpaNeed() {
        return DrRcpaNeed;
    }

    public void setDrRcpaNeed(String drRcpaNeed) {
        DrRcpaNeed = drRcpaNeed;
    }

    public String getChemistRcpaNeed() {
        return ChemistRcpaNeed;
    }

    public void setChemistRcpaNeed(String chemistRcpaNeed) {
        ChemistRcpaNeed = chemistRcpaNeed;
    }

    public String getDrGeoTagNeed() {
        return DrGeoTagNeed;
    }

    public void setDrGeoTagNeed(String drGeoTagNeed) {
        DrGeoTagNeed = drGeoTagNeed;
    }

    public String getChemistGeoTagNeed() {
        return ChemistGeoTagNeed;
    }

    public void setChemistGeoTagNeed(String chemistGeoTagNeed) {
        ChemistGeoTagNeed = chemistGeoTagNeed;
    }

    public String getStockistGeoTagNeed() {
        return StockistGeoTagNeed;
    }

    public void setStockistGeoTagNeed(String stockistGeoTagNeed) {
        StockistGeoTagNeed = stockistGeoTagNeed;
    }

    public String getCipGeoTagNeed() {
        return CipGeoTagNeed;
    }

    public void setCipGeoTagNeed(String cipGeoTagNeed) {
        CipGeoTagNeed = cipGeoTagNeed;
    }

    public String getUndrGeoTagNeed() {
        return UndrGeoTagNeed;
    }

    public void setUndrGeoTagNeed(String undrGeoTagNeed) {
        UndrGeoTagNeed = undrGeoTagNeed;
    }

    public String getMapGeoFenceCircleRad() {
        return MapGeoFenceCircleRad;
    }

    public void setMapGeoFenceCircleRad(String mapGeoFenceCircleRad) {
        MapGeoFenceCircleRad = mapGeoFenceCircleRad;
    }

    public String getDevRegId() {
        return DevRegId;
    }

    public void setDevRegId(String devRegId) {
        DevRegId = devRegId;
    }

    public String getProfileMclDetailing() {
        return ProfileMclDetailing;
    }

    public void setProfileMclDetailing(String profileMclDetailing) {
        ProfileMclDetailing = profileMclDetailing;
    }

    public String getPresentationNActNeed() {
        return PresentationNActNeed;
    }

    public void setPresentationNActNeed(String presentationNActNeed) {
        PresentationNActNeed = presentationNActNeed;
    }

    public String getPresentationSpecFilter() {
        return PresentationSpecFilter;
    }

    public void setPresentationSpecFilter(String presentationSpecFilter) {
        PresentationSpecFilter = presentationSpecFilter;
    }

    public String getChemistRxNeed() {
        return ChemistRxNeed;
    }

    public void setChemistRxNeed(String chemistRxNeed) {
        ChemistRxNeed = chemistRxNeed;
    }

    public String getAddingDr() {
        return AddingDr;
    }

    public void setAddingDr(String addingDr) {
        AddingDr = addingDr;
    }

    public String getShowDelOptionDcr() {
        return ShowDelOptionDcr;
    }

    public void setShowDelOptionDcr(String showDelOptionDcr) {
        ShowDelOptionDcr = showDelOptionDcr;
    }

    public String getChemDetailingSkip() {
        return ChemDetailingSkip;
    }

    public void setChemDetailingSkip(String chemDetailingSkip) {
        ChemDetailingSkip = chemDetailingSkip;
    }

    public String getStockistDetailingSkip() {
        return StockistDetailingSkip;
    }

    public void setStockistDetailingSkip(String stockistDetailingSkip) {
        StockistDetailingSkip = stockistDetailingSkip;
    }

    public String getUndrDetailingSkip() {
        return UndrDetailingSkip;
    }

    public void setUndrDetailingSkip(String undrDetailingSkip) {
        UndrDetailingSkip = undrDetailingSkip;
    }

    public String getAddingChemist() {
        return AddingChemist;
    }

    public void setAddingChemist(String addingChemist) {
        AddingChemist = addingChemist;
    }

    public String getStockistRxNeed() {
        return StockistRxNeed;
    }

    public void setStockistRxNeed(String stockistRxNeed) {
        StockistRxNeed = stockistRxNeed;
    }

    public String getUndrRxNeed() {
        return UndrRxNeed;
    }

    public void setUndrRxNeed(String undrRxNeed) {
        UndrRxNeed = undrRxNeed;
    }

    public String getTpNeed() {
        return TpNeed;
    }

    public void setTpNeed(String tpNeed) {
        TpNeed = tpNeed;
    }

    public String getSurveyNeed() {
        return SurveyNeed;
    }

    public void setSurveyNeed(String surveyNeed) {
        SurveyNeed = surveyNeed;
    }

    public String getPastLeavePost() {
        return PastLeavePost;
    }

    public void setPastLeavePost(String pastLeavePost) {
        PastLeavePost = pastLeavePost;
    }

    public String getRcpaMandatory() {
        return RcpaMandatory;
    }

    public void setRcpaMandatory(String rcpaMandatory) {
        RcpaMandatory = rcpaMandatory;
    }

    public String getQuizNeed() {
        return QuizNeed;
    }

    public void setQuizNeed(String quizNeed) {
        QuizNeed = quizNeed;
    }

    public String getQuizMandatory() {
        return QuizMandatory;
    }

    public void setQuizMandatory(String quizMandatory) {
        QuizMandatory = quizMandatory;
    }

    public String getMissedDateMandatory() {
        return MissedDateMandatory;
    }

    public void setMissedDateMandatory(String missedDateMandatory) {
        MissedDateMandatory = missedDateMandatory;
    }

    public String getTpDcrDeviation() {
        return TpDcrDeviation;
    }

    public void setTpDcrDeviation(String tpDcrDeviation) {
        TpDcrDeviation = tpDcrDeviation;
    }

    public String getTpBasedDcr() {
        return TpBasedDcr;
    }

    public void setTpBasedDcr(String tpBasedDcr) {
        TpBasedDcr = tpBasedDcr;
    }

    public String getTpMandatoryNeed() {
        return TpMandatoryNeed;
    }

    public void setTpMandatoryNeed(String tpMandatoryNeed) {
        TpMandatoryNeed = tpMandatoryNeed;
    }

    public String getTpStartDate() {
        return TpStartDate;
    }

    public void setTpStartDate(String tpStartDate) {
        TpStartDate = tpStartDate;
    }

    public String getTpEndDate() {
        return TpEndDate;
    }

    public void setTpEndDate(String tpEndDate) {
        TpEndDate = tpEndDate;
    }

    public String getChemistSamNeed() {
        return ChemistSamNeed;
    }

    public void setChemistSamNeed(String chemistSamNeed) {
        ChemistSamNeed = chemistSamNeed;
    }

    public String getDrJwMandatory() {
        return DrJwMandatory;
    }

    public void setDrJwMandatory(String drJwMandatory) {
        DrJwMandatory = drJwMandatory;
    }

    public String getChemistJwMandatory() {
        return ChemistJwMandatory;
    }

    public void setChemistJwMandatory(String chemistJwMandatory) {
        ChemistJwMandatory = chemistJwMandatory;
    }

    public String getStockistJwMandatory() {
        return StockistJwMandatory;
    }

    public void setStockistJwMandatory(String stockistJwMandatory) {
        StockistJwMandatory = stockistJwMandatory;
    }

    public String getUndrJwMandatory() {
        return UndrJwMandatory;
    }

    public void setUndrJwMandatory(String undrJwMandatory) {
        UndrJwMandatory = undrJwMandatory;
    }

    public String getDetailingCipSkip() {
        return DetailingCipSkip;
    }

    public void setDetailingCipSkip(String detailingCipSkip) {
        DetailingCipSkip = detailingCipSkip;
    }

    public String getDrFeedbackMandatory() {
        return DrFeedbackMandatory;
    }

    public void setDrFeedbackMandatory(String drFeedbackMandatory) {
        DrFeedbackMandatory = drFeedbackMandatory;
    }

    public String getDrFeedbackNeed() {
        return DrFeedbackNeed;
    }

    public void setDrFeedbackNeed(String drFeedbackNeed) {
        DrFeedbackNeed = drFeedbackNeed;
    }

    public String getChemistFeedbackNeed() {
        return ChemistFeedbackNeed;
    }

    public void setChemistFeedbackNeed(String chemistFeedbackNeed) {
        ChemistFeedbackNeed = chemistFeedbackNeed;
    }

    public String getStockistFeedbackNeed() {
        return StockistFeedbackNeed;
    }

    public void setStockistFeedbackNeed(String stockistFeedbackNeed) {
        StockistFeedbackNeed = stockistFeedbackNeed;
    }

    public String getUndrFeedbackNeed() {
        return UndrFeedbackNeed;
    }

    public void setUndrFeedbackNeed(String undrFeedbackNeed) {
        UndrFeedbackNeed = undrFeedbackNeed;
    }

    public String getCipFeedbackNeed() {
        return CipFeedbackNeed;
    }

    public void setCipFeedbackNeed(String cipFeedbackNeed) {
        CipFeedbackNeed = cipFeedbackNeed;
    }

    public String getDrPobNeed() {
        return DrPobNeed;
    }

    public void setDrPobNeed(String drPobNeed) {
        DrPobNeed = drPobNeed;
    }

    public String getDrPobMandatory() {
        return DrPobMandatory;
    }

    public void setDrPobMandatory(String drPobMandatory) {
        DrPobMandatory = drPobMandatory;
    }

    public String getChemistPobNeed() {
        return ChemistPobNeed;
    }

    public void setChemistPobNeed(String chemistPobNeed) {
        ChemistPobNeed = chemistPobNeed;
    }

    public String getChemistPobMandatory() {
        return ChemistPobMandatory;
    }

    public void setChemistPobMandatory(String chemistPobMandatory) {
        ChemistPobMandatory = chemistPobMandatory;
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

    public String getCipPobNeed() {
        return CipPobNeed;
    }

    public void setCipPobNeed(String cipPobNeed) {
        CipPobNeed = cipPobNeed;
    }

    public String getCipPobMandatory() {
        return CipPobMandatory;
    }

    public void setCipPobMandatory(String cipPobMandatory) {
        CipPobMandatory = cipPobMandatory;
    }

    public String getDrRemarksMan_Need() {
        return DrRemarksMan_Need;
    }

    public void setDrRemarksMan_Need(String drRemarksMan_Need) {
        DrRemarksMan_Need = drRemarksMan_Need;
    }

    public String getSalesTargetReportNeed() {
        return SalesTargetReportNeed;
    }

    public void setSalesTargetReportNeed(String salesTargetReportNeed) {
        SalesTargetReportNeed = salesTargetReportNeed;
    }

    public String getVisitControl() {
        return VisitControl;
    }

    public void setVisitControl(String visitControl) {
        VisitControl = visitControl;
    }

    public String getMultiCluster() {
        return MultiCluster;
    }

    public void setMultiCluster(String multiCluster) {
        MultiCluster = multiCluster;
    }

    public String getSampleValidation() {
        return SampleValidation;
    }

    public void setSampleValidation(String sampleValidation) {
        SampleValidation = sampleValidation;
    }

    public String getInputValidation() {
        return InputValidation;
    }

    public void setInputValidation(String inputValidation) {
        InputValidation = inputValidation;
    }

    public String getLeaveEntitlementNeed() {
        return LeaveEntitlementNeed;
    }

    public void setLeaveEntitlementNeed(String leaveEntitlementNeed) {
        LeaveEntitlementNeed = leaveEntitlementNeed;
    }

    public String getReminderCallNeed() {
        return ReminderCallNeed;
    }

    public void setReminderCallNeed(String reminderCallNeed) {
        ReminderCallNeed = reminderCallNeed;
    }

    public String getReminderPrdMandatory() {
        return ReminderPrdMandatory;
    }

    public void setReminderPrdMandatory(String reminderPrdMandatory) {
        ReminderPrdMandatory = reminderPrdMandatory;
    }

    public String getGeoTagApprovalNeed() {
        return GeoTagApprovalNeed;
    }

    public void setGeoTagApprovalNeed(String geoTagApprovalNeed) {
        GeoTagApprovalNeed = geoTagApprovalNeed;
    }

    public String getCaptionDr() {
        return CaptionDr;
    }

    public void setCaptionDr(String captionDr) {
        CaptionDr = captionDr;
    }
}
