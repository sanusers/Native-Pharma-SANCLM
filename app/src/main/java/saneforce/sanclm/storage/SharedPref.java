package saneforce.sanclm.storage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

import saneforce.sanclm.activity.slideDownloaderAlertBox.SlideModelClass;

public class SharedPref {

    public static final String SP_NAME = "e_detail";

    //Setting
    public static final String SELECTED_LANGUAGE = "language";
    public static final String SETTING_STATE = "setting_state";
    public static final String BASE_URL = "base_url";
    public static final String LICENSE_KEY = "license_key";
    public static final String BASE_WEB_URL = "base_web_wrl";
    public static final String PHP_PATH_URL = "php_path_url";
    public static final String REPORTS_URL = "reports_url";
    public static final String SLIDES_URL = "slides_url";
    public static final String LOGO_URL = "logo_url";
    public static final String CALL_API_URL = "call_api_url";
    public static final String LOGI_SITE="log_site";


    //Login
    public static final String LOGIN_USER_ID = "login_userId";
    public static final String LOGIN_STATE = "login_state";
    public static final String DEVICE_ID = "device_id";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String SF_CODE = "sf_code";
    public static final String SF_TYPE = "sf_type";
    public static final String TAG_IMAGE_URL = "tag_image_url";

    //Master Sync
    public static final String MASTER_LAST_SYNC = "last_sync";
    public static final String HQ_NAME = "hq_name";
    public static final String HQ_CODE = "hq_code";


    //Map Activity
    public static final String TAGGED_SUCCESSFULLY = "tagged_successfully";
    public static final String CUSTOMER_POSITION = "cust_pos";

    //HomeDashboard
    //MyDayPlan
    public static final String TodayDayPlanSfCode = "today_plan_sfcode";
    public static final String TodayDayPlanSfName = "today_plan_sfname";
    public static final String TodayDayPlanClusterCode = "today_plan_cluster_code";

    //SetUp
    public static final String GEOTAG_IMAGE = "geo_tag_img";
    public static final String GEOTAG_APPROVAL_NEED = "geotag_approval_need";

    //Approval
    public static final String APPROVAL_COUNT = "approval_count";


    // Slide
    public static final String SLIDEID = "slideid";
    public static final String SLIDELIST = "slidelist";

    public static final String SLIDEDOWNCOUNT = "slidedowncount";
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;




