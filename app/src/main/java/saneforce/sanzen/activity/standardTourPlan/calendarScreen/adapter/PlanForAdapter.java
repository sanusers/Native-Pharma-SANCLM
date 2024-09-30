package saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.PlanForModel;

public class PlanForAdapter extends RecyclerView.Adapter<PlanForAdapter.MyViewHolder> {

    private Context context;
    private List<PlanForModel> planForModelList;

    public PlanForAdapter() {
    }

    public PlanForAdapter(Context context, List<PlanForModel> planForModelList) {
        this.context = context;
        this.planForModelList = planForModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_for_stp, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PlanForModel planForModel = planForModelList.get(position);
        holder.icon.setImageResource(planForModel.getImageID());
        holder.caption.setText(planForModel.getCaption());
        holder.total.setText(String.valueOf(planForModel.getTotal()));
        holder.selected.setText(String.valueOf(planForModel.getSelected()));
    }

    @Override
    public int getItemCount() {
        return planForModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView caption, total, selected;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.img_plan_for);
            caption = itemView.findViewById(R.id.caption);
            total = itemView.findViewById(R.id.total);
            selected = itemView.findViewById(R.id.selected);
        }
    }
}
