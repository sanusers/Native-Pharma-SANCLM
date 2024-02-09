package saneforce.santrip.activity.homeScreen.adapters;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SampleValidation;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.isFromActivity;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Chemist_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Doctor_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Stockiest_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.callAnalysisBinding;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.cip_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.hos_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.unlistered_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.Designation;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.DivCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.InputValidation;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.SfCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.SfType;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.StateCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.SubDivisionCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.TodayPlanSfCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.binding;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment;
import saneforce.santrip.activity.homeScreen.fragment.CallsFragment;
import saneforce.santrip.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.santrip.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.activity.previewPresentation.adapter.PreviewAdapter;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkStatusTask;


public class Call_adapter extends RecyclerView.Adapter<Call_adapter.listDataViewholider> {
    Context context;
    ArrayList<CallsModalClass> list;
    ApiInterface apiInterface;
    ProgressDialog progressBar;
    SQLite sqLite;
    Dialog dialogTransparent;
    CommonUtilsMethods commonUtilsMethods;

    public Call_adapter(Context context, ArrayList<CallsModalClass> list, ApiInterface apiInterface) {
        this.context = context;
        this.list = list;
        this.apiInterface = apiInterface;
        sqLite = new SQLite(context);
        commonUtilsMethods = new CommonUtilsMethods(context);
        dialogTransparent = new Dialog(context, android.R.style.Theme_Black);
        View view = LayoutInflater.from(context).inflate(
                R.layout.remove_border_progress, null);
        dialogTransparent.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialogTransparent.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogTransparent.setContentView(view);
    }

    @NonNull
    @Override
    public listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calls_item_view, parent, false);
        return new listDataViewholider(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listDataViewholider holder, int position) {

        CallsModalClass callslist = list.get(position);
        holder.DocName.setText(callslist.getDocName());
        holder.datetime.setText(callslist.getCallsDateTime());

        String type = callslist.getDocNameID();

        if (type.equalsIgnoreCase("1")) {
            holder.imageView.setImageResource(R.drawable.map_dr_img);
        } else if (type.equalsIgnoreCase("2")) {
            holder.imageView.setImageResource(R.drawable.map_chemist_img);
        } else if (type.equalsIgnoreCase("3")) {
            holder.imageView.setImageResource(R.drawable.map_stockist_img);
        } else if (type.equalsIgnoreCase("4")) {
            holder.imageView.setImageResource(R.drawable.map_unlistdr_img);
        } else if (type.equalsIgnoreCase("5")) {
            holder.imageView.setImageResource(R.drawable.map_cip_img);
        } else if (type.equalsIgnoreCase("6")) {
            holder.imageView.setImageResource(R.drawable.tp_hospital_icon);
        }

        holder.menu.setOnClickListener((View.OnClickListener) v -> {
            if (UtilityClass.isNetworkAvailable(context)) {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                popup.inflate(R.menu.call_menu);
                popup.setOnMenuItemClickListener(menuItem -> {
                        if (menuItem.getItemId() == R.id.menuEdit) {
                            CallEditAPI(callslist.getTrans_Slno(), callslist.getADetSLNo(), callslist.getDocName(), callslist.getDocCode(), callslist.getDocNameID());
                        } else if (menuItem.getItemId() == R.id.menuDelete) {
                            try {
                                dialogTransparent.show();
                                CallDeleteAPI(callslist.getTrans_Slno(), callslist.getADetSLNo(), callslist.getDocNameID(), callslist.getCallsDateTime().substring(0, 10), callslist.getDocCode());

                                JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(callslist.getCallsDateTime().substring(0, 10)) && jsonObject.getString("CustCode").equalsIgnoreCase(callslist.getDocCode())) {
                                        jsonArray.remove(i);
                                        break;
                                    }
                                }

                                sqLite.saveMasterSyncData(Constants.DCR, jsonArray.toString(), 0);
                                sqLite.deleteLineChart(callslist.getDocCode(), callslist.getCallsDateTime().substring(0, 10));

                                switch (callslist.getDocNameID()) {
                                    case "1":
                                        int doc_current_callcount = sqLite.getcurrentmonth_calls_count("1");
                                        callAnalysisBinding.txtDocCount.setText(String.format("%d / %d", doc_current_callcount, Doctor_list.length()));
                                        break;
                                    case "2":
                                        int che_current_callcount = sqLite.getcurrentmonth_calls_count("2");
                                        callAnalysisBinding.txtCheCount.setText(String.format("%d / %d", che_current_callcount, Chemist_list.length()));
                                        break;
                                    case "3":
                                        int stockiest_current_callcount = sqLite.getcurrentmonth_calls_count("3");
                                        callAnalysisBinding.txtStockCount.setText(String.format("%d / %d", stockiest_current_callcount, Stockiest_list.length()));
                                        break;
                                    case "4":
                                        int unlistered_current_callcount = sqLite.getcurrentmonth_calls_count("4");
                                        callAnalysisBinding.txtUnlistCount.setText(String.format("%d / %d", unlistered_current_callcount, unlistered_list.length()));
                                        break;
                                    case "5":
                                        int cip_current_callcount = sqLite.getcurrentmonth_calls_count("5");
                                        callAnalysisBinding.txtCipCount.setText(String.format("%d / %d", cip_current_callcount, cip_list.length()));
                                        break;
                                    case "6":
                                        int hos_current_callcount = sqLite.getcurrentmonth_calls_count("6");
                                        callAnalysisBinding.txtHosCount.setText(String.format("%d / %d", hos_current_callcount, hos_list.length()));
                                        break;
                                }


                                // CallAnalysisFragment.SetcallDetailsInLineChart(sqLite, context);
                                new CountDownTimer(300, 300) {
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        removeAt(holder.getAbsoluteAdapterPosition());
                                        dialogTransparent.dismiss();
                                    }
                                }.start();

                            } catch (Exception e) {
                                Log.v("sdsd", e.toString());
                                dialogTransparent.dismiss();
                            }
                        }
                    return true;
                });
                popup.show();
            } else {
                commonUtilsMethods.ShowToast(context, context.getString(R.string.no_network), 100);
            }
        });
    }

    private void CallDeleteAPI(String TranslNo, String aDetSLNo, String type, String date, String docCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sfcode", SfCode);
            jsonObject.put("division_code", DivCode);
            jsonObject.put("Rsf", TodayPlanSfCode);
            jsonObject.put("sf_type", SfType);
            jsonObject.put("Designation", Designation);
            jsonObject.put("state_code", StateCode);
            jsonObject.put("subdivision_code", SubDivisionCode);
            jsonObject.put("amc", aDetSLNo);
            jsonObject.put("CusType", type);
            jsonObject.put("sample_validation", CallsFragment.SampleValidation);
            jsonObject.put("input_validation", InputValidation);
            Log.v("delCall", jsonObject.toString());
        } catch (Exception e) {
            Log.v("delCall", e.toString());
        }

        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));


        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "delete/dcr");
        Call<ResponseBody> deleteCall = apiInterface.getResponseBody(SharedPref.getCallApiUrl(context), mapString,jsonObject.toString());
        deleteCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        // JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.v("delCall", response.toString());
                        JSONArray jsonArray = new JSONArray(SharedPref.getTodayCallList(context));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            if (json.getString("Trans_SlNo").equalsIgnoreCase(TranslNo) && json.getString("ADetSLNo").equalsIgnoreCase(aDetSLNo)) {
                                Log.v("delCall", json.getString("CustName"));
                                jsonArray.remove(i);
                            }
                        }
                        SharedPref.setTodayCallList(context, jsonArray.toString());


                     /*   JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(date) && jsonObject.getString("CustCode").equalsIgnoreCase(docCode)) {
                                jsonArray.remove(i);
                                break;
                            }
                        }
                        sqLite.saveMasterSyncData(Constants.DCR, jsonArray.toString(), 0);
                        CallAnalysisFragment.SetcallDetailsInLineChart(sqLite,context);*/
                        //   Toast.makeText(context, "Call Deleted Successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.v("delCall", e.toString());
                    }
                } else {
                    commonUtilsMethods.ShowToast(context, context.getString(R.string.toast_response_failed), 100);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                commonUtilsMethods.ShowToast(context, context.getString(R.string.response_failed_please_sync), 100);
            }
        });
    }

    private void CallEditAPI(String transSlno, String aDetSLNo, String docName, String docCode, String type) {
        progressBar = CommonUtilsMethods.createProgressDialog(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("headerno", transSlno);
            jsonObject.put("detno", aDetSLNo);
            jsonObject.put("sfcode", SfCode);
            jsonObject.put("division_code", DivCode);
            jsonObject.put("Rsf", TodayPlanSfCode);
            jsonObject.put("sf_type", SfType);
            jsonObject.put("Designation", Designation);
            jsonObject.put("state_code", StateCode);
            jsonObject.put("subdivision_code", SubDivisionCode);
            jsonObject.put("cusname", docName);
            jsonObject.put("custype", docCode);
            jsonObject.put("pob", "1");
            Log.v("editCall", jsonObject.toString());

        } catch (Exception e) {
            Log.v("editCall", e.toString());
        }
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));


        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "edit/dcr");
        Call<JsonElement> getEditCallDetails = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonObject.toString());
