package saneforce.sanzen.activity.standardTourPlan.addListScreen.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.model.ClusterModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class SelectedDCRAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> dcrModelList;
    private static final int VIEW_TYPE_CLUSTER = 0;
    private static final int VIEW_TYPE_DCR = 1;
    private String selectedDCR;
    private CommonUtilsMethods commonUtilsMethods;
    private DeleteClickListener deleteClickListener;

    public interface DeleteClickListener {
        void onDeleted(DCRModel dcrModel, String selectedDCR);
    }

    public SelectedDCRAdapter() {
    }

    public SelectedDCRAdapter(Context context, List<Object> dcrModelList, String selectedDCR, DeleteClickListener deleteClickListener) {
        this.context = context;
        this.dcrModelList = dcrModelList;
        this.selectedDCR = selectedDCR;
        this.deleteClickListener = deleteClickListener;
        this.commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_CLUSTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stp_cluster_header, parent, false);
            return new ClusterViewHolder(view);
        }else if(viewType == VIEW_TYPE_DCR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stp_add_list_selected_dcr, parent, false);
            return new DCRViewHolder(view);
        }else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ClusterViewHolder) {
            ClusterViewHolder clusterViewHolder = (ClusterViewHolder) holder;
            ClusterModel clusterModel = (ClusterModel) dcrModelList.get(position);
            clusterViewHolder.name.setText(clusterModel.getClusterName());
        }else if(holder instanceof DCRViewHolder) {
            DCRViewHolder dcrViewHolder = (DCRViewHolder) holder;
            DCRModel dcrModel = (DCRModel) dcrModelList.get(position);
            dcrViewHolder.name.setText(dcrModel.getName());
            dcrViewHolder.delete.setOnClickListener(view -> {
                deleteClickListener.onDeleted(dcrModel, selectedDCR);
            });
            dcrViewHolder.name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(context, view, dcrModel.getName()));
        }
    }

    public void updateList(List<Object> dataList) {
        dcrModelList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(dcrModelList.get(position) instanceof ClusterModel) {
            return VIEW_TYPE_CLUSTER;
        }else if(dcrModelList.get(position) instanceof DCRModel) {
            return VIEW_TYPE_DCR;
        }else {
            Log.e("DCR Adapter STP", "getItemViewType: invalid view type");
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        return dcrModelList.size();
    }

    public static class DCRViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView delete;

        public DCRViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_dcr_name);
            delete = itemView.findViewById(R.id.img_delete);
        }
    }

    public static class ClusterViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public ClusterViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_cluster_name);
        }
    }

}
