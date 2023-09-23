package saneforce.sanclm.storage;

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


    //Login
    public static final String LOGIN_STATE = "login_state";
    public static final String DEVICE_ID = "device_id";
    public static final String FCM_TOKEN = "fcm_token";

    //Master Sync
    public static final String MASTER_LAST_SYNC = "last_sync";

    //Map Activity
    public static final String MAP_SELECTED_TAB = "selected_tab_map";

    //SetUp
    public static final String GEO_NEED = "geo_need";
    public static final String GEO_CHECK = "geo_check";
    public static final String T_BASE = "t_base";
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
    public static final String FEEDBACK_NEED = "feedback_need";
    public static final String CHECK_IN_OUT_NEED = "check_in_out_need";
    public static final String DR_EVENT_CAPTURE = "dr_event_capture";
    public static final String CHEMIST_EVENT_CAPTURE = "che_event_capture";
    public static final String STOCKIST_EVENT_CAPTURE = "stockiest_event_capture";
    public static final String UNDR_EVENT_CAPTURE = "undr_event_capture";
    public static final String DR_PRD_MANDATORY = "dr_prd_man";
    public static final String DR_INP_MANDATORY = "dr_inp_man";
    public static final String DR_RCPA_NEED = "dr_rcpa_need";
    public static final String CHEMIST_RCPA_NEED = "chemist_rcpa_need";
    public static final String MULTIPLE_DR_NEED = "multiple_dr_need";
    public static final String MAIL_NEED = "mail_need";
    public static final String DR_DOB_DOW = "dr_dob_dow";
    public static final String DR_GEO_TAG_NEED = "dr_geo_tag_need";
    public static final String CHEMIST_GEO_TAG_NEED = "che_geo_tag_need";
    public static final String STK_GEO_TAG_NEED = "stk_geo_tag_need";
    public static final String UNDR_GEO_TAG_NEED = "undr_geo_tag_need";
    public static final String CIP_GEO_TAG_NEED = "cip_geo_tag_need";
    public static final String GEOFENCING_CIRCLE_RADIUS = "map_circle_radius";
    public static final String GEOTAG_IMAGE = "geo_tag_img";
    public static final String DEVICE_REG_ID = "device_reg_id";
    public static final String ATTENDANCE = "attendance";
    public static final String MCL_DETAILING = "mcl_detailing";
    public static final String CIRCULAR = "circular";
    public static final String DR_NEXT_VISIT = "dr_nxt_visit";
    public static final String DR_NEXT_VISIT_MANDATORY = "dr_nxt_visit_man";
    public static final String DR_POLICY = "dr_policy";
    public static final String DR_CALL_TYPE = "dr_call_type";
    public static final String DR_CALL_TYPE_MODE = "dr_call_type_mode";
    public static final String SKIP_SLIDE_DEMO = "skip_slide_demo";
    public static final String RATING_BASED_SLIDE = "rating_based_slide";
    public static final String MEET_EVENT_NEED = "meet_event_need";
    public static final String ACTIVITY_NEED = "activity_need";
    public static final String N_ACTIVITY_NEED = "n_activity_need";
    public static final String DR_ACTIVITY_NEED = "dr_activity_need";
    public static final String CHEMIST_ACTIVITY_NEED = "che_activity_need";
    public static final String STOCKIST_ACTIVITY_NEED = "stk_activity_need";
    public static final String UN_DR_ACTIVITY_NEED = "undr_activity_need";
    public static final String SURVEY_NEED = "survey_need";
    public static final String DR_SURVEY_NEED = "dr_survey_need";
    public static final String CHEMIST_SURVEY_NEED = "che_survey_need";
    public static final String STOCKIST_SURVEY_NEED = "stk_survey_need";
    public static final String UN_DR_SURVEY_NEED = "undr_survey_need";
    public static final String SPEC_FILTER = "spec_filter";
    public static final String MISSED_ENTRY = "missed_entry";
    //  public static final String ADD_NEW_DR_NEED= "add_new_dr_need";
    public static final String MAX_RATING_STAR = "max_rate_star";
    public static final String CHEMIST_RX_NEED = "chm_rx_need";
    public static final String ADD_DR = "add_dr";
    public static final String SHOW_DELETE_OPTION = "show_delete_option";
    public static final String DETAILING_CHEM = "detailing_chem";
    public static final String DETAILING_STOCKIST = "detailing_stk";
    public static final String DETAILING_UN_DR = "detailing_undr";
    public static final String ADD_CHEMIST = "add_chemist";
    public static final String STOCKIST_POB_NEED = "stk_pob_need";
    public static final String UN_DR_POB_NEED = "undr_pob_need";
    public static final String TP_NEED = "tp_need";
    public static final String PAST_LEAVE_POST = "past_leave_post";
    public static final String RCPA_MANDATORY = "rcpa_man";
    public static final String QUIZ_NEED = "quiz_need";
    public static final String QUIZ_NEED_MANDATORY = "quiz_need_man";
    public static final String MISSED_DATE_MANDATORY = "missed_date_man";
    public static final String DELAY_CONTROL = "delay_control";
    public static final String DCR_LOCK_DAYS = "dcr_lock_days";
    public static final String TP_DCR_DEVIATION = "tp_dcr_deviation";
    public static final String TP_BASED_DCR = "tp_based_dcr";
    public static final String TP_MANDATORY_NEED = "tp_based_dcr";
    public static final String TP_START_DATE = "tp_start_date";
    public static final String TP_END_DATE = "tp_end_date";
    public static final String CHEMIST_SAM_QTY_NEED = "che_sam_qty_need";
    public static final String DR_JW_MANDATORY = "dr_jw_man";
    public static final String CHEMIST_JW_MANDATORY = "chemist_jw_man";
    public static final String STOCKIST_JW_MANDATORY = "stockiest_jw_man";
    public static final String UN_DR_JW_MANDATORY = "undr_jw_man";
    public static final String CIP_EVENT_CAPTURE = "cip_event_cap";
    public static final String DR_FEEDBACK_NEED = "dr_feedback_need";
    public static final String CHEMIST_FEEDBACK_NEED = "che_feedback_need";
    public static final String STOCKIST_FEEDBACK_NEED = "stk_feedback_need";
    public static final String UN_DR_FEEDBACK_NEED = "undr_feedback_need";
    public static final String CIP_FEEDBACK_NEED = "cip_feedback_need";
    public static final String DR_POB_NEED = "dr_pob_need";
    public static final String DR_POB_MANDATORY = "dr_pob_man";
    public static final String CHEMIST_POB_NEED = "che_pob_need";
    public static final String CHEMIST_POB_MANDATORY = "che_pob_man";
    public static final String STOCKIST_POB_MANDATORY = "stk_pob_man";
    public static final String UN_DR_POB_MANDATORY = "undr_pob_man";
    public static final String CIP_POB_NEED = "cip_pob_need";
    public static final String CIP_POB_MANDATORY = "cip_pob_man";
    public static final String DR_REMARKS_NEED = "dr_remarks_need";
    public static final String TARGET_REPORT_NEED = "target_report_need";
    public static final String VISIT_CONTROL = "visit_control";
    public static final String MULTI_CLUSTER = "multi_cluster";
    public static final String SAMPLE_VALIDATION = "sample_validation";
    public static final String INPUT_VALIDATION = "input_validation";
    public static final String LEAVE_ENTITLEMENT_NEED = "leave_entitlement_need";
    public static final String REMINDER_NEED = "redminder_need";
    public static final String REMINDER_PRD_MANDATORY = "redminder_prd_man";


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
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(REPORTS_URL, "");
    }

    public static String getSlideUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SLIDES_URL, "");
    }

    public static String getLogoUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(LOGO_URL, "");
    }

    public static void saveSettingState(Context context, boolean state) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SETTING_STATE, state).apply();
    }

    public static boolean getSettingState(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(SETTING_STATE, false);
    }

    public static void saveLoginState(Context context, boolean state) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN_STATE, state).apply();
    }

    public static boolean getLoginState(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(LOGIN_STATE, false);
    }

    public static void saveDeviceId(Context context, String deviceId) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DEVICE_ID, deviceId).apply();
    }

    public static String getDeviceId(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DEVICE_ID, "");
    }

    public static void saveFcmToken(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(FCM_TOKEN, token).apply();
    }

    public static String getFcmToken(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(FCM_TOKEN, "");
    }

    public static void saveMasterLastSync(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MASTER_LAST_SYNC, date).apply();
    }

    public static String getLastSync(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MASTER_LAST_SYNC, "");
    }

    public static void setGeoNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(GEO_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEO_NEED, status).apply();
    }

    public static String getGeoNeed(Context context) {
        return context.getSharedPreferences(GEO_NEED, Context.MODE_PRIVATE).getString(GEO_NEED, "");
    }

    public static void setGeoCheck(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(GEO_CHECK, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEO_CHECK, status).apply();
    }

    public static String getGeoCheck(Context context) {
        return context.getSharedPreferences(GEO_CHECK, Context.MODE_PRIVATE).getString(GEO_CHECK, "");
    }

    public static void setChemistNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_NEED, status).apply();
    }

    public static String getChemistNeed(Context context) {
        return context.getSharedPreferences(CHEMIST_NEED, Context.MODE_PRIVATE).getString(CHEMIST_NEED, "");
    }

    public static void setStockistNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STOCKIST_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_NEED, status).apply();
    }

    public static String getStockistNeed(Context context) {
        return context.getSharedPreferences(STOCKIST_NEED, Context.MODE_PRIVATE).getString(STOCKIST_NEED, "");
    }

    public static void setUndrNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_NEED, status).apply();
    }

    public static String getUndrNeed(Context context) {
        return context.getSharedPreferences(UNDR_NEED, Context.MODE_PRIVATE).getString(UNDR_NEED, "");
    }

    public static void setDrPrdNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_PRD_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_PRD_NEED, status).apply();
    }

    public static String getDrPrdNeed(Context context) {
        return context.getSharedPreferences(DR_PRD_NEED, Context.MODE_PRIVATE).getString(DR_PRD_NEED, "");
    }

    public static void setDrInpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_INP_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_INP_NEED, status).apply();
    }

    public static String getDrInpNeed(Context context) {
        return context.getSharedPreferences(DR_INP_NEED, Context.MODE_PRIVATE).getString(DR_INP_NEED, "");
    }

    public static void setChePrdNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHE_PRD_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHE_PRD_NEED, status).apply();
    }

    public static String setChePrdNeed(Context context) {
        return context.getSharedPreferences(CHE_PRD_NEED, Context.MODE_PRIVATE).getString(CHE_PRD_NEED, "");
    }

    public static void setCheInpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHE_INP_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHE_INP_NEED, status).apply();
    }

    public static String getCheInpNeed(Context context) {
        return context.getSharedPreferences(CHE_INP_NEED, Context.MODE_PRIVATE).getString(CHE_INP_NEED, "");
    }

    public static void setStkPrdNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STK_PRD_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STK_PRD_NEED, status).apply();
    }

    public static String getStkPrdNeed(Context context) {
        return context.getSharedPreferences(STK_PRD_NEED, Context.MODE_PRIVATE).getString(STK_PRD_NEED, "");
    }

    public static void setStkInpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STK_INP_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STK_INP_NEED, status).apply();
    }

    public static String getStkInpNeed(Context context) {
        return context.getSharedPreferences(STK_INP_NEED, Context.MODE_PRIVATE).getString(STK_INP_NEED, "");
    }

    public static void setUnDrPrdNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_PRD_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_PRD_NEED, status).apply();
    }

    public static String getUnDrPrdNeed(Context context) {
        return context.getSharedPreferences(UNDR_PRD_NEED, Context.MODE_PRIVATE).getString(UNDR_PRD_NEED, "");
    }

    public static void setUnDrInpNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_INP_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_INP_NEED, status).apply();
    }

    public static String getUnDrInpNeed(Context context) {
        return context.getSharedPreferences(UNDR_INP_NEED, Context.MODE_PRIVATE).getString(UNDR_INP_NEED, "");
    }

    public static void setCaptionDr(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR, status).apply();
    }

    public static String getCaptionDr(Context context) {
        return context.getSharedPreferences(CAPTION_DR, Context.MODE_PRIVATE).getString(CAPTION_DR, "");
    }

    public static void setCaptionChemist(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHEMIST, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHEMIST, status).apply();
    }

    public static String getCaptionChemist(Context context) {
        return context.getSharedPreferences(CAPTION_CHEMIST, Context.MODE_PRIVATE).getString(CAPTION_CHEMIST, "");
    }

    public static void setCaptionStockist(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_STOCKIST, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_STOCKIST, status).apply();
    }

    public static String getCaptionStockist(Context context) {
        return context.getSharedPreferences(CAPTION_STOCKIST, Context.MODE_PRIVATE).getString(CAPTION_STOCKIST, "");
    }

    public static void setCaptionUnDr(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UNDR, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UNDR, status).apply();
    }

    public static String getCaptionUnDr(Context context) {
        return context.getSharedPreferences(CAPTION_UNDR, Context.MODE_PRIVATE).getString(CAPTION_UNDR, "");
    }

    public static void setCaptionDrPrd(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR_PRD, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR_PRD, status).apply();
    }

    public static String getCaptionDrPrd(Context context) {
        return context.getSharedPreferences(CAPTION_DR_PRD, Context.MODE_PRIVATE).getString(CAPTION_DR_PRD, "");
    }

    public static void setCaptionChemistPrd(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHEMIST_PRD, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHEMIST_PRD, status).apply();
    }

    public static String getCaptionChemistPrd(Context context) {
        return context.getSharedPreferences(CAPTION_CHEMIST_PRD, Context.MODE_PRIVATE).getString(CAPTION_CHEMIST_PRD, "");
    }

    public static void setCaptionStockistPrd(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_STOCKIST_PRD, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_STOCKIST_PRD, status).apply();
    }

    public static String getCaptionStockistPrd(Context context) {
        return context.getSharedPreferences(CAPTION_STOCKIST_PRD, Context.MODE_PRIVATE).getString(CAPTION_STOCKIST_PRD, "");
    }

    public static void setCaptionUnDrPrd(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UNDR_PRD, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UNDR_PRD, status).apply();
    }

    public static String getCaptionUnDrPrd(Context context) {
        return context.getSharedPreferences(CAPTION_UNDR_PRD, Context.MODE_PRIVATE).getString(CAPTION_UNDR_PRD, "");
    }

    public static void setCaptionDrInp(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR_INP, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR_INP, status).apply();
    }

    public static String getCaptionDrInp(Context context) {
        return context.getSharedPreferences(CAPTION_DR_INP, Context.MODE_PRIVATE).getString(CAPTION_DR_INP, "");
    }

    public static void setCaptionChemistInp(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHEMIST_INP, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHEMIST_INP, status).apply();
    }

    public static String getCaptionChemistInp(Context context) {
        return context.getSharedPreferences(CAPTION_CHEMIST_INP, Context.MODE_PRIVATE).getString(CAPTION_CHEMIST_INP, "");
    }

    public static void setCaptionStockistInp(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_STOCKIST_INP, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_STOCKIST_INP, status).apply();
    }

    public static String getCaptionStockistInp(Context context) {
        return context.getSharedPreferences(CAPTION_STOCKIST_INP, Context.MODE_PRIVATE).getString(CAPTION_STOCKIST_INP, "");
    }

    public static void setCaptionUnDrInp(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UN_DR_INP, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UN_DR_INP, status).apply();
    }

    public static String getCaptionUnDrInp(Context context) {
        return context.getSharedPreferences(CAPTION_UN_DR_INP, Context.MODE_PRIVATE).getString(CAPTION_UN_DR_INP, "");
    }

    public static void setMapSelectedTab(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(MAP_SELECTED_TAB, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MAP_SELECTED_TAB, status).apply();
    }

    public static String getMapSelectedTab(Context context) {
        return context.getSharedPreferences(MAP_SELECTED_TAB, Context.MODE_PRIVATE).getString(MAP_SELECTED_TAB, "");
    }


    public static void setDrJwNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_JW_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_JW_NEED, status).apply();
    }

    public static String getDrJwNeed(Context context) {
        return context.getSharedPreferences(DR_JW_NEED, Context.MODE_PRIVATE).getString(DR_JW_NEED, "");
    }

    public static void setChemistJwNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_JW_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_JW_NEED, status).apply();
    }

    public static String getChemistJwNeed(Context context) {
        return context.getSharedPreferences(CHEMIST_JW_NEED, Context.MODE_PRIVATE).getString(CHEMIST_JW_NEED, "");
    }

    public static void setStockistJwNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STOCKIST_JW_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_JW_NEED, status).apply();
    }

    public static String getStockistJwNeed(Context context) {
        return context.getSharedPreferences(STOCKIST_JW_NEED, Context.MODE_PRIVATE).getString(STOCKIST_JW_NEED, "");
    }

    public static void setUndJwNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_JW_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_JW_NEED, status).apply();
    }

    public static String getUndJwNeed(Context context) {
        return context.getSharedPreferences(UNDR_JW_NEED, Context.MODE_PRIVATE).getString(UNDR_JW_NEED, "");
    }

    public static void setCaptionCheSamQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHE_SAM_QTY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHE_SAM_QTY, status).apply();
    }

    public static String getCaptionCheSamQty(Context context) {
        return context.getSharedPreferences(CAPTION_CHE_SAM_QTY, Context.MODE_PRIVATE).getString(CAPTION_CHE_SAM_QTY, "");
    }

    public static void setChemistRcpaMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_RCPA_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_RCPA_MANDATORY, status).apply();
    }

    public static String getChemistRcpaMandatory(Context context) {
        return context.getSharedPreferences(CHEMIST_RCPA_MANDATORY, Context.MODE_PRIVATE).getString(CHEMIST_RCPA_MANDATORY, "");
    }

    public static void setCaptionDrSamQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR_SAM_QTY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR_SAM_QTY, status).apply();
    }

    public static String getCaptionDrSamQty(Context context) {
        return context.getSharedPreferences(CAPTION_DR_SAM_QTY, Context.MODE_PRIVATE).getString(CAPTION_DR_SAM_QTY, "");
    }

    public static void setCaptionDrRxQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_DR_RX_QTY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_DR_RX_QTY, status).apply();
    }

    public static String getCaptionDrRxQty(Context context) {
        return context.getSharedPreferences(CAPTION_DR_RX_QTY, Context.MODE_PRIVATE).getString(CAPTION_DR_RX_QTY, "");
    }

    public static void setCaptionCheRxQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CHE_RX_QTY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CHE_RX_QTY, status).apply();
    }

    public static String getCaptionCheRxQty(Context context) {
        return context.getSharedPreferences(CAPTION_CHE_RX_QTY, Context.MODE_PRIVATE).getString(CAPTION_CHE_RX_QTY, "");
    }

    public static void setCaptionStkRxQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_STK_RX_QTY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_STK_RX_QTY, status).apply();
    }

    public static String getCaptionStkRxQty(Context context) {
        return context.getSharedPreferences(CAPTION_STK_RX_QTY, Context.MODE_PRIVATE).getString(CAPTION_STK_RX_QTY, "");
    }

    public static void setMgrDrRcpaMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(MGR_DR_RCPA_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MGR_DR_RCPA_MANDATORY, status).apply();
    }

    public static String getMgrDrRcpaMandatory(Context context) {
        return context.getSharedPreferences(MGR_DR_RCPA_MANDATORY, Context.MODE_PRIVATE).getString(MGR_DR_RCPA_MANDATORY, "");
    }

    public static void setMgrCheRcpaMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(MGR_CHE_RCPA_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MGR_CHE_RCPA_MANDATORY, status).apply();
    }

    public static String getMgrCheRcpaMandatory(Context context) {
        return context.getSharedPreferences(MGR_CHE_RCPA_MANDATORY, Context.MODE_PRIVATE).getString(MGR_CHE_RCPA_MANDATORY, "");
    }

    public static void setCaptionCluster(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_CLUSTER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_CLUSTER, status).apply();
    }

    public static String getCaptionCluster(Context context) {
        return context.getSharedPreferences(CAPTION_CLUSTER, Context.MODE_PRIVATE).getString(CAPTION_CLUSTER, "");
    }

    public static void setDrEventCaptureMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_EVENT_CAPTURE_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_EVENT_CAPTURE_MANDATORY, status).apply();
    }

    public static String getDrEventCaptureMandatory(Context context) {
        return context.getSharedPreferences(DR_EVENT_CAPTURE_MANDATORY, Context.MODE_PRIVATE).getString(DR_EVENT_CAPTURE_MANDATORY, "");
    }

    public static void setCheEventCaptureMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHE_EVENT_CAPTURE_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHE_EVENT_CAPTURE_MANDATORY, status).apply();
    }

    public static String getCheEventCaptureMandatory(Context context) {
        return context.getSharedPreferences(CHE_EVENT_CAPTURE_MANDATORY, Context.MODE_PRIVATE).getString(CHE_EVENT_CAPTURE_MANDATORY, "");
    }

    public static void setStkEventCaptureMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STK_EVENT_CAPTURE_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STK_EVENT_CAPTURE_MANDATORY, status).apply();
    }

    public static String getStkEventCaptureMandatory(Context context) {
        return context.getSharedPreferences(STK_EVENT_CAPTURE_MANDATORY, Context.MODE_PRIVATE).getString(STK_EVENT_CAPTURE_MANDATORY, "");
    }

    public static void setUndrEventCaptureMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_EVENT_CAPTURE_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_EVENT_CAPTURE_MANDATORY, status).apply();
    }

    public static String getUndrEventCaptureMandatory(Context context) {
        return context.getSharedPreferences(UNDR_EVENT_CAPTURE_MANDATORY, Context.MODE_PRIVATE).getString(UNDR_EVENT_CAPTURE_MANDATORY, "");
    }

    public static void setCaptionUnDrRxQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UNDR_RX_QTY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UNDR_RX_QTY, status).apply();
    }

    public static String getCaptionUnDrRxQty(Context context) {
        return context.getSharedPreferences(CAPTION_UNDR_RX_QTY, Context.MODE_PRIVATE).getString(CAPTION_UNDR_RX_QTY, "");
    }

    public static void setCaptionUnDrSamQty(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CAPTION_UNDR_SAM_QTY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CAPTION_UNDR_SAM_QTY, status).apply();
    }

    public static String getCaptionUnDrSamQty(Context context) {
        return context.getSharedPreferences(CAPTION_UNDR_SAM_QTY, Context.MODE_PRIVATE).getString(CAPTION_UNDR_SAM_QTY, "");
    }

    public static void setDrRxNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_RX_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_RX_NEED, status).apply();
    }

    public static String getDrRxNeed(Context context) {
        return context.getSharedPreferences(DR_RX_NEED, Context.MODE_PRIVATE).getString(DR_RX_NEED, "");
    }

    public static void setDrSamNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_SAM_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_SAM_NEED, status).apply();
    }

    public static String getDrSamNeed(Context context) {
        return context.getSharedPreferences(DR_SAM_NEED, Context.MODE_PRIVATE).getString(DR_SAM_NEED, "");
    }

    public static void setDrRxQtyMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_RX_QTY_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_RX_QTY_MANDATORY, status).apply();
    }

    public static String getDrRxQtyMandatory(Context context) {
        return context.getSharedPreferences(DR_RX_QTY_MANDATORY, Context.MODE_PRIVATE).getString(DR_RX_QTY_MANDATORY, "");
    }

    public static void setDrSamQtyMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_SAM_QTY_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_SAM_QTY_MANDATORY, status).apply();
    }

    public static String getDrSamQtyMandatory(Context context) {
        return context.getSharedPreferences(DR_SAM_QTY_MANDATORY, Context.MODE_PRIVATE).getString(DR_SAM_QTY_MANDATORY, "");
    }

    public static void setCheckInOutNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHECK_IN_OUT_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHECK_IN_OUT_NEED, status).apply();
    }

    public static String getCheckInOutNeed(Context context) {
        return context.getSharedPreferences(CHECK_IN_OUT_NEED, Context.MODE_PRIVATE).getString(CHECK_IN_OUT_NEED, "");
    }

    public static void setDrEventCapture(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_EVENT_CAPTURE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_EVENT_CAPTURE, status).apply();
    }

    public static String getDrEventCapture(Context context) {
        return context.getSharedPreferences(DR_EVENT_CAPTURE, Context.MODE_PRIVATE).getString(DR_EVENT_CAPTURE, "");
    }

    public static void setChemistEventCapture(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_EVENT_CAPTURE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_EVENT_CAPTURE, status).apply();
    }

    public static String getChemistEventCapture(Context context) {
        return context.getSharedPreferences(CHEMIST_EVENT_CAPTURE, Context.MODE_PRIVATE).getString(CHEMIST_EVENT_CAPTURE, "");
    }

    public static void setStockistEventCapture(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STOCKIST_EVENT_CAPTURE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STOCKIST_EVENT_CAPTURE, status).apply();
    }

    public static String getStockistEventCapture(Context context) {
        return context.getSharedPreferences(STOCKIST_EVENT_CAPTURE, Context.MODE_PRIVATE).getString(STOCKIST_EVENT_CAPTURE, "");
    }

    public static void setUndrEventCapture(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_EVENT_CAPTURE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_EVENT_CAPTURE, status).apply();
    }

    public static String getUndrEventCapture(Context context) {
        return context.getSharedPreferences(UNDR_EVENT_CAPTURE, Context.MODE_PRIVATE).getString(UNDR_EVENT_CAPTURE, "");
    }

    public static void setDrPrdMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_PRD_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_PRD_MANDATORY, status).apply();
    }

    public static String getDrPrdMandatory(Context context) {
        return context.getSharedPreferences(DR_PRD_MANDATORY, Context.MODE_PRIVATE).getString(DR_PRD_MANDATORY, "");
    }
    public static void setDrInpMandatory(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_INP_MANDATORY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_INP_MANDATORY, status).apply();
    }

    public static String getDrInpMandatory(Context context) {
        return context.getSharedPreferences(DR_INP_MANDATORY, Context.MODE_PRIVATE).getString(DR_INP_MANDATORY, "");
    }

    public static void setDrRcpaNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_RCPA_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_RCPA_NEED, status).apply();
    }

    public static String getDrRcpaNeed(Context context) {
        return context.getSharedPreferences(DR_RCPA_NEED, Context.MODE_PRIVATE).getString(DR_RCPA_NEED, "");
    }

    public static void setChemistRcpaNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_RCPA_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_RCPA_NEED, status).apply();
    }

    public static String getChemistRcpaNeed(Context context) {
        return context.getSharedPreferences(CHEMIST_RCPA_NEED, Context.MODE_PRIVATE).getString(CHEMIST_RCPA_NEED, "");
    }

    public static void setDrGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DR_GEO_TAG_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_GEO_TAG_NEED, status).apply();
    }

    public static String getDrGeoTagNeed(Context context) {
        return context.getSharedPreferences(DR_GEO_TAG_NEED, Context.MODE_PRIVATE).getString(DR_GEO_TAG_NEED, "");
    }
    public static void setChemistGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CHEMIST_GEO_TAG_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHEMIST_GEO_TAG_NEED, status).apply();
    }

    public static String getChemistGeoTagNeed(Context context) {
        return context.getSharedPreferences(CHEMIST_GEO_TAG_NEED, Context.MODE_PRIVATE).getString(CHEMIST_GEO_TAG_NEED, "");
    }

    public static void setStockistGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(STK_GEO_TAG_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STK_GEO_TAG_NEED, status).apply();
    }

    public static String getStockistGeoTagNeed(Context context) {
        return context.getSharedPreferences(STK_GEO_TAG_NEED, Context.MODE_PRIVATE).getString(STK_GEO_TAG_NEED, "");
    }

    public static void setCipGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(CIP_GEO_TAG_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CIP_GEO_TAG_NEED, status).apply();
    }

    public static String getCipGeoTagNeed(Context context) {
        return context.getSharedPreferences(CIP_GEO_TAG_NEED, Context.MODE_PRIVATE).getString(CIP_GEO_TAG_NEED, "");
    }

    public static void setUndrGeoTagNeed(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(UNDR_GEO_TAG_NEED, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(UNDR_GEO_TAG_NEED, status).apply();
    }

    public static String getUndrGeoTagNeed(Context context) {
        return context.getSharedPreferences(UNDR_GEO_TAG_NEED, Context.MODE_PRIVATE).getString(UNDR_GEO_TAG_NEED, "");
    }

    public static void setGeofencingCircleRadius(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(GEOFENCING_CIRCLE_RADIUS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEOFENCING_CIRCLE_RADIUS, status).apply();
    }

    public static String getGeofencingCircleRadius(Context context) {
        return context.getSharedPreferences(GEOFENCING_CIRCLE_RADIUS, Context.MODE_PRIVATE).getString(GEOFENCING_CIRCLE_RADIUS, "");
    }

    public static void setGeotagImage(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(GEOTAG_IMAGE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(GEOTAG_IMAGE, status).apply();
    }

    public static String getGeotagImage(Context context) {
        return context.getSharedPreferences(GEOTAG_IMAGE, Context.MODE_PRIVATE).getString(GEOTAG_IMAGE, "");
    }

    public static void setDeviceRegId(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(DEVICE_REG_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DEVICE_REG_ID, status).apply();
    }

    public static String getDeviceRegId(Context context) {
        return context.getSharedPreferences(DEVICE_REG_ID, Context.MODE_PRIVATE).getString(DEVICE_REG_ID, "");
    }
}
