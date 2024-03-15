package saneforce.santrip.storage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.santrip.activity.slideDownloaderAlertBox.SlideModelClass;

public class SharedPref {

    public static final String SP_NAME = "e_detail";
    public static final String SP_NAME_NOT_DELETE = "e_detail_not_delete";
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
    public static final String LOGI_SITE = "log_site";
    public static final String SAVE_URL_SETTING = "save_url_setting";
    public static final String SAVE_LICENSE_SETTING = "save_url_license";
    //Login
    public static final String LOGIN_USER_ID = "login_userId";
    public static final String LOGIN_USER_PWD = "login_userPwd";
    public static final String LOGIN_STATE = "login_state";
    public static final String DEVICE_ID = "device_id";
    public static final String FCM_TOKEN = "fcm_token";

    public static final String TAG_IMAGE_URL = "tag_image_url";
    public static final String TAG_API_IMAGE_URL = "tag_api_image_url";



// LogIN Data

    public static final String SF_CODE = "SF_Code";
    public static final String SF_NAME = "SF_Name";
    public static final String SF_PASSWORD = "SF_Password";
    public static final String SF_TYPE = "sf_type";
    public static final String DESIG_CODE = "desig_Code";
    public static final String DIVISION_NAME = "Division_name";
    public static final String DESIG = "Desig";
    public static final String HQ_NAME = "HQName";
    public static final String SF_STAT = "SFStat";
    public static final String DIVISION_CODE = "Division_Code";
    public static final String T_BASE = "TBase";
    public static final String GEO_CHK = "GeoChk";
    public static final String GEO_NEED = "GeoNeed";
    public static final String CHM_NEED = "ChmNeed";
    public static final String STK_NEED = "StkNeed";
    public static final String UNL_NEED = "UNLNeed";
    public static final String DP_NEED = "DPNeed";
    public static final String DI_NEED = "DINeed";
    public static final String CHM_RX_QTY = "ChmRxQty";
    public static final String HOS_POB_MD = "HosPOBMd";
    public static final String HOS_POB_ND = "HosPOBNd";
    public static final String SAMPLE_VALIDATION = "sample_validation";
    public static final String INPUT_VALIDATION = "input_validation";
    public static final String DOC_BUSINESS_PRODUCT = "doc_business_product";
    public static final String DOC_BUSINESS_VALUE = "doc_business_value";
    public static final String DCR_DOC_BUSINESS_PRODUCT = "dcr_doc_business_product";
    public static final String DR_MAPPINGPRODUCT = "Dr_mappingproduct";
    public static final String CP_NEED = "CPNeed";
    public static final String CI_NEED = "CINeed";
    public static final String CMPGN_NEED = "CmpgnNeed";
    public static final String SP_NEED = "SPNeed";
    public static final String SI_NEED = "SINeed";
    public static final String VST_ND = "VstNd";
    public static final String MSD_ENTRY = "MsdEntry";
    public static final String NP_NEED = "NPNeed";
    public static final String NI_NEED = "NINeed";
    public static final String CAT_NEED = "Catneed";
    public static final String CHM_AD_QTY = "chm_ad_qty";
    public static final String CAMP_NEED = "Campneed";
    public static final String CHE_BASE = "CHEBase";
    public static final String DR_CAP = "DrCap";
    public static final String CHM_CAP = "ChmCap";
    public static final String STK_CAP = "StkCap";
    public static final String NL_CAP = "NLCap";
    public static final String USER_N = "UserN";
    public static final String PASS = "Pass";
    public static final String DR_RX_Q_CAP = "DrRxQCap";
    public static final String CHM_RCPA_MD = "ChmRcpaMd";
    public static final String CHM_RCPA_MD_MGR = "ChmRcpaMd_Mgr";
    public static final String DR_SMP_Q_CAP = "DrSmpQCap";
    public static final String DR_RX_ND = "DrRxNd";
    public static final String APPROVE_NEED = "Approveneed";
    public static final String EXPENSENEED = "Expenseneed";
    public static final String TERRITORY_VST_ND = "Territory_VstNd";
    public static final String TRAVEL_DISTANCE_NEED = "travelDistance_Need";
    public static final String ANDROID_APP = "Android_App";
    public static final String IOS_APP = "ios_app";
    public static final String DOC_INPUT_CAPTION = "Doc_Input_caption";
    public static final String CHM_INPUT_CAPTION = "Chm_Input_caption";
    public static final String DASHBOARD = "dashboard";
    public static final String SRT_ND = "SrtNd";
    public static final String UL_STK_NEED = "Ul_stk_Need";
    public static final String UL_STK_MANDATORY = "Ul_stk_Mandatory";
    public static final String TP_NEW = "tp_new";
    public static final String TAXNAME_CAPTION = "Taxname_caption";
    public static final String SURVEY_ND = "SurveyNd";
    public static final String ST_STK_NEED = "st_stk_Need";
    public static final String ST_STK_MANDATORY = "st_stk_Mandatory";
    public static final String SRT_MAND_ND = "SrtMandNd";
    public static final String SINGLE_ACTIVITY = "single_activity";
    public static final String QUIZ_NEED_MANDT = "quiz_need_mandt";
    public static final String QUIZ_HEADING = "quiz_heading";
    public static final String QUICKADD = "quickadd";
    public static final String PRODUCT_RATE_EDITABLE = "Product_Rate_Editable";
    public static final String OFFLINE_LOGIN = "offlineLogin";
    public static final String OFFICEWORK_HOME = "officework_home";
    public static final String HOSP_DCR = "hosp_dcr";
    public static final String DR_POLICY = "DrPolicy";
    public static final String DOC_STK_NEED = "Doc_stk_Need";
    public static final String DOC_STK_MANDATORY = "Doc_stk_Mandatory";
    public static final String CUST_SRT_ND = "CustSrtNd";
    public static final String CHM_STK_NEED = "Chm_stk_Need";
    public static final String CHM_STK_MANDATORY = "Chm_stk_Mandatory";
    public static final String ADD_DR = "addDr";
    public static final String ACTIVITY_NEED = "activityneed";
    public static final String SF_EMAIL = "sfEmail";
    public static final String SF_MOBILE = "sfMobile";
    public static final String DS_NAME = "DS_name";
    public static final String ACTIVITY_ND = "ActivityNd";
    public static final String ACTIVITY_MAND = "activityMand";
    public static final String SEQUENTIAL_DCR = "sequential_dcr";
    public static final String MYDAYPLAN_NEED = "mydayplan_need";
    public static final String MISSED_DATE_MAND = "missedDateMand";
    public static final String MEDIA_TRANS_NEED = "mediaTrans_Need";
    public static final String DR_SMP_Q_MD = "DrSmpQMd";
    public static final String DR_RX_Q_MD = "DrRxQMd";
    public static final String MYPLN_RMRKS_MAND = "myplnRmrksMand";
    public static final String STK_INPUT_CAPTION = "Stk_Input_caption";
    public static final String UL_INPUT_CAPTION = "Ul_Input_caption";
    public static final String RCPA_ND = "RcpaNd";
    public static final String RCPA_COMPETITOR_EXTRA = "Rcpa_Competitor_extra";
    public static final String DETAILING_TYPE = "Detailing_type";
    public static final String CIP_SRT_ND = "CipSrtNd";
    public static final String DCR_FIRSTSELFIE = "Dcr_firstselfie";
    public static final String CHM_Q_CAP = "ChmQCap";
    public static final String STK_Q_CAP = "StkQCap";
    public static final String MULTIPLE_DOC_NEED = "multiple_doc_need";
    public static final String MAIL_NEED = "mailneed";
    public static final String CIRCULAR = "circular";
    public static final String DR_FEED_MD = "DrFeedMd";
    public static final String DF_NEED = "DFNeed";
    public static final String CF_NEED = "CFNeed";
    public static final String SF_NEED = "SFNeed";
    public static final String CIP_F_NEED = "CIP_FNeed";
    public static final String NF_NEED = "NFNeed";
    public static final String HF_NEED = "HFNeed";
    public static final String DQ_NEED = "DQNeed";
    public static final String CQ_NEED = "CQNeed";
    public static final String SQ_NEED = "SQNeed";
    public static final String NQ_NEED = "NQNeed";
    public static final String CIP_Q_NEED = "CIP_QNeed";
    public static final String HQ_NEED = "HQNeed";
    public static final String DE_NEED = "DENeed";
    public static final String CE_NEED = "CENeed";
    public static final String SE_NEED = "SENeed";
    public static final String NE_NEED = "NENeed";
    public static final String CIP_E_NEED = "CIP_ENeed";
    public static final String HE_NEED = "HENeed";
    public static final String TP_NEED = "tp_need";
    public static final String CLUSTER_CAP = "cluster_cap";
    public static final String WRK_AREA_NAME = "wrk_area_Name";
    public static final String NL_RX_Q_CAP = "NLRxQCap";
    public static final String NL_SMP_Q_CAP = "NLSmpQCap";
    public static final String APP_DEVICE_ID = "app_device_id";
    public static final String USR_DFD_USERNAME = "UsrDfd_UserName";
    public static final String SF_USER_NAME = "SF_User_Name";
    public static final String ATTENDANCE = "Attendance";
    public static final String DEVICE_ID_NEED = "DeviceId_Need";
    public static final String DOCTOR_DOBDOW = "doctor_dobdow";
    public static final String PRODUCT_POB_NEED_MSG = "product_pob_need_msg";
    public static final String PROD_REMARK = "prod_remark";
    public static final String PROD_REMARK_MD = "prod_remark_md";
    public static final String CIP_NEED = "cip_need";
    public static final String CIP_P_NEED = "CIP_PNeed";
    public static final String CIP_I_NEED = "CIP_INeed";
    public static final String DR_PRD_MD = "DrPrdMd";
    public static final String DR_INP_MD = "DrInpMd";
    public static final String CIP_JOINTWORK_NEED = "CIP_jointwork_Need";
    public static final String CIP_CAPTION = "CIP_Caption";
    public static final String HOSP_CAPTION = "hosp_caption";
    public static final String SEP_RCPA_ND = "Sep_RcpaNd";
    public static final String PAST_LEAVE_POST = "past_leave_post";
    public static final String DLY_CTRL = "DlyCtrl";
    public static final String FEED_ND = "FeedNd";
    public static final String TEMP_ND = "TempNd";
    public static final String PROD_DET_NEED = "prod_det_need";
    public static final String CNT_REMARKS = "cntRemarks";
    public static final String PRODUCT_POB_NEED = "product_pob_need";
    public static final String SECONDARY_ORDER_DISCOUNT = "secondary_order_discount";
    public static final String TARGET_REPORT_MD = "Target_report_md";
    public static final String RCPA_UNIT_ND = "RCPA_unit_nd";
    public static final String CHM_RCPA_NEED = "Chm_RCPA_Need";
    public static final String DR_RCPA_COMPETITOR_NEED = "DrRCPA_competitor_Need";
    public static final String CHM_RCPA_COMPETITOR_NEED = "ChmRCPA_competitor_Need";
    public static final String CURRENTDAY_TPPLANNED = "Currentday_TPplanned";
    public static final String DOC_CLUSTER_BASED = "Doc_cluster_based";
    public static final String CHM_CLUSTER_BASED = "Chm_cluster_based";
    public static final String STK_CLUSTER_BASED = "Stk_cluster_based";
    public static final String ULDOC_CLUSTER_BASED = "UlDoc_cluster_based";
    public static final String MULTI_CLUSTER = "multi_cluster";
    public static final String TERR_BASED_TAG = "Terr_based_Tag";
    public static final String RCPA_MD_MGR = "RcpaMd_Mgr";
    public static final String DR_NEED = "DrNeed";
    public static final String FAQ = "faq";
    public static final String EDIT_HOLIDAY = "edit_holiday";
    public static final String EDIT_WEEKLYOFF = "edit_weeklyoff";
    public static final String TARGET_REPORT_ND = "Target_report_Nd";
    public static final String DCR_LOCK_DAYS = "DcrLockDays";
    public static final String DOC_POB_CAPTION = "Doc_pob_caption";
    public static final String STK_POB_CAPTION = "Stk_pob_caption";
    public static final String CHM_POB_CAPTION = "Chm_pob_caption";
    public static final String ULDOC_POB_CAPTION = "Uldoc_pob_caption";
    public static final String CIP_POB_CAPTION = "CIP_pob_caption";
    public static final String HOSP_POB_CAPTION = "Hosp_pob_caption";
    public static final String DOC_POB_MANDATORY_NEED = "Doc_Pob_Mandatory_Need";
    public static final String CHM_POB_MANDATORY_NEED = "Chm_Pob_Mandatory_Need";
    public static final String DR_SAMP_ND = "DrSampNd";
    public static final String RCPA_MD = "RcpaMd";
    public static final String MISC_EXPENSE_NEED = "misc_expense_need";
    public static final String DOC_POB_NEED = "Doc_Pob_Need";
    public static final String CHM_POB_NEED = "Chm_Pob_Need";
    public static final String STK_POB_NEED = "Stk_Pob_Need";
    public static final String UL_POB_NEED = "Ul_Pob_Need";
    public static final String STK_POB_MANDATORY_NEED = "Stk_Pob_Mandatory_Need";
    public static final String UL_POB_MANDATORY_NEED = "Ul_Pob_Mandatory_Need";
    public static final String DOC_JOINTWORK_NEED = "Doc_jointwork_Need";
    public static final String CHM_JOINTWORK_NEED = "Chm_jointwork_Need";
    public static final String STK_JOINTWORK_NEED = "Stk_jointwork_Need";
    public static final String UL_JOINTWORK_NEED = "Ul_jointwork_Need";
    public static final String DOC_JOINTWORK_MANDATORY_NEED = "Doc_jointwork_Mandatory_Need";
    public static final String CHM_JOINTWORK_MANDATORY_NEED = "Chm_jointwork_Mandatory_Need";
    public static final String STK_JOINTWORK_MANDATORY_NEED = "Stk_jointwork_Mandatory_Need";
    public static final String UL_JOINTWORK_MANDATORY_NEED = "Ul_jointwork_Mandatory_Need";
    public static final String DOC_PRODUCT_CAPTION = "Doc_Product_caption";
    public static final String CHM_PRODUCT_CAPTION = "Chm_Product_caption";
    public static final String STK_PRODUCT_CAPTION = "Stk_Product_caption";
    public static final String UL_PRODUCT_CAPTION = "Ul_Product_caption";
    public static final String REMAINDER_PRD_MD = "Remainder_prd_Md";
    public static final String GEOTAG_NEED = "GEOTagNeed";
    public static final String GEOTAG_NEED_CHE = "GEOTagNeedche";
    public static final String REMAINDER_GEO = "Remainder_geo";
    public static final String GEOTAG_NEED_STOCK = "GEOTagNeedstock";
    public static final String GEOTAG_NEED_CIP = "GeoTagNeedcip";
    public static final String GEOTAG_NEED_UNLST = "GEOTagNeedunlst";
    public static final String DIS_RAD = "DisRad";
    public static final String DEVICE_REG_ID = "DeviceRegId";
    public static final String SFTP_DATE = "SFTPDate";
    public static final String MCL_DET = "MCLDet";
    public static final String RMDR_NEED = "RmdrNeed";
    public static final String EXPENSE_NEED = "expense_need";
    public static final String GEOTAG_IMG = "geoTagImg";
    public static final String DR_RCPA_Q_MD = "DrRcpaQMd";
    public static final String QUES_NEED = "ques_need";
    public static final String HOSP_NEED = "hosp_need";
    public static final String HP_NEED = "HPNeed";
    public static final String HI_NEED = "HINeed";
    public static final String CHMSAMQTY_NEED = "chmsamQty_need";
    public static final String CHM_SMP_CAP = "ChmSmpCap";
    public static final String CALL_FEED_ENTERABLE = "call_feed_enterable";
    public static final String PWD_SETUP = "Pwdsetup";
    public static final String RCPA_EXTRA = "rcpaextra";
    public static final String LEAVE_STATUS = "LeaveStatus";
    public static final String STATE_CODE = "State_Code";
    public static final String SF_EMP_ID = "sf_emp_id";
    public static final String SUBDIVISION_CODE = "subdivision_code";
    public static final String REMAINDER_CALL_CAP = "Remainder_call_cap";
    public static final String CALL_REPORT_FROM_DATE = "call_report_from_date";
    public static final String CALL_REPORT_TO_DATE = "call_report_to_date";
    public static final String CALL_REPORT = "call_report";
    public static final String DAYS = "days";
    public static final String MRHLFDY = "MRHlfDy";
    public static final String ORDER_MANAGEMENT = "Order_management";
    public static final String ORDER_CAPTION = "Order_caption";
    public static final String PRIMARY_ORDER_CAPTION = "Primary_order_caption";
    public static final String SECONDARY_ORDER_CAPTION = "Secondary_order_caption";
    public static final String PRIMARY_ORDER = "Primary_order";
    public static final String SECONDARY_ORDER = "Secondary_order";
    public static final String GST_OPTION = "Gst_option";
    public static final String TPDCR_DEVIATION_APPR_STATUS = "TPDCR_Deviation_Appr_Status";
    public static final String TPDCR_DEVIATION = "TPDCR_Deviation";
    public static final String TPDCR_MGRAPPR = "TPDCR_MGRAppr";
    public static final String NEXT_VST = "NextVst";
    public static final String NEXT_VST_MANDATORY_NEED = "NextVst_Mandatory_Need";
    public static final String APPR_MANDATORY_NEED = "Appr_Mandatory_Need";
    public static final String RCPA_QTY_NEED = "RCPAQty_Need";
    public static final String PROD_STK_NEED = "Prod_Stk_Need";
    public static final String TP_MANDATORY_NEED = "TP_Mandatory_Need";
    public static final String TP_START_DATE = "Tp_Start_Date";
    public static final String PRDFDBACK = "prdfdback";
    public static final String TRACKING_INTERVAL = "tracking_interval";
    public static final String TP_END_DATE = "Tp_End_Date";
    public static final String DR_EVENT_MD = "DrEvent_Md";
    public static final String CHM_EVENT_MD = "ChmEvent_Md";
    public static final String STK_EVENT_MD = "StkEvent_Md";
    public static final String ULDR_EVENT_MD = "UlDrEvent_Md";
    public static final String CIP_EVENT_MD = "CipEvent_Md";
    public static final String HOSP_EVENT_MD = "HospEvent_Md";
    public static final String LEAVE_ENTITLEMENT_NEED = "Leave_entitlement_need";
    public static final String PRIMARYSEC_NEED = "primarysec_need";
    public static final String MGRHLFDY = "MGRHlfDy";
    public static final String NO_OF_TP_VIEW = "No_of_TP_View";
    public static final String CURRENTDAY = "currentDay";
    public static final String DAYPLAN_TP_BASED = "dayplan_tp_based";
    public static final String QUIZ_NEED = "quiz_need";
    public static final String STP = "stp";
    public static final String POB_MINVALUE = "pob_minvalue";
    public static final String TPBASED_DCR = "TPbasedDCR";
    public static final String LOCATION_TRACK = "Location_track";
    public static final String TRACKING_TIME = "tracking_time";
    public static final String ENTRY_FORM_MGR = "entryFormMgr";
    public static final String ENTRY_FORM_NEED = "entryFormNeed";
    public static final String DLY_CTRL_S = "Dly_Ctrls";
    public static final String ANDROID_DETAILING = "Android_Detailing";
    public static final String IOS_DETAILING = "ios_Detailing";
    public static final String SAMPLE_VAL_QTY = "Sample_Val_Qty";
    public static final String INPUT_VAL_QTY = "Input_Val_Qty";
    public static final String QUOTE_TEXT = "quote_Text";
    public static final String PRO_DET_NEED = "pro_det_need";
    public static final String AUTHENTICATION = "Authentication";
    public static final String GEOTAG_APPROVAL_NEED = "GeoTagApprovalNeed";
    public static final String CHM_SRT_ND = "ChmSrtNd";
    public static final String UNLIST_SRT_ND = "UnlistSrtNd";
    public static final String RCPA_COMPETITOR_ADD = "RCPA_competitor_add";
    public static final String GEOTAGGING = "GeoTagging";



