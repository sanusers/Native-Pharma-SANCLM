package saneforce.santrip.activity.activityModule;

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

import saneforce.santrip.R;

public class ActvityList2Adapter extends RecyclerView.Adapter<ActvityList2Adapter.Viewholder> {

    private Context context;
    private List<String> ListName;
    private List<String> ListCode;
    private List<String> filteredList;
    private LayoutInflater inflater;
    boolean isMultiple;
    CheckBoxInterface checkBoxInterface;
    public ActvityList2Adapter(Context context, List<String> ListName,List<String> ListCode, boolean isMultiple, CheckBoxInterface checkBoxInterface) {
        this.context = context;
        this.ListCode = ListCode;
        this.ListName = ListName;
        this.filteredList = ListName;
        this.inflater = LayoutInflater.from(context);
        this.isMultiple=isMultiple;
        this.checkBoxInterface=checkBoxInterface;
    }

    @NonNull
    @Override
    public ActvityList2Adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_text, parent, false);
        return new ActvityList2Adapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActvityList2Adapter.Viewholder holder, int position) {
        holder.txtHq.setText(filteredList.get(position));
        if(isMultiple){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else {
            holder.checkBox.setVisibility(View. GONE);

        }

        int mposition = ListName.indexOf(filteredList.get(position));

        if(!isMultiple){
            holder.txtHq.setOnClickListener(view -> {
                checkBoxInterface.Checked(filteredList.get(position),ListCode.get(mposition));
            });
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                  checkBoxInterface.Checked(filteredList.get(position),ListCode.get(mposition));
            } else {
                checkBoxInterface.UnChecked(filteredList.get(position),ListCode.get(mposition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView txtHq;
        CheckBox checkBox;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            txtHq=itemView.findViewById(R.id.itemTitle);
            checkBox=itemView.findViewById(R.id.ch_mutiple);
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase();
                List<String> filtered = new ArrayList<>();
                for (String data : ListName) {
                    if (data.toLowerCase().contains(searchString)) {
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
                filteredList = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
