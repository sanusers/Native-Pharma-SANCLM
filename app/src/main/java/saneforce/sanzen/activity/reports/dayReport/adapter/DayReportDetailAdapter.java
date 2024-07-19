package saneforce.sanzen.activity.reports.dayReport.adapter;



import static saneforce.sanzen.activity.call.DCRCallActivity.CallActivityCustDetails;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
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
import androidx.cardview.widget.CardView;
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
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.dcr.detailView.adapter.InputAdapter;
import saneforce.sanzen.activity.approvals.dcr.detailView.adapter.ProductAdapter;
import saneforce.sanzen.activity.call.pojo.input.SaveCallInputList;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.activity.reports.dayReport.model.DayReportDetailModel;
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
    boolean checkInOutNeed, VisitNeed,isRcpaRequested,isCaptureRequested,isPobRequested,isFeedBackRequested,isInputRequested,isProductRequested;
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
    ArrayList<DayReportRcpaModelClass> rcpaModelArray;


    public DayReportDetailAdapter(Context context, ArrayList<DayReportDetailModel> arrayList, String reportOf, String callCheckInOutNeed, String nextVst,String ActCode,String ReportingSfCode,String rcpaItem,String eventCaptureItem,String pobItem,String feedBackItem,String inputItem,String productItem) {
        this.context = context;
        this.arrayList = arrayList;
        this.supportModelArray = arrayList;
        this.reportOf = reportOf;
        this.ReportingSfCode=ReportingSfCode;
        this.acdCode=ActCode;
        commonUtilsMethods = new CommonUtilsMethods(context);
        checkInOutNeed = callCheckInOutNeed.equalsIgnoreCase("0");
        VisitNeed = nextVst.equalsIgnoreCase("0");
        isRcpaRequested = rcpaItem.equals("0");
        isCaptureRequested = eventCaptureItem.equals("0");
        isPobRequested = pobItem.equals("0");
        isFeedBackRequested = feedBackItem.equals("0");
        isInputRequested = inputItem.equals("0");
        isProductRequested = productItem.equals("0");
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
        holder.checkInOutLayout.setVisibility(View.GONE);
        if (isRcpaRequested){
            holder.rcpaLayoutitle.setVisibility(View.VISIBLE);
        }
        if (isCaptureRequested){
            holder.EventLayout.setVisibility(View.VISIBLE);
        }
        if (isPobRequested){
            holder.pobLayOut.setVisibility(View.VISIBLE);
        }
        if (isFeedBackRequested){
            holder.feedBackLayout.setVisibility(View.VISIBLE);
        }
        if (isInputRequested && !dataModel.getGifts().isEmpty()){
            holder.InpLayout.setVisibility(View.VISIBLE);
        }
        if (isProductRequested && !dataModel.getProducts().isEmpty()){
            holder.PrdLayout.setVisibility(View.VISIBLE);
        }
        if(SharedPref.getWrkAreaName(context).isEmpty() || SharedPref.getWrkAreaName(context).equalsIgnoreCase(null)){
            holder.clusterText.setText("Cluster");
        } else{
            holder.clusterText.setText(SharedPref.getWrkAreaName(context));
        }
        String detailingNeed = "0";

        switch (reportOf) {
            case Constants.DOCTOR: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_dr_icon));
                if (SharedPref.getDocProductCaption(context).isEmpty() || SharedPref.getDocProductCaption(context).equalsIgnoreCase(null)){
                    holder.textProduct.setText("Product");
                    holder.textProductName.setText("Product Name");
                }else{
                    holder.textProduct.setText(SharedPref.getDocProductCaption(context));
                    holder.textProductName.setText(SharedPref.getDocProductCaption(context));
                }
                if ( SharedPref.getDrSmpQCap(context).isEmpty() ||  SharedPref.getDrSmpQCap(context).equalsIgnoreCase(null)){
                    holder.textSamples.setText("Samples");
                }else {
                    holder.textSamples.setText( SharedPref.getDrSmpQCap(context));
                }
                if (SharedPref.getDrRxQCap(context).isEmpty() || SharedPref.getDrRxQCap(context).equalsIgnoreCase(null)){
                    holder.textRxQty.setText("RX Qty");
                }else {
                    holder.textRxQty.setText(SharedPref.getDrRxQCap(context));
                }
                if (SharedPref.getDocInputCaption(context).isEmpty() || SharedPref.getDocInputCaption(context).equalsIgnoreCase(null)){
                    holder.textInput.setText("Input");
                    holder.textInputName.setText("Input Name");
                }
                else {
                    holder.textInput.setText(SharedPref.getDocInputCaption(context));
                    holder.textInputName.setText(SharedPref.getDocInputCaption(context));
                }
                if (SharedPref.getDocJointworkNeed(context).equals("0")){
                    holder.jointWorkLayout.setVisibility(View.VISIBLE);
                    holder.jointView.setVisibility(View.VISIBLE);
                }else {
                    holder.jointWorkLayout.setVisibility(View.GONE);
                    holder.jointView.setVisibility(View.GONE);
                }
                if(SharedPref.getRcpaQtyNeed(context).equalsIgnoreCase("0")) {
                    holder.textRCPAName.setVisibility(View.VISIBLE);
                } else {
                    holder.textRCPAName.setVisibility(View.INVISIBLE);
                }
                holder.SlidercpaLayoutitle.setVisibility(View.VISIBLE);
                break;
            }
            case Constants.CHEMIST: {
                detailingNeed = SharedPref.getCHMDetailingNeed(context);
                holder.textPromoted.setVisibility(View.INVISIBLE);
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_chemist_icon));
                if (SharedPref.getChmProductCaption(context).isEmpty() || SharedPref.getChmProductCaption(context).equalsIgnoreCase(null)){
                    holder.textProduct.setText("Product");
                    holder.textProductName.setText("Product Name");
                }else{
                    holder.textProduct.setText(SharedPref.getChmProductCaption(context));
                    holder.textProductName.setText(SharedPref.getChmProductCaption(context));
                }
                if ( SharedPref.getChmSmpCap(context).isEmpty() ||  SharedPref.getChmSmpCap(context).equalsIgnoreCase(null)){
                    holder.textSamples.setText("Samples");
                }else {
                    holder.textSamples.setText(( SharedPref.getChmSmpCap(context)));
                }
                if (SharedPref.getChmQCap(context).isEmpty() || SharedPref.getChmQCap(context).isEmpty()){
                    holder.textRxQty.setText("RX Qty");
                }else {
                    holder.textRxQty.setText(SharedPref.getChmQCap(context));
                }
                if (SharedPref.getChmInputCaption(context).isEmpty() || SharedPref.getChmInputCaption(context).equalsIgnoreCase(null)){
                    holder.textInput.setText("Input");
                    holder.textInputName.setText("Input Name");
                }else {
                    holder.textInput.setText(SharedPref.getChmInputCaption(context));
                    holder.textInputName.setText(SharedPref.getChmInputCaption(context));
                }
                if (SharedPref.getChmJointworkNeed(context).equals("0")){
                    holder.jointWorkLayout.setVisibility(View.VISIBLE);
                    holder.jointView.setVisibility(View.VISIBLE);
                }else {
                    holder.jointWorkLayout.setVisibility(View.GONE);
                    holder.jointView.setVisibility(View.GONE);
                }
                holder.textRCPAName.setVisibility(View.INVISIBLE);
                break;
            }
            case Constants.STOCKIEST: {
                detailingNeed = SharedPref.getSTKDetailingNeed(context);
                holder.textPromoted.setVisibility(View.INVISIBLE);
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_stockiest_icon));
                if (SharedPref.getStkProductCaption(context).isEmpty() || SharedPref.getStkProductCaption(context).equalsIgnoreCase(null)){
                    holder.textProduct.setText("Product");
                    holder.textProductName.setText("Product Name");
                }else{
                    holder.textProduct.setText(SharedPref.getStkProductCaption(context));
                    holder.textProductName.setText(SharedPref.getStkProductCaption(context));
                }
                if (SharedPref.getStkQCap(context).isEmpty() || SharedPref.getStkQCap(context).isEmpty()){
                    holder.textRxQty.setText("RX Qty");
                }else {
                    holder.textRxQty.setText(SharedPref.getStkQCap(context));
                }
                if (SharedPref.getStkInputCaption(context).isEmpty() || SharedPref.getStkInputCaption(context).equalsIgnoreCase(null)){
                    holder.textInput.setText("Input");
                    holder.textInputName.setText("Input Name");
                }else {
                    holder.textInput.setText(SharedPref.getStkInputCaption(context));
                    holder.textInputName.setText(SharedPref.getStkInputCaption(context));
                }
                if (SharedPref.getStkJointworkNeed(context).equals("0")){
                    holder.jointWorkLayout.setVisibility(View.VISIBLE);
                    holder.jointView.setVisibility(View.VISIBLE);
                }else {
                    holder.jointWorkLayout.setVisibility(View.GONE);
                    holder.jointView.setVisibility(View.GONE);
                }
                holder.textRCPAName.setVisibility(View.INVISIBLE);
                break;
            }
            case Constants.UNLISTED_DOCTOR: {
                detailingNeed = SharedPref.getUNDRDetailingNeed(context);
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_unlist_dr_icon));
                if (SharedPref.getUlProductCaption(context).isEmpty() || SharedPref.getUlProductCaption(context).equalsIgnoreCase(null)){
                    holder.textProduct.setText("Product");
                    holder.textProductName.setText("Product Name");
                }else{
                    holder.textProduct.setText(SharedPref.getUlProductCaption(context));
                    holder.textProductName.setText(SharedPref.getUlProductCaption(context));
                }
                if ( SharedPref.getNlSmpQCap(context).isEmpty() ||  SharedPref.getNlSmpQCap(context).equalsIgnoreCase(null)){
                    holder.textSamples.setText("Samples");
                }else {
                    holder.textSamples.setText(( SharedPref.getNlSmpQCap(context)));
                }
                if (SharedPref.getNlRxQCap(context).isEmpty() || SharedPref.getNlRxQCap(context).isEmpty()){
                    holder.textRxQty.setText("RX Qty");
                }else {
                    holder.textRxQty.setText(SharedPref.getNlRxQCap(context));
                }
                if (SharedPref.getUlInputCaption(context).isEmpty() || SharedPref.getUlInputCaption(context).equalsIgnoreCase(null)){
                    holder.textInput.setText("Input");
                    holder.textInputName.setText("Input Name");
                }else {
                    holder.textInput.setText(SharedPref.getUlInputCaption(context));
                    holder.textInputName.setText(SharedPref.getUlInputCaption(context));
                }
                if (SharedPref.getUlJointworkNeed(context).equals("0")){
                    holder.jointWorkLayout.setVisibility(View.VISIBLE);
                    holder.jointView.setVisibility(View.VISIBLE);
                }else {
                    holder.jointWorkLayout.setVisibility(View.GONE);
                    holder.jointView.setVisibility(View.GONE);
                }
                holder.textRCPAName.setVisibility(View.INVISIBLE);
                break;
            }
            case Constants.CIP: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_cip_icon));
                holder.textRCPAName.setVisibility(View.INVISIBLE);
                break;
            }
            case Constants.HOSPITAL: {
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_hospital_icon));
                holder.textRCPAName.setVisibility(View.INVISIBLE);
                break;
            }
        }

        if(detailingNeed.equalsIgnoreCase("0")){
            holder.slideLayout.setVisibility(View.VISIBLE);
            holder.view5.setVisibility(View.VISIBLE);
        }else {
            holder.slideLayout.setVisibility(View.GONE);
            holder.view5.setVisibility(View.GONE);
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
                    productAdapter = new ProductAdapter(context, getProductList(dataModel.getProducts()),reportOf);
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
            rcpaList.clear();
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
                holder.slide_arrow.setImageDrawable(context.getDrawable(R.drawable.click_logo));
                holder.slideDetailsLayout.setVisibility(View.GONE);
            }else {
                if(callDetailingLists.size()>0){
                    holder.slide_arrow.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                    if(dataModel.getTrans_Detail_Slno().equalsIgnoreCase(Slededataid)){
                        holder.slideDetailsLayout.setVisibility(View.VISIBLE);
                    }else {
                        SldeDetails(holder.rvSlideDetails,holder.slideDetailsLayout,position,holder);
                    }
                }else {
                    SldeDetails(holder.rvSlideDetails,holder.slideDetailsLayout,position,holder);
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

                String Rcpa = "";
                if(item.length > 3) {
                    Rcpa = item[3];
                    if(item[3].contains("^")) {
                        String[] rcpa = item[3].replace("^", ",").split("[,]");
                        Rcpa = rcpa[1];
                    }
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
        TextView name, visitTime, modifiedTime, cluster, pob, feedback, jointWork, nextVisit, checkInTime, checkInAddress, checkInMarker,textInputName;
        TextView checkOutTime, checkOutAddress, checkOutMarker, overAllRemark, viewMoreTxt,textPromoted,textProduct,textSamples,textRxQty,textInput,textProductName,clusterText, textRCPAName;
        ImageView nameIcon, viewMoreArrow,rcpa_arrow,slide_arrow;
        LinearLayout viewMore, checkInOutLayout,EventLayout,rcpaLayout,rcpaLayoutitle, slideDetailsLayout,SlidercpaLayoutitle,jointWorkLayout;
        RelativeLayout rlNextVisit,pobLayOut,feedBackLayout;
        ConstraintLayout PrdLayout, InpLayout, expandLayout;
        CardView slideLayout;


        RecyclerView rvPrd, rvInput,rvRcpa,rvSlideDetails;
        View viewNextVisit,jointView, view5;

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
            slideLayout = itemView.findViewById(R.id.cardSlideLayout);
            slideDetailsLayout = itemView.findViewById(R.id.SlideLayout);
            rvSlideDetails = itemView.findViewById(R.id.Sliderecyelerview);
            slide_arrow = itemView.findViewById(R.id.slide_arrow);
            SlidercpaLayoutitle = itemView.findViewById(R.id.SlideLayoutitle);
            textPromoted =  itemView.findViewById(R.id.product_qty);
            pobLayOut = itemView.findViewById(R.id.pobLayout);
            feedBackLayout = itemView.findViewById(R.id.feedBackLayout);
            textProduct = itemView.findViewById(R.id.tag_sample_prd);
            textSamples = itemView.findViewById(R.id.tag_samples);
            textRxQty =  itemView.findViewById(R.id.tag_rx_qty);
            textInput = itemView.findViewById(R.id.tag_input);
            jointWorkLayout = itemView.findViewById(R.id.ll2);
            jointView = itemView.findViewById(R.id.view3);
            textProductName = itemView.findViewById(R.id.tag_prd_name);
            textInputName = itemView.findViewById(R.id.tag_input_name_main);
            view5 = itemView.findViewById(R.id.view5);
            clusterText = itemView.findViewById(R.id.clusterTxt);
            textRCPAName = itemView.findViewById(R.id.tag_rcpa_prd);
        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            ArrayList<DayReportDetailModel> filteredModelArray = new ArrayList<>();
            ArrayList<DayReportRcpaModelClass> rcpaModelClassArrayList = new ArrayList<>();
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
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
                        jsonObject.put("tableName", "getevent_rpt");
                        jsonObject.put("dcr_cd", acdCode);
                        jsonObject.put("dcrdetail_cd", arrayList.get(position).getTrans_Detail_Slno());
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf", ReportingSfCode);

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
                        JSONObject jsonObject =CommonUtilsMethods.CommonObjectParameter(context);
                        jsonObject.put("tableName", "getdcr_rcpa");
                        jsonObject.put("dcrdetail_cd", arrayList.get(position).getTrans_Detail_Slno());
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf",ReportingSfCode);
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


    public  void SldeDetails(RecyclerView recyclerView , LinearLayout layout, int position,MyViewHolder holder){
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        if (UtilityClass.isNetworkAvailable(context)) {
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
                        jsonObject.put("tableName", "getslidedet");
                        jsonObject.put("ACd", acdCode);
                        jsonObject.put("Mslcd", arrayList.get(position).getCode());
                        jsonObject.put("sfcode", SharedPref.getSfCode(context));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                        jsonObject.put("Rsf",ReportingSfCode);
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
                                                holder.slide_arrow.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                                                DayReportSlideDetailsAdapter adapter=new DayReportSlideDetailsAdapter(callDetailingLists,context);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                                commonUtilsMethods.recycleTestWithDivider(recyclerView);
                                                recyclerView.setAdapter(adapter);
                                                layout.setVisibility(View.VISIBLE);
                                                Slededataid=arrayList.get(position).getTrans_Detail_Slno();
                                            }else {
                                                holder.slide_arrow.setImageDrawable(context.getDrawable(R.drawable.click_logo));
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
