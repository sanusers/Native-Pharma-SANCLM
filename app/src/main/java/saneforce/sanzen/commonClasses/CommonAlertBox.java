package saneforce.sanzen.commonClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.reports.missedReport.OnSwipeTouchListener;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.utility.location.LocationEvents;
import saneforce.sanzen.utility.location.LocationFinder;
import saneforce.sanzen.storage.SharedPref;


public class CommonAlertBox {

    private static final String TAG = "LocationStatus";
    public static AlertDialog dialog;
    // 1. INGAE THAAN PODANUM (Top of the class)
    public static List<String> tempChmVList, tempChmNVList;
    public static Map<String, String> tempChmNameMap, tempChmClusterMap, tempChmCatMap;
    public static String tempChmVRatio, tempChmNVRatio;
    public static void setChemistData(List<String> v, List<String> nv, Map<String, String> n,
                                      Map<String, String> cl, Map<String, String> cat,
                                      String vr, String nvr) {
        tempChmVList = v;
        tempChmNVList = nv;
        tempChmNameMap = n;
        tempChmClusterMap = cl;
        tempChmCatMap = cat;
        tempChmVRatio = vr;
        tempChmNVRatio = nvr;
    }

    public static void CheckLocationStatus(Activity activity, GPSTrack gpsTrack) {
        if (SharedPref.getGeoChk(activity).equalsIgnoreCase("0")) {
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

    /// /            Intent intent = new Intent(activity, birthdayAnniversary_viewscreen.class);
    /// /            activity.startActivity(intent);
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

    public static void DoctorPlanPopup(Activity activity, CharSequence message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);

        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.popup_doctor_count, null);

        TextView heading = alertLayout.findViewById(R.id.heading);
        TextView messagePopup = alertLayout.findViewById(R.id.messagePopup);
        Button okButton = alertLayout.findViewById(R.id.okButton);
        heading.setText(" Today's Planned " + SharedPref.getDrCap(activity));
        messagePopup.setText(message);

        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        okButton.setOnClickListener(view -> dialog.dismiss());
    }
    public static void DoctorPlanPopup2(Activity activity,
                                     boolean isDoctor,
                                     List<String> vList,
                                     List<String> nvList,
                                     Map<String, String> nameMap,
                                     Map<String, String> clusterMap,
                                     Map<String, String> categoryMap,
                                     String vRatio,
                                     String nvRatio) {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.popup_doctor_count_time, null);

        TextView heading = layout.findViewById(R.id.heading);
        //ImageView imgIcon = layout.findViewById(R.id.imgdr);

        TextView clusterHead = layout.findViewById(R.id.tv_Cluster);
        TextView name = layout.findViewById(R.id.tv_Name);
        TextView clusterHead2 = layout.findViewById(R.id.tv_Cluster2);
        TextView name2 = layout.findViewById(R.id.tv_Name2);

        LinearLayout vContainer = layout.findViewById(R.id.visitedContainer);
        LinearLayout nvContainer = layout.findViewById(R.id.visitedContainer2);

        TextView tvVCount = layout.findViewById(R.id.tv_visited_ratio);
        TextView tvNVCount = layout.findViewById(R.id.tv_visited_ratio2);

        Button okButton2 = layout.findViewById(R.id.okButton2);

        TabLayout tabLayout = layout.findViewById(R.id.tabLayout);

        // Headers
        clusterHead.setText(SharedPref.getClusterCap(activity));
        clusterHead2.setText(SharedPref.getClusterCap(activity));

        // Tabs
//        tabLayout.addTab(tabLayout.newTab().setText("Doctor"));
//        tabLayout.addTab(tabLayout.newTab().setText("Chemist"));
        tabLayout.addTab(tabLayout.newTab().setText("DOCTOR"));
        tabLayout.addTab(tabLayout.newTab().setText("CHEMIST"));

        TabLayout.Tab doctorTab = tabLayout.getTabAt(0);
        TabLayout.Tab chemistTab = tabLayout.getTabAt(1);

        if (doctorTab != null) {
            TextView tv = new TextView(activity);
            tv.setText(SharedPref.getDrCap(activity));
            //tv.setText("DOCTOR");
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.BLACK);
            tv.setTypeface(null, Typeface.BOLD);
            // பட்டன் ஓரத்திற்கு ஒட்டாமல் இருக்க ஒரு சிறிய Padding
            tv.setPadding(100,0,100,0);
            Drawable icon = ContextCompat.getDrawable(activity, R.drawable.doctor_img); // declare
            icon.setBounds(0, 0, 35, 35); // small size

