package saneforce.santrip.activity.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
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
    PreCallAnalysisAdapter adapter;
    LoginResponse loginResponse;
    SQLite sqLite;

    public static String RCPANeed ;
    ArrayList<PreCallAnalysisModelClass> ProductList=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preCallAnalysisBinding = FragmentPrecallAnalysisBinding.inflate(inflater);
        View v = preCallAnalysisBinding.getRoot();
        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        sqLite = new SQLite(requireContext());
        getRequiredData();
        CallPreCallAPI(getActivity());



        adapter=new PreCallAnalysisAdapter(ProductList,getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        preCallAnalysisBinding.recyelerview.setNestedScrollingEnabled(false);
        preCallAnalysisBinding.recyelerview.setHasFixedSize(true);
        preCallAnalysisBinding.recyelerview.setLayoutManager(manager);
        preCallAnalysisBinding.recyelerview.setAdapter(adapter);

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
        RCPANeed = loginResponse.getChm_RCPA_Need();
    }


    public  void CallPreCallAPI(Activity activity) {
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

        Call<List<DCRLastVisitDetails>> dcrLastCallDetails = apiInterface.LastVisitDetails(String.valueOf(json));
        dcrLastCallDetails.enqueue(new Callback<List<DCRLastVisitDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<DCRLastVisitDetails>> call, @NonNull Response<List<DCRLastVisitDetails>> response) {
                Log.e("Reponse:Resmas",  " :" + response);
                Log.e("Reponse",  " :" + response .body());
                if (response.isSuccessful()) {
                    try {
                        //   CustomerProfile.progressDialog.dismiss();
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        CustomerProfile.isPreAnalysisCalled = true;
                        List<DCRLastVisitDetails> dcrLastVstDetails = response.body();
                        assert dcrLastVstDetails != null;
                        if(response.body().isEmpty()){
                            preCallAnalysisBinding.tvPrdPromoted.setText(R.string.no_prds_promoted);
                            preCallAnalysisBinding.tvPrdPromoted.setVisibility(View.VISIBLE);
                            preCallAnalysisBinding.productTableList.setVisibility(View.GONE);
                            preCallAnalysisBinding.recyelerview.setVisibility(View.GONE);

                        }else{
                            if (dcrLastVstDetails.get(0).getProdSamp().isEmpty()) {
                                preCallAnalysisBinding.tvPrdPromoted.setText(R.string.no_prds_promoted);
                                preCallAnalysisBinding.tvPrdPromoted.setVisibility(View.VISIBLE);
                                preCallAnalysisBinding.productTableList.setVisibility(View.GONE);
                                preCallAnalysisBinding.recyelerview.setVisibility(View.GONE);
                            } else {

                                preCallAnalysisBinding.tvPrdPromoted.setVisibility(View.GONE);
                                preCallAnalysisBinding.productTableList.setVisibility(View.VISIBLE);
                                preCallAnalysisBinding.recyelerview.setVisibility(View.VISIBLE);

                                dataSplite(dcrLastVstDetails.get(0).getProdSamp());

                                if(DCRCallActivity.PrdSamNeed.equalsIgnoreCase("0")){
                                    preCallAnalysisBinding.sampleCaption.setVisibility(View.VISIBLE);
                                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.VISIBLE);
                                }else {
                                    preCallAnalysisBinding.sampleCaption.setVisibility(View.GONE);
                                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.GONE);
                                }
                                if(DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")){
                                    preCallAnalysisBinding.rxCaption.setVisibility(View.VISIBLE);
                                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.VISIBLE);
                                }else {
                                    preCallAnalysisBinding.rxCaption.setVisibility(View.GONE);
                                    preCallAnalysisBinding.rxCaption.setVisibility(View.GONE);
                                }
                                if (RCPANeed.equalsIgnoreCase("0")) {
                                    preCallAnalysisBinding.rcpaCaption.setVisibility(View.VISIBLE);
                                    preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.VISIBLE);
                                }else {
                                    preCallAnalysisBinding.rcpaCaption.setVisibility(View.GONE);
                                    preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.GONE);

                                }

                            }

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
                        //   CustomerProfile.progressDialog.dismiss();
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DCRLastVisitDetails>> call, @NonNull Throwable t) {
                //  CustomerProfile.progressDialog.dismiss();
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });
    }

    public void dataSplite(String inputString) {
        ProductList.clear();

        String str = inputString.replace(")", "");
        String[] separated = str.split(",");

        for (String s : separated) {
            String[] item = s.split("[(]");
            String Rcpa = item[3];
            if (item[3].contains("^")) {
                String[] rcpa = item[3].replace("^", ",").split("[,]");
                Rcpa = rcpa[1];
            }
            ProductList.add(new PreCallAnalysisModelClass(item[0].trim(), item[1], item[2], Rcpa, item[4]));
            adapter.notifyDataSetChanged();

        }
    }
}
