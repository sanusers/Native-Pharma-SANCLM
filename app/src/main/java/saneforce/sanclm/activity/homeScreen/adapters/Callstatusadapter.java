package saneforce.sanclm.activity.homeScreen.adapters;





import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.modelClass.CallStatusModelClass;


public class Callstatusadapter extends RecyclerView.Adapter<Callstatusadapter.CalendarViewHolder> {
        private final List<String> days;
        private final Context context;
       LocalDate selectedMonth;
    ArrayList<CallStatusModelClass> callsatuslist =new ArrayList<>();

    public Callstatusadapter(List<String> days, Context context, LocalDate selectedMonth) {
        this.days = days;
        this.context = context;
        this.selectedMonth = selectedMonth;
    }

    @NonNull
    @Override
        public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_calderdate_layout, parent, false);
            return new CalendarViewHolder(view);
        }

        @SuppressLint("Range")
        @Override
        public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
            String day = days.get(position);
            holder.dayTextView.setText(day);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyy", Locale.ENGLISH);
            String fullMonthName = selectedMonth.format(formatter);
            String year = selectedMonth.format(formatter1);


            if (!day.equalsIgnoreCase("")) {

                holder.imageView.setVisibility(View.VISIBLE);
            } else {

                holder.imageView.setVisibility(View.GONE);

            }

             @SuppressLint("UseCompatLoadingForDrawables") GradientDrawable drawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.event_point_background);
                drawable.setColor(Color.GREEN);
                holder.imageView.setImageDrawable(drawable);




            if (position == 0) {
                holder.linearLayout.setBackgroundResource(R.drawable.calender_background_a);
            } else if (position <= 6) {
                holder.linearLayout.setBackgroundResource(R.drawable.calender_background_b);

            }else if(position==7 || position==14||position==21||position==28||position==35){
                holder.linearLayout.setBackgroundResource(R.drawable.calender_background_c);

            }else {
                holder.linearLayout.setBackgroundResource(R.drawable.calender_background_d);
            }

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    if(!day.equalsIgnoreCase("")) {

                        HomeDashBoard.text_date.setText(fullMonthName + " " + day + ", " + year);
                        HomeDashBoard.view_calender_layout.setVisibility(View.GONE);
                        HomeDashBoard.ll_tab_layout.setVisibility(View.VISIBLE);
                        HomeDashBoard.viewPager.setVisibility(View.VISIBLE);

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return days.size();
        }

        public class CalendarViewHolder extends RecyclerView.ViewHolder {
            TextView dayTextView;

            ImageView imageView;

            LinearLayout linearLayout;

            public CalendarViewHolder(View itemView) {
                super(itemView);
                dayTextView = itemView.findViewById(R.id.cellDayText);
                imageView = itemView.findViewById(R.id.img_event_point);
                linearLayout=itemView.findViewById(R.id.day_bgd);
            }
        }








    }


