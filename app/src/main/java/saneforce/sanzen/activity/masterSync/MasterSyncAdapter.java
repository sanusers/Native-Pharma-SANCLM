package saneforce.sanzen.activity.masterSync;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;

public class MasterSyncAdapter extends RecyclerView.Adapter<MasterSyncAdapter.MyViewHolder> {

    Context context;
    ArrayList<MasterSyncItemModel> masterSyncItemModels;
    MasterSyncItemClick masterSyncItemClick;

    public MasterSyncAdapter() {
    }

    public MasterSyncAdapter(ArrayList<MasterSyncItemModel> masterSyncItemModels, Context context, MasterSyncItemClick masterSyncItemClick) {
        this.context = context;
        this.masterSyncItemModels = masterSyncItemModels;
        this.masterSyncItemClick = masterSyncItemClick;
        Log.v("masterCheck", "--222-" + masterSyncItemModels.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.master_sync_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MasterSyncItemModel data = masterSyncItemModels.get(position);
        String name = data.getName();
        String count = String.valueOf(data.getCount());
        Log.v("masterCheck", "---" + data.getName());
        holder.name.setText(name);
        holder.count.setText(count);

        if (count.equals("-1")) {
            holder.count.setText("");
        }


        if (data.isPBarVisibility()) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.syncErrorIcon.setVisibility(View.GONE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.syncErrorIcon.setVisibility(View.VISIBLE);
            if (data.syncSuccess == 1)
                holder.syncErrorIcon.setImageResource(R.drawable.icon_sync_failed);
            else if (data.syncSuccess == 2) holder.syncErrorIcon.setImageResource(R.drawable.done);
            else holder.syncErrorIcon.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(view -> {

            holder.progressBar.setVisibility(View.VISIBLE);
            holder.syncErrorIcon.setVisibility(View.GONE);
            masterSyncItemClick.itemClick(data, holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return masterSyncItemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, count;
        ProgressBar progressBar;
        ImageView syncErrorIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
            progressBar = itemView.findViewById(R.id.progressBar);
            syncErrorIcon = itemView.findViewById(R.id.syncFailedImage);
        }
    }
}