    //Master Sync
    public static final String MASTER_LAST_SYNC = "last_sync";
    public static final String HQ_CODE = "hq_code";
    //Map Activity
    public static final String TAGGED_SUCCESSFULLY = "tagged_successfully";
    public static final String CUSTOMER_POSITION = "cust_pos";
    //HomeDashboard
    public static final String CHECK_TODAY_DATE_CHECKINOUT = "check_today_date_checkinout";
    public static final String CHECK_IN_TIME = "check_today_date_checkinout";
    public static final String CHECK_DATE_TODAY_PLAN = "check_date_todayplan";
    public static final String SET_UP_CLICKED_TAB = "set_up_clicked_tab";
    public static final String SKIP_CHECK_IN = "skip_check_in";
    public static final String SELECTED_DATE_CAL = "selected_date_cal";
    //MyDayPlan
    public static final String TodayDayPlanSfCode = "today_plan_sfcode";
    public static final String TodayDayPlanSfName = "today_plan_sfname";
    public static final String TodayDayPlanClusterCode = "today_plan_cluster_code";

    //TodayCalls
    public static final String TODAY_CALL_LIST = "today_call_list";
    //Approval
    public static final String APPROVAL_COUNT = "approval_count";
    // Slide
    public static final String SLIDEID = "slideid";
    public static final String SLIDELIST = "slidelist";
    public static final String SLIDEDOWNCOUNT = "slidedowncount";
    public static final String SYNC_STATUS = "SP_MAS_DETAILS";
    public static SharedPreferences sharedPreferences;
    public static final String SP_CALL_ClEAR_MONTH = "call_clear_month";
    public static final String SETHQCODE = "SETHQCODE";
    public static final String SETHQ_DETAILS = "SETHQ_DETAILS";


