package saneforce.sanclm.activity.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;

public class TaggingAdapter extends RecyclerView.Adapter<TaggingAdapter.ViewHolder> {
    Context context;
    ArrayList<TaggedMapList> taggedMapLists;
   // onItemClickListener onItemClickListener;

   /* public void setOnItemClickListerner(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = (TaggingAdapter.onItemClickListener) onItemClickListener;
    }*/

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

        if (taggedMapLists.get(position).clicked) {
            holder.img_info.setImageDrawable(context.getResources().getDrawable(R.drawable.info_icon_pink));
        } else {
            holder.img_info.setImageDrawable(context.getResources().getDrawable(R.drawable.info_icon_purple));
        }


  /*      holder.constraint_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(taggedMapLists.get(holder.getAdapterPosition()).getCode());
            }
        });*/

        holder.img_info.setOnClickListener(view -> {



          /*  Intent intent = new Intent(context, CustomerProfile.class);
            context.startActivity(intent);*/
        });
    }



    @Override
    public int getItemCount() {
        return taggedMapLists.size();
    }

/*
    public interface onItemClickListener {
        void onClick(String code);
    }
*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_address;
        ImageView img_info;
        ConstraintLayout constraint_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            img_info = itemView.findViewById(R.id.img_info);
            constraint_main = itemView.findViewById(R.id.constraint_list_tagged);
        }
    }
}
