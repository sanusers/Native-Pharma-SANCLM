package saneforce.sanzen.activity.tourPlan.overview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.overview.model.CategoryWiseModel;
import saneforce.sanzen.activity.tourPlan.overview.model.VisitModel;

public class CategoryDataAdapter  extends RecyclerView.Adapter<CategoryDataAdapter.ViewHolder> {
    private Context context;
    private List<CategoryWiseModel> categoryWiseModelList;
    private CategoryClickListener categoryClickListener;

    public enum CategoryClickType{
        PLANNED_DOCTORS,
        PLANNED_VISITS,
        UNPLANNED_VISITS
    }

    public interface CategoryClickListener {
        void onCategoryClick(CategoryClickType categoryClickType, CategoryWiseModel categoryWiseModel);
    }

    public CategoryDataAdapter() {
    }

    public CategoryDataAdapter(Context context, List<CategoryWiseModel> categoryWiseModelList, CategoryClickListener categoryClickListener) {
        this.context = context;
        this.categoryWiseModelList = categoryWiseModelList;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_wise_tp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryWiseModel categoryWiseModel = categoryWiseModelList.get(position);
        if (categoryWiseModel != null) {
            holder.category.setText(categoryWiseModel.getName() + " (" + categoryWiseModel.getFrequency() + ")");
            Map<String, String> totalDoctors = categoryWiseModel.getTotalDoctors(), plannedDoctors = categoryWiseModel.getPlannedDoctors();
            Map<String, VisitModel> plannedVisit = categoryWiseModel.getPlannedVisit();
            holder.totalDoctors.setText(String.valueOf(totalDoctors.size()));
            holder.totalVisits.setText(String.valueOf((totalDoctors.size() * categoryWiseModel.getFrequency())));
            holder.plannedDoctors.setText(String.valueOf(plannedDoctors.size()));
            int plannedVisitCount = 0;
            for (VisitModel visitModel : plannedVisit.values()) {
                plannedVisitCount += visitModel.getDates().size();
            }
            holder.plannedVisits.setText(String.valueOf(plannedVisitCount));

            Map<String, Integer> unplanned = new HashMap<>();
            int unplannedVisitCount = 0;
            for (String drCode : totalDoctors.keySet()) {
                if (!plannedDoctors.containsKey(drCode)) {
                    unplannedVisitCount += categoryWiseModel.getFrequency();
                    unplanned.put(drCode, categoryWiseModel.getFrequency());
                } else {
                    VisitModel visitModel = plannedVisit.get(drCode);
                    if (visitModel != null) {
                        unplannedVisitCount += (categoryWiseModel.getFrequency() - visitModel.getDates().size());
                        unplanned.put(drCode, visitModel.getDates().size());
                    } else {
                        unplannedVisitCount += categoryWiseModel.getFrequency();
                        unplanned.put(drCode, categoryWiseModel.getFrequency());
                    }
                }
            }

            holder.unplannedVisits.setText(String.valueOf(unplannedVisitCount));

            holder.plannedDoctors.setOnClickListener(view -> categoryClickListener.onCategoryClick(CategoryClickType.PLANNED_DOCTORS, categoryWiseModel));
            holder.plannedVisits.setOnClickListener(view -> categoryClickListener.onCategoryClick(CategoryClickType.PLANNED_VISITS, categoryWiseModel));
            holder.unplannedVisits.setOnClickListener(view -> categoryClickListener.onCategoryClick(CategoryClickType.UNPLANNED_VISITS, categoryWiseModel));
        }
    }

    @Override
    public int getItemCount() {
        return categoryWiseModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, totalDoctors, totalVisits, plannedDoctors, plannedVisits, unplannedVisits;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category);
            totalDoctors = itemView.findViewById(R.id.tv_total_doctors);
            totalVisits = itemView.findViewById(R.id.tv_total_visits);
            plannedDoctors = itemView.findViewById(R.id.tv_planned_doctors);
            plannedVisits = itemView.findViewById(R.id.tv_planned_visits);
            unplannedVisits = itemView.findViewById(R.id.tv_unplanned_visits);
        }
    }
}
