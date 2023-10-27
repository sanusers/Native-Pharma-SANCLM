package saneforce.sanclm.activity.approvals.tp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.tourPlan.TourPlanActivity;

public class TpApprovalAdapter extends RecyclerView.Adapter<TpApprovalAdapter.ViewHolder> {
    Context context;
    ArrayList<TpModelList> tpModelLists;

    public TpApprovalAdapter(Context context, ArrayList<TpModelList> tpModelLists) {
        this.context = context;
        this.tpModelLists = tpModelLists;
    }

    @NonNull
    @Override
    public TpApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tp_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TpApprovalAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(tpModelLists.get(position).getName());
        holder.tv_month.setText(tpModelLists.get(position).getMonth());
        holder.tv_year.setText(tpModelLists.get(position).getYear());

        holder.tv_view.setOnClickListener(view -> context.startActivity(new Intent(context, TourPlanActivity.class)));
    }

    @Override
    public int getItemCount() {
        return tpModelLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_month, tv_year, tv_view;

        public ViewHolder(@NonNull View item) {
            super(item);
            tv_name = item.findViewById(R.id.txt_name);
            tv_month = item.findViewById(R.id.txt_month);
            tv_year = item.findViewById(R.id.txt_year);
            tv_view = item.findViewById(R.id.txt_view);
        }
    }
}
