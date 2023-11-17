package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView;


import static saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.callDetailsSideBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterInputAdditionalCall extends RecyclerView.Adapter<AdapterInputAdditionalCall.ViewHolder> {
    public static ArrayList<AddInputAdditionalCall> addedInpList;
    ArrayList<CallCommonCheckedList> callInputList;
    ListViewInput listViewInput;
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

    public AdapterInputAdditionalCall(Context context, ArrayList<AddInputAdditionalCall> addedInpList, ArrayList<CallCommonCheckedList> callInputList) {
        this.context = context;
        AdapterInputAdditionalCall.addedInpList = addedInpList;
        this.callInputList = callInputList;
    }

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
        SetupInputData(holder.rv_Input);
        commonUtilsMethods = new CommonUtilsMethods(context);

        holder.tv_select_input.setText(addedInpList.get(position).getInput_name());
        holder.edt_inp_qty.setText(addedInpList.get(position).getInp_qty());
        holder.rv_Input.addOnItemTouchListener(mScrollTouchListener);

        holder.tv_select_input.setOnClickListener(view -> {
            if (ViewHolder.listCv_Input.getVisibility() == View.VISIBLE) {
                ViewHolder.listCv_Input.setVisibility(View.GONE);
            } else {
                ListViewInput.pos = holder.getAdapterPosition();
                ViewHolder.listCv_Input.setVisibility(View.VISIBLE);
            }
        });

        holder.search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
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
                addedInpList.set(position, new AddInputAdditionalCall(addedInpList.get(position).getCust_name(), addedInpList.get(position).getCust_code(), addedInpList.get(position).getInput_name(), addedInpList.get(position).getInput_code(), addedInpList.get(position).getStock(), holder.edt_inp_qty.getText().toString()));
            }
        });

        holder.img_del_input.setOnClickListener(view -> {
            removeAt(position);
        });

    }

    private void SetupInputData(RecyclerView rv_Input) {
        try {
            listViewInput = new ListViewInput(context, callInputList);
            RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(context);
            rv_Input.setLayoutManager(mLayoutManagerChe);
            rv_Input.setItemAnimator(new DefaultItemAnimator());
            rv_Input.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            rv_Input.setAdapter(listViewInput);
        } catch (Exception e) {
            Log.v("JsonInput", "error--" + e);
        }
    }

    @Override
    public int getItemCount() {
        return addedInpList.size();
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : callInputList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        listViewInput.filterList(filterdNames);
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
        public static CardView listCv_Input;
        TextView tv_select_input;
        ImageView img_del_input;
        EditText edt_inp_qty, search_input;
        RecyclerView rv_Input;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_select_input = itemView.findViewById(R.id.tv_select_input);
            img_del_input = itemView.findViewById(R.id.img_del_input);
            edt_inp_qty = itemView.findViewById(R.id.ed_input_qty);
            listCv_Input = itemView.findViewById(R.id.listCv_Input);
            rv_Input = itemView.findViewById(R.id.lv_Input);
            search_input = itemView.findViewById(R.id.searchInput);
        }
    }

    public static class ListViewInput extends RecyclerView.Adapter<ListViewInput.ViewHolder> {
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
    }
}
