package saneforce.santrip.activity.approvals;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.dcr.DcrApprovalActivity;
import saneforce.santrip.activity.approvals.geotagging.GeoTaggingActivity;
import saneforce.santrip.activity.approvals.leave.LeaveApprovalActivity;
import saneforce.santrip.activity.approvals.tp.TpApprovalActivity;
import saneforce.santrip.activity.approvals.tpdeviation.TpDeviationApprovalActivity;

public class AdapterApprovals extends RecyclerView.Adapter<AdapterApprovals.ViewHolder> {
    Context context;
    ArrayList<AdapterModel> approval_list;

    public AdapterApprovals(Context context, ArrayList<AdapterModel> approval_list) {
        this.context = context;
        this.approval_list = approval_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_approvals_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(approval_list.get(position).getName());
        holder.tv_count.setText(approval_list.get(position).getCount());
        holder.constraintMain.setOnClickListener(view -> {
            if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.leave_approvals))) {
                context.startActivity(new Intent(context, LeaveApprovalActivity.class));
            } else if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.tp_approvals))) {
                context.startActivity(new Intent(context, TpApprovalActivity.class));
            } else if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.tp_deviation))) {
                context.startActivity(new Intent(context, TpDeviationApprovalActivity.class));
            } else if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.dcr_approvals))) {
                context.startActivity(new Intent(context, DcrApprovalActivity.class));
            } else if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.geo_tagging))) {
                context.startActivity(new Intent(context, GeoTaggingActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return approval_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_count;
        ConstraintLayout constraintMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintMain = itemView.findViewById(R.id.constraint_main);
            tv_name = itemView.findViewById(R.id.tv_listed_approvals);
            tv_count = itemView.findViewById(R.id.tv_approval_count);
        }
    }
}
