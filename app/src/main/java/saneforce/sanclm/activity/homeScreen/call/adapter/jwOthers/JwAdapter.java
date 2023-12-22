package saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.JointWorkSelectionSide;
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
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data, parent, false);
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
            holder.checkBox.setChecked(true);
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
        } else {
            holder.checkBox.setChecked(false);
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (holder.checkBox.isPressed()) {
                if (holder.checkBox.isChecked()) {
                    holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
                    holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
                    jwLists.get(position).setCheckedItem(true);
                    JointWorkSelectionSide.JwList.get(position).setCheckedItem(true);
                } else {
                    holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
                    holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
                    jwLists.get(position).setCheckedItem(false);
                    JointWorkSelectionSide.JwList.get(position).setCheckedItem(false);
                    for (int j = 0; j < JWOthersFragment.callAddedJointList.size(); j++) {
                        if (JWOthersFragment.callAddedJointList.get(j).getCode().equalsIgnoreCase(jwLists.get(position).getCode())) {
                            JWOthersFragment.callAddedJointList.remove(j);
                            j--;
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
    public void filterList(ArrayList<CallCommonCheckedList> filteredNames) {
        this.jwLists = filteredNames;
        notifyDataSetChanged();
    }

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
