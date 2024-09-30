package saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DoctorCategoryXVisitFrequencyModel;

public class DocCategoryXVisitAdapter extends RecyclerView.Adapter<DocCategoryXVisitAdapter.MyViewHolder> {

    private Context context;
    private List<DoctorCategoryXVisitFrequencyModel> doctorCategoryXVisitFrequencyModelList;

    public DocCategoryXVisitAdapter() {
    }

    public DocCategoryXVisitAdapter(Context context, List<DoctorCategoryXVisitFrequencyModel> doctorCategoryXVisitFrequencyModelList) {
        this.context = context;
        this.doctorCategoryXVisitFrequencyModelList = doctorCategoryXVisitFrequencyModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doc_cat_x_visit_stp, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DoctorCategoryXVisitFrequencyModel doctorCategoryXVisitFrequencyModel = doctorCategoryXVisitFrequencyModelList.get(position);
        holder.category.setText(doctorCategoryXVisitFrequencyModel.getCategory());
        holder.visitFrequency.setText(String.valueOf(doctorCategoryXVisitFrequencyModel.getVisitFrequency()));
    }

    @Override
    public int getItemCount() {
        return doctorCategoryXVisitFrequencyModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView category, visitFrequency;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category);
            visitFrequency = itemView.findViewById(R.id.tv_visit_frequency);
        }
    }
}
