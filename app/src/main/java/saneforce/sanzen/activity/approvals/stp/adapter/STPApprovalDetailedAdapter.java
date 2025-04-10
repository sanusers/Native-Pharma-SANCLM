package saneforce.sanzen.activity.approvals.stp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.stp.model.STPDetailedModel;

public class STPApprovalDetailedAdapter extends RecyclerView.Adapter<STPApprovalDetailedAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<STPDetailedModel> stpDetailedModels;
    private String selectedDay = "";

    public STPApprovalDetailedAdapter(Context context, ArrayList<STPDetailedModel> stpDetailedModels) {
        this.context = context;
        this.stpDetailedModels = stpDetailedModels;
    }

    @NonNull
    @Override
    public STPApprovalDetailedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tp_detailed_list, parent, false);
        return new STPApprovalDetailedAdapter.ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull STPApprovalDetailedAdapter.ViewHolder holder, int position) {
        holder.tvDayNo.setText(stpDetailedModels.get(position).getDayPlanShortName());
        holder.tvWtName.setText(stpDetailedModels.get(position).getDayPlanName());

        if(selectedDay.equalsIgnoreCase(stpDetailedModels.get(position).getDayPlanShortName())) {
            holder.rvViewDetailed.setVisibility(View.VISIBLE);
            holder.listArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.up_arrow));
            ArrayList<STPDetailedModel> stpClickedList = new ArrayList<>();

            stpClickedList.add(new STPDetailedModel(stpDetailedModels.get(position).getTransNo(), stpDetailedModels.get(position).getSfCode(), stpDetailedModels.get(position).getDivisionCode(), stpDetailedModels.get(position).getDayPlanName(), stpDetailedModels.get(position).getDayPlanShortName(), stpDetailedModels.get(position).getDayPlanCode(), stpDetailedModels.get(position).getClusterCode(), stpDetailedModels.get(position).getClusterName(), stpDetailedModels.get(position).getDoctorCode(), stpDetailedModels.get(position).getDoctorName(), stpDetailedModels.get(position).getChemistCode(), stpDetailedModels.get(position).getChemistName(), stpDetailedModels.get(position).getActiveFlag(), stpDetailedModels.get(position).getCreatedDate()));
            STPClickViewDetailsAdapter stpClickViewDetailsAdapter = new STPClickViewDetailsAdapter(context, stpClickedList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            holder.rvViewDetailed.setLayoutManager(mLayoutManager);
            holder.rvViewDetailed.setAdapter(stpClickViewDetailsAdapter);
        }else {
            holder.rvViewDetailed.setVisibility(View.GONE);
            holder.listArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drop_down));
        }

        holder.cardView.setOnClickListener(v -> {
            if(holder.cardView.isPressed()) {
                if(holder.rvViewDetailed.getVisibility() == View.VISIBLE) {
                    selectedDay = "";
                }else {
                    selectedDay = stpDetailedModels.get(position).getDayPlanShortName();
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stpDetailedModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNo, tvWtName;
        CardView cardView;
        ImageView listArrow;
        RecyclerView rvViewDetailed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNo = itemView.findViewById(R.id.tv_dayNo);
            tvWtName = itemView.findViewById(R.id.tv_workTypes);
            listArrow = itemView.findViewById(R.id.listArrow);
            rvViewDetailed = itemView.findViewById(R.id.rv_tp_detailed_list);
            cardView = itemView.findViewById(R.id.card_view_top);
        }
    }
}
