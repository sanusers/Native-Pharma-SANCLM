package saneforce.sanzen.activity.reports.missedReport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.map.custSelection.CustList;

public class DoctorVisitAdapter extends RecyclerView.Adapter<DoctorVisitAdapter.ViewHolder> {

    private final Context context;
    private  List<DoctorVisitItem> doctorList;
    private  List<DoctorVisitItem> fullList;


    public DoctorVisitAdapter(Context context, List<DoctorVisitItem> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
        this.fullList = new ArrayList<>(doctorList);
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
        holder.textDoctor.setText(checkEmpty(item.getName()));
        holder.textPlace.setText(checkEmpty(item.getTerritory()));
        holder.qualification.setText(checkEmpty(item.getQualification()));
        holder.category.setText(checkEmpty(item.getCategory()));
        holder.speciality.setText(checkEmpty(item.getSpeciality()));
        holder.className.setText(checkEmpty(item.getClassName()));

        holder.number.setText((position + 1) + ")");

        holder.itemView.setOnClickListener(new SafeClickListener() {
                                               @Override
                                               public void onSafeClick(View view) {}
            // Your click action here
        });
    }
    //        holder.textDoctor.setText(item.getName());
//        holder.textPlace.setText(item.getTerritory());
//        holder.qualification.setText(item.getQualification());
//        holder.category.setText(item.getCategory());
//        holder.speciality.setText(item.getSpeciality());
//        holder.className.setText(item.getClassName());
//        holder.number.setText((position + 1) + ")");
//        holder.itemView.setOnClickListener(v -> {
//
//            });
//    }
    private String checkEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<DoctorVisitItem> filteredNames) {
        this.doctorList = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDoctor, textPlace;
        TextView qualification, category, speciality, className,number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDoctor = itemView.findViewById(R.id.textDoctor);
            textPlace = itemView.findViewById(R.id.textPlace);
            qualification = itemView.findViewById(R.id.Qualification);
            category = itemView.findViewById(R.id.Category);
            speciality = itemView.findViewById(R.id.Speciality);
            className = itemView.findViewById(R.id.Class);
            number = itemView.findViewById(R.id.number);
        }
    }
    public void updateData(List<DoctorVisitItem> newList) {
        Log.d("AdapterUpdate", "updateData called. Size: " + newList.size());

        fullList.clear();
        fullList = new ArrayList<>(newList);

        notifyDataSetChanged();
    }



    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("FILTER", "Filtering with: " + constraint);

                List<DoctorVisitItem> filteredResults = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredResults.addAll(fullList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (DoctorVisitItem item : fullList) {
                        if (item.getName() != null && item.getName().toLowerCase().contains(filterPattern)) {
                            filteredResults.add(item);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;
                results.count = filteredResults.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                doctorList.clear();
                doctorList.addAll((List<DoctorVisitItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }

}

