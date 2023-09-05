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





   public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static void saveBaseUrl(Context context,String baseUrl){
        sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(BASE_URL,baseUrl).apply();
    }

    public static void saveUrls(Context context,String baseUrl,String licenseKey,String baseWebUrl,String PhpPathUrl,String reportsUrl,String SlidesUrl,String logoUrl,boolean settingState){
        sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(BASE_URL,baseUrl);
        editor.putString(LICENSE_KEY,licenseKey);
        editor.putString(BASE_WEB_URL,baseWebUrl);
        editor.putString(PHP_PATH_URL,PhpPathUrl);
        editor.putString(REPORTS_URL,reportsUrl);
        editor.putString(SLIDES_URL,SlidesUrl);
        editor.putString(LOGO_URL,logoUrl);
        editor.putBoolean(SETTING_STATE,settingState);
        editor.apply();
    }

    public static String getBaseUrl(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(BASE_URL,"");
    }

    public static String getLicenseKey(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(LICENSE_KEY,"");
    }

    public static String getBaseWebUrl(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(BASE_WEB_URL,"");
    }

    public static String getPhpPathUrl(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(PHP_PATH_URL,"");
    }

    public static String getReportsURl(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(REPORTS_URL,"");
    }

    public static String getSlideUrl(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(SLIDES_URL,"");
    }

    public static String getLogoUrl(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(LOGO_URL,"");
    }

    public static void saveSettingState(Context context,boolean state){
        sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(SETTING_STATE,state).apply();
    }

    public static boolean getSettingState(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getBoolean(SETTING_STATE,false);
    }

    public static void saveLoginState(Context context,boolean state){
        sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN_STATE,state).apply();
    }
    public static boolean getLoginState(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getBoolean(LOGIN_STATE,false);
    }


    public static void saveDeviceId(Context context,String deviceId){
        sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(DEVICE_ID,deviceId).apply();
    }

    public static String getDeviceId(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(DEVICE_ID,"");
    }

    public static void saveFcmToken(Context context,String token){
        sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(FCM_TOKEN,token).apply();
    }

    public static String getFcmToken(Context context){
        return context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getString(FCM_TOKEN,"");
    }




}
