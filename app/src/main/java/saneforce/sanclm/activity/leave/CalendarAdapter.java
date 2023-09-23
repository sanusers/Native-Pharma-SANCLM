package saneforce.sanclm.activity.leave;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.time.LocalDate;
import java.util.ArrayList;

import saneforce.sanclm.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    public static Context OnItemListener;
    private final ArrayList<String> daysOfMonth;
    private LocalDate selectedDate,currentdate1;
    private final LocalDate currentDate;
    String current_month;

    private final OnItemListener onItemListener;


    public CalendarAdapter(ArrayList<String> daysInMonth, OnItemListener onItemListener, LocalDate selectedDate,String month) {
        this.daysOfMonth = daysInMonth;
        this.onItemListener = onItemListener;
        this.selectedDate = selectedDate;
        this.currentDate = LocalDate.now();
        this.current_month =month;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_screen, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener, currentDate, current_month);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        String dayText = daysOfMonth.get(position);
        holder.dayOfMonth.setText(dayText);
        holder.dayOfMonth.setText(daysOfMonth.get(position));

        if (!dayText.equals("")) {
            int day = Integer.parseInt(dayText);

            // Check if the date is before the current date
            if (day < currentDate.getDayOfMonth() && selectedDate.getMonth() == currentDate.getMonth()) {
                // Date is before the current date, apply a different color
                holder.dayOfMonth.setTextColor(Color.parseColor("#B0B0B0")); // Adjust the color code
            } else if (day == currentDate.getDayOfMonth() && selectedDate.getMonth() == currentDate.getMonth()) {
                // Date is the current date, apply a different color
                holder.dayOfMonth.setTextColor(Color.parseColor("#FF0000")); // Red color as an example
                holder.day_bgd.setBackgroundResource(R.drawable.today_calenderbgd); // Highlight the current date
            } else {
                // Date is after the current date, set the default color
                holder.dayOfMonth.setTextColor(Color.parseColor("#282A3C")); // Adjust the color code
            }
        } else {
            // Handle empty date cells as needed (e.g., set a default color)
            holder.dayOfMonth.setTextColor(Color.parseColor("#B0B0B0")); // Adjust the color code
        }

        // Convert the dayText to an integer
        if (!dayText.equals("")) {
            int day = Integer.parseInt(dayText);

            // Get the current date
            LocalDate currentDate = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentDate = LocalDate.now();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (day == currentDate.getDayOfMonth() && selectedDate.getMonth() == currentDate.getMonth()) {
                    // Highlight the current date
                    holder.day_bgd.setBackgroundResource(R.drawable.today_calenderbgd);
                    holder.dayOfMonth.setTextColor(Color.WHITE);
                }
            }
        }

    }
    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }


    public static class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dayOfMonth;
        ConstraintLayout day_bgd;
        private final CalendarAdapter.OnItemListener onItemListener;
        private boolean isSelected = false; // Add a flag to track selection
        private final LocalDate currentDate ;
        private LocalDate selectedDate;
        String month;

        public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener,LocalDate currentDate,String month) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            day_bgd = itemView.findViewById(R.id.day_bgd);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
            this.currentDate = currentDate;
            this.month = month;

//        setInitialState();

        }

        @Override
        public void onClick(View view) {
            selectedDate = LocalDate.now();
            String dayText = dayOfMonth.getText().toString();
            String dateval=(String)dayOfMonth.getText();

            String datecurrent=(dateval+" "+month);
//        LocalDate livedate= LocalDate.parse(TPDateConvert);
            int day = Integer.parseInt(dayText);

            if (!dayText.isEmpty()) {


//            if(livedate.isEqual(currentDate)||livedate.isAfter(currentDate)){
                LocalDate clickedDate = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), day);
                if (clickedDate.isEqual(currentDate) || clickedDate.isAfter(currentDate)) {
                    // Change color and background when selected
                    if (!isSelected) {
                        dayOfMonth.setTextColor(Color.parseColor("#FFFFFF"));
                        day_bgd.setBackgroundResource(R.drawable.today_calenderbgd);
                        isSelected = true; // Mark as selected
                    } else {
                        // Reset color and background when deselected
                        dayOfMonth.setTextColor(Color.parseColor("#282A3C"));
                        day_bgd.setBackgroundResource(R.drawable.calender_square);
                        isSelected = false; // Mark as deselected
                    }
                }


                // Notify the listener about the click event
                onItemListener.onItemClick(getAdapterPosition(), dayText);


            }
        }

    }

    public interface  OnItemListener {
        void onItemClick(int position, String dayText);


    }
}
