package saneforce.sanclm.Activities.Call.Adapter.RCPA;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.Activities.Call.Pojo.RCPA.RCPAAddedCompList;

public class RCPACompListAdapter extends RecyclerView.Adapter<RCPACompListAdapter.ViewHolder> {
    Context context;
    ArrayList<RCPAAddedCompList> rcpaAddedCompListArrayList;

    public RCPACompListAdapter(Context context, ArrayList<RCPAAddedCompList> rcpaAddedCompListArrayList) {
        this.context = context;
        this.rcpaAddedCompListArrayList = rcpaAddedCompListArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_comp_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (rcpaAddedCompListArrayList.get(position).getComp_brand().isEmpty() ||
                rcpaAddedCompListArrayList.get(position).getComp_brand().equalsIgnoreCase("Select")) {
            holder.constraint_main.setVisibility(View.GONE);
        }

        holder.tv_company.setText(rcpaAddedCompListArrayList.get(position).getComp_name());
        holder.tv_brand.setText(rcpaAddedCompListArrayList.get(position).getComp_brand());
        holder.tv_qty.setText(rcpaAddedCompListArrayList.get(position).getQty());
        holder.tv_rate.setText(rcpaAddedCompListArrayList.get(position).getRate());
        holder.tv_value.setText(rcpaAddedCompListArrayList.get(position).getValue());
        holder.tv_remarks.setText(rcpaAddedCompListArrayList.get(position).getRemarks());
    }

    @Override
    public int getItemCount() {
        return rcpaAddedCompListArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_company, tv_brand, tv_qty, tv_rate, tv_value, tv_remarks;
        ConstraintLayout constraint_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_company = itemView.findViewById(R.id.tv_comp_cmpy);
            tv_brand = itemView.findViewById(R.id.tv_comp_brand);
            tv_qty = itemView.findViewById(R.id.tv_qty_comp);
            tv_rate = itemView.findViewById(R.id.tv_rate_comp);
            tv_value = itemView.findViewById(R.id.tv_value_comp);
            tv_remarks = itemView.findViewById(R.id.tv_remarks);
            constraint_main = itemView.findViewById(R.id.constraint_list_comp);
        }
    }
}
