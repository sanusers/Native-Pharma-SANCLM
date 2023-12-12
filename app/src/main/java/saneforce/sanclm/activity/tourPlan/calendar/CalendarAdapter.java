package saneforce.sanclm.activity.tourPlan.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.tourPlan.model.ModelClass;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    ArrayList<ModelClass> inputData = new ArrayList<>();
    OnDayClickInterface onDayClickInterface;
    Context context;

    public CalendarAdapter () {
    }

    public CalendarAdapter (ArrayList<ModelClass> inputData, Context context, OnDayClickInterface onDayClickInterface) {
        this.inputData = inputData;
        this.context = context;
        this.onDayClickInterface = onDayClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        ModelClass modelClass = inputData.get(holder.getAbsoluteAdapterPosition());
        String date = modelClass.getDayNo();
        holder.dateNo.setText(date);

        if (!date.isEmpty() && !modelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) //if work type is not empty means tour plan added for the date
            holder.cornerImage.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                onDayClickInterface.onDayClicked(holder.getAbsoluteAdapterPosition(), date, inputData.get(holder.getAbsoluteAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount () {
        return inputData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dateNo;
        ImageView cornerImage;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            dateNo = itemView.findViewById(R.id.dateNo);
            cornerImage = itemView.findViewById(R.id.cornerImage);
        }
    }
}
