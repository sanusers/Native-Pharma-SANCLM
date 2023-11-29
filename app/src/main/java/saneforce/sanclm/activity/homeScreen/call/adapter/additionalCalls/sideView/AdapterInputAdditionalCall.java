package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView;


import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.InputValidation;
import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrcallBinding;
import static saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.callDetailsSideBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
    RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };


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
            AddCallSelectInpSide.SelectACInputAdapter.pos = holder.getAdapterPosition();
            dcrcallBinding.fragmentAcSelectInputSide.setVisibility(View.VISIBLE);
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
                } catch (Exception e) {

                }
            }
        });

        holder.img_del_input.setOnClickListener(view -> {
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

   /* private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : callInputList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        listViewInput.filterList(filterdNames);
    }*/

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

    public class InputFilterMinMax implements InputFilter {

        private final int min;
        private final int max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input)) return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

   /* public static class ListViewInput extends RecyclerView.Adapter<ListViewInput.ViewHolder> {
        public static int pos;
        Context context;
        ArrayList<CallCommonCheckedList> InpList;
        CommonUtilsMethods commonUtilsMethods;

        public ListViewInput(Context context, ArrayList<CallCommonCheckedList> inpList) {
            this.context = context;
            InpList = inpList;
        }

        @NonNull
        @Override
        public ListViewInput.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull ListViewInput.ViewHolder holder, int position) {
            commonUtilsMethods = new CommonUtilsMethods(context);
            holder.tv_name.setText(InpList.get(position).getName());
            holder.tv_name.setOnClickListener(view -> {
                for (int i = 0; i < addedInpList.size(); i++) {
                    if (!InpList.get(position).getCode().equalsIgnoreCase(addedInpList.get(i).getInput_code())) {
                        if (pos == i) {
                            addedInpList.set(pos, new AddInputAdditionalCall(addedInpList.get(pos).getCust_name(), addedInpList.get(pos).getCust_code(), InpList.get(holder.getAdapterPosition()).getName(), InpList.get(holder.getAdapterPosition()).getCode(), "", addedInpList.get(pos).getInp_qty()));
                            commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddInputsAdditional);
                            callDetailsSideBinding.rvAddInputsAdditional.setAdapter(AdditionalCallDetailedSide.adapterInputAdditionalCall);
                            AdditionalCallDetailedSide.adapterInputAdditionalCall.notifyDataSetChanged();
                            AdapterInputAdditionalCall.ViewHolder.listCv_Input.setVisibility(View.GONE);
                            break;
                        }
                    } else {
                        Toast.makeText(context, "You Already Select this Input", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return InpList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CallCommonCheckedList> filterdNames) {
            this.InpList = filterdNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
            }
        }
    }*/
}
