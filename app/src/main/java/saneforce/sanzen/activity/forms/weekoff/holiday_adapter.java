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


public class holiday_adapter extends RecyclerView.Adapter<holiday_adapter.ViewHolder> {
    ArrayList<String> list_id = new ArrayList<>();
    ArrayList<String> dateslist;
    ArrayList<fromsmodelclass> res_List;
    Context context;
    int colrid=0;
    String[] colr={"#F97168","#C0F968","#53F78C","#53F7E3","#DCEBFC","#9E5DFC","#F65EF8","#F85EAD","#63CFEC"};

    public holiday_adapter(ArrayList<fromsmodelclass> res_List, Context context) {
        this.res_List = res_List;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holiday_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final fromsmodelclass app_adapt = res_List.get(position);

        holder.month_name.setText(app_adapt.getMonthname());
        holder.day.setText(app_adapt.getDate());
        String bar = app_adapt.getWeekname().substring(0, 3);
        holder.weekdays.setText(bar);
        holder.Holiday.setText(app_adapt.getHolidayname());



        holder.Holiday.setTextColor(Color.parseColor(app_adapt.getAllclr()));
        holder.line_2.setBackgroundColor(Color.parseColor(app_adapt.getAllclr()));
        holder.viewclr.setBackgroundColor(Color.parseColor(app_adapt.getAllclr()));
        holder.Holiday.setBackgroundColor(Color.parseColor(app_adapt.getBackgrdclr()));
        holder.line_22.setBackgroundColor(Color.parseColor(app_adapt.getDub_coltcode()));

    }

    @Override
    public int getItemCount() {
        return res_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView month_name, day, weekdays, Holiday;
        LinearLayout Holidayl, line, line_2,line_22,back_clr;

        View viewclr;

        public ViewHolder(@NonNull View itemView) {
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
