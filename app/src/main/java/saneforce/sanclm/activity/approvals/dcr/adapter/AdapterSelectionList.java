package saneforce.sanclm.activity.approvals.dcr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.AdapterModel;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;

public class AdapterSelectionList extends RecyclerView.Adapter<AdapterSelectionList.ViewHolder> {
    Context context;
    ArrayList<AdapterModel> adapterModels;
    int selectedPosition = -1;
    int lastSelectedPosition = -1;
    AdapterCustMainList adapterCustMainList;
    ArrayList<DcrDetailModelList> dcrApprovalLists;

    public AdapterSelectionList(Context context, ArrayList<AdapterModel> adapterModels, ArrayList<DcrDetailModelList> dcrApprovalLists, AdapterCustMainList adapterCustMainList) {
        this.context = context;
        this.adapterModels = adapterModels;
        this.dcrApprovalLists = dcrApprovalLists;
        this.adapterCustMainList = adapterCustMainList;
    }

    @NonNull
    @Override
    public AdapterSelectionList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_selection_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AdapterSelectionList.ViewHolder holder, int position) {
        holder.tv_name.setText(adapterModels.get(position).getName());
        holder.tv_count.setText(adapterModels.get(position).getCount());

        if (selectedPosition == holder.getBindingAdapterPosition()) {
            switch (adapterModels.get(position).getName()) {
                case "All":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_light_grey_1_border));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_purple_round));
                    filter("");
                    break;
                case "Doctor":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_priority_border));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_green_round));
                    filter("1");
                    break;
                case "Chemist":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sale_border));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_lite_blue_round));
                    filter("2");
                    break;
                case "Stockiest":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sample_border));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_pink_round));
                    filter("3");
                    break;
                case "Unlisted Dr":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sale_sample_border));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_grey_round));
                    filter("4");
                    break;
                case "Hospital":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_hospital_border));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_grey_round));
                    filter("7");
                    break;
            }
        } else {
            switch (adapterModels.get(position).getName()) {
                case "All":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_light_grey_1));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_purple_round));
                    break;
                case "Doctor":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_priority));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_green_round));
                    break;
                case "Chemist":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sale));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_lite_blue_round));
                    break;
                case "Stockiest":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sample));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_pink_round));
                    break;
                case "Unlisted Dr":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sale_sample));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_grey_round));
                    break;
                case "Hospital":
                    holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_hospital));
                    holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_grey_round));
                    break;
            }
        }


        holder.tv_name.setOnClickListener(view -> {
            lastSelectedPosition = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    private void filter(String text) {
        ArrayList<DcrDetailModelList> filterdNames = new ArrayList<>();
        for (DcrDetailModelList s : dcrApprovalLists) {
            if (s.getType().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        adapterCustMainList.filterList(filterdNames);
    }

    @Override
    public int getItemCount() {
        return adapterModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_text);
            tv_count = itemView.findViewById(R.id.tv_count);
        }
    }
}
