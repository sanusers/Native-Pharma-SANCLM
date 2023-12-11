package saneforce.sanclm.activity.homeScreen.call.adapter.input;


import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.StockInput;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.fragments.InputFragment;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;


public class FinalInputCallAdapter extends RecyclerView.Adapter<FinalInputCallAdapter.ViewHolder> {
    public static ArrayList<CallCommonCheckedList> checked_arraylist;
    Context context;
    Activity activity;
    ArrayList<SaveCallInputList> saveCallInputLists;
    CheckInputListAdapter checkInputListAdapter;
    CommonUtilsMethods commonUtilsMethods;

    public FinalInputCallAdapter(Activity activity, Context context, ArrayList<SaveCallInputList> saveCallInputLists, ArrayList<CallCommonCheckedList> callCommonCheckedLists) {
        this.activity = activity;
        this.context = context;
        this.saveCallInputLists = saveCallInputLists;
        checked_arraylist = callCommonCheckedLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_input_call, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);

        if (DCRCallActivity.InputValidation.equalsIgnoreCase("1")) {
            holder.tv_input_stk.setVisibility(View.VISIBLE);
            if (Integer.parseInt(saveCallInputLists.get(position).getLast_inp_stk()) > 0) {
                holder.ed_inpQty.setEnabled(true);
            } else {
                holder.ed_inpQty.setText("0");
                holder.ed_inpQty.setEnabled(false);
            }
        } else {
            holder.tv_input_stk.setVisibility(View.GONE);
        }

        holder.tv_inp_name.setText(saveCallInputLists.get(position).getInput_name());
        holder.tv_input_stk.setText(saveCallInputLists.get(position).getBalance_inp_stk());
        holder.ed_inpQty.setText(saveCallInputLists.get(position).getInp_qty());

