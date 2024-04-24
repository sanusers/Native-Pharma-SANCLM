package saneforce.santrip.activity.reports.dayReport.fragment;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.reports.ReportFragContainerActivity;
import saneforce.santrip.activity.reports.dayReport.DataViewModel;
import saneforce.santrip.activity.reports.dayReport.adapter.DayReportDetailAdapter;
import saneforce.santrip.activity.reports.dayReport.model.DayReportDetailModel;
import saneforce.santrip.activity.reports.dayReport.model.DayReportModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.FragmentDayReportDetailBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkStatusTask;
import saneforce.santrip.utility.TimeUtils;


public class DayReportDetailFragment extends Fragment {
    public static String callCheckInOutNeed;
    FragmentDayReportDetailBinding binding;
    DayReportDetailAdapter adapter;
    DayReportModel dayReportModel;
    DataViewModel dataViewModel;
    ApiInterface apiInterface;
//    SQLite sqLite;

    ArrayList<DayReportDetailModel> arrayOfReportData = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDayReportDetailBinding.inflate(inflater, container, false);
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        initialisation();
        binding.doctor.setSelected(true);
        getData("1", Constants.DOCTOR);

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    binding.searchClearIcon.setVisibility(View.VISIBLE);
                } else {
                    binding.searchClearIcon.setVisibility(View.GONE);
                }
                if (adapter != null) adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.searchClearIcon.setOnClickListener(view -> binding.searchET.setText(""));

        binding.doctor.setOnClickListener(view -> {
            if (!view.isSelected()) {
                setSelection(binding.doctor);
                getData("1", Constants.DOCTOR);
            }
        });

        binding.all.setOnClickListener(v -> {

        });

        binding.chemist.setOnClickListener(view -> {
            if (!view.isSelected()) {
                setSelection(binding.chemist);
                getData("2", Constants.CHEMIST);
            }
        });

        binding.stockiest.setOnClickListener(view -> {
            if (!view.isSelected()) {
                setSelection(binding.stockiest);
                getData("3", Constants.STOCKIEST);
            }
        });

        binding.unDr.setOnClickListener(view -> {
            if (!view.isSelected()) {
                setSelection(binding.unDr);
                getData("4", Constants.UNLISTED_DOCTOR);
            }
        });

        binding.cip.setOnClickListener(view -> {
            if (!view.isSelected()) {
                setSelection(binding.cip);
                getData("5", Constants.CIP);
            }
        });

        binding.hosp.setOnClickListener(view -> {
            if (!view.isSelected()) {
                setSelection(binding.hosp);
                getData("6", Constants.HOSPITAL);
            }
        });


        return binding.getRoot();

    }

    public void initialisation() {
//        sqLite = new SQLite(getContext());

        Type type = new TypeToken<DayReportModel>() {
        }.getType();
        dayReportModel = new Gson().fromJson(dataViewModel.getDetailedData().getValue(), type);

        ReportFragContainerActivity activity = (ReportFragContainerActivity) getActivity();
        String date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_19, dayReportModel.getAdate());
        assert activity != null;
        activity.title.setText(String.format("Day Report ( %s )", date));
        int drCount = 0, chmCount = 0, stkCount = 0, undrCount = 0, cipCount = 0, hosCount = 0;

        if (SharedPref.getDrNeed(requireContext()).equalsIgnoreCase("0")) {
            binding.drLayout.setVisibility(View.VISIBLE);
            drCount = Integer.parseInt(dayReportModel.getDrs());
            binding.drCount.setText(dayReportModel.getDrs());
        }
        if (SharedPref.getChmNeed(requireContext()).equalsIgnoreCase("0")) {
            binding.cheLayout.setVisibility(View.VISIBLE);
            chmCount = Integer.parseInt(dayReportModel.getChm());
            binding.cheCount.setText(dayReportModel.getChm());
        }
        if (SharedPref.getStkNeed(requireContext()).equalsIgnoreCase("0")) {
            binding.stkLayout.setVisibility(View.VISIBLE);
            stkCount = Integer.parseInt(dayReportModel.getStk());
            binding.stkCount.setText(dayReportModel.getStk());
        }
        if (SharedPref.getUnlNeed(requireContext()).equalsIgnoreCase("0")) {
            binding.unDrLayout.setVisibility(View.VISIBLE);
            cipCount = Integer.parseInt(dayReportModel.getUdr());
            binding.unDrCount.setText(dayReportModel.getUdr());
        }
        if (SharedPref.getCipNeed(requireContext()).equalsIgnoreCase("0")) {
            binding.cipLayout.setVisibility(View.VISIBLE);
            undrCount = Integer.parseInt(dayReportModel.getCip());
            binding.cipCount.setText(dayReportModel.getCip());
        }
        if (SharedPref.getHospNeed(requireContext()).equalsIgnoreCase("0")) {
            binding.hospLayout.setVisibility(View.VISIBLE);
            hosCount = Integer.parseInt(dayReportModel.getCip());
            binding.hospCount.setText(dayReportModel.getHos());
        }

        binding.name.setText(dayReportModel.getSF_Name());
        binding.workType.setText(dayReportModel.getWtype());
        binding.cluster.setText(dayReportModel.getTerrWrk());

        binding.workType2.setText(dayReportModel.getHalfDay_FW_Type());
        binding.allCount.setText(String.valueOf(drCount + chmCount + stkCount + cipCount + undrCount + hosCount));
    }

    public void setSelection(LinearLayout linearLayout) {
        binding.doctor.setSelected(false);
        binding.chemist.setSelected(false);
        binding.stockiest.setSelected(false);
        binding.unDr.setSelected(false);
        binding.cip.setSelected(false);
        binding.hosp.setSelected(false);

        linearLayout.setSelected(true);
    }

    public void getData(String type, String reportOf) {
        progressDialog = CommonUtilsMethods.createProgressDialog(requireContext());
        if (UtilityClass.isNetworkAvailable(requireContext())) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(requireContext(), status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("tableName", "getvwvstdet");
                        jsonObject.put("ACd", dayReportModel.getACode());
                        jsonObject.put("typ", type);
                        jsonObject.put("sfcode", SharedPref.getSfCode(requireContext()));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
                        jsonObject.put("Rsf", dayReportModel.getSF_Code());
                        jsonObject.put("sf_type", SharedPref.getSfType(requireContext()));
                        jsonObject.put("Designation", SharedPref.getDesig(requireContext()));
                        jsonObject.put("state_code", SharedPref.getStateCode(requireContext()));
                        jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(requireContext()));

                        Log.d("paramObject",jsonObject.toString());
                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/reports");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                Log.e("test", "res : " + response.body());
                                progressDialog.dismiss();
                                try {
                                    if (response.body() != null && response.isSuccessful()) {
                                        JSONArray jsonArray = new JSONArray();
                                        if (response.body().isJsonArray()) {
                                            jsonArray = new JSONArray(response.body().getAsJsonArray().toString());
                                            Type typeToken = new TypeToken<ArrayList<DayReportDetailModel>>() {
                                            }.getType();
                                            arrayOfReportData = new Gson().fromJson(String.valueOf(jsonArray), typeToken);
                                            populateAdapter(arrayOfReportData, reportOf, type);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.toast_response_failed));
                                progressDialog.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.poor_connection));
                }
            });
            networkStatusTask.execute();
        } else {
            progressDialog.dismiss();
            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
        }
    }

    public void populateAdapter(ArrayList<DayReportDetailModel> arrayList, String reportOf, String type) {
        if (arrayList.size() > 0) binding.noReportFoundTxt.setVisibility(View.GONE);
        else binding.noReportFoundTxt.setVisibility(View.VISIBLE);


        switch (type) {
            case "1":
                callCheckInOutNeed = SharedPref.getCustSrtNd(requireContext());
                break;
            case "2":
                callCheckInOutNeed = SharedPref.getChmSrtNd(requireContext());
                break;
            case "4":
                callCheckInOutNeed = SharedPref.getUnlistSrtNd(requireContext());
                break;
            case "5":
                callCheckInOutNeed = SharedPref.getCipSrtNd(requireContext());
                break;
            default:
                callCheckInOutNeed = "1";
                break;
        }

        adapter = new DayReportDetailAdapter(getContext(), arrayList, reportOf, callCheckInOutNeed, SharedPref.getNextVst(requireContext()),dayReportModel.getACode(),dayReportModel.getSF_Code());
        binding.dayReportDetailRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.dayReportDetailRecView.setAdapter(adapter);
    }
}