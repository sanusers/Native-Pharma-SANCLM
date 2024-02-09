package saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.sideView;


import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.InputValidation;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.StockInput;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter.New_Edit;
import static saneforce.santrip.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.callDetailsSideBinding;
import static saneforce.santrip.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.editedInpList;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.fragments.AddCallSelectInpSide;
import saneforce.santrip.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.InputFilterMinMax;

public class AdapterInputAdditionalCall extends RecyclerView.Adapter<AdapterInputAdditionalCall.ViewHolder> {
    public static ArrayList<AddInputAdditionalCall> addedInpList;
    Context context;
    CommonUtilsMethods commonUtilsMethods;

    public AdapterInputAdditionalCall(Context context, ArrayList<AddInputAdditionalCall> addedInpList) {
        this.context = context;
        AdapterInputAdditionalCall.addedInpList = addedInpList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_input_additional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);

        holder.tv_select_input.setText(addedInpList.get(position).getInput_name());
        holder.edt_inp_qty.setText(addedInpList.get(position).getInp_qty());

        if (InputValidation.equalsIgnoreCase("1")) {
            holder.tv_stock.setVisibility(View.VISIBLE);
            holder.tv_stock.setText(addedInpList.get(position).getBalance_stock());
        }

        if (addedInpList.get(position).getInp_qty().isEmpty()) {
            holder.edt_inp_qty.setHint("0");
        }

        holder.edt_inp_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (DCRCallActivity.InputValidation.equalsIgnoreCase("1")) {
                        holder.edt_inp_qty.setFilters(new InputFilter[]{new InputFilterMinMax("1", addedInpList.get(position).getLast_stock())});
                        if (!editable.toString().isEmpty()) {
                            int final_value = Integer.parseInt(addedInpList.get(position).getLast_stock()) - Integer.parseInt(editable.toString());
                            holder.tv_stock.setText(String.valueOf(final_value));
                            addedInpList.set(position, new AddInputAdditionalCall(addedInpList.get(position).getCust_name(), addedInpList.get(position).getCust_code(), addedInpList.get(position).getInput_name(), addedInpList.get(position).getInput_code(), String.valueOf(final_value), addedInpList.get(position).getLast_stock(), holder.edt_inp_qty.getText().toString()));
                        } else {
                            holder.tv_stock.setText(addedInpList.get(position).getLast_stock());
                            addedInpList.set(position, new AddInputAdditionalCall(addedInpList.get(position).getCust_name(), addedInpList.get(position).getCust_code(), addedInpList.get(position).getInput_name(), addedInpList.get(position).getInput_code(), addedInpList.get(position).getLast_stock(), addedInpList.get(position).getLast_stock(), holder.edt_inp_qty.getText().toString()));
                        }
                    } else {
                        addedInpList.set(position, new AddInputAdditionalCall(addedInpList.get(position).getCust_name(), addedInpList.get(position).getCust_code(), addedInpList.get(position).getInput_name(), addedInpList.get(position).getInput_code(), addedInpList.get(position).getBalance_stock(), addedInpList.get(position).getLast_stock(), holder.edt_inp_qty.getText().toString()));
                    }
                } catch (Exception ignored) {

                }
            }
        });

        holder.img_del_input.setOnClickListener(view -> {
            if (New_Edit.equalsIgnoreCase("Edit")) {
                for (int j = 0; j < editedInpList.size(); j++) {
                    if (editedInpList.get(j).getInput_code().equalsIgnoreCase(addedInpList.get(position).getInput_code())) {
                        editedInpList.remove(j);
                        break;
                    }
                }
                if (InputValidation.equalsIgnoreCase("1")) {
                    for (int i = 0; i < StockInput.size(); i++) {
                        int currentBalance;
                        if (StockInput.get(i).getStockCode().equalsIgnoreCase(addedInpList.get(position).getInput_code())) {
                            if (addedInpList.get(position).getInp_qty().equalsIgnoreCase("0") || addedInpList.get(position).getInp_qty().isEmpty()) {
                                currentBalance = Integer.parseInt(StockInput.get(i).getCurrentStock());
                            } else {
                                currentBalance = Integer.parseInt(StockInput.get(i).getCurrentStock()) + Integer.parseInt(addedInpList.get(position).getInp_qty());
                            }
                            StockInput.set(i, new CallCommonCheckedList(StockInput.get(i).getStockCode(), StockInput.get(i).getActualStock(), String.valueOf(currentBalance)));
                        }
                    }
                }
            }
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return addedInpList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        addedInpList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, addedInpList.size());
        commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddInputsAdditional);
        callDetailsSideBinding.rvAddInputsAdditional.setAdapter(AdditionalCallDetailedSide.adapterInputAdditionalCall);
        AdditionalCallDetailedSide.adapterInputAdditionalCall.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_select_input, tv_stock;
        ImageView img_del_input;
        EditText edt_inp_qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_select_input = itemView.findViewById(R.id.tv_select_input);
            tv_stock = itemView.findViewById(R.id.tv_stock);
            img_del_input = itemView.findViewById(R.id.img_del_input);
            edt_inp_qty = itemView.findViewById(R.id.ed_input_qty);
        }
    }
}