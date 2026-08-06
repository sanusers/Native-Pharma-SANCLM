package saneforce.sanzen.activity.activityModule.adapter;

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
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.activityModule.model.ActivityModelClass;
import saneforce.sanzen.activity.activityModule.CheckBoxInterface;

public class ActvityList2Adapter extends RecyclerView.Adapter<ActvityList2Adapter.Viewholder> {
    private Context context;
    private List<ActivityModelClass> List;
    private List<ActivityModelClass> filteredList;
    boolean isMultiple;
    TextView selcteids;
    CheckBoxInterface checkBoxInterface;
    public ActvityList2Adapter(Context context, List<ActivityModelClass> List,TextView selcteids, boolean isMultiple, CheckBoxInterface checkBoxInterface) {
        this.context = context;
        this.List = List;
        this.filteredList = List;
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
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(filteredList.get(position).getIscheck());
//            if(filteredList.get(position).getIscheck()){
//                checkBoxInterface.Checked(filteredList.get(position));
//            }
        }else {
            holder.checkBox.setVisibility(View. GONE);
        }

        if(!isMultiple){
            holder.txtHq.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    int adapterPosition = holder.getBindingAdapterPosition();
                    if (adapterPosition == RecyclerView.NO_POSITION) return;

                    checkBoxInterface.Checked(filteredList.get(adapterPosition));
                }
            });
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            ActivityModelClass item = filteredList.get(adapterPosition);
            item.setIscheck(isChecked);

            if (isChecked) {
                checkBoxInterface.Checked(item);
            } else {
                checkBoxInterface.UnChecked(item);
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
                    if (data.getName().toLowerCase().contains(searchString)) {
                            filtered.add(data);
                    }
                }
                filteredList = new ArrayList<>(filtered);
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
