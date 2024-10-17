package saneforce.sanzen.activity.standardTourPlan.addListScreen.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.AddListActivity;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.model.ClusterModel;
import saneforce.sanzen.activity.standardTourPlan.addListScreen.model.NoDataModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;

public class DCRSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> filtereddcrModelList;
    private List<Object> dcrModelList;
    private static final int VIEW_TYPE_CLUSTER = 0;
    private static final int VIEW_TYPE_DCR = 1;
    private static final int VIEW_TYPE_NO_DATA = 2;
    private CheckBoxClickListener checkBoxClickListener;
    private String selectedDCR;
    private CommonUtilsMethods commonUtilsMethods;

    public interface CheckBoxClickListener {

        void onSelected(DCRModel dcrModel, String selectedDCR);

        void onDeSelected(DCRModel dcrModel, String selectedDCR);
    }

    public DCRSelectionAdapter() {
    }

    public DCRSelectionAdapter(Context context, List<Object> filtereddcrModelList, CheckBoxClickListener checkBoxClickListener, String selectedDCR) {
        this.context = context;
        this.filtereddcrModelList = filtereddcrModelList;
        this.dcrModelList = filtereddcrModelList;
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
        }else if(viewType == VIEW_TYPE_NO_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_data, parent, false);
            return new NoDataViewHolder(view);
        }else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NoDataViewHolder) {
            NoDataViewHolder noDataViewHolder = (NoDataViewHolder) holder;
            NoDataModel noDataModel = (NoDataModel) filtereddcrModelList.get(position);
            noDataViewHolder.noData.setText("No " + noDataModel.getCaption() + " found");
        }else if(holder instanceof ClusterViewHolder) {
            ClusterViewHolder clusterViewHolder = (ClusterViewHolder) holder;
            ClusterModel clusterModel = (ClusterModel) filtereddcrModelList.get(position);
            clusterViewHolder.name.setText(clusterModel.getClusterName());
        }else if(holder instanceof DCRViewHolder) {
            DCRViewHolder dcrViewHolder = (DCRViewHolder) holder;
            DCRModel dcrModel = (DCRModel) filtereddcrModelList.get(position);

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

            try {
                if(context instanceof AddListActivity) {
                    dcrViewHolder.checkBox.setVisibility(View.VISIBLE);
                    dcrViewHolder.plannedFor.setText(CommonUtilsMethods.removeLastComma(dcrModel.getPlannedForName()));
                    dcrViewHolder.plannedFor.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(context, view, CommonUtilsMethods.removeLastComma(dcrModel.getPlannedForName())));
                }else {
                    dcrViewHolder.checkBox.setVisibility(View.GONE);

                    ViewGroup.MarginLayoutParams dcrNameParams = (ViewGroup.MarginLayoutParams) dcrViewHolder.name.getLayoutParams();
                    dcrNameParams.leftMargin = 20;
                    dcrViewHolder.name.setLayoutParams(dcrNameParams);

                    String[] codeList = CommonUtilsMethods.removeLastComma(dcrModel.getPlannedForCode()).split(",");
                    codeList = Arrays.stream(codeList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
                    if(dcrModel.getVisitFrequency()>0) {
                        dcrViewHolder.plannedFor.setText(String.valueOf(dcrModel.getVisitFrequency() - codeList.length));
                    }else {
                        dcrViewHolder.plannedFor.setText("0");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            dcrViewHolder.checkBox.setChecked(dcrModel.isSelected());
            dcrViewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.green_2 : R.color.dark_purple)));

            dcrViewHolder.name.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
            dcrViewHolder.speciality.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
            dcrViewHolder.categoryXVisitFreq.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
            dcrViewHolder.plannedFor.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));

            dcrViewHolder.checkBox.setOnClickListener(buttonView -> {
                Log.d("Adapter", "onBindViewHolder: position -> " + position + " binding position -> " + dcrViewHolder.getBindingAdapterPosition() + " absolute position -> " + dcrViewHolder.getAbsoluteAdapterPosition());
                String[] docList = CommonUtilsMethods.removeLastComma(dcrModel.getPlannedForCode()).split(",");
                docList = Arrays.stream(docList).filter(str -> str != null && !str.isEmpty() && !str.equals(",")).toArray(String[]::new);
                if(docList.length<dcrModel.getVisitFrequency() && selectedDCR.equalsIgnoreCase(Constants.DOCTOR)) {
                    updateDcrModelAndViews(dcrModel, dcrViewHolder, position);
                }else if(selectedDCR.equalsIgnoreCase(Constants.DOCTOR)) {
                    commonUtilsMethods.showToastMessage(context, "Visit Frequency already met");
                    dcrViewHolder.checkBox.setChecked(false);
                }else {
                    updateDcrModelAndViews(dcrModel, dcrViewHolder, position);
                }
            });

            dcrViewHolder.name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(context, view, dcrModel.getName()));
        }
    }

    private void updateDcrModelAndViews(DCRModel dcrModel, DCRViewHolder dcrViewHolder, int position) {
        dcrModel.setSelected(!dcrModel.isSelected());
        dcrViewHolder.checkBox.setChecked(dcrModel.isSelected());
        dcrViewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.green_2 : R.color.dark_purple)));
        dcrViewHolder.name.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
        dcrViewHolder.speciality.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
        dcrViewHolder.categoryXVisitFreq.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
        dcrViewHolder.plannedFor.setTextColor(ContextCompat.getColor(context, dcrModel.isSelected() ? R.color.dark_purple : R.color.bg_txt_color));
        notifyItemChanged(position);
        if(dcrModel.isSelected()) {
            checkBoxClickListener.onSelected(dcrModel, selectedDCR);
        }else {
            checkBoxClickListener.onDeSelected(dcrModel, selectedDCR);
        }
    }

    @Override
    public int getItemCount() {
        return filtereddcrModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(filtereddcrModelList.get(position) instanceof ClusterModel) {
            return VIEW_TYPE_CLUSTER;
        }else if(filtereddcrModelList.get(position) instanceof DCRModel) {
            return VIEW_TYPE_DCR;
        }else if(filtereddcrModelList.get(position) instanceof NoDataModel) {
            return VIEW_TYPE_NO_DATA;
        }else {
            Log.e("DCR Adapter STP", "getItemViewType: invalid view type");
            return super.getItemViewType(position);
        }
    }

    public void updateList(List<Object> dataList) {
        filtereddcrModelList = dataList;
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                List<DCRModel> filtered = new ArrayList<>();
                for (Object data : dcrModelList) {
                    if(data instanceof DCRModel) {
                        if(((DCRModel) data).getName().toLowerCase().contains(searchString)
                                || ((DCRModel) data).getPlannedForName().toLowerCase().contains(searchString)
                                || ((DCRModel) data).getSpeciality().toLowerCase().contains(searchString)
                                || ((DCRModel) data).getCategory().toLowerCase().contains(searchString)) {
                            filtered.add((DCRModel) data);
                        }
                    }
                }

                HashMap<String, List<DCRModel>> clusterXDcrMap = new HashMap<>();
                HashMap<String, String> clusterMap = new HashMap<>();
                List<Object> dataList = new ArrayList<>();

                for (DCRModel dcrModel : filtered) {
                    if(!clusterXDcrMap.containsKey(dcrModel.getTownCode())) {
                        clusterXDcrMap.put(dcrModel.getTownCode(), new ArrayList<>());
                        clusterMap.put(dcrModel.getTownCode(), dcrModel.getTownName());
                    }
                    List<DCRModel> dcrModels = clusterXDcrMap.get(dcrModel.getTownCode());
                    if(dcrModels == null) {
                        dcrModels = new ArrayList<>();
                    }
                    dcrModels.add(dcrModel);
                    clusterXDcrMap.put(dcrModel.getTownCode(), dcrModels);
                }

                List<Map.Entry<String, String>> clusterEntries = new ArrayList<>(clusterMap.entrySet());
                Collections.sort(clusterEntries, Map.Entry.comparingByValue());

                for (Map.Entry<String, String> entry : clusterEntries) {
                    String clusterCode = entry.getKey();
                    dataList.add(new ClusterModel(clusterCode, clusterMap.get(clusterCode)));
                    List<DCRModel> dcrModels = clusterXDcrMap.get(clusterCode);
                    if(dcrModels != null && !dcrModels.isEmpty()) {
                        dcrModels.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
                    }
                    dataList.addAll(dcrModels);
                }

                filtereddcrModelList = dataList;
                FilterResults filterResults = new FilterResults();
                filterResults.values = filtereddcrModelList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filtereddcrModelList = (List<Object>) results.values;
                notifyDataSetChanged();
            }
        };
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

    public static class NoDataViewHolder extends RecyclerView.ViewHolder {

        TextView noData;

        public NoDataViewHolder(@NonNull View itemView) {
            super(itemView);
            noData = itemView.findViewById(R.id.tv_no_data);
        }
    }

}
