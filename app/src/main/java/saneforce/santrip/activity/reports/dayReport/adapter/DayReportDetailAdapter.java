package saneforce.santrip.activity.reports.dayReport.adapter;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.Locale;

import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.dcr.detailView.adapter.InputAdapter;
import saneforce.santrip.activity.approvals.dcr.detailView.adapter.ProductAdapter;
import saneforce.santrip.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.santrip.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.santrip.activity.reports.dayReport.model.DayReportDetailModel;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;

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
    private ValueFilter valueFilter;

    public DayReportDetailAdapter(Context context, ArrayList<DayReportDetailModel> arrayList, String reportOf, String callCheckInOutNeed, String nextVst) {
        this.context = context;
        this.arrayList = arrayList;
        this.supportModelArray = arrayList;
        this.reportOf = reportOf;
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
        String PrdName, PrdSamQty, PrdRxQty;
        productList = new ArrayList<>();
        if (!products.isEmpty()) {
            String[] StrArray = products.split(",");
            for (String value : StrArray) {
                if (!value.equalsIgnoreCase("  )")) {
                    PrdName = value.substring(0, value.indexOf('(')).trim();

                    PrdSamQty = value.substring(value.indexOf("(") + 1);
                    PrdSamQty = PrdSamQty.substring(0, PrdSamQty.indexOf(")"));

                    PrdRxQty = value.substring(value.indexOf(")") + 1).trim();
                    if (PrdRxQty.contains("(")) {
                        PrdRxQty = PrdRxQty.substring(PrdRxQty.indexOf("(") + 1);
                        PrdRxQty = PrdRxQty.substring(0, PrdRxQty.indexOf(")"));
                    } else {
                        PrdRxQty = "0";
                    }
                    productList.add(new SaveCallProductList(arrayList.get(0).getCode(), PrdName, PrdSamQty, PrdRxQty, "0", "Yes"));
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
        ImageView nameIcon, viewMoreArrow;
        LinearLayout viewMore, checkInOutLayout;
        RelativeLayout rlNextVisit;
        ConstraintLayout PrdLayout, InpLayout, expandLayout;
        RecyclerView rvPrd, rvInput;
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


            nameIcon = itemView.findViewById(R.id.iconName);
            expandLayout = itemView.findViewById(R.id.constraint_expand_view);
            viewMore = itemView.findViewById(R.id.viewMore);
            viewMoreArrow = itemView.findViewById(R.id.viewMoreArrow);
            rvPrd = itemView.findViewById(R.id.rv_sample_prd);
            PrdLayout = itemView.findViewById(R.id.productLayout);
            rvInput = itemView.findViewById(R.id.rv_input);
            InpLayout = itemView.findViewById(R.id.inputLayout);
            checkInOutLayout = itemView.findViewById(R.id.checkInOutLayout);
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

}
