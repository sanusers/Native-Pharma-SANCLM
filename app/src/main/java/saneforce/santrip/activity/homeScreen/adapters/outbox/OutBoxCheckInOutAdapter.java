package saneforce.santrip.activity.homeScreen.adapters.outbox;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.modelClass.CheckInOutModelClass;

public class OutBoxCheckInOutAdapter extends RecyclerView.Adapter<OutBoxCheckInOutAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CheckInOutModelClass> checkInOutModelList;

    public OutBoxCheckInOutAdapter(Activity activity, Context context, ArrayList<CheckInOutModelClass> checkInOutModelList) {
        this.context = context;
        this.activity = activity;
        this.checkInOutModelList = checkInOutModelList;
    }

    @NonNull
    @Override
    public OutBoxCheckInOutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_check_view, parent, false);
        return new OutBoxCheckInOutAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutBoxCheckInOutAdapter.ViewHolder holder, int position) {

        if (checkInOutModelList.get(position).getCheckInTime() != null) {
            if (checkInOutModelList.get(position).getCheckInTime().isEmpty()) {
                holder.tagInTime.setVisibility(View.INVISIBLE);
                holder.tvInTime.setVisibility(View.INVISIBLE);
            } else {
                holder.tagInTime.setVisibility(View.VISIBLE);
                holder.tagInTime.setVisibility(View.VISIBLE);
                holder.tvInTime.setText(checkInOutModelList.get(position).getCheckInTime());
            }
        } else {
            holder.tagInTime.setVisibility(View.INVISIBLE);
            holder.tvInTime.setVisibility(View.INVISIBLE);
        }
        if (checkInOutModelList.get(position).getCheckOutTime() != null) {
            if (checkInOutModelList.get(position).getCheckOutTime().isEmpty()) {
                holder.tagOutTime.setVisibility(View.INVISIBLE);
                holder.tvOutTime.setVisibility(View.INVISIBLE);
            } else {
                holder.tagOutTime.setVisibility(View.VISIBLE);
                holder.tvOutTime.setVisibility(View.VISIBLE);
                holder.tvOutTime.setText(checkInOutModelList.get(position).getCheckOutTime());
            }
        } else {
            holder.tagOutTime.setVisibility(View.INVISIBLE);
            holder.tvOutTime.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return checkInOutModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvInTime;
        final TextView tvOutTime;
        final TextView tagInTime;
        final TextView tagOutTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInTime = itemView.findViewById(R.id.tvCheckIn);
            tagInTime = itemView.findViewById(R.id.tagCheckIn);
            tvOutTime = itemView.findViewById(R.id.tvCheckOut);
            tagOutTime = itemView.findViewById(R.id.tagCheckOut);
        }
    }
}