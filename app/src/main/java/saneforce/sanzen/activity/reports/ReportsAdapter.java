package saneforce.sanzen.activity.reports;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
//import saneforce.sanzen.activity.reports.missedReport.MissedReport;
import saneforce.sanzen.activity.reports.visitMonitor.VisitMonitorActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {

    ArrayList<String> arrayList;
    Context context;

    public ReportsAdapter(ArrayList<String> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_adapter_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.MyViewHolder holder, int position) {
        String name = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.reportName.setText(name);

        switch (name.toUpperCase()) {
            case "DAY REPORT": {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_day_icon));
                break;
            }
            case "MONTHLY REPORT": {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_monthly_icon));
                break;
            }
            case "DAY CHECK IN REPORT": {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_day_check_icon));
                break;
            }
            case "CUSTOMER CHECK IN REPORT": {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_customer_check_icon));
                break;
            }
            case "VISIT MONITOR": {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_visit_monitor_icon));
                break;
            }
            case "DASH BOARD": {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_monthly_icon));
                break;
            }
            case "MODULE": {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_monthly_icon));
                break;
            }
        }

        holder.itemView.setOnClickListener(view -> {
            if (name.equalsIgnoreCase("Dash Board")) {
                Intent intentWeb = new Intent(context, ReportWebActivity.class);
                context.startActivity(intentWeb);
            } /*else if (name.equalsIgnoreCase("Missed Report")) {
                Intent intentWeb= new Intent(context, MissedReport.class);
                context.startActivity(intentWeb);
            }*/ else if (name.equalsIgnoreCase("Day Report")) {
                ReportsActivity activity = (ReportsActivity) context;
                activity.progressDialog = CommonUtilsMethods.createProgressDialog(context);
                activity.getData(name, TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_4));
            } else if (SharedPref.getDynamicOptionNeed(context).equalsIgnoreCase("0") && name.equalsIgnoreCase(SharedPref.getDynamicOptionCaps(context))) {
                ReportsActivity activity = (ReportsActivity) context;
                activity.progressDialog = CommonUtilsMethods.createProgressDialog(context);
                activity.getDynamicData();
            }else if (name.equalsIgnoreCase("Visit Monitor")){
                Intent intent = new Intent(context, VisitMonitorActivity.class);
                context.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView reportName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            reportName = itemView.findViewById(R.id.reportName);
        }
    }
}
