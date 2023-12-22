package saneforce.sanclm.activity.approvals.tpdeviation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;

public class TpDeviationAdapter extends RecyclerView.Adapter<TpDeviationAdapter.ViewHolder> {
    Context context;
    ArrayList<TpDeviationModelList> tpDeviationModelLists;

    public TpDeviationAdapter(Context context, ArrayList<TpDeviationModelList> tpDeviationModelLists) {
        this.context = context;
        this.tpDeviationModelLists = tpDeviationModelLists;
    }

    @NonNull
    @Override
    public TpDeviationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tpdeviation_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TpDeviationAdapter.ViewHolder holder, int position) {
        holder.tv_WorkTypeS1.setText(tpDeviationModelLists.get(position).getWtName());
        holder.tv_clusterS1.setText(tpDeviationModelLists.get(position).getCluster());
        holder.tv_hqS1.setText(tpDeviationModelLists.get(position).getHq());
        holder.tv_remarkS1.setText(tpDeviationModelLists.get(position).getRemarks());
        holder.tv_deviationRemarks.setText(tpDeviationModelLists.get(position).getDeviationRemarks());
    }

    @Override
    public int getItemCount() {
        return tpDeviationModelLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_WorkTypeS1, tv_clusterS1, tv_hqS1, tv_remarkS1, tv_deviationRemarks;
        Button btn_reject, btn_approve;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_WorkTypeS1 = itemView.findViewById(R.id.tv_worktype_1);
            tv_clusterS1 = itemView.findViewById(R.id.tv_cluster_1);
            tv_hqS1 = itemView.findViewById(R.id.tv_hq_1);
            tv_remarkS1 = itemView.findViewById(R.id.tv_remark_1);
            tv_deviationRemarks = itemView.findViewById(R.id.tv_deviation_remarks);
            btn_reject = itemView.findViewById(R.id.btn_reject);
            btn_approve = itemView.findViewById(R.id.btn_approved);
        }
    }
}
