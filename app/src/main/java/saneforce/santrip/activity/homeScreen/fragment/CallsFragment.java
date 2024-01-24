package saneforce.santrip.activity.homeScreen.fragment;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.activity.homeScreen.adapters.Call_adapter;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.adapter.detailing.PaintView;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.santrip.commonClasses.CommonSharedPreference;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.CallsFragmentBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;


public class CallsFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static CallsFragmentBinding binding;
    Call_adapter adapter;
    ApiInterface apiInterface;
    public static String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, TodayPlanSfCode, SampleValidation, InputValidation;
    LoginResponse loginResponse;
    List<CallsModalClass> TodayCallList = new ArrayList<>();
    SQLite sqLite;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CallsFragmentBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        sqLite = new SQLite(requireContext());

        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        getRequiredData();
        CallTodayCallsAPI();

        binding.rlSyncCall.setOnClickListener(v12 -> {
            if (UtilityClass.isNetworkAvailable(context)) {
                CallTodayCallsAPI();
            } else {
                Toast.makeText(requireContext(), "No Network Available!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvAddCall.setOnClickListener(view -> startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class)));

        return v;
    }

    private void CallTodayCallsAPI() {
        TodayCallList.clear();
        if (UtilityClass.isNetworkAvailable(context)) {
            try {
                apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", "gettodycalls");
                jsonObject.put("sfcode", SfCode);
                jsonObject.put("ReqDt", CommonUtilsMethods.getCurrentDate());
                jsonObject.put("division_code", DivCode);
                jsonObject.put("Rsf", TodayPlanSfCode);
                jsonObject.put("sf_type", SfType);
                jsonObject.put("Designation", Designation);
                jsonObject.put("state_code", StateCode);
                jsonObject.put("subdivision_code", SubDivisionCode);
                Log.v("TodayCalls", "--json--" + jsonObject);

                Call<JsonArray> getTodayCalls = null;
                getTodayCalls = apiInterface.getTodayCalls(jsonObject.toString());

                getTodayCalls.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                        if (response.isSuccessful()) {
                            try {
                                assert response.body() != null;
                                JSONArray jsonArray = new JSONArray(response.body().toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    TodayCallList.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));
                                }

                                binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
                                SharedPref.setTodayCallList(requireContext(), response.body().toString());

                                adapter = new Call_adapter(requireContext(), TodayCallList, apiInterface);
                                LinearLayoutManager manager = new LinearLayoutManager(requireContext());
                                binding.recyelerview.setNestedScrollingEnabled(false);
                                binding.recyelerview.setHasFixedSize(true);
                                binding.recyelerview.setLayoutManager(manager);
                                binding.recyelerview.setAdapter(adapter);

                            } catch (Exception e) {
                                Log.v("TodayCalls", "--error--" + e);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.v("TodayCalls", "--error--2--" + e);
            }
        } else {
            try {
                JSONArray jsonArray = new JSONArray(SharedPref.getTodayCallList(requireContext()));
                TodayCallList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    TodayCallList.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));
                }
                binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
                adapter = new Call_adapter(requireContext(), TodayCallList, apiInterface);
                LinearLayoutManager manager = new LinearLayoutManager(requireContext());
                binding.recyelerview.setNestedScrollingEnabled(false);
                binding.recyelerview.setHasFixedSize(true);
                binding.recyelerview.setLayoutManager(manager);
                binding.recyelerview.setAdapter(adapter);
            } catch (Exception e) {

            }
        }
    }

    private void getRequiredData() {
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        SfType = loginResponse.getSf_type();
        SfCode = loginResponse.getSF_Code();
        SfName = loginResponse.getSF_Name();
        DivCode = loginResponse.getDivision_Code();
        SubDivisionCode = loginResponse.getSubdivision_code();
        Designation = loginResponse.getDesig();
        StateCode = loginResponse.getState_Code();
        SampleValidation = loginResponse.getSample_validation();
        InputValidation = loginResponse.getInput_validation();
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(requireContext());
    }
}