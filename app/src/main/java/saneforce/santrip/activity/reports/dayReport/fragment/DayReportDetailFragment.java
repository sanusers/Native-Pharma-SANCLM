package saneforce.santrip.activity.reports.dayReport.fragment;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import saneforce.santrip.activity.reports.dayReport.model.DayReportDetailModel;
import saneforce.santrip.activity.reports.dayReport.model.DayReportModel;
import saneforce.santrip.activity.reports.dayReport.adapter.DayReportDetailAdapter;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.FragmentDayReportDetailBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkStatusTask;
import saneforce.santrip.utility.TimeUtils;


public class DayReportDetailFragment extends Fragment {

    FragmentDayReportDetailBinding binding;
    DayReportDetailAdapter adapter;
    DayReportModel dayReportModel;
    DataViewModel dataViewModel;
    ApiInterface apiInterface;
    SQLite sqLite;
    LoginResponse loginResponse;
    ArrayList<DayReportDetailModel> arrayOfReportData = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;

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
                if(charSequence.length()>0) {
                    binding.searchClearIcon.setVisibility(View.VISIBLE);
                }else {
                    binding.searchClearIcon.setVisibility(View.GONE);
                }
                if(adapter != null)
                    adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.searchClearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchET.setText("");
            }
        });

        binding.doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()) {
                    setSelection(binding.doctor);
                    getData("1", Constants.DOCTOR);
                }
            }
        });

        binding.chemist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()) {
                    setSelection(binding.chemist);
                    getData("2", Constants.CHEMIST);
                }
            }
        });

        binding.stockiest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()) {
                    setSelection(binding.stockiest);
                    getData("3", Constants.STOCKIEST);
                }
            }
        });

        binding.unDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()) {
                    setSelection(binding.unDr);
                    getData("4", Constants.UNLISTED_DOCTOR);
                }
            }
        });

        binding.cip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()) {
                    setSelection(binding.cip);
                    getData("5", Constants.CIP);
                }
            }
        });

        binding.hosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()) {
                    setSelection(binding.hosp);
                    getData("6", Constants.HOSPITAL);
                }
            }
        });


        return binding.getRoot();

    }

    public void initialisation() {
        sqLite = new SQLite(getContext());
        loginResponse = sqLite.getLoginData();
        Type type = new TypeToken<DayReportModel>() {
        }.getType();
        dayReportModel = new Gson().fromJson(dataViewModel.getDetailedData().getValue(), type);

        ReportFragContainerActivity activity = (ReportFragContainerActivity) getActivity();
        String date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_19, dayReportModel.getAdate());
        activity.title.setText("Day Report ( " + date + " )");

        int allCount = Integer.parseInt(dayReportModel.getDrs()) + Integer.parseInt(dayReportModel.getChm()) + Integer.parseInt(dayReportModel.getStk()) +
                Integer.parseInt(dayReportModel.getHos());
        binding.name.setText(dayReportModel.getTerrWrk());
        binding.workType.setText(dayReportModel.getWtype());
        binding.cluster.setText(dayReportModel.getTerrWrk());

        binding.workType2.setText(dayReportModel.getHalfDay_FW_Type());
        binding.allCount.setText(String.valueOf(allCount));
        binding.drCount.setText(dayReportModel.getDrs());
        binding.cheCount.setText(dayReportModel.getChm());
        binding.stkCount.setText(dayReportModel.getStk());
        binding.unDrCount.setText(dayReportModel.getUdr());
        binding.cipCount.setText(dayReportModel.getCip());
        binding.hospCount.setText(dayReportModel.getHos());

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
        binding.progressBar.setVisibility(View.VISIBLE);
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(requireContext(), new NetworkStatusTask.NetworkStatusInterface() {
            @Override
            public void isNetworkAvailable(Boolean status) {
                if(status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("tableName", "getvwvstdet");
                        jsonObject.put("ACd", dayReportModel.getACode());
                        jsonObject.put("typ", type);
                        jsonObject.put("sfcode", loginResponse.getSF_Code());
                        jsonObject.put("division_code", loginResponse.getDivision_Code());
                        jsonObject.put("Rsf", loginResponse.getSF_Code());
                        jsonObject.put("sf_type", loginResponse.getSf_type());
                        jsonObject.put("Designation", loginResponse.getDesig());
                        jsonObject.put("state_code", loginResponse.getState_Code());
                        jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/reports");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                Log.e("test", "res : " + response.body());
                                binding.progressBar.setVisibility(View.GONE);
                                try {
                                    if(response.body() != null && response.isSuccessful()) {
                                        JSONArray jsonArray = new JSONArray();
                                        if(response.body().isJsonArray()) {
                                            jsonArray = new JSONArray(response.body().getAsJsonArray().toString());
                                            Type typeToken = new TypeToken<ArrayList<DayReportDetailModel>>() {
                                            }.getType();
                                            arrayOfReportData = new Gson().fromJson(String.valueOf(jsonArray), typeToken);
                                            populateAdapter(arrayOfReportData, reportOf);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    binding.progressBar.setVisibility(View.GONE);
                    commonUtilsMethods.ShowToast(requireContext(),getString(R.string.no_network),100);
                }
            }
        });
        networkStatusTask.execute();
    }

    public void populateAdapter(ArrayList<DayReportDetailModel> arrayList, String reportOf) {
        if(arrayList.size()>0)
            binding.noReportFoundTxt.setVisibility(View.GONE);
        else
            binding.noReportFoundTxt.setVisibility(View.VISIBLE);

        adapter = new DayReportDetailAdapter(getContext(), arrayList, reportOf);
        binding.dayReportDetailRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.dayReportDetailRecView.setAdapter(adapter);

    }

}