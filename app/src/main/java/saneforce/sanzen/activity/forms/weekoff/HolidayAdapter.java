//package saneforce.sanzen.activity.forms.weekoff;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//import saneforce.sanzen.R;
//
//public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.ViewHolder> {
//    ArrayList<String> list_id = new ArrayList<>();
//    ArrayList<String> dateslist;
//    ArrayList<FormsModelClass> res_List;
//    Context context;
//    int colrid=0;
//    String[] colr={"#F97168","#C0F968","#53F78C","#53F7E3","#DCEBFC","#9E5DFC","#F65EF8","#F85EAD","#63CFEC"};
//
//    public HolidayAdapter(ArrayList<FormsModelClass> res_List, Context context) {
//        this.res_List = res_List;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holiday_adapter, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final FormsModelClass app_adapt = res_List.get(position);
//
//        holder.month_name.setText(app_adapt.getMonthname());
//        holder.day.setText(app_adapt.getDate());
//        String bar = app_adapt.getWeekname().substring(0, 3);
//        holder.weekdays.setText(bar);
//        holder.Holiday.setText(app_adapt.getHolidayname());
//
//        holder.Holiday.setTextColor(Color.parseColor(app_adapt.getAllclr()));
//        holder.line_2.setBackgroundColor(Color.parseColor(app_adapt.getAllclr()));
//        holder.viewclr.setBackgroundColor(Color.parseColor(app_adapt.getAllclr()));
//        holder.Holiday.setBackgroundColor(Color.parseColor(app_adapt.getBackgrdclr()));
//        holder.line_22.setBackgroundColor(Color.parseColor(app_adapt.getDub_coltcode()));
//    }
//
//    @Override
//    public int getItemCount() {
//        return res_List.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView month_name, day, weekdays, Holiday;
//        LinearLayout Holidayl, line, line_2,line_22,back_clr;
//        View viewclr;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            Holidayl = itemView.findViewById(R.id.Holidayl);
//            month_name = itemView.findViewById(R.id.month_name);
//            day = itemView.findViewById(R.id.day);
//            weekdays = itemView.findViewById(R.id.weekdays);
//            Holiday = itemView.findViewById(R.id.Holiday);
//            line = itemView.findViewById(R.id.line);
//            line_2 = itemView.findViewById(R.id.line_2);
//            viewclr = itemView.findViewById(R.id.viewclr);
//            line_22 = itemView.findViewById(R.id.line_22);
//            back_clr = itemView.findViewById(R.id.back_clr);
//        }
//    }
//}
package saneforce.sanzen.activity.forms.weekoff;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;

public class HolidayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_YEAR_HEADER = 0;
    private static final int TYPE_HOLIDAY_ITEM = 1;
    private static final String YEAR_HEADER_PREFIX = "YEAR_HEADER:";

    ArrayList<FormsModelClass> res_List;
    Context context;

    public HolidayAdapter(ArrayList<FormsModelClass> res_List, Context context) {
        this.res_List = res_List;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        FormsModelClass item = res_List.get(position);
        // Check if it's a year header by looking at monthname
        if (item.getMonthname() != null && item.getMonthname().startsWith(YEAR_HEADER_PREFIX)) {
            return TYPE_YEAR_HEADER;
        }
        return TYPE_HOLIDAY_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_YEAR_HEADER) {
            View view = inflater.inflate(R.layout.year_header_item, parent, false);
            return new YearHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.holiday_adapter, parent, false);
            return new HolidayItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FormsModelClass item = res_List.get(position);

        if (holder.getItemViewType() == TYPE_YEAR_HEADER) {
            YearHeaderViewHolder yearHolder = (YearHeaderViewHolder) holder;
            // Extract year from "YEAR_HEADER:2025"
            String monthname = item.getMonthname();
            String year = monthname.replace(YEAR_HEADER_PREFIX, "");
            yearHolder.yearText.setText(year);
        } else {
            HolidayItemViewHolder holidayHolder = (HolidayItemViewHolder) holder;

            // Set holiday data
            holidayHolder.month_name.setText(item.getMonthname());
            holidayHolder.day.setText(item.getDate());

            if (item.getWeekname() != null && !item.getWeekname().isEmpty()) {
                String weekShort = item.getWeekname().length() >= 3 ?
                        item.getWeekname().substring(0, 3) : item.getWeekname();
                holidayHolder.weekdays.setText(weekShort);
            } else {
                holidayHolder.weekdays.setText("");
            }

            holidayHolder.Holiday.setText(item.getHolidayname());

            // Set colors
            try {
                holidayHolder.Holiday.setTextColor(Color.parseColor(item.getAllclr()));
                holidayHolder.line_2.setBackgroundColor(Color.parseColor(item.getAllclr()));
                holidayHolder.viewclr.setBackgroundColor(Color.parseColor(item.getAllclr()));
                holidayHolder.Holiday.setBackgroundColor(Color.parseColor(item.getBackgrdclr()));
                holidayHolder.line_22.setBackgroundColor(Color.parseColor(item.getDub_coltcode()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return res_List.size();
    }

    // ViewHolder for Year Header
    public static class YearHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView yearText;

        public YearHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            yearText = itemView.findViewById(R.id.year_text);
        }
    }

    // ViewHolder for Holiday Item (your existing ViewHolder)
    public static class HolidayItemViewHolder extends RecyclerView.ViewHolder {
        TextView month_name, day, weekdays, Holiday;
        LinearLayout Holidayl, line, line_2, line_22, back_clr;
        View viewclr;

        public HolidayItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Holidayl = itemView.findViewById(R.id.Holidayl);
            month_name = itemView.findViewById(R.id.month_name);
            day = itemView.findViewById(R.id.day);
            weekdays = itemView.findViewById(R.id.weekdays);
            Holiday = itemView.findViewById(R.id.Holiday);
            line = itemView.findViewById(R.id.line);
            line_2 = itemView.findViewById(R.id.line_2);
            viewclr = itemView.findViewById(R.id.viewclr);
            line_22 = itemView.findViewById(R.id.line_22);
            back_clr = itemView.findViewById(R.id.back_clr);
        }
    }
}