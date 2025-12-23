//package saneforce.sanzen.activity.myresource.leave;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//import saneforce.sanzen.R;
//
//public class LeaveHistoryAdapter extends RecyclerView.Adapter<LeaveHistoryAdapter.ViewHolder> {
//
//    Context context;
//    List<LeaveHistoryModel> list;
//
//    public LeaveHistoryAdapter(Context context, List<LeaveHistoryModel> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context)
//                .inflate(R.layout.item_leave_history, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
//
//        LeaveHistoryModel m = list.get(position);
//
//        h.tvDate.setText(m.fromDate + " To " + m.toDate);
//        h.tvType.setText(m.type);
//        h.tvApplied.setText("Applied : " + m.appliedOn);
//        h.tvReason.setText("Leave Reason : " + m.reason);
//        h.tvDays.setText(String.valueOf(m.days));
//        h.tvStatus.setText(m.status);
//
//        if ("Rejected".equalsIgnoreCase(m.status)) {
//            h.tvStatus.setTextColor(Color.RED);
//            h.tvRejectedReason.setVisibility(View.VISIBLE);
//            h.tvRejectedReason.setText("Rejected Reason : " + m.rejectedReason);
//        } else {
//            h.tvStatus.setTextColor(Color.BLUE);
//            h.tvRejectedReason.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView tvDate, tvStatus, tvType, tvApplied,
//                tvReason, tvRejectedReason, tvDays;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvDate = itemView.findViewById(R.id.tvDate);
//            tvStatus = itemView.findViewById(R.id.tvStatus);
//            tvType = itemView.findViewById(R.id.tvType);
//            tvApplied = itemView.findViewById(R.id.tvApplied);
//            tvReason = itemView.findViewById(R.id.tvReason);
//            tvRejectedReason = itemView.findViewById(R.id.tvRejectedReason);
//            tvDays = itemView.findViewById(R.id.tvDays);
//        }
//    }
//}
package saneforce.sanzen.activity.myresource.leave;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;

public class
LeaveHistoryAdapter extends RecyclerView.Adapter<LeaveHistoryAdapter.ViewHolder> {

    private Context context;
    private List<LeaveHistoryModel> leaveList;

    public LeaveHistoryAdapter(Context context, List<LeaveHistoryModel> leaveList) {
        this.context = context;
        this.leaveList = leaveList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leave_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveHistoryModel leave = leaveList.get(position);
        holder.tvDate.setText(leave.getFromDate() + " To " + leave.getToDate());
        holder.tvType.setText(leave.getLeaveType());
        holder.tvApplied.setText("Applied : " + leave.getCreatedDate());
        holder.tvReason.setText(leave.getReason());
        holder.tvRejectedReason.setText(leave.getRejectedReason());
        holder.tvStatus.setText(leave.getStatus());
        holder.tvDays.setText(leave.getDays());


        if ("Rejected".equalsIgnoreCase(leave.status)) {
            holder.tvStatus.setTextColor(Color.RED);
            holder.tvRejectedReason.setVisibility(View.VISIBLE);
            holder.tvRejectedReason.setText("Rejected Reason : " + leave.rejectedReason);
        } else {
            holder.tvStatus.setTextColor(Color.BLUE);
            holder.tvRejectedReason.setVisibility(View.GONE);
        }
        //holder.tvStatus.setText(leave.status);
//        LeaveHistoryModel leave = leaveList.get(position);
//        holder.tvLeaveName.setText(leave.getLeaveName());
//        holder.tvLeaveSName.setText(leave.getLeaveSName());
//        holder.tvLeaveCode.setText(leave.getLeaveCode());
//        holder.tvCreatedDate.setText(leave.getCreatedDate());
//        if ("Rejected".equalsIgnoreCase(leave.status)) {
//            holder.tvStatus.setTextColor(Color.RED);
//            holder.tvRejectedReason.setVisibility(View.VISIBLE);
//            h.tvRejectedReason.setText("Rejected Reason : " + leave.rejectedReason);
//        } else {
//            h.tvStatus.setTextColor(Color.BLUE);
//            h.tvRejectedReason.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        return leaveList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // TextView tvLeaveName, tvLeaveSName, tvLeaveCode, tvCreatedDate;
        TextView tvType, tvApplied, tvDate, tvStatus, tvReason, tvRejectedReason, tvDays;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvApplied = itemView.findViewById(R.id.tvApplied);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvRejectedReason = itemView.findViewById(R.id.tvRejectedReason);
            tvDays = itemView.findViewById(R.id.tvDays);
            // tvDate=itemView.findViewById(R.id.tvDate);
//            tvDate = itemView.findViewById(R.id.tvDate);
//            tvStatus = itemView.findViewById(R.id.tvStatus);
//            tvType = itemView.findViewById(R.id.tvType);
//            tvCreatedDate = itemView.findViewById(R.id.tvApplied);
            //tvReason = itemView.findViewById(R.id.tvReason);
//            tvRejectedReason = itemView.findViewById(R.id.tvRejectedReason);
//            tvDays = itemView.findViewById(R.id.tvDays);
        }
    }
}

