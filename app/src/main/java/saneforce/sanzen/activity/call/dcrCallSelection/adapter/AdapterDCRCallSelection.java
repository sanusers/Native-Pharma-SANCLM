package saneforce.sanzen.activity.call.dcrCallSelection.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.profile.CustomerProfile;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.commonClasses.SafeClickListener;
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
    private int limit = 1;
    private ProgressBar progressBar;
    private RelativeLayout refreshLocation;
    private final boolean isFencing;
    private final int colorPink;
    private final int colorDarkPurple;
    private HashSet<String> clusterCodeSet = new HashSet<>();

    public AdapterDCRCallSelection(Activity activity, Context context, ArrayList<CustList> cusListArrayList, String needCheckInOut, boolean isFencing, String isFrom) {
        this.activity = activity;
        this.context = context;
        this.cusListArrayList = cusListArrayList;
        this.needCheckInOut = needCheckInOut;
        this.isFencing = isFencing;
        this.isFrom = isFrom;
        this.FillteredList = cusListArrayList;
        roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();
        gpsTrack = new GPSTrack(activity);
        commonUtilsMethods = new CommonUtilsMethods(context);
        colorPink = ContextCompat.getColor(context, R.color.pink);
        colorDarkPurple = ContextCompat.getColor(context, R.color.dark_purple);
        buildClusterSet();

        if (needCheckInOut.equalsIgnoreCase("0")) {
            dialogCheckIn = new Dialog(context);
            dialogCheckIn.setContentView(R.layout.dialog_cus_checkin);
            dialogCheckIn.setCancelable(false);
            if (dialogCheckIn.getWindow() != null) {
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
            progressBar = dialogCheckIn.findViewById(R.id.progress_bar);
            refreshLocation = dialogCheckIn.findViewById(R.id.rl_refresh_location);
            progressBar.setVisibility(View.GONE);
        }

    }

    private void buildClusterSet() {
        clusterCodeSet.clear();
        List<String> clusterList = DcrCallTabLayoutActivity.TodayPlanClusterList;
        for (int i = 0; i < clusterList.size(); i += 2) {
            clusterCodeSet.add(clusterList.get(i));
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
        CustList custList = cusListArrayList.get(position);
        holder.tv_name.setText(custList.getName());
        if (custList.getCategory().isEmpty())
            holder.tv_category.setText("");
        else
            holder.tv_category.setText(custList.getCategory());
        holder.tv_specialist.setText(custList.getSpecialist());
        holder.tv_area.setText(custList.getTown_name());

        if (isFrom.equalsIgnoreCase("1") || isFrom.equalsIgnoreCase("4")) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.VISIBLE);

        } else if (isFrom.equalsIgnoreCase("2") || isFrom.equalsIgnoreCase("5")) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.GONE);

        } else {
            holder.tv_category.setVisibility(View.GONE);
            holder.tv_specialist.setVisibility(View.GONE);
        }

        holder.tv_name.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                commonUtilsMethods.displayPopupWindow(context, view, custList.getName());
            }
        });

