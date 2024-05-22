package saneforce.sanzen.commonClasses;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;

import saneforce.sanzen.storage.SharedPref;


public class UtilityClass {
    public static boolean isOnline(Context context) {
        Log.v("nettt", "--0000--");
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            Log.v("nettt", "--1111--");
            Log.v("nettt", "--2222--");
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                Log.v("nettt", "--545445--" + networkInfo.getType() + "-----" + NetworkInfo.State.CONNECTED + "----" + networkInfo.getState());
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        Log.v("nettt", "--3333--");
                        Process p1 = null;
                        int returnVal = 5;
                        InetAddress ipAddr = null;
                        try {
                            //   ipAddr = InetAddress.getByName("www.google.com");
                            p1 = Runtime.getRuntime().exec("ping -c 1 -W 2 www.google.com");
                            returnVal = p1.waitFor();
                        } catch (IOException e) {
                            Log.v("nettt", "--eeee--" + e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        Log.v("nettt", "--4545454--" + returnVal);
                        return (returnVal == 0);
                    }

                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.v("nettt", "--99999--");
                        Process p1 = null;
                        int returnVal = 5;
                        InetAddress ipAddr = null;
                        try {
                            //   ipAddr = InetAddress.getByName("www.google.com");
                            p1 = Runtime.getRuntime().exec("ping -c 1 -W 2 www.google.com");
                            returnVal = p1.waitFor();
                        } catch (IOException e) {
                            Log.v("nettt", "--eeee--" + e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        Log.v("nettt", "--4545454--" + returnVal);
                        return (returnVal == 1);
                    }


                }
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setLanguage(Context context) {
        String language = SharedPref.getSelectedLanguage(context);
        Locale locale = new Locale(language);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
