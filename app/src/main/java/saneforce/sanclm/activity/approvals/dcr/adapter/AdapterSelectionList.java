package saneforce.sanclm.activity.approvals.dcr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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
    String DrCaption, ChemistCaption, CipCaption, StockistCaption, UndrCaption;

    public AdapterSelectionList(Context context, ArrayList<AdapterModel> adapterModels, ArrayList<DcrDetailModelList> dcrApprovalLists, AdapterCustMainList adapterCustMainList) {
        this.context = context;
        this.adapterModels = adapterModels;
        this.dcrApprovalLists = dcrApprovalLists;
        this.adapterCustMainList = adapterCustMainList;
    }

    public AdapterSelectionList(Context context, ArrayList<AdapterModel> adapterModels, ArrayList<DcrDetailModelList> dcrDetailedList, AdapterCustMainList adapterCustMainList, String drCaption, String chemistCaption, String stockistCaption, String undrCaption) {
        this.context = context;
        this.adapterModels = adapterModels;
        this.dcrApprovalLists = dcrDetailedList;
        this.adapterCustMainList = adapterCustMainList;
        this.DrCaption = drCaption;
        this.ChemistCaption = chemistCaption;
        this.StockistCaption = stockistCaption;
        this.UndrCaption = undrCaption;
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
        Log.v("tytyty", adapterModels.get(position).getName());
        holder.tv_name.setText(adapterModels.get(position).getName());
        holder.tv_count.setText(adapterModels.get(position).getCount());

        if (selectedPosition == holder.getBindingAdapterPosition()) {
            if (adapterModels.get(position).getName().equalsIgnoreCase("ALL")) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_light_grey_1_border));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_purple_round));
                filter("");
            } else if (adapterModels.get(position).getName().equalsIgnoreCase(DrCaption)) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_priority_border));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_green_round));
                filter("1");
            } else if (adapterModels.get(position).getName().equalsIgnoreCase(ChemistCaption)) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sale_border));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_lite_blue_round));
                filter("2");
            } else if (adapterModels.get(position).getName().equalsIgnoreCase(StockistCaption)) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sample_border));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_pink_round));
                filter("3");
            } else if (adapterModels.get(position).getName().equalsIgnoreCase(UndrCaption)) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sale_sample_border));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_grey_round));
                filter("4");
            } else if (adapterModels.get(position).getName().equalsIgnoreCase("Hospital")) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_hospital_border));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_grey_round));
                filter("7");
            }
        } else {
            if (adapterModels.get(position).getName().equalsIgnoreCase("ALL")) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_light_grey_1));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_purple_round));
            } else if (adapterModels.get(position).getName().equalsIgnoreCase(DrCaption)) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_priority));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_green_round));
            } else if (adapterModels.get(position).getName().equalsIgnoreCase(ChemistCaption)) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sale));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_lite_blue_round));
            } else if (adapterModels.get(position).getName().equalsIgnoreCase(StockistCaption)) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sample));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_pink_round));
            } else if (adapterModels.get(position).getName().equalsIgnoreCase(UndrCaption)) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_sale_sample));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_grey_round));
            } else if (adapterModels.get(position).getName().equalsIgnoreCase("Hospital")) {
                holder.tv_name.setBackground(context.getResources().getDrawable(R.drawable.bg_hospital));
                holder.tv_count.setBackground(context.getResources().getDrawable(R.drawable.bg_dark_grey_round));
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
