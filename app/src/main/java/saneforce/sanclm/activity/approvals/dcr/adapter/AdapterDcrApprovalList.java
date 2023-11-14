package saneforce.sanclm.activity.approvals.dcr.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.dcr.DcrCallApprovalActivity;
import saneforce.sanclm.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterDcrApprovalList extends RecyclerView.Adapter<AdapterDcrApprovalList.ViewHolder> {
    Context context;
    ArrayList<DCRApprovalList> dcrApprovalLists;
    ArrayList<DCRApprovalList> dcrApprovalListsDummy;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    int selectedPosition = -1;
    int lastSelectedPosition = -1;

    public AdapterDcrApprovalList(Activity activity, Context context, ArrayList<DCRApprovalList> dcrApprovalLists, ArrayList<DCRApprovalList> dcrApprovalListsDummy) {
        this.activity = activity;
        this.context = context;
        this.dcrApprovalLists = dcrApprovalLists;
        this.dcrApprovalListsDummy = dcrApprovalListsDummy;
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
        holder.tv_date.setText(dcrApprovalLists.get(position).getActivity_date());

        if (dcrApprovalLists.get(position).getSfCode().equalsIgnoreCase(DcrCallApprovalActivity.SelectedSfCode) && dcrApprovalLists.get(position).getActivity_date().equalsIgnoreCase(DcrCallApprovalActivity.SelectedActivityDate)) {
            holder.constraint_main.setBackground(context.getResources().getDrawable(R.drawable.bg_purple));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_date.setBackground(context.getResources().getDrawable(R.drawable.selector_box));
            holder.list_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_white));
        } else {
            holder.constraint_main.setBackground(context.getResources().getDrawable(R.drawable.selector_box));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.dark_purple));
            holder.tv_date.setBackground(context.getResources().getDrawable(R.drawable.bg_light_grey_1));
            holder.list_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_purple));
        }


        //  holder.tv_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, dcrApprovalLists.get(position).getSf_name()));

        holder.constraint_main.setOnClickListener(view -> {
            DcrCallApprovalActivity.dcrCallApprovalBinding.tvName.setText(dcrApprovalLists.get(position).getSf_name());
            DcrCallApprovalActivity.dcrCallApprovalBinding.tvWorktype1.setText(dcrApprovalLists.get(position).getWorkType_name());
            DcrCallApprovalActivity.dcrCallApprovalBinding.tvActivityDate.setText(dcrApprovalLists.get(position).getActivity_date());
            DcrCallApprovalActivity.dcrCallApprovalBinding.tvSubmittedDate.setText(dcrApprovalLists.get(position).getActivity_date());
            DcrCallApprovalActivity.SelectedTransCode = dcrApprovalLists.get(position).getTrans_slNo();
            DcrCallApprovalActivity.SelectedSfCode = dcrApprovalLists.get(position).getSfCode();
            DcrCallApprovalActivity.SelectedActivityDate = dcrApprovalLists.get(position).getActivity_date();
            DcrCallApprovalActivity.SelectedPosition = holder.getAdapterPosition();
            DcrCallApprovalActivity.getDcrContentList(context);
            DcrCallApprovalActivity.dcrCallApprovalBinding.constraintDcrListContent.setVisibility(View.VISIBLE);
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
    public void filterList(ArrayList<DCRApprovalList> filterdNames) {
        this.dcrApprovalLists = filterdNames;
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
