package saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.sideView;


import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.PrdSamNeed;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SamQtyRestrictValue;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SamQtyRestriction;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SampleValidation;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.StockSample;
import static saneforce.santrip.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.callDetailsSideBinding;
import static saneforce.santrip.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.editedPrdList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.santrip.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.InputFilterMinMax;

public class AdapterSampleAdditionalCall extends RecyclerView.Adapter<AdapterSampleAdditionalCall.ViewHolder> {
    public static ArrayList<AddSampleAdditionalCall> addedProductList;
    Context context;
    CommonUtilsMethods commonUtilsMethods;
    String finalValue;

    public AdapterSampleAdditionalCall(Context context, ArrayList<AddSampleAdditionalCall> addedProductList) {
        this.context = context;
        AdapterSampleAdditionalCall.addedProductList = addedProductList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_sample_additional, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);

        holder.tv_select_sample.setText(addedProductList.get(position).getPrd_name());

        if (PrdSamNeed.equalsIgnoreCase("1")) {
            holder.edt_sam_qty.setVisibility(View.VISIBLE);
            holder.edt_sam_qty.setText(addedProductList.get(position).getSample_qty());
        }

        if (addedProductList.get(position).getSample_qty().isEmpty()) {
            holder.edt_sam_qty.setHint("0");
        }

        if (SampleValidation.equalsIgnoreCase("1")) {
            holder.tv_sample_stock.setVisibility(View.VISIBLE);
            holder.tv_sample_stock.setText(addedProductList.get(position).getBalance_stock());

            if (addedProductList.get(position).getCategory().equalsIgnoreCase("Sale") || addedProductList.get(position).getCategory().equalsIgnoreCase("Sample")) {
                holder.edt_sam_qty.setEnabled(true);
            } else if (addedProductList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                if (Integer.parseInt(addedProductList.get(position).getBalance_stock()) > 0) {
                    holder.edt_sam_qty.setEnabled(true);
                } else {
                    holder.edt_sam_qty.setEnabled(false);
                    holder.edt_sam_qty.setText("0");
                }
            }
        } else {
            holder.tv_sample_stock.setVisibility(View.GONE);
        }


        holder.edt_sam_qty.setOnTouchListener((v, event) -> {
            if (SampleValidation.equalsIgnoreCase("1")) {
                if (addedProductList.get(position).getCategory().equalsIgnoreCase("Sample") || addedProductList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                    if (SamQtyRestriction.equalsIgnoreCase("0")) {
                        //  Log.v("asdasds", (Integer.parseInt(SamQtyRestrictValue) >= Integer.parseInt(productListArrayList.get(position).getLast_stock())) + "----" + SamQtyRestrictValue + "----" + productListArrayList.get(position).getLast_stock());
                        if (Integer.parseInt(SamQtyRestrictValue) >= Integer.parseInt(addedProductList.get(position).getLast_stock())) {
                            holder.edt_sam_qty.setFilters(new InputFilter[]{new InputFilterMinMax("1", addedProductList.get(position).getLast_stock())});
                            finalValue = addedProductList.get(position).getLast_stock();
                        } else {
                            finalValue = SamQtyRestrictValue;
                            holder.edt_sam_qty.setFilters(new InputFilter[]{new InputFilterMinMax("1", SamQtyRestrictValue)});
                        }
                    } else {
                        finalValue = addedProductList.get(position).getLast_stock();
                        holder.edt_sam_qty.setFilters(new InputFilter[]{new InputFilterMinMax("1", addedProductList.get(position).getLast_stock())});
                    }
                }
            } else {
                if (SamQtyRestriction.equalsIgnoreCase("0")) {
                    finalValue = SamQtyRestrictValue;
                    holder.edt_sam_qty.setFilters(new InputFilter[]{new InputFilterMinMax("1", SamQtyRestrictValue)});
                }
            }
            return false;
        });


