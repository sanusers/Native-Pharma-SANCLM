package saneforce.santrip.activity.homeScreen.call.dcrCallSelection.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.activity.profile.CustomerProfile;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.storage.SQLite;

public class AdapterDCRCallSelection extends RecyclerView.Adapter<AdapterDCRCallSelection.ViewHolder> {
    Context context;
    ArrayList<CustList> cusListArrayList;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    SQLite sqLite;


    public AdapterDCRCallSelection(Activity activity, Context context, ArrayList<CustList> cusListArrayList) {
        this.activity = activity;
        this.context = context;
        this.cusListArrayList = cusListArrayList;
        sqLite = new SQLite(context);
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
            goNextActivity(position);
          /*  try {
                boolean isVisitedToday = false;
                JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(CommonUtilsMethods.getCurrentDate()) && jsonObject.getString("CustCode").equalsIgnoreCase(cusListArrayList.get(position).getCode())) {
                        isVisitedToday = true;
                        break;
                    }
                }

                if (!isVisitedToday) {
                    if (cusListArrayList.get(position).getType().equalsIgnoreCase("1")) {
                        int count = 0;
                        JSONArray jsonVisit = sqLite.getMasterSyncDataByKey(Constants.DCR);
                        for (int i = 0; i < jsonVisit.length(); i++) {
                            JSONObject jsonObject = jsonVisit.getJSONObject(i);
                            if (jsonObject.getString("CustCode").equalsIgnoreCase(cusListArrayList.get(position).getCode())) {
                                count++;
                            }
                        }
                        if (count < Integer.parseInt(cusListArrayList.get(position).getTotalVisitCount())) {
                            goNextActivity(position);
                        } else {
                            Toast.makeText(context, "No of Visit Exceeded", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        goNextActivity(position);
                    }
                } else {
                    Toast.makeText(context, "Already Visited Today", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ignored) {

            }*/

        });
    }

    private void goNextActivity(int position) {
        DCRCallActivity.CallActivityCustDetails = new ArrayList<>();
        DCRCallActivity.CallActivityCustDetails.add(0, new CustList(cusListArrayList.get(position).getName(),
                cusListArrayList.get(position).getCode(), cusListArrayList.get(position).getType(),
                cusListArrayList.get(position).getCategory(), cusListArrayList.get(position).getCategoryCode(),
                cusListArrayList.get(position).getSpecialist(), cusListArrayList.get(position).getSpecialistCode(), cusListArrayList.get(position).getTown_name(),
                cusListArrayList.get(position).getTown_code(), cusListArrayList.get(position).getMaxTag(),
                cusListArrayList.get(position).getTag(), cusListArrayList.get(position).getPosition(),
                cusListArrayList.get(position).getLatitude(), cusListArrayList.get(position).getLongitude(),
                cusListArrayList.get(position).getAddress(), cusListArrayList.get(position).getDob(),
                cusListArrayList.get(position).getWedding_date(), cusListArrayList.get(position).getEmail(),
                cusListArrayList.get(position).getMobile(), cusListArrayList.get(position).getPhone(),
                cusListArrayList.get(position).getQualification(), cusListArrayList.get(position).getPriorityPrdCode(), cusListArrayList.get(position).getMappedBrands(), cusListArrayList.get(position).getMappedSlides()));
        Intent intent = new Intent(context, CustomerProfile.class);
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
