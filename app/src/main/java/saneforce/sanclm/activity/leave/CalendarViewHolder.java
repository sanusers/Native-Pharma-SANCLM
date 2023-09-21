package saneforce.sanclm.activity.leave;


import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.time.LocalDate;

import saneforce.sanclm.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
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

//    @Override
//    public void onClick(View view) {
//        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
//
//        if (!dayOfMonth.getText().toString().equals("")) {
//            // Check if the current date is already selected
//            if (!isSelected) {
//                // Change color and background when selected
//                dayOfMonth.setTextColor(Color.parseColor("#FFFFFF"));
//                day_bgd.setBackgroundResource(R.drawable.today_calenderbgd);
//                isSelected = true; // Mark as selected
//            } else {
//                // Reset color and background when deselected
//                dayOfMonth.setTextColor(Color.parseColor("#282A3C"));
//                day_bgd.setBackgroundResource(R.drawable.calender_square);
//                isSelected = false; // Mark as deselected
//            }
//        }else{
//
//        }
//    }
}

