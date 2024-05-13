package saneforce.santrip.activity.reports.dayReport.adapter;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.dcr.detailView.adapter.InputAdapter;
import saneforce.santrip.activity.approvals.dcr.detailView.adapter.ProductAdapter;
import saneforce.santrip.activity.call.pojo.detailing.CallDetailingList;
import saneforce.santrip.activity.call.pojo.input.SaveCallInputList;
import saneforce.santrip.activity.call.pojo.product.SaveCallProductList;
import saneforce.santrip.activity.reports.dayReport.model.DayReportDetailModel;
import saneforce.santrip.activity.reports.dayReport.model.DayReportRcpaModelClass;
import saneforce.santrip.activity.reports.dayReport.model.EventCaptureModelClass;
import saneforce.santrip.activity.reports.dayReport.model.SlideRatingDetalisModelClass;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkStatusTask;

public class DayReportDetailAdapter extends RecyclerView.Adapter<DayReportDetailAdapter.MyViewHolder> implements Filterable {

    Context context;
    ArrayList<DayReportDetailModel> arrayList;
    String reportOf;
    ArrayList<DayReportDetailModel> supportModelArray;
    ArrayList<SaveCallProductList> productList;
    ArrayList<SaveCallInputList> inputLists;
    CommonUtilsMethods commonUtilsMethods;
    ProductAdapter productAdapter;
    InputAdapter inputAdapter;
    boolean checkInOutNeed, VisitNeed;
    ArrayList productPromoted = new ArrayList();
    private ValueFilter valueFilter;
    ArrayList<EventCaptureModelClass> EventCaptureData = new ArrayList<>();
    ArrayList<DayReportRcpaModelClass> rcpaList = new ArrayList<>();
    String rcpadataid="",Slededataid;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    String acdCode;
    String ReportingSfCode;
    ArrayList<SlideRatingDetalisModelClass> callDetailingLists=new ArrayList<>();


    public DayReportDetailAdapter(Context context, ArrayList<DayReportDetailModel> arrayList, String reportOf, String callCheckInOutNeed, String nextVst,String ActCode,String ReportingSfCode) {
        this.context = context;
        this.arrayList = arrayList;
        this.supportModelArray = arrayList;
        this.reportOf = reportOf;
        this.ReportingSfCode=ReportingSfCode;
        this.acdCode=ActCode;
        commonUtilsMethods = new CommonUtilsMethods(context);
        checkInOutNeed = callCheckInOutNeed.equalsIgnoreCase("0");
        VisitNeed = nextVst.equalsIgnoreCase("0");
    }

