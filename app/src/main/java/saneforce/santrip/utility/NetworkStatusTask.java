package saneforce.santrip.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkStatusTask extends AsyncTask<Void, Void, Boolean> {

    Context context;
    NetworkStatusInterface networkStatusInterface;

    public NetworkStatusTask(Context context, NetworkStatusInterface networkStatusInterface) {
        this.context = context;
        this.networkStatusInterface = networkStatusInterface;
    }

/*
    @Override
    protected Boolean doInBackground(Void... voids) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    try {
                        Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 -W 1 www.google.com");
                        int returnVal = p1.waitFor();
                        return (returnVal == 0);
                    } catch (Exception ignored) {
                    }
                    return false;
                }
        }
        return false;
    }
*/


    @Override
    protected Boolean doInBackground(Void... voids) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        Process p1 = null;
                        int returnVal = 5;
                        try {
                            p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com");
                            returnVal = p1.waitFor();
                        } catch (IOException ignored) {
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return (returnVal == 0);
                    }
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        boolean success = false;
                        try {
                            URL url = new URL("https://google.com");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setConnectTimeout(500);
                       /*     int networkType = networkInfo.getSubtype();
                            switch (networkType) {
                                case TelephonyManager.NETWORK_TYPE_HSPAP:
                                    Log.v("ntttt", "3g");
                                    connection.setConnectTimeout(4000);
                                    break;
                                case TelephonyManager.NETWORK_TYPE_LTE:
                                    Log.v("ntttt", "4g");
                                    connection.setConnectTimeout(3000);
                                    break;
                                case TelephonyManager.NETWORK_TYPE_EDGE:
                                    Log.v("ntttt", "2g");
                                    connection.setConnectTimeout(5000);
                                    break;
                                default:
                                    Log.v("ntttt", "default");
                                    connection.setConnectTimeout(3000);
                                    break;
                            }*/

                            connection.connect();
                            success = connection.getResponseCode() == 200;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return success;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
//        super.onPostExecute(aBoolean);
        if (networkStatusInterface != null) {
            networkStatusInterface.isNetworkAvailable(aBoolean);
        }
    }

    public interface NetworkStatusInterface {
        void isNetworkAvailable(Boolean status);
    }
}
