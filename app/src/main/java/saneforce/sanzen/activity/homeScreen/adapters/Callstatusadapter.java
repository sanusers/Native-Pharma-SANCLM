package saneforce.sanzen.activity.homeScreen.adapters;


import static saneforce.sanzen.activity.homeScreen.HomeDashBoard.SequentialEntry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
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
import java.util.Locale;
import java.util.TreeSet;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.modelClass.EventCalenderModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.MyDayPlanEntriesNeeded;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;


public class Callstatusadapter extends RecyclerView.Adapter<Callstatusadapter.CalendarViewHolder> {
    private final ArrayList<EventCalenderModelClass> days;
    private final Context context;
    LocalDate selectedMonth;
    CommonUtilsMethods commonUtilsMethods;
    TreeSet<String> dateStrings = new TreeSet<>();
    String selectedDate;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;


    public Callstatusadapter(ArrayList<EventCalenderModelClass> days, Context context, LocalDate selectedMonth) {
        this.days = days;
        this.context = context;
        this.selectedMonth = selectedMonth;
        commonUtilsMethods = new CommonUtilsMethods(context);
        roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();
        dateStrings.clear();
        selectedDate = SharedPref.getSelectedDateCal(context);
        dateStrings = MyDayPlanEntriesNeeded.datesNeeded;
//        try {
//            JSONArray getMissedDates = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC).getMasterSyncDataJsonArray();
//            for (int i = 0; i < getMissedDates.length(); i++) {
//                JSONObject jsonObject = getMissedDates.getJSONObject(i);
//                if (jsonObject.getString("tbname").equalsIgnoreCase("missed") || jsonObject.getString("tbname").equalsIgnoreCase("dcr")) {
//                    dateStrings.add(jsonObject.getJSONObject("dt").getString("date").substring(0, 10));
//                }
//            }
//        } catch (Exception e) {
//            Log.v("Chkkk", "--error---" + e);
//        }
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_calderdate_layout, parent, false);
        return new CalendarViewHolder(view);
    }

    @SuppressLint({"Range", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        EventCalenderModelClass list = days.get(position);
        holder.dayTextView.setText(list.getDateID());


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyy", Locale.ENGLISH);
        String fullMonthName = selectedMonth.format(formatter);
        String year = selectedMonth.format(formatter1);

        // set Event
        GradientDrawable drawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.event_point_background);
        if (list.getWorkTypeFlag().equalsIgnoreCase("F")) {
            drawable.setColor(context.getResources().getColor(R.color.green_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorkTypeFlag().equalsIgnoreCase("W")) {
            drawable.setColor(context.getResources().getColor(R.color.yellow_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorkTypeFlag().equalsIgnoreCase("H")) {
            drawable.setColor(context.getResources().getColor(R.color.lustylavender_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorkTypeFlag().equalsIgnoreCase("L")) {
            drawable.setColor(context.getResources().getColor(R.color.red_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorkTypeFlag().equalsIgnoreCase("N")) {
            drawable.setColor(context.getResources().getColor(R.color.blue_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorkTypeFlag().equalsIgnoreCase("M")) {
            drawable.setColor(context.getResources().getColor(R.color.Hilo_bay_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorkTypeFlag().equalsIgnoreCase("RE")) {
            drawable.setColor(context.getResources().getColor(R.color.pink_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorkTypeFlag().equalsIgnoreCase("R")) {
            drawable.setColor(context.getResources().getColor(R.color.brown_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.imageView.setImageDrawable(drawable);


        // set SquareBox for background
        if (position == 0) {
            holder.linearLayout.setBackgroundResource(R.drawable.calender_background_a);
        } else if (position <= 6) {
            holder.linearLayout.setBackgroundResource(R.drawable.calender_background_b);

        } else if (position == 7 || position == 14 || position == 21 || position == 28 || position == 35) {
            holder.linearLayout.setBackgroundResource(R.drawable.calender_background_c);
        } else {
            holder.linearLayout.setBackgroundResource(R.drawable.calender_background_d);
        }

        if (selectedDate.equalsIgnoreCase(String.format("%s-%s-%s", list.getDateID(), list.getMonth(), list.getYear()))) {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#282A3C"));
            holder.dayTextView.setTextColor(context.getColor(R.color.white));
            holder.imageView.setVisibility(View.GONE);
        }

        holder.linearLayout.setOnClickListener(v -> {
            if (!list.getDateID().equalsIgnoreCase("")) {

                boolean isApplicableDate = false;
                String monthConverted = "";
                if (!list.getMonth().isEmpty()) {
                    monthConverted = list.getMonth();
                    if (Integer.parseInt(list.getMonth()) < 10) {
                        monthConverted = "0" + monthConverted;
                    }
                }

                String dayConverted = "";
                if (!list.getDateID().isEmpty()) {
                    dayConverted = list.getDateID();
                    if (Integer.parseInt(list.getDateID()) < 10) {
                        dayConverted = "0" + dayConverted;
                    }
                }

                String selectedDate = String.format("%s-%s-%s", list.getYear(), monthConverted, dayConverted);
                if (selectedDate.equalsIgnoreCase(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"))) {
                    isApplicableDate = true;
                }
//                else {
//                    if (HomeDashBoard.SequentialEntry.equalsIgnoreCase("0")) {
//                        if (selectedDate.equalsIgnoreCase(dateStrings.first())) {
//                            isApplicableDate = true;
//                        }
//                    } else {
                        for (String date: dateStrings) {
                            if (selectedDate.equalsIgnoreCase(date)) {
                                isApplicableDate = true;
                                break;
                            }
                        }
//                    }
//                }

                if(SequentialEntry.equalsIgnoreCase("0")) {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.sequential_entry_cannot_change_date));
                } else if(!SharedPref.getDayPlanStartedDate(context).isEmpty() && MyDayPlanEntriesNeeded.datesNeeded.contains(SharedPref.getDayPlanStartedDate(context))) {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.complete_day));
                } else if(!MyDayPlanEntriesNeeded.datesNeeded.isEmpty() && !SharedPref.getSelectedDateCal(context).isEmpty()) {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_pending_dates_to_select));
                } else if (isApplicableDate) {
                    SharedPref.setSelectedDateCal(context, String.format("%s-%s-%s", list.getDateID(), list.getMonth(), list.getYear()));
                    HomeDashBoard.binding.textDate.setText(String.format("%s %s, %s", fullMonthName, list.getDateID(), year));
                    HomeDashBoard.binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                    HomeDashBoard.binding.tabLayout.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewPager.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewDummy.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.imgDoubleVecer.setImageDrawable(context.getDrawable(R.drawable.arrow_bot_top_img));
//                    if (HomeDashBoard.SequentialEntry.equalsIgnoreCase("0")) {
                        HomeDashBoard.checkAndSetEntryDate(context);
//                    }
                } else {
                    Log.e("call status", "onBindViewHolder: " );
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.not_chose_after_date));
                }


              /*  if (new Date().equals(strDate)) {
                    HomeDashBoard.binding.textDate.setText(String.format("%s %s, %s", fullMonthName, list.getDateID(), year));
                    HomeDashBoard.binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                    HomeDashBoard.binding.tabLayout.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewPager.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewDummy.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.imgDoubleVecer.setImageDrawable(context.getDrawable(R.drawable.arrow_bot_top_img));
                } else {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.not_chose_after_date));
                }*/

            /*   if (new Date().after(strDate)) {
                    HomeDashBoard.binding.textDate.setText(String.format("%s %s, %s", fullMonthName, list.getDateID(), year));
                    HomeDashBoard.binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                    HomeDashBoard.binding.tabLayout.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewPager.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewDummy.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.imgDoubleVecer.setImageDrawable(context.getDrawable(R.drawable.arrow_bot_top_img));
                } else {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.not_chose_after_date));
                }*/
            }
           /* if (list.getDateID().equalsIgnoreCase(CommonUtilsMethods.getCurrentInstance("MMMM d, yyyy")) || list.getDateID() > CommonUtilsMethods.getCurrentInstance("MMMM d, yyyy")) {
                if (!list.getDateID().equalsIgnoreCase("")) {
                    HomeDashBoard.binding.textDate.setText(String.format("%s %s, %s", fullMonthName, list.getDateID(), year));
                    HomeDashBoard.binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                    //  HomeDashBoard.binding.tabLayout.getRoot().setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.tabLayout.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewPager.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewDummy.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.imgDoubleVecer.setImageDrawable(context.getDrawable(R.drawable.arrow_bot_top_img));

                }
            } else {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.submit_checkin));
            }*/
        });
    }


    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        ImageView imageView;
        LinearLayout linearLayout;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.cellDayText);
            imageView = itemView.findViewById(R.id.img_event_point);
            linearLayout = itemView.findViewById(R.id.day_bgd);
        }
    }
}