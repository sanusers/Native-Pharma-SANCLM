package saneforce.sanzen.commonClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.utility.location.LocationEvents;
import saneforce.sanzen.utility.location.LocationFinder;
import saneforce.sanzen.storage.SharedPref;


public class CommonAlertBox {

    private static final String TAG = "LocationStatus";
    public static AlertDialog dialog;

    public static void CheckLocationStatus(Activity activity, GPSTrack gpsTrack) {
        if(SharedPref.getGeoChk(activity).equalsIgnoreCase("0")){
            if (isMockLocation(activity) || (gpsTrack != null && gpsTrack.isFakeLocation())) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setCancelable(false);
                LayoutInflater inflater = activity.getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.fake_gps_alert_box, null);
                Button btnOk = alertLayout.findViewById(R.id.BtnClose);
                alert.setView(alertLayout);
                dialog = alert.create();
                dialog.show();
                btnOk.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        activity.finishAffinity();
                        System.exit(0);
                        dialog.dismiss();
                    }
                });
            } else {
                getlocation_status(activity);
            }

        }
    }

    private static boolean isMockLocation(Context context) {
        boolean NmockLocationsEnabled = false;
//        if (Build.MANUFACTURER.equalsIgnoreCase("LENOVO")) {
//            NmockLocationsEnabled = areThereMockPermissionApps(context);
//        }
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
                            throw new Exception("Fake Location App : " + applicationInfo.packageName);
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ", e.getMessage());
            } catch (Exception e) {
                Log.e("Fake Location", "areThereMockPermissionApps: " + applicationInfo.packageName);
                e.printStackTrace();
            }
        }
        if (count > 0) return true;
        return false;
    }

    public static boolean areMockLocationsEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
    }

