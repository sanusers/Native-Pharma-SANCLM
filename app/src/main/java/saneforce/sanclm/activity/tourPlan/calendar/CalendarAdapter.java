package saneforce.sanclm.activity.tourPlan.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.tourPlan.ModelClass;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    ArrayList<ModelClass> daysOfMonth= new ArrayList<>();
    OnDayClickInterface onDayClickInterface;
    Context context;

    public CalendarAdapter () {
    }

    public CalendarAdapter (ArrayList<ModelClass> daysOfMonth, Context context, OnDayClickInterface onDayClickInterface) {
        this.daysOfMonth = daysOfMonth;
        this.context = context;
        this.onDayClickInterface = onDayClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_cell,parent,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        String date = daysOfMonth.get(holder.getAdapterPosition()).getDayNo();
        holder.dateNo.setText(date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                onDayClickInterface.onDayClicked(holder.getAdapterPosition(),date,daysOfMonth.get(holder.getAbsoluteAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount () {
        return daysOfMonth.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dateNo;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            dateNo = itemView.findViewById(R.id.dateNo);
        }
    }
}
