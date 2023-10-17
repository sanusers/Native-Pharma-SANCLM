package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter;

import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.map.custSelection.CustList;
import saneforce.sanclm.activity.profile.CustomerProfile;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterDCRCallSelection extends RecyclerView.Adapter<AdapterDCRCallSelection.ViewHolder> {
    Context context;
    ArrayList<CustList> custListArrayList;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;

    public AdapterDCRCallSelection(Activity activity, Context context, ArrayList<CustList> custListArrayList) {
        this.activity = activity;
        this.context = context;
        this.custListArrayList = custListArrayList;
    }

    @NonNull
    @Override
    public AdapterDCRCallSelection.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_call_cust_list, parent, false);
        return new AdapterDCRCallSelection.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDCRCallSelection.ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(custListArrayList.get(position).getName());
        holder.tv_category.setText(custListArrayList.get(position).getCategory());
        holder.tv_specialist.setText(custListArrayList.get(position).getSpecialist());
        holder.tv_area.setText(custListArrayList.get(position).getTown_name());

        holder.tv_name.setOnClickListener(view -> {
            if (holder.tv_name.getText().toString().length() > 18) {
                commonUtilsMethods.displayPopupWindow(activity, context, view, custListArrayList.get(position).getName());
            }
        });

        for (int i = 0; i < DcrCallTabLayoutActivity.TodayPlanClusterList.size(); i++) {
            if (custListArrayList.get(position).getType().equalsIgnoreCase("3")) {
                if (custListArrayList.get(position).getTown_name().contains(DcrCallTabLayoutActivity.TodayPlanClusterList.get(i))) {
                    holder.view_top.setVisibility(View.VISIBLE);
                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.pink));
                    break;
                } else {
                    holder.view_top.setVisibility(View.GONE);
                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
            } else {
                if (custListArrayList.get(position).getTown_code().contains(DcrCallTabLayoutActivity.TodayPlanClusterList.get(i))) {
                    holder.view_top.setVisibility(View.VISIBLE);
                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.pink));
                    break;
                } else {
                    holder.view_top.setVisibility(View.GONE);
                    holder.tv_area.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
            }
        }

      /*  for (int i = 0; i < TodayPlanClusterList.size(); i++) {
            if (TodayPlanClusterList.get(i).equalsIgnoreCase(custListArrayList.get(position).getTown_code())) {
                Log.v("Tod", TodayPlanClusterList.get(i) + "---" + custListArrayList.get(position).getTown_code());
                holder.view_top.setVisibility(View.VISIBLE);
                holder.tv_area.setTextColor(context.getResources().getColor(R.color.pink));
            } else {
                holder.view_top.setVisibility(View.GONE);
                holder.tv_area.setTextColor(context.getResources().getColor(R.color.dark_purple));
            }
        }*/

       /* if (custListArrayList.get(position).getTown_code().equalsIgnoreCase("Trichy")) {
            holder.view_top.setVisibility(View.VISIBLE);
            holder.tv_area.setTextColor(context.getResources().getColor(R.color.pink));
        }*/


        holder.constraint_main.setOnClickListener(view -> {
            Intent intent = new Intent(context, CustomerProfile.class);
            intent.putExtra("cust_name", custListArrayList.get(position).getName());
            intent.putExtra("cust_code", custListArrayList.get(position).getCode());
            intent.putExtra("cust_type", custListArrayList.get(position).getType());
            intent.putExtra("town_code", custListArrayList.get(position).getTown_code());
            intent.putExtra("town_name", custListArrayList.get(position).getTown_name());
            intent.putExtra("sf_code", TodayPlanSfCode);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return custListArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CustList> filterdNames) {
        this.custListArrayList = filterdNames;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