            tv.setCompoundDrawables(icon, null, null, null);
            tv.setCompoundDrawablePadding(10);
            doctorTab.setCustomView(tv);
        }

        if (chemistTab != null) {
            TextView tv = new TextView(activity);
            tv.setText(SharedPref.getChmCap(activity));
          //  tv.setText("CHEMIST");
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.BLACK);
            tv.setTypeface(null, Typeface.BOLD);
            // பட்டன் ஓரத்திற்கு ஒட்டாமல் இருக்க ஒரு சிறிய Padding
            tv.setPadding(100,0,100,0);
            Drawable icon = ContextCompat.getDrawable(activity, R.drawable.map_chemist_img); // declare
            icon.setBounds(0, 0, 35, 35); // small size

            tv.setCompoundDrawables(icon, null, null, null);
            tv.setCompoundDrawablePadding(10);
            chemistTab.setCustomView(tv);
        }
        // Default Doctor Load
       // heading.setText(" Today's " + SharedPref.getDrCap(activity) + " Plan ");
       // imgIcon.setImageResource(R.drawable.doctor_img);

        name.setText(SharedPref.getDrCap(activity));
        name2.setText(SharedPref.getDrCap(activity));

        tvVCount.setText(vRatio);
        tvNVCount.setText(nvRatio);

        addRows(activity, vContainer, vList, nameMap, clusterMap, categoryMap);
        addRows(activity, nvContainer, nvList, nameMap, clusterMap, categoryMap);

        // Tab click logic
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {

                    // Doctor
                  //  heading.setText(" Today's " + SharedPref.getDrCap(activity) + " Plan ");
                   // imgIcon.setImageResource(R.drawable.doctor_img);

                    name.setText(SharedPref.getDrCap(activity));
                    name2.setText(SharedPref.getDrCap(activity));

                    tvVCount.setText(vRatio);
                    tvNVCount.setText(nvRatio);

                    addRows(activity, vContainer, vList, nameMap, clusterMap, categoryMap);
                    addRows(activity, nvContainer, nvList, nameMap, clusterMap, categoryMap);

                } else {

                    // Chemist
                   // heading.setText(" Today's Chemist Plan ");
                   // imgIcon.setImageResource(R.drawable.map_chemist_img);

                    name.setText(SharedPref.getChmCap(activity));
                    name2.setText(SharedPref.getChmCap(activity));

                    tvVCount.setText(tempChmVRatio);
                    tvNVCount.setText(tempChmNVRatio);

                    addRows(activity, vContainer, tempChmVList, tempChmNameMap, tempChmClusterMap, tempChmCatMap);
                    addRows(activity, nvContainer, tempChmNVList, tempChmNameMap, tempChmClusterMap, tempChmCatMap);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        alert.setView(layout);
        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);

        dialog.show();

        okButton2.setOnClickListener(v -> dialog.dismiss());

        Window window = dialog.getWindow();
        if (window != null) {

            int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.65);

            int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.55);

            window.setLayout(width, height);
        }
    }

    private static void addRows(Activity act, LinearLayout container, List<String> codes,
                                Map<String, String> nMap,
                                Map<String, String> cMap,
                                Map<String, String> catMap) {

        container.removeAllViews();

        int i = 1;

        for (String code : codes) {

            View rowView = act.getLayoutInflater()
                    .inflate(R.layout.popup_doctor_count_rows, container, false);

            TextView tvSno = rowView.findViewById(R.id.tvSno);
            TextView tvCluster = rowView.findViewById(R.id.tvCluster);
            TextView tvDoctor = rowView.findViewById(R.id.tvDoctor);
            TextView tvCategory = rowView.findViewById(R.id.tvCategory);

            tvSno.setText(String.valueOf(i++));
            tvCluster.setText(cMap.getOrDefault(code, "-"));
            tvCategory.setText(catMap.getOrDefault(code, "-"));
            tvDoctor.setText(nMap.getOrDefault(code, code));

            container.addView(rowView);
        }
    }
