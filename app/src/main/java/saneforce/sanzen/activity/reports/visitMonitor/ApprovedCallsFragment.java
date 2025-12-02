package saneforce.sanzen.activity.reports.visitMonitor;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.function.ObjIntConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.missedReport.MissedReportItem;
import saneforce.sanzen.activity.reports.visitMonitor.adapter.ApprovedCallsAdapter;
import saneforce.sanzen.activity.reports.visitMonitor.model.VisitStatsModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.reports.ReportsActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityVisitMonitorBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;
import saneforce.sanzen.utility.TimeUtils;

public class ApprovedCallsFragment extends Fragment {
    private RoomDB roomDB;
    MasterDataDao masterDataDao;

    private RecyclerView recyclerView;
    ApiInterface apiInterface;
    private AlertDialog monthDialog;
    private AlertDialog hqDialog;
    public  String JoiningDate, JoiningMonth, JoiningYear;
    final List<VisitStatsModel> reportList = new ArrayList<>();

    JSONObject jsonObject = new JSONObject();
    ApprovedCallsAdapter adapter;
    private String selectedDate = "";
    private String selectedHqId = "";
    private String date = "";
    CommonUtilsMethods commonUtilsMethods;
    private TextView noReport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_approved_calls, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new ApprovedCallsAdapter(requireContext(),reportList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        TextView calender = view.findViewById(R.id.calender);
        TextView headquarters = view.findViewById(R.id.headquarters_visit);
        EditText searchCust = view.findViewById(R.id.search_cust);
        noReport = view.findViewById(R.id.noReportFoundTxt);
        noReport.setVisibility(View.VISIBLE);
        if(SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")){
            headquarters.setVisibility(View.VISIBLE);
            headquarters.setOnClickListener(v -> {
                if (selectedDate == null || selectedDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select month", Toast.LENGTH_SHORT).show();
                    return;
                }
                showHeadquartersPicker();
            });
        }else{
            headquarters.setVisibility(View.GONE);
        }
        searchCust.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        getJoiningDate();

        calender.setOnClickListener(v -> showMonthYearPicker(view.findViewById(R.id.calender)));
        hideSystemBars();
//        headquarters.setOnClickListener(view1 -> showHeadquartersPicker());
        return view;

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

                TextView headquarters = requireView().findViewById(R.id.headquarters_visit);
                headquarters.setText(selectedHq);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.optString("name").equalsIgnoreCase(selectedHq)) {
                            selectedHqId = jsonObject.optString("id", "");
                            break;
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                }
                getVisitData();
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

//        if (SharedPref.getSfType(requireContext()).equals("1")) {
//            fetchAndLoadMonthlyData(selectedDate, SharedPref.getSfCode(requireContext()));
//        } else if (SharedPref.getSfType(requireContext()).equals("2") && selectedHqId != null && !selectedHqId.isEmpty()) {
//            fetchAndLoadMonthlyData(selectedDate, selectedHqId);
//        }
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
            if(SharedPref.getSfType(requireContext()).equalsIgnoreCase("2")){
                commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.please_select_headquarters));
                noReport.setVisibility(View.VISIBLE);
            }else {
                noReport.setVisibility(View.GONE);
                getVisitData();
            }
            try {
                reportList.clear();
                adapter.notifyDataSetChanged();


                SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                date = outputFormat.format(inputFormat.parse(selected));
                selectedDate = date;

//                binding.outboxEmtyImage.setVisibility(View.GONE);
//                binding.recyclerMissedReports.setVisibility(View.VISIBLE);
//                binding.emptyMessage.setVisibility(View.GONE);

//                if (SharedPref.getSfType(requireContext()).equals("1")) tryFetchReport();
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

    public void getVisitData() {

        if (UtilityClass.isNetworkAvailable(requireContext())) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(requireContext(), status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                        jsonObject = CommonUtilsMethods.CommonObjectParameter(requireContext());

                        String sfCodeToUse = selectedHqId.isEmpty() ? SharedPref.getSfCode(requireContext()) : selectedHqId;

                        jsonObject.put("sfcode", sfCodeToUse);
                        jsonObject.put("division_code",SharedPref.getDivisionCode(requireContext()));
                        jsonObject.put("Rsf",sfCodeToUse);
                        String selected = ((TextView) getView().findViewById(R.id.calender)).getText().toString();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(inputFormat.parse(selected));
                        jsonObject.put("month", cal.get(Calendar.MONTH) + 1);
                        jsonObject.put("year", cal.get(Calendar.YEAR));
                        jsonObject.put("tableName","getvisitmonitor_zen");
                        Log.v("TAG" ,jsonObject.toString() );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/reports");

                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(requireContext()), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {

                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                try {
                                    if (response.isSuccessful() && response.body() != null) {
                                        JsonElement jsonElement = response.body();

                                        if (jsonElement.isJsonArray()) {
                                            JSONArray jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                            reportList.clear();

                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject obj = jsonArray.getJSONObject(i);
                                                VisitStatsModel model = new VisitStatsModel();
                                                model.setName(obj.optString("FieldForce Name"));
                                                model.setHq(obj.optString("HQ"));
                                                model.setDesignation(obj.optString("Designation Name"));
                                                model.setTotalCustomers(obj.optString("Total_Listed_Drs").replace("-","0"));
                                                model.setVisitedCustomers(obj.optString("Doctors_Met").replace("-","0"));
                                                model.setMissedCustomers(obj.optString("Listed_Drs_Missed").replace("-","0"));
                                                model.setFwDays(obj.optString("No_Of_Field_Wrk_Days").replace("-","0"));
                                                model.setCallAvg(obj.optString("Call_Average"));
                                                model.setCoverage(obj.optString("Coverage_Per"));
                                                reportList.add(model);
                                            }

                                            adapter.notifyDataSetChanged();
                                            noReport.setVisibility(View.GONE);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                                commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.something_went_wrong_please_try_again));

                            }

                        });
                }else{
                    commonUtilsMethods.showToastMessage(requireContext(),getString(R.string.poor_network_connection));

                }

            });

            networkStatusTask.execute();
        }
    }
}
