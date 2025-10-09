package saneforce.sanzen.activity.survey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.survey.model.SurveyOptionsModelClass;

public class SurveySideAdapter extends RecyclerView.Adapter<SurveySideAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<SurveyOptionsModelClass> surveyOptionsList;
    private ArrayList<SurveyOptionsModelClass> filteredOptionsList;
    private final boolean isMultiple;
    private final CheckBoxClickListener checkBoxClickListener;

    public interface CheckBoxClickListener {
        void onChecked(SurveyOptionsModelClass surveyOptionsModelClass);
        void onUnchecked(SurveyOptionsModelClass surveyOptionsModelClass);
    }

    public SurveySideAdapter(Context context, ArrayList<SurveyOptionsModelClass> surveyOptionsList, boolean isMultiple, CheckBoxClickListener checkBoxClickListener) {
        this.context = context;
        this.surveyOptionsList = surveyOptionsList;
        this.filteredOptionsList = surveyOptionsList;
        this.isMultiple = isMultiple;
        this.checkBoxClickListener = checkBoxClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvOption.setText(filteredOptionsList.get(position).getName());
        if(isMultiple) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(filteredOptionsList.get(position).isChecked());
            if(filteredOptionsList.get(position).isChecked()) {
                checkBoxClickListener.onChecked(filteredOptionsList.get(position));
            }
        }else {
            holder.checkBox.setVisibility(View.GONE);
            holder.tvOption.setOnClickListener(new SafeClickListener() {
                                                   @Override
                                                   public void onSafeClick(View view) {
                                                       checkBoxClickListener.onChecked(filteredOptionsList.get(position));
                                                   }
            });
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                checkBoxClickListener.onChecked(filteredOptionsList.get(position));
            }else {
                checkBoxClickListener.onUnchecked(filteredOptionsList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredOptionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOption;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOption = itemView.findViewById(R.id.itemTitle);
            checkBox = itemView.findViewById(R.id.ch_mutiple);
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                ArrayList<SurveyOptionsModelClass> filtered = new ArrayList<>();
                for (SurveyOptionsModelClass data : surveyOptionsList) {
                    if(data.getName().toLowerCase().contains(searchString)) {
                        filtered.add(data);
                    }
                }
                filteredOptionsList = filtered;
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredOptionsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredOptionsList = (ArrayList<SurveyOptionsModelClass>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
