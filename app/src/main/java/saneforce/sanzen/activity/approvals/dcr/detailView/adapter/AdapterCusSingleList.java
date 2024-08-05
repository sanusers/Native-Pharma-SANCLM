package saneforce.sanzen.activity.approvals.dcr.detailView.adapter;

import static saneforce.sanzen.activity.approvals.dcr.detailView.DcrDetailViewActivity.dcrDetailModelLists;
import static saneforce.sanzen.activity.approvals.dcr.detailView.DcrDetailViewActivity.dcrDetailViewBinding;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanzen.activity.approvals.dcr.DcrApprovalActivity;
import saneforce.sanzen.activity.approvals.dcr.detailView.DcrDetailViewActivity;
import saneforce.sanzen.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanzen.activity.call.pojo.input.SaveCallInputList;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.activity.reports.dayReport.adapter.DayReportSlideDetailsAdapter;
import saneforce.sanzen.activity.reports.dayReport.adapter.ReoportRcpaAdapter;
import saneforce.sanzen.activity.reports.dayReport.model.DayReportRcpaModelClass;
import saneforce.sanzen.activity.reports.dayReport.model.EventCaptureModelClass;
import saneforce.sanzen.activity.reports.dayReport.model.SlideRatingDetalisModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;

public class AdapterCusSingleList extends RecyclerView.Adapter<AdapterCusSingleList.ViewHolder> {
    public static ArrayList<SaveCallProductList> ProductList;
    public static ArrayList<SaveCallInputList> InputList;
    Context context;
    ArrayList<DcrDetailModelList> dcrApprovalNames;
    ArrayList<SaveCallProductList> ProductListNew;
    ArrayList<SaveCallInputList> InputListNew;
    ProductAdapter productAdapter;
    ArrayList<EventCaptureModelClass> EventCaptureData = new ArrayList<>();
    ArrayList<DayReportRcpaModelClass> rcpaList = new ArrayList<>();
    InputAdapter inputAdapter;
    CommonUtilsMethods commonUtilsMethods;
    OnItemClickListenerApproval listenerApproval;
    ProgressDialog progressDialog;
    ApiInterface apiInterface;
    EventDetailsCapture adapter;
    String category;

    ArrayList<SlideRatingDetalisModelClass> callDetailingLists=new ArrayList<>();
    public AdapterCusSingleList(Context context, ArrayList<DcrDetailModelList> dcrApprovalNames, OnItemClickListenerApproval listenerApproval,String category) {
        this.context = context;
        this.dcrApprovalNames = dcrApprovalNames;
        this.listenerApproval = listenerApproval;
        this.category = category;
    }

