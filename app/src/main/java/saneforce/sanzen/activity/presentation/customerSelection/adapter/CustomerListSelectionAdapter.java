package saneforce.sanzen.activity.presentation.customerSelection.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.customerSelection.model.CustomerDataModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;

public class CustomerListSelectionAdapter extends RecyclerView.Adapter<CustomerListSelectionAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CustomerDataModel> customerDataList;
    private CommonUtilsMethods commonUtilsMethods;
    private String customerType = "";
    private CustomerSelectionListener customerSelectionListener;

    public interface CustomerSelectionListener {
        void onSelect(CustomerDataModel customerDataModel);
    }

    public CustomerListSelectionAdapter(Context context, ArrayList<CustomerDataModel> customerDataList, String customerType, CustomerSelectionListener customerSelectionListener) {
        this.context = context;
        this.customerDataList = customerDataList;
        this.customerType = customerType;
        this.customerSelectionListener = customerSelectionListener;
        this.commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_call_cust_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerDataModel customerDataModel = customerDataList.get(position);

        holder.tv_name.setText(customerDataModel.getName());
        holder.tv_category.setText(customerDataModel.getCategoryName());
        holder.tv_specialist.setText(customerDataModel.getSpecialityName());
        holder.tv_class.setText(customerDataModel.getClassName());
        holder.tv_area.setText(customerDataModel.getClusterName());

        if(customerType.equalsIgnoreCase(Constants.DOCTOR) || customerType.equalsIgnoreCase(Constants.UNLISTED_DOCTOR)) {
//        if(customerType.equalsIgnoreCase(Constants.DOCTOR_MAS) || customerType.equalsIgnoreCase(Constants.UNLISTED_DOCTOR_MAS)) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.VISIBLE);
            holder.tv_class.setVisibility(View.VISIBLE);
        }else if(customerType.equalsIgnoreCase(Constants.CHEMIST) || customerType.equalsIgnoreCase(Constants.CIP)) {
//        }else if(customerType.equalsIgnoreCase(Constants.CHEMIST_MAS) || customerType.equalsIgnoreCase(Constants.CIP)) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.GONE);
            holder.tv_class.setVisibility(View.GONE);
        }else {
            holder.tv_category.setVisibility(View.GONE);
            holder.tv_specialist.setVisibility(View.GONE);
            holder.tv_class.setVisibility(View.GONE);
        }
        holder.view_top.setVisibility(View.GONE);

        holder.tv_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(context, view, customerDataModel.getName()));

        holder.constraint_main.setOnClickListener(view -> {
            customerSelectionListener.onSelect(customerDataModel);
            customerDataModel.setSelected(!customerDataModel.isSelected());
            if(customerDataModel.isSelected()) {
                holder.iv_selected_tick.setVisibility(View.VISIBLE);
            }else {
                holder.iv_selected_tick.setVisibility(View.GONE);
            }
        });

        if(customerDataModel.isSelected()) {
            holder.iv_selected_tick.setVisibility(View.VISIBLE);
        }else {
            holder.iv_selected_tick.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return customerDataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CustomerDataModel> customerDataList) {
        this.customerDataList = customerDataList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_category, tv_specialist, tv_area, tv_class;
        ConstraintLayout constraint_main;
        View view_top;
        ImageView iv_selected_tick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.txt_cust_name);
            tv_category = itemView.findViewById(R.id.txt_cat);
            tv_specialist = itemView.findViewById(R.id.txt_specialist);
            tv_area = itemView.findViewById(R.id.txt_address);
            tv_class = itemView.findViewById(R.id.txt_class);
            constraint_main = itemView.findViewById(R.id.constraint_main);
            view_top = itemView.findViewById(R.id.view_top);
            iv_selected_tick = itemView.findViewById(R.id.img_select);
        }
    }

}
