//package saneforce.sanzen.activity.reports.missedReport;
//
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import saneforce.sanzen.R;
//
//public class ApprovedCallsMissedFragment extends Fragment {
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_approved_calls_missed, container, false);
//    }
//}
package saneforce.sanzen.activity.reports.missedReport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityMissedReportBinding;
import saneforce.sanzen.databinding.FragmentApprovedCallsMissedBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.MissedDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;
import saneforce.sanzen.utility.TimeUtils;

public class FragmentApprovedCallsMissed extends Fragment {
    public static String JoiningDate, JoiningMonth, JoiningYear;
    public String currentmonth, currentYear;
    private MasterDataDao masterDataDao;
    private String date = "";
    private RoomDB db;
    private AlertDialog hqDialog;
    private AlertDialog monthDialog;
    private String selectedDate = "";
    private String selectedHqId = "";
    private FragmentApprovedCallsMissedBinding binding;
    MissedReportAdapter adapter;
    final List<MissedReportItem> reportList = new ArrayList<>();
    private View blockingOverlay;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentApprovedCallsMissedBinding.inflate(inflater, container, false);

        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        db = RoomDB.getDatabase(requireContext());
        masterDataDao = db.masterDataDao();
        blockingOverlay = binding.blockingOverlay;
        blockingOverlay.setVisibility(View.GONE);
        getJoiningDate();

        adapter = new MissedReportAdapter(requireContext(), reportList, (item, position) -> fetchAndLoadData(date, item.getSfCode()));
        binding.recyclerMissedReports.setAdapter(adapter);
        binding.recyclerMissedReports.setVisibility(View.GONE);
        binding.outboxEmtyImage.setVisibility(View.VISIBLE);

        if (SharedPref.getSfType(requireContext()).equals("1")) {
            binding.emptyMessage.setText("Please Select Month");
        } else if (SharedPref.getSfType(requireContext()).equals("2")) {
            binding.emptyMessage.setText("Please Select Month & Headquarters");
        } else {
            binding.emptyMessage.setText("");
        }

        currentmonth = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_8);
        currentYear = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_26);

        setupSearch();

        if (SharedPref.getSfType(requireContext()).equals("2")) {
            binding.headquarters.setVisibility(View.VISIBLE);
            binding.headquarters.setOnClickListener(v -> {
                if (selectedDate == null || selectedDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select month", Toast.LENGTH_SHORT).show();
                    return;
                }
                showHeadquartersPicker();
            });
        } else {
            binding.headquarters.setVisibility(View.GONE);
        }

