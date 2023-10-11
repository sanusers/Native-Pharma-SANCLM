package saneforce.sanclm.activity.approvals.leave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;

public class LeaveApprovalAdapter extends RecyclerView.Adapter<LeaveApprovalAdapter.ViewHolder> {
    Context context;
    ArrayList<LeaveModelList> leaveModelLists;

    public LeaveApprovalAdapter(Context context, ArrayList<LeaveModelList> leaveModelLists) {
        this.context = context;
        this.leaveModelLists = leaveModelLists;
    }

    @NonNull
    @Override
    public LeaveApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_leave_approval, parent, false);
        return new LeaveApprovalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveApprovalAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(leaveModelLists.get(position).getName());
        holder.tv_reason.setText(leaveModelLists.get(position).getReason());
        holder.tv_address.setText(leaveModelLists.get(position).getAddr());
        holder.tv_date.setText(String.format("%s - %s", leaveModelLists.get(position).getFrom_date(), leaveModelLists.get(position).getTo_date()));
        holder.tv_leave_type.setText(String.format("Leave Type : %s", leaveModelLists.get(position).getLeave_type()));
        holder.tv_no_of_days.setText(String.format("No of Days : %s", leaveModelLists.get(position).getNo_of_days()));
        holder.tv_availability.setText(String.format("No of Days Available : %s", leaveModelLists.get(position).getAvailable_leave()));
    }

    @Override
    public int getItemCount() {
        return leaveModelLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_reason, tv_date, tv_no_of_days, tv_leave_type, tv_availability, tv_address;
        Button btn_accept, btn_reject;

        public ViewHolder(@NonNull View item) {
            super(item);
            tv_name = item.findViewById(R.id.txt_name);
            tv_reason = item.findViewById(R.id.tv_reason_for_leave);
            tv_date = item.findViewById(R.id.txt_date);
            tv_no_of_days = item.findViewById(R.id.txt_no_of_days);
            tv_leave_type = item.findViewById(R.id.tv_leave_type);
            tv_availability = item.findViewById(R.id.tv_available_days);
            tv_address = item.findViewById(R.id.tv_address);
            btn_accept = item.findViewById(R.id.btn_accept);
            btn_reject = item.findViewById(R.id.btn_reject);


        }
    }
}
