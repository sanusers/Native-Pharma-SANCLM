package saneforce.santrip.activity.homeScreen.adapters;

import static saneforce.santrip.activity.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Chemist_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Doctor_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Stockiest_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.callAnalysisBinding;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.cip_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.hos_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.unlistered_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.binding;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.call.DCRCallActivity;
import saneforce.santrip.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.roomdatabase.CallDataRestClass;
import saneforce.santrip.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SharedPref;


public class Call_adapter extends RecyclerView.Adapter<Call_adapter.listDataViewholider> {
    Context context;
    ArrayList<CallsModalClass> list;
    ApiInterface apiInterface;
    ProgressDialog progressBar;
    Dialog dialogTransparent;
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB db;
    private static MasterDataDao masterDataDao;
    private static CallTableDao callTableDao;

    public Call_adapter(Context context, ArrayList<CallsModalClass> list, ApiInterface apiInterface) {
        this.context = context;
        this.list = list;
        this.apiInterface = apiInterface;
        commonUtilsMethods = new CommonUtilsMethods(context);
        db = RoomDB.getDatabase(context);
        masterDataDao =db.masterDataDao();
        callTableDao = db.callTableDao();
        dialogTransparent = new Dialog(context, android.R.style.Theme_Black);
        View view = LayoutInflater.from(context).inflate(R.layout.remove_border_progress, null);
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

        holder.menu.setOnClickListener(v -> {
            if (UtilityClass.isNetworkAvailable(context)) {

                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popupMenu = new PopupMenu(wrapper, v, Gravity.END);
                popupMenu.getMenuInflater().inflate(R.menu.call_online_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.menuEdit) {
                        CallEditAPI(callslist.getTrans_Slno(), callslist.getADetSLNo(), callslist.getDocName(), callslist.getDocCode(), callslist.getDocNameID());
                    } else if (menuItem.getItemId() == R.id.menuDelete) {


                        Dialog   dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dcr_cancel_alert);
                        dialog.setCancelable(false);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        TextView btn_yes=dialog.findViewById(R.id.btn_yes);
                        TextView btn_no=dialog.findViewById(R.id.btn_no);
                        TextView titte=dialog.findViewById(R.id.ed_alert_msg);
                        titte.setText("Are You Sure To Delete ?");

                        btn_yes.setOnClickListener(view12 -> {
                            dialog.dismiss();
                            try {
                                dialogTransparent.show();

                                CallDeleteAPI(callslist.getTrans_Slno(), callslist.getADetSLNo(), callslist.getDocNameID(), callslist.getCallsDateTime().substring(0, 10), callslist.getDocCode());
                                String mMdata= masterDataDao.getDataByKey(Constants.CALL_SYNC);
                                JSONArray jsonArray = new JSONArray(mMdata);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(callslist.getCallsDateTime().substring(0, 10)) && jsonObject.getString("CustCode").equalsIgnoreCase(callslist.getDocCode())) {
                                        jsonArray.remove(i);
                                        break;
                                    }
                                }
                                MasterDataTable data = new MasterDataTable();
                                data.setMasterKey(Constants.CALL_SYNC);
                                data.setMasterValues(jsonArray.toString());
                                data.setSyncStatus(0);
                                MasterDataTable mNChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
                                if (mNChecked != null) {
                                    masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
                                } else {
                                    masterDataDao.insert(data);

                                }
                                CallDataRestClass.resetcallValues(context);


                                new CountDownTimer(250, 250) {
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        removeAt(holder.getAbsoluteAdapterPosition());
                                        dialogTransparent.dismiss();
                                    }
                                }.start();

                            } catch (Exception e) {
                                Log.v("DeleteCall", "---" + e);
                                dialogTransparent.dismiss();
                            }

                        });

                        btn_no.setOnClickListener(view12 -> {
                            dialog.dismiss();
                        });



                    }
                    return true;
                });
                popupMenu.show();
            } else {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void AssignCallAnalysis(String sfType, String docNameID) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        switch (docNameID) {
            case "1":
                String doc_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth), "1"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtDocCount.setText(String.format("%s / %d", doc_current_callcount, Doctor_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(doc_current_callcount);
                }
                break;
            case "2":
                String che_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth),"2"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtDocCount.setText(String.format("%s / %d", che_current_callcount, Chemist_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(che_current_callcount);
                }
                break;
            case "3":
                String stockiest_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth),"3"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtStockCount.setText(String.format("%s / %d", stockiest_current_callcount, Stockiest_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(stockiest_current_callcount);
                }
                break;
            case "4":
                String unlistered_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth),"4"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtUnlistCount.setText(String.format("%s / %d", unlistered_current_callcount, unlistered_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(unlistered_current_callcount);
                }
                break;
            case "5":
                String cip_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth),"5"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtCipCount.setText(String.format("%s / %d", cip_current_callcount, cip_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(cip_current_callcount);
                }
                break;
            case "6":
                String hos_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth),"6"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtHosCount.setText(String.format("%s / %d", hos_current_callcount, hos_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(hos_current_callcount);
                }
                break;
        }
    }

    private void CallDeleteAPI(String TranslNo, String aDetSLNo, String type, String date, String docCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sfcode",  SharedPref.getSfCode(context));
            jsonObject.put("division_code",  SharedPref.getDivisionCode(context));
            jsonObject.put("Rsf",  SharedPref.getHqCode(context));
            jsonObject.put("sf_type",  SharedPref.getSfType(context));
            jsonObject.put("Designation",  SharedPref.getDesig(context));
            jsonObject.put("state_code",  SharedPref.getStateCode(context));
            jsonObject.put("subdivision_code",  SharedPref.getSubdivisionCode(context));
            jsonObject.put("amc", aDetSLNo);
            jsonObject.put("CusType", type);
            jsonObject.put("sample_validation", SharedPref.getSampleValidation(context));
            jsonObject.put("input_validation",  SharedPref.getInputValidation(context));
            Log.v("delCall", jsonObject.toString());
        } catch (Exception e) {
            Log.v("delCall", e.toString());
        }

        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));


        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "delete/dcr");
        Call<ResponseBody> deleteCall = apiInterface.getResponseBody(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
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
                        commonUtilsMethods.showToastMessage(context, "Call Deleted");


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
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.response_failed_please_sync));
            }
        });
    }

    private void CallEditAPI(String transSlno, String aDetSLNo, String docName, String docCode, String type) {
        progressBar = CommonUtilsMethods.createProgressDialog(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("headerno", transSlno);
            jsonObject.put("detno", aDetSLNo);
            jsonObject.put("sfcode",  SharedPref.getSfCode(context));
            jsonObject.put("division_code",  SharedPref.getDivisionCode(context));
            jsonObject.put("Rsf",  SharedPref.getHqCode(context));
            jsonObject.put("sf_type",  SharedPref.getSfType(context));
            jsonObject.put("Designation",  SharedPref.getDesig(context));
            jsonObject.put("state_code",  SharedPref.getStateCode(context));
            jsonObject.put("subdivision_code",  SharedPref.getSubdivisionCode(context));
            jsonObject.put("cusname", docName);
            jsonObject.put("custype", type);
            jsonObject.put("pob", "1");
            Log.v("editCall", jsonObject.toString());

        } catch (Exception e) {
            Log.v("editCall", e.toString());
        }
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));


        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "edit/dcr");
        Call<JsonElement> getEditCallDetails = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());
        getEditCallDetails.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.v("editCall", jsonObject.toString());
                        Intent intent = new Intent(context, DCRCallActivity.class);
                        CallActivityCustDetails = new ArrayList<>();
                        CallActivityCustDetails.add(0, new CustList(docName, docCode, type, transSlno, aDetSLNo, "", jsonObject.toString()));
                        intent.putExtra(Constants.DETAILING_REQUIRED, "false");
                        intent.putExtra(Constants.DCR_FROM_ACTIVITY, "edit_online");
                        intent.putExtra("remainder_save", "0");
                        intent.putExtra("hq_code", "" );

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        new CountDownTimer(2000, 2000) {
                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                progressBar.dismiss();
                            }
                        }.start();
                    } catch (Exception e) {
                        progressBar.dismiss();
                        Log.v("editCall", e.toString());
                    }
                } else {
                    progressBar.dismiss();
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressBar.dismiss();
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
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