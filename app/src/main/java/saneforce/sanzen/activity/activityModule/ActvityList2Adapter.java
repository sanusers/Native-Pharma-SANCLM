package saneforce.sanzen.activity.activityModule;

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
import java.util.Arrays;
import java.util.List;

import saneforce.sanzen.R;

public class ActvityList2Adapter extends RecyclerView.Adapter<ActvityList2Adapter.Viewholder> {

    private Context context;
    private List<ActivityModelClass> List;
    private List<String> ListCode;
    private List<ActivityModelClass> filteredList;
    private LayoutInflater inflater;
    boolean isMultiple;
    TextView selcteids;
    CheckBoxInterface checkBoxInterface;
    public ActvityList2Adapter(Context context, List<ActivityModelClass> List,TextView selcteids, boolean isMultiple, CheckBoxInterface checkBoxInterface) {
        this.context = context;
        this.List = List;
        this.filteredList = List;
        this.inflater = LayoutInflater.from(context);
        this.isMultiple=isMultiple;
        this.checkBoxInterface=checkBoxInterface;
        this.selcteids=selcteids;
    }

    @NonNull
    @Override
    public ActvityList2Adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_text, parent, false);
        return new ActvityList2Adapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActvityList2Adapter.Viewholder holder, int position) {
        holder.txtHq.setText(filteredList.get(position).getName());
        if(isMultiple){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(filteredList.get(position).getIscheck());
            if(filteredList.get(position).getIscheck()){
                checkBoxInterface.Checked(filteredList.get(position));
            }
        }else {
            holder.checkBox.setVisibility(View. GONE);

        }



        if(!isMultiple){
            holder.txtHq.setOnClickListener(view -> {
                checkBoxInterface.Checked(filteredList.get(position));
            });
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                  checkBoxInterface.Checked(filteredList.get(position));
            } else {
                checkBoxInterface.UnChecked(filteredList.get(position));
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
                List<ActivityModelClass> filtered = new ArrayList<>();
                for (ActivityModelClass data : List) {
                    if (data.getName().contains(searchString)) {
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
                filteredList = (List<ActivityModelClass>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
