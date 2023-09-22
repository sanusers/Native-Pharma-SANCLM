package saneforce.sanclm.activity.Map.CustSelection;

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

import saneforce.sanclm.commonclasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.Map.MapsActivity;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;

public class CustListAdapter extends RecyclerView.Adapter<CustListAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CustList> custListArrayList;
    CommonUtilsMethods commonUtilsMethods;

    public CustListAdapter(Activity activity, Context context, ArrayList<CustList> custListArrayList) {
       this.activity = activity;
        this.context = context;
        this.custListArrayList = custListArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tag_cust, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(custListArrayList.get(position).getName());
        holder.tv_category.setText(custListArrayList.get(position).getCategory());
        holder.tv_specialist.setText(custListArrayList.get(position).getSpecialist());
        holder.tv_area.setText(custListArrayList.get(position).getArea());
        holder.tv_count.setText(custListArrayList.get(position).getTagCount());


        holder.tv_name.setOnClickListener(view -> {
            if (holder.tv_name.getText().toString().length() > 18) {
                commonUtilsMethods.displayPopupWindow(activity, context, view, custListArrayList.get(position).getName());
            }
        });

        holder.constraint_main.setOnClickListener(view -> {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("from", "tag_adapter");
            intent.putExtra("cust_name", holder.tv_name.getText().toString());
            context.startActivity(intent);
        });

        holder.tv_view.setOnClickListener(view -> {
            Intent intent = new Intent(context, DCRCallActivity.class);
            intent.putExtra("cust_name", holder.tv_name.getText().toString());
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
        TextView tv_name, tv_category, tv_specialist, tv_area, tv_count, tv_view;
        ConstraintLayout constraint_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.txt_cust_name);
            tv_category = itemView.findViewById(R.id.txt_cat);
            tv_specialist = itemView.findViewById(R.id.txt_specialist);
            tv_area = itemView.findViewById(R.id.txt_address);
            tv_count = itemView.findViewById(R.id.txt_count);
            tv_view = itemView.findViewById(R.id.txt_view);
            constraint_main = itemView.findViewById(R.id.constraint_main);
        }
    }
}
