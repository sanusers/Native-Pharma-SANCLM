package saneforce.sanclm.activity.homeScreen.fragment.worktype;


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
import java.util.List;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.modelClass.Multicheckclass_clust;

public class MultiClusterAdapter extends RecyclerView.Adapter<MultiClusterAdapter.ViewHolder> {

    private final Context context;
    private final List<Multicheckclass_clust> itemList;
    private List<Multicheckclass_clust> filteredList;
    private final LayoutInflater inflater;
    OnClusterClicklistener onCampclicklistener;

    public MultiClusterAdapter(Context context, List<Multicheckclass_clust> itemList, OnClusterClicklistener onCampclicklistener) {
        this.context = context;
        this.filteredList = itemList;
        this.itemList = itemList;
        this.onCampclicklistener = onCampclicklistener;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_view_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Multicheckclass_clust mul = filteredList.get(position);
        holder.itemTitle.setText(mul.getStrname());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(mul.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mul.setChecked(isChecked);
            if (isChecked) {
                onCampclicklistener.classCampaignItem_addClass(mul);
            } else {
                onCampclicklistener.classCampaignItem_removeClass(mul);
            }
        });

        if(mul.isChecked()){
            onCampclicklistener.classCampaignItem_addClass(mul);
        }

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            checkBox = itemView.findViewById(R.id.ch_cluster);
            checkBox.setVisibility(View.VISIBLE);
        }


    }

    public void setFilteredList(List<Multicheckclass_clust> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }

    public List<Multicheckclass_clust> getFilteredList() {
        return filteredList;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                List<Multicheckclass_clust> filtered = new ArrayList<>();
                for (Multicheckclass_clust data : itemList) {
                    if (data.getStrname().toLowerCase().contains(searchString)) {
                            filtered.add(data);}

                }
                filteredList = filtered;
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Multicheckclass_clust>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