    public static void saveSlideListID(Context context, ArrayList<String> List){
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SLIDEID, new Gson().toJson(List)).apply();

    }


    public static void saveSlideDownloadingList(Context context, String  Downloadcount,ArrayList<SlideModelClass> List){
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SLIDEDOWNCOUNT, String.valueOf(Downloadcount));
        editor.putString(SLIDELIST, new Gson().toJson(List));
        editor.apply();
    }


    public static String GetSlideID(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getString(SLIDEID,"[]");
    }
    public static String GetSlideDownloadingcount(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getString(SLIDEDOWNCOUNT,"0");
    }  public static String GetSlideList(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getString(SLIDELIST,"");
    }


    public static void ClearSharedPreference(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveSelectedLanguage(Context context,String language){
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SELECTED_LANGUAGE,language).apply();
    }

    public static String getSelectedLanguage(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getString(SELECTED_LANGUAGE,"");
    }


    public static void Loginsite(Context context, String site) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LOGI_SITE, site).apply();
    }
    public static void saveUrls(Context context, String baseUrl, String licenseKey, String baseWebUrl, String PhpPathUrl, String reportsUrl, String SlidesUrl, String logoUrl, boolean settingState) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(BASE_URL, baseUrl);
        editor.putString(LICENSE_KEY, licenseKey);
        editor.putString(BASE_WEB_URL, baseWebUrl);
        editor.putString(PHP_PATH_URL, PhpPathUrl);
        editor.putString(REPORTS_URL, reportsUrl);
        editor.putString(SLIDES_URL, SlidesUrl);
        editor.putString(LOGO_URL, logoUrl);
        editor.putBoolean(SETTING_STATE, settingState);
        editor.apply();
    }


    public static String getLogInsite(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(LOGI_SITE, "");
    }
    public static String getBaseUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(BASE_URL, "");
    }

    public static String getLicenseKey(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(LICENSE_KEY, "");
    }

    public static String getBaseWebUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(BASE_WEB_URL, "");
    }

    public static String getPhpPathUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PHP_PATH_URL, "");
    }

    public static String getReportsURl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(REPORTS_URL, "");
    }

    public static String getSlideUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SLIDES_URL, "");
    }

    public static String getLogoUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(LOGO_URL, "");
    }

    public static void saveSettingState(Context context, boolean state) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SETTING_STATE, state).apply();
    }

    public static boolean getSettingState(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(SETTING_STATE, false);
    }

    public static void saveLoginId(Context context, String id) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LOGIN_USER_ID, id).apply();
    }

    public static String getLoginId(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(LOGIN_USER_ID, "");
    }

    public static void saveLoginState(Context context, boolean state) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN_STATE, state).apply();
    }

    public static boolean getLoginState(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(LOGIN_STATE, false);
    }

    public static void saveDeviceId(Context context, String deviceId) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DEVICE_ID, deviceId).apply();
    }

    public static String getDeviceId(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DEVICE_ID, "");
    }

    public static void saveFcmToken(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(FCM_TOKEN, token).apply();
    }

    public static String getFcmToken(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(FCM_TOKEN, "");
    }

    public static void saveSfType(Context context, String type, String sfCode) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SF_TYPE, type);
        editor.putString(SF_CODE, sfCode).apply();
    }

    public static String getSfType(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_TYPE, "");
    }

    public static String getSfCode(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_CODE, "");
    }

    public static String getLastSync(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MASTER_LAST_SYNC, "");
    }

    public static void saveMasterLastSync(Context context,String date){
        sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MASTER_LAST_SYNC,date).apply();
    }

    public static void setCallApiUrl(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CALL_API_URL, token).apply();
    }

    public static String getCallApiUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CALL_API_URL, "");
    }

    public static void setTagImageUrl(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TAG_IMAGE_URL, token).apply();
    }

    public static String getTagImageUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TAG_IMAGE_URL, "");
    }

    public static void saveHq (Context context, String name, String code){
        sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(HQ_NAME, name);
        editor.putString(HQ_CODE, code).apply();
    }
    public static String getHqName (Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(HQ_NAME, "");
    }

    public static String getHqCode (Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(HQ_CODE, "");
    }

    public static void setApprovalsCounts(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(APPROVAL_COUNT, token).apply();
    }

    public static String getApprovalsCounts(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(APPROVAL_COUNT, "");
    }


    /*   public static void saveSetups(Context context, String geoNeed, String geoChk, String CheNeed, String StkistNeed, String UndrNeed, String DrPrdNeed,
                                  String DrInpNeed, String ChePrdNeed, String CheInpNeed, String StkPrdNeed, String StkInpNeed,
                                  String UndrPrdNeed, String UndrInpNeed, String CapDr, String CapChe, String CapStk, String CapUndr, String CapDrPrd,
                                  String CapChePrd, String CapStkPrd, String CapUndrPrd, String CapDrInp, String CapCheInp, String CapStkInp, String CapUndrInp,
                                  String DrJwNeed, String CheJwNeed, String StkJwNeed, String UndrJwNeed, String CapCheSamQty, String CheRcpaMandatory, String CapDrRxQty, String CapDrSamQty, String CapCheRxQty,
                                  String CapStkRxQty, String MgrDrRcpaMandatory, String CapCluster, String MgrCheRcpaMandatory, String DrECMandatory,
                                  String CheECMandatory, String StkECMandatory, String UndrECMandatory, String CapUndrRxQty, String CapUndrSamQty, String DrRxNeed, String DrSamNeed, String DrRxQtyMandatory,
                                  String DrSamQtyMandatory, String CheckInOut, String DrECNeed, String CheECNeed, String StkECNeed, String UndrECNeed, String DrPrdMandatory,
                                  String DrInpMandatory, String DrRcpaNeed, String CheRcpaNeed, String DrGeoTagNeed, String CheGeoTagNeed, String UndrGeoTagNeed,
                                  String CipGeoTagNeed, String GeoFenceCirRad, String GeoTagImage, String DevRegId, String ProMclDetailing, String PreNActNeed,
                                  String PreSpecFilter, String CheRxNeed,
                                  String AddDr, String ShowDel, String DetCheSkip, String DetStkSkip, String DetUndrSkip,
                                  String AddChemist, String StkPobNeed, String UndrPobNeed, String TpNeed, String SurveyNeed, String PastLeavePost, String RcpaMandatory,
                                  String QuizNeed, String QuizMandatory,
                                  String MissedDateMandatory, String TpDcrDeviation, String TpBasedDcr, String TpMandatoryNeed,
                                  String TpStartDate, String TpEndDate, String CheSamQtyNeed, String DtCipSkip, String DrFBMandatory, String DrFBNeed,
                                  String CheFBNeed, String StkFBNeed, String UndrFBNeed, String CipFBNeed, String DrPobNeed, String DrPobMandatory, String ChePobNeed, String ChePobMandatory, String StkPobMandatory,
                                  String UndrPobMandatory, String CipPobNeed, String CipPobMandatory, String DrRemNeed,
                                  String SalRepNeed, String VisitControl, String MultiCluster, String SamVal, String InpVal,
                                  String LeaveEntitleNeed, String RemCallNeed, String RemCallPrdNeed) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEO_NEED, geoNeed);
        editor.putString(GEO_CHECK, geoChk);
        editor.putString(CHEMIST_NEED, CheNeed);
        editor.putString(STOCKIST_NEED, StkistNeed);
        editor.putString(UNDR_NEED, UndrNeed);
        editor.putString(DR_PRD_NEED, DrPrdNeed);
        editor.putString(DR_INP_NEED, DrInpNeed);
        editor.putString(CHE_PRD_NEED, ChePrdNeed);
        editor.putString(CHE_INP_NEED, CheInpNeed);
        editor.putString(STK_PRD_NEED, StkPrdNeed);
        editor.putString(STK_INP_NEED, StkInpNeed);
        editor.putString(UNDR_PRD_NEED, UndrPrdNeed);
        editor.putString(UNDR_INP_NEED, UndrInpNeed);
        editor.putString(CAPTION_DR, CapDr);
        editor.putString(CAPTION_CHEMIST, CapChe);
        editor.putString(CAPTION_STOCKIST, CapStk);
        editor.putString(CAPTION_UNDR, CapUndr);
        editor.putString(CAPTION_DR_PRD, CapDrPrd);
        editor.putString(CAPTION_CHEMIST_PRD, CapChePrd);
        editor.putString(CAPTION_STOCKIST_PRD, CapStkPrd);
        editor.putString(CAPTION_UNDR_PRD, CapUndrPrd);
        editor.putString(CAPTION_DR_INP, CapDrInp);
        editor.putString(CAPTION_CHEMIST_INP, CapCheInp);
        editor.putString(CAPTION_STOCKIST_INP, CapStkInp);
        editor.putString(CAPTION_UN_DR_INP, CapUndrInp);
        editor.putString(DR_JW_NEED, DrJwNeed);
        editor.putString(CHEMIST_JW_NEED, CheJwNeed);
        editor.putString(CAPTION_STOCKIST_INP, StkJwNeed);
        editor.putString(UNDR_JW_NEED, UndrJwNeed);
        editor.putString(CAPTION_CHE_SAM_QTY, CapCheSamQty);
        editor.putString(CHEMIST_RCPA_MANDATORY, CheRcpaMandatory);
        editor.putString(CAPTION_DR_RX_QTY, CapDrRxQty);
        editor.putString(CAPTION_DR_SAM_QTY, CapDrSamQty);
        editor.putString(CAPTION_CHE_RX_QTY, CapCheRxQty);
        editor.putString(CAPTION_STK_RX_QTY, CapStkRxQty);
        editor.putString(MGR_DR_RCPA_MANDATORY, MgrDrRcpaMandatory);
        editor.putString(CAPTION_CLUSTER, CapCluster);
        editor.putString(MGR_CHE_RCPA_MANDATORY, MgrCheRcpaMandatory);
        editor.putString(DR_EVENT_CAPTURE_MANDATORY, DrECMandatory);
        editor.putString(CHE_EVENT_CAPTURE_MANDATORY, CheECMandatory);
        editor.putString(STK_EVENT_CAPTURE_MANDATORY, StkECMandatory);
        editor.putString(UNDR_EVENT_CAPTURE_MANDATORY, UndrECMandatory);
        editor.putString(CAPTION_UNDR_RX_QTY, CapUndrRxQty);
        editor.putString(CAPTION_UNDR_SAM_QTY, CapUndrSamQty);
        editor.putString(DR_RX_NEED, DrRxNeed);
        editor.putString(DR_SAM_NEED, DrSamNeed);
        editor.putString(DR_RX_QTY_MANDATORY, DrRxQtyMandatory);
        editor.putString(DR_SAM_QTY_MANDATORY, DrSamQtyMandatory);
        editor.putString(CHECK_IN_OUT_NEED, CheckInOut);
        editor.putString(DR_EVENT_CAPTURE, DrECNeed);
        editor.putString(CHEMIST_EVENT_CAPTURE, CheECNeed);
        editor.putString(STOCKIST_EVENT_CAPTURE, StkECNeed);
        editor.putString(UNDR_EVENT_CAPTURE, UndrECNeed);
        editor.putString(DR_PRD_MANDATORY, DrPrdMandatory);
        editor.putString(DR_INP_MANDATORY, DrInpMandatory);
        editor.putString(DR_RCPA_NEED, DrRcpaNeed);
        editor.putString(CHEMIST_RCPA_NEED, CheRcpaNeed);
        editor.putString(DR_GEO_TAG_NEED, DrGeoTagNeed);
        editor.putString(CHEMIST_GEO_TAG_NEED, CheGeoTagNeed);
        editor.putString(STK_GEO_TAG_NEED, UndrGeoTagNeed);
        editor.putString(UNDR_GEO_TAG_NEED, CipGeoTagNeed);
        editor.putString(CIP_GEO_TAG_NEED, GeoFenceCirRad);
        editor.putString(GEOFENCING_CIRCLE_RADIUS, GeoTagImage);
        editor.putString(GEOTAG_IMAGE, DevRegId);
        editor.putString(DEVICE_REG_ID, ProMclDetailing);
        editor.putString(PROFILE_MCL_DETAILING, PreNActNeed);
        editor.putString(PRESENTATION_N_ACTIVITY_NEED, PreNActNeed);
        editor.putString(PRESENTATION_SPEC_FILTER, PreSpecFilter);
        editor.putString(CHEMIST_RX_NEED, CheRxNeed);
        editor.putString(ADD_DR, AddDr);
        editor.putString(SHOW_DELETE_OPTION, ShowDel);
        editor.putString(DETAILING_CHEM_SKIP, DetCheSkip);
        editor.putString(DETAILING_STOCKIST_SKIP, DetStkSkip);
        editor.putString(DETAILING_UNDR_SKIP, DetUndrSkip);
        editor.putString(ADD_CHEMIST, AddChemist);
        editor.putString(STOCKIST_POB_NEED, StkPobNeed);
        editor.putString(UNDR_POB_NEED, UndrPobNeed);
        editor.putString(TP_NEED, TpNeed);
        editor.putString(SURVEY_NEED, SurveyNeed);
        editor.putString(PAST_LEAVE_POST, PastLeavePost);
        editor.putString(RCPA_MANDATORY, RcpaMandatory);
        editor.putString(QUIZ_NEED, QuizNeed);
        editor.putString(QUIZ_NEED_MANDATORY, QuizMandatory);
        editor.putString(MISSED_DATE_MANDATORY, MissedDateMandatory);
        editor.putString(TP_DCR_DEVIATION, TpDcrDeviation);
        editor.putString(TP_BASED_DCR, TpBasedDcr);
        editor.putString(TP_MANDATORY_NEED, TpMandatoryNeed);
        editor.putString(TP_START_DATE, TpStartDate);
        editor.putString(TP_END_DATE, TpEndDate);
        editor.putString(CHEMIST_SAM_QTY_NEED, CheSamQtyNeed);
        editor.putString(DR_JW_MANDATORY, DrJwNeed);
        editor.putString(CHEMIST_JW_MANDATORY, CheJwNeed);
        editor.putString(STOCKIST_JW_MANDATORY, StkJwNeed);
        editor.putString(UNDR_JW_MANDATORY, UndrJwNeed);
        editor.putString(DETAILING_CIP_SKIP, DtCipSkip);
        editor.putString(DR_FEEDBACK_MANDATORY, DrFBMandatory);
        editor.putString(DR_FEEDBACK_NEED, DrFBNeed);
        editor.putString(CHEMIST_FEEDBACK_NEED, CheFBNeed);
        editor.putString(STOCKIST_FEEDBACK_NEED, StkFBNeed);
        editor.putString(UNDR_FEEDBACK_NEED, UndrFBNeed);
        editor.putString(CIP_FEEDBACK_NEED, CipFBNeed);
        editor.putString(DR_POB_NEED, DrPobNeed);
        editor.putString(DR_POB_MANDATORY, DrPobMandatory);
        editor.putString(CHEMIST_POB_NEED, ChePobNeed);
        editor.putString(CHEMIST_POB_MANDATORY, ChePobMandatory);
        editor.putString(STOCKIST_POB_MANDATORY, StkPobMandatory);
        editor.putString(UNDR_POB_MANDATORY, UndrPobMandatory);
        editor.putString(CIP_POB_NEED, CipPobNeed);
        editor.putString(CIP_POB_MANDATORY, CipPobMandatory);
        editor.putString(DR_REMARKS_NEED, DrRemNeed);
        editor.putString(SALES_REPORT_NEED, SalRepNeed);
        editor.putString(VISIT_CONTROL, VisitControl);
        editor.putString(MULTI_CLUSTER, MultiCluster);
        editor.putString(SAMPLE_VALIDATION, SamVal);
        editor.putString(INPUT_VALIDATION, InpVal);
        editor.putString(LEAVE_ENTITLEMENT_NEED, LeaveEntitleNeed);
        editor.putString(REMINDER_CALL_NEED, RemCallNeed);
        editor.putString(REMINDER_CALL_PRD_MANDATORY, RemCallPrdNeed);
        editor.apply();
    }*/


    public static void setTodayDayPlanClusterCode(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TodayDayPlanClusterCode, status).apply();
    }

    public static String getTodayDayPlanClusterCode(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TodayDayPlanClusterCode, "");
    }

    public static void setTodayDayPlanSfCode(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TodayDayPlanSfCode, status).apply();
    }

    public static String getTodayDayPlanSfCode(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TodayDayPlanSfCode, "");
    }

    public static void setTodayDayPlanSfName(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TodayDayPlanSfName, status).apply();
    }

    public static String getTodayDayPlanSfName(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TodayDayPlanSfName, "");
    }

    public static void setGeotagApprovalNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEOTAG_APPROVAL_NEED, status).apply();
    }

    public static String getGeotagApprovalNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(GEOTAG_APPROVAL_NEED, "");
    }

    public static void setGeotagImage(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(GEOTAG_IMAGE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEOTAG_IMAGE, status).apply();
    }

    public static String getGeotagImage(Context context) {
        return context.getSharedPreferences(GEOTAG_IMAGE, MODE_PRIVATE).getString(GEOTAG_IMAGE, "");
    }



}
