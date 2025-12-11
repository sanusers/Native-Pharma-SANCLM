package saneforce.sanzen.activity.tourPlan.overview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.overview.model.ClusterWiseModel;

public class ClusterDataAdapter extends RecyclerView.Adapter<ClusterDataAdapter.ViewHolder> {
    private Context context;
    private List<ClusterWiseModel> clusterWiseModelList;
    private boolean isDr = false;

    public ClusterDataAdapter() {
    }

    public ClusterDataAdapter(Context context, List<ClusterWiseModel> clusterWiseModelList, boolean isDr) {
        this.context = context;
        this.clusterWiseModelList = clusterWiseModelList;
        this.isDr = isDr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cluster_wise_tp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClusterWiseModel clusterWiseModel = clusterWiseModelList.get(position);
        if (clusterWiseModel != null) {
            holder.cluster.setText(clusterWiseModel.getName());
            holder.total.setText(String.valueOf(clusterWiseModel.getTotal().size()));
            holder.planned.setText(String.valueOf(clusterWiseModel.getPlanned().size()));
        }
        if (isDr) {
            holder.planned.setTextColor(context.getColor(R.color.green_60));
        } else {
            holder.planned.setTextColor(context.getColor(R.color.blue_60));
        }
    }

    @Override
    public int getItemCount() {
        return clusterWiseModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cluster, total, planned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cluster = itemView.findViewById(R.id.tv_cluster);
            total = itemView.findViewById(R.id.tv_total);
            planned = itemView.findViewById(R.id.tv_planned);
        }
    }
}
