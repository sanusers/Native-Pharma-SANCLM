package saneforce.sanclm.activity.homeScreen.call.adapter.input;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
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
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;


public class SaveInputCallAdapter extends RecyclerView.Adapter<SaveInputCallAdapter.ViewHolder> {
    public static int pos;
    Context context;
    Activity activity;

    ArrayList<CallCommonCheckedList> checked_arraylist;
    ArrayList<SaveCallInputList> saveCallInputLists;
    CommonSharedPreference mCommonsharedpreference;
    CallInputListAdapter callInputListAdapter;
    CommonUtilsMethods commonUtilsMethods;


    public SaveInputCallAdapter(Activity activity, Context context, ArrayList<SaveCallInputList> saveCallInputLists, ArrayList<CallCommonCheckedList> callCommonCheckedLists) {
        this.activity = activity;
        this.context = context;
        this.saveCallInputLists = saveCallInputLists;
        this.checked_arraylist = callCommonCheckedLists;
    }

    public static int getPosition() {
        return pos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_input_call, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mCommonsharedpreference = new CommonSharedPreference(context);
        commonUtilsMethods = new CommonUtilsMethods(context);

      /*  if (DCRCallActivity.InputValidation.equalsIgnoreCase("1")) {
            holder.tv_input_stk.setVisibility(View.VISIBLE);
        } else {
            holder.tv_input_stk.setVisibility(View.GONE);
        }*/

        holder.tv_inp_name.setText(saveCallInputLists.get(position).getInput_name());
        holder.tv_input_stk.setText(String.valueOf(saveCallInputLists.get(position).getInput_stk()));
        holder.ed_inpQty.setText(String.valueOf(saveCallInputLists.get(position).getInp_qty()));

        holder.tv_inp_name.setOnClickListener(view -> {
            commonUtilsMethods.displayPopupWindow(activity, context, view, saveCallInputLists.get(position).getInput_name());
        });

        holder.ed_inpQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
             /*   if (!editable.toString().isEmpty()) {
                    int entered_qty = Integer.parseInt(editable.toString());
                    int final_value = Integer.parseInt(saveCallInputLists.get(position).getInput_stk()) - entered_qty;
                    holder.ed_inpQty.setText(String.valueOf(final_value));
                    saveCallInputLists.set(holder.getAdapterPosition(), new SaveCallInputList(saveCallInputLists.get(holder.getAdapterPosition()).getInput_name(), saveCallInputLists.get(holder.getAdapterPosition()).getInp_code(), editable.toString(), saveCallInputLists.get(holder.getAdapterPosition()).getInput_stk()));
                }*/
                saveCallInputLists.set(holder.getAdapterPosition(), new SaveCallInputList(saveCallInputLists.get(holder.getAdapterPosition()).getInput_name(), saveCallInputLists.get(holder.getAdapterPosition()).getInp_code(), editable.toString(), saveCallInputLists.get(holder.getAdapterPosition()).getInput_stk()));

            }
        });

        if (CallInputListAdapter.isCheckedInp && !CallInputListAdapter.UnSelectedInpCode.isEmpty()) {
            for (int i = 0; i < saveCallInputLists.size(); i++) {
                if (CallInputListAdapter.UnSelectedInpCode.equalsIgnoreCase(saveCallInputLists.get(position).getInp_code())) {
                    new CountDownTimer(200, 200) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            pos = position;
                            removeAt(position);
                            CallInputListAdapter.isCheckedInp = false;
                        }
                    }.start();
                    break;
                }
            }
        }

        holder.img_del_inp.setOnClickListener(view -> {
            for (int j = 0; j < checked_arraylist.size(); j++) {
                if (checked_arraylist.get(j).getName().equalsIgnoreCase(saveCallInputLists.get(position).getInput_name())) {
                    checked_arraylist.set(j, new CallCommonCheckedList(saveCallInputLists.get(position).getInput_name(), saveCallInputLists.get(position).getInp_code(), false));
                }
            }

            callInputListAdapter = new CallInputListAdapter(activity, context, checked_arraylist, saveCallInputLists);
            commonUtilsMethods.recycleTestWithDivider(InputFragment.fragmentInputBinding.rvCheckDataList);
            InputFragment.fragmentInputBinding.rvCheckDataList.setAdapter(callInputListAdapter);
            removeAt(position);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
