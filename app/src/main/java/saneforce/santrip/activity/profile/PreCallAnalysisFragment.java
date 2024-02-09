package saneforce.santrip.activity.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.FragmentPrecallAnalysisBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;


public class PreCallAnalysisFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentPrecallAnalysisBinding preCallAnalysisBinding;
    public static ApiInterface apiInterface;
    public static String SfName, SfType, SfCode, RSFCode, DivCode, Designation, StateCode, SubDivisionCode, prdDetails;
    static List<DCRLastVisitDetails> dcrLastVstDetails = new ArrayList<>();
    static DCRLastVisitDetails.VstDate vstDate;
    LoginResponse loginResponse;
    CommonUtilsMethods commonUtilsMethods;
    SQLite sqLite;

    public static void CallPreCallAPI(Context context,Activity activity) {
        JSONObject json = new JSONObject();
        try {
            json.put("tableName", "getcuslvst");
            if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
                json.put("typ", "D");
            } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
                json.put("typ", "C");
            } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
                json.put("typ", "S");
            } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
                json.put("typ", "U");
            }
            json.put("CusCode", DCRCallActivity.CallActivityCustDetails.get(0).getCode());
            json.put("sfcode", SfCode);
            json.put("division_code", DivCode);
            json.put("Rsf", RSFCode);
            json.put("sf_type", SfType);
            json.put("Designation", Designation);
            json.put("state_code", StateCode);
            json.put("subdivision_code", SubDivisionCode);
            Log.v("json_cus_l_visit", json.toString());
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "table/additionaldcrmasterdata");
        Call<JsonElement> dcrLastCallDetails = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, json.toString());
        dcrLastCallDetails.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        CustomerProfile.progressDialog.dismiss();
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        CustomerProfile.isPreAnalysisCalled = true;
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            JSONObject json = jsonArray.getJSONObject(0);
                            JSONObject jsonObject = json.getJSONObject("Vst_Date");
                            dcrLastVstDetails.clear();
                            vstDate = new DCRLastVisitDetails.VstDate();
                            vstDate.setDate(jsonObject.getString("date"));
                            dcrLastVstDetails.add(new DCRLastVisitDetails(json.getString("CustCode"), vstDate,
                                    json.getString("Prod_Samp"), json.getString("Prod_Det"), json.getString("Inputs"), json.getString("FeedbkCd"), json.getString("Feedbk"), json.getString("Remks"), json.getString("AMSLNo")));

                            assert dcrLastVstDetails != null;
                            if (dcrLastVstDetails.get(0).getProdSamp().isEmpty()) {
                                preCallAnalysisBinding.tvPrdPromoted.setText(R.string.no_prds_promoted);
                            } else {
                                prdDetails = dcrLastVstDetails.get(0).getProdSamp().replace("#", " , ");
                                prdDetails = prdDetails.replace("~", "-");
                                prdDetails = prdDetails.replace("$", "-");
                                prdDetails = prdDetails.replace("^", "-");
                                preCallAnalysisBinding.tvPrdPromoted.setText(prdDetails);
                            }

                            if (dcrLastVstDetails.get(0).getInputs().equalsIgnoreCase("( 0 ),") && !dcrLastVstDetails.get(0).getInputs().isEmpty()) {
                                preCallAnalysisBinding.tvInputs.setText(R.string.no_inputs);
                            } else {
                                preCallAnalysisBinding.tvInputs.setText(dcrLastVstDetails.get(0).getInputs());
                            }

                            if (dcrLastVstDetails.get(0).getVstDate().getDate().isEmpty()) {
                                preCallAnalysisBinding.tvLastVisit.setText(R.string.no_visit_date);
                            } else {
                                preCallAnalysisBinding.tvLastVisit.setText(dcrLastVstDetails.get(0).getVstDate().getDate());
                            }

                            if (dcrLastVstDetails.get(0).getFeedbk().isEmpty()) {
                                preCallAnalysisBinding.tvFeedback.setText(R.string.no_feedback);
                            } else {
                                preCallAnalysisBinding.tvFeedback.setText(dcrLastVstDetails.get(0).getFeedbk());
                            }

                            if (dcrLastVstDetails.get(0).getRemks().isEmpty()) {
                                preCallAnalysisBinding.tvRemark.setText(R.string.no_remarks);
                            } else {
                                preCallAnalysisBinding.tvRemark.setText(dcrLastVstDetails.get(0).getRemks());
                            }
                        }

                    } catch (Exception e) {
                        Log.v("preCall","--error--" + e);
                        CustomerProfile.progressDialog.dismiss();
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                CustomerProfile.progressDialog.dismiss();
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });


     /*   Call<List<DCRLastVisitDetails>> dcrLastCallDetails = apiInterface.LastVisitDetails(String.valueOf(json));
        dcrLastCallDetails.enqueue(new Callback<List<DCRLastVisitDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<DCRLastVisitDetails>> call, @NonNull Response<List<DCRLastVisitDetails>> response) {
                if (response.isSuccessful()) {
                    try {
                        CustomerProfile.progressDialog.dismiss();
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        CustomerProfile.isPreAnalysisCalled = true;
                        List<DCRLastVisitDetails> dcrLastVstDetails = response.body();
                        assert dcrLastVstDetails != null;
                        if (dcrLastVstDetails.get(0).getProdSamp().isEmpty()) {
                            preCallAnalysisBinding.tvPrdPromoted.setText(R.string.no_prds_promoted);
                        } else {
                            prdDetails = dcrLastVstDetails.get(0).getProdSamp().replace("#", " , ");
                            prdDetails = prdDetails.replace("~", "-");
                            prdDetails = prdDetails.replace("$", "-");
                            prdDetails = prdDetails.replace("^", "-");
                            preCallAnalysisBinding.tvPrdPromoted.setText(prdDetails);
                        }

                        if (dcrLastVstDetails.get(0).getInputs().equalsIgnoreCase("( 0 ),")) {
                            preCallAnalysisBinding.tvInputs.setText(R.string.no_inputs);
                        } else {
                            preCallAnalysisBinding.tvInputs.setText(dcrLastVstDetails.get(0).getInputs());
                        }

                        if (dcrLastVstDetails.get(0).getVstDate().getDate().isEmpty()) {
                            preCallAnalysisBinding.tvLastVisit.setText(R.string.no_visit_date);
                        } else {
                            preCallAnalysisBinding.tvLastVisit.setText(dcrLastVstDetails.get(0).getVstDate().getDate());
                        }

                        if (dcrLastVstDetails.get(0).getFeedbk().isEmpty()) {
                            preCallAnalysisBinding.tvFeedback.setText(R.string.no_feedback);
                        } else {
                            preCallAnalysisBinding.tvFeedback.setText(dcrLastVstDetails.get(0).getFeedbk());
                        }

                        if (dcrLastVstDetails.get(0).getRemks().isEmpty()) {
                            preCallAnalysisBinding.tvRemark.setText(R.string.no_remarks);
                        } else {
                            preCallAnalysisBinding.tvRemark.setText(dcrLastVstDetails.get(0).getRemks());
                        }


                    } catch (Exception e) {
                        CustomerProfile.progressDialog.dismiss();
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DCRLastVisitDetails>> call, @NonNull Throwable t) {
                CustomerProfile.progressDialog.dismiss();
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preCallAnalysisBinding = FragmentPrecallAnalysisBinding.inflate(inflater);
        View v = preCallAnalysisBinding.getRoot();
        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        getRequiredData();
        // CallPreCallAPI();
        return v;
    }

    private void getRequiredData() {
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        SfType = loginResponse.getSf_type();
        SfCode = loginResponse.getSF_Code();
        if (SfType.equalsIgnoreCase("1")) {
            RSFCode = loginResponse.getSF_Code();
        } else {
            RSFCode = SharedPref.getTodayDayPlanSfCode(requireContext());
        }
        SfName = loginResponse.getSF_Name();
        DivCode = loginResponse.getDivision_Code();
        SubDivisionCode = loginResponse.getSubdivision_code();
        Designation = loginResponse.getDesig();
        StateCode = loginResponse.getState_Code();
    }
}