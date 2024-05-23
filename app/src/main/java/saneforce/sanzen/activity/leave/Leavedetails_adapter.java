package saneforce.sanzen.activity.leave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;

public class Leavedetails_adapter extends RecyclerView.Adapter<Leavedetails_adapter.ViewHolder> {
    ArrayList<Leave_modelclass> leave_Dates;

    Context context;

    public Leavedetails_adapter(Context context, ArrayList<Leave_modelclass> leave_Dates) {
        this.context = context;
        this.leave_Dates = leave_Dates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_datelist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Leave_modelclass app_adapt = leave_Dates.get(position);
        if (!app_adapt.getLeaveDates().equals("")||!app_adapt.getLeaveDates().equals("null")) {
            holder.L_date.setText(app_adapt.getLeaveDates());
        }
    }



    @Override
    public int getItemCount() {
        return leave_Dates.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView L_plan, L_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            L_plan = itemView.findViewById(R.id.L_plan);
            L_date = itemView.findViewById(R.id.L_date);
        }
    }
}
