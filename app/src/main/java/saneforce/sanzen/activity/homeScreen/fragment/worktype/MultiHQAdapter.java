package saneforce.sanzen.activity.homeScreen.fragment.worktype;

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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.modelClass.Multicheckclass_clust;

public class MultiHQAdapter extends RecyclerView.Adapter<MultiHQAdapter.ViewHolder> {

    private final Context context;
    private List<Multicheckclass_clust> itemList;
    private List<Multicheckclass_clust> filteredList;
    private final LayoutInflater inflater;
    private final MultiHQSelectListener multiHQSelectListener;

    public interface MultiHQSelectListener {
        void onHQSelected(Multicheckclass_clust multicheckclassClust);
        void onHQUnSelected(Multicheckclass_clust multicheckclassClust);
    }

    public MultiHQAdapter(Context context, List<Multicheckclass_clust> itemList, MultiHQSelectListener multiHQSelectListener) {
        this.context = context;
        this.filteredList = itemList;
        this.itemList = itemList;
        this.multiHQSelectListener = multiHQSelectListener;
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
        Multicheckclass_clust multicheckclassClust = filteredList.get(position);
        holder.itemTitle.setText(multicheckclassClust.getStrname());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(multicheckclassClust.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            multicheckclassClust.setChecked(isChecked);
            if(isChecked) {
                multiHQSelectListener.onHQSelected(multicheckclassClust);
            }else {
                multiHQSelectListener.onHQUnSelected(multicheckclassClust);
            }
        });

        if(multicheckclassClust.isChecked()) {
            multiHQSelectListener.onHQSelected(multicheckclassClust);
        }

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            checkBox = itemView.findViewById(R.id.ch_mutiple);
            checkBox.setVisibility(View.VISIBLE);
        }

    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                List<Multicheckclass_clust> filtered = new ArrayList<>();
                for (Multicheckclass_clust data : itemList) {
                    if(data.getStrname().toLowerCase().contains(searchString)) {
                        filtered.add(data);
                    }
                }
                filteredList = filtered;
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    filteredList = (List<Multicheckclass_clust>) results.values;
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