//        for (int i = 0; i < DcrCallTabLayoutActivity.TodayPlanClusterList.size(); i++) {
//            if (custList.getType().equalsIgnoreCase("3")) {
//                if (custList.getTown_name().contains(DcrCallTabLayoutActivity.TodayPlanClusterList.get(i))) {
//                    holder.view_top.setVisibility(View.VISIBLE);
//                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.pink));
//                    break;
//                } else {
//                    holder.view_top.setVisibility(View.GONE);
//                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.dark_purple));
//                }
//            } else {
//                if (custList.getTown_code().contains(DcrCallTabLayoutActivity.TodayPlanClusterList.get(i))) {
//                    holder.view_top.setVisibility(View.VISIBLE);
//                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.pink));
//                    break;
//                } else {
//                    holder.view_top.setVisibility(View.GONE);
//                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.dark_purple));
//                }
//            }
//        }
        boolean inCluster;
        if (custList.getType().equalsIgnoreCase("3")) {
            inCluster = clusterCodeSet.contains(custList.getTown_name());
        } else {
            inCluster = clusterCodeSet.contains(custList.getTown_code());
        }
        holder.view_top.setVisibility(inCluster ? View.VISIBLE : View.GONE);
        holder.tv_area.setTextColor(inCluster ? colorPink : colorDarkPurple);

        holder.constraint_main.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                try {
                    boolean isVisitedToday = false;
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(HomeDashBoard.selectedDate.toString())
                                && jsonObject.getString("CustCode").equalsIgnoreCase(custList.getCode())
                                && jsonObject.getString("CustType").equalsIgnoreCase(custList.getType())) {
                            isVisitedToday = true;
                            break;
                        }
                    }

                    if (!isVisitedToday) {
                        if (SharedPref.getVstNd(context).equalsIgnoreCase("0") && SharedPref.getSfType(context).equalsIgnoreCase("1") && custList.getType().equalsIgnoreCase("1")) {
                            int count = 0;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("CustCode").equalsIgnoreCase(custList.getCode())
                                        && jsonObject.getString("CustType").equalsIgnoreCase(custList.getType())
                                        && TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, jsonObject.getString("Dcr_dt")).equals(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_8, HomeDashBoard.selectedDate.toString()))) {
                                    count++;
                                }
                            }
                            if (count < Integer.parseInt(custList.getTotalVisitCount())) {
                                goNextActivity(custList);
                            } else {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_of_visit), true);
                            }
                        } else {
                            goNextActivity(custList);
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.already_visited), true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("Call_Data1", "---" + e);
                }
            }
        });

        if (isFencing) {
            holder.info.setVisibility(View.VISIBLE);
            holder.info.setOnClickListener(view -> {
                String address = custList.getAddress();
                if (isFrom.equalsIgnoreCase("1")) {
                    address = custList.getGeoAddress();
                }
                showTimelinePopUp(view, custList.getLatitude(), custList.getLongitude(), address);
            });
        } else {
            holder.info.setVisibility(View.GONE);
        }
    }

    private void showTimelinePopUp(View view, String latitude, String longitude, String address) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.distance_popup, null);
        int popupWidth = (int) context.getResources().getDimension(R.dimen._110sdp);
        PopupWindow popupWindow = new PopupWindow(popupView, popupWidth, WindowManager.LayoutParams.WRAP_CONTENT, false);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.setOutsideTouchable(true);
        TextView tvAddress = popupView.findViewById(R.id.address);
        TextView tvMeters = popupView.findViewById(R.id.meters);
        ImageView close = popupView.findViewById(R.id.img_close);
        close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                popupWindow.dismiss();
            }
        });

        tvAddress.setText(String.format(address));
        //tvAddress.setText(context.getString(R.string.address_format, address));
        String meters = calculateDistance(Double.parseDouble(latitude), Double.parseDouble(longitude));
        if (!meters.isEmpty()) {
            tvMeters.setVisibility(View.VISIBLE);
            tvMeters.setText(meters);
        } else {
            tvMeters.setVisibility(View.GONE);
        }

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = popupView.getMeasuredHeight();

        int x = location[0] + (view.getWidth() / 2) - (popupWidth) + 10;
        int y = location[1] - popupHeight - 10;

        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
    }

    private String calculateDistance(double latitude, double longitude) {
        try {
            float[] distance = new float[2];
            Location.distanceBetween(latitude, longitude, DcrCallTabLayoutActivity.lat, DcrCallTabLayoutActivity.lng, distance);
            // return String.format(Locale.getDefault(), "Distance : %.2f meters", distance[0]);
            return context.getString(R.string.distance_format, distance[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void goNextActivity(CustList custList) {
        try {
            gpsTrack = new GPSTrack(activity);
            latitude = gpsTrack.getLatitude();
            longitude = gpsTrack.getLongitude();
            if (UtilityClass.isNetworkAvailable(activity)) {
                address = CommonUtilsMethods.gettingAddress(activity, latitude, longitude, false);
            } else {
                address = activity.getString(R.string.no_address_found);
            }
        } catch (Exception e) {
            address = activity.getString(R.string.no_address_found);
            e.printStackTrace();
        }

        if (needCheckInOut.equalsIgnoreCase("0") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
            tv_cusName.setText(custList.getName());
            startClock();
//            tv_dateTime.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));
            tvLatLong.setText(String.format(Locale.getDefault(), "%f , %f", latitude, longitude));
            tvAddress.setText(address);
            String customerCaption = SharedPref.getDrCap(context);
            switch (custList.getType()) {
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
            refreshLocation.setOnClickListener(view -> {
                try {
                    btnCheckIN.setEnabled(false);
                    stopClock();
                    startClock();
                    progressBar.setVisibility(View.VISIBLE);
                    gpsTrack = new GPSTrack(activity);
                    gpsTrack.setLocationChangeListener(location -> {
                        try {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            if (UtilityClass.isNetworkAvailable(context)) {
                                address = CommonUtilsMethods.gettingAddress(activity, latitude, longitude, false);
                            } else {
                                address = activity.getString(R.string.no_address_found);
                            }

                            tvLatLong.setText(String.format(Locale.getDefault(), "%f , %f", latitude, longitude));
                            tvAddress.setText(address);
                            progressBar.setVisibility(View.GONE);
                            btnCheckIN.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            dialogCheckIn.show();
            img_Close.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    stopClock();
                    dialogCheckIn.dismiss();
                }
            });

            btnCheckIN.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    stopClock();
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.check_in_successfully), true);
                    dialogCheckIn.dismiss();
                    changeActivity(custList);
                }
            });
        } else {
            changeActivity(custList);
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
                    limit++;
                    if (limit == 120) {
                        stopClock();
                        handleIdleTime();
                        dialogCheckIn.dismiss();
                    }
                } else {
                    stopClock();
                }
            }
        };
        handler.post(runnable);
    }

    private void stopClock() {
        if (gpsTrack != null) {
            gpsTrack.setLocationChangeListener(null);
        }
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
    }

    private void handleIdleTime() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
        TextView content = dialog.findViewById(R.id.ed_alert_msg);
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        btn_no.setVisibility(View.GONE);
        btn_yes.setText(content.getResources().getString(R.string.ok));
        content.setText(content.getResources().getString(R.string.you_have_been_idle_for_2_minutes_kindly_re_check_out));

        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });

        btn_no.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });
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

    private void changeActivity(CustList custList) {
        JSONObject jsonObject = prepareCheckInJsonObject();
        DCRCallActivity.CallActivityCustDetails = new ArrayList<>();
        CustList custList1 = new CustList(custList.getName(), custList.getCode(), custList.getType(), custList.getCategory(), custList.getCategoryCode(), custList.getSpecialist(), custList.getSpecialistCode(), custList.getTown_name(), custList.getTown_code(), custList.getMaxTag(), custList.getTag(), custList.getPosition(), custList.getLatitude(), custList.getLongitude(), custList.getAddress(), custList.getDob(), custList.getWedding_date(), custList.getEmail(), custList.getMobile(), custList.getPhone(), custList.getQualification(), custList.getPriorityPrdCode(), custList.getMappedBrands(), custList.getMappedSlides());
        custList1.setCountryCode(custList.getCountryCode());
        DCRCallActivity.CallActivityCustDetails.add(0, custList1);
        Intent intent = new Intent(context, CustomerProfile.class);
        if (needCheckInOut.equalsIgnoreCase("0") && HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
            intent.putExtra("CheckInJsonObject", jsonObject.toString());
        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        ImageView info, seenDr;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.txt_cust_name);
            tv_category = itemView.findViewById(R.id.txt_cat);
            tv_specialist = itemView.findViewById(R.id.txt_specialist);
            tv_area = itemView.findViewById(R.id.txt_address);
            constraint_main = itemView.findViewById(R.id.constraint_main);
            view_top = itemView.findViewById(R.id.view_top);
            info = itemView.findViewById(R.id.tv_duration_info);
            seenDr = itemView.findViewById(R.id.seenDr);
        }
    }

}