//        binding.imageBack.setOnClickListener(v -> {
//            RoomDB.databaseWriteExecutor.execute(() -> {
//                db.missedDao().deleteAll();
//                db.doctorVisitDao().deleteAll();
//                requireActivity().runOnUiThread(() -> requireActivity().finish());
//            });
//        });

        binding.calender.setOnClickListener(v -> showMonthYearPicker(binding.calender));
        hideSystemBars();

        return binding.getRoot();
    }

    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = requireActivity().getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }

    private void setupSearch() {
        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) adapter.getFilter().filter(charSequence.toString());
                binding.searchClearIcon.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.searchClearIcon.setOnClickListener(v -> {
            binding.searchET.setText("");
            binding.searchClearIcon.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(binding.searchET.getWindowToken(), 0);
        });
    }

    private void getJoiningDate() {
        try {
            String SFDCR_Date_sp = SharedPref.getSfDCRDate(requireContext());
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

    private void showHeadquartersPicker() {
        if (hqDialog != null && hqDialog.isShowing()) return;
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            ArrayList<String> list = new ArrayList<>();
            JSONObject object = new JSONObject();
            object.put("name", SharedPref.getSfName(requireContext()));
            object.put("id", SharedPref.getSfCode(requireContext()));
            jsonArray.put(object);
            list.add(object.optString("name", " "));

            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    if (!name.equalsIgnoreCase(SharedPref.getSfName(requireContext()))) {
                        list.add(name);
                    }
                }
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_headquarters_picker, null);
            alertDialog.setView(dialogView);
            TextView headerTxt = dialogView.findViewById(R.id.headerTxt);
            ListView listView = dialogView.findViewById(R.id.listView);
            SearchView searchView = dialogView.findViewById(R.id.searchET);
            headerTxt.setText(getResources().getText(R.string.select_hq));

            ArrayAdapter<String> adapterHQ = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapterHQ);
            hqDialog = alertDialog.create();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) { adapterHQ.getFilter().filter(s); return false; }
                @Override
                public boolean onQueryTextChange(String s) { adapterHQ.getFilter().filter(s); return false; }
            });

            listView.setOnItemClickListener((adapterView, view1, position, l) -> {
                String selectedHq = listView.getItemAtPosition(position).toString();
                binding.headquarters.setText(selectedHq);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.optString("name").equalsIgnoreCase(selectedHq)) {
                            selectedHqId = jsonObject.optString("id", "");
                            if (!selectedDate.isEmpty() && !selectedHqId.isEmpty()) tryFetchReport();
                            break;
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                    binding.emptyMessage.setVisibility(View.GONE);
                }
                hqDialog.dismiss();
            });

            alertDialog.setNegativeButton("Close", (dialog1, which) -> dialog1.dismiss());
            hqDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilityClass.hideKeyboard(requireActivity());
    }

    public void tryFetchReport() {
        if (selectedDate == null || selectedDate.isEmpty()) return;

        if (SharedPref.getSfType(requireContext()).equals("1")) {
            fetchAndLoadMonthlyData(selectedDate, SharedPref.getSfCode(requireContext()));
        } else if (SharedPref.getSfType(requireContext()).equals("2") && selectedHqId != null && !selectedHqId.isEmpty()) {
            fetchAndLoadMonthlyData(selectedDate, selectedHqId);
        }
    }

    public void showMonthYearPicker(TextView monthYearTextView) {
        if (monthDialog != null && monthDialog.isShowing()) return;
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_month_year, null);
        ImageView crossImage = dialogView.findViewById(R.id.crossImage);
        ListView monthYearListView = dialogView.findViewById(R.id.monthYearListView);

        List<String> monthYearList = getCurrentAndPreviousMonths();
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, monthYearList);
        monthYearListView.setAdapter(adapterMonth);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        monthDialog = builder.create();
        monthDialog.setCanceledOnTouchOutside(false);

        crossImage.setOnClickListener(v -> monthDialog.dismiss());
        monthDialog.show();

        monthYearListView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = monthYearList.get(position);
            monthYearTextView.setText(selected);

            try {
                reportList.clear();
                adapter.notifyDataSetChanged();
                binding.recyclerMissedReports.setVisibility(View.GONE);
                binding.headquarters.setText("Select HeadQuarters");

                SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                date = outputFormat.format(inputFormat.parse(selected));
                selectedDate = date;

                binding.outboxEmtyImage.setVisibility(View.GONE);
                binding.recyclerMissedReports.setVisibility(View.VISIBLE);
                binding.emptyMessage.setVisibility(View.GONE);

                if (SharedPref.getSfType(requireContext()).equals("1")) tryFetchReport();
            } catch (ParseException e) { e.printStackTrace(); }

            monthDialog.dismiss();
        });
    }

    private List<String> getCurrentAndPreviousMonths() {
        List<String> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        int joinMonth = Integer.parseInt(JoiningMonth);
        int joinYear = Integer.parseInt(JoiningYear);

        int count = 0;
        while ((cal.get(Calendar.YEAR) > joinYear ||
                (cal.get(Calendar.YEAR) == joinYear && (cal.get(Calendar.MONTH) + 1) >= joinMonth))
                && count < 3) {
            months.add(sdf.format(cal.getTime()));
            cal.add(Calendar.MONTH, -1);
            count++;
        }

        return months;
    }

    private void showLoadingOverlay() { blockingOverlay.setVisibility(View.VISIBLE); }
    private void hideLoadingOverlay() { blockingOverlay.setVisibility(View.GONE); }

    public void fetchAndLoadMonthlyData(String date, String sfcode) {
        RoomDB.databaseWriteExecutor.execute(() -> {
            MissedDao missedDao = db.missedDao();
            String storedJson = missedDao.getMissedValues(sfcode, date);

            requireActivity().runOnUiThread(() -> {
                if (storedJson != null && !storedJson.isEmpty()) {
                    try {
                        JSONArray storedArray = new JSONArray(storedJson);
                        reportList.clear();
                        for (int i = 0; i < storedArray.length(); i++) {
                            JSONObject obj = storedArray.optJSONObject(i);
                            Log.d("MissedAPI", obj.toString());
                            MissedReportItem missedReportItem = new MissedReportItem(
                                    obj.optString("Name"),
                                    obj.optString("Cluster"),
                                    obj.optString("Dcnt", "0"),
                                    obj.optString("Dmet", "0"),
                                    obj.optString("Dmis", "0"),
                                    obj.optString("sf_code")
                            );
                            reportList.add(missedReportItem);
                        }
                        adapter.updateData(reportList);
                        binding.recyclerMissedReports.setVisibility(View.VISIBLE);
                    } catch (JSONException e) { e.printStackTrace(); }
                } else getReportData(date, sfcode);
            });
        });
    }

    public void getReportData(String date, String sfcode) {
//        showLoadingOverlay();
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();
        if (!UtilityClass.isNetworkAvailable(requireContext())) {
//            hideLoadingOverlay();
            progressDialog.dismiss();
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
            return;
        }

        NetworkStatusTask networkStatusTask = new NetworkStatusTask(requireContext(), status -> {
            if (!status) {
//                hideLoadingOverlay();
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.poor_connection));
                return;
            }

            try {
                ApiInterface apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
                jsonObject.put("sfcode", sfcode);
                jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
                jsonObject.put("Rsf", SharedPref.getHqCode(requireContext()));
                jsonObject.put("rptDt", date);
                jsonObject.put("tableName", "getmissedrpt");
                Log.v("getMissedRpt",jsonObject.toString());

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "get/reports");

                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
//                        hideLoadingOverlay();
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                progressDialog.dismiss();
                                JsonElement jsonElement = response.body();
                                if (jsonElement.isJsonArray()) {
                                    JSONArray jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                    if (jsonArray.length() > 0) {
                                        String jsonString = jsonArray.toString();
                                        RoomDB.databaseWriteExecutor.execute(() -> {
                                            MissedDao missedDao = db.missedDao();
                                            missedDao.saveMissedJson(date, sfcode, jsonString);

                                            try {
                                                JSONArray storedArray = new JSONArray(missedDao.getMissedValues(sfcode, date));
                                                reportList.clear();
                                                for (int i = 0; i < storedArray.length(); i++) {
                                                    JSONObject obj = storedArray.optJSONObject(i);
                                                    MissedReportItem missedReportItem = new MissedReportItem(
                                                            obj.optString("Name"),
                                                            obj.optString("Cluster"),
                                                            obj.optString("Dcnt", "0"),
                                                            obj.optString("Dmet", "0"),
                                                            obj.optString("Dmis", "0"),
                                                            obj.optString("sf_code")
                                                    );
                                                    reportList.add(missedReportItem);
                                                }
                                                requireActivity().runOnUiThread(() -> {
                                                    adapter.updateData(reportList);
                                                    binding.recyclerMissedReports.setVisibility(View.VISIBLE);
                                                });
                                            } catch (JSONException e) { e.printStackTrace(); }
                                        });
                                    } else {
                                        requireActivity().runOnUiThread(() -> {
                                            reportList.clear();
                                            adapter.updateData(reportList);
                                            binding.recyclerMissedReports.setVisibility(View.GONE);
                                        });
                                    }
                                }
                            }
                        } catch (JSONException e) { e.printStackTrace(); }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
