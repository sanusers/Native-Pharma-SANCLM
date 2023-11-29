package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView;


import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.SampleValidation;
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
import saneforce.sanclm.activity.homeScreen.call.fragments.AddCallSelectPrdSide;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterSampleAdditionalCall extends RecyclerView.Adapter<AdapterSampleAdditionalCall.ViewHolder> {
    public static ArrayList<AddSampleAdditionalCall> addedSampleList;
    Context context;
    ArrayList<CallCommonCheckedList> callSampleList;
    // ListViewSample listViewSample;
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

    public AdapterSampleAdditionalCall(Context context, ArrayList<AddSampleAdditionalCall> addedSampleList) {
        this.context = context;
        AdapterSampleAdditionalCall.addedSampleList = addedSampleList;
    }

    public AdapterSampleAdditionalCall(Context context, ArrayList<AddSampleAdditionalCall> addedSampleList, ArrayList<CallCommonCheckedList> callSampleList) {
        this.context = context;
        AdapterSampleAdditionalCall.addedSampleList = addedSampleList;
        this.callSampleList = callSampleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_sample_additional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //  SetupSampleData(holder.rv_Sample);
        commonUtilsMethods = new CommonUtilsMethods(context);

        holder.tv_select_sample.setText(addedSampleList.get(position).getPrd_name());
        holder.edt_sam_qty.setText(addedSampleList.get(position).getSample_qty());
        holder.rv_Sample.addOnItemTouchListener(mScrollTouchListener);

        if (SampleValidation.equalsIgnoreCase("1")) {
            holder.tv_sample_stock.setVisibility(View.VISIBLE);
            holder.tv_sample_stock.setText(addedSampleList.get(position).getBalance_stock());
            if (addedSampleList.get(position).getCategory().equalsIgnoreCase("Sale") || addedSampleList.get(position).getCategory().equalsIgnoreCase("Sample")) {
                holder.edt_sam_qty.setEnabled(true);
            } else if (addedSampleList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                if (Integer.parseInt(addedSampleList.get(position).getBalance_stock()) > 0) {
                    holder.edt_sam_qty.setEnabled(true);
                } else {
                    holder.edt_sam_qty.setEnabled(false);
                    holder.edt_sam_qty.setText("0");
                }
            } else {
                holder.edt_sam_qty.setEnabled(true);
            }
        } else {
            holder.tv_sample_stock.setVisibility(View.GONE);
        }

        holder.tv_select_sample.setOnClickListener(view -> {
            AddCallSelectPrdSide.SelectACProductAdapter.pos = holder.getAdapterPosition();
            dcrcallBinding.fragmentAcSelectProductSide.setVisibility(View.VISIBLE);
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
                        if (addedSampleList.get(position).getCategory().equalsIgnoreCase("Sale")) {
                            addedSampleList.set(holder.getAdapterPosition(), new AddSampleAdditionalCall(addedSampleList.get(position).getCust_name(), addedSampleList.get(position).getCust_code(), addedSampleList.get(position).getPrd_name(), addedSampleList.get(position).getPrd_code(), addedSampleList.get(position).getBalance_stock(), addedSampleList.get(position).getLast_stock(), editable.toString(), addedSampleList.get(position).getCategory()));
                        } else if (addedSampleList.get(position).getCategory().equalsIgnoreCase("Sample") || addedSampleList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                            holder.edt_sam_qty.setFilters(new InputFilter[]{new InputFilterMinMax("1", addedSampleList.get(position).getBalance_stock())});
                            if (!editable.toString().isEmpty()) {
                                int final_value = Integer.parseInt(addedSampleList.get(position).getLast_stock()) - Integer.parseInt(editable.toString());
                                holder.tv_sample_stock.setText(String.valueOf(final_value));
                                addedSampleList.set(holder.getAdapterPosition(), new AddSampleAdditionalCall(addedSampleList.get(position).getCust_name(), addedSampleList.get(position).getCust_code(), addedSampleList.get(position).getPrd_name(), addedSampleList.get(position).getPrd_code(), String.valueOf(final_value), addedSampleList.get(position).getLast_stock(), editable.toString(), addedSampleList.get(position).getCategory()));
                            } else {
                                holder.tv_sample_stock.setText(addedSampleList.get(position).getLast_stock());
                                addedSampleList.set(holder.getAdapterPosition(), new AddSampleAdditionalCall(addedSampleList.get(position).getCust_name(), addedSampleList.get(position).getCust_code(), addedSampleList.get(position).getPrd_name(), addedSampleList.get(position).getPrd_code(), addedSampleList.get(position).getLast_stock(), addedSampleList.get(position).getLast_stock(), editable.toString(), addedSampleList.get(position).getCategory()));
                            }
                        }
                    } else {
                        addedSampleList.set(position, new AddSampleAdditionalCall(addedSampleList.get(position).getCust_name(), addedSampleList.get(position).getCust_code(), addedSampleList.get(position).getPrd_name(), addedSampleList.get(position).getPrd_code(), addedSampleList.get(position).getBalance_stock(), addedSampleList.get(position).getLast_stock(), editable.toString(), addedSampleList.get(position).getCategory()));
                    }
                } catch (Exception e) {

                }
            }
        });

        holder.img_del_sample.setOnClickListener(view -> {
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return addedSampleList.size();
    }

   /* private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : callSampleList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        listViewSample.filterList(filterdNames);
    }*/

 /*   private void SetupSampleData(RecyclerView rv_sample) {
        try {
            listViewSample = new ListViewSample(context, callSampleList);
            RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(context);
            rv_sample.setLayoutManager(mLayoutManagerChe);
            rv_sample.setItemAnimator(new DefaultItemAnimator());
            rv_sample.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
            rv_sample.setAdapter(listViewSample);
        } catch (Exception e) {
            Log.v("JsonSample", "error--" + e);
        }
    }*/

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        addedSampleList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, addedSampleList.size());
        commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddSampleAdditional);
        callDetailsSideBinding.rvAddSampleAdditional.setAdapter(AdditionalCallDetailedSide.adapterSampleAdditionalCall);
        AdditionalCallDetailedSide.adapterSampleAdditionalCall.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public static CardView listCv_Sample;
        TextView tv_select_sample, tv_sample_stock;
        ImageView img_del_sample;
        EditText edt_sam_qty, search_sample;
        RecyclerView rv_Sample;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_select_sample = itemView.findViewById(R.id.tv_select_sample);
            tv_sample_stock = itemView.findViewById(R.id.tv_sample_stock);
            img_del_sample = itemView.findViewById(R.id.img_del_sample);
            edt_sam_qty = itemView.findViewById(R.id.ed_sample_qty);
            listCv_Sample = itemView.findViewById(R.id.listCv_Sample);
            rv_Sample = itemView.findViewById(R.id.lv_Sample);
            search_sample = itemView.findViewById(R.id.searchSample);
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

  /*  public static class ListViewSample extends RecyclerView.Adapter<ListViewSample.ViewHolder> {
        public static int pos;
        Context context;
        ArrayList<CallCommonCheckedList> SamList;
        CommonUtilsMethods commonUtilsMethods;

        public ListViewSample(Context context, ArrayList<CallCommonCheckedList> samList) {
            this.context = context;
            SamList = samList;
        }

        @NonNull
        @Override
        public ListViewSample.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
            return new ListViewSample.ViewHolder(view);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            commonUtilsMethods = new CommonUtilsMethods(context);
            holder.tv_name.setText(SamList.get(position).getName());
            holder.tv_name.setOnClickListener(view -> {
                for (int i = 0; i < addedSampleList.size(); i++) {
                    if (!SamList.get(position).getCode().equalsIgnoreCase(addedSampleList.get(i).getPrd_code())) {
                        if (pos == i) {
                            addedSampleList.set(pos, new AddSampleAdditionalCall(addedSampleList.get(pos).getCust_name(), addedSampleList.get(pos).getCust_code(), SamList.get(holder.getAdapterPosition()).getName(), SamList.get(holder.getAdapterPosition()).getCode(), "", addedSampleList.get(pos).getSample_qty()));
                            commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddSampleAdditional);
                            callDetailsSideBinding.rvAddSampleAdditional.setAdapter(AdditionalCallDetailedSide.adapterSampleAdditionalCall);
                            AdditionalCallDetailedSide.adapterSampleAdditionalCall.notifyDataSetChanged();
                            AdapterSampleAdditionalCall.ViewHolder.listCv_Sample.setVisibility(View.GONE);
                            break;
                        }
                    } else {
                        Toast.makeText(context, "You Already Select this Sample", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return SamList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CallCommonCheckedList> filterdNames) {
            this.SamList = filterdNames;
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
