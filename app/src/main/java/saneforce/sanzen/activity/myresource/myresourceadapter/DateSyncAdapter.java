package saneforce.sanzen.activity.myresource.myresourceadapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.myresourcemodel.DateSyncModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.utility.TimeUtils;

public class DateSyncAdapter extends RecyclerView.Adapter<DateSyncAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<DateSyncModel> dateSyncModel;
    public DateSyncAdapter(Context mContext,ArrayList<DateSyncModel> dateSyncModel){
        this.mContext = mContext;
        this.dateSyncModel = dateSyncModel;
    }
    @NonNull
    @Override
    public DateSyncAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_sync, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateSyncAdapter.ViewHolder holder, int position) {
         DateSyncModel itemModel = dateSyncModel.get(position);
         holder.textDate.setText(CommonUtilsMethods.convertDate(itemModel.getDate()));
         String statusName = "";
         int statusColor = R.color.black;
         if (itemModel.getTbname().equals("leave")){
             statusName = "Leave";
             statusColor = R.color.red_60;
         } else if (itemModel.getTbname().equals("missed")) {
             statusName = "Missed Released";
             statusColor = R.color.Hilo_bay_60;
         } else if (itemModel.getTbname().equals("dcr") && itemModel.getFlg().equals("2")) {
             statusName = "Re Entry";
             statusColor = R.color.pink_60;
         } else if (itemModel.getTbname().equals("dcr") && itemModel.getFlg().equals("3")) {
             statusName = "Rejected";
             statusColor = R.color.brown_60;
         } else if (itemModel.getTbname().equals("dcr") && itemModel.getFlg().equals("0")){
             statusName = "Working";
             statusColor = R.color.yellow_45;
         }
         holder.textStatus.setText(statusName);
        holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), statusColor));

    }

    @Override
    public int getItemCount() {
        if (dateSyncModel!=null){
            return dateSyncModel.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate,textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textStatus = itemView.findViewById(R.id.textStatus);
        }
    }
}
