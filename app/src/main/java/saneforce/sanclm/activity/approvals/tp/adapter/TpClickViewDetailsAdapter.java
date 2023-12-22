package saneforce.sanclm.activity.approvals.tp.adapter;

import static saneforce.sanclm.activity.approvals.ApprovalsActivity.ChemistCaption;
import static saneforce.sanclm.activity.approvals.ApprovalsActivity.ClusterCaption;
import static saneforce.sanclm.activity.approvals.ApprovalsActivity.DrCaption;
import static saneforce.sanclm.activity.approvals.ApprovalsActivity.StockistCaption;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.tp.pojo.TpDetailedModel;

public class TpClickViewDetailsAdapter extends RecyclerView.Adapter<TpClickViewDetailsAdapter.ViewHolder> {
    Context context;
    ArrayList<TpDetailedModel> tpDetailedModels;
    String[] str;

    public TpClickViewDetailsAdapter(Context context, ArrayList<TpDetailedModel> tpDetailedModels) {
        this.context = context;
        this.tpDetailedModels = tpDetailedModels;
    }

    @NonNull
    @Override
    public TpClickViewDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tp_detailed_clicked, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TpClickViewDetailsAdapter.ViewHolder holder, int position) {
        holder.tv_wtName.setText(tpDetailedModels.get(position).getWtName());
        if (tpDetailedModels.get(position).getFWFlg().equalsIgnoreCase("F")) {
            holder.constraint_cluster.setVisibility(View.VISIBLE);
            holder.tv_cluster.setText(tpDetailedModels.get(position).getClusterName());
            holder.tag_cluster.setText(ClusterCaption);
            if (!tpDetailedModels.get(position).getClusterName().isEmpty()) {
                str = tpDetailedModels.get(position).getClusterName().split(",");
                holder.tv_cluster_count.setText(String.valueOf(str.length));
            }

            holder.constraint_doctor.setVisibility(View.VISIBLE);
            holder.tv_drList.setText(tpDetailedModels.get(position).getDrName());
            holder.tag_dr.setText(DrCaption);
            if (!tpDetailedModels.get(position).getDrName().isEmpty()) {
                str = tpDetailedModels.get(position).getDrName().split(",");
                holder.tv_dr_count.setText(String.valueOf(str.length));
            }

            holder.constraint_chemist.setVisibility(View.VISIBLE);
            holder.tv_chemistList.setText(tpDetailedModels.get(position).getChemistName());
            holder.tag_chemist.setText(ChemistCaption);
            if (!tpDetailedModels.get(position).getChemistName().isEmpty()) {
                str = tpDetailedModels.get(position).getChemistName().split(",");
                holder.tv_chemist_count.setText(String.valueOf(str.length));
            }

            holder.constraint_stockiest.setVisibility(View.VISIBLE);
            holder.tv_stockiestList.setText(tpDetailedModels.get(position).getStockiestName());
            holder.tag_stockist.setText(StockistCaption);
            if (!tpDetailedModels.get(position).getStockiestName().isEmpty()) {
                str = tpDetailedModels.get(position).getStockiestName().split(",");
                holder.tv_stockiest_count.setText(String.valueOf(str.length));
            }

            holder.constraint_jw.setVisibility(View.VISIBLE);
            holder.tv_jwList.setText(tpDetailedModels.get(position).getJwName());
            if (!tpDetailedModels.get(position).getJwName().isEmpty()) {
                str = tpDetailedModels.get(position).getJwName().split(",");
                holder.tv_jw_count.setText(String.valueOf(str.length));
            }

            holder.constraint_remarks.setVisibility(View.VISIBLE);
            holder.tv_remarks.setText(tpDetailedModels.get(position).getDayRemarks());
        } else {
            holder.constraint_cluster.setVisibility(View.GONE);
            holder.constraint_doctor.setVisibility(View.GONE);
            holder.constraint_chemist.setVisibility(View.GONE);
            holder.constraint_stockiest.setVisibility(View.GONE);
            holder.constraint_jw.setVisibility(View.GONE);
            holder.constraint_remarks.setVisibility(View.VISIBLE);
            holder.tv_remarks.setText(tpDetailedModels.get(position).getDayRemarks());

        }
    }

    @Override
    public int getItemCount() {
        return tpDetailedModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraint_cluster, constraint_doctor, constraint_chemist, constraint_stockiest, constraint_jw, constraint_remarks;
        TextView tv_wtName, tag_cluster,tag_dr,tag_chemist,tag_stockist,tag_jw,tv_cluster, tv_drList, tv_chemistList, tv_stockiestList, tv_jwList, tv_remarks, tv_cluster_count, tv_dr_count, tv_chemist_count, tv_stockiest_count, tv_jw_count;

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
        }
    }
}