package saneforce.sanclm.activity.map;

import static saneforce.sanclm.activity.map.MapsActivity.taggedMapListArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import saneforce.sanclm.R;

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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tv_name.setText(taggedMapLists.get(position).getName());
        holder.tv_address.setText(taggedMapLists.get(position).getAddr());
        //holder.tv_meters.setText(String.format("%s Meter", Math.round(taggedMapLists.get(position).getMeters())));
        holder.tv_meters.setText(String.format("%s Meter", taggedMapLists.get(position).getMeters()));

       // Collections.sort(taggedMapLists, Comparator.comparingDouble(TaggedMapList::getMeters));

        if (taggedMapLists.get(position).clicked) {
            holder.img_info.setImageDrawable(context.getResources().getDrawable(R.drawable.info_icon_pink));
        } else {
            holder.img_info.setImageDrawable(context.getResources().getDrawable(R.drawable.info_icon_purple));
        }

        holder.img_info.setOnClickListener(view -> {
        });
    }


    @Override
    public int getItemCount() {
        return taggedMapLists.size();
    }

    public int getItemPosition(String code) {
        for (int i = 0; i < taggedMapListArrayList.size(); i++) {
            if (taggedMapListArrayList.get(i).getCode().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_address, tv_meters;
        ImageView img_info;
        ConstraintLayout constraint_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_meters = itemView.findViewById(R.id.tv_meters);
            img_info = itemView.findViewById(R.id.img_info);
            constraint_main = itemView.findViewById(R.id.constraint_list_tagged);
        }
    }
}
