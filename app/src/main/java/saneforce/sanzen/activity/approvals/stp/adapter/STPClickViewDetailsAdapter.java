package saneforce.sanzen.activity.approvals.stp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.stp.model.STPDetailedModel;
import saneforce.sanzen.storage.SharedPref;

public class STPClickViewDetailsAdapter extends RecyclerView.Adapter<STPClickViewDetailsAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<STPDetailedModel> stpDetailedModels;

    public STPClickViewDetailsAdapter(Context context, ArrayList<STPDetailedModel> stpDetailedModels) {
        this.context = context;
        this.stpDetailedModels = stpDetailedModels;
    }

    @NonNull
    @Override
    public STPClickViewDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tp_detailed_clicked, parent, false);
        return new STPClickViewDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull STPClickViewDetailsAdapter.ViewHolder holder, int position) {
        holder.tv_wtName.setText(stpDetailedModels.get(position).getDayPlanName());
        holder.constraint_cluster.setVisibility(View.VISIBLE);
        holder.tv_cluster.setText(stpDetailedModels.get(position).getClusterName());
        holder.tag_cluster.setText(SharedPref.getClusterCap(context));
        String[] str;
        if(!stpDetailedModels.get(position).getClusterName().isEmpty()) {
            str = stpDetailedModels.get(position).getClusterName().split(",");
            holder.tv_cluster_count.setText(String.valueOf(str.length));
        }
        holder.constraint_hq.setVisibility(View.GONE);
        holder.constraint_remarks.setVisibility(View.GONE);
        holder.constraint_jw.setVisibility(View.GONE);

        if(SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
            holder.constraint_doctor.setVisibility(View.VISIBLE);
            holder.tv_drList.setText(stpDetailedModels.get(position).getDoctorName());
            holder.tag_dr.setText(SharedPref.getDrCap(context));
            if(!stpDetailedModels.get(position).getDoctorName().isEmpty()) {
                str = stpDetailedModels.get(position).getDoctorName().split(",");
                holder.tv_dr_count.setText(String.valueOf(str.length));
            }
        }

        if(SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
            holder.constraint_chemist.setVisibility(View.VISIBLE);
            holder.tv_chemistList.setText(stpDetailedModels.get(position).getChemistName());
            holder.tag_chemist.setText(SharedPref.getChmCap(context));
            if(!stpDetailedModels.get(position).getChemistName().isEmpty()) {
                str = stpDetailedModels.get(position).getChemistName().split(",");
                holder.tv_chemist_count.setText(String.valueOf(str.length));
            }
        }

    }

    @Override
    public int getItemCount() {
        return stpDetailedModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraint_cluster, constraint_doctor, constraint_chemist, constraint_stockiest, constraint_jw, constraint_remarks, constraint_hq;
        TextView tv_wtName, tag_cluster, tag_dr, tag_chemist, tag_stockist, tag_jw, tv_cluster, tv_drList, tv_chemistList, tv_stockiestList, tv_jwList, tv_remarks, tv_cluster_count, tv_dr_count, tv_chemist_count, tv_stockiest_count, tv_jw_count, tv_hq, tv_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_wtName = itemView.findViewById(R.id.tv_wt);
            constraint_cluster = itemView.findViewById(R.id.constraint_cluster);
            tv_cluster = itemView.findViewById(R.id.tv_cluster);
            tag_cluster = itemView.findViewById(R.id.tag_cluster);
            tv_cluster_count = itemView.findViewById(R.id.tv_cluster_count);
            constraint_doctor = itemView.findViewById(R.id.constraint_listed_doctor);
            tv_drList = itemView.findViewById(R.id.tv_listed_doctor);
            tag_dr = itemView.findViewById(R.id.tag_listed_doctor);
            tv_dr_count = itemView.findViewById(R.id.tv_dr_count);
            constraint_chemist = itemView.findViewById(R.id.constraint_listed_chemist);
            tv_chemistList = itemView.findViewById(R.id.tv_listed_chemist);
            tag_chemist = itemView.findViewById(R.id.tag_listed_chemist);
            tv_chemist_count = itemView.findViewById(R.id.tv_chemist_count);
            constraint_stockiest = itemView.findViewById(R.id.constraint_listed_stockist);
            tv_stockiestList = itemView.findViewById(R.id.tv_listed_stockist);
            tag_stockist = itemView.findViewById(R.id.tag_listed_stockist);
            tv_stockiest_count = itemView.findViewById(R.id.tv_stockiest_count);
            constraint_jw = itemView.findViewById(R.id.constraint_listed_jw);
            tv_jwList = itemView.findViewById(R.id.tv_listed_jw);
            tag_jw = itemView.findViewById(R.id.tag_listed_jw);
            tv_jw_count = itemView.findViewById(R.id.tv_jw_count);
            constraint_remarks = itemView.findViewById(R.id.constraint_remarks);
            tv_remarks = itemView.findViewById(R.id.tv_remarks);
            tv_hq = itemView.findViewById(R.id.tv_hq);
            tv_count = itemView.findViewById(R.id.tv_hq_count);
            constraint_hq = itemView.findViewById(R.id.constraint_hq);


        }
    }
}
