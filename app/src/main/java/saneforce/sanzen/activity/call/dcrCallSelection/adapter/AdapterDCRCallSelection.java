package saneforce.sanzen.activity.call.dcrCallSelection.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.activity.call.profile.CustomerProfile;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class AdapterDCRCallSelection extends RecyclerView.Adapter<AdapterDCRCallSelection.ViewHolder> {
    Context context;
    ArrayList<CustList> cusListArrayList;

    ArrayList<CustList> FillteredList;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    Dialog dialogCheckIn;
    Button btnCheckIN;
    ImageView img_Close;
    TextView tv_cusName, tv_dateTime;
    String needCheckInOut;
    RoomDB roomDB;

    MasterDataDao masterDataDao;



    String isFrom;
    public AdapterDCRCallSelection(Activity activity, Context context, ArrayList<CustList> cusListArrayList, String needCheckInOut, String isFrom) {
        this.activity = activity;
        this.context = context;
        this.cusListArrayList = cusListArrayList;
        this.needCheckInOut = needCheckInOut;
        this.isFrom = isFrom;
        this.FillteredList=cusListArrayList;
        roomDB=RoomDB.getDatabase(context);
        masterDataDao=roomDB.masterDataDao();

        if (needCheckInOut.equalsIgnoreCase("0")) {
            dialogCheckIn = new Dialog(context);
            dialogCheckIn.setContentView(R.layout.dialog_cus_checkin);
            dialogCheckIn.setCancelable(false);
            Objects.requireNonNull(dialogCheckIn.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btnCheckIN = dialogCheckIn.findViewById(R.id.btn_checkIn);
            img_Close = dialogCheckIn.findViewById(R.id.img_close);
            tv_cusName = dialogCheckIn.findViewById(R.id.txt_cus_name);
            tv_dateTime = dialogCheckIn.findViewById(R.id.txt_date_time);
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
        holder.tv_category.setText(cusListArrayList.get(position).getCategory());
        holder.tv_specialist.setText(cusListArrayList.get(position).getSpecialist());
        holder.tv_area.setText(cusListArrayList.get(position).getTown_name());


        if (isFrom.equalsIgnoreCase("1") || isFrom.equalsIgnoreCase("4")) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.VISIBLE);

        } else if (isFrom.equalsIgnoreCase("5")) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.GONE);

        }else {
            holder.tv_category.setVisibility(View.GONE);
            holder.tv_specialist.setVisibility(View.GONE);

        }



        holder.tv_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, cusListArrayList.get(position).getName()));

        for (int i = 0; i < DcrCallTabLayoutActivity.TodayPlanClusterList.size(); i++) {
            if (cusListArrayList.get(position).getType().equalsIgnoreCase("3")) {
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
                JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_27, TimeUtils.FORMAT_4, HomeDashBoard.binding.textDate.getText().toString())) && jsonObject.getString("CustCode").equalsIgnoreCase(cusListArrayList.get(position).getCode())) {
                        isVisitedToday = true;
                        break;
                    }
                }

                if (!isVisitedToday) {
                    if (SharedPref.getVstNd(context).equalsIgnoreCase("0") && SharedPref.getSfType(context).equalsIgnoreCase("1") && cusListArrayList.get(position).getType().equalsIgnoreCase("1")) {
                        int count = 0;
                        JSONArray jsonVisit = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                        for (int i = 0; i < jsonVisit.length(); i++) {
                            JSONObject jsonObject = jsonVisit.getJSONObject(i);
                            if (jsonObject.getString("CustCode").equalsIgnoreCase(cusListArrayList.get(position).getCode())) {
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
                Log.v("Call_Data", "---" + e);
            }
        });
    }

    private void goNextActivity(int position) {
        if (needCheckInOut.equalsIgnoreCase("0")) {
            tv_cusName.setText(cusListArrayList.get(position).getName());
            tv_dateTime.setText(CommonUtilsMethods.getCurrentInstance("dd MMM yyyy, hh:mm aa"));

            dialogCheckIn.show();
            img_Close.setOnClickListener(v -> dialogCheckIn.dismiss());

            btnCheckIN.setOnClickListener(v -> {
                DCRCallActivity.CallActivityCustDetails = new ArrayList<>();
                DCRCallActivity.CallActivityCustDetails.add(0, new CustList(cusListArrayList.get(position).getName(), cusListArrayList.get(position).getCode(), cusListArrayList.get(position).getType(), cusListArrayList.get(position).getCategory(), cusListArrayList.get(position).getCategoryCode(), cusListArrayList.get(position).getSpecialist(), cusListArrayList.get(position).getSpecialistCode(), cusListArrayList.get(position).getTown_name(), cusListArrayList.get(position).getTown_code(), cusListArrayList.get(position).getMaxTag(), cusListArrayList.get(position).getTag(), cusListArrayList.get(position).getPosition(), cusListArrayList.get(position).getLatitude(), cusListArrayList.get(position).getLongitude(), cusListArrayList.get(position).getAddress(), cusListArrayList.get(position).getDob(), cusListArrayList.get(position).getWedding_date(), cusListArrayList.get(position).getEmail(), cusListArrayList.get(position).getMobile(), cusListArrayList.get(position).getPhone(), cusListArrayList.get(position).getQualification(), cusListArrayList.get(position).getPriorityPrdCode(), cusListArrayList.get(position).getMappedBrands(), cusListArrayList.get(position).getMappedSlides()));
                Intent intent = new Intent(context, CustomerProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            });
        } else {
            DCRCallActivity.CallActivityCustDetails = new ArrayList<>();
            DCRCallActivity.CallActivityCustDetails.add(0, new CustList(cusListArrayList.get(position).getName(), cusListArrayList.get(position).getCode(), cusListArrayList.get(position).getType(), cusListArrayList.get(position).getCategory(), cusListArrayList.get(position).getCategoryCode(), cusListArrayList.get(position).getSpecialist(), cusListArrayList.get(position).getSpecialistCode(), cusListArrayList.get(position).getTown_name(), cusListArrayList.get(position).getTown_code(), cusListArrayList.get(position).getMaxTag(), cusListArrayList.get(position).getTag(), cusListArrayList.get(position).getPosition(), cusListArrayList.get(position).getLatitude(), cusListArrayList.get(position).getLongitude(), cusListArrayList.get(position).getAddress(), cusListArrayList.get(position).getDob(), cusListArrayList.get(position).getWedding_date(), cusListArrayList.get(position).getEmail(), cusListArrayList.get(position).getMobile(), cusListArrayList.get(position).getPhone(), cusListArrayList.get(position).getQualification(), cusListArrayList.get(position).getPriorityPrdCode(), cusListArrayList.get(position).getMappedBrands(), cusListArrayList.get(position).getMappedSlides()));
            Intent intent = new Intent(context, CustomerProfile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
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