        holder.edt_sam_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (SampleValidation.equalsIgnoreCase("1")) {
                        if (addedProductList.get(position).getCategory().equalsIgnoreCase("Sample") || addedProductList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                            // holder.edt_sam_qty.setFilters(new InputFilter[]{new InputFilterMinMax("1", addedProductList.get(position).getBalance_stock())});
                            if (!editable.toString().isEmpty()) {
                                int final_value = Integer.parseInt(addedProductList.get(position).getLast_stock()) - Integer.parseInt(editable.toString());
                                holder.tv_sample_stock.setText(String.valueOf(final_value));
                                addedProductList.set(holder.getBindingAdapterPosition(), new AddSampleAdditionalCall(addedProductList.get(position).getCust_name(), addedProductList.get(position).getCust_code(), addedProductList.get(position).getPrd_name(), addedProductList.get(position).getPrd_code(), String.valueOf(final_value), addedProductList.get(position).getLast_stock(), editable.toString(), addedProductList.get(position).getCategory()));
                            } else {
                                holder.tv_sample_stock.setText(addedProductList.get(position).getLast_stock());
                                addedProductList.set(holder.getBindingAdapterPosition(), new AddSampleAdditionalCall(addedProductList.get(position).getCust_name(), addedProductList.get(position).getCust_code(), addedProductList.get(position).getPrd_name(), addedProductList.get(position).getPrd_code(), addedProductList.get(position).getLast_stock(), addedProductList.get(position).getLast_stock(), editable.toString(), addedProductList.get(position).getCategory()));
                            }
                        }
                    } else {
                        addedProductList.set(holder.getBindingAdapterPosition(), new AddSampleAdditionalCall(addedProductList.get(position).getCust_name(), addedProductList.get(position).getCust_code(), addedProductList.get(position).getPrd_name(), addedProductList.get(position).getPrd_code(), addedProductList.get(position).getBalance_stock(), addedProductList.get(position).getLast_stock(), editable.toString(), addedProductList.get(position).getCategory()));
                    }
                } catch (Exception ignored) {

                }
            }
        });

        holder.img_del_sample.setOnClickListener(view -> {
            if (FinalAdditionalCallAdapter.New_Edit.equalsIgnoreCase("Edit")) {
                for (int j = 0; j < editedPrdList.size(); j++) {
                    if (editedPrdList.get(j).getPrd_code().equalsIgnoreCase(addedProductList.get(position).getPrd_code())) {
                        editedPrdList.remove(j);
                        break;
                    }
                }
                if (SampleValidation.equalsIgnoreCase("1")) {
                    for (int i = 0; i < StockSample.size(); i++) {
                        int currentBalance;
                        if (StockSample.get(i).getStockCode().equalsIgnoreCase(addedProductList.get(position).getPrd_code())) {
                            if (addedProductList.get(position).getSample_qty().equalsIgnoreCase("0") || addedProductList.get(position).getSample_qty().isEmpty()) {
                                currentBalance = Integer.parseInt(StockSample.get(i).getCurrentStock());
                            } else {
                                currentBalance = Integer.parseInt(StockSample.get(i).getCurrentStock()) + Integer.parseInt(addedProductList.get(position).getSample_qty());
                            }
                            StockSample.set(i, new CallCommonCheckedList(StockSample.get(i).getStockCode(), StockSample.get(i).getActualStock(), String.valueOf(currentBalance)));
                        }
                    }
                }
            }
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return addedProductList.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        addedProductList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, addedProductList.size());
        commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddSampleAdditional);
        callDetailsSideBinding.rvAddSampleAdditional.setAdapter(AdditionalCallDetailedSide.adapterSampleAdditionalCall);
        AdditionalCallDetailedSide.adapterSampleAdditionalCall.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_select_sample, tv_sample_stock;
        ImageView img_del_sample;
        EditText edt_sam_qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_select_sample = itemView.findViewById(R.id.tv_select_sample);
            tv_sample_stock = itemView.findViewById(R.id.tv_sample_stock);
            img_del_sample = itemView.findViewById(R.id.img_del_sample);
            edt_sam_qty = itemView.findViewById(R.id.ed_sample_qty);
        }
    }
}
