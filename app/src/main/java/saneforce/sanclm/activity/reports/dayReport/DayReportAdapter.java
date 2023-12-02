package saneforce.sanclm.activity.reports.dayReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.reports.ReportFragContainerActivity;

public class DayReportAdapter extends RecyclerView.Adapter<DayReportAdapter.MyViewHolder> {
    ArrayList<DayReportModel> arrayList = new ArrayList<>();
    Context context;


    public DayReportAdapter(ArrayList<DayReportModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
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
//        holder.status.setText(dayReportModel.getS());

        if(!dayReportModel.getDrs().equals("0") && !dayReportModel.getDrs().equals("") && dayReportModel.getDrs() != null){
            holder.drCount.setText(dayReportModel.getDrs());
        }else{
            holder.drIcon.setVisibility(View.GONE);
        }

        if(!dayReportModel.getChm().equals("0") && !dayReportModel.getChm().equals("") && dayReportModel.getChm() != null){
            holder.chemCount.setText(dayReportModel.getDrs());
        }else{
            holder.cheIcon.setVisibility(View.GONE);
        }

        if(!dayReportModel.getStk().equals("0") && !dayReportModel.getStk().equals("") && dayReportModel.getStk() != null){
            holder.stockCount.setText(dayReportModel.getDrs());
        }else{
            holder.stockIcon.setVisibility(View.GONE);
        }

        if(!dayReportModel.getCip().equals("0") && !dayReportModel.getCip().equals("") && dayReportModel.getCip() != null){
            holder.cipCount.setText(dayReportModel.getDrs());
        }else{
            holder.cipIcon.setVisibility(View.GONE);
        }

        if(!dayReportModel.getUdr().equals("0") && !dayReportModel.getUdr().equals("") && dayReportModel.getUdr() != null){
            holder.unDrCount.setText(dayReportModel.getDrs());
        }else{
            holder.unDrIcon.setVisibility(View.GONE);
        }

        if(!dayReportModel.getHos().equals("0") && !dayReportModel.getHos().equals("") && dayReportModel.getHos() != null){
            holder.hospCount.setText(dayReportModel.getDrs());
        }else{
            holder.hospIcon.setVisibility(View.GONE);
        }

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportFragContainerActivity activity = (ReportFragContainerActivity) context;
                activity.loadFragment(new DayReportDetailFragment(), new Gson().toJson(dayReportModel));
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,workType,checkInTime,checkInAddress,checkOutTime,checkOutAddress,submitDate,remarks,status;
        TextView drCount,chemCount,stockCount,unDrCount,cipCount,hospCount;
        ConstraintLayout drIcon,cheIcon,stockIcon,cipIcon,unDrIcon,hospIcon;
        LinearLayout arrow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            workType = itemView.findViewById(R.id.workType);
            checkInTime = itemView.findViewById(R.id.checkInTime);
            checkInAddress = itemView.findViewById(R.id.inAddress);
            checkOutTime = itemView.findViewById(R.id.checkOutTime);
            checkOutAddress = itemView.findViewById(R.id.outAddress);
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

            arrow = itemView.findViewById(R.id.arrow);

        }
    }
}
