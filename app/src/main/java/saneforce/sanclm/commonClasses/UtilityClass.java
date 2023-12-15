package saneforce.sanclm.commonClasses;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Locale;

import saneforce.sanclm.storage.SharedPref;


public class UtilityClass {
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    try {
                        Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
                        int returnVal = p1.waitFor();
                        return (returnVal == 0);
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                    return false;
                }
        }
        return false;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = ( InputMethodManager ) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setLanguage(Context context){
        String language = SharedPref.getSelectedLanguage(context);
        Locale locale = new Locale(language);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
    }


}
