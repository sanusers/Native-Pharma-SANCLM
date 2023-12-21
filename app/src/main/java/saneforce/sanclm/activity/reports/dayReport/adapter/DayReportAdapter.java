package saneforce.sanclm.activity.reports.dayReport.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.forms.weekoff.Holiday_fragment;
import saneforce.sanclm.activity.map.MapsActivity;
import saneforce.sanclm.activity.reports.ReportFragContainerActivity;
import saneforce.sanclm.activity.reports.dayReport.DataViewModel;
import saneforce.sanclm.activity.reports.dayReport.fragment.DayReportDetailFragment;
import saneforce.sanclm.activity.reports.dayReport.model.DayReportModel;


public class DayReportAdapter extends RecyclerView.Adapter<DayReportAdapter.MyViewHolder> implements Filterable {
    ArrayList<DayReportModel> arrayList = new ArrayList<>();
    ArrayList<DayReportModel> supportModelArray = new ArrayList<>();

    Context context;
    DataViewModel dataViewModel;
    private ValueFilter valueFilter;


    public DayReportAdapter(ArrayList<DayReportModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.supportModelArray = arrayList;
        this.context = context;
        dataViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(DataViewModel.class);
    }

    @NonNull
    @Override
    public DayReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_day_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayReportAdapter.MyViewHolder holder, int position) {
        DayReportModel dayReportModel = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.name.setText(dayReportModel.getTerrWrk());
        holder.workType.setText(dayReportModel.getWtype());
        holder.checkInTime.setText(dayReportModel.getIntime());
        holder.checkInAddress.setText(dayReportModel.getInaddress());
        holder.checkOutTime.setText(dayReportModel.getOuttime());
        holder.checkOutAddress.setText(dayReportModel.getOutaddress());
        holder.submitDate.setText(dayReportModel.getRptdate());
        holder.remarks.setText(dayReportModel.getRemarks());

        holder.drCount.setText(dayReportModel.getDrs());
        holder.chemCount.setText(dayReportModel.getChm());
        holder.stockCount.setText(dayReportModel.getStk());
        holder.cipCount.setText(dayReportModel.getCip());
        holder.unDrCount.setText(dayReportModel.getUdr());
        holder.hospCount.setText(dayReportModel.getHos());

        int status = dayReportModel.getTyp();
        switch (status){
            case 0 : {
                holder.status.setText("Pending");
                holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.text_dark_15)));
                break;
            }
            case 1 : {
                holder.status.setText("Approved");
                holder.status.setTextColor(context.getColor(R.color.green_2));
                holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.bg_priority)));
                break;
            }
            case 2 : {
                holder.status.setText("Rejected");
                holder.status.setTextColor(context.getColor(R.color.pink));
                holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.pink_15)));
                break;
            }
        }

        holder.checkInMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MapsActivity.class));
            }
        });

        holder.checkOutMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataViewModel.saveDetailedData(new Gson().toJson(dayReportModel));
                ReportFragContainerActivity activity = (ReportFragContainerActivity) context;
                activity.loadFragment(new DayReportDetailFragment());

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

        TextView name,workType,checkInTime,checkInAddress,checkInMarker,checkOutTime,checkOutAddress,checkOutMarker,submitDate,remarks,status;
        TextView drCount,chemCount,stockCount,unDrCount,cipCount,hospCount;
        ConstraintLayout drIcon,cheIcon,stockIcon,cipIcon,unDrIcon,hospIcon;
        LinearLayout arrow,statusLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            workType = itemView.findViewById(R.id.workType);
            checkInTime = itemView.findViewById(R.id.checkInTime);
            checkInAddress = itemView.findViewById(R.id.inAddress);
            checkInMarker = itemView.findViewById(R.id.checkInMarker);
            checkOutTime = itemView.findViewById(R.id.checkOutTime);
            checkOutAddress = itemView.findViewById(R.id.outAddress);
            checkOutMarker = itemView.findViewById(R.id.checkOutMarker);
            submitDate = itemView.findViewById(R.id.submitDate);
            remarks = itemView.findViewById(R.id.remarks);
            status = itemView.findViewById(R.id.status);

            drIcon = itemView.findViewById(R.id.drLayout);
            cheIcon = itemView.findViewById(R.id.cheLayout);
            stockIcon = itemView.findViewById(R.id.stkLayout);
            cipIcon = itemView.findViewById(R.id.cipLayout);
            unDrIcon = itemView.findViewById(R.id.unDrLayout);
            hospIcon = itemView.findViewById(R.id.hospLayout);

            drCount = itemView.findViewById(R.id.drCount);
            chemCount = itemView.findViewById(R.id.chemistCount);
            stockCount = itemView.findViewById(R.id.stockiestCount);
            unDrCount = itemView.findViewById(R.id.unDrCount);
            cipCount = itemView.findViewById(R.id.cipCount);
            hospCount = itemView.findViewById(R.id.hospCount);

            statusLayout = itemView.findViewById(R.id.statusLayout);
            arrow = itemView.findViewById(R.id.arrow);

        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results=new FilterResults();

            ArrayList<DayReportModel> filteredModelArray = new ArrayList<>();
            if(charSequence!=null && charSequence.length() > 0){
                for (DayReportModel dayReportModel : supportModelArray){
                    if(dayReportModel.getTerrWrk().toUpperCase().contains(charSequence.toString().toUpperCase()) || dayReportModel.getWtype().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        filteredModelArray.add(dayReportModel);
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
            arrayList = (ArrayList<DayReportModel>) results.values;
            notifyDataSetChanged();
        }
    }

}
