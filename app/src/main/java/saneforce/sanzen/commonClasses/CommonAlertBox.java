package saneforce.sanzen.commonClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.utility.location.LocationEvents;
import saneforce.sanzen.utility.location.LocationFinder;
import saneforce.sanzen.storage.SharedPref;


public class CommonAlertBox {

    private static final String TAG = "LocationStatus";

    public static void CheckLocationStatus(Activity activity) {
        if(SharedPref.getGeoChk(activity).equalsIgnoreCase("0")){
            if (isMockLocation(activity)) {
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
            } else {
                getlocation_status(activity);
            }

        }
    }

    private static boolean isMockLocation(Context context) {
        boolean NmockLocationsEnabled = false;
        if (Build.MANUFACTURER.equalsIgnoreCase("LENOVO")) {
            NmockLocationsEnabled = areThereMockPermissionApps(context);
        }
        boolean mockLocationsEnabled = areMockLocationsEnabled(context);
        return mockLocationsEnabled || NmockLocationsEnabled;

    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i].equals("android.permission.ACCESS_MOCK_LOCATION") && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ", e.getMessage());
            }
        }
        if (count > 0) return true;
        return false;
    }

    public static boolean areMockLocationsEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
    }

    public static void TpAlert(Activity activity) {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.warning_alert, null);
        Button btn_yes = alertLayout.findViewById(R.id.btnYes);
        Button btn_no = alertLayout.findViewById(R.id.btnNo);
        TextView alerttext = alertLayout.findViewById(R.id.ed_alert_msg);
        alerttext.setText(" Tour planning range has exceeded. Please prepare your tour plan....");
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
        btn_yes.setOnClickListener(v -> {
            Intent intent = new Intent(activity, TourPlanActivity.class);
            activity.startActivity(intent);
            dialog.dismiss();
        });
        btn_no.setOnClickListener(view -> {
            dialog.dismiss();
        });

    }

    public static void ApprovalAlert(Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.warning_alert, null);
        Button btn_yes = alertLayout.findViewById(R.id.btnYes);
        Button btn_no = alertLayout.findViewById(R.id.btnNo);
        TextView alerttext = alertLayout.findViewById(R.id.ed_alert_msg);
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
    }


    public static String getlocation_status(Activity activity) {
        String locate = "";
        try {
            new LocationFinder(activity, new LocationEvents() {
                Location mlocation;

                @Override
                public void OnLocationRecived(Location location) {
                    mlocation = location;
                    if (location != null) {
                        Log.d("Location1233", location.getLatitude() + " : " + location.getLongitude());
                    }
                    try {
                        boolean isMock = false;
                        isMock = location.isFromMockProvider();
                        if (isMock) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return locate;
    }


}