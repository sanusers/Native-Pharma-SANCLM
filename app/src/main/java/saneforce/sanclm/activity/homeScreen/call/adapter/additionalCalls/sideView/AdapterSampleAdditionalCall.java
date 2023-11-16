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
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterSampleAdditionalCall extends RecyclerView.Adapter<AdapterSampleAdditionalCall.ViewHolder> {
    public static ArrayList<AddSampleAdditionalCall> addedSampleList;
    Context context;
    ArrayList<CallCommonCheckedList> callSampleList;
    ListViewSample listViewSample;
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
        SetupSampleData(holder.rv_Sample);
        commonUtilsMethods = new CommonUtilsMethods(context);

        Log.v("Sample", "----" + addedSampleList.get(position).getSample_qty());
        holder.tv_select_sample.setText(addedSampleList.get(position).getPrd_name());
        holder.edt_sam_qty.setText(addedSampleList.get(position).getSample_qty());
        holder.rv_Sample.addOnItemTouchListener(mScrollTouchListener);
        holder.tv_select_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ViewHolder.listCv_Sample.getVisibility() == View.VISIBLE) {
                    ViewHolder.listCv_Sample.setVisibility(View.GONE);
                } else {
                    ListViewSample.pos = holder.getAdapterPosition();
                    ViewHolder.listCv_Sample.setVisibility(View.VISIBLE);
                }
            }
        });


        holder.search_sample.addTextChangedListener(new TextWatcher() {
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

        holder.edt_sam_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.v("Sample", "--edit--" + editable.toString());
                addedSampleList.set(position, new AddSampleAdditionalCall(addedSampleList.get(position).getCust_name(), addedSampleList.get(position).getCust_code(), addedSampleList.get(position).getPrd_name(), addedSampleList.get(position).getPrd_code(), addedSampleList.get(position).getPrd_stock(), editable.toString()));
            }
        });

        holder.img_del_sample.setOnClickListener(view -> {
            removeAt(position);
        });
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : callSampleList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        listViewSample.filterList(filterdNames);
    }

    private void SetupSampleData(RecyclerView rv_sample) {
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
    }

    @Override
    public int getItemCount() {
        return addedSampleList.size();
    }

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
        TextView tv_select_sample;
        ImageView img_del_sample;
        EditText edt_sam_qty, search_sample;
        RecyclerView rv_Sample;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_select_sample = itemView.findViewById(R.id.tv_select_sample);
            img_del_sample = itemView.findViewById(R.id.img_del_sample);
            edt_sam_qty = itemView.findViewById(R.id.ed_sample_qty);
            listCv_Sample = itemView.findViewById(R.id.listCv_Sample);
            rv_Sample = itemView.findViewById(R.id.lv_Sample);
            search_sample = itemView.findViewById(R.id.searchSample);
        }
    }

    public static class ListViewSample extends RecyclerView.Adapter<ListViewSample.ViewHolder> {
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
                        Toast.makeText(context, "You Already Select this Input", Toast.LENGTH_SHORT).show();
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
    }
}
