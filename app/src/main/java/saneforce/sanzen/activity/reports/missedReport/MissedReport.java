package saneforce.sanzen.activity.reports.missedReport;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.masterSync.MasterSyncActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityMissedReportBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitTable;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.MissedDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;
import saneforce.sanzen.utility.TimeUtils;

public class MissedReport extends AppCompatActivity {
    public static String JoiningDate, JoiningMonth, JoiningYear;
    public String currentmonth, currentYear;
    private MasterDataDao masterDataDao;
    private String date = "";
    private RoomDB db;
    private AlertDialog hqDialog;
    private AlertDialog monthDialog;
    private String selectedDate = "";
    private String selectedHqId = "";
    private ActivityResultLauncher<Intent> launcher;
    private ActivityMissedReportBinding binding;
    MissedReportAdapter adapter;
    final List<MissedReportItem> reportList = new ArrayList<>();

    private FrameLayout blockingOverlay;
    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30+
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Legacy for API < 30
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMissedReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = RoomDB.getDatabase(this);
        masterDataDao = db.masterDataDao();


        if (SharedPref.getSfType(this).equals("2")) {
            binding.headquarters.setVisibility(View.VISIBLE);
            binding.headquarters.setOnClickListener(view -> {
                if (selectedDate == null || selectedDate.isEmpty()) {
                    Toast.makeText(MissedReport.this, getString(R.string.please_select_month), Toast.LENGTH_SHORT).show();
                    return;
                }
//                showHeadquartersPicker();
            });
        } else {
            binding.headquarters.setVisibility(View.GONE);
        }



        blockingOverlay = findViewById(R.id.blocking_overlay);
        blockingOverlay.setVisibility(View.GONE);
        getJoiningDate();
        adapter = new MissedReportAdapter(this, reportList, (item, position) -> {
//            getData(date, item.getSfCode());
//            fetchAndLoadData(date, item.getSfCode());
        });
        binding.recyclerMissedReports.setAdapter(adapter);
        binding.recyclerMissedReports.setVisibility(View.GONE);
        binding.outboxEmtyImage.setVisibility(View.VISIBLE);


        if (SharedPref.getSfType(this).equals("1")) {
            binding.emptyMessage.setText(getString(R.string.please_select_month));

        } else if (SharedPref.getSfType(this).equals("2")) {
            binding.emptyMessage.setText(getString(R.string.please_select_month_headquarters));
        } else {
            binding.emptyMessage.setText("");
        }

        currentmonth = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_8);
        currentYear = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_26);

//        adapter = new MissedReportAdapter(MissedReportAdapter.this, reportList);
//        binding.recyclerMissedReports.setAdapter(adapter);
        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) {
                    adapter.getFilter().filter(charSequence.toString());
                }
                if (charSequence.length() > 0) {
                    binding.searchClearIcon.setVisibility(View.VISIBLE);
                } else {
                    binding.searchClearIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.searchClearIcon.setOnClickListener(view -> {
            binding.searchET.setText("");
            binding.searchClearIcon.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(binding.searchET.getWindowToken(), 0);
            }
        });


//        adapter = new MissedReportAdapter(this, reportList);
//        binding.recyclerMissedReports.setAdapter(adapter);
//        binding.recyclerMissedReports.setVisibility(View.GONE);

//        binding.boxCombined.setVisibility(View.GONE);

        binding.imageBack.setOnClickListener(view -> {
            RoomDB.databaseWriteExecutor.execute(() -> {
                RoomDB db = RoomDB.getDatabase(MissedReport.this);
                db.missedDao().deleteAll();        // Clear missed data
                db.doctorVisitDao().deleteAll();   // Clear doctor visit data
                runOnUiThread(this::finish);       // Close the activity after clearing
            });
        });