        holder.tv_inp_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, saveCallInputLists.get(position).getInput_name()));
        holder.ed_inpQty.addTextChangedListener(new TextWatcher() {
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
                        holder.ed_inpQty.setFilters(new InputFilter[]{new InputFilterMinMax("1", saveCallInputLists.get(position).getLast_inp_stk())});
                        if (!editable.toString().isEmpty()) {
                            int final_value = Integer.parseInt(saveCallInputLists.get(position).getLast_inp_stk()) - Integer.parseInt(editable.toString());
                            holder.tv_input_stk.setText(String.valueOf(final_value));
                            saveCallInputLists.set(holder.getBindingAdapterPosition(), new SaveCallInputList(saveCallInputLists.get(holder.getBindingAdapterPosition()).getInput_name(), saveCallInputLists.get(holder.getBindingAdapterPosition()).getInp_code(), editable.toString(), String.valueOf(final_value), saveCallInputLists.get(holder.getBindingAdapterPosition()).getLast_inp_stk()));

                            for (int i = 0; i < StockInput.size(); i++) {
                                if (StockInput.get(i).getStockCode().equalsIgnoreCase(saveCallInputLists.get(position).getInp_code())) {
                                    StockInput.set(i, new CallCommonCheckedList(StockInput.get(i).getStockCode(), StockInput.get(i).getActualStock(), String.valueOf(final_value)));
                                }
                            }

                        } else {
                            holder.tv_input_stk.setText(saveCallInputLists.get(position).getLast_inp_stk());
                            saveCallInputLists.set(holder.getBindingAdapterPosition(), new SaveCallInputList(saveCallInputLists.get(holder.getBindingAdapterPosition()).getInput_name(), saveCallInputLists.get(holder.getBindingAdapterPosition()).getInp_code(), "", saveCallInputLists.get(holder.getBindingAdapterPosition()).getLast_inp_stk(), saveCallInputLists.get(holder.getBindingAdapterPosition()).getLast_inp_stk()));

                            for (int i = 0; i < StockInput.size(); i++) {
                                if (StockInput.get(i).getStockCode().equalsIgnoreCase(saveCallInputLists.get(position).getInp_code())) {
                                    StockInput.set(i, new CallCommonCheckedList(StockInput.get(i).getStockCode(), StockInput.get(i).getActualStock(), saveCallInputLists.get(position).getLast_inp_stk()));
                                }
                            }
                        }
                    } else {
                        saveCallInputLists.set(holder.getBindingAdapterPosition(), new SaveCallInputList(saveCallInputLists.get(holder.getBindingAdapterPosition()).getInput_name(), saveCallInputLists.get(holder.getBindingAdapterPosition()).getInp_code(), saveCallInputLists.get(holder.getBindingAdapterPosition()).getLast_inp_stk(), saveCallInputLists.get(holder.getBindingAdapterPosition()).getLast_inp_stk(), saveCallInputLists.get(holder.getBindingAdapterPosition()).getLast_inp_stk()));
                    }

                } catch (Exception ignored) {

                }
            }
        });

        if (CheckInputListAdapter.isCheckedInp && !CheckInputListAdapter.UnSelectedInpCode.isEmpty()) {
            for (int i = 0; i < saveCallInputLists.size(); i++) {
                if (CheckInputListAdapter.UnSelectedInpCode.equalsIgnoreCase(saveCallInputLists.get(position).getInp_code())) {
                    new CountDownTimer(80, 80) {
                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            try {
                                for (int i = 0; i < StockInput.size(); i++) {
                                    int currentBalance;
                                    if (StockInput.get(i).getStockCode().equalsIgnoreCase(saveCallInputLists.get(position).getInp_code())) {
                                        if (saveCallInputLists.get(position).getInp_qty().equalsIgnoreCase("0") || saveCallInputLists.get(position).getInp_qty().isEmpty()) {
                                            currentBalance = Integer.parseInt(StockInput.get(i).getCurrentStock());
                                        } else {
                                            currentBalance = Integer.parseInt(StockInput.get(i).getCurrentStock()) + Integer.parseInt(saveCallInputLists.get(position).getInp_qty());
                                        }
                                        StockInput.set(i, new CallCommonCheckedList(StockInput.get(i).getStockCode(), StockInput.get(i).getActualStock(), String.valueOf(currentBalance)));
                                    }
                                }
                                removeAt(position);
                                CheckInputListAdapter.isCheckedInp = false;
                            } catch (Exception ignored) {

                            }
                        }
                    }.start();
                    break;
                }
            }
        }

        holder.img_del_inp.setOnClickListener(view -> {
            try {
                for (int j = 0; j < checked_arraylist.size(); j++) {
                    if (checked_arraylist.get(j).getCode().equalsIgnoreCase(saveCallInputLists.get(position).getInp_code())) {
                        checked_arraylist.set(j, new CallCommonCheckedList(checked_arraylist.get(j).getName(), checked_arraylist.get(j).getCode(), checked_arraylist.get(j).getStock_balance(), false));
                        break;
                    }
                }

                for (int i = 0; i < StockInput.size(); i++) {
                    int currentBalance;
                    if (StockInput.get(i).getStockCode().equalsIgnoreCase(saveCallInputLists.get(position).getInp_code())) {
                        if (saveCallInputLists.get(position).getInp_qty().equalsIgnoreCase("0") || saveCallInputLists.get(position).getInp_qty().isEmpty()) {
                            currentBalance = Integer.parseInt(StockInput.get(i).getCurrentStock());
                        } else {
                            currentBalance = Integer.parseInt(StockInput.get(i).getCurrentStock()) + Integer.parseInt(saveCallInputLists.get(position).getInp_qty());
                        }
                        StockInput.set(i, new CallCommonCheckedList(StockInput.get(i).getStockCode(), StockInput.get(i).getActualStock(), String.valueOf(currentBalance)));
                    }
                }

                checkInputListAdapter = new CheckInputListAdapter(activity, context, checked_arraylist, saveCallInputLists);
                commonUtilsMethods.recycleTestWithDivider(InputFragment.fragmentInputBinding.rvCheckDataList);
                InputFragment.fragmentInputBinding.rvCheckDataList.setAdapter(checkInputListAdapter);
                removeAt(position);
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return saveCallInputLists.size();
    }

    public void removeAt(int position) {
        saveCallInputLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, saveCallInputLists.size());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_inp_name, tv_input_stk;
        ImageView img_del_inp;
        EditText ed_inpQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_inp_name = itemView.findViewById(R.id.tv_input_name);
            tv_input_stk = itemView.findViewById(R.id.tv_input_stk);
            img_del_inp = itemView.findViewById(R.id.img_del_input);
            ed_inpQty = itemView.findViewById(R.id.ed_input_qty);
        }
    }
}