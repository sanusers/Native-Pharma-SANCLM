package saneforce.sanzen.commonClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.storage.SharedPref;


public class CommonAlertBox {
    public  static  void CheckLocationStatus(Activity activity){

        LocationManager  locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {boolean isMock = isMockLocation(activity.getApplicationContext(),location);
                    if (isMock) {
                        locationManager.removeUpdates(this);
                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                        alert.setCancelable(false);
                        LayoutInflater inflater = activity.getLayoutInflater();
                        View alertLayout = inflater.inflate(R.layout.fake_gps_alert_box, null);
                        Button btnOk = alertLayout.findViewById(R.id.BtnClose);
                        alert.setView(alertLayout);
                        AlertDialog dialog = alert.create();
                        dialog.show();
                        btnOk.setOnClickListener(v -> {
                            activity.finishAffinity();
                            System.exit(0);
                            dialog.dismiss();
                        });

                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }


    }

    private static boolean isMockLocation(Context context, Location location) {
        if (location == null) {
            return false;
        }
        if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0) {
            return true;
        }
        if (location.isFromMockProvider()) {
            return true;
        }

        return false;
    }

 public  static  void TpAlert(Activity activity){

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.warning_alert, null);
        Button btn_yes=alertLayout.findViewById(R.id.btnYes);
        Button btn_no=alertLayout.findViewById(R.id.btnNo);
        TextView alerttext=alertLayout.findViewById(R.id.ed_alert_msg);
        alerttext.setText("The tour planning date has exceeded. Please prepare your tour plan....");
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
        btn_yes.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ApprovalsActivity.class);
            activity.startActivity(intent);
            dialog.dismiss();
        });
        btn_no.setOnClickListener(view -> {
            dialog.dismiss();
        });
   //  dialog.getWindow().setLayout((int) activity.getResources().getDimension(R.dimen._200sdp), (int) activity.getResources().getDimension(R.dimen._100sdp));
    }
    public  static  void ApprovalAlert(Activity activity){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.warning_alert, null);
        Button btn_yes=alertLayout.findViewById(R.id.btnYes);
        Button btn_no=alertLayout.findViewById(R.id.btnNo);
        TextView alerttext=alertLayout.findViewById(R.id.ed_alert_msg);
        alerttext.setText("Team approvals are still pending. Please clear all approvals...");
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
        btn_yes.setOnClickListener(v -> {
            SharedPref.setApprovalsCounts(activity, "false");
            Intent intent = new Intent(activity, ApprovalsActivity.class);
            activity.startActivity(intent);
            dialog.dismiss();
        });
        btn_no.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.getWindow().setLayout((int) activity.getResources().getDimension(R.dimen._150sdp), (int) activity.getResources().getDimension(R.dimen._100sdp));


    }










}
