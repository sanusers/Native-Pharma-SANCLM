package saneforce.santrip.activity.approvals.dcr.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.OnItemClickListenerApproval;
import saneforce.santrip.activity.approvals.dcr.DcrApprovalActivity;
import saneforce.santrip.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class AdapterDcrApprovalList extends RecyclerView.Adapter<AdapterDcrApprovalList.ViewHolder> {
    Context context;
    ArrayList<DCRApprovalList> dcrApprovalLists;
    CommonUtilsMethods commonUtilsMethods;
    OnItemClickListenerApproval onItemClickListenerApproval;
    Activity activity;

    public AdapterDcrApprovalList(Activity activity, Context context, ArrayList<DCRApprovalList> dcrApprovalLists, OnItemClickListenerApproval onItemClickListenerApproval) {
        this.activity = activity;
        this.context = context;
        this.dcrApprovalLists = dcrApprovalLists;
        this.onItemClickListenerApproval = onItemClickListenerApproval;
    }

    @NonNull
    @Override
    public AdapterDcrApprovalList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_appr_list, parent, false);
        return new AdapterDcrApprovalList.ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull AdapterDcrApprovalList.ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(dcrApprovalLists.get(position).getSf_name());
        holder.tv_date.setText(CommonUtilsMethods.setConvertDate("dd/MM/yyyy", "dd MMM yyyy", dcrApprovalLists.get(position).getActivity_date()));
       //  holder.tv_date.setText(dcrApprovalLists.get(position).getActivity_date());

        if (dcrApprovalLists.get(position).getSfCode().equalsIgnoreCase(DcrApprovalActivity.SelectedSfCode) && dcrApprovalLists.get(position).getActivity_date().equalsIgnoreCase(DcrApprovalActivity.SelectedActivityDate)) {
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_box));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.greater_than_white));
        } else {
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_box));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.dark_purple));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_light_grey_1));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.greater_than_purple));
        }


        holder.constraint_main.setOnClickListener(view -> {
            onItemClickListenerApproval.onClick(new DCRApprovalList(dcrApprovalLists.get(position).getTrans_slNo(), dcrApprovalLists.get(position).getSf_name(), dcrApprovalLists.get(position).getActivity_date(), dcrApprovalLists.get(position).getPlan_name(), dcrApprovalLists.get(position).getWorkType_name(), dcrApprovalLists.get(position).getSfCode(), dcrApprovalLists.get(position).getFieldWork_indicator(), dcrApprovalLists.get(position).getSubmission_date_sub(), dcrApprovalLists.get(position).getOther_wt()), holder.getBindingAdapterPosition());
            notifyDataSetChanged();
        });
    }

    public void removeAt(int position) {
        dcrApprovalLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dcrApprovalLists.size());
    }

    @Override
    public int getItemCount() {
        return dcrApprovalLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<DCRApprovalList> filteredNames) {
        this.dcrApprovalLists = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_date;
        ConstraintLayout constraint_main;
        ImageView list_arrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            constraint_main = itemView.findViewById(R.id.constraint_main);
            list_arrow = itemView.findViewById(R.id.listArrow);
        }
    }
}