//    public static void DoctorPlanPopup2(Activity activity,
//                                     boolean isDoctor,
//                                     List<String> vList,
//                                     List<String> nvList,
//                                     Map<String, String> nameMap,
//                                     Map<String, String> clusterMap,
//                                     Map<String, String> categoryMap,
//                                     String vRatio,
//                                     String nvRatio) {
//
//        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
//        View layout = activity.getLayoutInflater().inflate(R.layout.popup_doctor_count_time, null);
//
//        // XML elements-ai find pannunga
//        TextView heading = layout.findViewById(R.id.heading);
//        ImageView imgIcon = layout.findViewById(R.id.imgdr);
//        TextView clusterHead = layout.findViewById(R.id.tv_Cluster);
//        TextView name = layout.findViewById(R.id.tv_Name);
//        TextView clusterHead2 = layout.findViewById(R.id.tv_Cluster2);
//        TextView name2 = layout.findViewById(R.id.tv_Name2);
//        LinearLayout vContainer = layout.findViewById(R.id.visitedContainer);
//        LinearLayout nvContainer = layout.findViewById(R.id.visitedContainer2);
//        TextView tvVCount = layout.findViewById(R.id.tv_visited_ratio);
//        TextView tvNVCount = layout.findViewById(R.id.tv_visited_ratio2);
//        Button okButton2 = layout.findViewById(R.id.okButton2);
//
//        // Title and Headers Dynamic Setup
//        String cap = isDoctor ? SharedPref.getDrCap(activity) : "Chemist"; // Or get from SharedPref
//        heading.setText(" Today's " + cap + " Plan ");
//        imgIcon.setImageResource(isDoctor ? R.drawable.doctor_img : R.drawable.map_chemist_img);
//
//        clusterHead.setText(SharedPref.getClusterCap(activity));
//        clusterHead2.setText(SharedPref.getClusterCap(activity));
//        name.setText(cap);
//        name2.setText(cap);
//
//        tvVCount.setText(vRatio);
//        tvNVCount.setText(nvRatio);
//
//        // Add Rows
//        addRows(activity, vContainer, vList, nameMap, clusterMap, categoryMap);
//        addRows(activity, nvContainer, nvList, nameMap, clusterMap, categoryMap);
//
//        alert.setView(layout);
//        AlertDialog dialog = alert.create();
//        dialog.setCancelable(false);
//
//        // --- SWIPE LOGIC START ---
//        // Layout-la top-most view-ku ID iruntha atha find panni touch listener kudunga
////        View rootLayout = layout.findViewById(R.id.popup_parent_layout);
////        View headerLayout = layout.findViewById(R.id.header_layout_touch);
////
////        OnSwipeTouchListener swipeListener = new OnSwipeTouchListener(activity) {
////            @Override
////            public void onSwipeLeft() {
////                if (isDoctor && tempChmNVList != null) {
////                    Toast.makeText(activity, "Swiping to Chemist...", Toast.LENGTH_SHORT).show();
////                    dialog.dismiss();
////                    DoctorPlanPopup2(activity, false,
////                            tempChmVList, tempChmNVList,
////                            tempChmNameMap, tempChmClusterMap, tempChmCatMap,
////                            tempChmVRatio, tempChmNVRatio);
////                }
////            }
////            Override
////            public void onSwipeRight() {
////                // Ippo neenga ketta logic: Chemist-la irunthu thirumba Doctor-ku vara
////                // Ingae data-vai namma parameter-la irunthe eduthukalaam
////                if (!isDoctor) {
////                    Toast.makeText(activity, "Back to Doctor Plan", Toast.LENGTH_SHORT).show();
////                    dialog.dismiss();
////
////                    // Thirumba Doctor popup-ai original data-vudan call panrom
////                    // Intha vList, nvList ellam intha method-oda starting parameters
////                    DoctorPlanPopup2(activity, true,
////                            vList, nvList, nameMap, clusterMap, categoryMap, vRatio, nvRatio);
////                }
////            }
////        };
////
////// Rendu layouts-kum listener-ai attach pannunga
////        rootLayout.setOnTouchListener(swipeListener);
////        headerLayout.setOnTouchListener(swipeListener);
//        View rootLayout = layout.findViewById(R.id.popup_parent_layout);
//        View headerLayout = layout.findViewById(R.id.header_layout_touch);
//
//        OnSwipeTouchListener swipeListener = new OnSwipeTouchListener(activity) {
//            @Override
//            public void onSwipeLeft() {
//                // Doctor -> Chemist (Left Swipe)
//                if (isDoctor && tempChmNVList != null) {
//                    Toast.makeText(activity, "Swiping to Chemist...", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    DoctorPlanPopup2(activity, false,
//                            tempChmVList, tempChmNVList,
//                            tempChmNameMap, tempChmClusterMap, tempChmCatMap,
//                            tempChmVRatio, tempChmNVRatio);
//                }
//            }
//
//            @Override
//            public void onSwipeRight() {
//                // Chemist -> Doctor (Right Swipe / Left to Right)
//                // Ingae data-vai namma method parameters-la irunthe eduthukirom
//                if (!isDoctor) {
//                    Toast.makeText(activity, "Back to Doctor Plan", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//
//                    // Intha vList, nvList ellam Doctor-oda original data
//                    DoctorPlanPopup2(activity, true,
//                            vList, nvList, nameMap, clusterMap, categoryMap,
//                            vRatio, nvRatio);
//                }
//            }
//        };
//
//// Listener-ai rendu layout-kum attach pannunga
//        if (rootLayout != null) rootLayout.setOnTouchListener(swipeListener);
//        if (headerLayout != null) headerLayout.setOnTouchListener(swipeListener);
//        // --- SWIPE LOGIC END ---
//
//        dialog.show();
//        okButton2.setOnClickListener(v -> dialog.dismiss());
//
//        // Window size setup
//        Window window = dialog.getWindow();
//        if (window != null) {
//            int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.65);
//            int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.55);
//            window.setLayout(width, height);
//        }
//    }
////    public static void DoctorPlanPopup2(Activity activity,
////                                        List<String> vList,
////                                        List<String> nvList,
////                                        Map<String, String> nameMap,
////                                        Map<String, String> clusterMap,
////                                        Map<String, String> categoryMap,
////                                        String vRatio,
////                                        String nvRatio) {
////
////        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
////        View layout = activity.getLayoutInflater()
////                .inflate(R.layout.popup_doctor_count_time, null);
////
////        // Find Views
////        TextView heading = layout.findViewById(R.id.heading);
////        TextView clusterHead = layout.findViewById(R.id.tv_Cluster);
////        TextView name = layout.findViewById(R.id.tv_Name);
////        TextView clusterHead2 = layout.findViewById(R.id.tv_Cluster2);
////        TextView name2 = layout.findViewById(R.id.tv_Name2);
////        LinearLayout vContainer = layout.findViewById(R.id.visitedContainer);
////        LinearLayout nvContainer = layout.findViewById(R.id.visitedContainer2);
////        TextView tvVCount = layout.findViewById(R.id.tv_visited_ratio);
////        TextView tvNVCount = layout.findViewById(R.id.tv_visited_ratio2);
////        Button okButton2 = layout.findViewById(R.id.okButton2);
////        heading.setText(" Today's " + SharedPref.getDrCap(activity) + " Plan ");
////        clusterHead.setText(SharedPref.getClusterCap(activity));
////        clusterHead2.setText(SharedPref.getClusterCap(activity));
////        name.setText(SharedPref.getDrCap(activity));
////        name2.setText(SharedPref.getDrCap(activity));
////        // Set Ratio Text
////        tvVCount.setText(vRatio);
////        tvNVCount.setText(nvRatio);
////
////        // Add Rows
////        addRows(activity, vContainer, vList, nameMap, clusterMap, categoryMap);
////        addRows(activity, nvContainer, nvList, nameMap, clusterMap, categoryMap);
////
////        alert.setView(layout);
////
////        AlertDialog dialog = alert.create();
////        dialog.setCancelable(false);
////        dialog.setCanceledOnTouchOutside(false);
////
////        // OK Button Click
////
////
////        // SHOW FIRST
////        dialog.show();
////        okButton2.setOnClickListener(v -> dialog.dismiss());
////        // AFTER showing, set proper size
////        Window window = dialog.getWindow();
////        if (window != null) {
////
////            int width = (int) (activity.getResources()
////                    .getDisplayMetrics().widthPixels * 0.65);
////
////            int height = (int) (activity.getResources()
////                    .getDisplayMetrics().heightPixels * 0.55);
////
////            window.setLayout(width, height);
////        }
////    }
////    public static void DoctorPlanPopup2(Activity activity, List<String> vList, List<String> nvList,
////                                        Map<String, String> nameMap, Map<String, String> clusterMap,
////                                        String vRatio, String nvRatio) {
////
////        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
////        View layout = activity.getLayoutInflater().inflate(R.layout.popup_doctor_count_time, null);
////
////        LinearLayout vContainer = layout.findViewById(R.id.visitedContainer);
////        LinearLayout nvContainer = layout.findViewById(R.id.visitedContainer2);
////        TextView tvVCount = layout.findViewById(R.id.tv_visited_ratio);
////        TextView tvNVCount = layout.findViewById(R.id.tv_visited_ratio2);
////
////        tvVCount.setText(vRatio);
////        tvNVCount.setText(nvRatio);
////
////        // Visited Rows
////        addRows(activity, vContainer, vList, nameMap, clusterMap);
////        // Not Visited Rows
////        addRows(activity, nvContainer, nvList, nameMap, clusterMap);
////
////        alert.setView(layout);
////        AlertDialog dialog = alert.create();
////        dialog.setCancelable(false);
////        dialog.setCanceledOnTouchOutside(false);
////        layout.findViewById(R.id.okButton2).setOnClickListener(v -> dialog.dismiss());
////
////        // Inside DoctorPlanPopup2
////        dialog.setOnShowListener(d -> {
////            Window window = dialog.getWindow();
////            if (window != null) {
////                // Width 85% of screen
////                int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.65);
////
////                // Max height limit (60% of screen)
////                int maxHeight = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.60);
////
////                // Setting height to WRAP_CONTENT makes it small for 1 doctor
////                // But the XML layout_weight="1" will prevent it from going past the screen
////                window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
////
////                WindowManager.LayoutParams lp = window.getAttributes();
////                // If content is huge, this forces it to stay within screen limits
////                if (window.getDecorView().getHeight() > maxHeight) {
////                    window.setLayout(width, maxHeight);
////                }
////            }
////        });
////        dialog.show();
////    }
//
//    private static void addRows(Activity act, LinearLayout container, List<String> codes,
//                                Map<String, String> nMap, Map<String, String> cMap, Map<String, String> catMap) {
//        container.removeAllViews(); // ஹெடரை அழிக்காமல் இருக்க, XML-ல் ஹெடரை ScrollView-க்கு வெளியே வைத்திருங்கள்.
//
//        int i = 1;
//        for (String code : codes) {
//            // ✅ ஜாவாவில் புது TextView-க்கு பதில், உங்கள் XML டிசைனைப் பயன்படுத்துகிறோம்
//            View rowView = act.getLayoutInflater()
//                    .inflate(R.layout.popup_doctor_count_rows, container, false);
//            LinearLayout.LayoutParams params =
//                    new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT);
//
//            rowView.setLayoutParams(params);
//            TextView tvSno = rowView.findViewById(R.id.tvSno);
//            TextView tvCluster = rowView.findViewById(R.id.tvCluster);
//            TextView tvDoctor = rowView.findViewById(R.id.tvDoctor);
//            TextView tvCategory = rowView.findViewById(R.id.tvCategory);
//
//            // ✅ டேட்டாவை செட் செய்கிறோம்
//            tvSno.setText(String.valueOf(i++));
//            tvCluster.setText(cMap.getOrDefault(code, "-"));
//            tvCategory.setText(catMap.getOrDefault(code, "-"));
//            tvDoctor.setText(nMap.getOrDefault(code, code));
//
//            // ✅ முழு Row-வையும் லிஸ்ட்டில் சேர்க்கிறோம்
//            container.addView(rowView);
//        }
//    }