    @NonNull
    @Override
    public DayReportDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_day_detail_item_one, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayReportDetailAdapter.MyViewHolder holder, int position) {
        holder.expandLayout.setVisibility(View.GONE);
        DayReportDetailModel dataModel = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.name.setText(dataModel.getName());
        holder.visitTime.setText(dataModel.getVisitTime());
        holder.modifiedTime.setText(dataModel.getModTime());
        holder.cluster.setText(dataModel.getTerritory());
        holder.pob.setText(String.valueOf(dataModel.getPob_value()));
        holder.feedback.setText(dataModel.getCall_Fdback());
        holder.jointWork.setText(dataModel.getWWith());
        holder.nextVisit.setText(dataModel.getNextVstDate());
        holder.overAllRemark.setText(dataModel.getRemarks());


        if (checkInOutNeed) {
            holder.checkInOutLayout.setVisibility(View.VISIBLE);
        }

        if (VisitNeed) {
            holder.viewNextVisit.setVisibility(View.VISIBLE);
            holder.rlNextVisit.setVisibility(View.VISIBLE);
        }

        switch (reportOf) {
            case Constants.DOCTOR: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_dr_icon));
                break;
            }
            case Constants.CHEMIST: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_chemist_icon));
                break;
            }
            case Constants.STOCKIEST: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_stockiest_icon));
                break;
            }
            case Constants.UNLISTED_DOCTOR: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_unlist_dr_icon));
                break;
            }
            case Constants.CIP: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_cip_icon));
                break;
            }
            case Constants.HOSPITAL: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_hospital_icon));
                break;
            }
        }

        holder.viewMore.setOnClickListener(view -> {
            if (holder.expandLayout.getVisibility() == View.VISIBLE) {
                holder.viewMoreTxt.setText(R.string.view_more);
                holder.viewMoreArrow.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                holder.expandLayout.setVisibility(View.GONE);
            } else {
                if (!dataModel.getProducts().isEmpty()) {
                    holder.rvPrd.setVisibility(View.VISIBLE);
                    holder.PrdLayout.setVisibility(View.VISIBLE);
                    productPromoted = getList(dataModel.getPromoted_product());
                    productAdapter = new ProductAdapter(context, getProductList(dataModel.getProducts()));
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    holder.rvPrd.setLayoutManager(mLayoutManager);
                    commonUtilsMethods.recycleTestWithDivider(holder.rvPrd);
                    holder.rvPrd.setNestedScrollingEnabled(false);
                    holder.rvPrd.setAdapter(productAdapter);
                }

                if (!dataModel.getGifts().isEmpty()) {
                    holder.rvInput.setVisibility(View.VISIBLE);
                    holder.InpLayout.setVisibility(View.VISIBLE);
                    inputAdapter = new InputAdapter(context, getInputList(dataModel.getGifts()));
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    holder.rvInput.setLayoutManager(mLayoutManager);
                    commonUtilsMethods.recycleTestWithDivider(holder.rvInput);
                    holder.rvInput.setNestedScrollingEnabled(false);
                    holder.rvInput.setAdapter(inputAdapter);
                }

                holder.viewMoreTxt.setText(R.string.view_less);
                holder.viewMoreArrow.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                holder.expandLayout.setVisibility(View.VISIBLE);
            }
        });

        holder.checkInMarker.setOnClickListener(view -> {






        });

        holder.checkOutMarker.setOnClickListener(view -> {


        });

        holder.EventLayout.setOnClickListener(view -> {

            EvetCapureAPICall(position);
        });

        holder.rcpaLayoutitle.setOnClickListener(view -> {

            if(holder.rcpaLayout.getVisibility()==View.VISIBLE){
                holder.rcpa_arrow.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                holder.rcpaLayout.setVisibility(View.GONE);
            }else {
                holder.rcpa_arrow.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                if(rcpaList.size()>0){
                    if(dataModel.getTrans_Detail_Slno().equalsIgnoreCase(rcpadataid)){
                        holder.rcpaLayout.setVisibility(View.VISIBLE);
                    }else {
                        Rcpagetdata(holder.rvRcpa,holder.rcpaLayout,position);
                    }
                }else {
                    Rcpagetdata(holder.rvRcpa,holder.rcpaLayout,position);
                }

            }
        });




        holder.SlidercpaLayoutitle.setOnClickListener(view -> {

            if(holder.slideDetailsLayout.getVisibility()==View.VISIBLE){
                holder.slide_arrow.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                holder.slideDetailsLayout.setVisibility(View.GONE);
            }else {
                holder.slide_arrow.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                if(callDetailingLists.size()>0){
                    if(dataModel.getTrans_Detail_Slno().equalsIgnoreCase(Slededataid)){
                        holder.slideDetailsLayout.setVisibility(View.VISIBLE);
                    }else {
                        SldeDetails(holder.rvSlideDetails,holder.slideDetailsLayout,position);
                    }
                }else {
                    SldeDetails(holder.rvSlideDetails,holder.slideDetailsLayout,position);
                }


            }
        });

    }

    private ArrayList<String>  getList(String s) {

        ArrayList<String> list=new ArrayList<>();
        String[] clstarrrayqty = s.split("#");
        StringBuilder ss1 = new StringBuilder();
        for (String value : clstarrrayqty) {
            list.add(value.substring(value.lastIndexOf("$") + 1));
           // ss1.append(value.substring(value.lastIndexOf("$") + 1)).append(",");
        }
        return list ;
    }


    public ArrayList<SaveCallInputList> getInputList(String inputs) {
        //Extract Input Values
        String InpName, InpQty;
        inputLists = new ArrayList<>();
        if (!inputs.isEmpty()) {
            String[] StrArray = inputs.split(",");
            for (String value : StrArray) {
                if (!value.equalsIgnoreCase("  )")) {
                    InpName = value.substring(0, value.indexOf('(')).trim();
                    InpQty = value.substring(value.indexOf("(") + 1);
                    InpQty = InpQty.substring(0, InpQty.indexOf(")"));
                    inputLists.add(new SaveCallInputList(arrayList.get(0).getCode(), InpName, InpQty));
                }
            }
        }

        return inputLists;
    }


    public ArrayList<SaveCallProductList> getProductList(String products) {
        //Extract Product Values
        productList = new ArrayList<>();
        if (!products.isEmpty()) {
            String str = products.replace(")", "");
            String[] separated = str.split(",");

            List<String> resultList = new ArrayList<>();
            for (String str1 : separated) {
                str1 = str1.trim();
                if (!str1.isEmpty()) {
                    resultList.add(str1);
                }
            }
            String[] newArray = resultList.toArray(new String[0]);
            for (String s : newArray) {
                String[] item = s.split("[(]");

                String Rcpa = item[3];
                if (item[3].contains("^")) {
                    String[] rcpa = item[3].replace("^", ",").split("[,]");
                    Rcpa = rcpa[1];
                }
                Log.e("PromotedCode", productPromoted + " ???? " + item[0]);
                if (productPromoted.contains(item[0].trim())) {
                    Log.e("PromotedCode", "Yes");
                    productList.add(new SaveCallProductList(arrayList.get(0).getCode(), item[0], item[1], item[2], Rcpa, "Yes"));
                } else {
                    Log.e("PromotedCode", "No");
                    productList.add(new SaveCallProductList(arrayList.get(0).getCode(), item[0], item[1], item[2], Rcpa, "No"));
                }

            }
        }
            return productList;
        }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, visitTime, modifiedTime, cluster, pob, feedback, jointWork, nextVisit, checkInTime, checkInAddress, checkInMarker;
        TextView checkOutTime, checkOutAddress, checkOutMarker, overAllRemark, viewMoreTxt;
        ImageView nameIcon, viewMoreArrow,rcpa_arrow,slide_arrow;
        LinearLayout viewMore, checkInOutLayout,EventLayout,rcpaLayout,rcpaLayoutitle, slideDetailsLayout,SlidercpaLayoutitle;
        RelativeLayout rlNextVisit;
        ConstraintLayout PrdLayout, InpLayout, expandLayout;



        RecyclerView rvPrd, rvInput,rvRcpa,rvSlideDetails;
        View viewNextVisit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            visitTime = itemView.findViewById(R.id.visitTime);
            modifiedTime = itemView.findViewById(R.id.modifyTime);
            cluster = itemView.findViewById(R.id.cluster);
            pob = itemView.findViewById(R.id.pob);
            feedback = itemView.findViewById(R.id.feedback);
            jointWork = itemView.findViewById(R.id.jointWork);
            nextVisit = itemView.findViewById(R.id.nextVisit);
            checkInTime = itemView.findViewById(R.id.checkInTime);
            checkInAddress = itemView.findViewById(R.id.inAddress);
            checkInMarker = itemView.findViewById(R.id.checkInMarker);
            checkOutTime = itemView.findViewById(R.id.checkOutTime);
            checkOutAddress = itemView.findViewById(R.id.outAddress);
            checkOutMarker = itemView.findViewById(R.id.checkOutMarker);
            overAllRemark = itemView.findViewById(R.id.overAllRemark);
            viewMoreTxt = itemView.findViewById(R.id.viewMoreTxt);
            rlNextVisit = itemView.findViewById(R.id.rl_nextVisit);
            viewNextVisit = itemView.findViewById(R.id.view_ll2);
            EventLayout=itemView.findViewById(R.id.eventcaptureLayout);
            rcpaLayout=itemView.findViewById(R.id.rcpaLayout);
            rcpaLayoutitle=itemView.findViewById(R.id.rcpaLayoutitle);
            rcpa_arrow=itemView.findViewById(R.id.rcpa_arrow);
            nameIcon = itemView.findViewById(R.id.iconName);
            expandLayout = itemView.findViewById(R.id.constraint_expand_view);
            viewMore = itemView.findViewById(R.id.viewMore);
            viewMoreArrow = itemView.findViewById(R.id.viewMoreArrow);
            rvPrd = itemView.findViewById(R.id.rv_sample_prd);
            rvRcpa = itemView.findViewById(R.id.Rcparecyelerview);
            PrdLayout = itemView.findViewById(R.id.productLayout);
            rvInput = itemView.findViewById(R.id.rv_input);
            InpLayout = itemView.findViewById(R.id.inputLayout);
            checkInOutLayout = itemView.findViewById(R.id.checkInOutLayout);

            slideDetailsLayout = itemView.findViewById(R.id.SlideLayout);
            rvSlideDetails = itemView.findViewById(R.id.Sliderecyelerview);
            slide_arrow = itemView.findViewById(R.id.slide_arrow);
            SlidercpaLayoutitle = itemView.findViewById(R.id.SlideLayoutitle);

        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            ArrayList<DayReportDetailModel> filteredModelArray = new ArrayList<>();
            if (charSequence != null && charSequence.length() > 0) {
                for (DayReportDetailModel model : supportModelArray) {
                    if (model.getName().toUpperCase().contains(charSequence.toString().toUpperCase()) || model.getTerritory().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        filteredModelArray.add(model);
                    }
                }
                results.count = filteredModelArray.size();
                results.values = filteredModelArray;
            } else {
                results.count = supportModelArray.size();
                results.values = supportModelArray;
            }
            return results;

        }

        @SuppressLint("NotifyDataSetChanged")
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<DayReportDetailModel>) results.values;
            notifyDataSetChanged();
        }
    }





    public  void EvetCapureAPICall(int position){


        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        if (UtilityClass.isNetworkAvailable(context)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("tableName", "getevent_rpt");
                        jsonObject.put("dcr_cd", acdCode);
                        jsonObject.put("dcrdetail_cd", arrayList.get(position).getTrans_Detail_Slno());
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf", ReportingSfCode);
                        jsonObject.put("sf_type", SharedPref.getSfType(context));
                        jsonObject.put("Designation", SharedPref.getDesig(context));
                        jsonObject.put("state_code", SharedPref.getStateCode(context));
                        jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(context));
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
                                                commonUtilsMethods.showToastMessage(context, " Event Capture Not Available");
                                            }

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
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

   public void setEventCaptureData(ArrayList<EventCaptureModelClass> List){

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dayreport_eventcapture_image_layout, null);
        dialog.setView(view);
        RecyclerView recyclerView=view.findViewById(R.id.recyelerview);
        EventCaptureAdapter adapter =new EventCaptureAdapter(context,List);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        AlertDialog dialog1=dialog.create();
        dialog1.show();

    }




    public  void Rcpagetdata(RecyclerView recyclerView ,LinearLayout layout,int position){


        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        if (UtilityClass.isNetworkAvailable(context)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("tableName", "getdcr_rcpa");
                        jsonObject.put("dcrdetail_cd", arrayList.get(position).getTrans_Detail_Slno());
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf",ReportingSfCode);
                        jsonObject.put("sf_type", SharedPref.getSfType(context));
                        jsonObject.put("Designation", SharedPref.getDesig(context));
                        jsonObject.put("state_code", SharedPref.getStateCode(context));
                        jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(context));
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

                                                rcpadataid=arrayList.get(position).getTrans_Detail_Slno();
                                            }else {
                                                commonUtilsMethods.showToastMessage(context, " RCPA  Details Not Available");
                                            }

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
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
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("tableName", "getslidedet");
                        jsonObject.put("ACd", acdCode);
                        jsonObject.put("Mslcd", arrayList.get(position).getCode());
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf",ReportingSfCode);
                        jsonObject.put("sf_type", SharedPref.getSfType(context));
                        jsonObject.put("Designation", SharedPref.getDesig(context));
                        jsonObject.put("state_code", SharedPref.getStateCode(context));
                        jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(context));
                        jsonObject.put("app_version", context.getResources().getString(R.string.app_version));
                        jsonObject.put("Mode", context.getResources().getString(R.string.app_mode));
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
                                                Slededataid=arrayList.get(position).getTrans_Detail_Slno();
                                            }else {
                                                commonUtilsMethods.showToastMessage(context, " Slides   Details Not Available");
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
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