//    public static void ShowCombinedWishesAlert(Activity activity, String birthdayMsg, String anniversaryMsg) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
//        alert.setCancelable(false);
//
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View alertLayout = inflater.inflate(R.layout.wishes_box, null);
//
//        // 🎉 Title
//        TextView tvTitle = alertLayout.findViewById(R.id.tvTitle);
//        tvTitle.setText("Today's Wishes !!!");
//
//        LinearLayout contentRow = alertLayout.findViewById(R.id.contentRow);
//
//        TextView tvBirthday = alertLayout.findViewById(R.id.imgwishes_birthday);
//        TextView tvBirthdayTitle = alertLayout.findViewById(R.id.tvBirthdayTitle);
//        ImageView imgBirthday = alertLayout.findViewById(R.id.imgBirthday);
//
//        TextView tvAnniversary = alertLayout.findViewById(R.id.imgwishes_anniversary);
//        TextView tvAnniversaryTitle = alertLayout.findViewById(R.id.tvAnniversaryTitle);
//        ImageView imgAnniversary = alertLayout.findViewById(R.id.imgAnniversary);
//
//        Button btnOk = alertLayout.findViewById(R.id.btnOk);
//
//        // 🎂 Birthday Section
//        if (birthdayMsg != null && !birthdayMsg.trim().isEmpty()) {
//            tvBirthday.setText(birthdayMsg);
//            tvBirthday.setVisibility(View.VISIBLE);
//            tvBirthdayTitle.setVisibility(View.VISIBLE);
//            imgBirthday.setVisibility(View.VISIBLE);
//        } else {
//            tvBirthday.setVisibility(View.GONE);
//            tvBirthdayTitle.setVisibility(View.GONE);
//            imgBirthday.setVisibility(View.GONE);
//        }
//
//        // 💐 Anniversary Section
//        if (anniversaryMsg != null && !anniversaryMsg.trim().isEmpty()) {
//            tvAnniversary.setText(anniversaryMsg);
//            tvAnniversary.setVisibility(View.VISIBLE);
//            tvAnniversaryTitle.setVisibility(View.VISIBLE);
//            imgAnniversary.setVisibility(View.VISIBLE);
//        } else {
//            tvAnniversary.setVisibility(View.GONE);
//            tvAnniversaryTitle.setVisibility(View.GONE);
//            imgAnniversary.setVisibility(View.GONE);
//        }
//
//        // 🧩 Hide divider if one section missing
//        if ((birthdayMsg == null || birthdayMsg.trim().isEmpty()) ||
//                (anniversaryMsg == null || anniversaryMsg.trim().isEmpty())) {
//            View dividerView = contentRow.getChildAt(1); // middle divider
//            if (dividerView != null) dividerView.setVisibility(View.GONE);
//        }
//
//        alert.setView(alertLayout);
//        AlertDialog dialog = alert.create();
//        dialog.show();
//
//
//
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setLayout(
//                    (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.60),
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//            );
//        }
//
//        btnOk.setOnClickListener(v -> dialog.dismiss());
//    }

    public static void ShowCombinedWishesAlert(Activity activity, String birthdayMsg, String anniversaryMsg) {
        Log.d("CombinedWishes", "Creating combined wishes alert...");

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);

        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.wishes_box, null);

        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();

        TextView tvTitle = alertLayout.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.todays_wishes);

        LinearLayout contentRow = alertLayout.findViewById(R.id.contentRow);
        LinearLayout layoutBirthday = alertLayout.findViewById(R.id.layoutBirthday);
        LinearLayout layoutAnniversary = alertLayout.findViewById(R.id.layoutAnniversary);


        // Birthday section
        TextView tvBirthday = alertLayout.findViewById(R.id.imgwishes_birthday);
        TextView tvBirthdayTitle = alertLayout.findViewById(R.id.tvBirthdayTitle);
        ImageView imgBirthday = alertLayout.findViewById(R.id.imgBirthday);

        // Anniversary section
        TextView tvAnniversary = alertLayout.findViewById(R.id.imgwishes_anniversary);
        TextView tvAnniversaryTitle = alertLayout.findViewById(R.id.tvAnniversaryTitle);
        ImageView imgAnniversary = alertLayout.findViewById(R.id.imgAnniversary);

        View dividerView = alertLayout.findViewById(R.id.dividerView);
        Button btnOk = alertLayout.findViewById(R.id.btnOk);

        boolean hasBirthday = birthdayMsg != null && !birthdayMsg.trim().isEmpty();
        boolean hasAnniversary = anniversaryMsg != null && !anniversaryMsg.trim().isEmpty();

        // 🎂 Birthday
        if (hasBirthday) {
            tvBirthday.setText(birthdayMsg);
            tvBirthday.setVisibility(View.VISIBLE);
            tvBirthdayTitle.setVisibility(View.VISIBLE);
            imgBirthday.setVisibility(View.VISIBLE);
        } else {
            tvBirthday.setVisibility(View.GONE);
            tvBirthdayTitle.setVisibility(View.GONE);
            imgBirthday.setVisibility(View.GONE);
        }

        // 💐 Anniversary
        if (hasAnniversary) {
            tvAnniversary.setText(anniversaryMsg);
            tvAnniversary.setVisibility(View.VISIBLE);
            tvAnniversaryTitle.setVisibility(View.VISIBLE);
            imgAnniversary.setVisibility(View.VISIBLE);
        } else {
            tvAnniversary.setVisibility(View.GONE);
            tvAnniversaryTitle.setVisibility(View.GONE);
            imgAnniversary.setVisibility(View.GONE);
        }

        // 🔹 Divider logic + layout alignment
        if (dividerView != null) {
            if (hasBirthday && hasAnniversary) {
                dividerView.setVisibility(View.VISIBLE);
            } else {
                dividerView.setVisibility(View.GONE);
            }
        }
