package saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers;

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
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;

public class AdapterCallJointWorkList extends RecyclerView.Adapter<AdapterCallJointWorkList.ViewHolder> {
    Context context;
    ArrayList<CallCommonCheckedList> jointworkAddedList;


    public AdapterCallJointWorkList(Context context, ArrayList<CallCommonCheckedList> jointworkAddedList) {
        this.context = context;
        this.jointworkAddedList = jointworkAddedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_jointwork, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_jw_name.setText(jointworkAddedList.get(position).getName());
        holder.img_del.setOnClickListener(view -> removeAt(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return jointworkAddedList.size();
    }

    public void removeAt(int position) {
        jointworkAddedList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, jointworkAddedList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_jw_name;
        ImageView img_del;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_jw_name = itemView.findViewById(R.id.tv_jointwork_name);
            img_del = itemView.findViewById(R.id.img_del_jw);
        }
    }
}
