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
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;

public class DCRSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> dcrModelList;
    private static final int VIEW_TYPE_CLUSTER = 0;
    private static final int VIEW_TYPE_DCR = 1;
    private CheckBoxClickListener checkBoxClickListener;
    private String selectedDCR;
    private CommonUtilsMethods commonUtilsMethods;

    public interface CheckBoxClickListener {

        void onSelected(DCRModel dcrModel, int position, String selectedDCR);

        void onDeSelected(DCRModel dcrModel, int position, String selectedDCR);
    }

    public DCRSelectionAdapter() {
    }

    public DCRSelectionAdapter(Context context, List<Object> dcrModelList, CheckBoxClickListener checkBoxClickListener, String selectedDCR) {
        this.context = context;
        this.dcrModelList = dcrModelList;
        this.checkBoxClickListener = checkBoxClickListener;
        this.selectedDCR = selectedDCR;
        commonUtilsMethods = new CommonUtilsMethods(context);
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

            switch (selectedDCR){
                case Constants.DOCTOR:
                    dcrViewHolder.speciality.setVisibility(View.VISIBLE);
                    dcrViewHolder.categoryXVisitFreq.setVisibility(View.VISIBLE);
                    break;
                case Constants.CHEMIST:
                    dcrViewHolder.speciality.setVisibility(View.GONE);
                    dcrViewHolder.categoryXVisitFreq.setVisibility(View.GONE);
                    break;

            }

            dcrViewHolder.name.setText(dcrModel.getName());
            dcrViewHolder.speciality.setText(dcrModel.getSpeciality());
            dcrViewHolder.categoryXVisitFreq.setText(dcrModel.getCategory() + " (" + dcrModel.getVisitFrequency() + ")");
            dcrViewHolder.plannedFor.setText(dcrModel.getPlannedForName());

            dcrViewHolder.checkBox.setChecked(dcrModel.isSelected());
            dcrViewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.green_2 : R.color.dark_purple)));

            dcrViewHolder.name.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
            dcrViewHolder.speciality.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
            dcrViewHolder.categoryXVisitFreq.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
            dcrViewHolder.plannedFor.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));

            dcrViewHolder.checkBox.setOnClickListener(buttonView -> {
                Log.d("Adapter", "onBindViewHolder: position -> " + position + " binding position -> " + dcrViewHolder.getBindingAdapterPosition() + " absolute position -> " + dcrViewHolder.getAbsoluteAdapterPosition());
                dcrModel.setSelected(!dcrModel.isSelected());
                dcrViewHolder.checkBox.setChecked(dcrModel.isSelected());
                dcrViewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.green_2 : R.color.dark_purple)));
                dcrViewHolder.name.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
                dcrViewHolder.speciality.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
                dcrViewHolder.categoryXVisitFreq.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
                dcrViewHolder.plannedFor.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
                notifyItemChanged(position);
                if(dcrModel.isSelected()) {
                    checkBoxClickListener.onSelected(dcrModel, holder.getBindingAdapterPosition(), selectedDCR);
                }else {
                    checkBoxClickListener.onDeSelected(dcrModel, holder.getBindingAdapterPosition(), selectedDCR);
                }
            });

            dcrViewHolder.name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(context, view, dcrModel.getName()));
            dcrViewHolder.plannedFor.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(context, view, dcrModel.getPlannedForName()));
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

    public void updateList(List<Object> dataList) {
        dcrModelList = dataList;
        notifyDataSetChanged();
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
