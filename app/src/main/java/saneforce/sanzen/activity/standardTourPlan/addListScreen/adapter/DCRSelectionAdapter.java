package saneforce.sanzen.activity.standardTourPlan.addListScreen.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.ClusterModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;

public class DCRSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> dcrModelList;
    private static final int VIEW_TYPE_CLUSTER = 0;
    private static final int VIEW_TYPE_DCR = 1;
    private CheckBoxClickListener checkBoxClickListener;

    public interface CheckBoxClickListener {

        void onSelected(DCRModel dcrModel, int position);

        void onDeSelected(DCRModel dcrModel, int position);
    }

    public DCRSelectionAdapter() {
    }

    public DCRSelectionAdapter(Context context, List<Object> dcrModelList, CheckBoxClickListener checkBoxClickListener) {
        this.context = context;
        this.dcrModelList = dcrModelList;
        this.checkBoxClickListener = checkBoxClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_CLUSTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stp_cluster_header, parent, false);
            return new ClusterViewHolder(view);
        }else if(viewType == VIEW_TYPE_DCR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stp_add_list_dcr, parent, false);
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
            dcrViewHolder.speciality.setText(dcrModel.getSpeciality());
            dcrViewHolder.categoryXVisitFreq.setText(dcrModel.getCategory() + " (" + dcrModel.getVisitFrequency() + ")");
            dcrViewHolder.plannedFor.setText(dcrModel.getPlannedForName());
            dcrViewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d("Adapter", "onBindViewHolder: position -> " + position + " binding position -> " + dcrViewHolder.getBindingAdapterPosition() + " absolute position -> " + dcrViewHolder.getAbsoluteAdapterPosition());
                if(isChecked) {
                    checkBoxClickListener.onSelected(dcrModel, dcrViewHolder.getBindingAdapterPosition());
                    dcrViewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
                }else {
                    checkBoxClickListener.onDeSelected(dcrModel, dcrViewHolder.getBindingAdapterPosition());
                    dcrViewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_purple)));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dcrModelList.size();
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

    public static class DCRViewHolder extends RecyclerView.ViewHolder {

        TextView name, speciality, categoryXVisitFreq, plannedFor;
        CheckBox checkBox;

        public DCRViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_dcr);
            name = itemView.findViewById(R.id.tv_dcr_name);
            speciality = itemView.findViewById(R.id.tv_dcr_spec);
            categoryXVisitFreq = itemView.findViewById(R.id.tv_dcr_cat_x_visit);
            plannedFor = itemView.findViewById(R.id.tv_dcr_planned_for);
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
