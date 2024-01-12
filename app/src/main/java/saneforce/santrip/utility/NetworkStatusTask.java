package saneforce.santrip.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class NetworkStatusTask extends AsyncTask<Void,Void,Boolean> {

    Context context;
    NetworkStatusInterface networkStatusInterface;

    public NetworkStatusTask(Context context, NetworkStatusInterface networkStatusInterface) {
        this.context = context;
        this.networkStatusInterface = networkStatusInterface;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
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

    @Override
    protected void onPostExecute(Boolean aBoolean) {
//        super.onPostExecute(aBoolean);
        if(networkStatusInterface != null){
            networkStatusInterface.isNetworkAvailable(aBoolean);
        }
    }

    public interface NetworkStatusInterface{
        public void isNetworkAvailable(Boolean status);
    }
}
