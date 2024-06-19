package saneforce.sanzen.activity.call.profile.preCallAnalysis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.profile.CustomerProfile;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentPrecallAnalysisBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;


public class PreCallAnalysisFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentPrecallAnalysisBinding preCallAnalysisBinding;
    public static ApiInterface apiInterface;
    public static String PrdSamNeed, PrdRxNeed, RCPANeed, CallFeedbackNeed;
    public static PreCallAnalysisAdapter adapter;
    public static ArrayList<PreCallAnalysisModelClass> ProductList = new ArrayList<>();
    static List<DCRLastVisitDetails> dcrLastVstDetails = new ArrayList<>();
    static DCRLastVisitDetails.VstDate vstDate;

    CommonUtilsMethods commonUtilsMethods;

    public static void CallPreCallAPI(Context context, Activity activity) {
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
            json.put("sfcode", SharedPref.getSfCode(context));
            json.put("division_code", SharedPref.getDivisionCode(context));
            json.put("Rsf", SharedPref.getHqCode(context));
            json.put("sf_type", SharedPref.getSfType(context));
            json.put("Designation", SharedPref.getDesig(context));
            json.put("state_code", SharedPref.getStateCode(context));
            json.put("subdivision_code", SharedPref.getSubdivisionCode(context));
            json.put("versionNo", context.getString(R.string.app_version));
            json.put("mod", Constants.APP_MODE);
            json.put("Device_version", Build.VERSION.RELEASE);
            json.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            json.put("AppName", context.getString(R.string.str_app_name));
            json.put("language", SharedPref.getSelectedLanguage(context));
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
                            dcrLastVstDetails.add(new DCRLastVisitDetails(json.getString("CustCode"), vstDate, json.getString("Prod_Samp"), json.getString("Prod_Det"), json.getString("Inputs"), json.getString("FeedbkCd"), json.getString("Feedbk"), json.getString("Remks"), json.getString("AMSLNo")));

                            assert dcrLastVstDetails != null;
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
                                HiddenVisibleFunction();
//                                if (PrdSamNeed.equalsIgnoreCase("0")) {
//                                    preCallAnalysisBinding.sampleCaption.setVisibility(View.VISIBLE);
//                                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.VISIBLE);
//                                } else {
//                                    preCallAnalysisBinding.sampleCaption.setVisibility(View.GONE);
//                                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.GONE);
//                                }
//                                if (PrdRxNeed.equalsIgnoreCase("0")) {
//                                    preCallAnalysisBinding.rxCaption.setVisibility(View.VISIBLE);
//                                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.VISIBLE);
//                                } else {
//                                    preCallAnalysisBinding.rxCaption.setVisibility(View.GONE);
//                                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.GONE);
//                                }
//                                if (RCPANeed.equalsIgnoreCase("1")) {
//                                    preCallAnalysisBinding.rcpaCaption.setVisibility(View.VISIBLE);
//                                    preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.VISIBLE);
//                                } else {
//                                    preCallAnalysisBinding.rcpaCaption.setVisibility(View.GONE);
//                                    preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.GONE);
//                                }
                               /* prdDetails = dcrLastVstDetails.get(0).getProdSamp().replace("#", " , ");
                                prdDetails = prdDetails.replace("~", "-");
                                prdDetails = prdDetails.replace("$", "-");
                                prdDetails = prdDetails.replace("^", "-");
                                preCallAnalysisBinding.tvPrdPromoted.setText(prdDetails);*/

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
                        } else {
                            preCallAnalysisBinding.tvPrdPromoted.setText(R.string.no_prds_promoted);
                            preCallAnalysisBinding.tvPrdPromoted.setVisibility(View.VISIBLE);
                            preCallAnalysisBinding.productTableList.setVisibility(View.GONE);
                            preCallAnalysisBinding.recyelerview.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        Log.v("preCall", "--error--" + e);
                        e.printStackTrace();
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
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void dataSplite(String inputString) {
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
            if(item.length>4){
                ProductList.add(new PreCallAnalysisModelClass(item[0].trim(), item[1], item[2], Rcpa, item[4]));
            } else if(item.length>3) {
                ProductList.add(new PreCallAnalysisModelClass(item[0].trim(), item[1], item[2], Rcpa, ""));
            }
            adapter.notifyDataSetChanged();
        }
    }

    public static void HiddenVisibleFunction() {
        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()) {
            case "1":

                if (PrdSamNeed.equalsIgnoreCase("1")) {
                    preCallAnalysisBinding.sampleCaption.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.sampleCaption.setVisibility(View.GONE);
                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.GONE);
                }
                if (PrdRxNeed.equalsIgnoreCase("1")) {
                    preCallAnalysisBinding.rxCaption.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.rxCaption.setVisibility(View.GONE);
                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.GONE);
                }
                if (RCPANeed.equalsIgnoreCase("0")) {
                    preCallAnalysisBinding.rcpaCaption.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.rcpaCaption.setVisibility(View.GONE);
                    preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.GONE);
                }
                if (CallFeedbackNeed.equalsIgnoreCase("0")) {
                    preCallAnalysisBinding.tvTagFeedback.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.tvFeedback.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.tvTagFeedback.setVisibility(View.GONE);
                    preCallAnalysisBinding.tvFeedback.setVisibility(View.GONE);
                }
                break;
            case "2":
                if (PrdSamNeed.equalsIgnoreCase("1")) {
                    preCallAnalysisBinding.sampleCaption.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.sampleCaption.setVisibility(View.GONE);
                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.GONE);
                }
                if (PrdRxNeed.equalsIgnoreCase("0")) {
                    preCallAnalysisBinding.rxCaption.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.rxCaption.setVisibility(View.GONE);
                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.GONE);
                }
                if (RCPANeed.equalsIgnoreCase("0")) {
                    preCallAnalysisBinding.rcpaCaption.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.rcpaCaption.setVisibility(View.GONE);
                    preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.GONE);
                }
                if (CallFeedbackNeed.equalsIgnoreCase("0")) {
                    preCallAnalysisBinding.tvTagFeedback.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.tvFeedback.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.tvTagFeedback.setVisibility(View.GONE);
                    preCallAnalysisBinding.tvFeedback.setVisibility(View.GONE);
                }
                break;
            case "3":
            case "4":
            case "5":
            case "6":
                if (PrdSamNeed.equalsIgnoreCase("0")) {
                    preCallAnalysisBinding.sampleCaption.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.sampleCaption.setVisibility(View.GONE);
                    preCallAnalysisBinding.sampleCaptionLine.setVisibility(View.GONE);
                }
                if (PrdRxNeed.equalsIgnoreCase("0")) {
                    preCallAnalysisBinding.rxCaption.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.rxCaption.setVisibility(View.GONE);
                    preCallAnalysisBinding.rxCaptionLine.setVisibility(View.GONE);
                }
                if (CallFeedbackNeed.equalsIgnoreCase("0")) {
                    preCallAnalysisBinding.tvTagFeedback.setVisibility(View.VISIBLE);
                    preCallAnalysisBinding.tvFeedback.setVisibility(View.VISIBLE);
                } else {
                    preCallAnalysisBinding.tvTagFeedback.setVisibility(View.GONE);
                    preCallAnalysisBinding.tvFeedback.setVisibility(View.GONE);
                }
                preCallAnalysisBinding.rcpaCaption.setVisibility(View.GONE);
                preCallAnalysisBinding.rcpaCaptionLine.setVisibility(View.GONE);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preCallAnalysisBinding = FragmentPrecallAnalysisBinding.inflate(inflater);
        View v = preCallAnalysisBinding.getRoot();
        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        getRequiredData();
        // CallPreCallAPI();
        ProductList.clear();
        adapter = new PreCallAnalysisAdapter(ProductList, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        preCallAnalysisBinding.recyelerview.setNestedScrollingEnabled(false);
        preCallAnalysisBinding.recyelerview.setHasFixedSize(true);
        preCallAnalysisBinding.recyelerview.setLayoutManager(manager);
        preCallAnalysisBinding.recyelerview.setAdapter(adapter);
        return v;
    }

    private void getRequiredData() {
        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()) {
            case "1":
                PrdSamNeed = SharedPref.getDrSampNd(requireContext());
                PrdRxNeed =  SharedPref.getDrRxNd(requireContext());
                RCPANeed =  SharedPref.getRcpaNd(requireContext());
                CallFeedbackNeed = SharedPref.getDfNeed(requireContext());
                if (SharedPref.getDocProductCaption(requireContext()).isEmpty() || SharedPref.getDocProductCaption(requireContext()).equalsIgnoreCase(null)){
                    preCallAnalysisBinding.prodCaption.setText("Product");
                }else{
                    preCallAnalysisBinding.prodCaption.setText(SharedPref.getDocProductCaption(requireContext()));
                }
                if (SharedPref.getDocInputCaption(requireContext()).isEmpty() || SharedPref.getDocInputCaption(requireContext()).equalsIgnoreCase(null)){
                    preCallAnalysisBinding.tvTagInputs.setText("Inputs");
                }
                else {
                    preCallAnalysisBinding.tvTagInputs.setText(SharedPref.getDocInputCaption(requireContext()));
                }
                break;
            case "2":
                PrdSamNeed = SharedPref.getChmsamqtyNeed(requireContext());
                PrdRxNeed = SharedPref.getChmRxQty(requireContext());;//1
                RCPANeed = SharedPref.getChmRcpaNeed(requireContext());
                CallFeedbackNeed = SharedPref.getCfNeed(requireContext());
                if (SharedPref.getChmProductCaption(requireContext()).isEmpty() || SharedPref.getChmProductCaption(requireContext()).equalsIgnoreCase(null)){
                    preCallAnalysisBinding.prodCaption.setText("Product");
                }else{
                    preCallAnalysisBinding.prodCaption.setText(SharedPref.getChmProductCaption(requireContext()));
                }
                if (SharedPref.getChmInputCaption(requireContext()).isEmpty() || SharedPref.getChmInputCaption(requireContext()).equalsIgnoreCase(null)){
                    preCallAnalysisBinding.tvTagInputs.setText("Inputs");
                }
                else {
                    preCallAnalysisBinding.tvTagInputs.setText(SharedPref.getChmInputCaption(requireContext()));
                }
                break;
            case "3":
                PrdSamNeed = "0";
                PrdRxNeed = SharedPref.getStkPobNeed(requireContext());
                RCPANeed = "0";
                CallFeedbackNeed = SharedPref.getSfNeed(requireContext());
                if (SharedPref.getStkProductCaption(requireContext()).isEmpty() || SharedPref.getStkProductCaption(requireContext()).equalsIgnoreCase(null)){
                    preCallAnalysisBinding.prodCaption.setText("Product");
                }else{
                    preCallAnalysisBinding.prodCaption.setText(SharedPref.getStkProductCaption(requireContext()));
                }
                if (SharedPref.getStkInputCaption(requireContext()).isEmpty() || SharedPref.getStkInputCaption(requireContext()).equalsIgnoreCase(null)){
                    preCallAnalysisBinding.tvTagInputs.setText("Inputs");
                }
                else {
                    preCallAnalysisBinding.tvTagInputs.setText(SharedPref.getStkInputCaption(requireContext()));
                }
                break;
            case "4":
                PrdRxNeed = SharedPref.getUlPobNeed(requireContext());
                PrdSamNeed = "0";
                RCPANeed = "0";
                CallFeedbackNeed = SharedPref.getNfNeed(requireContext());
                if (SharedPref.getUlProductCaption(requireContext()).isEmpty() || SharedPref.getUlProductCaption(requireContext()).equalsIgnoreCase(null)){
                    preCallAnalysisBinding.prodCaption.setText("Product");
                }else{
                    preCallAnalysisBinding.prodCaption.setText(SharedPref.getUlProductCaption(requireContext()));
                }
                if (SharedPref.getUlInputCaption(requireContext()).isEmpty() || SharedPref.getUlInputCaption(requireContext()).equalsIgnoreCase(null)){
                    preCallAnalysisBinding.tvTagInputs.setText("Inputs");
                }
                else {
                    preCallAnalysisBinding.tvTagInputs.setText(SharedPref.getUlInputCaption(requireContext()));
                }
                break;
            case "5":
                CallFeedbackNeed = SharedPref.getCipFNeed(requireContext());
            case "6":
                PrdSamNeed = "0";
                PrdRxNeed = "0";
                RCPANeed = "0";
                CallFeedbackNeed = SharedPref.getHfNeed(requireContext());
                break;
        }

      /*  if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1")) {
            PrdSamNeed = loginResponse.getDrSampNd();
            PrdRxNeed = loginResponse.getDrRxNd();
            RCPANeed = loginResponse.getRcpaNd();
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")) {
            PrdSamNeed = loginResponse.getChmsamQty_need();
            PrdRxNeed = loginResponse.getChmRxQty();//1
            RCPANeed = loginResponse.getChm_RCPA_Need();
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            PrdSamNeed = "0";
            PrdRxNeed = loginResponse.getStk_Pob_Need();
            RCPANeed = "0";
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4")) {
            PrdSamNeed = "0";
            PrdRxNeed = loginResponse.getUl_Pob_Need();
            RCPANeed = "0";
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("5")) {
            PrdSamNeed = "0";
            PrdRxNeed = "0";
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("6")) {
            PrdSamNeed = "0";
            PrdRxNeed = "0";
        }*/
    }
}