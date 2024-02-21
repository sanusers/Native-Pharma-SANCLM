package saneforce.santrip.commonClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcelable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.LocaleHelper;


public class CommonUtilsMethods {
    Context context;
    Activity activity;

    public CommonUtilsMethods(Activity activity) {
        this.activity = activity;
    }


    public CommonUtilsMethods(Context context) {
        this.context = context;
    }


    public static String gettingAddress(Activity activity, double la, double ln, boolean toastMsg) {
        Geocoder geocoder;
        List<Address> addresses;
        String address = "";
        geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(la, ln, 1);
            if (Objects.requireNonNull(addresses).size() > 0) {
                address = addresses.get(0).getAddressLine(0);
                /*String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();*/
            } else {
                address = "No Address Found";
            }

            if (toastMsg) {

                Toast toast = Toast.makeText(activity, "Location Captured:" + address, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        } catch (IOException e) {
            if (toastMsg) {
                address = "No Address Found";
                Toast toast = Toast.makeText(activity, address + " Try Again!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
            e.printStackTrace();
        }
        return address;
    }


    public static InputFilter FilterSpaceEditText(EditText editText) {
        return new InputFilter() {
            boolean canEnterSpace = false;

            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (editText.getText().toString().equals("")) {
                    canEnterSpace = false;
                }

                StringBuilder builder = new StringBuilder();

                for (int i = start; i < end; i++) {
                    char currentChar = source.charAt(i);

                    if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                        builder.append(currentChar);
                        canEnterSpace = true;
                    }

                    if (Character.isWhitespace(currentChar) && canEnterSpace) {
                        builder.append(currentChar);
                    }


                }
                return builder.toString();
            }
        };
    }

    public static void RequestGPSPermission(Activity activity) {
        // LocationManager locationManager = (LocationManager) activity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        new android.app.AlertDialog.Builder(activity).setTitle("Alert")  // GPS not found
                .setCancelable(false).setMessage("Activate the Gps to proceed further") // Want to enable?
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialogInterface.dismiss();
                }).show();
    }

    public static void RequestPermissions(Activity activity, String[] Permissions, boolean isRefresh) {
        PermissionListener permissionlistener = new PermissionListener() {
            @SuppressLint("UnsafeIntentLaunch")
            @Override
            public void onPermissionGranted() {
                if (isRefresh) activity.startActivity(activity.getIntent());
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
            }
        };

        TedPermission.create().setPermissionListener(permissionlistener).setPermissions(Permissions).setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]").check();
    }

    public static String getCurrentInstance(String requiredFormat) {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(requiredFormat);
        return sdf.format(c.getTimeInMillis());
    }

    @SuppressLint("SimpleDateFormat")
    public static String setConvertDate(String currentFormat, String requiredFormat, String date) {
        SimpleDateFormat spf = new SimpleDateFormat(currentFormat);
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException ignored) {
        }
        spf = new SimpleDateFormat(requiredFormat);
        assert newDate != null;
        return spf.format(newDate);
    }

    public static String getCurrentTime() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(currentTime);
    }


    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(c.getTimeInMillis());
    }

    public static String getCurrentAMPM() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        return sdf.format(c.getTimeInMillis());
    }

    public static String getCurrentDateWithMonthName() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm aa");
        return sdf.format(c.getTimeInMillis());
    }

    public static String getCurrentDateDashBoard() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        return sdf.format(c.getTimeInMillis());
    }

    public static String getCurrentMonthNumber() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("M");
        String val = sdf.format(currentTime);
        return val;
    }

    public static String getCurrentMonthName() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        String val = sdf.format(currentTime);
        return val;
    }

    public static String getCurrentYear() {
        Date currentTime = Calendar.getInstance().getTime();
        Log.v("Printing_current_time", String.valueOf(currentTime.getTime()));
        Log.v("Printing_current_time", String.valueOf(currentTime));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String val = sdf.format(currentTime);
        Log.v("Printing_current_date", val);
        return val;
    }


    public static String getCurrentDateDMY() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(currentTime);
    }

    public static String getCurrentDayNo() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("d");
        return sdf.format(currentTime);
    }

    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException ignored) {

        }

        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_progress);
        return dialog;
    }

    public void setUpLanguage(Context context) {
        String language = SharedPref.getSelectedLanguage(context);
        Resources resources = context.getResources();
        if (language.equalsIgnoreCase("")) {
            language = "en";
        }
        Locale myLocale = new Locale(language);
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.locale = myLocale;
        resources.updateConfiguration(conf, dm);
        LocaleHelper.setLocale(context, language);
    }

    public void ShowToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();

//Gets the actual oval background of the Toast then sets the colour filter
       // assert view != null;
    //    view.getBackground().setColorFilter(context.getColor(R.color.dark_purple), PorterDuff.Mode.SRC_IN);

//Gets the TextView from the Toast so it can be editted
//        TextView text = view.findViewById(android.R.id.message);
//        text.setTextColor(context.getColor(R.color.white));
//
//        toast.show();
    }

    public void recycleTestWithoutDivider(RecyclerView rv_test) {
        try {
            if (rv_test.getItemDecorationCount() > 0) {
                for (int i = 0; i < rv_test.getItemDecorationCount(); i++) {
                    rv_test.removeItemDecorationAt(i);
                }
            }
            rv_test.setItemAnimator(new DefaultItemAnimator());
            Parcelable recyclerViewState;
            recyclerViewState = Objects.requireNonNull(rv_test.getLayoutManager()).onSaveInstanceState();
            rv_test.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        } catch (Exception ignored) {
        }
    }

    public void ExpandableView(ExpandableListView rv_test) {
        try {
            Parcelable recyclerViewState;
            recyclerViewState = Objects.requireNonNull(rv_test).onSaveInstanceState();
            rv_test.onRestoreInstanceState(recyclerViewState);
        } catch (Exception ignored) {
        }
    }

    public void displayPopupWindow(Activity activity, Context context, View view, String name) {
        PopupWindow popup = new PopupWindow(context);
        @SuppressLint("InflateParams") View layout = activity.getLayoutInflater().inflate(R.layout.popup_text, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tv_name = layout.findViewById(R.id.tv_name);
        tv_name.setText(name);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(view);
    }

    public void recycleTestWithDivider(RecyclerView rv_test) {

        if (rv_test.getItemDecorationCount() > 0) {
            for (int i = 0; i < rv_test.getItemDecorationCount(); i++) {
                rv_test.removeItemDecorationAt(i);
            }
        }
        rv_test.setItemAnimator(new DefaultItemAnimator());
        rv_test.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        Parcelable recyclerViewState;
        recyclerViewState = Objects.requireNonNull(rv_test.getLayoutManager()).onSaveInstanceState();
        rv_test.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    public void setSpinnerText(Spinner spin, String text) {
        for (int i = 0; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().contains(text)) {
                spin.setSelection(i);
            }
        }
    }

    public void FullScreencall() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
