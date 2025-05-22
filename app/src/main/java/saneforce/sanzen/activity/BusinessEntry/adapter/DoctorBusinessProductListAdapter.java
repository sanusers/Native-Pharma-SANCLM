package saneforce.sanzen.activity.BusinessEntry.adapter;

import static saneforce.sanzen.activity.BusinessEntry.DocBusinessProductList.docbusinessProductListBinding;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.BusinessEntry.Interface.UpdateUi;
import saneforce.sanzen.activity.BusinessEntry.ModelClass.AddDoctorEntryProducts;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class DoctorBusinessProductListAdapter extends RecyclerView.Adapter<DoctorBusinessProductListAdapter.ViewHolder> {
    public static int pos;
    ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    Context context;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;

    public static int getPosition() {
        return pos;
    }
    public DoctorBusinessProductListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        this.activity = activity;
        this.context = context;
        this.callCommonCheckedListArrayList = callCommonCheckedListArrayList;
        commonUtilsMethods=new CommonUtilsMethods(context);
    }
    @NonNull
    @Override
    public DoctorBusinessProductListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_doctorbusiness_product_list, parent, false);
        return new DoctorBusinessProductListAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull DoctorBusinessProductListAdapter.ViewHolder holder, int position) {
        CallCommonCheckedList item = callCommonCheckedListArrayList.get(position); // Use filtered list
        holder.tv_name.setText(item.getName());
        holder.tv_pack.setText(item.getPack());
        if (item.isSelected()) {
            holder.rate.setText(String.valueOf(item.getSelectedMRP()));
            holder.BusinessQty.setText(String.valueOf(item.getQty()));
            holder.Businessvalue.setText(String.format(Locale.getDefault(), "%.2f", item.getSelectedValue()));
        } else {
            holder.rate.setText(item.getRate());
            holder.BusinessQty.setText(String.valueOf(item.getQty()));
            holder.Businessvalue.setText("0.00");
        }
        // Remove existing watcher if present
        if (holder.qtyTextWatcher != null) {
            holder.BusinessQty.removeTextChangedListener(holder.qtyTextWatcher);
        }

        holder.BusinessQty.setText(String.valueOf(item.getQty())); // Update current qt
        // Create and assign new watcher
        holder.qtyTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String val = editable.toString();
                int qtyVal = 0;
                try {
                    if (!TextUtils.isEmpty(val)) {
                        qtyVal = Integer.parseInt(val);
                    }
                    item.setQty(qtyVal);
                    float rate;
                    try {
                        rate = Float.parseFloat(holder.rate.getText().toString());
                    } catch (NumberFormatException e) {
                        rate = 0;
                    }
                    float cal = qtyVal * rate;
                    item.setSelectedValue(cal);
                    holder.Businessvalue.setText(String.format(Locale.getDefault(), "%.2f", cal));
                    countvalues(); // Update activity
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        };
        holder.BusinessQty.addTextChangedListener(holder.qtyTextWatcher);
//// Attach new watcher
//        holder.BusinessQty.addTextChangedListener(holder.qtyTextWatcher);
//        // Avoid multiple triggers
//        holder.BusinessQty.addTextChangedListener(new TextWatcher() {
//            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
//            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String val = editable.toString();
//                int qtyVal = 0;
//                try {
//                    if (!TextUtils.isEmpty(val)) {
//                        qtyVal = Integer.parseInt(val);
//                    }
//                    item.setQty(qtyVal);
//                    float rate;
//                    try {
//                        rate = Float.parseFloat(holder.rate.getText().toString());
//                    } catch (NumberFormatException e) {
//                        rate = 0;
//                    }
//                    float cal = qtyVal * rate;
//                    item.setSelectedValue(cal); // Store calculated value
//                    holder.Businessvalue.setText(String.format(Locale.getDefault(), "%.2f", cal));
//                    countvalues();
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        holder.tv_name.setOnClickListener(view -> {
            commonUtilsMethods.displayPopupWindow(context, view, item.getName());
        });
    }
    @Override
    public int getItemCount() {
        return callCommonCheckedListArrayList.size();
    }
    public void filterList(ArrayList<CallCommonCheckedList> filteredNames) {
        this.callCommonCheckedListArrayList = filteredNames;
        notifyDataSetChanged();
    }
    public List<AddDoctorEntryProducts> getSelectedProducts() {
        List<AddDoctorEntryProducts> selectedProducts = new ArrayList<>();
        for (CallCommonCheckedList item : callCommonCheckedListArrayList) {
            if (item.getQty() > 0 && item.getSelectedValue() > 0) {
                selectedProducts.add(new AddDoctorEntryProducts(item.getName(),item.getCode(),String.valueOf(item.getQty()),item.getProduct_Sale_Unit(),String.valueOf(item.getSelectedMRP()),item.getRetailor_Price(),
                        item.getDistributor_Price(),item.getNSR_Price(),item.getSample_Price(),item.getTarget_Price(),String.valueOf(item.getSelectedValue()),item.getDetailcode()));
            }
        }
        return selectedProducts;
    }
    public void countvalues() {
        int count = 0;
        double total = 0;
        for (CallCommonCheckedList p : callCommonCheckedListArrayList) {
            if (p.getQty() > 0) {
                count++;

                double rate;
                try {
                    if (p.isSelected()) {
                        rate = p.getSelectedMRP(); // use selected MRP if selected
                    } else {
                        rate = Double.parseDouble(p.getRate());
                    }
                } catch (NumberFormatException e) {
                    rate = 0;
                }

                total += p.getQty() * rate;
            }
        }
        docbusinessProductListBinding.prdCount.setText(String.valueOf(count));
        docbusinessProductListBinding.txtValue.setText(String.format(Locale.getDefault(), "%.2f", total));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_pack;
        EditText rate,targetQty,potentialQty,BusinessQty,Businessvalue;
        TextWatcher qtyTextWatcher;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_docbusiness_prd_name);
            tv_pack = itemView.findViewById(R.id.tv_pack);
            rate = itemView.findViewById(R.id.ed_rate);
            targetQty = itemView.findViewById(R.id.ed_target);
            potentialQty = itemView.findViewById(R.id.ed_potential);
            BusinessQty = itemView.findViewById(R.id.ed_businessqty);
            Businessvalue = itemView.findViewById(R.id.ed_businessvalue);
        }
    }

}

