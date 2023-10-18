package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.FilterDataList;
import saneforce.sanclm.activity.map.custSelection.CustList;

public class AdapterFilterSelection extends RecyclerView.Adapter<AdapterFilterSelection.ViewHolder> {
    public static Dialog dialog;
    Context context;
    ArrayList<FilterDataList> FilterList;
    ArrayList<CustList> custListArrayList;
    ArrayAdapter arrayAdapter;
    ArrayList<String> filterSelectionList = new ArrayList<>();
    ConstraintLayout constraintLayout;
    final static int ITEMS_PER_PAGE = 2;

    public AdapterFilterSelection(Context context, ArrayList<FilterDataList> filterList, ArrayList<CustList> custListArrayList) {
        this.context = context;
        FilterList = filterList;
        this.custListArrayList = custListArrayList;
    }


    @NonNull
    @Override
    public AdapterFilterSelection.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_condition, parent, false);
        return new ViewHolder(view);
       /* int itemWidth = parent.getWidth() / ITEMS_PER_PAGE;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_add_condition, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = itemWidth;
        itemView.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);*/
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFilterSelection.ViewHolder holder, int position) {
        holder.tv_conditions.setText(FilterList.get(position).getName());

        holder.tv_conditions.setOnClickListener(view -> {
            Log.v("dfsfsd", String.valueOf(FilterList.get(position).getPos()));
            filterSelectionList.clear();
            if (holder.rv_filterList.getVisibility() == View.VISIBLE) {
                holder.rv_filterList.setVisibility(View.GONE);
            } else {
                holder.rv_filterList.setVisibility(View.VISIBLE);
                if (FilterList.get(position).getPos() == 0) {
                    for (int i = 0; i < custListArrayList.size(); i++) {
                        filterSelectionList.add(custListArrayList.get(i).getSpecialist());
                    }

                    int count = filterSelectionList.size();
                    for (int i = 0; i < count; i++) {
                        for (int j = i + 1; j < count; j++) {
                            if (filterSelectionList.get(i).equalsIgnoreCase(filterSelectionList.get(j))) {
                                filterSelectionList.remove(j--);
                                count--;
                            }
                        }
                    }
                } else if (FilterList.get(position).getPos() == 1) {
                    for (int i = 0; i < custListArrayList.size(); i++) {
                        filterSelectionList.add(custListArrayList.get(i).getCategory());
                    }

                    int count = filterSelectionList.size();
                    for (int i = 0; i < count; i++) {
                        for (int j = i + 1; j < count; j++) {
                            if (filterSelectionList.get(i).equalsIgnoreCase(filterSelectionList.get(j))) {
                                filterSelectionList.remove(j--);
                                count--;
                            }
                        }
                    }
                }

                holder.rv_filterList.setVisibility(View.VISIBLE);
                arrayAdapter = new ArrayAdapter(context, R.layout.listview_items, filterSelectionList);
                holder.rv_filterList.setAdapter(arrayAdapter);
            }



         /*   dialog = new Dialog(context, R.style.AlertDialogCustom);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.popup_filter_selection);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            ListView rvList = dialog.findViewById(R.id.rv_filter_list);
            ImageView iv_close = dialog.findViewById(R.id.iv_close_popup);

            final PopupFilterAdapter popupAdapter = new PopupFilterAdapter(context, filterSelectionList, holder.getAdapterPosition());
            rvList.setAdapter(popupAdapter);
            popupAdapter.notifyDataSetChanged();*/

            //iv_close.setOnClickListener(view1 -> dialog.dismiss());

          /*  arrayAdapter = new ArrayAdapter(context, R.layout.listview_items, filterSelectionList);
            ListedDoctorFragment.filterList.setAdapter(arrayAdapter);

            ListedDoctorFragment.constraintFilter.setVisibility(View.VISIBLE);*/
        });

        holder.rv_filterList.setOnItemClickListener((adapterView, view, i, l) -> {
            holder.rv_filterList.setVisibility(View.GONE);
            Log.v("fgdfgd", holder.rv_filterList.getItemAtPosition(i).toString() + "---" + holder.getAdapterPosition());
            holder.tv_conditions.setText(holder.rv_filterList.getItemAtPosition(i).toString());
            FilterList.set(holder.getAdapterPosition(), new FilterDataList(holder.rv_filterList.getItemAtPosition(i).toString(), holder.getAdapterPosition()));
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return FilterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_conditions;
        ListView rv_filterList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_conditions = itemView.findViewById(R.id.txt_condition_name);
            rv_filterList = itemView.findViewById(R.id.rv_filter_list);
        }
    }
}
