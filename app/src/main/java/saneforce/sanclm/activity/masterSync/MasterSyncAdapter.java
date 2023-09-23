package saneforce.sanclm.activity.masterSync;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.network.ApiInterface;

public class MasterSyncAdapter extends RecyclerView.Adapter<MasterSyncAdapter.MyViewHolder> {

    Context context;
    ArrayList<MasterSyncItemModel> masterSyncItemModels;
    MasterSyncItemClick masterSyncItemClick;

    public MasterSyncAdapter () {
    }

    public MasterSyncAdapter (ArrayList<MasterSyncItemModel> masterSyncItemModels, Context context, MasterSyncItemClick masterSyncItemClick) {
        this.context = context;
        this.masterSyncItemModels = masterSyncItemModels;
        this.masterSyncItemClick = masterSyncItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.master_sync_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        MasterSyncItemModel data = masterSyncItemModels.get(position);
        String name = data.getName();
        String count = String.valueOf(data.getCount());

        holder.name.setText(name);
        holder.count.setText(count);

        if (data.isPB_visibility()){
            holder.progressBar.setVisibility(View.VISIBLE);
        }else {
            holder.progressBar.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
                masterSyncItemClick.itemClick(data,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount () {
        return masterSyncItemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,count;
        ProgressBar progressBar;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }

}