/*        Call<JsonObject> getEditCallDetails = null;
        getEditCallDetails = apiInterface.getEditCallDetails(jsonObject.toString());*/

        getEditCallDetails.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        progressBar.dismiss();
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.v("editCall", jsonObject.toString());
                        Intent intent = new Intent(context, DCRCallActivity.class);
                        CallActivityCustDetails = new ArrayList<>();
                        CallActivityCustDetails.add(0, new CustList(docName, docCode, type, transSlno, aDetSLNo,"", jsonObject.toString()));
                        intent.putExtra("isDetailedRequired", "false");
                        intent.putExtra("from_activity", "edit_online");
                        context.startActivity(intent);
                    } catch (Exception e) {
                        progressBar.dismiss();
                        Log.v("editCall", e.toString());
                    }
                } else {
                    progressBar.dismiss();
                    commonUtilsMethods.ShowToast(context, context.getString(R.string.toast_response_failed), 100);

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressBar.dismiss();
                commonUtilsMethods.ShowToast(context, context.getString(R.string.toast_response_failed), 100);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
        binding.txtCallcount.setText(String.valueOf(list.size()));

    }

    public static class listDataViewholider extends RecyclerView.ViewHolder {
        TextView DocName, datetime, menu;
        CircleImageView imageView;

        public listDataViewholider(@NonNull View itemView) {
            super(itemView);
            DocName = itemView.findViewById(R.id.textViewLabel1);
            datetime = itemView.findViewById(R.id.textViewLabel2);
            imageView = itemView.findViewById(R.id.profile_icon);
            menu = itemView.findViewById(R.id.optionview);
        }
    }
}