//    private static TextView createTextView(Activity act, String text, int weight) {
//        TextView tv = new TextView(act);
//        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
//        tv.setText(text);
//        tv.setTextSize(10f);
//        tv.setTextColor(Color.BLACK);
//        tv.setTypeface(Typeface.MONOSPACE);
//        return tv;
//    }
//    public static void DoctorPlanPopup2(Activity activity, String visitedMsg, String notVisitedMsg, String ratio, String nvRatio) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
//        alert.setCancelable(false);
//
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View alertLayout = inflater.inflate(R.layout.popup_doctor_count_time, null);
//
//        TextView heading = alertLayout.findViewById(R.id.heading);
//        TextView tvVisitedList = alertLayout.findViewById(R.id.messagePopup);
//        TextView tvNotVisitedList = alertLayout.findViewById(R.id.messagePopup2);
//
//        // ✅ பிழை திருத்தம்: 'alertLayout' மூலம் ஐடியை தேட வேண்டும்
//        TextView tvRatio = alertLayout.findViewById(R.id.tv_visited_ratio);
//        TextView tvRatio2 = alertLayout.findViewById(R.id.tv_visited_ratio2);
//
//        AppCompatButton okButton = alertLayout.findViewById(R.id.okButton);
//
//        heading.setText(" Today's Planned " + SharedPref.getDrCap(activity));
//
//        // ✅ டேட்டாவை செட் செய்தல்
//        tvVisitedList.setText(visitedMsg);
//        tvNotVisitedList.setText(notVisitedMsg);
//
//        // ✅ ரேஷியோ (1/12) செட் செய்தல்
//        if (tvRatio != null) {
//            tvRatio.setText(ratio);
//        }
//        if (tvRatio2 != null) {
//            tvRatio2.setText(nvRatio);
//        }
//        alert.setView(alertLayout);
//        AlertDialog dialog = alert.create();
//
//        // ✅ பட்டன் கிளிக் லாஜிக்
//        okButton.setOnClickListener(view -> dialog.dismiss());
//
//        dialog.show();
//    }

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
                            if (isMock) {
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
                        Log.e("Location CAB", "OnLocationReceived: Location null");
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