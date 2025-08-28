package saneforce.sanzen.activity.reports.dayReport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
import saneforce.sanzen.storage.SharedPref;

public  class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.MyViewHolder> {
    ArrayList<String> arrayList;
    ArrayList<MenuModel> menuModelArrayList;
    Context context;

    public DynamicAdapter(ArrayList<String> arrayList,ArrayList<MenuModel> menuModelArrayList,Context context){
        this.arrayList = arrayList;
        this.menuModelArrayList = menuModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DynamicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dynamic_link, parent, false);
        return new DynamicAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicAdapter.MyViewHolder holder, int position) {
        String menu_Name = menuModelArrayList.get(position).getMenu_Name();
        String img_Name = menuModelArrayList.get(position).getMenu_Icon();
        holder.textView.setText(menu_Name);
        String url = SharedPref.getTagImageUrl(context)+img_Name;

        Picasso.get()
                .load(url)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.menu_name);
        }
    }
}
