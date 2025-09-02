package saneforce.sanzen.storage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    public static final String SLIDES_PATH = "SLIDES_PATH";
    public static final String WELCOME_SLIDES_PATH = "WELCOME_SLIDES_PATH";
    public static final String LOGO_URL = "logo_url";
    public static final String OPTION_FILES_URL = "logo_url";
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
    public static final String HQ_NAMEMAIN = "HQNameMain";
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
    public static final String SFDCR_DATE = "SFDCRDate";
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
    public static final String DCR_APPROVAL_NEED = "DcrapprvNd";
    public static final String ONE_BUILD = "one_build";
    public static final String S3_BUCKET_NEED = "S3BucketNeed";
    public static final String DYNAMIC_OPTION_NEED = "dynamic_option_need";
    public static final String DYNAMIC_OPTION_CAPS = "dynamic_option_caps";
    //myresource
    public static final String SETSYNHQ = "SETSYNHQ";
    public static final String SETSYN_HQCODE = "SETSYN_HQCODE";

    //Master Sync
    public static final String MASTER_LAST_SYNC = "last_sync";
    public static final String HQ_CODE = "hq_code";
    public static final String MULTI_HQ_CODE = "multi_hq_code";
    public static final String MULTI_HQ_NAME = "multi_hq_name";
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
    public static final String TodayDayPlanClusterName = "today_plan_cluster_name";

    //TodayCalls
    public static final String TODAY_CALL_LIST = "today_call_list";
    //Approval
    public static final String APPROVAL_COUNT = "approval_count";
    // Slide
    public static final String TAB_STATUS = "TAB_STATUS";
    public static final String SYNC_STATUS = "SP_MAS_DETAILS";
    public static SharedPreferences sharedPreferences;
    public static final String SP_CALL_ClEAR_MONTH = "call_clear_month";
    public static final String SETHQCODE = "SETHQCODE";
    public static final String SETHQ_DETAILS = "SETHQ_DETAILS";

    public static final String IS_MYDAYPLAN = "IS_MYDAYPLAN";
    public static final String IS_FEILD = "IS_FEILD";

    public static final String JWKCODE = "JWKCODE";
    public static final String JWKDATE = "JWKDATE";

    public static final String TP_MANATORY_STATUS = "TP_MANATORY_STATUS";
    public static final String APPROVAL_MANATORY_STATUS = "APPROVAL_MANATORY_STATUS";
    public static final String APPROVAL_SKIPDATE = "APPROVAL_SKIPDATE";
    public static final String SKIPDATE = "SKIPDATE";

    public static final String POLICY_STAUS = "POLICYSTATUS";
    public static final String TPSYNC_STAUS = "TPSYNC_STAUS";

    public static final String DR_ADD_CALL_NEED = "DrAdditionalCallNeed";
    public static final String POB_STOCKIST_NEED = "Pob_Stockist_Nd";
    public static final String POB_UNLISTED_DR_NEED = "Pob_Unlstdr_Nd";
    public static final String DCR_SEQUENTIAL = "dcr_sequential";

    public static final String DAY_PLAN_STARTED = "day_plan_started";
    public static final String PRESENTATION_NEED = "PresentNd";
    public static final String CUSTOMIZATION_PRESENTATION_NEED = "Customization_need";
    public static final String THERAPTIC_PRESENTATION_NEED = "TherapticNd";
    public static final String STOCKIST_POB_NEED = "Pob_Stockist_Nd";
    public static final String UNLISTED_DOCTOR_POB_NEED = "Pob_Unlstdr_Nd";
    public static final String ADDITIONAL_CALL_NEED = "Additional_Call";

    public static final String SLIDE_DOWNLOADING_STATUS = "Slide_downloding_status";
    public static final String WELCOME_SLIDE_DOWNLOADING_STATUS = "Welcome_slide_downloding_status";
    public static final String LAST_CALL_DATE = "Last_Call_Date";
    public static final String LAST_OUTBOX_ALERT_DATE = "Last_Outbox_Alert_Date";

    public static final String CHM_DETAILING_NEED = "Detailing_chem";
    public static final String STK_DETAILING_NEED = "Detailing_stk";
    public static final String UNDR_DETAILING_NEED = "Detailing_undr";
    public static final String EDIT_CALL_DEL_NEED = "EditCallDelNeed";

    public static final String ACTIVITY_CAP = "ActivityCap";
    public static final String JOINGMONTH = "JOING_MONTH";
    public static final String JOININGDATE = "JOINING_DATE";
    public static final String JOININGYEAR = "JOINING_YEAR";
    public static final String SANZEN_EDET = "sanzen_edet";

    public static final String STP_STATUS = "STP_STATUS";
    public static final String STP_NEED = "STP_NEED";
    public static final String STP_APPR_NEED = "STP_APPR_NEED";
    public static final String STP_BASED_MTP = "STP_BASED_MTP";
    public static final String STP_BASED_DCR = "STP_BASED_DCR";
    public static final String STP_CAPTION = "Stp_Caption";

    public static final String LAST_CALL_SYNC_DATE = "Last Call Sync Date";

    public static final String SELECTED_QUALIFICATION = "selectedqualification";
    public static final String SELECTED_CATEGORY = "selectedcategory";
    public static final String SELECTED_SPECIALITY = "selectedspeciality";
    public static final String SELECTED_CLUSTER = "selcectedcluster";
    public static final String SELECT_HQ = "selectedhq";
    public static final String TAGGED_ADDRESS = "tggedaddress";
    public static final String SELECTED_CLASS = "selectedclass";

    public static final String LATITUDE = "lati";
    public static final String LONGITUDE = "longi";
    public static final String ADD_CHM = "addChm";
    public static final String ADD_UNLST = "addDr";

    public static final String HOLIDAY_AUTOPOST_NEED = "Holiday_AutoPost_Need";
    public static final String WEEKOFF_AUTOPOST_NEED = "Weekoff_AutoPost_Need";
    //    public static final String TAGGED_ADDRESS = "tggedaddress";
    public static final String PROFILING_NEED = "DrProfile";

    public static final String QUIZ_DATE = "Quiz Date";
    public static final String QUIZ_SYNC_DATE = "Quiz Sync Date";
    public static final String QUIZ_ATTEMPTS = "Quiz Attempts";
    public static final String QUIZ_ASSERT_DOWNLOADING_STATUS = "Quiz_assert_downloding_status";
    public static final String QUIZ_AVAILABLE_DATE = "Quiz Available Date";
    public static final String QUIZ_DATA = "Quiz Data";

    public static final String SEQ_DLY_CTRL = "Seq_dly_ctrl";
    public static final String SEQ_DCR_LOCK_DAYS = "SeqDcrLockDays";
    public static final String DELAY_HW_NEED = "Delay_HW_Need";

    public static final String TAGGED_DCR_CUSTOMERS = "Tagged DCR Customers";

    public static final String DETAILING_IDLE_DURATION = "detailing_idle_duration";
