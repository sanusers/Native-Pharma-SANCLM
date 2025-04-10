package saneforce.sanzen.activity.approvals.stp.adapter;

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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanzen.activity.approvals.stp.model.STPModelList;

public class STPApprovalAdapter extends RecyclerView.Adapter<STPApprovalAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<STPModelList> stpModelLists;
    private final OnItemClickListenerApproval mListener;
    private String selectedSFCode = "";

    public STPApprovalAdapter(Context context, ArrayList<STPModelList> stpModelLists, OnItemClickListenerApproval mListener) {
        this.context = context;
        this.stpModelLists = stpModelLists;
        this.mListener = mListener;
    }

    public void setSelectedSFCode(String selectedSFCode, int position) {
        this.selectedSFCode = selectedSFCode;
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public STPApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_appr_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(stpModelLists.get(position).getName());
        holder.tv_date.setVisibility(View.GONE);

        if(stpModelLists.get(position).getCode().equalsIgnoreCase(selectedSFCode)) {
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_box));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.greater_than_white));
        }else {
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_box));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.dark_purple));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_light_grey_1));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.greater_than_purple));
        }

        holder.constraint_main.setOnClickListener(view -> {
            setSelectedSFCode(stpModelLists.get(position).getCode(), position);
            mListener.onSTPItemClick(new STPModelList(stpModelLists.get(position).getName(), stpModelLists.get(position).getCode(), stpModelLists.get(position).getDivCode()), holder.getBindingAdapterPosition());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return stpModelLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<STPModelList> filteredNames) {
        this.stpModelLists = filteredNames;
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        stpModelLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, stpModelLists.size());
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
