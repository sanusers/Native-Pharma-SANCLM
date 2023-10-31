package saneforce.sanclm.storage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    public static final String SP_NAME = "e_detail";

    //Setting
    public static final String SETTING_STATE = "setting_state";
    public static final String BASE_URL = "base_url";
    public static final String LICENSE_KEY = "license_key";
    public static final String BASE_WEB_URL = "base_web_wrl";
    public static final String PHP_PATH_URL = "php_path_url";
    public static final String REPORTS_URL = "reports_url";
    public static final String SLIDES_URL = "slides_url";
    public static final String LOGO_URL = "logo_url";
    public static final String CALL_API_URL = "call_api_url";


    //Login
    public static final String LOGIN_STATE = "login_state";
    public static final String DEVICE_ID = "device_id";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String SF_CODE = "sf_code";
    public static final String SF_TYPE = "sf_type";

    public static final String SF_NAME = "sf_name";
    public static final String USER_NAME = "user_name";
    public static final String DIVISION_CODE = "division_code";
    public static final String DESIGNATION_NAME = "dsg_name";
    public static final String STATE_CODE = "state_code";
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String SUBDIV_CODE = "subdiv_code";
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
    public static final String GEO_NEED = "geo_need";
    public static final String GEO_CHECK = "geo_check";
    public static final String T_BASE = "t_base"; //TBase
    public static final String CHEMIST_NEED = "chemist_need";
    public static final String STOCKIST_NEED = "stockiest_need";
    public static final String UNDR_NEED = "undr_need";
    public static final String DR_PRD_NEED = "dr_prd_need";
    public static final String DR_INP_NEED = "dr_inp_need";
    public static final String CHE_PRD_NEED = "che_prd_need";
    public static final String CHE_INP_NEED = "che_inp_need";
    public static final String STK_PRD_NEED = "stk_prd_need";
    public static final String STK_INP_NEED = "stk_inp_need";
    public static final String UNDR_PRD_NEED = "undr_prd_need";
    public static final String UNDR_INP_NEED = "undr_inp_need";

    public static final String CAPTION_DR = "caption_dr";
    public static final String CAPTION_CHEMIST = "caption_chemist";
    public static final String CAPTION_STOCKIST = "caption_stockiest";
    public static final String CAPTION_UNDR = "caption_undr";
    public static final String CAPTION_DR_PRD = "caption_dr_prd";
    public static final String CAPTION_CHEMIST_PRD = "caption_chemist_prd";
    public static final String CAPTION_STOCKIST_PRD = "caption_stockiest_prd";
    public static final String CAPTION_UNDR_PRD = "caption_undr_prd";
    public static final String CAPTION_DR_INP = "caption_dr_inp";
    public static final String CAPTION_CHEMIST_INP = "caption_chemist_inp";
    public static final String CAPTION_STOCKIST_INP = "caption_stockiest_inp";
    public static final String CAPTION_UN_DR_INP = "caption_undr_inp";
    public static final String DR_JW_NEED = "dr_jw_need";
    public static final String CHEMIST_JW_NEED = "chemist_jw_need";
    public static final String STOCKIST_JW_NEED = "stockiest_jw_need";
    public static final String UNDR_JW_NEED = "undr_jw_need";

    public static final String CAPTION_CHE_SAM_QTY = "caption_chemist_sam";
    public static final String CHEMIST_RCPA_MANDATORY = "chemist_rcpa_man";
    public static final String CAPTION_DR_RX_QTY = "caption_dr_rx";
    public static final String CAPTION_DR_SAM_QTY = "caption_dr_sam";
    public static final String CAPTION_CHE_RX_QTY = "caption_chemist_rx";
    public static final String CAPTION_STK_RX_QTY = "caption_stk_rx";
    public static final String MGR_DR_RCPA_MANDATORY = "mgr_dr_rcpa_man";
    public static final String CAPTION_CLUSTER = "caption_cluster";
    public static final String MGR_CHE_RCPA_MANDATORY = "mgr_chemist_rcpa_man";
    public static final String DR_EVENT_CAPTURE_MANDATORY = "dr_event_capture_man";
    public static final String CHE_EVENT_CAPTURE_MANDATORY = "che_event_capture_man";
    public static final String STK_EVENT_CAPTURE_MANDATORY = "stockiest_event_capture_man";
    public static final String UNDR_EVENT_CAPTURE_MANDATORY = "undr_event_capture_man";
    public static final String CAPTION_UNDR_RX_QTY = "caption_undr_rx";
    public static final String CAPTION_UNDR_SAM_QTY = "caption_undr_sam";

    public static final String DR_RX_NEED = "dr_rx_need";
    public static final String DR_SAM_NEED = "dr_sam_need";
    public static final String DR_RX_QTY_MANDATORY = "dr_rx_qty_man";
    public static final String DR_SAM_QTY_MANDATORY = "dr_sam_qty_man";
    public static final String FEEDBACK_NEED = "feedback_need"; //FeedNd
    public static final String CHECK_IN_OUT_NEED = "check_in_out_need";
    public static final String DR_EVENT_CAPTURE = "dr_event_capture";
    public static final String CHEMIST_EVENT_CAPTURE = "che_event_capture";
    public static final String STOCKIST_EVENT_CAPTURE = "stockiest_event_capture";
    public static final String UNDR_EVENT_CAPTURE = "undr_event_capture";
    public static final String DR_PRD_MANDATORY = "dr_prd_man";
    public static final String DR_INP_MANDATORY = "dr_inp_man";
    public static final String DR_RCPA_NEED = "dr_rcpa_need";
    public static final String CHEMIST_RCPA_NEED = "chemist_rcpa_need";
    public static final String MULTIPLE_DR_NEED = "multiple_dr_need"; //multiple_doc_need
    public static final String MAIL_NEED = "mail_need"; //mailneed
    public static final String DR_DOB_DOW = "dr_dob_dow"; //doctor_dobdow
    public static final String DR_GEO_TAG_NEED = "dr_geo_tag_need";
    public static final String CHEMIST_GEO_TAG_NEED = "che_geo_tag_need";
    public static final String STK_GEO_TAG_NEED = "stk_geo_tag_need";
    public static final String UNDR_GEO_TAG_NEED = "undr_geo_tag_need";
    public static final String CIP_GEO_TAG_NEED = "cip_geo_tag_need";
    public static final String GEOFENCING_CIRCLE_RADIUS = "map_circle_radius";
    public static final String GEOTAG_IMAGE = "geo_tag_img";
    public static final String DEVICE_REG_ID = "device_reg_id";
    public static final String SFTP_DATE = "sftp_date"; //SFTPDate
    public static final String ATTENDANCE = "attendance"; //Attendance
    public static final String PROFILE_MCL_DETAILING = "mcl_detailing";
    public static final String CIRCULAR = "circular"; //circular
    public static final String DR_NEXT_VISIT = "dr_nxt_visit"; //DrNxtVst
    public static final String DR_NEXT_VISIT_MANDATORY = "dr_nxt_visit_man"; //DrNxtVstMd
    public static final String DR_POLICY = "dr_policy"; //DrPolicy
    public static final String DR_CALL_TYPE = "dr_call_type"; //DrCallTyp
    public static final String DR_CALL_TYPE_MODE = "dr_call_type_mode";//DrCallTypMd
    public static final String SKIP_SLIDE_DEMO = "skip_slide_demo"; //SkipSlideDemo
    public static final String RATING_BASED_SLIDE = "rating_based_slide";//RatingBasedSlide
    public static final String MEET_EVENT_NEED = "meet_event_need";//MeetEventNeed
    public static final String ACTIVITY_NEED = "activity_need";//ActivityNeed
    public static final String PRESENTATION_N_ACTIVITY_NEED = "n_activity_need";
    public static final String DR_ACTIVITY_NEED = "dr_activity_need";//DrActivityNeed
    public static final String CHEMIST_ACTIVITY_NEED = "che_activity_need";//ChmActivityNeed
    public static final String STOCKIST_ACTIVITY_NEED = "stk_activity_need";//StkActivityNeed
    public static final String UN_DR_ACTIVITY_NEED = "undr_activity_need";//NdrActivityNeed
    public static final String SURVEY_NEED_DUPLICATE = "survey_need";//SurveyNeed
    public static final String DR_SURVEY_NEED = "dr_survey_need";//DrSurveyNeed
    public static final String CHEMIST_SURVEY_NEED = "che_survey_need";//ChmSurveyNeed
    public static final String STOCKIST_SURVEY_NEED = "stk_survey_need";//StkSurveyNeed
    public static final String UN_DR_SURVEY_NEED = "undr_survey_need";//NdrSurveyNeed
    public static final String PRESENTATION_SPEC_FILTER = "spec_filter";
    public static final String MISSED_ENTRY = "missed_entry";//MissedEntry

    public static final String ADD_NEW_DR_NEED = "add_new_dr_need";//AddNewDrNeed
    public static final String MAX_RATING_STAR = "max_rate_star";//MaxStarRate


    public static final String CHEMIST_RX_NEED = "chm_rx_need";
    public static final String ADD_DR = "add_dr";
    public static final String SHOW_DELETE_OPTION = "show_delete_option";
    public static final String DETAILING_CHEM_SKIP = "detailing_chem";
    public static final String DETAILING_STOCKIST_SKIP = "detailing_stk";
    public static final String DETAILING_UNDR_SKIP = "detailing_undr";
    public static final String ADD_CHEMIST = "add_chemist";
    public static final String STOCKIST_POB_NEED = "stk_pob_need";
    public static final String UNDR_POB_NEED = "undr_pob_need";
    public static final String TP_NEED = "tp_need";
    public static final String SURVEY_NEED = "survey_nd";
    public static final String PAST_LEAVE_POST = "past_leave_post";
    public static final String RCPA_MANDATORY = "rcpa_man";
    public static final String QUIZ_NEED = "quiz_need";
    public static final String QUIZ_NEED_MANDATORY = "quiz_need_man";
    public static final String MISSED_DATE_MANDATORY = "missed_date_man";
    public static final String DELAY_CONTROL = "delay_control";//DlyCtrl
    public static final String DCR_LOCK_DAYS = "dcr_lock_days";//DcrLockDays

    public static final String TP_DCR_DEVIATION = "tp_dcr_deviation";
    public static final String TP_BASED_DCR = "tp_based_dcr";
    public static final String TP_MANDATORY_NEED = "tp_based_dcr";
    public static final String TP_START_DATE = "tp_start_date";
    public static final String TP_END_DATE = "tp_end_date";
    public static final String CHEMIST_SAM_QTY_NEED = "che_sam_qty_need";
    public static final String DR_JW_MANDATORY = "dr_jw_man";
    public static final String CHEMIST_JW_MANDATORY = "chemist_jw_man";
    public static final String STOCKIST_JW_MANDATORY = "stockiest_jw_man";
    public static final String UNDR_JW_MANDATORY = "undr_jw_man";
    public static final String DETAILING_CIP_SKIP = "cip_detailing";
    public static final String DR_FEEDBACK_MANDATORY = "dr_feedback_man";
    public static final String DR_FEEDBACK_NEED = "dr_feedback_need";

    public static final String CHEMIST_FEEDBACK_NEED = "che_feedback_need";
    public static final String STOCKIST_FEEDBACK_NEED = "stk_feedback_need";
    public static final String UNDR_FEEDBACK_NEED = "undr_feedback_need";
    public static final String CIP_FEEDBACK_NEED = "cip_feedback_need";
    public static final String DR_POB_NEED = "dr_pob_need";
    public static final String DR_POB_MANDATORY = "dr_pob_man";
    public static final String CHEMIST_POB_NEED = "che_pob_need";
    public static final String CHEMIST_POB_MANDATORY = "che_pob_man";
    public static final String STOCKIST_POB_MANDATORY = "stk_pob_man";
    public static final String UNDR_POB_MANDATORY = "undr_pob_man";
    public static final String CIP_POB_NEED = "cip_pob_need";
    public static final String CIP_POB_MANDATORY = "cip_pob_man";
    public static final String DR_REMARKS_NEED = "dr_remarks_need";
    public static final String SALES_REPORT_NEED = "sales_report_need";
    public static final String VISIT_CONTROL = "visit_control";
    public static final String MULTI_CLUSTER = "multi_cluster";
    public static final String SAMPLE_VALIDATION = "sample_validation";
    public static final String INPUT_VALIDATION = "input_validation";
    public static final String LEAVE_ENTITLEMENT_NEED = "leave_entitlement_need";
    public static final String REMINDER_CALL_NEED = "reminder_call_need";
    public static final String REMINDER_CALL_PRD_MANDATORY = "reminder_call_prd_man";
    public static final String GEOTAG_APPROVAL_NEED = "geotag_approval_need";
    public static final String CIP_NEED = "cip_need";
    public static final String CAPTION_CIP = "cap_cip";
    public static final String HOSPITAL_NEED = "hos_need";



    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static void saveBaseUrl(Context context, String baseUrl) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(BASE_URL, baseUrl).apply();
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

    public static void ClearSharefPreference(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
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

    public static String getStateCode(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STATE_CODE, "");
    }

    public static void setSubdivCode(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SUBDIV_CODE, token).apply();
    }

    public static String getSubdivCode(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SUBDIV_CODE, "");
    }

    public static void setEmployeeId(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(EMPLOYEE_ID, token).apply();
    }

    public static String getEmployeeId(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(EMPLOYEE_ID, "");
    }


    public static void setUserName(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(USER_NAME, token).apply();
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

    public static void setSfName(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SF_NAME, token).apply();
    }

    public static void setSfCode(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(USER_NAME, token).apply();
    }

    public static String getUserName(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(USER_NAME, "");
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

    public static String getSfName(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SF_NAME, "");
    }

    public static void setTagImageUrl(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TAG_IMAGE_URL, token).apply();
    }

    public static String getTagImageUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TAG_IMAGE_URL, "");
    }

    public static void setDivisionCode(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DIVISION_CODE, token).apply();
    }

    public static String getDivisionCode(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DIVISION_CODE, "");
    }


    public static void setDesignationName(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DESIGNATION_NAME, token).apply();
    }

    public static String getDesignationName(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DESIGNATION_NAME, "");
    }

    public static void setStateCode(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STATE_CODE, token).apply();
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

    public static void setGeoNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEO_NEED, status).apply();
    }

    public static String getGeoNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(GEO_NEED, "");
    }

    public static void setGeoCheck(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEO_CHECK, status).apply();
    }

    public static String getGeoCheck(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(GEO_CHECK, "");
    }

    public static void setChemistNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_NEED, status).apply();
    }

    public static String getChemistNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHEMIST_NEED, "");
    }

    public static void setStockistNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_NEED, status).apply();
    }

    public static String getStockistNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STOCKIST_NEED, "");
    }

    public static void setUndrNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_NEED, status).apply();
    }

    public static String getUndrNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(UNDR_NEED, "");
    }


    public static void setCipNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CIP_NEED, status).apply();
    }

    public static String getCipNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CIP_NEED, "");
    }

    public static void setHospitalNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(HOSPITAL_NEED, status).apply();
    }

    public static String getHospitalNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(HOSPITAL_NEED, "");
    }

    public static void setTaggedSuccessfully(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TAGGED_SUCCESSFULLY, status).apply();
    }

    public static String getTaggedSuccessfully(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TAGGED_SUCCESSFULLY, "");
    }

    public static void setCustomerPosition(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CUSTOMER_POSITION, status).apply();
    }

    public static String getCustomerPosition(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CUSTOMER_POSITION, "");
    }

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

    public static void setProfileMclDetailing(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PROFILE_MCL_DETAILING, status).apply();
    }

    public static String getProfileMclDetailing(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(PROFILE_MCL_DETAILING, "");
    }

    public static void setPresentationNActivityNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PRESENTATION_N_ACTIVITY_NEED, status).apply();
    }

    public static String getPresentationNActivityNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(PRESENTATION_N_ACTIVITY_NEED, "");
    }

    public static void setPresentationSpecFilter(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PRESENTATION_SPEC_FILTER, status).apply();
    }

    public static String getPresentationSpecFilter(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(PRESENTATION_SPEC_FILTER, "");
    }

    public static void setChemistRxNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_RX_NEED, status).apply();
    }

    public static String getChemistRxNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHEMIST_RX_NEED, "");
    }

    public static void setAddDr(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(ADD_DR, status).apply();
    }

    public static String getAddDr(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(ADD_DR, "");
    }

    public static void setShowDeleteOption(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SHOW_DELETE_OPTION, status).apply();
    }

    public static String getShowDeleteOption(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SHOW_DELETE_OPTION, "");
    }

    public static void setDetailingChemSkip(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DETAILING_CHEM_SKIP, status).apply();
    }

    public static String getDetailingChemSkip(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DETAILING_CHEM_SKIP, "");
    }

    public static void setDetailingStockistSkip(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DETAILING_STOCKIST_SKIP, status).apply();
    }

    public static String getDetailingStockistSkip(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DETAILING_STOCKIST_SKIP, "");
    }

    public static void setDetailingUndrSkip(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DETAILING_UNDR_SKIP, status).apply();
    }

    public static String getDetailingUndrSkip(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DETAILING_UNDR_SKIP, "");
    }

    public static void setAddChemist(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(ADD_CHEMIST, status).apply();
    }

    public static String getAddChemist(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(ADD_CHEMIST, "");
    }

    public static void setStockistPobNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_POB_NEED, status).apply();
    }

    public static String getStockistPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STOCKIST_POB_NEED, "");
    }

    public static void setUndrPobNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_POB_NEED, status).apply();
    }

    public static String getUndrPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(UNDR_POB_NEED, "");
    }

    public static void setTpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TP_NEED, status).apply();
    }

    public static String getTpNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TP_NEED, "");
    }

    public static void setSurveyNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SURVEY_NEED, status).apply();
    }

    public static String getSurveyNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SURVEY_NEED, "");
    }

    public static void setPastLeavePost(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PAST_LEAVE_POST, status).apply();
    }

    public static String getPastLeavePost(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(PAST_LEAVE_POST, "");
    }

    public static void setRcpaMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(RCPA_MANDATORY, status).apply();
    }

    public static String getRcpaMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(RCPA_MANDATORY, "");
    }

    public static void setQuizNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(QUIZ_NEED, status).apply();
    }

    public static String getQuizNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(QUIZ_NEED, "");
    }

    public static void setQuizNeedMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(QUIZ_NEED_MANDATORY, status).apply();
    }

    public static String getQuizNeedMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(QUIZ_NEED_MANDATORY, "");
    }

    public static void setMissedDateMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MISSED_DATE_MANDATORY, status).apply();
    }

    public static String getMissedDateMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(MISSED_DATE_MANDATORY, "");
    }

    public static void setTpDcrDeviation(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TP_DCR_DEVIATION, status).apply();
    }

    public static String getTpDcrDeviation(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TP_DCR_DEVIATION, "");
    }

    public static void setTpBasedDcr(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TP_BASED_DCR, status).apply();
    }

    public static String getTpBasedDcr(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TP_BASED_DCR, "");
    }

    public static void setTpMandatoryNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TP_MANDATORY_NEED, status).apply();
    }

    public static String getTpMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TP_MANDATORY_NEED, "");
    }

    public static void setTpStartDate(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TP_START_DATE, status).apply();
    }

    public static String getTpStartDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TP_START_DATE, "");
    }

    public static void setTpEndDate(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TP_END_DATE, status).apply();
    }

    public static String getTpEndDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TP_END_DATE, "");
    }

    public static void setChemistSamQtyNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_SAM_QTY_NEED, status).apply();
    }

    public static String getChemistSamQtyNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHEMIST_SAM_QTY_NEED, "");
    }

    public static void setDrJwMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_JW_MANDATORY, status).apply();
    }

    public static String getDrJwMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DR_JW_MANDATORY, "");
    }

    public static void setChemistJwMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_JW_MANDATORY, status).apply();
    }

    public static String getChemistJwMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHEMIST_JW_MANDATORY, "");
    }

    public static void setStockistJwMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_JW_MANDATORY, status).apply();
    }

    public static String getStockistJwMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STOCKIST_JW_MANDATORY, "");
    }

    public static void setUndrJwMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_JW_MANDATORY, status).apply();
    }

    public static String getUndrJwMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(UNDR_JW_MANDATORY, "");
    }

    public static void setDetailingCipSkip(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DETAILING_CIP_SKIP, status).apply();
    }

    public static String getDetailingCipSkip(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DETAILING_CIP_SKIP, "");
    }

    public static void setDrFeedbackMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_FEEDBACK_MANDATORY, status).apply();
    }

    public static String getDrFeedbackMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DR_FEEDBACK_MANDATORY, "");
    }

    public static void setDrFeedbackNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_FEEDBACK_NEED, status).apply();
    }

    public static String getDrFeedbackNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DR_FEEDBACK_NEED, "");
    }

    public static void setChemistFeedbackNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_FEEDBACK_NEED, status).apply();
    }

    public static String getChemistFeedbackNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHEMIST_FEEDBACK_NEED, "");
    }

    public static void setStockistFeedbackNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_FEEDBACK_NEED, status).apply();
    }

    public static String getStockistFeedbackNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STOCKIST_FEEDBACK_NEED, "");
    }

    public static void setUndrFeedbackNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_FEEDBACK_NEED, status).apply();
    }

    public static String getUndrFeedbackNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(UNDR_FEEDBACK_NEED, "");
    }

    public static void setCipFeedbackNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CIP_FEEDBACK_NEED, status).apply();
    }

    public static String getCipFeedbackNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CIP_FEEDBACK_NEED, "");
    }

    public static void setDrPobNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_POB_NEED, status).apply();
    }

    public static String getDrPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DR_POB_NEED, "");
    }

    public static void setDrPobMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_POB_MANDATORY, status).apply();
    }

    public static String getDrPobMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DR_POB_MANDATORY, "");
    }

    public static void setChemistPobNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_POB_NEED, status).apply();
    }

    public static String getChemistPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHEMIST_POB_NEED, "");
    }

    public static void setChemistPobMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_POB_MANDATORY, status).apply();
    }

    public static String getChemistPobMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHEMIST_POB_MANDATORY, "");
    }

    public static void setStockistPobMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_POB_MANDATORY, status).apply();
    }

    public static String getStockistPobMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STOCKIST_POB_MANDATORY, "");
    }

    public static void setUndrPobMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_POB_MANDATORY, status).apply();
    }

    public static String getUndrPobMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(UNDR_POB_MANDATORY, "");
    }

    public static void setCipPobNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CIP_POB_NEED, status).apply();
    }

    public static String getCipPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CIP_POB_NEED, "");
    }

    public static void setCipPobMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CIP_POB_NEED, status).apply();
    }

    public static String getCipPobMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CIP_POB_MANDATORY, "");
    }

    public static void setDrRemarksNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_REMARKS_NEED, status).apply();
    }

    public static String getDrRemarksNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DR_REMARKS_NEED, "");
    }

    public static void setSalesReportNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SALES_REPORT_NEED, status).apply();
    }

    public static String getSalesReportNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SALES_REPORT_NEED, "");
    }

    public static void setVisitControl(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(VISIT_CONTROL, status).apply();
    }

    public static String getVisitControl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(VISIT_CONTROL, "");
    }

    public static void setMultiCluster(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MULTI_CLUSTER, status).apply();
    }

    public static String getMultiCluster(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(MULTI_CLUSTER, "");
    }

    public static void setSampleValidation(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SAMPLE_VALIDATION, status).apply();
    }

    public static String getSampleValidation(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SAMPLE_VALIDATION, "");
    }

    public static void setInputValidation(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(INPUT_VALIDATION, status).apply();
    }

    public static String getInputValidation(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(INPUT_VALIDATION, "");
    }

    public static void setLeaveEntitlementNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LEAVE_ENTITLEMENT_NEED, status).apply();
    }

    public static String getLeaveEntitlementNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(LEAVE_ENTITLEMENT_NEED, "");
    }

    public static void setReminderCallNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(REMINDER_CALL_NEED, status).apply();
    }

    public static String getReminderCallNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(REMINDER_CALL_NEED, "");
    }

    public static void setReminderCallPrdMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(REMINDER_CALL_PRD_MANDATORY, status).apply();
    }

    public static String setReminderCallPrdMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(REMINDER_CALL_PRD_MANDATORY, "");
    }

    public static void setGeotagApprovalNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEOTAG_APPROVAL_NEED, status).apply();
    }

    public static String getGeotagApprovalNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(GEOTAG_APPROVAL_NEED, "");
    }

    public static void setDrPrdNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_PRD_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_PRD_NEED, status).apply();
    }

    public static String getDrPrdNeed(Context context) {
        return context.getSharedPreferences(DR_PRD_NEED, MODE_PRIVATE).getString(DR_PRD_NEED, "");
    }

    public static void setDrInpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_INP_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_INP_NEED, status).apply();
    }

    public static String getDrInpNeed(Context context) {
        return context.getSharedPreferences(DR_INP_NEED, MODE_PRIVATE).getString(DR_INP_NEED, "");
    }

    public static void setChePrdNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHE_PRD_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHE_PRD_NEED, status).apply();
    }

    public static String setChePrdNeed(Context context) {
        return context.getSharedPreferences(CHE_PRD_NEED, MODE_PRIVATE).getString(CHE_PRD_NEED, "");
    }

    public static void setCheInpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHE_INP_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHE_INP_NEED, status).apply();
    }

    public static String getCheInpNeed(Context context) {
        return context.getSharedPreferences(CHE_INP_NEED, MODE_PRIVATE).getString(CHE_INP_NEED, "");
    }

    public static void setStkPrdNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STK_PRD_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STK_PRD_NEED, status).apply();
    }

    public static String getStkPrdNeed(Context context) {
        return context.getSharedPreferences(STK_PRD_NEED, MODE_PRIVATE).getString(STK_PRD_NEED, "");
    }

    public static void setStkInpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STK_INP_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STK_INP_NEED, status).apply();
    }

    public static String getStkInpNeed(Context context) {
        return context.getSharedPreferences(STK_INP_NEED, MODE_PRIVATE).getString(STK_INP_NEED, "");
    }

    public static void setUnDrPrdNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_PRD_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_PRD_NEED, status).apply();
    }

    public static String getUnDrPrdNeed(Context context) {
        return context.getSharedPreferences(UNDR_PRD_NEED, MODE_PRIVATE).getString(UNDR_PRD_NEED, "");
    }

    public static void setUnDrInpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_INP_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_INP_NEED, status).apply();
    }

    public static String getUnDrInpNeed(Context context) {
        return context.getSharedPreferences(UNDR_INP_NEED, MODE_PRIVATE).getString(UNDR_INP_NEED, "");
    }

    public static void setCaptionDr(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR, status).apply();
    }

    public static String getCaptionDr(Context context) {
        return context.getSharedPreferences(CAPTION_DR, MODE_PRIVATE).getString(CAPTION_DR, "");
    }

    public static void setCaptionChemist(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHEMIST, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHEMIST, status).apply();
    }

    public static String getCaptionChemist(Context context) {
        return context.getSharedPreferences(CAPTION_CHEMIST, MODE_PRIVATE).getString(CAPTION_CHEMIST, "");
    }

    public static void setCaptionStockist(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_STOCKIST, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_STOCKIST, status).apply();
    }

    public static String getCaptionStockist(Context context) {
        return context.getSharedPreferences(CAPTION_STOCKIST, MODE_PRIVATE).getString(CAPTION_STOCKIST, "");
    }

    public static void setCaptionUnDr(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UNDR, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UNDR, status).apply();
    }

    public static String getCaptionUnDr(Context context) {
        return context.getSharedPreferences(CAPTION_UNDR, MODE_PRIVATE).getString(CAPTION_UNDR, "");
    }

    public static void setCaptionCip(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CIP, status).apply();
    }

    public static String getCaptionCip(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CAPTION_CIP, "");
    }


    public static void setCaptionDrPrd(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR_PRD, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR_PRD, status).apply();
    }

    public static String getCaptionDrPrd(Context context) {
        return context.getSharedPreferences(CAPTION_DR_PRD, MODE_PRIVATE).getString(CAPTION_DR_PRD, "");
    }

    public static void setCaptionChemistPrd(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHEMIST_PRD, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHEMIST_PRD, status).apply();
    }

    public static String getCaptionChemistPrd(Context context) {
        return context.getSharedPreferences(CAPTION_CHEMIST_PRD, MODE_PRIVATE).getString(CAPTION_CHEMIST_PRD, "");
    }

    public static void setCaptionStockistPrd(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_STOCKIST_PRD, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_STOCKIST_PRD, status).apply();
    }

    public static String getCaptionStockistPrd(Context context) {
        return context.getSharedPreferences(CAPTION_STOCKIST_PRD, MODE_PRIVATE).getString(CAPTION_STOCKIST_PRD, "");
    }

    public static void setCaptionUnDrPrd(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UNDR_PRD, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UNDR_PRD, status).apply();
    }

    public static String getCaptionUnDrPrd(Context context) {
        return context.getSharedPreferences(CAPTION_UNDR_PRD, MODE_PRIVATE).getString(CAPTION_UNDR_PRD, "");
    }

    public static void setCaptionDrInp(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR_INP, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR_INP, status).apply();
    }

    public static String getCaptionDrInp(Context context) {
        return context.getSharedPreferences(CAPTION_DR_INP, MODE_PRIVATE).getString(CAPTION_DR_INP, "");
    }

    public static void setCaptionChemistInp(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHEMIST_INP, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHEMIST_INP, status).apply();
    }

    public static String getCaptionChemistInp(Context context) {
        return context.getSharedPreferences(CAPTION_CHEMIST_INP, MODE_PRIVATE).getString(CAPTION_CHEMIST_INP, "");
    }

    public static void setCaptionStockistInp(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_STOCKIST_INP, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_STOCKIST_INP, status).apply();
    }

    public static String getCaptionStockistInp(Context context) {
        return context.getSharedPreferences(CAPTION_STOCKIST_INP, MODE_PRIVATE).getString(CAPTION_STOCKIST_INP, "");
    }

    public static void setCaptionUnDrInp(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UN_DR_INP, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UN_DR_INP, status).apply();
    }

    public static String getCaptionUnDrInp(Context context) {
        return context.getSharedPreferences(CAPTION_UN_DR_INP, MODE_PRIVATE).getString(CAPTION_UN_DR_INP, "");
    }

    public static void setDrJwNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_JW_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_JW_NEED, status).apply();
    }

    public static String getDrJwNeed(Context context) {
        return context.getSharedPreferences(DR_JW_NEED, MODE_PRIVATE).getString(DR_JW_NEED, "");
    }

    public static void setChemistJwNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_JW_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_JW_NEED, status).apply();
    }

    public static String getChemistJwNeed(Context context) {
        return context.getSharedPreferences(CHEMIST_JW_NEED, MODE_PRIVATE).getString(CHEMIST_JW_NEED, "");
    }

    public static void setStockistJwNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STOCKIST_JW_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_JW_NEED, status).apply();
    }

    public static String getStockistJwNeed(Context context) {
        return context.getSharedPreferences(STOCKIST_JW_NEED, MODE_PRIVATE).getString(STOCKIST_JW_NEED, "");
    }

    public static void setUndJwNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_JW_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_JW_NEED, status).apply();
    }

    public static String getUndJwNeed(Context context) {
        return context.getSharedPreferences(UNDR_JW_NEED, MODE_PRIVATE).getString(UNDR_JW_NEED, "");
    }

    public static void setCaptionCheSamQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHE_SAM_QTY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHE_SAM_QTY, status).apply();
    }

    public static String getCaptionCheSamQty(Context context) {
        return context.getSharedPreferences(CAPTION_CHE_SAM_QTY, MODE_PRIVATE).getString(CAPTION_CHE_SAM_QTY, "");
    }

    public static void setChemistRcpaMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_RCPA_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_RCPA_MANDATORY, status).apply();
    }


    public static String getChemistRcpaMandatory(Context context) {
        return context.getSharedPreferences(CHEMIST_RCPA_MANDATORY, MODE_PRIVATE).getString(CHEMIST_RCPA_MANDATORY, "");
    }

    public static void setCaptionDrSamQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR_SAM_QTY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR_SAM_QTY, status).apply();
    }

    public static String getCaptionDrSamQty(Context context) {
        return context.getSharedPreferences(CAPTION_DR_SAM_QTY, MODE_PRIVATE).getString(CAPTION_DR_SAM_QTY, "");
    }

    public static void setCaptionDrRxQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR_RX_QTY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR_RX_QTY, status).apply();
    }

    public static String getCaptionDrRxQty(Context context) {
        return context.getSharedPreferences(CAPTION_DR_RX_QTY, MODE_PRIVATE).getString(CAPTION_DR_RX_QTY, "");
    }

    public static void setCaptionCheRxQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHE_RX_QTY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHE_RX_QTY, status).apply();
    }

    public static String getCaptionCheRxQty(Context context) {
        return context.getSharedPreferences(CAPTION_CHE_RX_QTY, MODE_PRIVATE).getString(CAPTION_CHE_RX_QTY, "");
    }

    public static void setCaptionStkRxQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_STK_RX_QTY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_STK_RX_QTY, status).apply();
    }

    public static String getCaptionStkRxQty(Context context) {
        return context.getSharedPreferences(CAPTION_STK_RX_QTY, MODE_PRIVATE).getString(CAPTION_STK_RX_QTY, "");
    }

    public static void setMgrDrRcpaMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(MGR_DR_RCPA_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MGR_DR_RCPA_MANDATORY, status).apply();
    }

    public static String getMgrDrRcpaMandatory(Context context) {
        return context.getSharedPreferences(MGR_DR_RCPA_MANDATORY, MODE_PRIVATE).getString(MGR_DR_RCPA_MANDATORY, "");
    }

    public static void setMgrCheRcpaMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(MGR_CHE_RCPA_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MGR_CHE_RCPA_MANDATORY, status).apply();
    }

    public static String getMgrCheRcpaMandatory(Context context) {
        return context.getSharedPreferences(MGR_CHE_RCPA_MANDATORY, MODE_PRIVATE).getString(MGR_CHE_RCPA_MANDATORY, "");
    }

    public static void setCaptionCluster(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CLUSTER, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CLUSTER, status).apply();
    }

    public static String getCaptionCluster(Context context) {
        return context.getSharedPreferences(CAPTION_CLUSTER, MODE_PRIVATE).getString(CAPTION_CLUSTER, "");
    }

    public static void setDrEventCaptureMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_EVENT_CAPTURE_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_EVENT_CAPTURE_MANDATORY, status).apply();
    }

    public static String getDrEventCaptureMandatory(Context context) {
        return context.getSharedPreferences(DR_EVENT_CAPTURE_MANDATORY, MODE_PRIVATE).getString(DR_EVENT_CAPTURE_MANDATORY, "");
    }

    public static void setCheEventCaptureMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHE_EVENT_CAPTURE_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHE_EVENT_CAPTURE_MANDATORY, status).apply();
    }

    public static String getCheEventCaptureMandatory(Context context) {
        return context.getSharedPreferences(CHE_EVENT_CAPTURE_MANDATORY, MODE_PRIVATE).getString(CHE_EVENT_CAPTURE_MANDATORY, "");
    }

    public static void setStkEventCaptureMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STK_EVENT_CAPTURE_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STK_EVENT_CAPTURE_MANDATORY, status).apply();
    }

    public static String getStkEventCaptureMandatory(Context context) {
        return context.getSharedPreferences(STK_EVENT_CAPTURE_MANDATORY, MODE_PRIVATE).getString(STK_EVENT_CAPTURE_MANDATORY, "");
    }

    public static void setUndrEventCaptureMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_EVENT_CAPTURE_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_EVENT_CAPTURE_MANDATORY, status).apply();
    }

    public static String getUndrEventCaptureMandatory(Context context) {
        return context.getSharedPreferences(UNDR_EVENT_CAPTURE_MANDATORY, MODE_PRIVATE).getString(UNDR_EVENT_CAPTURE_MANDATORY, "");
    }

    public static void setCaptionUnDrRxQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UNDR_RX_QTY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UNDR_RX_QTY, status).apply();
    }

    public static String getCaptionUnDrRxQty(Context context) {
        return context.getSharedPreferences(CAPTION_UNDR_RX_QTY, MODE_PRIVATE).getString(CAPTION_UNDR_RX_QTY, "");
    }

    public static void setCaptionUnDrSamQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UNDR_SAM_QTY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UNDR_SAM_QTY, status).apply();
    }

    public static String getCaptionUnDrSamQty(Context context) {
        return context.getSharedPreferences(CAPTION_UNDR_SAM_QTY, MODE_PRIVATE).getString(CAPTION_UNDR_SAM_QTY, "");
    }

    public static void setDrRxNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_RX_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_RX_NEED, status).apply();
    }

    public static String getDrRxNeed(Context context) {
        return context.getSharedPreferences(DR_RX_NEED, MODE_PRIVATE).getString(DR_RX_NEED, "");
    }

    public static void setDrSamNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_SAM_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_SAM_NEED, status).apply();
    }

    public static String getDrSamNeed(Context context) {
        return context.getSharedPreferences(DR_SAM_NEED, MODE_PRIVATE).getString(DR_SAM_NEED, "");
    }

    public static void setDrRxQtyMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_RX_QTY_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_RX_QTY_MANDATORY, status).apply();
    }

    public static String getDrRxQtyMandatory(Context context) {
        return context.getSharedPreferences(DR_RX_QTY_MANDATORY, MODE_PRIVATE).getString(DR_RX_QTY_MANDATORY, "");
    }

    public static void setDrSamQtyMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_SAM_QTY_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_SAM_QTY_MANDATORY, status).apply();
    }

    public static String getDrSamQtyMandatory(Context context) {
        return context.getSharedPreferences(DR_SAM_QTY_MANDATORY, MODE_PRIVATE).getString(DR_SAM_QTY_MANDATORY, "");
    }

    public static void setCheckInOutNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHECK_IN_OUT_NEED, status).apply();
    }

    public static String getCheckInOutNeed(Context context) {
        return context.getSharedPreferences(CHECK_IN_OUT_NEED, MODE_PRIVATE).getString(CHECK_IN_OUT_NEED, "");
    }

    public static void setDrEventCapture(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_EVENT_CAPTURE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_EVENT_CAPTURE, status).apply();
    }

    public static String getDrEventCapture(Context context) {
        return context.getSharedPreferences(DR_EVENT_CAPTURE, MODE_PRIVATE).getString(DR_EVENT_CAPTURE, "");
    }

    public static void setChemistEventCapture(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_EVENT_CAPTURE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_EVENT_CAPTURE, status).apply();
    }

    public static String getChemistEventCapture(Context context) {
        return context.getSharedPreferences(CHEMIST_EVENT_CAPTURE, MODE_PRIVATE).getString(CHEMIST_EVENT_CAPTURE, "");
    }

    public static void setStockistEventCapture(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STOCKIST_EVENT_CAPTURE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_EVENT_CAPTURE, status).apply();
    }

    public static String getStockistEventCapture(Context context) {
        return context.getSharedPreferences(STOCKIST_EVENT_CAPTURE, MODE_PRIVATE).getString(STOCKIST_EVENT_CAPTURE, "");
    }

    public static void setUndrEventCapture(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_EVENT_CAPTURE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_EVENT_CAPTURE, status).apply();
    }

    public static String getUndrEventCapture(Context context) {
        return context.getSharedPreferences(UNDR_EVENT_CAPTURE, MODE_PRIVATE).getString(UNDR_EVENT_CAPTURE, "");
    }

    public static void setDrPrdMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_PRD_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_PRD_MANDATORY, status).apply();
    }

    public static String getDrPrdMandatory(Context context) {
        return context.getSharedPreferences(DR_PRD_MANDATORY, MODE_PRIVATE).getString(DR_PRD_MANDATORY, "");
    }

    public static void setDrInpMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_INP_MANDATORY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_INP_MANDATORY, status).apply();
    }

    public static String getDrInpMandatory(Context context) {
        return context.getSharedPreferences(DR_INP_MANDATORY, MODE_PRIVATE).getString(DR_INP_MANDATORY, "");
    }

    public static void setDrRcpaNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_RCPA_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_RCPA_NEED, status).apply();
    }

    public static String getDrRcpaNeed(Context context) {
        return context.getSharedPreferences(DR_RCPA_NEED, MODE_PRIVATE).getString(DR_RCPA_NEED, "");
    }

    public static void setChemistRcpaNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_RCPA_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_RCPA_NEED, status).apply();
    }

    public static String getChemistRcpaNeed(Context context) {
        return context.getSharedPreferences(CHEMIST_RCPA_NEED, MODE_PRIVATE).getString(CHEMIST_RCPA_NEED, "");
    }

    public static void setDrGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_GEO_TAG_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_GEO_TAG_NEED, status).apply();
    }

    public static String getDrGeoTagNeed(Context context) {
        return context.getSharedPreferences(DR_GEO_TAG_NEED, MODE_PRIVATE).getString(DR_GEO_TAG_NEED, "");
    }

    public static void setChemistGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_GEO_TAG_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_GEO_TAG_NEED, status).apply();
    }

    public static String getChemistGeoTagNeed(Context context) {
        return context.getSharedPreferences(CHEMIST_GEO_TAG_NEED, MODE_PRIVATE).getString(CHEMIST_GEO_TAG_NEED, "");
    }

    public static void setStockistGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STK_GEO_TAG_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STK_GEO_TAG_NEED, status).apply();
    }

    public static String getStockistGeoTagNeed(Context context) {
        return context.getSharedPreferences(STK_GEO_TAG_NEED, MODE_PRIVATE).getString(STK_GEO_TAG_NEED, "");
    }

    public static void setCipGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CIP_GEO_TAG_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CIP_GEO_TAG_NEED, status).apply();
    }

    public static String getCipGeoTagNeed(Context context) {
        return context.getSharedPreferences(CIP_GEO_TAG_NEED, MODE_PRIVATE).getString(CIP_GEO_TAG_NEED, "");
    }

    public static void setUndrGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_GEO_TAG_NEED, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_GEO_TAG_NEED, status).apply();
    }

    public static String getUndrGeoTagNeed(Context context) {
        return context.getSharedPreferences(UNDR_GEO_TAG_NEED, MODE_PRIVATE).getString(UNDR_GEO_TAG_NEED, "");
    }

    public static void setGeofencingCircleRadius(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(GEOFENCING_CIRCLE_RADIUS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEOFENCING_CIRCLE_RADIUS, status).apply();
    }

    public static String getGeofencingCircleRadius(Context context) {
        return context.getSharedPreferences(GEOFENCING_CIRCLE_RADIUS, MODE_PRIVATE).getString(GEOFENCING_CIRCLE_RADIUS, "");
    }

    public static void setGeotagImage(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(GEOTAG_IMAGE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEOTAG_IMAGE, status).apply();
    }

    public static String getGeotagImage(Context context) {
        return context.getSharedPreferences(GEOTAG_IMAGE, MODE_PRIVATE).getString(GEOTAG_IMAGE, "");
    }

    public static void setDeviceRegId(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DEVICE_REG_ID, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DEVICE_REG_ID, status).apply();
    }

    public static String getDeviceRegId(Context context) {
        return context.getSharedPreferences(DEVICE_REG_ID, MODE_PRIVATE).getString(DEVICE_REG_ID, "");
    }
}
