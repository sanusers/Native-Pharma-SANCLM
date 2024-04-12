package saneforce.santrip.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import saneforce.santrip.activity.homeScreen.fragment.OutboxFragment;


public class NetworkChangeReceiver extends BroadcastReceiver {
    public static final String NOT_CONNECT = "NOT_CONNECT";
    Context mContext;
    String isNetworkConnected = "2";


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String status = NetworkUtil.getConnectivityStatusString(context);
        Log.e("Receiver ", status);
        if (status.equals(NOT_CONNECT)) {
            Log.e("Receiver ", "not connection");// your code when internet lost
            isNetworkConnected = "1";
        } else {
            if (isNetworkConnected.equalsIgnoreCase("1")) {
                isNetworkConnected = "3";
            }
            Log.e("Receiver ", "connected to internet");//your code when internet connection come back

        }

        if (isNetworkConnected.equalsIgnoreCase("3")) {
            OutboxFragment.NetworkConnectCallHomeDashBoard(status);
            isNetworkConnected = "0";
        }

        //HomeDashBoard.NetworkConnectCallHomeDashBoard(status);

    }
}
