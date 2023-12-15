package saneforce.sanclm.activity.approvals.tp;

import android.annotation.SuppressLint;
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

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.OnItemClickListenerApproval;

public class TpApprovalAdapter extends RecyclerView.Adapter<TpApprovalAdapter.ViewHolder> {
    Context context;
    ArrayList<TpModelList> tpModelLists;
    OnItemClickListenerApproval mListener;

    public TpApprovalAdapter(Context context, ArrayList<TpModelList> tpModelLists, OnItemClickListenerApproval mListener) {
        this.context = context;
        this.tpModelLists = tpModelLists;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public TpApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_appr_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull TpApprovalAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(tpModelLists.get(position).getName());
        holder.tv_date.setText(String.format("%s %s", tpModelLists.get(position).getMonth(), tpModelLists.get(position).getYear()));

        if (tpModelLists.get(position).getCode().equalsIgnoreCase(TpApprovalActivity.SelectedSfCode) && String.format("%s %s", tpModelLists.get(position).getMonth(), tpModelLists.get(position).getYear()).equalsIgnoreCase(TpApprovalActivity.SelectedMonthYear)) {
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_purple));
            holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.white));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_box));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.greater_than_white));
        } else {
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_box));
            holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.dark_purple));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_light_grey_1));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.greater_than_purple));
        }

        holder.constraint_main.setOnClickListener(view -> {
            mListener.onItemClick(new TpModelList(tpModelLists.get(position).getCode(),tpModelLists.get(position).getName(),tpModelLists.get(position).getMonth(),tpModelLists.get(position).getYear(),tpModelLists.get(position).getMn()));
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return tpModelLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<TpModelList> filteredNames) {
        this.tpModelLists = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_date;
        ConstraintLayout constraint_main;
        ImageView list_arrow;

        public ViewHolder(@NonNull View item) {
            super(item);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            constraint_main = itemView.findViewById(R.id.constraint_main);
            list_arrow = itemView.findViewById(R.id.listArrow);
        }
    }
}
