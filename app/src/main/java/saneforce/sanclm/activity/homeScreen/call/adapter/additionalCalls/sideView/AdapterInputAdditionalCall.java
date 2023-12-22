package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView;


import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.InputValidation;
import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.StockInput;
import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter.New_Edit;
import static saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.callDetailsSideBinding;
import static saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.editedInpList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
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

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.fragments.AddCallSelectInpSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

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

        holder.tv_select_input.setOnClickListener(view -> {
            boolean isEditedAvailable = false;
            if (New_Edit.equalsIgnoreCase("Edit")) {
                if (editedInpList.size() > 0) {
                    for (int j = 0; j < editedInpList.size(); j++) {
                        if (editedInpList.get(j).getInput_code().equalsIgnoreCase(addedInpList.get(position).getInput_code())) {
                            isEditedAvailable = true;
                            break;
                        }
                    }
                }
            }

            if (!isEditedAvailable) {
                AddCallSelectInpSide.SelectACInputAdapter.pos = holder.getBindingAdapterPosition();
                AddCallSelectInpSide.selectInputSideBinding.searchList.setText("");
                AddCallSelectInpSide.selectInputSideBinding.searchList.setHint(context.getResources().getString(R.string.search));
                AddCallSelectInpSide.selectInputSideBinding.selectListView.scrollToPosition(0);
                dcrCallBinding.fragmentAcSelectInputSide.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(context, "Can't edit data that already stored! Just Delete & Add new one", Toast.LENGTH_SHORT).show();
            }
        });
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

    public static class InputFilterMinMax implements InputFilter {

        private final int min;
        private final int max;

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input)) return null;
            } catch (NumberFormatException ignored) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
