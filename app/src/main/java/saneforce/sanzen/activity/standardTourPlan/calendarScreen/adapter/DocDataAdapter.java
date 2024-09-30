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
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DocDataModel;

public class DocDataAdapter extends RecyclerView.Adapter<DocDataAdapter.MyViewHolder> {

    private Context context;
    private List<DocDataModel> docDataModelList;

    public DocDataAdapter() {
    }

    public DocDataAdapter(Context context, List<DocDataModel> docDataModelList) {
        this.context = context;
        this.docDataModelList = docDataModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doc_data_stp, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DocDataModel docDataModel = docDataModelList.get(position);
        holder.category.setText(docDataModel.getCategory());
        holder.totalDoctors.setText(String.valueOf(docDataModel.getTotalDoctors()));
        holder.totalVisits.setText(String.valueOf(docDataModel.getTotalVisits()));
        holder.plannedDoctors.setText(String.valueOf(docDataModel.getPlannedDoctors()));
        holder.plannedVisits.setText(String.valueOf(docDataModel.getPlannedVisits()));
    }

    @Override
    public int getItemCount() {
        return docDataModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView category, totalDoctors, totalVisits, plannedVisits, plannedDoctors;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category);
            totalDoctors = itemView.findViewById(R.id.tv_total_doctors);
            totalVisits = itemView.findViewById(R.id.tv_total_visits);
            plannedDoctors = itemView.findViewById(R.id.tv_planned_doctors);
            plannedVisits = itemView.findViewById(R.id.tv_planned_visits);
        }
    }
}