    public static final String SLIDE_DOWNLOADING_STATUS = "Slide_downloding_status";
    public static SharedPreferences.Editor editor;

    public static void clearSP(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear().apply();
    }


    public static void InsertLogInData(Context context, JSONObject jsonObject){
        try{

        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SF_CODE, jsonObject.getString("SF_Code"));
        editor.putString(SF_NAME, jsonObject.getString("SF_Name"));
        editor.putString(SF_PASSWORD, jsonObject.getString("SF_Password"));
        editor.putString(SF_TYPE, jsonObject.getString("sf_type"));
        editor.putString(DESIG_CODE, jsonObject.getString("desig_Code"));
        editor.putString(DIVISION_NAME, jsonObject.getString("Division_name"));
        editor.putString(DESIG, jsonObject.getString("Desig"));
        editor.putString(HQ_NAME, jsonObject.getString("HQName"));
        editor.putString(SF_STAT, jsonObject.getString("SFStat"));
        editor.putString(DIVISION_CODE, jsonObject.getString("Division_Code"));
        editor.putString(T_BASE, jsonObject.getString("TBase"));
        editor.putString(GEO_CHK, jsonObject.getString("GeoChk"));
        editor.putString(GEO_NEED, jsonObject.getString("GeoNeed"));
        editor.putString(CHM_NEED, jsonObject.getString("ChmNeed"));
        editor.putString(STK_NEED, jsonObject.getString("StkNeed"));
        editor.putString(UNL_NEED, jsonObject.getString("UNLNeed"));
        editor.putString(DP_NEED, jsonObject.getString("DPNeed"));
        editor.putString(DI_NEED, jsonObject.getString("DINeed"));
        editor.putString(CHM_RX_QTY, jsonObject.getString("ChmRxQty"));
        editor.putString(HOS_POB_MD, jsonObject.getString("HosPOBMd"));
        editor.putString(HOS_POB_ND, jsonObject.getString("HosPOBNd"));
        editor.putString(SAMPLE_VALIDATION, jsonObject.getString("sample_validation"));
        editor.putString(INPUT_VALIDATION, jsonObject.getString("input_validation"));
        editor.putString(DOC_BUSINESS_PRODUCT, jsonObject.getString("doc_business_product"));
        editor.putString(DOC_BUSINESS_VALUE, jsonObject.getString("doc_business_value"));
        editor.putString(DCR_DOC_BUSINESS_PRODUCT, jsonObject.getString("dcr_doc_business_product"));
        editor.putString(DR_MAPPINGPRODUCT, jsonObject.getString("Dr_mappingproduct"));
        editor.putString(CP_NEED, jsonObject.getString("CPNeed"));
        editor.putString(CI_NEED, jsonObject.getString("CINeed"));
        editor.putString(CMPGN_NEED, jsonObject.getString("CmpgnNeed"));
        editor.putString(SP_NEED, jsonObject.getString("SPNeed"));
        editor.putString(SI_NEED, jsonObject.getString("SINeed"));
        editor.putString(VST_ND, jsonObject.getString("VstNd"));
        editor.putString(MSD_ENTRY, jsonObject.getString("MsdEntry"));
        editor.putString(NP_NEED, jsonObject.getString("NPNeed"));
        editor.putString(NI_NEED, jsonObject.getString("NINeed"));
        editor.putString(CAT_NEED, jsonObject.getString("Catneed"));
        editor.putString(CHM_AD_QTY, jsonObject.getString("chm_ad_qty"));
        editor.putString(CAMP_NEED, jsonObject.getString("Campneed"));
        editor.putString(CHE_BASE, jsonObject.getString("CHEBase"));
        editor.putString(DR_CAP, jsonObject.getString("DrCap"));
        editor.putString(CHM_CAP, jsonObject.getString("ChmCap"));
        editor.putString(STK_CAP, jsonObject.getString("StkCap"));
        editor.putString(NL_CAP, jsonObject.getString("NLCap"));
        editor.putString(USER_N, jsonObject.getString("UserN"));
        editor.putString(PASS, jsonObject.getString("Pass"));
        editor.putString(DR_RX_Q_CAP, jsonObject.getString("DrRxQCap"));
        editor.putString(CHM_RCPA_MD, jsonObject.getString("ChmRcpaMd"));
        editor.putString(CHM_RCPA_MD_MGR, jsonObject.getString("ChmRcpaMd_Mgr"));
        editor.putString(DR_SMP_Q_CAP, jsonObject.getString("DrSmpQCap"));
        editor.putString(DR_RX_ND, jsonObject.getString("DrRxNd"));
        editor.putString(APPROVE_NEED, jsonObject.getString("Approveneed"));
        editor.putString(EXPENSENEED, jsonObject.getString("Expenseneed"));
        editor.putString(TERRITORY_VST_ND, jsonObject.getString("Territory_VstNd"));
        editor.putString(TRAVEL_DISTANCE_NEED, jsonObject.getString("travelDistance_Need"));
        editor.putString(ANDROID_APP, jsonObject.getString("Android_App"));
        editor.putString(IOS_APP, jsonObject.getString("ios_app"));
        editor.putString(DOC_INPUT_CAPTION, jsonObject.getString("Doc_Input_caption"));
        editor.putString(CHM_INPUT_CAPTION, jsonObject.getString("Chm_Input_caption"));
        editor.putString(DASHBOARD, jsonObject.getString("dashboard"));
        editor.putString(SRT_ND, jsonObject.getString("SrtNd"));
        editor.putString(UL_STK_NEED, jsonObject.getString("Ul_stk_Need"));
        editor.putString(UL_STK_MANDATORY, jsonObject.getString("Ul_stk_Mandatory"));
        editor.putString(TP_NEW, jsonObject.getString("tp_new"));
        editor.putString(TAXNAME_CAPTION, jsonObject.getString("Taxname_caption"));
        editor.putString(SURVEY_ND, jsonObject.getString("SurveyNd"));
        editor.putString(ST_STK_NEED, jsonObject.getString("st_stk_Need"));
        editor.putString(ST_STK_MANDATORY, jsonObject.getString("st_stk_Mandatory"));
        editor.putString(SRT_MAND_ND, jsonObject.getString("SrtMandNd"));
        editor.putString(SINGLE_ACTIVITY, jsonObject.getString("single_activity"));
        editor.putString(QUIZ_NEED_MANDT, jsonObject.getString("quiz_need_mandt"));
        editor.putString(QUIZ_HEADING, jsonObject.getString("quiz_heading"));
        editor.putString(QUICKADD, jsonObject.getString("quickadd"));
        editor.putString(PRODUCT_RATE_EDITABLE, jsonObject.getString("Product_Rate_Editable"));
        editor.putString(OFFLINE_LOGIN, jsonObject.getString("offlineLogin"));
        editor.putString(OFFICEWORK_HOME, jsonObject.getString("officework_home"));
        editor.putString(HOSP_DCR, jsonObject.getString("hosp_dcr"));
        editor.putString(DR_POLICY, jsonObject.getString("DrPolicy"));
        editor.putString(DOC_STK_NEED, jsonObject.getString("Doc_stk_Need"));
        editor.putString(DOC_STK_MANDATORY, jsonObject.getString("Doc_stk_Mandatory"));
        editor.putString(CUST_SRT_ND, jsonObject.getString("CustSrtNd"));
        editor.putString(CHM_STK_NEED, jsonObject.getString("Chm_stk_Need"));
        editor.putString(CHM_STK_MANDATORY, jsonObject.getString("Chm_stk_Mandatory"));
        editor.putString(ADD_DR, jsonObject.getString("addDr"));
        editor.putString(ACTIVITY_NEED, jsonObject.getString("activityneed"));
        editor.putString(SF_EMAIL, jsonObject.getString("sfEmail"));
        editor.putString(SF_MOBILE, jsonObject.getString("sfMobile"));
        editor.putString(DS_NAME, jsonObject.getString("DS_name"));
        editor.putString(ACTIVITY_ND, jsonObject.getString("ActivityNd"));
        editor.putString(ACTIVITY_MAND, jsonObject.getString("activityMand"));
        editor.putString(SEQUENTIAL_DCR, jsonObject.getString("sequential_dcr"));
        editor.putString(MYDAYPLAN_NEED, jsonObject.getString("mydayplan_need"));
        editor.putString(MISSED_DATE_MAND, jsonObject.getString("missedDateMand"));
        editor.putString(MEDIA_TRANS_NEED, jsonObject.getString("mediaTrans_Need"));
        editor.putString(DR_SMP_Q_MD, jsonObject.getString("DrSmpQMd"));
        editor.putString(DR_RX_Q_MD, jsonObject.getString("DrRxQMd"));
        editor.putString(MYPLN_RMRKS_MAND, jsonObject.getString("myplnRmrksMand"));
        editor.putString(STK_INPUT_CAPTION, jsonObject.getString("Stk_Input_caption"));
        editor.putString(UL_INPUT_CAPTION, jsonObject.getString("Ul_Input_caption"));
        editor.putString(RCPA_ND, jsonObject.getString("RcpaNd"));
        editor.putString(RCPA_COMPETITOR_EXTRA, jsonObject.getString("Rcpa_Competitor_extra"));
        editor.putString(DETAILING_TYPE, jsonObject.getString("Detailing_type"));
        editor.putString(CIP_SRT_ND, jsonObject.getString("CipSrtNd"));
        editor.putString(DCR_FIRSTSELFIE, jsonObject.getString("Dcr_firstselfie"));
        editor.putString(CHM_Q_CAP, jsonObject.getString("ChmQCap"));
        editor.putString(STK_Q_CAP, jsonObject.getString("StkQCap"));
        editor.putString(MULTIPLE_DOC_NEED, jsonObject.getString("multiple_doc_need"));
        editor.putString(MAIL_NEED, jsonObject.getString("mailneed"));
        editor.putString(CIRCULAR, jsonObject.getString("circular"));
        editor.putString(DR_FEED_MD, jsonObject.getString("DrFeedMd"));
        editor.putString(DF_NEED, jsonObject.getString("DFNeed"));
        editor.putString(CF_NEED, jsonObject.getString("CFNeed"));
        editor.putString(SF_NEED, jsonObject.getString("SFNeed"));
        editor.putString(CIP_F_NEED, jsonObject.getString("CIP_FNeed"));
        editor.putString(NF_NEED, jsonObject.getString("NFNeed"));
        editor.putString(HF_NEED, jsonObject.getString("HFNeed"));
        editor.putString(DQ_NEED, jsonObject.getString("DQNeed"));
        editor.putString(CQ_NEED, jsonObject.getString("CQNeed"));
        editor.putString(SQ_NEED, jsonObject.getString("SQNeed"));
        editor.putString(NQ_NEED, jsonObject.getString("NQNeed"));
        editor.putString(CIP_Q_NEED, jsonObject.getString("CIP_QNeed"));
        editor.putString(HQ_NEED, jsonObject.getString("HQNeed"));
        editor.putString(DE_NEED, jsonObject.getString("DENeed"));
        editor.putString(CE_NEED, jsonObject.getString("CENeed"));
        editor.putString(SE_NEED, jsonObject.getString("SENeed"));
        editor.putString(NE_NEED, jsonObject.getString("NENeed"));
        editor.putString(CIP_E_NEED, jsonObject.getString("CIP_ENeed"));
        editor.putString(HE_NEED, jsonObject.getString("HENeed"));
        editor.putString(TP_NEED, jsonObject.getString("tp_need"));
        editor.putString(CLUSTER_CAP, jsonObject.getString("cluster_cap"));
        editor.putString(WRK_AREA_NAME, jsonObject.getString("wrk_area_Name"));
        editor.putString(NL_RX_Q_CAP, jsonObject.getString("NLRxQCap"));
        editor.putString(NL_SMP_Q_CAP, jsonObject.getString("NLSmpQCap"));
        editor.putString(APP_DEVICE_ID, jsonObject.getString("app_device_id"));
        editor.putString(USR_DFD_USERNAME, jsonObject.getString("UsrDfd_UserName"));
        editor.putString(SF_USER_NAME, jsonObject.getString("SF_User_Name"));
        editor.putString(ATTENDANCE, jsonObject.getString("Attendance"));
        editor.putString(DEVICE_ID_NEED, jsonObject.getString("DeviceId_Need"));
        editor.putString(DOCTOR_DOBDOW, jsonObject.getString("doctor_dobdow"));
        editor.putString(PRODUCT_POB_NEED_MSG, jsonObject.getString("product_pob_need_msg"));
        editor.putString(PROD_REMARK, jsonObject.getString("prod_remark"));
        editor.putString(PROD_REMARK_MD, jsonObject.getString("prod_remark_md"));
        editor.putString(CIP_NEED, jsonObject.getString("cip_need"));
        editor.putString(CIP_P_NEED, jsonObject.getString("CIP_PNeed"));
        editor.putString(CIP_I_NEED, jsonObject.getString("CIP_INeed"));
        editor.putString(DR_PRD_MD, jsonObject.getString("DrPrdMd"));
        editor.putString(DR_INP_MD, jsonObject.getString("DrInpMd"));
        editor.putString(CIP_JOINTWORK_NEED, jsonObject.getString("CIP_jointwork_Need"));
        editor.putString(CIP_CAPTION, jsonObject.getString("CIP_Caption"));
        editor.putString(HOSP_CAPTION, jsonObject.getString("hosp_caption"));
        editor.putString(SEP_RCPA_ND, jsonObject.getString("Sep_RcpaNd"));
        editor.putString(PAST_LEAVE_POST, jsonObject.getString("past_leave_post"));
        editor.putString(DLY_CTRL, jsonObject.getString("DlyCtrl"));
        editor.putString(FEED_ND, jsonObject.getString("FeedNd"));
        editor.putString(TEMP_ND, jsonObject.getString("TempNd"));
        editor.putString(PROD_DET_NEED, jsonObject.getString("prod_det_need"));
        editor.putString(CNT_REMARKS, jsonObject.getString("cntRemarks"));
        editor.putString(PRODUCT_POB_NEED, jsonObject.getString("product_pob_need"));
        editor.putString(SECONDARY_ORDER_DISCOUNT, jsonObject.getString("secondary_order_discount"));
        editor.putString(TARGET_REPORT_MD, jsonObject.getString("Target_report_md"));
        editor.putString(RCPA_UNIT_ND, jsonObject.getString("RCPA_unit_nd"));
        editor.putString(CHM_RCPA_NEED, jsonObject.getString("Chm_RCPA_Need"));
        editor.putString(DR_RCPA_COMPETITOR_NEED, jsonObject.getString("DrRCPA_competitor_Need"));
        editor.putString(CHM_RCPA_COMPETITOR_NEED, jsonObject.getString("ChmRCPA_competitor_Need"));
        editor.putString(CURRENTDAY_TPPLANNED, jsonObject.getString("Currentday_TPplanned"));
        editor.putString(DOC_CLUSTER_BASED, jsonObject.getString("Doc_cluster_based"));
        editor.putString(CHM_CLUSTER_BASED, jsonObject.getString("Chm_cluster_based"));
        editor.putString(STK_CLUSTER_BASED, jsonObject.getString("Stk_cluster_based"));
        editor.putString(ULDOC_CLUSTER_BASED, jsonObject.getString("UlDoc_cluster_based"));
        editor.putString(MULTI_CLUSTER, jsonObject.getString("multi_cluster"));
        editor.putString(TERR_BASED_TAG, jsonObject.getString("Terr_based_Tag"));
        editor.putString(RCPA_MD_MGR, jsonObject.getString("RcpaMd_Mgr"));
        editor.putString(DR_NEED, jsonObject.getString("DrNeed"));
        editor.putString(FAQ, jsonObject.getString("faq"));
        editor.putString(EDIT_HOLIDAY, jsonObject.getString("edit_holiday"));
        editor.putString(EDIT_WEEKLYOFF, jsonObject.getString("edit_weeklyoff"));
        editor.putString(TARGET_REPORT_ND, jsonObject.getString("Target_report_Nd"));
        editor.putString(DCR_LOCK_DAYS, jsonObject.getString("DcrLockDays"));
        editor.putString(DOC_POB_CAPTION, jsonObject.getString("Doc_pob_caption"));
        editor.putString(STK_POB_CAPTION, jsonObject.getString("Stk_pob_caption"));
        editor.putString(CHM_POB_CAPTION, jsonObject.getString("Chm_pob_caption"));
        editor.putString(ULDOC_POB_CAPTION, jsonObject.getString("Uldoc_pob_caption"));
        editor.putString(CIP_POB_CAPTION, jsonObject.getString("CIP_pob_caption"));
        editor.putString(HOSP_POB_CAPTION, jsonObject.getString("Hosp_pob_caption"));
        editor.putString(DOC_POB_MANDATORY_NEED, jsonObject.getString("Doc_Pob_Mandatory_Need"));
        editor.putString(CHM_POB_MANDATORY_NEED, jsonObject.getString("Chm_Pob_Mandatory_Need"));
        editor.putString(DR_SAMP_ND, jsonObject.getString("DrSampNd"));
        editor.putString(RCPA_MD, jsonObject.getString("RcpaMd"));
        editor.putString(MISC_EXPENSE_NEED, jsonObject.getString("misc_expense_need"));
        editor.putString(DOC_POB_NEED, jsonObject.getString("Doc_Pob_Need"));
        editor.putString(CHM_POB_NEED, jsonObject.getString("Chm_Pob_Need"));
        editor.putString(STK_POB_NEED, jsonObject.getString("Stk_Pob_Need"));
        editor.putString(UL_POB_NEED, jsonObject.getString("Ul_Pob_Need"));
        editor.putString(STK_POB_MANDATORY_NEED, jsonObject.getString("Stk_Pob_Mandatory_Need"));
        editor.putString(UL_POB_MANDATORY_NEED, jsonObject.getString("Ul_Pob_Mandatory_Need"));
        editor.putString(DOC_JOINTWORK_NEED, jsonObject.getString("Doc_jointwork_Need"));
        editor.putString(CHM_JOINTWORK_NEED, jsonObject.getString("Chm_jointwork_Need"));
        editor.putString(STK_JOINTWORK_NEED, jsonObject.getString("Stk_jointwork_Need"));
        editor.putString(UL_JOINTWORK_NEED, jsonObject.getString("Ul_jointwork_Need"));
        editor.putString(DOC_JOINTWORK_MANDATORY_NEED, jsonObject.getString("Doc_jointwork_Mandatory_Need"));
        editor.putString(CHM_JOINTWORK_MANDATORY_NEED, jsonObject.getString("Chm_jointwork_Mandatory_Need"));
        editor.putString(STK_JOINTWORK_MANDATORY_NEED, jsonObject.getString("Stk_jointwork_Mandatory_Need"));
        editor.putString(UL_JOINTWORK_MANDATORY_NEED, jsonObject.getString("Ul_jointwork_Mandatory_Need"));
        editor.putString(DOC_PRODUCT_CAPTION, jsonObject.getString("Doc_Product_caption"));
        editor.putString(CHM_PRODUCT_CAPTION, jsonObject.getString("Chm_Product_caption"));
        editor.putString(STK_PRODUCT_CAPTION, jsonObject.getString("Stk_Product_caption"));
        editor.putString(UL_PRODUCT_CAPTION, jsonObject.getString("Ul_Product_caption"));
        editor.putString(REMAINDER_PRD_MD, jsonObject.getString("Remainder_prd_Md"));
        editor.putString(GEOTAG_NEED, jsonObject.getString("GEOTagNeed"));
        editor.putString(GEOTAG_NEED_CHE, jsonObject.getString("GEOTagNeedche"));
        editor.putString(REMAINDER_GEO, jsonObject.getString("Remainder_geo"));
        editor.putString(GEOTAG_NEED_STOCK, jsonObject.getString("GEOTagNeedstock"));
        editor.putString(GEOTAG_NEED_CIP, jsonObject.getString("GeoTagNeedcip"));
        editor.putString(GEOTAG_NEED_UNLST, jsonObject.getString("GEOTagNeedunlst"));
        editor.putString(DIS_RAD, jsonObject.getString("DisRad"));
        editor.putString(DEVICE_REG_ID, jsonObject.getString("DeviceRegId"));
        editor.putString(SFTP_DATE, jsonObject.getString("SFTPDate"));
        editor.putString(MCL_DET, jsonObject.getString("MCLDet"));
        editor.putString(RMDR_NEED, jsonObject.getString("RmdrNeed"));
        editor.putString(EXPENSE_NEED, jsonObject.getString("expense_need"));
        editor.putString(GEOTAG_IMG, jsonObject.getString("geoTagImg"));
        editor.putString(DR_RCPA_Q_MD, jsonObject.getString("DrRcpaQMd"));
        editor.putString(QUES_NEED, jsonObject.getString("ques_need"));
        editor.putString(HOSP_NEED, jsonObject.getString("hosp_need"));
        editor.putString(HP_NEED, jsonObject.getString("HPNeed"));
        editor.putString(HI_NEED, jsonObject.getString("HINeed"));
        editor.putString(CHMSAMQTY_NEED, jsonObject.getString("chmsamQty_need"));
        editor.putString(CHM_SMP_CAP, jsonObject.getString("ChmSmpCap"));
        editor.putString(CALL_FEED_ENTERABLE, jsonObject.getString("call_feed_enterable"));
        editor.putString(PWD_SETUP, jsonObject.getString("Pwdsetup"));
        editor.putString(RCPA_EXTRA, jsonObject.getString("rcpaextra"));
        editor.putString(LEAVE_STATUS, jsonObject.getString("LeaveStatus"));
        editor.putString(STATE_CODE, jsonObject.getString("State_Code"));
        editor.putString(SF_EMP_ID, jsonObject.getString("sf_emp_id"));
        editor.putString(SUBDIVISION_CODE, jsonObject.getString("subdivision_code"));
        editor.putString(REMAINDER_CALL_CAP, jsonObject.getString("Remainder_call_cap"));
        editor.putString(CALL_REPORT_FROM_DATE, jsonObject.getString("call_report_from_date"));
        editor.putString(CALL_REPORT_TO_DATE, jsonObject.getString("call_report_to_date"));
        editor.putString(CALL_REPORT, jsonObject.getString("call_report"));
        editor.putString(DAYS, jsonObject.getString("days"));
        editor.putString(MRHLFDY, jsonObject.getString("MRHlfDy"));
        editor.putString(ORDER_MANAGEMENT, jsonObject.getString("Order_management"));
        editor.putString(ORDER_CAPTION, jsonObject.getString("Order_caption"));
        editor.putString(PRIMARY_ORDER_CAPTION, jsonObject.getString("Primary_order_caption"));
        editor.putString(SECONDARY_ORDER_CAPTION, jsonObject.getString("Secondary_order_caption"));
        editor.putString(PRIMARY_ORDER, jsonObject.getString("Primary_order"));
        editor.putString(SECONDARY_ORDER, jsonObject.getString("Secondary_order"));
        editor.putString(GST_OPTION, jsonObject.getString("Gst_option"));
        editor.putString(TPDCR_DEVIATION_APPR_STATUS, jsonObject.getString("TPDCR_Deviation_Appr_Status"));
        editor.putString(TPDCR_DEVIATION, jsonObject.getString("TPDCR_Deviation"));
        editor.putString(TPDCR_MGRAPPR, jsonObject.getString("TPDCR_MGRAppr"));
        editor.putString(NEXT_VST, jsonObject.getString("NextVst"));
        editor.putString(NEXT_VST_MANDATORY_NEED, jsonObject.getString("NextVst_Mandatory_Need"));
        editor.putString(APPR_MANDATORY_NEED, jsonObject.getString("Appr_Mandatory_Need"));
        editor.putString(RCPA_QTY_NEED, jsonObject.getString("RCPAQty_Need"));
        editor.putString(PROD_STK_NEED, jsonObject.getString("Prod_Stk_Need"));
        editor.putString(TP_MANDATORY_NEED, jsonObject.getString("TP_Mandatory_Need"));
        editor.putString(TP_START_DATE, jsonObject.getString("Tp_Start_Date"));
        editor.putString(PRDFDBACK, jsonObject.getString("prdfdback"));
        editor.putString(TRACKING_INTERVAL, jsonObject.getString("tracking_interval"));
        editor.putString(TP_END_DATE, jsonObject.getString("Tp_End_Date"));
        editor.putString(DR_EVENT_MD, jsonObject.getString("DrEvent_Md"));
        editor.putString(CHM_EVENT_MD, jsonObject.getString("ChmEvent_Md"));
        editor.putString(STK_EVENT_MD, jsonObject.getString("StkEvent_Md"));
        editor.putString(ULDR_EVENT_MD, jsonObject.getString("UlDrEvent_Md"));
        editor.putString(CIP_EVENT_MD, jsonObject.getString("CipEvent_Md"));
        editor.putString(HOSP_EVENT_MD, jsonObject.getString("HospEvent_Md"));
        editor.putString(LEAVE_ENTITLEMENT_NEED, jsonObject.getString("Leave_entitlement_need"));
        editor.putString(PRIMARYSEC_NEED, jsonObject.getString("primarysec_need"));
        editor.putString(MGRHLFDY, jsonObject.getString("MGRHlfDy"));
        editor.putString(NO_OF_TP_VIEW, jsonObject.getString("No_of_TP_View"));
        editor.putString(CURRENTDAY, jsonObject.getString("currentDay"));
        editor.putString(DAYPLAN_TP_BASED, jsonObject.getString("dayplan_tp_based"));
        editor.putString(QUIZ_NEED, jsonObject.getString("quiz_need"));
        editor.putString(STP, jsonObject.getString("stp"));
        editor.putString(POB_MINVALUE, jsonObject.getString("pob_minvalue"));
        editor.putString(TPBASED_DCR, jsonObject.getString("TPbasedDCR"));
        editor.putString(LOCATION_TRACK, jsonObject.getString("Location_track"));
        editor.putString(TRACKING_TIME, jsonObject.getString("tracking_time"));
        editor.putString(ENTRY_FORM_MGR, jsonObject.getString("entryFormMgr"));
        editor.putString(ENTRY_FORM_NEED, jsonObject.getString("entryFormNeed"));
        editor.putString(DLY_CTRL_S, jsonObject.getString("Dly_Ctrls"));
        editor.putString(ANDROID_DETAILING, jsonObject.getString("Android_Detailing"));
        editor.putString(IOS_DETAILING, jsonObject.getString("ios_Detailing"));
        editor.putString(SAMPLE_VAL_QTY, jsonObject.getString("Sample_Val_Qty"));
        editor.putString(INPUT_VAL_QTY, jsonObject.getString("Input_Val_Qty"));
        editor.putString(QUOTE_TEXT, jsonObject.getString("quote_Text"));
        editor.putString(PRO_DET_NEED, jsonObject.getString("pro_det_need"));
        editor.putString(AUTHENTICATION, jsonObject.getString("Authentication"));
        editor.putString(GEOTAG_APPROVAL_NEED, jsonObject.getString("GeoTagApprovalNeed"));
        editor.putString(CHM_SRT_ND, jsonObject.getString("ChmSrtNd"));
        editor.putString(UNLIST_SRT_ND, jsonObject.getString("UnlistSrtNd"));
        editor.putString(RCPA_COMPETITOR_ADD, jsonObject.getString("RCPA_competitor_add"));
        editor.putString(GEOTAGGING, jsonObject.getString("GeoTagging"));
        editor.apply();

    }catch (Exception ignore){
            ignore.printStackTrace();
        }

    }



    public static String getSfName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_NAME, "");
    }

    public static String getSfPassword(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_PASSWORD, "");
    }

    public static String getDesigCode(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DESIG_CODE, "");
    }

    public static String getDivisionName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DIVISION_NAME, "");
    }

    public static String getDesig(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DESIG, "");
    }
    public static String getSfStat(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_STAT, "");
    }

    public static String getDivisionCode(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DIVISION_CODE, "");
    }

    public static String getTBase(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(T_BASE, "");
    }

    public static String getGeoChk(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEO_CHK, "");
    }

    public static String getGeoNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEO_NEED, "");
    }

    public static String getChmNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_NEED, "");
    }

    public static String getStkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_NEED, "");
    }

    public static String getUnlNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UNL_NEED, "");
    }

    public static String getDpNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DP_NEED, "");
    }

    public static String getDiNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DI_NEED, "");
    }

    public static String getChmRxQty(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_RX_QTY, "");
    }

    public static String getHosPobMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HOS_POB_MD, "");
    }

    public static String getHosPobNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HOS_POB_ND, "");
    }

    public static String getSampleValidation(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SAMPLE_VALIDATION, "");
    }

    public static String getInputValidation(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(INPUT_VALIDATION, "");
    }

    public static String getDocBusinessProduct(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_BUSINESS_PRODUCT, "");
    }

    public static String getDocBusinessValue(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_BUSINESS_VALUE, "");
    }

    public static String getDcrDocBusinessProduct(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DCR_DOC_BUSINESS_PRODUCT, "");
    }

    public static String getDrMappingProduct(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_MAPPINGPRODUCT, "");
    }

    public static String getCpNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CP_NEED, "");
    }

    public static String getCiNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CI_NEED, "");
    }


    public static String getCmpgnNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CMPGN_NEED, "");
    }

    public static String getSpNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SP_NEED, "");
    }

    public static String getSiNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SI_NEED, "");
    }

    public static String getVstNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(VST_ND, "");
    }

    public static String getMsdEntry(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MSD_ENTRY, "");
    }

    public static String getNpNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NP_NEED, "");
    }

    public static String getNiNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NI_NEED, "");
    }

    public static String getCatNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CAT_NEED, "");
    }

    public static String getChmAdQty(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_AD_QTY, "");
    }

    public static String getCampNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CAMP_NEED, "");
    }

    public static String getCheBase(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHE_BASE, "");
    }

    public static String getDrCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_CAP, "");
    }

    public static String getChmCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_CAP, "");
    }

    public static String getStkCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_CAP, "");
    }

    public static String getUNLcap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NL_CAP, "");
    }

    public static String getUserN(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(USER_N, "");
    }

    public static String getPass(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PASS, "");
    }

    public static String getDrRxQCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_RX_Q_CAP, "");
    }

    public static String getChmRcpaMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_RCPA_MD, "");
    }

    public static String getChmRcpaMdMgr(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_RCPA_MD_MGR, "");
    }

    public static String getDrSmpQCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_SMP_Q_CAP, "");
    }

    public static String getDrRxNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_RX_ND, "");
    }

    public static String getApproveNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(APPROVE_NEED, "");
    }

    public static String getExpenseneed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(EXPENSENEED, "");
    }

    public static String getTerritoryVstNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TERRITORY_VST_ND, "");
    }

    public static String getTravelDistanceNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TRAVEL_DISTANCE_NEED, "");
    }

    public static String getAndroidApp(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ANDROID_APP, "");
    }

    public static String getIosApp(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(IOS_APP, "");
    }

    public static String getDocInputCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_INPUT_CAPTION, "");
    }

    public static String getChmInputCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_INPUT_CAPTION, "");
    }

    public static String getDashboard(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DASHBOARD, "");
    }

    public static String getSrtNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SRT_ND, "");
    }

    public static String getUlStkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UL_STK_NEED, "");
    }

    public static String getUlStkMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UL_STK_MANDATORY, "");
    }

    public static String getTpNew(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TP_NEW, "");
    }

    public static String getTaxnameCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TAXNAME_CAPTION, "");
    }

    public static String getSurveyNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SURVEY_ND, "");
    }

    public static String getStStkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ST_STK_NEED, "");
    }

    public static String getStStkMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ST_STK_MANDATORY, "");
    }

    public static String getSrtMandNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SRT_MAND_ND, "");
    }

    public static String getSingleActivity(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SINGLE_ACTIVITY, "");
    }

    public static String getQuizNeedMandt(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(QUIZ_NEED_MANDT, "");
    }

    public static String getQuizHeading(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(QUIZ_HEADING, "");
    }

    public static String getQuickadd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(QUICKADD, "");
    }

    public static String getProductRateEditable(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PRODUCT_RATE_EDITABLE, "");
    }

    public static String getOfflineLogin(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(OFFLINE_LOGIN, "");
    }

    public static String getOfficeworkHome(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(OFFICEWORK_HOME, "");
    }

    public static String getHospDcr(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HOSP_DCR, "");
    }

    public static String getDrPolicy(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_POLICY, "");
    }

    public static String getDocStkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_STK_NEED, "");
    }

    public static String getDocStkMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_STK_MANDATORY, "");
    }

    public static String getCustSrtNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CUST_SRT_ND, "");
    }

    public static String getChmStkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_STK_NEED, "");
    }

    public static String getChmStkMandatory(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_STK_MANDATORY, "");
    }

    public static String getAddDr(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ADD_DR, "");
    }

    public static String getActivityNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ACTIVITY_NEED, "");
    }

    public static String getSfEmail(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_EMAIL, "");
    }

    public static String getSfMobile(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_MOBILE, "");
    }

    public static String getDsName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DS_NAME, "");
    }

    public static String getActivityNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ACTIVITY_ND, "");
    }

    public static String getActivityMand(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ACTIVITY_MAND, "");
    }

    public static String getSequentialDcr(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SEQUENTIAL_DCR, "");
    }

    public static String getMydayplanNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MYDAYPLAN_NEED, "");
    }

    public static String getMissedDateMand(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MISSED_DATE_MAND, "");
    }

    public static String getMediaTransNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MEDIA_TRANS_NEED, "");
    }

    public static String getDrSmpQMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_SMP_Q_MD, "");
    }

    public static String getDrRxQMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_RX_Q_MD, "");
    }

    public static String getMyplnRmrksMand(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MYPLN_RMRKS_MAND, "");
    }

    public static String getStkInputCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_INPUT_CAPTION, "");
    }

    public static String getUlInputCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UL_INPUT_CAPTION, "");
    }

    public static String getRcpaNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RCPA_ND, "");
    }

    public static String getRcpaCompetitorExtra(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RCPA_COMPETITOR_EXTRA, "");
    }

    public static String getDetailingType(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DETAILING_TYPE, "");
    }

    public static String getCipSrtNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_SRT_ND, "");
    }

    public static String getDcrFirstselfie(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DCR_FIRSTSELFIE, "");
    }

    public static String getChmQCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_Q_CAP, "");
    }

    public static String getStkQCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_Q_CAP, "");
    }

    public static String getMultipleDocNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MULTIPLE_DOC_NEED, "");
    }

    public static String getMailNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MAIL_NEED, "");
    }

    public static String getCircular(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIRCULAR, "");
    }

    public static String getDrFeedMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_FEED_MD, "");
    }

    public static String getDfNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DF_NEED, "");
    }

    public static String getCfNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CF_NEED, "");
    }

    public static String getSfNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_NEED, "");
    }

    public static String getCipFNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_F_NEED, "");
    }

    public static String getNfNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NF_NEED, "");
    }
    public static String getHfNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HF_NEED, "");
    }

    public static String getDqNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DQ_NEED, "");
    }

    public static String getCqNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CQ_NEED, "");
    }

    public static String getSqNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SQ_NEED, "");
    }

    public static String getNqNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NQ_NEED, "");
    }

    public static String getCipQNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_Q_NEED, "");
    }

    public static String getHqNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HQ_NEED, "");
    }

    public static String getDeNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DE_NEED, "");
    }

    public static String getCeNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CE_NEED, "");
    }

    public static String getSeNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SE_NEED, "");
    }

    public static String getNeNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NE_NEED, "");
    }

    public static String getCipENeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_E_NEED, "");
    }

    public static String getHeNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HE_NEED, "");
    }

    public static String getTpNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TP_NEED, "");
    }

    public static String getClusterCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CLUSTER_CAP, "");
    }

    public static String getWrkAreaName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(WRK_AREA_NAME, "");
    }

    public static String getNlRxQCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NL_RX_Q_CAP, "");
    }

    public static String getNlSmpQCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NL_SMP_Q_CAP, "");
    }

    public static String getAppDeviceId(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(APP_DEVICE_ID, "");
    }

    public static String getUsrDfdUsername(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(USR_DFD_USERNAME, "");
    }

    public static String getSfUserName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_USER_NAME, "");
    }

    public static String getAttendance(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ATTENDANCE, "");
    }

    public static String getDeviceIdNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DEVICE_ID_NEED, "");
    }

    public static String getDoctorDobdow(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOCTOR_DOBDOW, "");
    }

    public static String getProductPobNeedMsg(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PRODUCT_POB_NEED_MSG, "");
    }

    public static String getProdRemark(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PROD_REMARK, "");
    }

    public static String getProdRemarkMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PROD_REMARK_MD, "");
    }

    public static String getCipNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_NEED, "");
    }

    public static String getCipPNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_P_NEED, "");
    }

    public static String getCipINeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_I_NEED, "");
    }

    public static String getDrPrdMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_PRD_MD, "");
    }

    public static String getDrInpMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_INP_MD, "");
    }

    public static String getCipJointworkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_JOINTWORK_NEED, "");
    }

    public static String getCipCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_CAPTION, "");
    }

    public static String getHospCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HOSP_CAPTION, "");
    }

    public static String getSepRcpaNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SEP_RCPA_ND, "");
    }

    public static String getPastLeavePost(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PAST_LEAVE_POST, "");
    }

    public static String getDlyCtrl(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DLY_CTRL, "");
    }

    public static String getFeedNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(FEED_ND, "");
    }

    public static String getTempNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TEMP_ND, "");
    }

    public static String getProdDetNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PROD_DET_NEED, "");
    }

    public static String getCntRemarks(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CNT_REMARKS, "");
    }

    public static String getProductPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PRODUCT_POB_NEED, "");
    }

    public static String getSecondaryOrderDiscount(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SECONDARY_ORDER_DISCOUNT, "");
    }

    public static String getTargetReportMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TARGET_REPORT_MD, "");
    }

    public static String getRcpaUnitNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RCPA_UNIT_ND, "");
    }

    public static String getChmRcpaNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_RCPA_NEED, "");
    }

    public static String getDrRcpaCompetitorNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_RCPA_COMPETITOR_NEED, "");
    }

    public static String getChmRcpaCompetitorNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_RCPA_COMPETITOR_NEED, "");
    }

    public static String getCurrentdayTpplanned(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CURRENTDAY_TPPLANNED, "");
    }

    public static String getDocClusterBased(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_CLUSTER_BASED, "");
    }

    public static String getChmClusterBased(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_CLUSTER_BASED, "");
    }

    public static String getStkClusterBased(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_CLUSTER_BASED, "");
    }

    public static String getUldocClusterBased(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ULDOC_CLUSTER_BASED, "");
    }

    public static String getMultiCluster(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MULTI_CLUSTER, "");
    }

    public static String getTerrBasedTag(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TERR_BASED_TAG, "");
    }

    public static String getRcpaMdMgr(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RCPA_MD_MGR, "");
    }

    public static String getDrNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_NEED, "");
    }

    public static String getFaq(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(FAQ, "");
    }

    public static String getEditHoliday(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(EDIT_HOLIDAY, "");
    }

    public static String getEditWeeklyoff(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(EDIT_WEEKLYOFF, "");
    }

    public static String getTargetReportNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TARGET_REPORT_ND, "");
    }

    public static String getDcrLockDays(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DCR_LOCK_DAYS, "");
    }

    public static String getDocPobCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_POB_CAPTION, "");
    }

    public static String getStkPobCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_POB_CAPTION, "");
    }

    public static String getChmPobCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_POB_CAPTION, "");
    }

    public static String getUldocPobCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ULDOC_POB_CAPTION, "");
    }



    public static String getCipPobCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_POB_CAPTION, "");
    }

    public static String getHospPobCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HOSP_POB_CAPTION, "");
    }

    public static String getDocPobMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_POB_MANDATORY_NEED, "");
    }

    public static String getChmPobMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_POB_MANDATORY_NEED, "");
    }

    public static String getDrSampNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_SAMP_ND, "");
    }

    public static String getRcpaMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RCPA_MD, "");
    }

    public static String getMiscExpenseNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MISC_EXPENSE_NEED, "");
    }

    public static String getDocPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_POB_NEED, "");
    }

    public static String getChmPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_POB_NEED, "");
    }

    public static String getStkPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_POB_NEED, "");
    }

    public static String getUlPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UL_POB_NEED, "");
    }

    public static String getStkPobMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_POB_MANDATORY_NEED, "");
    }

    public static String getUlPobMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UL_POB_MANDATORY_NEED, "");
    }

    public static String getDocJointworkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_JOINTWORK_NEED, "");
    }

    public static String getChmJointworkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_JOINTWORK_NEED, "");
    }

    public static String getStkJointworkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_JOINTWORK_NEED, "");
    }

    public static String getUlJointworkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UL_JOINTWORK_NEED, "");
    }

    public static String getDocJointworkMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_JOINTWORK_MANDATORY_NEED, "");
    }

    public static String getChmJointworkMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_JOINTWORK_MANDATORY_NEED, "");
    }

    public static String getStkJointworkMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_JOINTWORK_MANDATORY_NEED, "");
    }

    public static String getUlJointworkMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UL_JOINTWORK_MANDATORY_NEED, "");
    }

    public static String getDocProductCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DOC_PRODUCT_CAPTION, "");
    }

    public static String getChmProductCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_PRODUCT_CAPTION, "");
    }

    public static String getStkProductCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_PRODUCT_CAPTION, "");
    }

    public static String getUlProductCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UL_PRODUCT_CAPTION, "");
    }

    public static String getRemainderPrdMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(REMAINDER_PRD_MD, "");
    }

    public static String getGeotagNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEOTAG_NEED, "");
    }

    public static String getGeotagNeedChe(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEOTAG_NEED_CHE, "");
    }

    public static String getRemainderGeo(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(REMAINDER_GEO, "");
    }

    public static String getGeotagNeedStock(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEOTAG_NEED_STOCK, "");
    }
    public static String getGeotagNeedCip(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEOTAG_NEED_CIP, "");
    }

    public static String getGeotagNeedUnlst(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEOTAG_NEED_UNLST, "");
    }

    public static String getDisRad(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DIS_RAD, "");
    }

    public static String getDeviceRegId(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DEVICE_REG_ID, "");
    }

    public static String getSftpDate(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SFTP_DATE, "");
    }

    public static String getMclDet(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MCL_DET, "");
    }

    public static String getRmdrNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RMDR_NEED, "");
    }

    public static String getExpenseNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(EXPENSE_NEED, "");
    }

    public static String getGeotagImg(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEOTAG_IMG, "");
    }

    public static String getDrRcpaQMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_RCPA_Q_MD, "");
    }

    public static String getQuesNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(QUES_NEED, "");
    }

    public static String getHospNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HOSP_NEED, "");
    }

    public static String getHpNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HP_NEED, "");
    }

    public static String getHiNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HI_NEED, "");
    }

    public static String getChmsamqtyNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHMSAMQTY_NEED, "");
    }

    public static String getChmSmpCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_SMP_CAP, "");
    }

    public static String getCallFeedEnterable(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CALL_FEED_ENTERABLE, "");
    }

    public static String getPwdSetup(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PWD_SETUP, "");
    }

    public static String getRcpaExtra(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RCPA_EXTRA, "");
    }

    public static String getLeaveStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(LEAVE_STATUS, "");
    }

    public static String getStateCode(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STATE_CODE, "");
    }

    public static String getSfEmpId(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SF_EMP_ID, "");
    }

    public static String getSubdivisionCode(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SUBDIVISION_CODE, "");
    }

    public static String getRemainderCallCap(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(REMAINDER_CALL_CAP, "");
    }

    public static String getCallReportFromDate(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CALL_REPORT_FROM_DATE, "");
    }

    public static String getCallReportToDate(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CALL_REPORT_TO_DATE, "");
    }

    public static String getCallReport(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CALL_REPORT, "");
    }

    public static String getDays(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DAYS, "");
    }


    public static String getMrhlfdy(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MRHLFDY, "");
    }

    public static String getOrderManagement(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ORDER_MANAGEMENT, "");
    }

    public static String getOrderCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ORDER_CAPTION, "");
    }

    public static String getPrimaryOrderCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PRIMARY_ORDER_CAPTION, "");
    }

    public static String getSecondaryOrderCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SECONDARY_ORDER_CAPTION, "");
    }

    public static String getPrimaryOrder(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PRIMARY_ORDER, "");
    }

    public static String getSecondaryOrder(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SECONDARY_ORDER, "");
    }

    public static String getGstOption(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GST_OPTION, "");
    }

    public static String getTpdcrDeviationApprStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TPDCR_DEVIATION_APPR_STATUS, "");
    }

    public static String getTpdcrDeviation(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TPDCR_DEVIATION, "");
    }

    public static String getTpdcrMgrappr(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TPDCR_MGRAPPR, "");
    }

    public static String getNextVst(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NEXT_VST, "");
    }

    public static String getNextVstMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NEXT_VST_MANDATORY_NEED, "");
    }

    public static String getApprMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(APPR_MANDATORY_NEED, "");
    }

    public static String getRcpaQtyNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RCPA_QTY_NEED, "");
    }

    public static String getProdStkNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PROD_STK_NEED, "");
    }

    public static String getTpMandatoryNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TP_MANDATORY_NEED, "");
    }

    public static String getTpStartDate(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TP_START_DATE, "");
    }

    public static String getPrdfeedback(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PRDFDBACK, "");
    }

    public static String getTrackingInterval(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TRACKING_INTERVAL, "");
    }

    public static String getTpEndDate(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TP_END_DATE, "");
    }

    public static String getDrEventMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DR_EVENT_MD, "");
    }

    public static String getChmEventMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_EVENT_MD, "");
    }

    public static String getStkEventMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STK_EVENT_MD, "");
    }

    public static String getUldrEventMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ULDR_EVENT_MD, "");
    }

    public static String getCipEventMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CIP_EVENT_MD, "");
    }

    public static String getHospEventMd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HOSP_EVENT_MD, "");
    }

    public static String getLeaveEntitlementNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(LEAVE_ENTITLEMENT_NEED, "");
    }

    public static String getPrimarysecNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PRIMARYSEC_NEED, "");
    }


    public static String getMgrhlfdy(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MGRHLFDY, "");
    }

    public static String getNoOfTpView(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(NO_OF_TP_VIEW, "");
    }

    public static String getCurrentday(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CURRENTDAY, "");
    }

    public static String getDayplanTpBased(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DAYPLAN_TP_BASED, "");
    }

    public static String getQuizNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(QUIZ_NEED, "");
    }

    public static String getStp(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STP, "");
    }

    public static String getPobMinvalue(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(POB_MINVALUE, "");
    }

    public static String getTpbasedDcr(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TPBASED_DCR, "");
    }

    public static String getLocationTrack(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(LOCATION_TRACK, "");
    }

    public static String getTrackingTime(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(TRACKING_TIME, "");
    }

    public static String getEntryFormMgr(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ENTRY_FORM_MGR, "");
    }

    public static String getEntryFormNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ENTRY_FORM_NEED, "");
    }

    public static String getDlyCtrlS(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(DLY_CTRL_S, "");
    }

    public static String getAndroidDetailing(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(ANDROID_DETAILING, "");
    }

    public static String getIosDetailing(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(IOS_DETAILING, "");
    }

    public static String getSampleValQty(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SAMPLE_VAL_QTY, "");
    }

    public static String getInputValQty(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(INPUT_VAL_QTY, "");
    }

    public static String getQuoteText(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(QUOTE_TEXT, "");
    }

    public static String getProdetneed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(PRO_DET_NEED, "");
    }

    public static String getAuthentication(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(AUTHENTICATION, "");
    }

    public static String getGeotagApprovalNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEOTAG_APPROVAL_NEED, "");
    }

    public static String getChmSrtNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(CHM_SRT_ND, "");
    }

    public static String getUnlistSrtNd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(UNLIST_SRT_ND, "");
    }

    public static String getRcpaCompetitorAdd(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(RCPA_COMPETITOR_ADD, "");
    }

    public static String getGeotagging(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(GEOTAGGING, "");
    }







    public static void saveSlideDownloadingList(Context context, String Downloadcount, ArrayList<SlideModelClass> List,ArrayList<String>IdList) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SLIDEDOWNCOUNT, String.valueOf(Downloadcount));
        editor.putString(SLIDELIST, new Gson().toJson(List));
        editor.putString(SLIDEID, new Gson().toJson(IdList));
        editor.apply();
    }


    public static String GetSlideID(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SLIDEID, "[]");
    }

    public static String GetSlideDownloadingcount(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SLIDEDOWNCOUNT, "0");
    }

    public static String GetSlideList(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SLIDELIST, "");
    }


    public static void ClearSharedPreference(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveSelectedLanguage(Context context, String language) {
        sharedPreferences = context.getSharedPreferences(SP_NAME_NOT_DELETE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SELECTED_LANGUAGE, language).apply();
    }

    public static String getSelectedLanguage(Context context) {
        return context.getSharedPreferences(SP_NAME_NOT_DELETE, MODE_PRIVATE).getString(SELECTED_LANGUAGE, "");
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

    public static void saveLoginId(Context context, String id, String pwd) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LOGIN_USER_ID, id);
        editor.putString(LOGIN_USER_PWD, pwd);
        editor.apply();
    }

    public static void saveLoginPwd(Context context, String pwd) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LOGIN_USER_PWD, pwd).apply();
    }

    public static String getLoginUserPwd(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(LOGIN_USER_PWD, "");
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

    public static void saveMasterLastSync(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MASTER_LAST_SYNC, date).apply();
    }



    public static void setSaveUrlSetting(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME_NOT_DELETE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SAVE_URL_SETTING, token).apply();
    }

    public static String getSaveUrlSetting(Context context) {
        return context.getSharedPreferences(SP_NAME_NOT_DELETE, MODE_PRIVATE).getString(SAVE_URL_SETTING, "");
    }

    public static void setSaveLicenseSetting(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME_NOT_DELETE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SAVE_LICENSE_SETTING, token).apply();
    }

    public static String getSaveLicenseSetting(Context context) {
        return context.getSharedPreferences(SP_NAME_NOT_DELETE, MODE_PRIVATE).getString(SAVE_LICENSE_SETTING, "");
    }

    public static void setTodayCallList(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TODAY_CALL_LIST, token).apply();
    }

    public static String getTodayCallList(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TODAY_CALL_LIST, "");
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

    public static void setTagApiImageUrl(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TAG_API_IMAGE_URL, token).apply();
    }

    public static String getTagApiImageUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TAG_API_IMAGE_URL, "");
    }

    public static void saveHq(Context context, String name, String code) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(HQ_NAME, name);
        editor.putString(HQ_CODE, code).apply();
    }

    public static String getHqName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HQ_NAME, "");
    }

    public static String getHqCode(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HQ_CODE, "");
    }

    public static void setApprovalsCounts(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(APPROVAL_COUNT, token).apply();
    }

    public static String getApprovalsCounts(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(APPROVAL_COUNT, "");
    }

    public static void setTodayDayPlanClusterCode(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TodayDayPlanClusterCode, status).apply();
    }

    public static String getTodayDayPlanClusterCode(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TodayDayPlanClusterCode, "");
    }

    public static void setCheckTodayCheckInOut(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHECK_TODAY_DATE_CHECKINOUT, status).apply();
    }

    public static String getCheckTodayCheckInOut(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHECK_TODAY_DATE_CHECKINOUT, "");
    }

    public static void setCheckInTime(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHECK_IN_TIME, status).apply();
    }

    public static String getCheckInTime(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHECK_IN_TIME, "");
    }

    public static void setCheckDateTodayPlan(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHECK_DATE_TODAY_PLAN, status).apply();
    }

    public static String getCheckDateTodayPlan(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHECK_DATE_TODAY_PLAN, "");
    }

    public static void setSetUpClickedTab(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SET_UP_CLICKED_TAB, status).apply();
    }

    public static String getSetUpClickedTab(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SET_UP_CLICKED_TAB, "0");
    }

    public static void setSkipCheckIn(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SKIP_CHECK_IN, status).apply();
    }

    public static boolean getSkipCheckIn(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(SKIP_CHECK_IN, false);
    }

    public static void setSelectedDateCal(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SELECTED_DATE_CAL, status).apply();
    }

    public static String getSelectedDateCal(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SELECTED_DATE_CAL, "");
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

    public static void putAutomassync(Context context, boolean mas_sync) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SYNC_STATUS, mas_sync).apply();
    }


    public static void putSlidestatus(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SLIDE_DOWNLOADING_STATUS, status).apply();
    }


    public static boolean getSlideDowloadingStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(SLIDE_DOWNLOADING_STATUS, false);
    }


    public static boolean getAutomassyncFromSP(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(SYNC_STATUS, false);
    }

    public static int getMonthForClearCalls(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(SP_CALL_ClEAR_MONTH, 15);
    }

    public static void putMonth(Context context,int Month) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt( SP_CALL_ClEAR_MONTH, Month).apply();
    }

    public static void setDcr_dochqcode(Context context, String status) {//SETHQCODE,SETHQ_DETAILS
        sharedPreferences = context.getSharedPreferences(SETHQ_DETAILS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SETHQCODE, status).apply();
    }

    public static String getDcrdoc_hqcode(Context context) {
        return context.getSharedPreferences(SETHQ_DETAILS, MODE_PRIVATE).getString(SETHQCODE, "");
    }


}