    @NonNull
    @Override
    public AdapterCusSingleList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_appr_list, parent, false);
        return new AdapterCusSingleList.ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull AdapterCusSingleList.ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        if (DcrDetailViewActivity.SelectedCode.equalsIgnoreCase(dcrApprovalNames.get(position).getCode())) {
            ProductListNew = new ArrayList<>();
            InputListNew = new ArrayList<>();
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_purple));
            holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.white));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_box));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.greater_than_white));

            //ProductAdapter
            for (int i = 0; i < ProductList.size(); i++) {
                if (ProductList.get(i).getCode().equalsIgnoreCase(dcrApprovalNames.get(position).getName())) {
                    ProductListNew.add(new SaveCallProductList(ProductList.get(i).getCode(), ProductList.get(i).getName(), ProductList.get(i).getSample_qty(), ProductList.get(i).getRx_qty(), ProductList.get(i).getRcpa_qty(), ProductList.get(i).getPromoted()));
                }
            }

            Log.v("size", "--prd--" + ProductListNew.size());
            if (ProductListNew.size() > 0) {
                dcrDetailViewBinding.constraintMainSample.setVisibility(View.VISIBLE);
                productAdapter = new ProductAdapter(context, ProductListNew,dcrApprovalNames.get(position).getTypeCust());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                dcrDetailViewBinding.rvSamplePrd.setLayoutManager(mLayoutManager);
                commonUtilsMethods.recycleTestWithDivider(dcrDetailViewBinding.rvSamplePrd);
                dcrDetailViewBinding.rvSamplePrd.setNestedScrollingEnabled(false);
                dcrDetailViewBinding.rvSamplePrd.setAdapter(productAdapter);
            } else {
                dcrDetailViewBinding.constraintMainSample.setVisibility(View.GONE);
            }

            //InputAdapter
            for (int j = 0; j < InputList.size(); j++) {
                if (InputList.get(j).getInp_code().equalsIgnoreCase(dcrApprovalNames.get(position).getName())) {
                    InputListNew.add(new SaveCallInputList(InputList.get(j).getInp_code(), InputList.get(j).getInput_name(), InputList.get(j).getInp_qty()));
                }
            }

            Log.v("size", "--inp--" + InputListNew.size());
            if (InputListNew.size() > 0) {
                dcrDetailViewBinding.constraintMainInput.setVisibility(View.VISIBLE);
                inputAdapter = new InputAdapter(context, InputListNew);
                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(context);
                dcrDetailViewBinding.rvInput.setLayoutManager(mLayoutManager1);
                commonUtilsMethods.recycleTestWithDivider(dcrDetailViewBinding.rvInput);
                dcrDetailViewBinding.rvInput.setNestedScrollingEnabled(false);
                dcrDetailViewBinding.rvInput.setAdapter(inputAdapter);
            } else {
                dcrDetailViewBinding.constraintMainInput.setVisibility(View.GONE);
            }
        } else {
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_box));
            holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.dark_purple));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_light_grey_1));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.greater_than_purple));
        }

        holder.tv_date.setVisibility(View.INVISIBLE);
        holder.tv_name.setText(dcrApprovalNames.get(position).getName());

        holder.constraint_main.setOnClickListener(view -> {
            EventCaptureData.clear();
            rcpaList.clear();
            callDetailingLists.clear();
            dcrDetailViewBinding.rcpaLayout.setVisibility(View.GONE);
            dcrDetailViewBinding.sldLayout.setVisibility(View.GONE);
            if (adapter != null)
                adapter.notifyDataSetChanged();
            dcrDetailViewBinding.constraintTagIc.setVisibility(View.GONE);
            listenerApproval.onClickDcrDetail(new DcrDetailModelList(dcrApprovalNames.get(position).getHq_name(), dcrApprovalNames.get(position).getName(), dcrApprovalNames.get(position).getCode(), dcrApprovalNames.get(position).getTypeCust(), dcrApprovalNames.get(position).getType(), dcrApprovalNames.get(position).getSdp_name(), dcrApprovalNames.get(position).getPob(), dcrApprovalNames.get(position).getRemark(), dcrApprovalNames.get(position).getJointWork(), dcrApprovalNames.get(position).getCall_feedback(), dcrApprovalNames.get(position).getModTime(), dcrApprovalNames.get(position).getVisitTime(),dcrApprovalNames.get(position).getDct_id(),dcrApprovalNames.get(position).getDcr_detial_id()));
            notifyDataSetChanged();
        });

        dcrDetailViewBinding.constraintMainCapImg.setOnClickListener(view -> {

            if(dcrDetailViewBinding.constraintTagIc.getVisibility()==View.VISIBLE){
                dcrDetailViewBinding.tagViewIc.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                dcrDetailViewBinding.constraintTagIc.setVisibility(View.GONE);
            }else {
                if(EventCaptureData.size()>0){
                    dcrDetailViewBinding.tagViewIc.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                    dcrDetailViewBinding.constraintTagIc.setVisibility(View.VISIBLE);
                }else {
                    EvetCapureAPICall();
                }

            }



        });
        dcrDetailViewBinding.constraintMainRcpa.setOnClickListener(view -> {

            if(dcrDetailViewBinding.rcpaLayout.getVisibility()==View.VISIBLE){
                dcrDetailViewBinding.tagViewRcpa.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                dcrDetailViewBinding.rcpaLayout.setVisibility(View.GONE);
            }else {
                dcrDetailViewBinding.tagViewRcpa.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                if(rcpaList.size()>0){
                    dcrDetailViewBinding.rcpaLayout.setVisibility(View.VISIBLE);
                }else {
                    Rcpagetdata(dcrDetailViewBinding.Rcparecyelerview,dcrDetailViewBinding.rcpaLayout,position);
                }

            }
        });


        dcrDetailViewBinding.constraintMainSld.setOnClickListener(view -> {

            if(dcrDetailViewBinding.sldLayout.getVisibility()==View.VISIBLE){
                dcrDetailViewBinding.tagViewSld.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                dcrDetailViewBinding.sldLayout.setVisibility(View.GONE);
            }else {
                dcrDetailViewBinding.tagViewSld.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                if(callDetailingLists.size()>0){
                    dcrDetailViewBinding.sldLayout.setVisibility(View.VISIBLE);
                }else {
                    SldeDetails(dcrDetailViewBinding.sldarecyelerviwew,dcrDetailViewBinding.sldLayout,position);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return dcrApprovalNames.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<DcrDetailModelList> filteredNames) {
        this.dcrApprovalNames = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_date;
        ConstraintLayout constraint_main;
        ImageView list_arrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            constraint_main = itemView.findViewById(R.id.constraint_main);
            list_arrow = itemView.findViewById(R.id.listArrow);
        }
    }

    public  void EvetCapureAPICall(){

        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        if (UtilityClass.isNetworkAvailable(context)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
                        jsonObject.put("tableName", "getevent_rpt");
                        jsonObject.put("dcr_cd", DcrDetailViewActivity.dcr_id);
                        jsonObject.put("dcrdetail_cd",DcrDetailViewActivity.Details_id);
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf", DcrApprovalActivity.SelectedSfCode);

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
                                            Type typeToken = new TypeToken<ArrayList<EventCaptureModelClass>>() {
                                            }.getType();
                                            EventCaptureData = new Gson().fromJson(String.valueOf(jsonArray), typeToken);

                                            if(EventCaptureData.size()>0){
                                                setEventCaptureData(EventCaptureData);
                                            }else {
                                                dcrDetailViewBinding.tagViewIc.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                                dcrDetailViewBinding.constraintTagIc.setVisibility(View.GONE);
                                                commonUtilsMethods.showToastMessage(context, "No Event Capture");
                                            }

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    dcrDetailViewBinding.tagViewIc.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                    dcrDetailViewBinding.rvEventListview.setVisibility(View.GONE);
                                    dcrDetailViewBinding.constraintTagIc.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                dcrDetailViewBinding.tagViewIc.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                dcrDetailViewBinding.rvEventListview.setVisibility(View.GONE);
                                dcrDetailViewBinding.constraintTagIc.setVisibility(View.GONE);
                                commonUtilsMethods.showToastMessage(context,context.getString(R.string.no_network));
                                progressDialog.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(context,context. getString(R.string.poor_connection));
                }
            });
            networkStatusTask.execute();
        } else {
            progressDialog.dismiss();
            commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
        }

    }

    public void setEventCaptureData(ArrayList<EventCaptureModelClass> List){


         dcrDetailViewBinding.constraintTagIc.setVisibility(View.VISIBLE);
         adapter =new EventDetailsCapture(List,context);
         dcrDetailViewBinding.rvEventListview.setLayoutManager(new LinearLayoutManager(context));
         dcrDetailViewBinding.rvEventListview.setAdapter(adapter);


    }


    public  void Rcpagetdata(RecyclerView recyclerView , LinearLayout layout,int position){

        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        if (UtilityClass.isNetworkAvailable(context)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                        JSONObject jsonObject =CommonUtilsMethods.CommonObjectParameter(context);
                        jsonObject.put("tableName", "getdcr_rcpa");
                        jsonObject.put("dcrdetail_cd", DcrDetailViewActivity.Details_id);
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf",DcrApprovalActivity.SelectedSfCode);
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
                                            Type typeToken = new TypeToken<ArrayList<DayReportRcpaModelClass>>() {
                                            }.getType();
                                            rcpaList = new Gson().fromJson(String.valueOf(jsonArray), typeToken);
                                            if(rcpaList.size()>0){
                                                ReoportRcpaAdapter adapter=new ReoportRcpaAdapter(rcpaList,context);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                                commonUtilsMethods.recycleTestWithDivider(recyclerView);
                                                recyclerView.setAdapter(adapter);
                                                layout.setVisibility(View.VISIBLE);
                                            }else {
                                                layout.setVisibility(View.GONE);
                                                dcrDetailViewBinding.tagViewRcpa.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                                commonUtilsMethods.showToastMessage(context, "No RCPA Values");
                                            }

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    layout.setVisibility(View.GONE);
                                    dcrDetailViewBinding.tagViewRcpa.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                layout.setVisibility(View.GONE);
                                dcrDetailViewBinding.tagViewRcpa.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                                progressDialog.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.poor_connection));
                }
            });
            networkStatusTask.execute();
        } else {
            progressDialog.dismiss();
            commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
        }

    }

    public  void SldeDetails(RecyclerView recyclerView ,LinearLayout layout,int position){

        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        if (UtilityClass.isNetworkAvailable(context)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
                        jsonObject.put("tableName", "getslidedet");
                        jsonObject.put("ACd", DcrDetailViewActivity.dcr_id);
                        jsonObject.put("Mslcd", DcrDetailViewActivity.SelectedCode);
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf",DcrApprovalActivity.SelectedSfCode);

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
                                            Type typeToken = new TypeToken<ArrayList<SlideRatingDetalisModelClass>>() {
                                            }.getType();
                                            callDetailingLists = new Gson().fromJson(String.valueOf(jsonArray), typeToken);
                                            if(callDetailingLists.size()>0){
                                                DayReportSlideDetailsAdapter adapter=new DayReportSlideDetailsAdapter(callDetailingLists,context);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                                commonUtilsMethods.recycleTestWithDivider(recyclerView);
                                                recyclerView.setAdapter(adapter);
                                                layout.setVisibility(View.VISIBLE);
                                         //       Slededataid=arrayList.get(position).getTrans_Detail_Slno();
                                            }else {
                                                layout.setVisibility(View.GONE);
                                                dcrDetailViewBinding.tagViewSld.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                                commonUtilsMethods.showToastMessage(context, " Slides   Details Not Available");
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    layout.setVisibility(View.GONE);
                                    dcrDetailViewBinding.tagViewSld.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                                layout.setVisibility(View.GONE);
                                dcrDetailViewBinding.tagViewSld.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                                progressDialog.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.poor_connection));
                }
            });
            networkStatusTask.execute();
        } else {
            progressDialog.dismiss();
            commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
        }

    }

}
