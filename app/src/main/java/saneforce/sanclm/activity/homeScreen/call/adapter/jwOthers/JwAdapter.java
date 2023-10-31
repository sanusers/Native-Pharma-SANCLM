package saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.JointworkSelectionSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;

public class JwAdapter extends RecyclerView.Adapter<JwAdapter.ViewHolder> {
    ArrayList<CallCommonCheckedList> jwLists;
    Context context;

    public JwAdapter(Context context, ArrayList<CallCommonCheckedList> jwLists) {
        this.context = context;
        this.jwLists = jwLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data_inp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        for (int j = 0; j < JWOthersFragment.callAddedJointList.size(); j++) {
            if (JWOthersFragment.callAddedJointList.get(j).getCode().equalsIgnoreCase(jwLists.get(position).getCode())) {
                jwLists.set(position, new CallCommonCheckedList(jwLists.get(position).getName(), jwLists.get(position).getCode(), true));
            }
        }

        holder.tv_name.setText(jwLists.get(position).getName());
        holder.checkBox.setChecked(jwLists.get(position).isCheckedItem());

        if (holder.checkBox.isChecked()) {
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
            }
        } else {
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
            }
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (holder.checkBox.isPressed()) {
                if (holder.checkBox.isChecked()) {
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                    }
                    jwLists.get(position).setCheckedItem(true);
                    JointworkSelectionSide.JwList.get(position).setCheckedItem(true);
                } else {
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                    }
                    jwLists.get(position).setCheckedItem(false);
                    JointworkSelectionSide.JwList.get(position).setCheckedItem(false);
                    for (int j = 0; j < JWOthersFragment.callAddedJointList.size(); j++) {
                        if (JWOthersFragment.callAddedJointList.get(j).getCode().equalsIgnoreCase(jwLists.get(position).getCode())) {
                            JWOthersFragment.callAddedJointList.remove(j);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return jwLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CallCommonCheckedList> filterdNames) {
        Log.v("sasas", String.valueOf(filterdNames));
        this.jwLists = filterdNames;
        notifyDataSetChanged();
    }

   /* @Override
    public Filter getFilter() {
        return filter;
       *//* if (filter == null) {
            filter = new MyFilter(this, getElements());
        }
        return filter;*//*
    }*/

    /*private static class MyFilter extends Filter {
        private final JwAdapter adapter;
        private final List<Object> originalList;
        private final List<Object> filteredList;

        private MyFilter(JwAdapter adapter, List<Object> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<Object>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Object item : originalList) {
                    if (item.toLowerCase().contains(filterPattern) {
                        filteredList.add(item);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mDataset.clear();
            mDataset.add(filterResults.values);
            adapter.notifyDataSetChanged();
        }
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_data_name);
            checkBox = itemView.findViewById(R.id.chk_box);
        }
    }
}
