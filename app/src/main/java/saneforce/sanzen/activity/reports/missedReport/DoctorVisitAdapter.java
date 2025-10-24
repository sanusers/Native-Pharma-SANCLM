package saneforce.sanzen.activity.reports.missedReport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private List<DoctorVisitItem> doctorList;
    private final String type;
    // private  List<DoctorVisitItem> fullList;


    public DoctorVisitAdapter(Context context, List<DoctorVisitItem> doctorList, String type) {
        this.context = context;
        this.doctorList = new ArrayList<>(doctorList);
        this.type = type;
        //this.fullList = new ArrayList<>(doctorList);
//        this.context = context;
//        this.doctorList = new ArrayList<>(doctorList);
//        this.doctorList.addAll(doctorList);
//        //this.doctorList = doctorList;
//        this.fullList = new ArrayList<>(doctorList);
//        this.fullList.addAll(doctorList);
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

        // Always show Name & Place
        holder.textDoctor.setText(checkEmpty(item.getName()));
        holder.textPlace.setText(checkEmpty(item.getTerritory()));
        holder.number.setText((position + 1) + ")");
        holder.textDoctor.setVisibility(View.VISIBLE);
        holder.textPlace.setVisibility(View.VISIBLE);

        // Hide all optional fields initially
        holder.layoutQualification.setVisibility(View.GONE);
        holder.layoutCategory.setVisibility(View.GONE);
        holder.layoutSpeciality.setVisibility(View.GONE);
        holder.layoutClass.setVisibility(View.GONE);

        // Hide divider initially
        holder.divider.setVisibility(View.GONE);

        boolean optionalVisible = false;

        switch (type) {
            case "1": // Doctor → show all optional fields
                holder.drImg.setImageResource(R.drawable.tp_dr_icon);

                holder.layoutQualification.setVisibility(View.VISIBLE);
                holder.layoutCategory.setVisibility(View.VISIBLE);
                holder.layoutSpeciality.setVisibility(View.VISIBLE);
                holder.layoutClass.setVisibility(View.VISIBLE);

                holder.qualification.setText(checkEmpty(item.getQualification()));
                holder.category.setText(checkEmpty(item.getCategory()));
                holder.speciality.setText(checkEmpty(item.getSpeciality()));
                holder.className.setText(checkEmpty(item.getClassName()));

                optionalVisible = true;
                break;

            case "2": // Chemist → show Category only
                holder.drImg.setImageResource(R.drawable.tp_chemist_icon);

                holder.layoutCategory.setVisibility(View.VISIBLE);
                holder.category.setText(checkEmpty(item.getCategory()));

                optionalVisible = true;
                break;

            case "3": // Stockist → Name & Place only
                holder.drImg.setImageResource(R.drawable.tp_stockiest_icon);
                break;

            case "4": // Unlisted → show Category + Specialty
                holder.drImg.setImageResource(R.drawable.tp_unlist_dr_icon);

                holder.layoutCategory.setVisibility(View.VISIBLE);
                holder.layoutSpeciality.setVisibility(View.VISIBLE);
                holder.layoutQualification.setVisibility(View.VISIBLE);
                holder.layoutClass.setVisibility(View.VISIBLE);

                holder.category.setText(checkEmpty(item.getCategory()));
                holder.speciality.setText(checkEmpty(item.getSpeciality()));
                holder.qualification.setText(checkEmpty(item.getQualification()));
                holder.className.setText(checkEmpty(item.getClassName()));

                optionalVisible = true;
                break;
        }

        // Show divider only if any optional field is visible
        holder.divider.setVisibility(optionalVisible ? View.VISIBLE : View.GONE);
    }

   // @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//// Always show Name and Place
