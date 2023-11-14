package saneforce.sanclm.activity.leave;

import android.graphics.Color;
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
import saneforce.sanclm.utility.TimeUtils;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private LocalDate selectedDate;
    LocalDate fromdate,currentDate,lap_dats,mountyearval;
    String current_month,fromdate_val;
    ArrayList<String>ldates=new ArrayList<>();
    private final OnItemListener onItemListener;

    String setval;
    String val32;
    public CalendarAdapter(ArrayList<String> daysInMonth, OnItemListener onItemListener, LocalDate selectedDate,String month,String fromdate_val,String setval) {
        this.daysOfMonth = daysInMonth;
        this.onItemListener = onItemListener;
        this.selectedDate = selectedDate;
        this.current_month =month;
        this.fromdate_val =fromdate_val;
        this.setval =setval;
        ldates.addAll(Leave_Application.ltypecount);

        val32=  TimeUtils.GetConvertedDate(TimeUtils.FORMAT_23, TimeUtils.FORMAT_24, current_month);
    }
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_screen, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        if(setval.equals("1")){
            return new CalendarViewHolder(view, onItemListener,currentDate,current_month);
        }else{
            if(!fromdate_val.equals("") ) {
                fromdate = LocalDate.parse((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_4, fromdate_val)));//FORMAT_23
            }
            return new CalendarViewHolder(view, onItemListener, fromdate, current_month);
        }

    }
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(daysOfMonth.get(position));
        String dayText = daysOfMonth.get(position);
        holder.dayOfMonth.setText(dayText);

        if(fromdate_val.equals("") || fromdate_val.equals("null") ){

        }else{
            fromdate= LocalDate.parse((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_4,fromdate_val )));
        }

        if (!dayText.equals("")) {

            if(setval.equals("1")){

//                currentDate = LocalDate.now();
//                int day = Integer.parseInt(dayText);
//                if (day < currentDate.getDayOfMonth() && selectedDate.getMonth() == currentDate.getMonth() && selectedDate.getDayOfYear() == currentDate.getDayOfYear() ) {
//                    holder.dayOfMonth.setTextColor(Color.parseColor("#B0B0B0"));
//                } else if (day == currentDate.getDayOfMonth() && selectedDate.getMonth() == currentDate.getMonth() && selectedDate.getDayOfYear() == currentDate.getDayOfYear()) {
//                    holder.dayOfMonth.setTextColor(Color.parseColor("#FFFFFF"));
//                    holder.day_bgd.setBackgroundResource(R.drawable.today_calenderbgd);
//                } else if(!fromdate_val.equals("") ){
//                    int day1 = Integer.parseInt(dayText);
//                    if (day1 < fromdate.getDayOfMonth()&& selectedDate.getMonth() ==fromdate.getMonth()&& selectedDate.getDayOfYear() == currentDate.getDayOfYear()) {
//
//                        holder.dayOfMonth.setTextColor(Color.parseColor("#B0B0B0"));
//                    }
//                } else {
//                    holder.dayOfMonth.setTextColor(Color.parseColor("#282A3C"));
//                }
            }else{
                currentDate = (fromdate);
                int day = Integer.parseInt(dayText);
                if (day < currentDate.getDayOfMonth() && selectedDate.getMonth() == currentDate.getMonth() && selectedDate.getDayOfYear() == currentDate.getDayOfYear() ) {
                    holder.dayOfMonth.setTextColor(Color.parseColor("#B0B0B0"));
                } else if (day == currentDate.getDayOfMonth() && selectedDate.getMonth() == currentDate.getMonth() && selectedDate.getDayOfYear() == currentDate.getDayOfYear()) {
                    holder.dayOfMonth.setTextColor(Color.parseColor("#282A3C"));
                } else if(!fromdate_val.equals("") ){
                    int day1 = Integer.parseInt(dayText);
                    if (day1 < fromdate.getDayOfMonth()&& selectedDate.getMonth() ==fromdate.getMonth()&& selectedDate.getDayOfYear() == currentDate.getDayOfYear()) {
                        holder.dayOfMonth.setTextColor(Color.parseColor("#B0B0B0"));
                    }
                } else {
                    holder.dayOfMonth.setTextColor(Color.parseColor("#282A3C"));
                }
            }
        } else {
            holder.dayOfMonth.setTextColor(Color.parseColor("#B0B0B0"));
        }

//        if(!dayText.equals("")){
//            int day_1= Integer.parseInt((dayText));
//
//            if(ldates.size() !=0){
//                for(int b=0;b<ldates.size();b++){
//                    lap_dats= LocalDate.parse(ldates.get(b));
//                    mountyearval= LocalDate.parse(val32);
//                    Log.d("values",mountyearval.getMonthValue()+"---"+mountyearval.getYear());
//                }
//            }
//
//        }


    }
    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }


    public static class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dayOfMonth;
        ConstraintLayout day_bgd;
        private final OnItemListener onItemListener;
        String month;

        public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener,LocalDate currentDate,String month) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            day_bgd = itemView.findViewById(R.id.day_bgd);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
            this.month = month;
        }

        @Override
        public void onClick(View view) {

            String dayText = dayOfMonth.getText().toString();
            if (!dayText.isEmpty()) {
                // Notify the listener about the click event
                onItemListener.onItemClick(getAdapterPosition(), dayText);

            }
        }
    }


    public interface  OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
