package saneforce.sanzen.activity.reports.missedReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;

public class DoctorVisitAdapter extends RecyclerView.Adapter<DoctorVisitAdapter.ViewHolder> {

    private final Context context;
    private final List<DoctorVisitItem> doctorList;

    public DoctorVisitAdapter(Context context, List<DoctorVisitItem> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor_visit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorVisitItem item = doctorList.get(position);
        holder.textDoctor.setText(item.getName());
        holder.textPlace.setText(item.getTerritory());
        holder.qualification.setText(item.getQualification());
        holder.category.setText(item.getCategory());
        holder.speciality.setText(item.getSpeciality());
        holder.className.setText(item.getClassName());
        holder.itemView.setOnClickListener(v -> {
            });
    }
    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDoctor, textPlace;
        TextView qualification, category, speciality, className;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDoctor = itemView.findViewById(R.id.textDoctor);
            textPlace = itemView.findViewById(R.id.textPlace);
            qualification = itemView.findViewById(R.id.Qualification);
            category = itemView.findViewById(R.id.Category);
            speciality = itemView.findViewById(R.id.Speciality);
            className = itemView.findViewById(R.id.Class);
        }
    }
}
