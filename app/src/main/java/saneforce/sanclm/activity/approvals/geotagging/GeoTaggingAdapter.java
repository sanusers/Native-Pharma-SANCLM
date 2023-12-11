package saneforce.sanclm.activity.approvals.geotagging;

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

public class GeoTaggingAdapter extends RecyclerView.Adapter<GeoTaggingAdapter.ViewHolder> {
    Context context;
    ArrayList<GeoTaggingModelList> geoTaggingModelLists;

    public GeoTaggingAdapter(Context context, ArrayList<GeoTaggingModelList> geoTaggingModelLists) {
        this.context = context;
        this.geoTaggingModelLists = geoTaggingModelLists;
    }

    @NonNull
    @Override
    public GeoTaggingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_geotagging, parent, false);
        return new GeoTaggingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GeoTaggingAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(geoTaggingModelLists.get(position).getName());
        holder.tv_hq.setText(geoTaggingModelLists.get(position).getHqName());
        holder.tv_cluster.setText(geoTaggingModelLists.get(position).getCluster());
        holder.tv_address.setText(geoTaggingModelLists.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return geoTaggingModelLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_hq, tv_cluster, tv_address;
        Button btn_approved, btn_rejected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_hq = itemView.findViewById(R.id.tv_hq);
            tv_cluster = itemView.findViewById(R.id.tv_cluster_geo);
            tv_address = itemView.findViewById(R.id.tv_address_geo);
            btn_approved = itemView.findViewById(R.id.btn_approved);
            btn_rejected = itemView.findViewById(R.id.btn_reject);
        }
    }
}
