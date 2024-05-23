package saneforce.sanzen.activity.reports.dayReport.adapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import java.util.ArrayList;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.geotagging.GeoTaggingModelList;
import saneforce.sanzen.activity.reports.ReportFragContainerActivity;
import saneforce.sanzen.activity.reports.dayReport.DataViewModel;
import saneforce.sanzen.activity.reports.dayReport.MapViewActvity;
import saneforce.sanzen.activity.reports.dayReport.fragment.DayReportDetailFragment;
import saneforce.sanzen.activity.reports.dayReport.model.DayReportModel;
import saneforce.sanzen.storage.SharedPref;


public class DayReportAdapter extends RecyclerView.Adapter<DayReportAdapter.MyViewHolder> implements Filterable {
    public static ArrayList<GeoTaggingModelList> DayReportMapList = new ArrayList<>();
    ArrayList<DayReportModel> arrayList = new ArrayList<>();
    ArrayList<DayReportModel> supportModelArray = new ArrayList<>();
    Context context;
    DataViewModel dataViewModel;


    public DayReportAdapter(ArrayList<DayReportModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.supportModelArray = arrayList;
        this.context = context;
        dataViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(DataViewModel.class);
    }

    @NonNull
    @Override
    public DayReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_day_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayReportAdapter.MyViewHolder holder, int position) {
        DayReportModel dayReportModel = arrayList.get(holder.getAbsoluteAdapterPosition());
        String address = dayReportModel.getInaddress();
        holder.name.setText(dayReportModel.getSF_Name());
        holder.workType.setText(dayReportModel.getWtype());
        holder.submitDate.setText(dayReportModel.getRptdate());
        holder.remarks.setText(dayReportModel.getRemarks());
        if (SharedPref.getSrtNd(context).equalsIgnoreCase("0")){
            if (dayReportModel.getInaddress().equals("")){
                holder.rlCheckIn.setVisibility(View.GONE);
            }else{
                holder.rlCheckIn.setVisibility(View.VISIBLE);
                holder.checkInTime.setText(dayReportModel.getIntime());
                holder.checkInAddress.setText(dayReportModel.getInaddress());
            }
            if (dayReportModel.getOutaddress().equals("")){
                holder.rlCheckOut.setVisibility(View.GONE);
            }else{
                holder.rlCheckOut.setVisibility(View.VISIBLE);
                holder.checkOutTime.setText(dayReportModel.getOuttime());
                holder.checkOutAddress.setText(dayReportModel.getOutaddress());
            }
        } else{
            holder.rlCheckIn.setVisibility(View.GONE);
            holder.rlCheckOut.setVisibility(View.GONE);
        }

        if (SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
            holder.drIcon.setVisibility(View.VISIBLE);
            holder.drCount.setText(dayReportModel.getDrs());
        }

        if (SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
            holder.cheIcon.setVisibility(View.VISIBLE);
            holder.chemCount.setText(dayReportModel.getChm());
        }

        if (SharedPref.getStkNeed(context).equalsIgnoreCase("0")) {
            holder.stockIcon.setVisibility(View.VISIBLE);
            holder.stockCount.setText(dayReportModel.getStk());
        }

        if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) {
            holder.unDrIcon.setVisibility(View.VISIBLE);
            holder.unDrCount.setText(dayReportModel.getUdr());
        }

        if (SharedPref.getCipNeed(context).equalsIgnoreCase("0")) {
            holder.cipIcon.setVisibility(View.VISIBLE);
            holder.cipCount.setText(dayReportModel.getCip());
        }

        if (SharedPref.getCipNeed(context).equalsIgnoreCase("0")) {
            holder.hospIcon.setVisibility(View.VISIBLE);
            holder.hospCount.setText(dayReportModel.getHos());
        }


        if(dayReportModel.getFWFlg().equalsIgnoreCase("F")){
            holder.arrow.setVisibility(View.VISIBLE);
            holder.DcrIconLayout.setVisibility(View.VISIBLE);
        }else {
            holder.arrow.setVisibility(View.GONE);
            holder.DcrIconLayout.setVisibility(View.GONE);
        }
       /* if(dayReportModel.getRemarks().equals("")){
           holder.remarksTxt.setVisibility(View.GONE);
           holder.remarks.setVisibility(View.GONE);
        }*/
       /* int status = dayReportModel.getTyp();
        switch (status) {
            case 0: {
                holder.status.setText(R.string.pending);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.text_dark_15)));
                break;
            }
            case 1: {
                holder.status.setText(R.string.approved);
                holder.status.setTextColor(context.getColor(R.color.green_2));
                holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.bg_priority)));
                break;
            }
            case 2: {
                holder.status.setText(R.string.rejected);
                holder.status.setTextColor(context.getColor(R.color.pink));
                holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.pink_15)));
                break;
            }
        }*/
        int status = dayReportModel.getTyp();
        String confirmStatus1 = dayReportModel.getConfirmed();
        int confirmStatus = Integer.parseInt(confirmStatus1);
        if (status==0 && confirmStatus==0) {
            holder.status.setText("Draft");
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.text_dark_15)));
        } else if ((status==0 || status==1) && confirmStatus==1) {
            holder.status.setText("Finished");
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.bg_priority)));
        } else if ((status==0 || status==1) && confirmStatus==2) {
            holder.status.setText("Rejected");
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.pink_15)));
        } else if ((status==0 || status==1) && confirmStatus==3) {
            holder.status.setText("ReEntry");
            holder.status.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.bg_lite_blue)));
        }


        holder.checkInMarker.setOnClickListener(view -> {
            Intent intent = new Intent(context, MapViewActvity.class);
            Bundle bundle = new Bundle();

            bundle.putString("INLat", "12.976810");
            bundle.putString("INLong", "80.221489");
            bundle.putString("OUTLat", "13.011760");
            bundle.putString("OUTLong", "80.221481");
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

        holder.checkOutMarker.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(context, MapViewActvity.class);
            bundle.putString("INLat", "12.976810");
            bundle.putString("INLong", "80.221489");
            bundle.putString("OUTLat", "13.011760");
            bundle.putString("OUTLong", "80.221481");
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

        holder.arrow.setOnClickListener(view -> {
            dataViewModel.saveDetailedData(new Gson().toJson(dayReportModel));
            ReportFragContainerActivity activity = (ReportFragContainerActivity) context;
            activity.loadFragment(new DayReportDetailFragment());
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

//    @Override
//    public Filter getFilter() {
//        if (valueFilter == null) {
//            valueFilter = new ValueFilter();
//        }
//        return valueFilter;
//    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, workType, checkInTime, checkInAddress, checkInMarker, checkOutTime, checkOutAddress, checkOutMarker, submitDate, remarks, status,remarksTxt;
        TextView drCount, chemCount, stockCount, unDrCount, cipCount, hospCount;
        ConstraintLayout drIcon, cheIcon, stockIcon, cipIcon, unDrIcon, hospIcon;
        LinearLayout arrow, statusLayout, DcrIconLayout;
        RelativeLayout rlCheckIn, rlCheckOut;

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
            DcrIconLayout = itemView.findViewById(R.id.iconLayout1);


            statusLayout = itemView.findViewById(R.id.statusLayout);
            arrow = itemView.findViewById(R.id.arrow);
            rlCheckIn = itemView.findViewById(R.id.rl_checkIn);
            rlCheckOut = itemView.findViewById(R.id.rl_checkOut);
            remarksTxt = itemView.findViewById(R.id.remarksTxt);
        }
    }

//    private class ValueFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(CharSequence charSequence) {
//            FilterResults results = new FilterResults();
//
//            ArrayList<DayReportModel> filteredModelArray = new ArrayList<>();
//            if (charSequence != null && charSequence.length() > 0) {
//                for (DayReportModel dayReportModel : supportModelArray) {
//                    if (dayReportModel.getTerrWrk().toUpperCase().contains(charSequence.toString().toUpperCase()) || dayReportModel.getWtype().toUpperCase().contains(charSequence.toString().toUpperCase())) {
//                        filteredModelArray.add(dayReportModel);
//                    }
//                }
//                results.count = filteredModelArray.size();
//                results.values = filteredModelArray;
//            } else {
//                results.count = supportModelArray.size();
//                results.values = supportModelArray;
//            }
//            return results;
//
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            arrayList = (ArrayList<DayReportModel>) results.values;
//            notifyDataSetChanged();
//        }
//    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                ArrayList<DayReportModel> filtered = new ArrayList<>();
                for (DayReportModel dayReportModel : supportModelArray) {
                    try {
                        if (dayReportModel.getTerrWrk().toUpperCase().contains(constraint.toString().toUpperCase()) || dayReportModel.getWtype().toUpperCase().contains(constraint.toString().toUpperCase())|| dayReportModel.getSF_Name().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            filtered.add(dayReportModel);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                arrayList = filtered;
                FilterResults filterResults = new FilterResults();
                filterResults.values = arrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayList = (ArrayList<DayReportModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }



}
