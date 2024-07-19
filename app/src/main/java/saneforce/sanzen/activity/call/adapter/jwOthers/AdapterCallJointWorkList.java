package saneforce.sanzen.activity.call.adapter.jwOthers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.fragments.jwOthers.JointWorkSelectionSide;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class AdapterCallJointWorkList extends RecyclerView.Adapter<AdapterCallJointWorkList.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CallCommonCheckedList> jwAddedList;
    ArrayList<CallCommonCheckedList> jwSelectionList;
    CommonUtilsMethods commonUtilsMethods;


    public AdapterCallJointWorkList(Context context, Activity activity, ArrayList<CallCommonCheckedList> jwAddedList, ArrayList<CallCommonCheckedList> jwSelectionList) {
        this.context = context;
        this.activity = activity;
        this.jwAddedList = jwAddedList;
        this.jwSelectionList = jwSelectionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_jointwork, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_jw_name.setText(jwAddedList.get(position).getName());

        holder.img_del.setOnClickListener(view -> {
            try {
                for (int j = 0; j < JointWorkSelectionSide.JwList.size(); j++) {
                    if (JointWorkSelectionSide.JwList.get(j).getCode().equalsIgnoreCase(jwAddedList.get(position).getCode())) {
                        JointWorkSelectionSide.JwList.set(j, new CallCommonCheckedList(JointWorkSelectionSide.JwList.get(j).getName(), JointWorkSelectionSide.JwList.get(j).getCode(), false));
                    }
                }
            } catch (Exception ignored) {
            }

            JointWorkSelectionSide.jwAdapter = new JwAdapter(activity, JointWorkSelectionSide.JwList);
            commonUtilsMethods.recycleTestWithDivider(JointWorkSelectionSide.selectJwSideBinding.rvJwList);
            JointWorkSelectionSide.selectJwSideBinding.rvJwList.setAdapter(JointWorkSelectionSide.jwAdapter);
            JointWorkSelectionSide.jwAdapter.notifyDataSetChanged();
            removeAt(holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return jwAddedList.size();
    }

    public void removeAt(int position) {
        if(jwAddedList.size() > position) {
            jwAddedList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, jwAddedList.size());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_jw_name;
        ImageView img_del;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_jw_name = itemView.findViewById(R.id.tv_jointwork_name);
            img_del = itemView.findViewById(R.id.img_del_jw);
        }
    }
}