// 🔹 Center alignment when only one section exists
        if (hasBirthday && !hasAnniversary) {
            layoutBirthday.setVisibility(View.VISIBLE);
            layoutAnniversary.setVisibility(View.GONE);

            // Parent row
//            contentRow.setGravity(Gravity.CENTER_HORIZONTAL);

            // Remove weights → else center won't work
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutAnniversary.setLayoutParams(lp);

            ViewGroup.LayoutParams params = tvBirthday.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            tvBirthday.setLayoutParams(params);

            tvBirthday.post(() -> {
                float textSizePx = tvBirthday.getTextSize();
                Paint paint = new Paint();
                paint.setTextSize(textSizePx);
                float textWidth = paint.measureText(tvBirthday.getText().toString());
                int padding = tvBirthday.getPaddingLeft() + tvBirthday.getPaddingRight();
                int finalWidth = (int) (textWidth + padding);
                if (finalWidth < 400) finalWidth = 400;

                Window window = dialog.getWindow();
                if (window != null) {
                    int windowWidth = window.getDecorView().getMeasuredWidth();
                    window.setLayout(Math.min(windowWidth, finalWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                }
            });
//            tvBirthday.setPadding((int) activity.getResources().getDimension(R.dimen._60sdp), 0 , (int) activity.getResources().getDimension(R.dimen._60sdp), 0);
        } else if (!hasBirthday && hasAnniversary) {
            layoutAnniversary.setVisibility(View.VISIBLE);
            layoutBirthday.setVisibility(View.GONE);

//            contentRow.setGravity(Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutBirthday.setLayoutParams(lp);

            ViewGroup.LayoutParams params = tvAnniversary.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            tvAnniversary.setLayoutParams(params);

            tvAnniversary.post(() -> {
                float textSizePx = tvAnniversary.getTextSize();
                Paint paint = new Paint();
                paint.setTextSize(textSizePx);
                float textWidth = paint.measureText(tvAnniversary.getText().toString());
                int padding = tvAnniversary.getPaddingLeft() + tvAnniversary.getPaddingRight();
                int finalWidth = (int) (textWidth + padding);
                if (finalWidth < 400) finalWidth = 400;

                Window window = dialog.getWindow();
                if (window != null) {
                    int windowWidth = window.getDecorView().getMeasuredWidth();
                    window.setLayout(Math.min(windowWidth, finalWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                }
            });
//            tvAnniversary.setPadding((int) activity.getResources().getDimension(R.dimen._60sdp), 0 , (int) activity.getResources().getDimension(R.dimen._60sdp), 0);
        } else {
            layoutBirthday.setVisibility(View.VISIBLE);
            layoutAnniversary.setVisibility(View.VISIBLE);

            // Restore 50–50 width
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

            layoutBirthday.setLayoutParams(lp);
            layoutAnniversary.setLayoutParams(lp);

            contentRow.setGravity(Gravity.TOP);
        }

        // 🔹 If only one section — center it vertically
//        if (hasBirthday && !hasAnniversary) {
//            contentRow.setOrientation(LinearLayout.VERTICAL);
//            contentRow.setGravity(Gravity.CENTER_HORIZONTAL);
//        } else if (!hasBirthday && hasAnniversary) {
//            contentRow.setOrientation(LinearLayout.VERTICAL);
//            contentRow.setGravity(Gravity.CENTER_HORIZONTAL);
//        } else {
//            contentRow.setOrientation(LinearLayout.HORIZONTAL);
//            contentRow.setGravity(Gravity.CENTER_VERTICAL);
//        }

//        if (dialog.getWindow() != null) {
//            if ((hasBirthday && !hasAnniversary) || (!hasBirthday && hasAnniversary)) {
//                dialog.getWindow().setLayout(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
//            }
//            else {
//                dialog.getWindow().setLayout(
//                        (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.60),
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
//            }
//        }
        dialog.setOnShowListener(d -> {
            Window window = dialog.getWindow();
            if (window != null) {
                if ((hasBirthday && !hasAnniversary) || (!hasBirthday && hasAnniversary)) {
                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    window.setLayout((int) (activity.getResources().getDisplayMetrics().widthPixels * 0.70), ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });
        btnOk.setOnClickListener(v -> {
            v.setEnabled(false);
            Log.d("CombinedWishes", "OK clicked, dismissing...");
            dialog.dismiss();
        });
    }

//public static void ShowCombinedWishesAlert(Activity activity, String birthdayMsg, String anniversaryMsg) {
//    Log.d("CombinedWishes", "Creating combined wishes alert...");
//
//    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
//    alert.setCancelable(false);
//
//    LayoutInflater inflater = activity.getLayoutInflater();
//    View alertLayout = inflater.inflate(R.layout.wishes_box, null);
//
//    TextView tvTitle = alertLayout.findViewById(R.id.tvTitle);
//    tvTitle.setText("Today's Wishes !!!");
//
//    LinearLayout contentRow = alertLayout.findViewById(R.id.contentRow);
//
//    TextView tvBirthday = alertLayout.findViewById(R.id.imgwishes_birthday);
//    TextView tvBirthdayTitle = alertLayout.findViewById(R.id.tvBirthdayTitle);
//    ImageView imgBirthday = alertLayout.findViewById(R.id.imgBirthday);
//
//    TextView tvAnniversary = alertLayout.findViewById(R.id.imgwishes_anniversary);
//    TextView tvAnniversaryTitle = alertLayout.findViewById(R.id.tvAnniversaryTitle);
//    ImageView imgAnniversary = alertLayout.findViewById(R.id.imgAnniversary);
//
//    Button btnOk = alertLayout.findViewById(R.id.btnOk);
//
//    // Birthday section
//    if (birthdayMsg != null && !birthdayMsg.trim().isEmpty()) {
//        tvBirthday.setText(birthdayMsg);
//        tvBirthday.setVisibility(View.VISIBLE);
//        tvBirthdayTitle.setVisibility(View.VISIBLE);
//        imgBirthday.setVisibility(View.VISIBLE);
//    } else {
//        tvBirthday.setVisibility(View.GONE);
//        tvBirthdayTitle.setVisibility(View.GONE);
//        imgBirthday.setVisibility(View.GONE);
//    }
//
//    // Anniversary section
//    if (anniversaryMsg != null && !anniversaryMsg.trim().isEmpty()) {
//        tvAnniversary.setText(anniversaryMsg);
//        tvAnniversary.setVisibility(View.VISIBLE);
//        tvAnniversaryTitle.setVisibility(View.VISIBLE);
//        imgAnniversary.setVisibility(View.VISIBLE);
//    } else {
//        tvAnniversary.setVisibility(View.GONE);
//        tvAnniversaryTitle.setVisibility(View.GONE);
//        imgAnniversary.setVisibility(View.GONE);
//    }
//
//    // Hide divider if one missing
//    if ((birthdayMsg == null || birthdayMsg.trim().isEmpty()) ||
//            (anniversaryMsg == null || anniversaryMsg.trim().isEmpty())) {
//        View dividerView = contentRow.getChildAt(1);
//        if (dividerView != null) dividerView.setVisibility(View.GONE);
//    }
//
//    alert.setView(alertLayout);
//    AlertDialog dialog = alert.create();
//    dialog.show();
//
//    if (dialog.getWindow() != null) {
//        dialog.getWindow().setLayout(
//                (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.60),
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//    }
//
//    btnOk.setOnClickListener(v -> {
//        v.setEnabled(false); // prevents double tap
//        Log.d("CombinedWishes", "OK clicked, dismissing...");
//        dialog.dismiss();
//    });
//}


//    public static void BirthdayWishAlert (Activity activity, String message) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
//        alert.setCancelable(false);
//
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View alertLayout = inflater.inflate(R.layout.wishes_box, null);
//
//        Button btnOk = alertLayout.findViewById(R.id.btn_OK);
//        TextView alertMsg = alertLayout.findViewById(R.id.imgwishes_birthday);
//        alertMsg.setText(message);
//
//        alert.setView(alertLayout);
//        AlertDialog dialog = alert.create();
//        dialog.show();
//
//        btnOk.setOnClickListener(view -> {
//            // Optional: open BirthdayListActivity or just dismiss
////            Intent intent = new Intent(activity, birthdayAnniversary_viewscreen.class);
////            activity.startActivity(intent);
//            dialog.dismiss();
//        });
//    }

//    public static void AnniversaryWishAlert (Activity activity, String message) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
//        alert.setCancelable(false);
//
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View alertLayout = inflater.inflate(R.layout.wishes_popup, null);
//
//        Button btnOk2 = alertLayout.findViewById(R.id.btn_OK2);
//        TextView alertMsg = alertLayout.findViewById(R.id.imgwishes_anniversary);
//        alertMsg.setText(message);
//
//        alert.setView(alertLayout);
//        AlertDialog dialog = alert.create();
//        dialog.show();
//
//        btnOk2.setOnClickListener(view -> {
//            // Optional: open BirthdayListActivity or just dismiss
////            Intent intent = new Intent(activity, birthdayAnniversary_viewscreen.class);
////            activity.startActivity(intent);
//            dialog.dismiss();
//        });
//    }
    public static void TpAlert(Activity activity) {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.warning_alert, null);
        Button btn_yes = alertLayout.findViewById(R.id.btnYes);
        Button btn_no = alertLayout.findViewById(R.id.btnNo);
        TextView alerttext = alertLayout.findViewById(R.id.ed_alert_msg);
        alerttext.setText(R.string.tp_alert_content);
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
        btn_yes.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TourPlanActivity.class);
            activity.startActivity(intent);
            dialog.dismiss();
        });
        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
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
        alerttext.setText(R.string.clear_all_approvals_content);
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
        btn_yes.setOnClickListener(view -> {
            SharedPref.setApprovalsCounts(activity, "false");
            Intent intent = new Intent(activity, ApprovalsActivity.class);
            activity.startActivity(intent);
            dialog.dismiss();
        });
        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
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
                        try {
                            boolean isMock = false;
                            isMock = location.isFromMockProvider();
                            if(isMock) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                alert.setCancelable(false);
                                LayoutInflater inflater = activity.getLayoutInflater();
                                View alertLayout = inflater.inflate(R.layout.fake_gps_alert_box, null);
                                Button btnOk = alertLayout.findViewById(R.id.BtnClose);
                                alert.setView(alertLayout);
                                AlertDialog dialog = alert.create();
                                dialog.show();
                                btnOk.setOnClickListener(new SafeClickListener() {
                                    @Override
                                    public void onSafeClick(View view) {
                                        activity.finishAffinity();
                                        System.exit(0);
                                        dialog.dismiss();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("Location CAB", "OnLocationReceived: Location null" );
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return locate;
    }

    public static void permissionChangeAlert(Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.warning_alert, null);
        Button btn_yes = alertLayout.findViewById(R.id.btnYes);
        Button btn_no = alertLayout.findViewById(R.id.btnNo);
        btn_no.setVisibility(View.GONE);
        btn_yes.setText(activity.getResources().getString(R.string.continuee));
        TextView alertText = alertLayout.findViewById(R.id.ed_alert_msg);
        alertText.setText(R.string.permission_change_content);
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Intent intent = new Intent(activity, HomeDashBoard.class);
                activity.startActivity(intent);
                activity.finishAffinity();
                dialog.dismiss();
            }
        });
    }

    public static void outboxDataAvailableAlert(Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.warning_alert, null);
        Button btn_yes = alertLayout.findViewById(R.id.btnYes);
        Button btn_no = alertLayout.findViewById(R.id.btnNo);
        TextView alertText = alertLayout.findViewById(R.id.ed_alert_msg);
        btn_no.setVisibility(View.GONE);
        btn_yes.setText(activity.getResources().getString(R.string.continuee));
        alertText.setText(R.string.outboxAlertContent);
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                HomeDashBoard.binding.viewPager.setCurrentItem(2);
                dialog.dismiss();
            }
        });
    }

}