package saneforce.sanclm.activity.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.databinding.FragmentPrecallAnalysisBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;


public class PreCallAnalysisFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentPrecallAnalysisBinding precallAnalysisBinding;
    public static ApiInterface apiInterface;
    public static String SfName, SfType, SfCode, RSFCode, DivCode, Designation, StateCode, SubDivisionCode, pdtDtls;
    LoginResponse loginResponse;
    SQLite sqLite;

    public static void CallPreCallAPI() {
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
        } catch (Exception e) {

        }

        ArrayList<String> prdNames = new ArrayList<>();
        Call<List<DCRLastVisitDetails>> dcrlastcallDtls = apiInterface.LastVisitDetails(String.valueOf(json));
        dcrlastcallDtls.enqueue(new Callback<List<DCRLastVisitDetails>>() {
            @Override
            public void onResponse(Call<List<DCRLastVisitDetails>> call, Response<List<DCRLastVisitDetails>> response) {
                if (response.isSuccessful()) {
                    try {
                        CustomerProfile.progressDialog.dismiss();
                        CustomerProfile.isPreAnalysisCalled = true;
                        List<DCRLastVisitDetails> dcrLastvstDtls = response.body();
                        if (dcrLastvstDtls.get(0).getProdSamp().isEmpty()) {
                            precallAnalysisBinding.tvPrdPromoted.setText("No Products Promoted");
                        } else {
                            pdtDtls = dcrLastvstDtls.get(0).getProdSamp().replace("#", " , ");
                            pdtDtls = pdtDtls.replace("~", "-");
                            pdtDtls = pdtDtls.replace("$", "-");
                            pdtDtls = pdtDtls.replace("^", "-");
                            precallAnalysisBinding.tvPrdPromoted.setText(pdtDtls);
                         /*   pdtDtls = extractValues(dcrLastvstDtls.get(0).getProdSamp());
                            String[] separated = pdtDtls.split(",");
                            Collections.addAll(prdNames, separated);*/

                           /* ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout.listview_items, prdNames);
                            precallAnalysisBinding.lvTagPromotedPrd.setAdapter(adapter);*/
                        }

                        if (dcrLastvstDtls.get(0).getInputs().equalsIgnoreCase("( 0 ),")) {
                            precallAnalysisBinding.tvInputs.setText("No Inputs");
                        } else {
                            precallAnalysisBinding.tvInputs.setText(dcrLastvstDtls.get(0).getInputs());
                        }

                        if (dcrLastvstDtls.get(0).getVstDate().getDate().isEmpty()) {
                            precallAnalysisBinding.tvLastVisit.setText("No Visit Date");
                        } else {
                            precallAnalysisBinding.tvLastVisit.setText(dcrLastvstDtls.get(0).getVstDate().getDate());
                        }

                        if (dcrLastvstDtls.get(0).getFeedbk().isEmpty()) {
                            precallAnalysisBinding.tvFeedback.setText("No Feedback");
                        } else {
                            precallAnalysisBinding.tvFeedback.setText(dcrLastvstDtls.get(0).getFeedbk());
                        }

                        if (dcrLastvstDtls.get(0).getRemks().isEmpty()) {
                            precallAnalysisBinding.tvRemark.setText("No Remarks");
                        } else {
                            precallAnalysisBinding.tvRemark.setText(dcrLastvstDtls.get(0).getRemks());
                        }


                    } catch (Exception e) {
                        CustomerProfile.progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DCRLastVisitDetails>> call, Throwable t) {
                CustomerProfile.progressDialog.dismiss();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        precallAnalysisBinding = FragmentPrecallAnalysisBinding.inflate(inflater);
        View v = precallAnalysisBinding.getRoot();
        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        sqLite = new SQLite(requireContext());
        getRequiredData();
        // CallPreCallAPI();
        return v;
    }

    private void getRequiredData() {
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData(true);

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

    public String extractValues(String s) {
        if (TextUtils.isEmpty(s)) return "";

        String[] clstarrrayqty = s.split(",");
        Log.v("DcrDetail_extract", "products--000--" + clstarrrayqty[0] + "----" + clstarrrayqty[1]);
        StringBuilder ss1 = new StringBuilder();

        for (String value : clstarrrayqty) {
            Log.v("DcrDetail_extract", "product_inputs_qty--111--" + value);
            ss1.append(value.substring(0, value.indexOf("(")));
            Log.v("DcrDetail_extract", "product_inputs_qty--222--" + ss1);
        }
        String finalValue = "";
        finalValue = ss1.substring(0, ss1.length() - 1);
        Log.v("DcrDetail_extract", "product_inputs_qty--333--" + finalValue);
        return finalValue;
    }
}
