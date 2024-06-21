package saneforce.sanzen.commonClasses;

import static saneforce.sanzen.commonClasses.Constants.APP_MODE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import saneforce.sanzen.R;

import saneforce.sanzen.activity.login.LoginActivity;
import saneforce.sanzen.databinding.DialogTimezoneBinding;

import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.LocaleHelper;


public class CommonUtilsMethods {
    Context context;
    Activity activity;
    public static boolean isLocationFounded = false;

    public CommonUtilsMethods(Activity activity) {
        this.activity = activity;
    }


    public CommonUtilsMethods(Context context) {
        this.context = context;
    }


    public static String gettingAddress(Activity activity, double la, double ln, boolean toastMsg) {

        Geocoder geocoder;
        List<Address> addresses;
        String address = "No Address Found";
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
                LayoutInflater inflater = activity.getLayoutInflater();

                View layout = inflater.inflate(R.layout.toast_layout, activity.findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Location Captured:" + address);
                Toast toast = new Toast(activity);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                isLocationFounded = true;
               /* Toast toast = Toast.makeText(activity, "Location Captured:" + address, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();*/
            }
        } catch (IOException e) {
            if (toastMsg) {
                LayoutInflater inflater = activity.getLayoutInflater();

                View layout = inflater.inflate(R.layout.toast_layout, activity.findViewById(R.id.toast_layout_root));

                TextView text = layout.findViewById(R.id.text);
                text.setText("No Address Found!  Try Again!");

                Toast toast = new Toast(activity);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                isLocationFounded = false;

               /* Toast toast = Toast.makeText(activity, address + " Try Again!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();*/
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

    public static InputFilter FilterSpaceEditText(final EditText editText, final int maxLength) {
        return new InputFilter() {
            boolean canEnterSpace = false;

            @Override
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

                String result = dest.toString().substring(0, dstart) + builder.toString() + dest.toString().substring(dend);

                if (result.length() > maxLength) {
                    return "";
                }

                return builder.toString();
            }
        };
    }


    public static void RequestGPSPermission(Activity activity) {
        // LocationManager locationManager = (LocationManager) activity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        new android.app.AlertDialog.Builder(activity).setTitle("Alert")  // GPS not found
                .setCancelable(false).setMessage("Enable the location of Gps to proceed further") // Want to enable?
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialogInterface.dismiss();
                }).show();
    }



    public static void RequestGPSPermission(Activity activity,String FunctionName) {

        new android.app.AlertDialog.Builder(activity).setTitle("Alert")  // GPS not found
                .setCancelable(false).setMessage(FunctionName+" permission is required for this app to function correctly.") // Want to enable?
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                   activity.startActivity(intent);                        dialogInterface.dismiss();
                }).show();
    }



    public void loginNavigation(Activity activity) {
        new android.app.AlertDialog.Builder(activity)
                .setTitle("Alert")
                .setCancelable(false)
                .setMessage("Would you go to Login Page?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    dialogInterface.dismiss();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                })
                .show();
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


    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException ignored) {
            Log.e("DialogboxStatus", "" + ignored);
        }

        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_progress);
        return dialog;
    }

    public void showToastMessage(Activity activity, String message) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, activity.findViewById(R.id.toast_layout_root));

        //ImageView image = layout.findViewById(R.id.image);
        // image.setImageResource(R.drawable.san_clm_logo);
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void showToastMessage(Context context, String message) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, ((Activity) context).findViewById(R.id.toast_layout_root));

        //ImageView image = layout.findViewById(R.id.image);
        // image.setImageResource(R.drawable.san_clm_logo);
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

/*    public void ShowToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();
        try {
            assert view != null;
        } catch (Exception ignored) {

        }
        view.getBackground().setColorFilter(context.getColor(R.color.dark_purple), PorterDuff.Mode.SRC_IN);


        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(context.getColor(R.color.white));
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }*/

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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_text, null);
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

    public static String removeLastComma(String string) {
        if (string.endsWith(",")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    public boolean isAutoTimeEnabled(Context context) {
        ContentResolver resolver = context.getContentResolver();
        try {
            int autoTime = Settings.Global.getInt(resolver, Settings.Global.AUTO_TIME);
            return autoTime == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showCustomDialog(Activity context) {
        DialogTimezoneBinding timezoneBinding = DialogTimezoneBinding.inflate(LayoutInflater.from(context));
        AlertDialog.Builder builder = new AlertDialog.Builder(context, 0);
        AlertDialog customDialog = builder.create();
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setView(timezoneBinding.getRoot());
        customDialog.setCancelable(false);
        customDialog.show();
        timezoneBinding.btnOpenSettings.setOnClickListener(v -> {
            customDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            context.finishAffinity();
        });
    }
    public boolean isTimeZoneAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), Settings.System.AUTO_TIME_ZONE, 0) == 1;
        }
    }

    public static JSONObject  CommonObjectParameter(Context context){
        JSONObject jsonObject =new JSONObject();
        try {
            jsonObject.put("AppName", context.getString(R.string.str_app_name));
            jsonObject.put("Appver", context.getResources().getString(R.string.app_version));
            jsonObject.put("Mod", APP_MODE);
            jsonObject.put("sf_emp_id", SharedPref.getSfEmpId(context));
            jsonObject.put("sfname", SharedPref.getSfName(context));
            jsonObject.put("Device_version", Build.VERSION.RELEASE);
            jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonObject.put("language", SharedPref.getSelectedLanguage(context));
            jsonObject.put("sf_type", SharedPref.getSfType(context));
            jsonObject.put("Designation", SharedPref.getDesig(context));
            jsonObject.put("state_code", SharedPref.getStateCode(context));
            jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(context));
            jsonObject.put("key", SharedPref.getLicenseKey(context));
            jsonObject.put("Configurl", SharedPref.getBaseWebUrl(context));

        } catch (JSONException e) {
          e.printStackTrace();
        }
        return jsonObject;
    }
    }