//            DoctorVisitItem item = doctorList.get(position);
//
//            // Always show Name & Place
//            holder.textDoctor.setText(checkEmpty(item.getName()));
//            holder.textPlace.setText(checkEmpty(item.getTerritory()));
//            holder.number.setText((position + 1) + ")");
//            holder.textDoctor.setVisibility(View.VISIBLE);
//            holder.textPlace.setVisibility(View.VISIBLE);
//            // Hide all optional fields initially
//            holder.doctorqual.setVisibility(View.GONE);
//            holder.qualification.setVisibility(View.GONE);
//            holder.doctorcat.setVisibility(View.GONE);
//            holder.category.setVisibility(View.GONE);
//            holder.doctorspec.setVisibility(View.GONE);
//            holder.speciality.setVisibility(View.GONE);
//            holder.doctorclass.setVisibility(View.GONE);
//            holder.className.setVisibility(View.GONE);
//
//
//            // Hide divider initially
//            holder.divider.setVisibility(View.GONE);
//            switch (type) {
//                case "1": // Doctor → show all optional fields
//                    holder.drImg.setImageResource(R.drawable.tp_dr_icon);
//                    holder.doctorqual.setVisibility(View.VISIBLE);
//                    holder.qualification.setVisibility(View.VISIBLE);
//                    holder.doctorcat.setVisibility(View.VISIBLE);
//                    holder.category.setVisibility(View.VISIBLE);
//                    holder.doctorspec.setVisibility(View.VISIBLE);
//                    holder.speciality.setVisibility(View.VISIBLE);
//                    holder.doctorclass.setVisibility(View.VISIBLE);
//                    holder.className.setVisibility(View.VISIBLE);
//                    holder.qualification.setText(checkEmpty(item.getQualification()));
//                    holder.category.setText(checkEmpty(item.getCategory()));
//                    holder.speciality.setText(checkEmpty(item.getSpeciality()));
//                    holder.className.setText(checkEmpty(item.getClassName()));
//                    holder.divider.setVisibility(View.VISIBLE);
//                    break;
//
//                case "2": // Chemist → show Category only
//                    holder.drImg.setImageResource(R.drawable.tp_chemist_icon);
//                    holder.doctorcat.setVisibility(View.VISIBLE);
//                    holder.category.setVisibility(View.VISIBLE);
//                    holder.category.setText(checkEmpty(item.getCategory()));
//
//                    holder.divider.setVisibility(View.VISIBLE);
//                    break;
//
//                case "3": // Stockist → Name & Place only
//                    holder.drImg.setImageResource(R.drawable.tp_stockiest_icon);
//                    break;
//
//                case "4": // Unlisted → show Category + Specialty
//                    holder.drImg.setImageResource(R.drawable.tp_unlist_dr_icon);
//                    holder.doctorcat.setVisibility(View.VISIBLE);
//                    holder.category.setVisibility(View.VISIBLE);
//                    holder.category.setText(checkEmpty(item.getCategory()));
//                    holder.doctorspec.setVisibility(View.VISIBLE);
//                    holder.speciality.setVisibility(View.VISIBLE);
//                    holder.speciality.setText(checkEmpty(item.getSpeciality()));
//                    holder.divider.setVisibility(View.VISIBLE);
//                    break;
//            }
//
//        }


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
        TextView qualification, category, speciality, className, number;
        TextView doctorqual, doctorcat, doctorspec, doctorclass;
        LinearLayout doctorNameLayout, layoutQualification, layoutCategory, layoutSpeciality, layoutClass;
        View divider;
        ImageView drImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDoctor = itemView.findViewById(R.id.textDoctor);
            textPlace = itemView.findViewById(R.id.textPlace);
            qualification = itemView.findViewById(R.id.Qualification);
            category = itemView.findViewById(R.id.Category);
            speciality = itemView.findViewById(R.id.Speciality);
            className = itemView.findViewById(R.id.Class);
            number = itemView.findViewById(R.id.number);


            doctorqual = itemView.findViewById(R.id.doctorqual);
            doctorcat = itemView.findViewById(R.id.doctorcat);
            doctorspec = itemView.findViewById(R.id.doctorspec);
            doctorclass = itemView.findViewById(R.id.doctorclass);
            doctorNameLayout = itemView.findViewById(R.id.doctorNameLayout);
            divider = itemView.findViewById(R.id.dividerView);
            drImg = itemView.findViewById(R.id.dr_img);

            layoutQualification = itemView.findViewById(R.id.layoutQualification);
            layoutCategory = itemView.findViewById(R.id.layoutCategory);
            layoutSpeciality = itemView.findViewById(R.id.layoutSpeciality);
            layoutClass = itemView.findViewById(R.id.layoutClass);

        }
    }

    public void updateData(List<DoctorVisitItem> newList) {
        doctorList.clear();
        doctorList.addAll(newList);
        Log.d("AdapterUpdate", "updateData called. Size: " + newList.size());
        notifyDataSetChanged();
    }

//        fullList.clear();
//        fullList.addAll(newList);
//        notifyDataSetChanged();
//        doctorList.clear();
//        doctorList.addAll(newList);
//       fullList.clear();
//        fullList = new ArrayList<>(newList);


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("FILTER", "Filtering with: " + constraint);

                List<DoctorVisitItem> filteredResults = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredResults.addAll(doctorList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (DoctorVisitItem item : doctorList) {
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