//        binding.imageBack.setOnClickListener(v -> {
//            binding.calender.setText("");
//            showMonthYearPicker(binding.calender);
//            finish();
//        });
//        binding.calender.setOnClickListener(view -> showMonthYearPicker(binding.calender));
//        binding.doctorStatsLayout.setOnClickListener(v -> {
//            Intent intent = new Intent(MissedReport.this, DoctorVisitActivity.class);
//            startActivity(intent);
//        });


        hideSystemBars();
        TextView monthYearTextView = findViewById(R.id.calender);

//        monthYearTextView.setOnClickListener(view ->
//
//                showMonthYearPicker(monthYearTextView));
    }

//    private void showHeadquartersPicker() {
//        if (hqDialog != null && hqDialog.isShowing()) {
//            return;
//        }
//        try {
//            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();//object to add
//            ArrayList<String> list = new ArrayList<>();
//
//            // added lines
//            JSONObject  object= new JSONObject();
//            object.put("name", SharedPref.getSfName(this));
//            object.put("id", SharedPref.getSfCode(this));
//            jsonArray.put(object);
//            list.add(object.optString("name", " "));
//
//            if (jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    //list.add(jsonObject.getString("name"));
//                    String name = jsonObject.getString("name");
//                    if (!name.equalsIgnoreCase(SharedPref.getSfName(this))) {
//                        list.add(name);
//                    }
//                }//sfname sharedpref
//            }
//
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MissedReport.this);
//            LayoutInflater inflater = MissedReport.this.getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.dialog_headquarters_picker, null);
//            alertDialog.setView(dialogView);
//            TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
//            ListView listView = dialogView.findViewById(R.id.listView);
//            SearchView searchView = dialogView.findViewById(R.id.searchET);
//
//            headerTxt.setText(getResources().getText(R.string.select_hq));
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(MissedReport.this, android.R.layout.simple_list_item_1, list);
//            listView.setAdapter(adapter);
//            hqDialog = alertDialog.create();
//
////            AlertDialog dialog = alertDialog.create();
//
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String s) {
//                    adapter.getFilter().filter(s);
//                    return false;
//                }
//                @Override
//                public boolean onQueryTextChange(String s) {
//                    adapter.getFilter().filter(s);
//                    return false;
//                }
//            });
//
//            listView.setOnItemClickListener((adapterView, view1, position, l) -> {
//                String selectedHq = listView.getItemAtPosition(position).toString();
//                binding.headquarters.setText(selectedHq);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    try {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        if (jsonObject.optString("name").equalsIgnoreCase(selectedHq)) {
//                            selectedHqId = jsonObject.optString("id", "");
//
//                            if (!selectedDate.isEmpty() && !selectedHqId.isEmpty()) {
//                                tryFetchReport();
//                            }
//                            break;
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    binding.emptyMessage.setVisibility(View.GONE);
//                }
//                hqDialog.dismiss();
//            });
//
//
//            alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
//            hqDialog.show();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        UtilityClass.hideKeyboard(MissedReport.this);
//    }

//    private void tryFetchReport() {
//        if (selectedDate == null || selectedDate.isEmpty()) {
//            Log.d("MissedReport", "Date not selected → skipping API call");
//            return;
//        }
//        if (SharedPref.getSfType(this).equals("1")) {
//            fetchAndLoadMonthlyData(selectedDate,SharedPref.getSfCode(this));
//        } else if (SharedPref.getSfType(this).equals("2")) {
//            if (selectedHqId != null && !selectedHqId.isEmpty()) {
//                fetchAndLoadMonthlyData(selectedDate,selectedHqId);
//            } else {
//                Log.d("MissedReport", "HQ not selected → skipping API call");
//            }
//        }
//    }


    private void getJoiningDate() {
        try {
            String SFDCR_Date_sp = SharedPref.getSfDCRDate(this);
            JSONObject obj = new JSONObject(SFDCR_Date_sp);
            String SFDCR_Date = obj.getString("date");
            if (!SFDCR_Date.isEmpty()) {
                JoiningDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_7, SFDCR_Date);
                JoiningMonth = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_8, SFDCR_Date);
                JoiningYear = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_10, SFDCR_Date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void showMonthYearPicker(TextView monthYearTextView) {
//        if (monthDialog != null && monthDialog.isShowing()) {
//            return;
//        }
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View dialogView = inflater.inflate(R.layout.activity_month_year, null);
//        ImageView crossImage = dialogView.findViewById(R.id.crossImage);
//        ListView monthYearListView = dialogView.findViewById(R.id.monthYearListView);
//
//        // Combine month & year into single string
//        List<String> monthYearList = getCurrentAndPreviousMonths();
//
//        // Adapter for combined month-year list
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, monthYearList);
//        monthYearListView.setAdapter(adapter);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialogView);
//        monthDialog = builder.create();
////        AlertDialog dialog = builder.create();
//        monthDialog.setCanceledOnTouchOutside(false);
//        crossImage.setOnClickListener(view -> monthDialog.dismiss());
//        monthDialog.show();
//
//        // When item is clicked, set to TextView and close dialog
//        monthYearListView.setOnItemClickListener((parent, view, position, id) -> {
//            String selected = monthYearList.get(position);
//
//            monthYearTextView.setText(selected);
//            try {
////                added lines
//                reportList.clear();
//                adapter. notifyDataSetChanged();;
//                binding.recyclerMissedReports.setVisibility(View.GONE);
//                hqDialog = null; // reset HQ dialog reference
//                binding.headquarters.setText("Select HeadQuarters");
//                // Input format: "MMMM yyyy" (August 2025)
//                SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
//                // Output format: "yyyy-MM-dd" (2025-08-01) - set day as 01 by default
//                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                date = outputFormat.format(inputFormat.parse(selected));
//                selectedDate=date;
//
//                binding.outboxEmtyImage.setVisibility(View.GONE);
//                binding.recyclerMissedReports.setVisibility(View.VISIBLE);
//                binding.emptyMessage.setVisibility(View.GONE);
//
//                if (SharedPref.getSfType(this).equals("1")) {
//                    tryFetchReport();
//                }
////                binding.boxCombined.setVisibility(View.VISIBLE);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            monthDialog.dismiss();
//        });
//    }

    private List<String> getCurrentAndPreviousMonths() {
        List<String> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        int joinMonth = Integer.parseInt(JoiningMonth); // "09" → 9
        int joinYear = Integer.parseInt(JoiningYear); // "2025" → 2025

        int count = 0; // to track max 4 months

// Loop backwards until join date or 4 months are added
        while ((cal.get(Calendar.YEAR) > joinYear ||
                (cal.get(Calendar.YEAR) == joinYear && (cal.get(Calendar.MONTH) + 1) >= joinMonth))
                && count < 4) {

            months.add(sdf.format(cal.getTime()));
            cal.add(Calendar.MONTH, -1);
            count++;
        }

        return months;
    }

    private void showLoadingOverlay() {
        if (blockingOverlay != null) {
            blockingOverlay.setVisibility(View.VISIBLE);
        }
    }

    // Hide the blocking overlay
    private void hideLoadingOverlay() {
        if (blockingOverlay != null) {
            blockingOverlay.setVisibility(View.GONE);
        }
    }

//    public void fetchAndLoadMonthlyData(String date,String sfcode) {
//        RoomDB.databaseWriteExecutor.execute(() -> {
//            RoomDB db = RoomDB.getDatabase(MissedReport.this);
//            MissedDao missedDao = db.missedDao();
//            String storedJson = missedDao.getMissedValues(sfcode,date);
//
//            runOnUiThread(() -> {
//                if (storedJson != null && !storedJson.isEmpty()) {
//                    try {
//                        JSONArray storedArray = new JSONArray(storedJson);
//                        reportList.clear();
//                        for (int i = 0; i < storedArray.length(); i++) {
//                            JSONObject obj = storedArray.optJSONObject(i);
//                            String name = obj.optString("Name");
//                            String hq = obj.optString("Cluster");
//                            String totalDoctor = obj.optString("Dcnt", "0");
//                            String visited = obj.optString("Dmet", "0");
//                            String missed = obj.optString("Dmis", "0");
//                            String sfCode = obj.optString("sf_code");
//
//                            MissedReportItem missedReportItem = new MissedReportItem(name, hq, totalDoctor, visited, missed, sfCode);
//                            reportList.add(missedReportItem);
//                        }
//
//                        adapter.updateData(reportList);
//                        binding.recyclerMissedReports.setVisibility(View.VISIBLE);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    getReportData(date,sfcode);
//                }
//            });
//        });
//    }

//    public void getReportData(String date ,String sfcode) {
//        showLoadingOverlay();
//        if (UtilityClass.isNetworkAvailable(this)) {
//            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
//                if (status) {
//                    try {
//                        ApiInterface apiInterface = RetrofitClient.getRetrofit(MissedReport.this, SharedPref.getCallApiUrl(MissedReport.this));
//                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
//                        jsonObject.put("sfcode", sfcode);
//                        jsonObject.put("division_code", SharedPref.getDivisionCode(this));
//                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
//                        jsonObject.put("rptDt", date);
//                        jsonObject.put("tableName", "getmissedrpt");
//                        Log.d("TAG", "getData: " + jsonObject);
//
//                        Map<String, String> mapString = new HashMap<>();
//                        mapString.put("axn", "get/reports");
//
//                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
//                        call.enqueue(new Callback<JsonElement>() {
//                            @Override
//                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
//                                hideLoadingOverlay();
//                                try {
//                                    if (response.isSuccessful() && response.body() != null) {
//                                        JsonElement jsonElement = response.body();
//
//                                        if (jsonElement.isJsonArray()) {
//                                            JSONArray jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
//
//                                            if (jsonArray.length() > 0) {
//                                                String jsonString = jsonArray.toString();
//
//                                                // Save and retrieve from Room
//                                                RoomDB.databaseWriteExecutor.execute(() -> {
//                                                    RoomDB db = RoomDB.getDatabase(MissedReport.this);
//                                                    MissedDao missedDao = db.missedDao();
//                                                    missedDao.saveMissedJson(date,sfcode,jsonString);
//
//                                                    if (jsonString != null && !jsonString.equals("[]")) {
//
//                                                        missedDao.saveMissedJson(date, sfcode, jsonString);
//
//                                                        String storedJson = missedDao.getMissedValues(sfcode,date); // retrieve
//
//                                                        try {
//                                                            JSONArray storedArray = new JSONArray(storedJson);
//
//                                                            reportList.clear();
//                                                            for (int i = 0; i < storedArray.length(); i++) {
//                                                                JSONObject obj = storedArray.optJSONObject(i);
//                                                                String name = obj.optString("Name");
//                                                                String hq = obj.optString("Cluster");
//                                                                String totalDoctor = obj.optString("Dcnt", "0");
//                                                                String visited = obj.optString("Dmet", "0");
//                                                                String missed = obj.optString("Dmis", "0");
//                                                                String sfCode = obj.optString("sf_code");
//
//                                                                MissedReportItem missedReportItem = new MissedReportItem(name, hq, totalDoctor, visited, missed, sfCode);
//                                                                reportList.add(missedReportItem);
//                                                            }
//
//                                                            runOnUiThread(() -> {
//                                                                adapter.updateData(reportList);
//                                                                binding.recyclerMissedReports.setVisibility(View.VISIBLE);
//                                                            });
//
//                                                        } catch (JSONException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                            //added lines
//                                            else {
//                                                runOnUiThread(() -> {
//                                                    reportList.clear();
//                                                    adapter.updateData(reportList);
//                                                    binding.recyclerMissedReports.setVisibility(View.GONE);
//                                                });
//                                            }
//                                        }
//                                    }
//                                }
//                                catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//
//                            @Override
//                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
//                                hideLoadingOverlay();
//                                runOnUiThread(() -> {
//                                    CommonUtilsMethods.showToastMessage(MissedReport.this, "Failed to load data");//fails
//                                });
//                                // Handle failure here if needed
//                            }
//                        });
//                    } catch (JSONException e) {
//                        hideLoadingOverlay();
//                        e.printStackTrace();
//                    }
//                } else {
//                    hideLoadingOverlay();
//                    CommonUtilsMethods.showToastMessage(MissedReport.this, getString(R.string.poor_connection));
//                }
//            });
//            networkStatusTask.execute();
//        } else {
//            hideLoadingOverlay();
//            CommonUtilsMethods.showToastMessage(MissedReport.this, getString(R.string.no_network));
//        }
//    }

//    public void getData(String date, String sfcode) {
//        showLoadingOverlay();
//        if (UtilityClass.isNetworkAvailable(this)) {
//            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
//                if (status) {
//                    try {
//                        ApiInterface apiInterface = RetrofitClient.getRetrofit(MissedReport.this, SharedPref.getCallApiUrl(MissedReport.this));
//                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
//                        jsonObject.put("sfcode", sfcode);
//                        jsonObject.put("division_code", SharedPref.getDivisionCode(this));
//                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
//                        jsonObject.put("report_date", date);
//                        jsonObject.put("tableName", "getmissedrptview");
//                        Log.d("TAG", "getData: " + jsonObject);
//
//                        Map<String, String> mapString = new HashMap<>();
//                        mapString.put("axn", "get/reports");
//
//                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
//                        call.enqueue(new Callback<JsonElement>() {
//                            @Override
//                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
//                                hideLoadingOverlay();
//                                try {
//                                    if (response.isSuccessful() && response.body() != null) {
//                                        JsonElement jsonElement = response.body();
//
//                                        if (jsonElement.isJsonArray()) {
//                                            JSONArray jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
//
//                                            if (jsonArray.length() > 0) {
//                                                String arrayAsString = jsonArray.toString();
//                                                RoomDB.databaseWriteExecutor.execute(() -> {
//                                                    Intent intent = new Intent(MissedReport.this, DoctorVisitActivity.class);
////                                                    intent.putExtra("doctor_array", arrayAsString);
////                                                    startActivity(intent);
//                                                    RoomDB db = RoomDB.getDatabase(MissedReport.this);  // context = MissedReport.this or your Activity context
//                                                    DoctorVisitDao visitDao = db.doctorVisitDao();
//                                                    visitDao.saveVisitJson(sfcode, date,jsonArray.toString());
//                                                    intent.putExtra("sfcode", sfcode);
//                                                    intent.putExtra("date",date);
//                                                    startActivity(intent);
//                                                });
//
//                                            }
//                                        }
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
//                                hideLoadingOverlay();
//                                runOnUiThread(() -> {
//                                    CommonUtilsMethods.showToastMessage(MissedReport.this, "Failed to load data");//fails
//                                });
//                            }
//                        });
//                    } catch (JSONException e) {
//                        hideLoadingOverlay();
//
//                        e.printStackTrace();
//                    }
//                } else {
//                    hideLoadingOverlay();
//                    CommonUtilsMethods.showToastMessage(MissedReport.this, getString(R.string.poor_connection));
//                }
//            });
//            networkStatusTask.execute();
//        } else {
//            hideLoadingOverlay();
//            CommonUtilsMethods.showToastMessage(MissedReport.this, getString(R.string.no_network));
//        }
//    }
//    public void fetchAndLoadData(String date, String sfcode) {
//
//        RoomDB.databaseWriteExecutor.execute(() -> {
//            RoomDB db = RoomDB.getDatabase(MissedReport.this);
//            DoctorVisitDao visitDao = db.doctorVisitDao();
//            String doctorArrayString = visitDao.getVisitValues(sfcode, date);
//
//            runOnUiThread(() -> {
//                if (doctorArrayString != null && !doctorArrayString.isEmpty()) {
//                    Intent intent = new Intent(MissedReport.this, DoctorVisitActivity.class);
////                    intent.putExtra("doctor_array", doctorArrayString);
//                    intent.putExtra("sfcode", sfcode);
//                    intent.putExtra("date", date);
//                    startActivity(intent);
//                } else {
//                    getData(date, sfcode);
//                }
//            });
//        });
//    }
}