//                        hideLoadingOverlay();
                        progressDialog.dismiss();
                        requireActivity().runOnUiThread(() ->
                                commonUtilsMethods.showToastMessage(requireContext(), "Failed to load data"));
                    }
                });
            } catch (JSONException e) {
//                hideLoadingOverlay();
                progressDialog.dismiss();
                e.printStackTrace();
            }
        });
        networkStatusTask.execute();
    }

    public void fetchAndLoadData(String date, String sfcode) {
        RoomDB.databaseWriteExecutor.execute(() -> {
            DoctorVisitDao visitDao = db.doctorVisitDao();
            String doctorArrayString = visitDao.getVisitValues(sfcode, date);

            requireActivity().runOnUiThread(() -> {
                if (doctorArrayString != null && !doctorArrayString.isEmpty()) {
                    Intent intent = new Intent(requireActivity(), DoctorVisitActivity.class);
                    intent.putExtra("sfcode", sfcode);
                    intent.putExtra("date", date);
                    startActivity(intent);
                } else getData(date, sfcode);
            });
        });
    }

    public void getData(String date, String sfcode) {
//        showLoadingOverlay();
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();
        if (!UtilityClass.isNetworkAvailable(requireContext())) {
//            hideLoadingOverlay();
            progressDialog.dismiss();
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
            return;
        }

        NetworkStatusTask networkStatusTask = new NetworkStatusTask(requireContext(), status -> {
            if (!status) {
//                hideLoadingOverlay();
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.poor_connection));
                return;
            }

            try {
                ApiInterface apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());
                jsonObject.put("sfcode", sfcode);
                jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
                jsonObject.put("Rsf", SharedPref.getHqCode(requireContext()));
                jsonObject.put("report_date", date);
                jsonObject.put("tableName", "getmissedrptview");
                Log.v("getMissedView",jsonObject.toString());

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "get/reports");

                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
