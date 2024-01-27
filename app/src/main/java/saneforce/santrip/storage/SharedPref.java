package saneforce.santrip.storage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

import saneforce.santrip.activity.slideDownloaderAlertBox.SlideModelClass;

public class SharedPref {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

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
    public static final String SAVE_URL_SETTING="save_url_setting";
    public static final String SAVE_LICENSE_SETTING="save_url_license";


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

    //Approval
    public static final String APPROVAL_COUNT = "approval_count";

    // Slide
    public static final String SLIDEID = "slideid";
    public static final String SLIDELIST = "slidelist";

    public static final String SLIDEDOWNCOUNT = "slidedowncount";

    public static final String SYNC_STATUS = "SP_MAS_DETAILS";


    public static void clearSP(Context context){
        sharedPreferences = context.getSharedPreferences(SP_NAME,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear().apply();
    }

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

    public static void setSaveUrlSetting(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SAVE_URL_SETTING, token).apply();
    }

    public static String getSaveUrlSetting(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SAVE_URL_SETTING, "");
    }

    public static void setSaveLicenseSetting(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SAVE_LICENSE_SETTING, token).apply();
    }

    public static String getSaveLicenseSetting(Context context) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(SAVE_LICENSE_SETTING, "");
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

    public static void putAutomassync(Context context, boolean mas_sync) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SYNC_STATUS, mas_sync).apply();
    }



    public static boolean getAutomassyncFromSP(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(SYNC_STATUS, false);
    }
}
