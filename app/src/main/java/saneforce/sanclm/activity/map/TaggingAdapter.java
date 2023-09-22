package saneforce.sanclm.activity.map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.profile.CustomerProfile;

public class TaggingAdapter extends RecyclerView.Adapter<TaggingAdapter.ViewHolder> {
    Context context;
    ArrayList<TaggedMapList> taggedMapLists;

    public TaggingAdapter(Context context, ArrayList<TaggedMapList> taggedMapLists) {
        this.context = context;
        this.taggedMapLists = taggedMapLists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tag_map, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(taggedMapLists.get(position).getName());
        holder.tv_address.setText(taggedMapLists.get(position).getAddr());

        holder.img_info.setOnClickListener(view -> {
            Intent intent = new Intent(context, CustomerProfile.class);
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return taggedMapLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_address;
        ImageView img_info;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            img_info = itemView.findViewById(R.id.img_info);
        }
    }
}