//                        hideLoadingOverlay();
                        progressDialog.dismiss();
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JsonElement jsonElement = response.body();
                                if (jsonElement.isJsonArray()) {
                                    JSONArray jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                    reportList.clear();
                                    for(int i = 0;i<jsonArray.length();i++){
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        DoctorVisitItem model = new DoctorVisitItem();
                                        model.setName(obj.optString("ListedDr_Name"));
                                        model.setCode(obj.optString("ListedDrCode"));
                                        model.setTerritory(obj.optString("territory_Name"));
                                        model.setSpeciality(obj.optString("Doc_Special_SName"));
                                        model.setCategory(obj.optString("Doc_Cat_SName"));
                                        model.setClassName(obj.optString("Doc_ClsSName"));
                                        model.setQualification(obj.optString("Doc_QuaName"));
                                    }

                                    if (jsonArray.length() > 0) {
                                        String arrayAsString = jsonArray.toString();
                                        RoomDB.databaseWriteExecutor.execute(() -> {
                                            DoctorVisitDao visitDao = db.doctorVisitDao();
                                            visitDao.saveVisitJson(sfcode, date, arrayAsString);

                                            Intent intent = new Intent(requireActivity(), DoctorVisitActivity.class);
                                            intent.putExtra("sfcode", sfcode);
                                            intent.putExtra("date", date);
                                            startActivity(intent);
                                        });
                                    }
                                }
                            }
                        } catch (JSONException e) { e.printStackTrace(); }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
//                        hideLoadingOverlay();
                        progressDialog.dismiss();
                        requireActivity().runOnUiThread(() ->
                                commonUtilsMethods.showToastMessage(requireContext(), "Failed to load data"));
                    }
                });
            } catch (JSONException e) {
//                hideLoadingOverlay();
                progressDialog.dismiss();
                e.printStackTrace();
            }
            adapter = new MissedReportAdapter(requireContext(), reportList, (item, position) -> fetchAndLoadData(date, item.getSfCode()));
            binding.recyclerMissedReports.setAdapter(adapter);
            binding.recyclerMissedReports.setVisibility(View.GONE);
            binding.outboxEmtyImage.setVisibility(View.VISIBLE);
        });
        networkStatusTask.execute();
    }
}
