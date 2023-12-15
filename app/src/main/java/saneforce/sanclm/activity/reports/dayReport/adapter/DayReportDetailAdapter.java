package saneforce.sanclm.activity.reports.dayReport.adapter;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.reports.dayReport.DayReportDetailModel;
import saneforce.sanclm.activity.reports.dayReport.DayReportModel;
import saneforce.sanclm.commonClasses.Constants;

public class DayReportDetailAdapter extends RecyclerView.Adapter<DayReportDetailAdapter.MyViewHolder> implements Filterable {

    Context context;
    ArrayList<DayReportDetailModel> arrayList = new ArrayList<>();
    String reportOf;
    ArrayList<DayReportDetailModel> supportModelArray = new ArrayList<>();
    private ValueFilter valueFilter;

    public DayReportDetailAdapter(Context context, ArrayList<DayReportDetailModel> arrayList, String reportOf) {
        this.context = context;
        this.arrayList = arrayList;
        this.supportModelArray = arrayList;
        this.reportOf = reportOf;
    }

    @NonNull
    @Override
    public DayReportDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_day_detail_item,parent,false);
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

//        holder.checkInTime.setText(dataModel.getCheckin());
//        holder.checkOutTime.setText(dataModel.getCheckout());
        holder.overAllRemark.setText(dataModel.getRemarks());

        switch (reportOf){
            case Constants.DOCTOR:{
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_dr_icon));
                break;
            }
            case Constants.CHEMIST:{
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_chemist_icon));
                break;
            }
            case Constants.STOCKIEST:{
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_stockiest_icon));
                break;
            }
            case Constants.UNLISTED_DOCTOR:{
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_unlist_dr_icon));
                break;
            }
            case Constants.CIP:{
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_cip_icon));
                break;
            }
            case Constants.HOSPITAL:{
                holder.nameIcon.setImageDrawable(context.getDrawable(R.drawable.tp_hospital_icon));
                break;
            }
        }

        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.expandLayout.getVisibility() == View.VISIBLE){
                    holder.viewMoreTxt.setText("View More");
                    holder.viewMoreArrow.setImageDrawable(context.getDrawable(R.drawable.arrow_down));
                    holder.expandLayout.setVisibility(View.GONE);
                }else{
                    holder.viewMoreTxt.setText("View Less");
                    holder.viewMoreArrow.setImageDrawable(context.getDrawable(R.drawable.up_arrow));
                    holder.expandLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.checkInMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.checkOutMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(valueFilter==null) {
            valueFilter=new ValueFilter();
        }
        return valueFilter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,visitTime,modifiedTime,cluster,pob,feedback,jointWork,nextVisit,checkInTime,checkInAddress,checkInMarker;
        TextView checkOutTime,checkOutAddress,checkOutMarker,overAllRemark,viewMoreTxt;
        ImageView nameIcon,viewMoreArrow;
        RelativeLayout expandLayout;
        LinearLayout viewMore;
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

            nameIcon = itemView.findViewById(R.id.iconName);
            expandLayout = itemView.findViewById(R.id.expandLayout);
            viewMore = itemView.findViewById(R.id.viewMore);
            viewMoreArrow = itemView.findViewById(R.id.viewMoreArrow);

        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results=new FilterResults();

            ArrayList<DayReportDetailModel> filteredModelArray = new ArrayList<>();
            if(charSequence!=null && charSequence.length() > 0){
                for (DayReportDetailModel model : supportModelArray){
                    if(model.getName().toUpperCase().contains(charSequence.toString().toUpperCase()) || model.getTerritory().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        filteredModelArray.add(model);
                    }
                }
                results.count=filteredModelArray.size();
                results.values=filteredModelArray;
            }else{
                results.count = supportModelArray.size();
                results.values = supportModelArray;
            }
            return results;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<DayReportDetailModel>) results.values;
            notifyDataSetChanged();
        }
    }

}
