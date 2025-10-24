//package saneforce.sanzen.activity.reports;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.gson.JsonObject;
//
//import java.util.ArrayList;
//
//import saneforce.sanzen.R;
//import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
//import saneforce.sanzen.activity.reports.dayReport.model.SubMenuModel;
//import saneforce.sanzen.commonClasses.SafeClickListener;
////import saneforce.sanzen.activity.reports.missedReport.MissedReport;
//import saneforce.sanzen.activity.reports.visitMonitor.VisitMonitorActivity;
//import saneforce.sanzen.activity.reports.missedReport.MissedReport;
//import saneforce.sanzen.activity.reports.missedReport.MissedReportGraph;
//import saneforce.sanzen.commonClasses.CommonUtilsMethods;
//import saneforce.sanzen.storage.SharedPref;
//import saneforce.sanzen.utility.TimeUtils;
//
//public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {
//
//    ArrayList<String> arrayList;
//    Context context;
//    OnReportClickListener listener;
//
//        public ReportsAdapter(ArrayList<String> arrayList, Context context) {
//        this.arrayList = arrayList;
//        this.context = context;
//    }
//    public ReportsAdapter(ArrayList<String> arrayList, Context context, OnReportClickListener listener) {
//        this.arrayList = arrayList;
//        this.context = context;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public ReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_adapter_item, parent, false);
//        return new MyViewHolder(view);
//    }
//
////    @Override
////    public void onBindViewHolder(@NonNull ReportsAdapter.MyViewHolder holder, int position) {
////        String name = arrayList.get(holder.getAbsoluteAdapterPosition());
////        holder.reportName.setText(name);
////        switch (name.toUpperCase()) {
////            case "DAY REPORT": {
////                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_day_icon));
////                break;
////            }
////            case "MONTHLY REPORT": {
////                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_monthly_icon));
////                break;
////            }
////            case "DAY CHECK IN REPORT": {
////                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_day_check_icon));
////                break;
////            }
////            case "CUSTOMER CHECK IN REPORT": {
////                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_customer_check_icon));
////                break;
////            }
////            case "VISIT MONITOR": {
////                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_visit_monitor_icon));
////                break;
////            }
////            case "DASH BOARD": {
////                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_monthly_icon));
////                break;
////            }
////            case "Missed Report" : {
////                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_day_icon));
////                break;
////            }
////        }
////
////        holder.itemView.setOnClickListener(new SafeClickListener() {
////            @Override
////            public void onSafeClick(View view) {
////                if (name.equalsIgnoreCase("Dash Board")) {
////                    Intent intentWeb = new Intent(context, ReportWebActivity.class);
////                    context.startActivity(intentWeb);
////                } else if (name.equalsIgnoreCase("Missed Report")) {
////                    //Intent intentWeb= new Intent(context, MissedReport.class);
////                    Intent intentWeb = new Intent(context, MissedReportGraph.class);
////                    context.startActivity(intentWeb);
////                } else if (name.equalsIgnoreCase("Day Report")) {
////                    ReportsActivity activity = (ReportsActivity) context;
////                    activity.progressDialog = CommonUtilsMethods.createProgressDialog(context);
////                    activity.getData(name, TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_4));
////                }else if (name.equalsIgnoreCase("Visit Monitor")) {
////                    Intent intent = new Intent(context, VisitMonitorActivity.class);
////                    context.startActivity(intent);
////                } else{
////                    if(SharedPref.getDynamicOptionNeed(context).equalsIgnoreCase("0")){
////                        Intent intent = new Intent(context, DynamicWebActivity.class);
////                        intent.putExtra("title", name);
////                     /*   String Menu_Page = SharedPref.getTagImageUrl(context)+ "/" + ReportsActivity.menuObject.get("Menu_Page").getAsString() + "?";
////                        Menu_Page = Menu_Page + "sfcode=" + SharedPref.getSfCode(context)
////                                + "&rSF=" +  SharedPref.getHqCode(context)
////                                + "&div_code=" + SharedPref.getDivisionCode(context)
////                                + "&cMnth=" + TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_9)
////                                + "&cYr=" +  TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_12)
////                                + "&doc_id=-1&IsDocView=0&cluster_code=-1";
////                        intent.putExtra("url", Menu_Page);*/
//
//    /// /                        intent.putExtra("url", MenuModel.class.arrayType());
////                        context.startActivity(intent);
////                    }
////                }
////            }
////        });
////    }
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        String name = arrayList.get(holder.getAbsoluteAdapterPosition());
//        holder.reportName.setText(name);
//
//        // set icons as before...
//        switch (name.toUpperCase()) {
//            case "DAY REPORT":
//                holder.imageView.setImageResource(R.drawable.report_day_icon);
//                break;
//            case "VISIT MONITOR":
//                holder.imageView.setImageResource(R.drawable.report_visit_monitor_icon);
//                break;
//            case "MISSED REPORT":
//                holder.imageView.setImageResource(R.drawable.report_day_icon);
//                break;
//            case "DASH BOARD":
//                holder.imageView.setImageResource(R.drawable.report_monthly_icon);
//                break;
//        }
//
//        holder.itemView.setOnClickListener(new SafeClickListener() {
//            @Override
//            public void onSafeClick(View v) {
//                listener.onReportClick(name);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrayList.size();
//    }
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView imageView;
//        TextView reportName;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.image);
//            reportName = itemView.findViewById(R.id.reportName);
//        }
//    }
//
//    public interface OnReportClickListener {
//        void onReportClick(String reportName);
//    }
//}

package saneforce.sanzen.activity.reports;

import android.content.Context;
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
//import saneforce.sanzen.activity.reports.missedReport.MissedReport;
import saneforce.sanzen.activity.reports.missedReport.MissedReportGraph;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {

    ArrayList<String> arrayList;
    Context context;
    OnReportClickListener listener;

    // Use the listener-based constructor as the main one
    public ReportsAdapter(ArrayList<String> arrayList, Context context, OnReportClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
    }
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.reportName.setText(name);

        // Set icons for hardcoded reports. Dynamic reports will need a default icon or a separate logic
        switch (name.toUpperCase()) {
            case "DAY REPORT":
                holder.imageView.setImageResource(R.drawable.report_day_icon);
                break;
            case "VISIT MONITOR":
                holder.imageView.setImageResource(R.drawable.report_visit_monitor_icon);
                break;
            case "MISSED REPORT":
                holder.imageView.setImageResource(R.drawable.report_day_icon);
                break;
            case "DASH BOARD":
                holder.imageView.setImageResource(R.drawable.report_monthly_icon);
                break;
            default:
                holder.imageView.setImageResource(0);
                break;
        }

        holder.itemView.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                // Delegate click event back to the Activity
                if (listener != null) {
                    listener.onReportClick(name);
                }
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

    public interface OnReportClickListener {
        void onReportClick(String reportName);
    }
}
