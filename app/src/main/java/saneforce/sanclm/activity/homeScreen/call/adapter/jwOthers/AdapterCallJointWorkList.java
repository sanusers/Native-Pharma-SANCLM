package saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers;

import static saneforce.sanclm.activity.homeScreen.call.fragments.JointworkSelectionSide.jwAdapter;

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

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.fragments.JointworkSelectionSide;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

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
                for (int j = 0; j < JointworkSelectionSide.JwList.size(); j++) {
                    if (JointworkSelectionSide.JwList.get(j).getCode().equalsIgnoreCase(jwAddedList.get(position).getCode())) {
                        JointworkSelectionSide.JwList.set(j, new CallCommonCheckedList(JointworkSelectionSide.JwList.get(j).getName(), JointworkSelectionSide.JwList.get(j).getCode(), false));
                    }
                }
            } catch (Exception ignored) {
            }

            jwAdapter = new JwAdapter(activity, JointworkSelectionSide.JwList);
            commonUtilsMethods.recycleTestWithDivider(JointworkSelectionSide.selectJwSideBinding.rvJwList);
            JointworkSelectionSide.selectJwSideBinding.rvJwList.setAdapter(jwAdapter);
            jwAdapter.notifyDataSetChanged();
            removeAt(holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return jwAddedList.size();
    }

    public void removeAt(int position) {
        jwAddedList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, jwAddedList.size());
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
