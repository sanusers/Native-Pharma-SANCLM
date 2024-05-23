package saneforce.sanzen.activity.reports;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.calendar.OnDayClickInterface;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.utility.TimeUtils;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    ArrayList<String> arrayList = new ArrayList<>();
    Context context;
    OnDayClickInterface onDayClickInterface;
    LocalDate localDate;

    public CalendarAdapter(ArrayList<String> arrayList, Context context,LocalDate localDate,OnDayClickInterface onDayClickInterface) {
        this.arrayList = arrayList;
        this.context = context;
        this.onDayClickInterface = onDayClickInterface;
        this.localDate = localDate;
        localDate = LocalDate.now();

    }

    @NonNull
    @Override
    public CalendarAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_calendar_cell, parent, false);
        return new CalendarAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.MyViewHolder holder, int position) {
        String dateString = arrayList.get(position);
        holder.dateNo.setText(dateString);
        if (!TextUtils.isEmpty(dateString)) {
            String myDate = monthYearFromDate(localDate, TimeUtils.FORMAT_24) + "-" + dateString;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                LocalDate parsedMyDate = LocalDate.parse(myDate, formatter);
                LocalDate currentDate = LocalDate.now();
                int comparisonResult = parsedMyDate.compareTo(currentDate);
                if (comparisonResult < 0) {
                    holder.itemView.setEnabled(true);
                } else if (comparisonResult > 0) {
                    holder.itemView.setEnabled(false);
                } else {
                    holder.itemView.setEnabled(true);
                }
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }
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
    private String monthYearFromDate(LocalDate date, String requiredFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(requiredFormat);
        return date.format(formatter);
    }
}
