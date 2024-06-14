package saneforce.sanzen.activity.approvals;

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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.dcr.DcrApprovalActivity;
import saneforce.sanzen.activity.approvals.geotagging.GeoTaggingActivity;
import saneforce.sanzen.activity.approvals.leave.LeaveApprovalActivity;
import saneforce.sanzen.activity.approvals.tp.TpApprovalActivity;
import saneforce.sanzen.activity.approvals.tpdeviation.TpDeviationApprovalActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class AdapterApprovals extends RecyclerView.Adapter<AdapterApprovals.ViewHolder> {
    Context context;
    ArrayList<AdapterModel> approval_list;
    CommonUtilsMethods commonUtilsMethods;

    public AdapterApprovals(Context context, ArrayList<AdapterModel> approval_list) {
        this.context = context;
        this.approval_list = approval_list;
        commonUtilsMethods=new CommonUtilsMethods(context);
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
                if (Integer.parseInt(approval_list.get(position).getCount()) > 0) {
                    context.startActivity(new Intent(context, LeaveApprovalActivity.class));
                } else {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.leave_ap_not_available));
                }
            } else if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.tp_approvals))) {

                if (Integer.parseInt(approval_list.get(position).getCount()) > 0) {
                    context.startActivity(new Intent(context, TpApprovalActivity.class));
                } else {
                    commonUtilsMethods.showToastMessage(context,  context.getString(R.string.tp_ap_not_available));
                }

            } else if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.tp_deviation))) {

                if (Integer.parseInt(approval_list.get(position).getCount()) > 0) {
                    context.startActivity(new Intent(context, TpDeviationApprovalActivity.class));
                } else {
                    commonUtilsMethods.showToastMessage(context,  context.getString(R.string.tpdevication_ap_not_available));
                }
            } else if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.dcr_approvals))) {
                if (Integer.parseInt(approval_list.get(position).getCount()) > 0) {
                    context.startActivity(new Intent(context, DcrApprovalActivity.class));
                } else {
                    commonUtilsMethods.showToastMessage(context,  context.getString(R.string.dcr_ap_not_available));
                }

            } else if (approval_list.get(position).getName().equalsIgnoreCase(context.getResources().getString(R.string.geo_tagging))) {
                if (Integer.parseInt(approval_list.get(position).getCount()) > 0) {
                    context.startActivity(new Intent(context, GeoTaggingActivity.class));
                } else {
                    commonUtilsMethods.showToastMessage(context,  context.getString(R.string.geoTagging_ap_not_available));
                }

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
