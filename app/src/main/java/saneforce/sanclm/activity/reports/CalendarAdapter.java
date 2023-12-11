package saneforce.sanclm.activity.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.tourPlan.calendar.OnDayClickInterface;
import saneforce.sanclm.activity.tourPlan.model.ModelClass;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    ArrayList<String> arrayList = new ArrayList<>();
    Context context;
    OnDayClickInterface onDayClickInterface;

    public CalendarAdapter(ArrayList<String> arrayList, Context context,OnDayClickInterface onDayClickInterface) {
        this.arrayList = arrayList;
        this.context = context;
        this.onDayClickInterface = onDayClickInterface;
    }

    @NonNull
    @Override
    public CalendarAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_calendar_cell, parent, false);
        return new CalendarAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.MyViewHolder holder, int position) {
        holder.dateNo.setText(arrayList.get(holder.getAbsoluteAdapterPosition()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!arrayList.get(holder.getAbsoluteAdapterPosition()).equals("")){
                    onDayClickInterface.onDayClicked(holder.getAbsoluteAdapterPosition(),arrayList.get(holder.getAbsoluteAdapterPosition()),new ModelClass()); // Used the same Interface class which used for TourPlan.So passing 1st and 3rd argument for no purpose
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dateNo;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            dateNo = itemView.findViewById(R.id.dateNo);
        }
    }
}
