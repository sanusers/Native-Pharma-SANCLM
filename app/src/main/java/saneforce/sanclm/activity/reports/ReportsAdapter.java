package saneforce.sanclm.activity.reports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.internal.report.model.Report;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.MainActivity;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {

    ArrayList<String> arrayList = new ArrayList<>();
    Context context;

    public ReportsAdapter(ArrayList<String> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_adapter_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.MyViewHolder holder, int position) {
        String name = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.reportName.setText(name);

        switch (name.toUpperCase()){
            case "DAY REPORT" : {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_day_icon));
                break;
            }
            case "MONTHLY REPORT" : {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_monthly_icon));
                break;
            }
            case "DAY CHECK IN REPORT" : {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_day_check_icon));
                break;
            }
            case "CUSTOMER CHECK IN REPORT" : {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_customer_check_icon));
                break;
            }
            case "VISIT MONITOR" : {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.report_visit_monitor_icon));
                break;
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportsActivity activity = (ReportsActivity) context;
                activity.getData(name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView reportName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            reportName = itemView.findViewById(R.id.reportName);
        }
    }
}
