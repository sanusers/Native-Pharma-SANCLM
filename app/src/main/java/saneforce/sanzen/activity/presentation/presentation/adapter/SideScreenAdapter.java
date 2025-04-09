package saneforce.sanzen.activity.presentation.presentation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.customerSelection.model.CustomerDataModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.CustomerDataItemBinding;

public class SideScreenAdapter extends RecyclerView.Adapter<SideScreenAdapter.ViewHolder> {
    private final Context context;
    private final List<CustomerDataModel> customerDataList;
    private CheckBoxSelectListener checkBoxSelectListener;

    public interface CheckBoxSelectListener {
        void onChecked(String customerCode);
        void onUnChecked(String customerCode);
    }

    public SideScreenAdapter(Context context, List<CustomerDataModel> customerDataList) {
        this.context = context;
        this.customerDataList = customerDataList;
    }

    public SideScreenAdapter(Context context, List<CustomerDataModel> customerDataList, CheckBoxSelectListener checkBoxSelectListener) {
        this.context = context;
        this.customerDataList = customerDataList;
        this.checkBoxSelectListener = checkBoxSelectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CustomerDataItemBinding binding = CustomerDataItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerDataModel customerDataModel = customerDataList.get(position);
        holder.binding.tvName.setText(customerDataModel.getName());
        holder.binding.checkBox.setChecked(customerDataModel.isSelected());
        holder.binding.tvCluster.setText(customerDataModel.getClusterName());
        holder.binding.tvName.setOnClickListener(view -> {
            new CommonUtilsMethods(context).displayPopupWindow(context, view, customerDataModel.getName());
        });
        holder.binding.tvCluster.setOnClickListener(view -> {
            new CommonUtilsMethods(context).displayPopupWindow(context, view, customerDataModel.getClusterName());
        });
//        List<String> customerDetailsList = getCustomerDetailsList(customerDataModel);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_textview, customerDetailsList) {
//            @NonNull
//            @Override
//            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView textView = view.findViewById(R.id.tv_single);
//                textView.setOnClickListener(view2 -> {
//                    new CommonUtilsMethods(context).displayPopupWindow(context, textView, customerDetailsList.get(position));
//                });
//                textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//                textView.setMaxLines(1);
//                textView.setEllipsize(TextUtils.TruncateAt.END);
//                return view;
//            }
//        };
//        holder.binding.gvCustomerData.setAdapter(adapter);
//        holder.binding.gvCustomerData.setNumColumns(2);
//        if(customerDetailsList.size() > 1) {
//            setGridViewHeightBasedOnChildren(holder.binding.gvCustomerData);
//        }

//        holder.binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if(isChecked) {
//                checkBoxSelectListener.onChecked(customerDataModel.getCode());
//            } else {
//                checkBoxSelectListener.onUnChecked(customerDataModel.getCode());
//            }
//        });
    }

    @NonNull
    private List<String> getCustomerDetailsList(CustomerDataModel customerDataModel) {
        List<String> customerDetailsList = new ArrayList<>();
//        if(customerDataModel.getCategoryName() != null && !customerDataModel.getCategoryName().isEmpty() && !customerDataModel.getCategoryName().equalsIgnoreCase("null")) {
//            customerDetailsList.add(customerDataModel.getCategoryName());
//        }
//        if(customerDataModel.getSpecialityName() != null && !customerDataModel.getSpecialityName().isEmpty() && !customerDataModel.getSpecialityName().equalsIgnoreCase("null")) {
//            customerDetailsList.add(customerDataModel.getSpecialityName());
//        }
        if(customerDataModel.getClusterName() != null && !customerDataModel.getClusterName().isEmpty() && !customerDataModel.getClusterName().equalsIgnoreCase("null")) {
            customerDetailsList.add(customerDataModel.getClusterName());
        }
//        if(customerDataModel.getClassName() != null && !customerDataModel.getClassName().isEmpty() && !customerDataModel.getClassName().equalsIgnoreCase("null")) {
//            customerDetailsList.add(customerDataModel.getClassName());
//        }
        return customerDetailsList;
    }

    @Override
    public int getItemCount() {
        return customerDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CustomerDataItemBinding binding;

        public ViewHolder(@NonNull CustomerDataItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void setGridViewHeightBasedOnChildren(GridView gridView) {
        try {
            ArrayAdapter listAdapter = (ArrayAdapter) gridView.getAdapter();
            if(listAdapter == null) {
                return;
            }
            View listItem = listAdapter.getView(0, null, gridView);
            listItem.measure(0, 0);
            int rows = 1;
            int items = listAdapter.getCount();
            int totalHeight = listItem.getMeasuredHeight();
            if(items>2) {
                float x = items / 2.0f;
                rows = (int) (Math.ceil(x));
            }
            totalHeight *= rows;
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = totalHeight;
            gridView.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
