package saneforce.sanclm.activity.reports.dayReport.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.activity.reports.ReportFragContainerActivity;
import saneforce.sanclm.activity.reports.dayReport.DataViewModel;
import saneforce.sanclm.activity.reports.dayReport.DayReportDetailModel;
import saneforce.sanclm.activity.reports.dayReport.DayReportModel;
import saneforce.sanclm.activity.reports.dayReport.adapter.DayReportDetailAdapter;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.FragmentDayReportDetailBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;
import saneforce.sanclm.utility.NetworkStatusTask;
import saneforce.sanclm.utility.TimeUtils;


public class DayReportDetailFragment extends Fragment {

    FragmentDayReportDetailBinding binding;
    DayReportDetailAdapter adapter;
    DayReportModel dayReportModel;
    DataViewModel dataViewModel;
    ApiInterface apiInterface;
    SQLite sqLite;
    LoginResponse loginResponse;
    ArrayList<DayReportDetailModel> arrayOfReportData = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDayReportDetailBinding.inflate(inflater,container,false);
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        initialisation();
        binding.doctor.setSelected(true);
        getData("1", Constants.DOCTOR);

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0) {
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
                if(!view.isSelected()){
                    setSelection(binding.doctor);
                    getData("1",Constants.DOCTOR);
                }
            }
        });

        binding.chemist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()){
                    setSelection(binding.chemist);
                    getData("2",Constants.CHEMIST);
                }
            }
        });

        binding.stockiest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()){
                    setSelection(binding.stockiest);
                    getData("3",Constants.STOCKIEST);
                }
            }
        });

        binding.unDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()){
                    setSelection(binding.unDr);
                    getData("4",Constants.UNLISTED_DOCTOR);
                }
            }
        });

        binding.cip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()){
                    setSelection(binding.cip);
                    getData("5",Constants.CIP);
                }
            }
        });

        binding.hosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isSelected()){
                    setSelection(binding.hosp);
                    getData("6",Constants.HOSPITAL);
                }
            }
        });


        return binding.getRoot();

    }

    public void initialisation(){
        sqLite = new SQLite(getContext());
        loginResponse = sqLite.getLoginData();
            Type type = new TypeToken<DayReportModel>(){}.getType();
            dayReportModel = new Gson().fromJson(dataViewModel.getDetailedData().getValue(), type);

            ReportFragContainerActivity activity = (ReportFragContainerActivity) getActivity();
            String date = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6,TimeUtils.FORMAT_19,dayReportModel.getAdate());
            activity.title.setText("Day Report ( " + date + " )");

            int allCount = Integer.parseInt(dayReportModel.getDrs()) + Integer.parseInt(dayReportModel.getChm()) + Integer.parseInt(dayReportModel.getStk()) +
                    Integer.parseInt(dayReportModel.getHos());
            binding.name.setText(dayReportModel.getTerrWrk());
            binding.allCount.setText(String.valueOf(allCount));
            binding.drCount.setText(dayReportModel.getDrs());
            binding.cheCount.setText(dayReportModel.getChm());
            binding.stkCount.setText(dayReportModel.getStk());
            binding.unDrCount.setText(dayReportModel.getUdr());
            binding.cipCount.setText(dayReportModel.getCip());
            binding.hospCount.setText(dayReportModel.getHos());
            binding.cluster.setText(dayReportModel.getTerrWrk());

    }

    public void setSelection(LinearLayout linearLayout){
        binding.doctor.setSelected(false);
        binding.chemist.setSelected(false);
        binding.stockiest.setSelected(false);
        binding.unDr.setSelected(false);
        binding.cip.setSelected(false);
        binding.hosp.setSelected(false);

        linearLayout.setSelected(true);
    }

    public void getData(String type,String reportOf){
        binding.progressBar.setVisibility(View.VISIBLE);
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(requireContext(), new NetworkStatusTask.NetworkStatusInterface() {
            @Override
            public void isNetworkAvailable(Boolean status) {
                if(status){
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

                        Call<JsonElement> call = apiInterface.getReports(jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                Log.e("test","res : " + response.body());
                                binding.progressBar.setVisibility(View.GONE);
                                try {
                                    if(response.body() != null && response.isSuccessful()){
                                        JSONArray jsonArray = new JSONArray();
                                        if(response.body().isJsonArray()){
                                            jsonArray = new JSONArray(response.body().getAsJsonArray().toString());
                                            Type typeToken = new TypeToken<ArrayList<DayReportDetailModel>>(){}.getType();
                                            arrayOfReportData = new Gson().fromJson(String.valueOf(jsonArray), typeToken);
                                            populateAdapter(arrayOfReportData,reportOf);
                                        }
                                    }
                                }catch (JSONException e){
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        });
                    }catch (JSONException e){
                        throw new RuntimeException(e);
                    }
                }else{
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        networkStatusTask.execute();
    }

    public void populateAdapter(ArrayList<DayReportDetailModel> arrayList,String reportOf){
        if(arrayList.size() > 0)
            binding.noReportFoundTxt.setVisibility(View.GONE);
        else
            binding.noReportFoundTxt.setVisibility(View.VISIBLE);

        adapter = new DayReportDetailAdapter(getContext(),arrayList,reportOf);
        binding.dayReportDetailRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.dayReportDetailRecView.setAdapter(adapter);

    }

}