//    public static final String S3_BUCKET_NEED = "S3BucketNeed";

    public static final String TPDCR_DEVIATED_DATE = "TPDCR_Deviated_date";

    public static final String DAY_CHECK_IN_DATA = "Day_Check_In_Data";
    public static final String CHECK_IN_SKIP_DATE = "Check_In_Skip_Date";

    public static final String A_S_KEY = "A_S_KEY";

    public static String TpIdCurrent = "tpIdCurrent";
    public static String TpIdPrevious = "tpIdPrevious";
    public static String TpIdNext     = "tpIdNext";

    public static SharedPreferences.Editor editor;

    public static void clearSP(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public static void InsertLogInData(Context context, JSONObject jsonObject) {
        try {

        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SF_CODE, jsonObject.optString("SF_Code"));
        editor.putString(SF_NAME, jsonObject.optString("SF_Name"));
        editor.putString(SF_PASSWORD, jsonObject.optString("SF_Password"));
        editor.putString(SF_TYPE, jsonObject.optString("sf_type"));
        editor.putString(DESIG_CODE, jsonObject.optString("desig_Code"));
        editor.putString(DIVISION_NAME, jsonObject.optString("Division_name"));
        editor.putString(DESIG, jsonObject.optString("Desig"));
       // editor.putString(HQ_NAME, jsonObject.optString("HQName"));
        editor.putString(SF_STAT, jsonObject.optString("SFStat"));
        editor.putString(DIVISION_CODE, jsonObject.optString("Division_Code"));
        editor.putString(T_BASE, jsonObject.optString("TBase"));
        editor.putString(GEO_CHK, jsonObject.optString("GeoChk"));
        editor.putString(GEO_NEED, jsonObject.optString("GeoNeed"));
        editor.putString(CHM_NEED, jsonObject.optString("ChmNeed"));
        editor.putString(STK_NEED, jsonObject.optString("StkNeed"));
        editor.putString(UNL_NEED, jsonObject.optString("UNLNeed"));
        editor.putString(DP_NEED, jsonObject.optString("DPNeed"));
        editor.putString(DI_NEED, jsonObject.optString("DINeed"));
        editor.putString(CHM_RX_QTY, jsonObject.optString("ChmRxQty"));
        editor.putString(HOS_POB_MD, jsonObject.optString("HosPOBMd"));
        editor.putString(HOS_POB_ND, jsonObject.optString("HosPOBNd"));
        editor.putString(SAMPLE_VALIDATION, jsonObject.optString("sample_validation"));
        editor.putString(INPUT_VALIDATION, jsonObject.optString("input_validation"));
        editor.putString(DOC_BUSINESS_PRODUCT, jsonObject.optString("doc_business_product"));
        editor.putString(DOC_BUSINESS_VALUE, jsonObject.optString("doc_business_value"));
        editor.putString(DCR_DOC_BUSINESS_PRODUCT, jsonObject.optString("dcr_doc_business_product"));
         editor.putString(DR_MAPPINGPRODUCT, jsonObject.optString("Dr_mappingproduct"));
        editor.putString(CP_NEED, jsonObject.optString("CPNeed"));
        editor.putString(CI_NEED, jsonObject.optString("CINeed"));
        editor.putString(CMPGN_NEED, jsonObject.optString("CmpgnNeed"));
        editor.putString(SP_NEED, jsonObject.optString("SPNeed"));
        editor.putString(SI_NEED, jsonObject.optString("SINeed"));
        editor.putString(VST_ND, jsonObject.optString("VstNd"));
        editor.putString(MSD_ENTRY, jsonObject.optString("MsdEntry"));
        editor.putString(NP_NEED, jsonObject.optString("NPNeed"));
        editor.putString(NI_NEED, jsonObject.optString("NINeed"));
        editor.putString(CAT_NEED, jsonObject.optString("Catneed"));
        editor.putString(CHM_AD_QTY, jsonObject.optString("chm_ad_qty"));
        editor.putString(CAMP_NEED, jsonObject.optString("Campneed"));
        editor.putString(CHE_BASE, jsonObject.optString("CHEBase"));
        editor.putString(DR_CAP, jsonObject.optString("DrCap"));
        editor.putString(CHM_CAP, jsonObject.optString("ChmCap"));
        editor.putString(STK_CAP, jsonObject.optString("StkCap"));
        editor.putString(NL_CAP, jsonObject.optString("NLCap"));
        editor.putString(USER_N, jsonObject.optString("UserN"));
        editor.putString(PASS, jsonObject.optString("Pass"));
        editor.putString(DR_RX_Q_CAP, jsonObject.optString("DrRxQCap"));
        editor.putString(CHM_RCPA_MD, jsonObject.optString("ChmRcpaMd"));
        editor.putString(CHM_RCPA_MD_MGR, jsonObject.optString("ChmRcpaMd_Mgr"));
        editor.putString(DR_SMP_Q_CAP, jsonObject.optString("DrSmpQCap"));
        editor.putString(DR_RX_ND, jsonObject.optString("DrRxNd"));
        editor.putString(APPROVE_NEED, jsonObject.optString("Approveneed"));
        editor.putString(EXPENSENEED, jsonObject.optString("Expenseneed"));
        editor.putString(TERRITORY_VST_ND, jsonObject.optString("Territory_VstNd"));
        editor.putString(TRAVEL_DISTANCE_NEED, jsonObject.optString("travelDistance_Need"));
        editor.putString(ANDROID_APP, jsonObject.optString("Android_App"));
        editor.putString(IOS_APP, jsonObject.optString("ios_app"));
        editor.putString(DOC_INPUT_CAPTION, jsonObject.optString("Doc_Input_caption"));
        editor.putString(CHM_INPUT_CAPTION, jsonObject.optString("Chm_Input_caption"));
        editor.putString(DASHBOARD, jsonObject.optString("dashboard"));
        editor.putString(SRT_ND, jsonObject.optString("SrtNd"));
        editor.putString(UL_STK_NEED, jsonObject.optString("Ul_stk_Need"));
        editor.putString(UL_STK_MANDATORY, jsonObject.optString("Ul_stk_Mandatory"));
        editor.putString(TP_NEW, jsonObject.optString("tp_new"));
        editor.putString(TAXNAME_CAPTION, jsonObject.optString("Taxname_caption"));
        editor.putString(SURVEY_ND, jsonObject.optString("SurveyNd"));
        editor.putString(ST_STK_NEED, jsonObject.optString("st_stk_Need"));
        editor.putString(ST_STK_MANDATORY, jsonObject.optString("st_stk_Mandatory"));
        editor.putString(SRT_MAND_ND, jsonObject.optString("SrtMandNd"));
        editor.putString(SINGLE_ACTIVITY, jsonObject.optString("single_activity"));
        editor.putString(QUIZ_NEED_MANDT, jsonObject.optString("quiz_need_mandt"));
        editor.putString(QUIZ_HEADING, jsonObject.optString("quiz_heading"));
        editor.putString(QUICKADD, jsonObject.optString("quickadd"));
        editor.putString(PRODUCT_RATE_EDITABLE, jsonObject.optString("Product_Rate_Editable"));
        editor.putString(OFFLINE_LOGIN, jsonObject.optString("offlineLogin"));
        editor.putString(OFFICEWORK_HOME, jsonObject.optString("officework_home"));
        editor.putString(HOSP_DCR, jsonObject.optString("hosp_dcr"));
        editor.putString(DR_POLICY, jsonObject.optString("DrPolicy"));
        editor.putString(DOC_STK_NEED, jsonObject.optString("Doc_stk_Need"));
        editor.putString(DOC_STK_MANDATORY, jsonObject.optString("Doc_stk_Mandatory"));
        editor.putString(CUST_SRT_ND, jsonObject.optString("CustSrtNd"));
        editor.putString(CHM_STK_NEED, jsonObject.optString("Chm_stk_Need"));
        editor.putString(CHM_STK_MANDATORY, jsonObject.optString("Chm_stk_Mandatory"));
        editor.putString(ADD_DR, jsonObject.optString("addDr"));
        editor.putString(ACTIVITY_NEED, jsonObject.optString("activityneed"));
        editor.putString(SF_EMAIL, jsonObject.optString("sfEmail"));
        editor.putString(SF_MOBILE, jsonObject.optString("sfMobile"));
        editor.putString(DS_NAME, jsonObject.optString("DS_name"));
        editor.putString(ACTIVITY_ND, jsonObject.optString("ActivityNd"));
        editor.putString(ACTIVITY_MAND, jsonObject.optString("activityMand"));
        editor.putString(SEQUENTIAL_DCR, jsonObject.optString("sequential_dcr"));
        editor.putString(MYDAYPLAN_NEED, jsonObject.optString("mydayplan_need"));
        editor.putString(MISSED_DATE_MAND, jsonObject.optString("missedDateMand"));
        editor.putString(MEDIA_TRANS_NEED, jsonObject.optString("mediaTrans_Need"));
        editor.putString(DR_SMP_Q_MD, jsonObject.optString("DrSmpQMd"));
        editor.putString(DR_RX_Q_MD, jsonObject.optString("DrRxQMd"));
        editor.putString(MYPLN_RMRKS_MAND, jsonObject.optString("myplnRmrksMand"));
        editor.putString(STK_INPUT_CAPTION, jsonObject.optString("Stk_Input_caption"));
        editor.putString(UL_INPUT_CAPTION, jsonObject.optString("Ul_Input_caption"));
        editor.putString(RCPA_ND, jsonObject.optString("RcpaNd"));
        editor.putString(RCPA_COMPETITOR_EXTRA, jsonObject.optString("Rcpa_Competitor_extra"));
        editor.putString(DETAILING_TYPE, jsonObject.optString("Detailing_type"));
        editor.putString(CIP_SRT_ND, jsonObject.optString("CipSrtNd"));
        editor.putString(DCR_FIRSTSELFIE, jsonObject.optString("Dcr_firstselfie"));
        editor.putString(CHM_Q_CAP, jsonObject.optString("ChmQCap"));
        editor.putString(STK_Q_CAP, jsonObject.optString("StkQCap"));
        editor.putString(MULTIPLE_DOC_NEED, jsonObject.optString("multiple_doc_need"));
        editor.putString(MAIL_NEED, jsonObject.optString("mailneed"));
        editor.putString(CIRCULAR, jsonObject.optString("circular"));
        editor.putString(DR_FEED_MD, jsonObject.optString("DrFeedMd"));
        editor.putString(DF_NEED, jsonObject.optString("DFNeed"));
        editor.putString(CF_NEED, jsonObject.optString("CFNeed"));
        editor.putString(SF_NEED, jsonObject.optString("SFNeed"));
        editor.putString(CIP_F_NEED, jsonObject.optString("CIP_FNeed"));
        editor.putString(NF_NEED, jsonObject.optString("NFNeed"));
        editor.putString(HF_NEED, jsonObject.optString("HFNeed"));
        editor.putString(DQ_NEED, jsonObject.optString("DQNeed"));
        editor.putString(CQ_NEED, jsonObject.optString("CQNeed"));
        editor.putString(SQ_NEED, jsonObject.optString("SQNeed"));
        editor.putString(NQ_NEED, jsonObject.optString("NQNeed"));
        editor.putString(CIP_Q_NEED, jsonObject.optString("CIP_QNeed"));
        editor.putString(HQ_NEED, jsonObject.optString("HQNeed"));
        editor.putString(DE_NEED, jsonObject.optString("DENeed"));
        editor.putString(CE_NEED, jsonObject.optString("CENeed"));
        editor.putString(SE_NEED, jsonObject.optString("SENeed"));
        editor.putString(NE_NEED, jsonObject.optString("NENeed"));
        editor.putString(CIP_E_NEED, jsonObject.optString("CIP_ENeed"));
        editor.putString(HE_NEED, jsonObject.optString("HENeed"));
        editor.putString(TP_NEED, jsonObject.optString("tp_need"));
        editor.putString(CLUSTER_CAP, jsonObject.optString("cluster_cap"));
        editor.putString(WRK_AREA_NAME, jsonObject.optString("wrk_area_Name"));
        editor.putString(NL_RX_Q_CAP, jsonObject.optString("NLRxQCap"));
        editor.putString(NL_SMP_Q_CAP, jsonObject.optString("NLSmpQCap"));
        editor.putString(APP_DEVICE_ID, jsonObject.optString("app_device_id"));
        editor.putString(USR_DFD_USERNAME, jsonObject.optString("UsrDfd_UserName"));
        editor.putString(SF_USER_NAME, jsonObject.optString("SF_User_Name"));
        editor.putString(ATTENDANCE, jsonObject.optString("Attendance"));
        editor.putString(DEVICE_ID_NEED, jsonObject.optString("DeviceId_Need"));
        editor.putString(DOCTOR_DOBDOW, jsonObject.optString("doctor_dobdow"));
        editor.putString(PRODUCT_POB_NEED_MSG, jsonObject.optString("product_pob_need_msg"));
        editor.putString(PROD_REMARK, jsonObject.optString("prod_remark"));
        editor.putString(PROD_REMARK_MD, jsonObject.optString("prod_remark_md"));
        editor.putString(CIP_NEED, jsonObject.optString("cip_need"));
        editor.putString(CIP_P_NEED, jsonObject.optString("CIP_PNeed"));
        editor.putString(CIP_I_NEED, jsonObject.optString("CIP_INeed"));
        editor.putString(DR_PRD_MD, jsonObject.optString("DrPrdMd"));
        editor.putString(DR_INP_MD, jsonObject.optString("DrInpMd"));
        editor.putString(CIP_JOINTWORK_NEED, jsonObject.optString("CIP_jointwork_Need"));
        editor.putString(CIP_CAPTION, jsonObject.optString("CIP_Caption"));
        editor.putString(HOSP_CAPTION, jsonObject.optString("hosp_caption"));
        editor.putString(SEP_RCPA_ND, jsonObject.optString("Sep_RcpaNd"));
        editor.putString(PAST_LEAVE_POST, jsonObject.optString("past_leave_post"));
        editor.putString(DLY_CTRL, jsonObject.optString("DlyCtrl"));
        editor.putString(FEED_ND, jsonObject.optString("FeedNd"));
        editor.putString(TEMP_ND, jsonObject.optString("TempNd"));
        editor.putString(PROD_DET_NEED, jsonObject.optString("prod_det_need"));
        editor.putString(CNT_REMARKS, jsonObject.optString("cntRemarks"));
        editor.putString(PRODUCT_POB_NEED, jsonObject.optString("product_pob_need"));
        editor.putString(SECONDARY_ORDER_DISCOUNT, jsonObject.optString("secondary_order_discount"));
        editor.putString(TARGET_REPORT_MD, jsonObject.optString("Target_report_md"));
        editor.putString(RCPA_UNIT_ND, jsonObject.optString("RCPA_unit_nd"));
        editor.putString(CHM_RCPA_NEED, jsonObject.optString("Chm_RCPA_Need"));
        editor.putString(DR_RCPA_COMPETITOR_NEED, jsonObject.optString("DrRCPA_competitor_Need"));
        editor.putString(CHM_RCPA_COMPETITOR_NEED, jsonObject.optString("ChmRCPA_competitor_Need"));
        editor.putString(CURRENTDAY_TPPLANNED, jsonObject.optString("Currentday_TPplanned"));
        editor.putString(DOC_CLUSTER_BASED, jsonObject.optString("Doc_cluster_based"));
        editor.putString(CHM_CLUSTER_BASED, jsonObject.optString("Chm_cluster_based"));
        editor.putString(STK_CLUSTER_BASED, jsonObject.optString("Stk_cluster_based"));
        editor.putString(ULDOC_CLUSTER_BASED, jsonObject.optString("UlDoc_cluster_based"));
        editor.putString(MULTI_CLUSTER, jsonObject.optString("multi_cluster"));
        editor.putString(TERR_BASED_TAG, jsonObject.optString("Terr_based_Tag"));
        editor.putString(RCPA_MD_MGR, jsonObject.optString("RcpaMd_Mgr"));
        editor.putString(DR_NEED, jsonObject.optString("DrNeed"));
        editor.putString(FAQ, jsonObject.optString("faq"));
        editor.putString(EDIT_HOLIDAY, jsonObject.optString("edit_holiday"));
        editor.putString(EDIT_WEEKLYOFF, jsonObject.optString("edit_weeklyoff"));
        editor.putString(TARGET_REPORT_ND, jsonObject.optString("Target_report_Nd"));
        editor.putString(DCR_LOCK_DAYS, jsonObject.optString("DcrLockDays"));
        editor.putString(DOC_POB_CAPTION, jsonObject.optString("Doc_pob_caption"));
        editor.putString(STK_POB_CAPTION, jsonObject.optString("Stk_pob_caption"));
        editor.putString(CHM_POB_CAPTION, jsonObject.optString("Chm_pob_caption"));
        editor.putString(ULDOC_POB_CAPTION, jsonObject.optString("Uldoc_pob_caption"));
        editor.putString(CIP_POB_CAPTION, jsonObject.optString("CIP_pob_caption"));
        editor.putString(HOSP_POB_CAPTION, jsonObject.optString("Hosp_pob_caption"));
        editor.putString(DOC_POB_MANDATORY_NEED, jsonObject.optString("Doc_Pob_Mandatory_Need"));
        editor.putString(CHM_POB_MANDATORY_NEED, jsonObject.optString("Chm_Pob_Mandatory_Need"));
        editor.putString(DR_SAMP_ND, jsonObject.optString("DrSampNd"));
        editor.putString(RCPA_MD, jsonObject.optString("RcpaMd"));
        editor.putString(MISC_EXPENSE_NEED, jsonObject.optString("misc_expense_need"));
        editor.putString(DOC_POB_NEED, jsonObject.optString("Doc_Pob_Need"));
        editor.putString(CHM_POB_NEED, jsonObject.optString("Chm_Pob_Need"));
        editor.putString(STK_POB_NEED, jsonObject.optString("Stk_Pob_Need"));
        editor.putString(UL_POB_NEED, jsonObject.optString("Ul_Pob_Need"));
        editor.putString(STK_POB_MANDATORY_NEED, jsonObject.optString("Stk_Pob_Mandatory_Need"));
        editor.putString(UL_POB_MANDATORY_NEED, jsonObject.optString("Ul_Pob_Mandatory_Need"));
        editor.putString(DOC_JOINTWORK_NEED, jsonObject.optString("Doc_jointwork_Need"));
        editor.putString(CHM_JOINTWORK_NEED, jsonObject.optString("Chm_jointwork_Need"));
        editor.putString(STK_JOINTWORK_NEED, jsonObject.optString("Stk_jointwork_Need"));
        editor.putString(UL_JOINTWORK_NEED, jsonObject.optString("Ul_jointwork_Need"));
        editor.putString(DOC_JOINTWORK_MANDATORY_NEED, jsonObject.optString("Doc_jointwork_Mandatory_Need"));
        editor.putString(CHM_JOINTWORK_MANDATORY_NEED, jsonObject.optString("Chm_jointwork_Mandatory_Need"));
        editor.putString(STK_JOINTWORK_MANDATORY_NEED, jsonObject.optString("Stk_jointwork_Mandatory_Need"));
        editor.putString(UL_JOINTWORK_MANDATORY_NEED, jsonObject.optString("Ul_jointwork_Mandatory_Need"));
        editor.putString(DOC_PRODUCT_CAPTION, jsonObject.optString("Doc_Product_caption"));
        editor.putString(CHM_PRODUCT_CAPTION, jsonObject.optString("Chm_Product_caption"));
        editor.putString(STK_PRODUCT_CAPTION, jsonObject.optString("Stk_Product_caption"));
        editor.putString(UL_PRODUCT_CAPTION, jsonObject.optString("Ul_Product_caption"));
        editor.putString(REMAINDER_PRD_MD, jsonObject.optString("Remainder_prd_Md"));
        editor.putString(GEOTAG_NEED, jsonObject.optString("GEOTagNeed"));
        editor.putString(GEOTAG_NEED_CHE, jsonObject.optString("GEOTagNeedche"));
        editor.putString(REMAINDER_GEO, jsonObject.optString("Remainder_geo"));
        editor.putString(GEOTAG_NEED_STOCK, jsonObject.optString("GEOTagNeedstock"));
        editor.putString(GEOTAG_NEED_CIP, jsonObject.optString("GeoTagNeedcip"));
        editor.putString(GEOTAG_NEED_UNLST, jsonObject.optString("GEOTagNeedunlst"));
        editor.putString(DIS_RAD, jsonObject.optString("DisRad"));
        editor.putString(DEVICE_REG_ID, jsonObject.optString("DeviceRegId"));
        editor.putString(SFTP_DATE, jsonObject.optString("SFTPDate"));
        editor.putString(SFDCR_DATE, jsonObject.optString("SFDCRDate"));
        editor.putString(MCL_DET, jsonObject.optString("MCLDet"));
        editor.putString(RMDR_NEED, jsonObject.optString("RmdrNeed"));
        editor.putString(EXPENSE_NEED, jsonObject.optString("expense_need"));
        editor.putString(GEOTAG_IMG, jsonObject.optString("geoTagImg"));
        editor.putString(DR_RCPA_Q_MD, jsonObject.optString("DrRcpaQMd"));
        editor.putString(QUES_NEED, jsonObject.optString("ques_need"));
        editor.putString(HOSP_NEED, jsonObject.optString("hosp_need"));
        editor.putString(HP_NEED, jsonObject.optString("HPNeed"));
        editor.putString(HI_NEED, jsonObject.optString("HINeed"));
        editor.putString(CHMSAMQTY_NEED, jsonObject.optString("chmsamQty_need"));
        editor.putString(CHM_SMP_CAP, jsonObject.optString("ChmSmpCap"));
        editor.putString(CALL_FEED_ENTERABLE, jsonObject.optString("call_feed_enterable"));
        editor.putString(PWD_SETUP, jsonObject.optString("Pwdsetup"));
        editor.putString(RCPA_EXTRA, jsonObject.optString("rcpaextra"));
        editor.putString(LEAVE_STATUS, jsonObject.optString("LeaveStatus"));
        editor.putString(STATE_CODE, jsonObject.optString("State_Code"));
        editor.putString(SF_EMP_ID, jsonObject.optString("sf_emp_id"));
        editor.putString(SUBDIVISION_CODE, jsonObject.optString("subdivision_code"));
        editor.putString(REMAINDER_CALL_CAP, jsonObject.optString("Remainder_call_cap"));
        editor.putString(CALL_REPORT_FROM_DATE, jsonObject.optString("call_report_from_date"));
        editor.putString(CALL_REPORT_TO_DATE, jsonObject.optString("call_report_to_date"));
        editor.putString(CALL_REPORT, jsonObject.optString("call_report"));
        editor.putString(DAYS, jsonObject.optString("days"));
        editor.putString(MRHLFDY, jsonObject.optString("MRHlfDy"));
        editor.putString(ORDER_MANAGEMENT, jsonObject.optString("Order_management"));
        editor.putString(ORDER_CAPTION, jsonObject.optString("Order_caption"));
        editor.putString(PRIMARY_ORDER_CAPTION, jsonObject.optString("Primary_order_caption"));
        editor.putString(SECONDARY_ORDER_CAPTION, jsonObject.optString("Secondary_order_caption"));
        editor.putString(PRIMARY_ORDER, jsonObject.optString("Primary_order"));
        editor.putString(SECONDARY_ORDER, jsonObject.optString("Secondary_order"));
        editor.putString(GST_OPTION, jsonObject.optString("Gst_option"));
        editor.putString(TPDCR_DEVIATION_APPR_STATUS, jsonObject.optString("TPDCR_Deviation_Appr_Status"));
        editor.putString(TPDCR_DEVIATION, jsonObject.optString("TPDCR_Deviation"));
        editor.putString(TPDCR_MGRAPPR, jsonObject.optString("TPDCR_MGRAppr"));
        editor.putString(NEXT_VST, jsonObject.optString("NextVst"));
        editor.putString(NEXT_VST_MANDATORY_NEED, jsonObject.optString("NextVst_Mandatory_Need"));
        editor.putString(APPR_MANDATORY_NEED, jsonObject.optString("Appr_Mandatory_Need"));
        editor.putString(RCPA_QTY_NEED, jsonObject.optString("RCPAQty_Need"));
        editor.putString(PROD_STK_NEED, jsonObject.optString("Prod_Stk_Need"));
        editor.putString(TP_MANDATORY_NEED, jsonObject.optString("TP_Mandatory_Need"));
        editor.putString(TP_START_DATE, jsonObject.optString("Tp_Start_Date"));
        editor.putString(PRDFDBACK, jsonObject.optString("prdfdback"));
        editor.putString(TRACKING_INTERVAL, jsonObject.optString("tracking_interval"));
        editor.putString(TP_END_DATE, jsonObject.optString("Tp_End_Date"));
        editor.putString(DR_EVENT_MD, jsonObject.optString("DrEvent_Md"));
        editor.putString(CHM_EVENT_MD, jsonObject.optString("ChmEvent_Md"));
        editor.putString(STK_EVENT_MD, jsonObject.optString("StkEvent_Md"));
        editor.putString(ULDR_EVENT_MD, jsonObject.optString("UlDrEvent_Md"));
        editor.putString(CIP_EVENT_MD, jsonObject.optString("CipEvent_Md"));
        editor.putString(HOSP_EVENT_MD, jsonObject.optString("HospEvent_Md"));
        editor.putString(LEAVE_ENTITLEMENT_NEED, jsonObject.optString("Leave_entitlement_need"));
        editor.putString(PRIMARYSEC_NEED, jsonObject.optString("primarysec_need"));
        editor.putString(MGRHLFDY, jsonObject.optString("MGRHlfDy"));
        editor.putString(NO_OF_TP_VIEW, jsonObject.optString("No_of_TP_View"));
        editor.putString(CURRENTDAY, jsonObject.optString("currentDay"));
        editor.putString(DAYPLAN_TP_BASED, jsonObject.optString("dayplan_tp_based"));
        editor.putString(QUIZ_NEED, jsonObject.optString("quiz_need"));
        editor.putString(STP, jsonObject.optString("stp"));
        editor.putString(POB_MINVALUE, jsonObject.optString("pob_minvalue"));
        editor.putString(TPBASED_DCR, jsonObject.optString("TPbasedDCR"));
        editor.putString(LOCATION_TRACK, jsonObject.optString("Location_track"));
        editor.putString(TRACKING_TIME, jsonObject.optString("tracking_time"));
        editor.putString(ENTRY_FORM_MGR, jsonObject.optString("entryFormMgr"));
        editor.putString(ENTRY_FORM_NEED, jsonObject.optString("entryFormNeed"));
        editor.putString(DLY_CTRL_S, jsonObject.optString("Dly_Ctrls"));
        editor.putString(ANDROID_DETAILING, jsonObject.optString("Android_Detailing"));
        editor.putString(IOS_DETAILING, jsonObject.optString("ios_Detailing"));
        editor.putString(SAMPLE_VAL_QTY, jsonObject.optString("Sample_Val_Qty"));
        editor.putString(INPUT_VAL_QTY, jsonObject.optString("Input_Val_Qty"));
        editor.putString(QUOTE_TEXT, jsonObject.optString("quote_Text"));
        editor.putString(PRO_DET_NEED, jsonObject.optString("pro_det_need"));
        editor.putString(AUTHENTICATION, jsonObject.optString("Authentication"));
        editor.putString(GEOTAG_APPROVAL_NEED, jsonObject.optString("GeoTagApprovalNeed"));
        editor.putString(CHM_SRT_ND, jsonObject.optString("ChmSrtNd"));
        editor.putString(UNLIST_SRT_ND, jsonObject.optString("UnlistSrtNd"));
        editor.putString(RCPA_COMPETITOR_ADD, jsonObject.optString("RCPA_competitor_add"));
        editor.putString(GEOTAGGING, jsonObject.optString("GeoTagging"));
        editor.putString(DCR_SEQUENTIAL, jsonObject.optString("dcr_sequential"));
        editor.putString(PRESENTATION_NEED, jsonObject.optString("PresentNd"));
        editor.putString(CUSTOMIZATION_PRESENTATION_NEED, jsonObject.optString("Customization_need"));
        editor.putString(THERAPTIC_PRESENTATION_NEED, jsonObject.optString("TherapticNd"));
        editor.putString(STOCKIST_POB_NEED, jsonObject.optString("Pob_Stockist_Nd"));
        editor.putString(UNLISTED_DOCTOR_POB_NEED, jsonObject.optString("Pob_Unlstdr_Nd"));
        editor.putString(ADDITIONAL_CALL_NEED, jsonObject.optString("Additional_Call"));
        editor.putString(CHM_DETAILING_NEED, jsonObject.optString("Detailing_chem"));
        editor.putString(STK_DETAILING_NEED, jsonObject.optString("Detailing_stk"));
        editor.putString(UNDR_DETAILING_NEED, jsonObject.optString("Detailing_undr"));
        editor.putString(EDIT_CALL_DEL_NEED, jsonObject.optString("EditCallDelNeed"));
        editor.putString(SANZEN_EDET, jsonObject.optString("sanzen_edet"));
        editor.putString(ACTIVITY_CAP, jsonObject.optString("ActivityCap"));
        editor.putString(STP_NEED, jsonObject.optString("STP_Need"));
        editor.putString(STP_APPR_NEED, jsonObject.optString("STP_Appr_Need"));
        editor.putString(STP_BASED_MTP, jsonObject.optString("STP_Based_MTP"));
        editor.putString(STP_BASED_DCR, jsonObject.optString("STP_Based_DCR"));
        editor.putString(STP_CAPTION, jsonObject.optString("Stp_Caption"));
        editor.putString(DCR_APPROVAL_NEED, jsonObject.optString("DcrapprvNd"));
        editor.putString(ADD_CHM, jsonObject.optString("addChm"));
        editor.putString(ADD_UNLST, jsonObject.optString("addDr"));
        editor.putString(HOLIDAY_AUTOPOST_NEED, jsonObject.optString("Holiday_AutoPost_Need"));
        editor.putString(WEEKOFF_AUTOPOST_NEED, jsonObject.optString("Weekoff_AutoPost_Need"));
        editor.putString(SLIDES_PATH, jsonObject.optString("slide_folder").replaceAll("\\\\",""));
        editor.putString(PROFILING_NEED, jsonObject.optString("DrProfile"));
        editor.putString(SEQ_DLY_CTRL, jsonObject.optString("Seq_dly_ctrl"));
        editor.putString(SEQ_DCR_LOCK_DAYS, jsonObject.optString("SeqDcrLockDays"));
        editor.putString(DELAY_HW_NEED, jsonObject.optString("Delay_HW_Need"));
        editor.putString(DETAILING_IDLE_DURATION, jsonObject.optString("detailing_idle_duration"));
        editor.putString(WELCOME_SLIDES_PATH, "");
        editor.putString(ONE_BUILD,jsonObject.optString("one_build"));
        editor.putString(S3_BUCKET_NEED, jsonObject.getString("S3BucketNeed"));
        editor.putString(DYNAMIC_OPTION_NEED,jsonObject.getString("dynamic_option_need"));
        editor.putString(DYNAMIC_OPTION_CAPS,jsonObject.getString("dynamic_option_caps"));
        editor.apply();

        } catch (Exception ignore) {
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

    public static String getSfDCRDate(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SFDCR_DATE, "");
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

    public static String getDrAddCallNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DR_ADD_CALL_NEED, "");
    }

    public static String getStpNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STP_NEED, "");
    }

    public static String getStpApprNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STP_APPR_NEED, "");
    }

    public static String getStpBasedMtp(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STP_BASED_MTP, "");
    }

    public static String getStpBasedDcr(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STP_BASED_DCR, "");
    }

    public static String getStpCaption(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STP_CAPTION, "");
    }

    public static String getDcrApprovalNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DCR_APPROVAL_NEED, "");
    }
    public static String getOneBuild(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getString(ONE_BUILD,"");
    }


    public static String getS3BucketNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(S3_BUCKET_NEED, "");
    }
    public static String getDynamicOptionNeed(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getString(DYNAMIC_OPTION_NEED,"");
    }
    public static String getDynamicOptionCaps(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getString(DYNAMIC_OPTION_CAPS,"");
    }


    public static void setDrAddCallNeed(Context context, String drAddCallNeed) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DR_ADD_CALL_NEED, drAddCallNeed).apply();
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

    public static void saveUrls(Context context, String baseUrl, String licenseKey, String baseWebUrl, String PhpPathUrl, String reportsUrl, String logoUrl, String optionFiles, boolean settingState) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(BASE_URL, baseUrl);
        editor.putString(LICENSE_KEY, licenseKey);
        editor.putString(BASE_WEB_URL, baseWebUrl);
        editor.putString(PHP_PATH_URL, PhpPathUrl);
        editor.putString(REPORTS_URL, reportsUrl);
        editor.putString(LOGO_URL, logoUrl);
        editor.putString(OPTION_FILES_URL, optionFiles);
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
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SLIDES_PATH, "");
    }

    public static String getWelcomeSlideUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(WELCOME_SLIDES_PATH, "");
    }

    public static String getOptionFilesUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(OPTION_FILES_URL, "");
    }

    public static String getLogoUrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(LOGO_URL, "");
    }

    public static void saveSettingState(Context context, boolean state) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SETTING_STATE, state).apply();
    }

    public static void setTpMandatoryNeed(Context context, String state) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TP_MANDATORY_NEED, state).apply();
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

    public static void saveMultiHQ(Context context, String name, String code) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MULTI_HQ_NAME, name);
        editor.putString(MULTI_HQ_CODE, code).apply();
    }

    public static void saveHqMain(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(HQ_NAMEMAIN, name).apply();
    }

    public static void MydayPlanStausAndFeildWorkStatus(Context context, boolean MydayplanStatus, boolean FeildWorkStatus) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_MYDAYPLAN, MydayplanStatus);
        editor.putBoolean(IS_FEILD, FeildWorkStatus).apply();
    }

    public static boolean getFeildWorkStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(IS_FEILD, false);
    }

    public static boolean getMydayPlanStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(IS_MYDAYPLAN, false);
    }

    public static String getHqName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HQ_NAME, "");
    }

    public static String getHqNameMain(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HQ_NAMEMAIN, "");
    }

    public static String getHqCode(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(HQ_CODE, "");
    }

    public static String getMultiHQName(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MULTI_HQ_NAME, "");
    }

    public static String getMultiHQCode(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(MULTI_HQ_CODE, "");
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

    public static void setTodayDayPlanClusterName(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TodayDayPlanClusterName, status).apply();
    }

    public static String getTodayDayPlanClusterName(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TodayDayPlanClusterName, "");
    }

    public static void setCheckTodayCheckInOut(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHECK_TODAY_DATE_CHECKINOUT, date).apply();
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

    public static void setSetUpClickedTab(Context context, int status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(TAB_STATUS, status).apply();
    }

    public static int getSetUpClickedTab(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(TAB_STATUS, 0);
    }

    public static void setSkipCheckIn(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SKIP_CHECK_IN, status).apply();
    }

    public static boolean getSkipCheckIn(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(SKIP_CHECK_IN, false);
    }

    public static void setSelectedDateCal(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SELECTED_DATE_CAL, date).apply();
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
        editor.putBoolean(  SYNC_STATUS, mas_sync).apply();
    }

    public static void putSlidestatus(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SLIDE_DOWNLOADING_STATUS, status).apply();
    }

    public static boolean getSlideDowloadingStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(SLIDE_DOWNLOADING_STATUS, false);
    }

    public static void putWelcomeSlideStatus(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(WELCOME_SLIDE_DOWNLOADING_STATUS, status).apply();
    }

    public static boolean getWelcomeSlideDownloadingStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(WELCOME_SLIDE_DOWNLOADING_STATUS, false);
    }

    public static void putQuizAssertDownloadingStatus(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(QUIZ_ASSERT_DOWNLOADING_STATUS, status).apply();
    }

    public static boolean getQuizAssertDownloadingStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(QUIZ_ASSERT_DOWNLOADING_STATUS, false);
    }

    public static boolean getAutomassyncFromSP(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(SYNC_STATUS, false);
    }

    public static int getMonthForClearCalls(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(SP_CALL_ClEAR_MONTH, 15);
    }

    public static void putMonth(Context context, int Month) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(SP_CALL_ClEAR_MONTH, Month).apply();
    }

    public static void setDcr_dochqcode(Context context, String status) {//SETHQCODE,SETHQ_DETAILS
        sharedPreferences = context.getSharedPreferences(SETHQ_DETAILS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SETHQCODE, status).apply();
    }

    public static String getDcrdoc_hqcode(Context context) {
        return context.getSharedPreferences(SETHQ_DETAILS, MODE_PRIVATE).getString(SETHQCODE, "");
    }

    public static void setJWKCODE(Context context, List<String> Jwkcode, String JwkDate) {
        Gson gson = new Gson();
        String json = gson.toJson(Jwkcode);
        sharedPreferences = context.getSharedPreferences(SETHQ_DETAILS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(JWKDATE, JwkDate);
        editor.putString(JWKCODE, json).apply();
    }

    public static String getJWKCODE(Context context) {
        return context.getSharedPreferences(SETHQ_DETAILS, MODE_PRIVATE).getString(JWKCODE, "");
    }

    public static String getJWKDATE(Context context) {
        return context.getSharedPreferences(SETHQ_DETAILS, MODE_PRIVATE).getString(JWKDATE, "");
    }

    public static void setSyncHQ(Context context, List<String> List) {
        Gson gson = new Gson();
        String json = gson.toJson(List);
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SETSYN_HQCODE, json).apply();
    }

    public static List<String> getsyn_hqcode(Context context) {
        Gson gson = new Gson();
        String json = context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SETSYN_HQCODE, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void setTpStatus(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(TP_MANATORY_STATUS, status).apply();
    }

    public static boolean getTpmanatoryStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(TP_MANATORY_STATUS, false);
    }

    public static void setApprvalManatoryStatus(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(APPROVAL_MANATORY_STATUS, status).apply();
    }

    public static boolean getApprovalManatoryStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(APPROVAL_MANATORY_STATUS, false);
    }
    // APPROVAL_SKIPDATE

    public static void setApprovalSKIPDate(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(APPROVAL_SKIPDATE, status).apply();
    }

    public static String getApprovalskipDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(APPROVAL_SKIPDATE, "");
    }

    public static void setTpSKIPDate(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SKIPDATE, status).apply();
    }

    public static String getskipDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SKIPDATE, "");
    }

    public static void setPolicyStaus(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(POLICY_STAUS, status).apply();
    }

    public static boolean getPolicy(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(POLICY_STAUS, false);
    }

    public static void setTpSyncStaus(Context context, boolean status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(TPSYNC_STAUS, status).apply();
    }

    public static boolean getTpSyncStaus(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(TPSYNC_STAUS, false);
    }

    public static String getDayPlanStartedDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DAY_PLAN_STARTED, "");
    }

    public static void setDayPlanStartedDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DAY_PLAN_STARTED, date).apply();
    }

    public static String getLastOutboxAlertDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(LAST_OUTBOX_ALERT_DATE, "");
    }

    public static void setLastOutboxAlertDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LAST_OUTBOX_ALERT_DATE, date).apply();
    }

    public static String getDcrSequential(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DCR_SEQUENTIAL, "");
    }

    public static String getPresentationNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(PRESENTATION_NEED, "");
    }

    public static String getCustomizationPresentationNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CUSTOMIZATION_PRESENTATION_NEED, "");
    }

    public static String getTherapticPresentationNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(THERAPTIC_PRESENTATION_NEED, "");
    }

    public static String getStockistPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STOCKIST_POB_NEED, "");
    }

    public static String getUnlistedDoctorPobNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(UNLISTED_DOCTOR_POB_NEED, "");
    }

    public static String getAdditionalCallNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(ADDITIONAL_CALL_NEED, "");
    }

    public static void setLastCallDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LAST_CALL_DATE, date).apply();
    }

    public static String getLastCallDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(LAST_CALL_DATE, "");
    }

    public static String getCHMDetailingNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHM_DETAILING_NEED, "");
    }

    public static String getSTKDetailingNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STK_DETAILING_NEED, "");
    }

    public static String getUNDRDetailingNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(UNDR_DETAILING_NEED, "");
    }

    public static String getEditCallDelNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(EDIT_CALL_DEL_NEED, "");
    }

    public static String getActivityCap(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(ACTIVITY_CAP, "");
    }

    public static String getAppAccess(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SANZEN_EDET, "");
    }

    public static void setJointDate(Context context, int date, int month, int year) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(JOININGDATE, date);
        editor.putInt(JOINGMONTH, month);
        editor.putInt(JOININGYEAR, year).apply();
    }


    public static int getJoiningDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(JOININGDATE, 0);
    }

    public static int getJoiningMonth(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(JOINGMONTH, 0);
    }

    public static int getJoiningYear(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(JOININGYEAR, 0);
    }

    public static void setStpStatus(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STP_STATUS, status).apply();
    }

    public static String getStpStatus(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(STP_STATUS, "");
    }

    public static void setTpDcrDeviationApprStatus(Context context, String status) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TPDCR_DEVIATION_APPR_STATUS, status).apply();
    }

    public static void setTpDcrDeviatedDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TPDCR_DEVIATED_DATE, date).apply();
    }

    public static String getTpDcrDeviatedDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TPDCR_DEVIATED_DATE, "");
    }

    public static void setLastCallSyncDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LAST_CALL_SYNC_DATE, date).apply();
    }

    public static String getLastCallSyncDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(LAST_CALL_SYNC_DATE, "");
    }

    public static int getSelectedQualification(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(SELECTED_QUALIFICATION, 0);
    }

    public static int getSelectedCategory(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(SELECTED_CATEGORY, 0);
    }

    public static int getSelectedSpeciality(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(SELECTED_SPECIALITY, 0);
    }

    public static int getSelectedCluster(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(SELECTED_CLUSTER, 0);
    }

    public static String getHq(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SELECT_HQ, "");
    }

    public static String getSaveTaggedAddress(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TAGGED_ADDRESS, "");
    }

    public static void setSelectedCategory(Context context, int category) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(SELECTED_CATEGORY, category).apply();
    }

    public static void setSelectedCluster(Context context, int cluster) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(SELECTED_CLUSTER, cluster).apply();
    }

    public static void sethq(Context context, String hq) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SELECT_HQ, hq).apply();
    }

    public static void setSelectedQualification(Context context, int quali) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(SELECTED_QUALIFICATION, quali).apply();
    }

    public static void setSelectedSpeciality(Context context, int speciality) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(SELECTED_SPECIALITY, speciality).apply();
    }

    public static void setSelectedClass(Context context, int clas) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(SELECTED_CLASS, clas).apply();
    }

    public static int getSelectedClass(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(SELECTED_CLASS, 0);
    }

    public static void setSaveLatitude(Context context, double lat) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LATITUDE, String.valueOf(lat)).apply();
    }

    public static void setSaveLongitutde(Context context, double lat) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LONGITUDE, String.valueOf(lat)).apply();
    }

    public static void setSaveTaggedAddress(Context context, String lat) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TAGGED_ADDRESS, lat).apply();
    }

    public static String getChemistAddition(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(ADD_CHM, "");
    }

    public static String getUnlistAddition(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(ADD_UNLST, "");
    }

    public static void setProfilingNeed(Context context, String profiling) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PROFILING_NEED, profiling).apply();
    }

    public static String getProfilingNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(PROFILING_NEED, "");
    }

    public static String getHolidayAutoPostNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(HOLIDAY_AUTOPOST_NEED, "");
    }

    public static String getWeekoffAutoPostNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(WEEKOFF_AUTOPOST_NEED, "");
    }

    public static String getLastQuizSubmittedDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(QUIZ_DATE, "");
    }

    public static void setLastQuizSubmittedDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(QUIZ_DATE, date).apply();
    }

    public static int getQuizAttempts(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(QUIZ_ATTEMPTS, 0);
    }

    public static void setQuizAttempts(Context context, int attempts) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(QUIZ_ATTEMPTS, attempts).apply();
    }

    public static String getQuizAvailableDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(QUIZ_AVAILABLE_DATE, "");
    }

    public static void setQuizAvailableDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(QUIZ_AVAILABLE_DATE, date).apply();
    }

    public static String getLastQuizSyncDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(QUIZ_SYNC_DATE, "");
    }

    public static void setLastQuizSyncDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(QUIZ_SYNC_DATE, date).apply();
    }

    public static String getQuizData(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(QUIZ_DATA, "");
    }

    public static void setQuizData(Context context, String data) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(QUIZ_DATA, data).apply();
    }

    public static String getTaggedDcrCustomers(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(TAGGED_DCR_CUSTOMERS, "");
    }

    public static void setTaggedDcrCustomers(Context context, String data) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(TAGGED_DCR_CUSTOMERS, data).apply();
    }

    public static void setSeqDcrLockDays(Context context, String days) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SEQ_DCR_LOCK_DAYS, days).apply();
    }

    public static String getSeqDlyCtrl(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SEQ_DLY_CTRL, "");
    }

    public static String getSeqDcrLockDays(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SEQ_DCR_LOCK_DAYS, "");
    }

    public static String getDelayHwNeed(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DELAY_HW_NEED, "");
    }

    public static String getDetailingIdleDuration(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DETAILING_IDLE_DURATION, "");
    }

    public static void setDayCheckInData(Context context, String data) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DAY_CHECK_IN_DATA, data).apply();
    }

    public static String getDayCheckInData(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(DAY_CHECK_IN_DATA, "");
    }

    public static void setCheckInSkipDate(Context context, String date) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CHECK_IN_SKIP_DATE, date).apply();
    }

    public static String getCheckInSkipDate(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(CHECK_IN_SKIP_DATE, "");
    }

    public static void saveTpId(Context context, int retrievedIdPm) {
        try{
            sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putInt(TpIdPrevious,retrievedIdPm);
            editor.apply();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
    public static int getTpIdPreviousMonth(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getInt(TpIdPrevious,0);
    }
    public static void saveTpIdCm(Context context, int retrievedIdCm) {
        try{
            sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putInt(TpIdCurrent,retrievedIdCm);
            editor.apply();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
    public static int getTpIdCurrentMonth(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getInt(TpIdCurrent,0);
    }

    public static void saveTpIdNm(Context context, int retrievedIdNm) {
        try{
            sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putInt(TpIdNext,retrievedIdNm);
            editor.apply();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public static int getTpIdNextMonth(Context context){
        return context.getSharedPreferences(SP_NAME,MODE_PRIVATE).getInt(TpIdNext,0);
    }


    public static void saveKeys(Context context, String aKey, String sKey) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(A_S_KEY, aKey + "^^" + sKey).apply();
    }

    public static String getKeys(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(A_S_KEY, "^^");
    }

}
