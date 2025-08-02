package saneforce.sanzen.activity.call.dcrCallSelection.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.activity.call.profile.CustomerProfile;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class AdapterDCRCallSelection extends RecyclerView.Adapter<AdapterDCRCallSelection.ViewHolder> {
    private final Context context;
    private ArrayList<CustList> cusListArrayList;
    private ArrayList<CustList> FillteredList;
    private CommonUtilsMethods commonUtilsMethods;
    private Activity activity;
    private Dialog dialogCheckIn;
    private Button btnCheckIN;
    private ImageView img_Close, imgCustomer;
    private TextView tv_cusName, tv_dateTime, tvLatLong, tvAddress, tvTitle;
    private String needCheckInOut;
    private RoomDB roomDB;
    private GPSTrack gpsTrack;
    private final MasterDataDao masterDataDao;
    private final String isFrom;
    private double latitude, longitude;
    private String address;
    private Handler handler;
    private Runnable runnable;

    public AdapterDCRCallSelection(Activity activity, Context context, ArrayList<CustList> cusListArrayList, String needCheckInOut, String isFrom) {
        this.activity = activity;
        this.context = context;
        this.cusListArrayList = cusListArrayList;
        this.needCheckInOut = needCheckInOut;
        this.isFrom = isFrom;
        this.FillteredList=cusListArrayList;
        roomDB=RoomDB.getDatabase(context);
        masterDataDao=roomDB.masterDataDao();
        gpsTrack = new GPSTrack(activity);

        if (needCheckInOut.equalsIgnoreCase("0")) {
            dialogCheckIn = new Dialog(context);
            dialogCheckIn.setContentView(R.layout.dialog_cus_checkin);
            dialogCheckIn.setCancelable(false);
            if(dialogCheckIn.getWindow() != null) {
                dialogCheckIn.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            btnCheckIN = dialogCheckIn.findViewById(R.id.btn_checkIn);
            img_Close = dialogCheckIn.findViewById(R.id.img_close);
            imgCustomer = dialogCheckIn.findViewById(R.id.img_cust);
            tvTitle = dialogCheckIn.findViewById(R.id.txt_heading);
            tv_cusName = dialogCheckIn.findViewById(R.id.txt_cus_name);
            tv_dateTime = dialogCheckIn.findViewById(R.id.txt_date_time);
            tvLatLong = dialogCheckIn.findViewById(R.id.txt_lat_lng);
            tvAddress = dialogCheckIn.findViewById(R.id.txt_address);
        }

    }

    @NonNull
    @Override
    public AdapterDCRCallSelection.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_call_cust_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDCRCallSelection.ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(cusListArrayList.get(position).getName());
        if(cusListArrayList.get(position).getCategory().isEmpty())
            holder.tv_category.setText("");
        else
            holder.tv_category.setText(cusListArrayList.get(position).getCategory());
        holder.tv_specialist.setText(cusListArrayList.get(position).getSpecialist());
        holder.tv_area.setText(cusListArrayList.get(position).getTown_name());

        if (isFrom.equalsIgnoreCase("1") || isFrom.equalsIgnoreCase("4")) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.VISIBLE);

        } else if (isFrom.equalsIgnoreCase("2") || isFrom.equalsIgnoreCase("5")) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.GONE);

        }else {
            holder.tv_category.setVisibility(View.GONE);
            holder.tv_specialist.setVisibility(View.GONE);
        }

        holder.tv_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(context, view, cusListArrayList.get(position).getName()));

        for (int i = 0; i < DcrCallTabLayoutActivity.TodayPlanClusterList.size(); i++) {
            if (cusListArrayList.get(position). getType().equalsIgnoreCase("3")) {
                if (cusListArrayList.get(position).getTown_name().contains(DcrCallTabLayoutActivity.TodayPlanClusterList.get(i))) {
                    holder.view_top.setVisibility(View.VISIBLE);
                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.pink));
                    break;
                } else {
                    holder.view_top.setVisibility(View.GONE);
                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
            } else {
                if (cusListArrayList.get(position).getTown_code().contains(DcrCallTabLayoutActivity.TodayPlanClusterList.get(i))) {
                    holder.view_top.setVisibility(View.VISIBLE);
                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.pink));
                    break;
                } else {
                    holder.view_top.setVisibility(View.GONE);
                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
            }
        }

        holder.constraint_main.setOnClickListener(view -> {
            try {
                boolean isVisitedToday = false;
                JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(HomeDashBoard.selectedDate.toString()) && jsonObject.getString("CustCode").equalsIgnoreCase(cusListArrayList.get(position).getCode())) {
                        isVisitedToday = true;
                        break;
                    }
                }

                if (!isVisitedToday) {
                    if (SharedPref.getVstNd(context).equalsIgnoreCase("0") && SharedPref.getSfType(context).equalsIgnoreCase("1") && cusListArrayList.get(position).getType().equalsIgnoreCase("1")) {
                        int count = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString("CustCode").equalsIgnoreCase(cusListArrayList.get(position).getCode())
                                    && TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, jsonObject.getString("Dcr_dt"))
                                    .equals(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, HomeDashBoard.selectedDate.toString()))) {
                                count++;
                            }
                        }
                        if (count < Integer.parseInt(cusListArrayList.get(position).getTotalVisitCount())) {
                            goNextActivity(position);
                        } else {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_of_visit));
                        }
                    } else {
                        goNextActivity(position);
                    }
                } else {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.already_visited));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("Call_Data1", "---" + e);
            }
        });
    }

    private void goNextActivity(int position) {
        try {
            gpsTrack = new GPSTrack(activity);
            latitude = gpsTrack.getLatitude();
            longitude = gpsTrack.getLongitude();
            if(UtilityClass.isNetworkAvailable(activity)) {
                address = CommonUtilsMethods.gettingAddress(activity, latitude, longitude, false);
            }else {
                address = activity.getString(R.string.no_address_found);
            }
        } catch (Exception e) {
            address = activity.getString(R.string.no_address_found);
            e.printStackTrace();
        }

        if (needCheckInOut.equalsIgnoreCase("0") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
            tv_cusName.setText(cusListArrayList.get(position).getName());
            startClock();
//            tv_dateTime.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));
            tvLatLong.setText(String.format(Locale.getDefault(), "%f , %f", latitude, longitude));
            tvAddress.setText(address);
            String customerCaption = SharedPref.getDrCap(context);
            switch (cusListArrayList.get(position).getType()) {
                case "1":
                    imgCustomer.setImageResource(R.drawable.doctor_img);
                    customerCaption = SharedPref.getDrCap(context);
                    break;
                case "2":
                    imgCustomer.setImageResource(R.drawable.chemist_img);
                    customerCaption = SharedPref.getChmCap(context);
                    break;
                case "3":
                    imgCustomer.setImageResource(R.drawable.tp_stockiest_icon);
                    customerCaption = SharedPref.getStkCap(context);
                    break;
                case "4":
                    imgCustomer.setImageResource(R.drawable.tp_unlist_dr_icon);
                    customerCaption = SharedPref.getUNLcap(context);
                    break;
                case "5":
                    imgCustomer.setImageResource(R.drawable.cip_img);
                    customerCaption = SharedPref.getCipCaption(context);
                    break;
                case "6":
                    imgCustomer.setImageResource(R.drawable.tp_hospital_icon);
                    customerCaption = SharedPref.getHospCaption(context);
                    break;
                default:
                    imgCustomer.setImageResource(R.drawable.doctor_img);
                    break;
            }
            tvTitle.setText(String.format("%s %s", customerCaption, context.getString(R.string.check_in)));

            dialogCheckIn.show();
            img_Close.setOnClickListener(v -> dialogCheckIn.dismiss());

            btnCheckIN.setOnClickListener(v -> {
                commonUtilsMethods.showToastMessage(context, "Check In Successfully");
                changeActivity(position);
            });
        } else {
            changeActivity(position);
        }
    }

    private void startClock() {
        handler = new Handler();
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.getDefault());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (tv_dateTime != null && dialogCheckIn != null && dialogCheckIn.isShowing()) {
                    Calendar calendar = Calendar.getInstance();
                    String currentTime = timeFormat.format(calendar.getTime());
                    tv_dateTime.setText(currentTime);
                    handler.postDelayed(this, 1000);
                } else {
                    stopClock();
                }
            }
        };
        handler.post(runnable);
    }

    private void stopClock() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
    }

    private JSONObject prepareCheckInJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("InDateTime", CommonUtilsMethods.getCurrentInstance(TimeUtils.FORMAT_1));
            jsonObject.put("InLatitude", String.valueOf(latitude));
            jsonObject.put("InLongitude", String.valueOf(longitude));
            jsonObject.put("InAddress", String.valueOf(address));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void changeActivity(int position) {
        JSONObject jsonObject = prepareCheckInJsonObject();
        DCRCallActivity.CallActivityCustDetails = new ArrayList<>();
        DCRCallActivity.CallActivityCustDetails.add(0, new CustList(cusListArrayList.get(position).getName(), cusListArrayList.get(position).getCode(), cusListArrayList.get(position).getType(), cusListArrayList.get(position).getCategory(), cusListArrayList.get(position).getCategoryCode(), cusListArrayList.get(position).getSpecialist(), cusListArrayList.get(position).getSpecialistCode(), cusListArrayList.get(position).getTown_name(), cusListArrayList.get(position).getTown_code(), cusListArrayList.get(position).getMaxTag(), cusListArrayList.get(position).getTag(), cusListArrayList.get(position).getPosition(), cusListArrayList.get(position).getLatitude(), cusListArrayList.get(position).getLongitude(), cusListArrayList.get(position).getAddress(), cusListArrayList.get(position).getDob(), cusListArrayList.get(position).getWedding_date(), cusListArrayList.get(position).getEmail(), cusListArrayList.get(position).getMobile(), cusListArrayList.get(position).getPhone(), cusListArrayList.get(position).getQualification(), cusListArrayList.get(position).getPriorityPrdCode(), cusListArrayList.get(position).getMappedBrands(), cusListArrayList.get(position).getMappedSlides()));
        Intent intent = new Intent(context, CustomerProfile.class);
        if(needCheckInOut.equalsIgnoreCase("0") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
            intent.putExtra("CheckInJsonObject", jsonObject.toString());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return cusListArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CustList> filteredNames) {
        this.cusListArrayList = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_category, tv_specialist, tv_area;
        ConstraintLayout constraint_main;
        View view_top;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.txt_cust_name);
            tv_category = itemView.findViewById(R.id.txt_cat);
            tv_specialist = itemView.findViewById(R.id.txt_specialist);
            tv_area = itemView.findViewById(R.id.txt_address);
            constraint_main = itemView.findViewById(R.id.constraint_main);
            view_top = itemView.findViewById(R.id.view_top);
        }
    }


}