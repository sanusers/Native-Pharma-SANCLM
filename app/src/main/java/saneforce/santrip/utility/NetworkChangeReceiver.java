package saneforce.santrip.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import saneforce.santrip.activity.homeScreen.HomeDashBoard;


public class NetworkChangeReceiver extends BroadcastReceiver
{
    public static final String NOT_CONNECT = "NOT_CONNECT";
    Context mContext;


    @Override
    public void onReceive(Context context, Intent intent)
    {
        mContext = context;

        String status = NetworkUtil.getConnectivityStatusString(context);
        Log.e("Receiver ", "" + status);
        if (status.equals(NOT_CONNECT))
        {
            Log.e("Receiver ", "not connection");// your code when internet lost
        }
        else
        {
            Log.e("Receiver ", "connected to internet");//your code when internet connection come back

        }
        HomeDashBoard.NetworkConnectCallHomeDashBoard(status);
    }
}
