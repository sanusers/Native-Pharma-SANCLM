package saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.CalendarModel;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.SelectedDCRModel;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    private Context context;
    private HashMap<String, List<CalendarModel>> calendarMap;
    private List<String> keySet;
    private CalendarDayClickListener calendarDayClickListener;
    private CalendarDayMenuClickListener calendarDayMenuClickListener;

    public enum Mode{
        EDIT,
        NEW
    }

    public interface CalendarDayClickListener {
        void onClick(CalendarModel calendarModel, Mode mode);
    }

    public interface CalendarDayMenuClickListener {
        void onClick(CalendarModel calendarModel, MenuItem menuItem);
    }

    public CalendarAdapter() {
    }

    public CalendarAdapter(Context context, HashMap<String, List<CalendarModel>> calendarMap, List<String> keySet, CalendarDayClickListener calendarDayClickListener, CalendarDayMenuClickListener calendarDayMenuClickListener) {
        this.context = context;
        this.calendarMap = calendarMap;
        this.keySet = keySet;
        this.calendarDayClickListener = calendarDayClickListener;
        this.calendarDayMenuClickListener = calendarDayMenuClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stp_calendar_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        List<CalendarModel> calendarModelList = calendarMap.get(keySet.get(position));
        if(calendarModelList != null && !calendarModelList.isEmpty()) {
            for (int index = 0; index<calendarModelList.size(); index++) {
                CalendarModel calendarModel = calendarModelList.get(index);
                List<SelectedDCRModel> dcrModelList = calendarModel.getDcrModelList();
                switch (index){
                    case 0:
                        if(dcrModelList != null && !dcrModelList.isEmpty()) {
                            holder.cl_add1.setVisibility(View.INVISIBLE);
                            holder.ll_dcr_data1.setVisibility(View.VISIBLE);
                            holder.cl_add1.setEnabled(false);
                            holder.dayTag1.setText(calendarModel.getCaption());
                            CalendarDCRAdapter calendarDCRAdapter = new CalendarDCRAdapter(context, dcrModelList);
                            holder.rv_dcr_data1.setAdapter(calendarDCRAdapter);
                            holder.options1.setOnClickListener(v -> {
                                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                                PopupMenu popupMenu = new PopupMenu(wrapper, v, Gravity.END);
                                popupMenu.getMenuInflater().inflate(R.menu.stp_calendar_menu, popupMenu.getMenu());
                                popupMenu.show();
                                popupMenu.setOnMenuItemClickListener(menuItem -> {
                                    calendarDayMenuClickListener.onClick(calendarModel, menuItem);
                                    return true;
                                });
                            });
//                            holder.ll_dcr_data1.setOnClickListener( v-> {
//                                calendarDayClickListener.onClick(calendarModel, Mode.EDIT);
//                            });
                        }else {
                            holder.cl_add1.setVisibility(View.VISIBLE);
                            holder.ll_dcr_data1.setVisibility(View.INVISIBLE);
                            holder.cl_add1.setOnClickListener(v -> {
                                calendarDayClickListener.onClick(calendarModel, Mode.NEW);
                            });
                            holder.dayCaption1.setText(calendarModel.getCaption());
                            if(calendarModel.isVisible()) {
                                holder.cell1.setAlpha(1f);
                                holder.cl_add1.setEnabled(true);
                            }else {
                                holder.cell1.setAlpha(0.5f);
                                holder.cl_add1.setEnabled(false);
                            }
                        }
                        break;

                    case 1:
                        if(dcrModelList != null && !dcrModelList.isEmpty()) {
                            holder.cl_add2.setVisibility(View.INVISIBLE);
                            holder.ll_dcr_data2.setVisibility(View.VISIBLE);
                            holder.cl_add2.setEnabled(false);
                            holder.dayTag2.setText(calendarModel.getCaption());
                            CalendarDCRAdapter calendarDCRAdapter = new CalendarDCRAdapter(context, dcrModelList);
                            holder.rv_dcr_data2.setAdapter(calendarDCRAdapter);
                            holder.options2.setOnClickListener(v -> {
                                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                                PopupMenu popupMenu = new PopupMenu(wrapper, v, Gravity.END);
                                popupMenu.getMenuInflater().inflate(R.menu.stp_calendar_menu, popupMenu.getMenu());
                                popupMenu.show();
                                popupMenu.setOnMenuItemClickListener(menuItem -> {
                                    calendarDayMenuClickListener.onClick(calendarModel, menuItem);
                                    return true;
                                });
                            });
//                            holder.ll_dcr_data2.setOnClickListener( v-> {
//                                calendarDayClickListener.onClick(calendarModel, Mode.EDIT);
//                            });
                        }else {
                            holder.cl_add2.setVisibility(View.VISIBLE);
                            holder.ll_dcr_data2.setVisibility(View.INVISIBLE);
                            holder.cl_add2.setOnClickListener(v -> {
                                calendarDayClickListener.onClick(calendarModel, Mode.NEW);
                            });
                            holder.dayCaption2.setText(calendarModel.getCaption());
                            if(calendarModel.isVisible()) {
                                holder.cell2.setAlpha(1f);
                                holder.cl_add2.setEnabled(true);
                            }else {
                                holder.cell2.setAlpha(0.5f);
                                holder.cl_add2.setEnabled(false);
                            }
                        }
                        break;

                    case 2:
                        if(dcrModelList != null && !dcrModelList.isEmpty()) {
                            holder.cl_add3.setVisibility(View.INVISIBLE);
                            holder.ll_dcr_data3.setVisibility(View.VISIBLE);
                            holder.cl_add3.setEnabled(false);
                            holder.dayTag3.setText(calendarModel.getCaption());
                            CalendarDCRAdapter calendarDCRAdapter = new CalendarDCRAdapter(context, dcrModelList);
                            holder.rv_dcr_data3.setAdapter(calendarDCRAdapter);
                            holder.options3.setOnClickListener(v -> {
                                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                                PopupMenu popupMenu = new PopupMenu(wrapper, v, Gravity.END);
                                popupMenu.getMenuInflater().inflate(R.menu.stp_calendar_menu, popupMenu.getMenu());
                                popupMenu.show();
                                popupMenu.setOnMenuItemClickListener(menuItem -> {
                                    calendarDayMenuClickListener.onClick(calendarModel, menuItem);
                                    return true;
                                });
                            });
//                            holder.ll_dcr_data3.setOnClickListener( v-> {
//                                calendarDayClickListener.onClick(calendarModel, Mode.EDIT);
//                            });
                        }else {
                            holder.cl_add3.setVisibility(View.VISIBLE);
                            holder.ll_dcr_data3.setVisibility(View.INVISIBLE);
                            holder.cl_add3.setOnClickListener(v -> {
                                calendarDayClickListener.onClick(calendarModel, Mode.NEW);
                            });
                            holder.dayCaption3.setText(calendarModel.getCaption());
                            if(calendarModel.isVisible()) {
                                holder.cell3.setAlpha(1f);
                                holder.cl_add3.setEnabled(true);
                            }else {
                                holder.cell3.setAlpha(0.5f);
                                holder.cl_add3.setEnabled(false);
                            }
                        }
                        break;

                    case 3:
                        if(dcrModelList != null && !dcrModelList.isEmpty()) {
                            holder.cl_add4.setVisibility(View.INVISIBLE);
                            holder.ll_dcr_data4.setVisibility(View.VISIBLE);
                            holder.cl_add4.setEnabled(false);
                            holder.dayTag4.setText(calendarModel.getCaption());
                            CalendarDCRAdapter calendarDCRAdapter = new CalendarDCRAdapter(context, dcrModelList);
                            holder.rv_dcr_data4.setAdapter(calendarDCRAdapter);
                            holder.options4.setOnClickListener(v -> {
                                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                                PopupMenu popupMenu = new PopupMenu(wrapper, v, Gravity.END);
                                popupMenu.getMenuInflater().inflate(R.menu.stp_calendar_menu, popupMenu.getMenu());
                                popupMenu.show();
                                popupMenu.setOnMenuItemClickListener(menuItem -> {
                                    calendarDayMenuClickListener.onClick(calendarModel, menuItem);
                                    return true;
                                });
                            });
//                            holder.ll_dcr_data4.setOnClickListener( v-> {
//                                calendarDayClickListener.onClick(calendarModel, Mode.EDIT);
//                            });
                        }else {
                            holder.cl_add4.setVisibility(View.VISIBLE);
                            holder.ll_dcr_data4.setVisibility(View.INVISIBLE);
                            holder.cl_add4.setOnClickListener(v -> {
                                calendarDayClickListener.onClick(calendarModel, Mode.NEW);
                            });
                            holder.dayCaption4.setText(calendarModel.getCaption());
                            if(calendarModel.isVisible()) {
                                holder.cell4.setAlpha(1f);
                                holder.cl_add4.setEnabled(true);
                            }else {
                                holder.cell4.setAlpha(0.5f);
                                holder.cl_add4.setEnabled(false);
                            }
                        }
                        break;

                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return calendarMap.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dayCaption1, dayCaption2, dayCaption3, dayCaption4;
        TextView dayTag1, dayTag2, dayTag3, dayTag4;
        TextView options1, options2, options3, options4;
        View cell1, cell2, cell3, cell4;
        ConstraintLayout cl_add1, cl_add2, cl_add3, cl_add4;
        LinearLayout ll_dcr_data1, ll_dcr_data2, ll_dcr_data3, ll_dcr_data4;
        RecyclerView rv_dcr_data1, rv_dcr_data2, rv_dcr_data3, rv_dcr_data4;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cell1 = itemView.findViewById(R.id.monday1);
            cell2 = itemView.findViewById(R.id.monday2);
            cell3 = itemView.findViewById(R.id.monday3);
            cell4 = itemView.findViewById(R.id.monday4);
            cl_add1 = cell1.findViewById(R.id.cl_add);
            cl_add2 = cell2.findViewById(R.id.cl_add);
            cl_add3 = cell3.findViewById(R.id.cl_add);
            cl_add4 = cell4.findViewById(R.id.cl_add);
            ll_dcr_data1 = cell1.findViewById(R.id.ll_dcr_data);
            ll_dcr_data2 = cell2.findViewById(R.id.ll_dcr_data);
            ll_dcr_data3 = cell3.findViewById(R.id.ll_dcr_data);
            ll_dcr_data4 = cell4.findViewById(R.id.ll_dcr_data);
            rv_dcr_data1 = cell1.findViewById(R.id.rv_dcr_data_stp);
            rv_dcr_data2 = cell2.findViewById(R.id.rv_dcr_data_stp);
            rv_dcr_data3 = cell3.findViewById(R.id.rv_dcr_data_stp);
            rv_dcr_data4 = cell4.findViewById(R.id.rv_dcr_data_stp);
            dayCaption1 = cell1.findViewById(R.id.day_tag_add);
            dayCaption2 = cell2.findViewById(R.id.day_tag_add);
            dayCaption3 = cell3.findViewById(R.id.day_tag_add);
            dayCaption4 = cell4.findViewById(R.id.day_tag_add);
            dayTag1 = cell1.findViewById(R.id.day_tag);
            dayTag2 = cell2.findViewById(R.id.day_tag);
            dayTag3 = cell3.findViewById(R.id.day_tag);
            dayTag4 = cell4.findViewById(R.id.day_tag);
            options1 = cell1.findViewById(R.id.options);
            options2 = cell2.findViewById(R.id.options);
            options3 = cell3.findViewById(R.id.options);
            options4 = cell4.findViewById(R.id.options);
        }
    }
}
