package saneforce.sanzen.activity.reports.dayReport.adapter;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.DynamicSubMenuActivity;
import saneforce.sanzen.activity.reports.ReportFragContainerActivity;
import saneforce.sanzen.activity.reports.dayReport.fragment.DayReportDetailFragment;
import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.storage.SharedPref;

public class DynamicAdapter extends BaseAdapter {

    ArrayList<MenuModel> menuModelArrayList;
    Context context;
    CommonUtilsMethods commonUtilsMethods;

    public DynamicAdapter(ArrayList<MenuModel> menuModelArrayList,Context context){

        this.menuModelArrayList = menuModelArrayList;
        this.context = context;
    }


    @Override
    public int getCount() {
        return menuModelArrayList.size();
    }

    public Object getItem(int position) {
        return menuModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_dynamic_link, null);

            holder = new MyViewHolder();
            holder.cardView = convertView.findViewById(R.id.cardview_grid);
            holder.imageView = convertView.findViewById(R.id.iv_menu_icon);
            holder.textView = convertView.findViewById(R.id.tv_menu_title);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        MenuModel menuModel = menuModelArrayList.get(position);

        if (!menuModel.getMenu_Icon().equalsIgnoreCase("")) {
            Glide.with(context).load(menuModel.getMenu_Icon()).into(holder.imageView);
        }else{
            holder.imageView.setImageResource(R.drawable.web_icon);
        }

        holder.textView.setText(menuModel.getMenu_Name());
        commonUtilsMethods = new CommonUtilsMethods(context);

        holder.cardView.setOnClickListener(v -> {

                Intent intent = new Intent(context, DynamicSubMenuActivity.class);
                intent.putExtra("title",menuModel.getMenu_Name());
                String Data = String.valueOf(menuModel.getMenu_Sub_Details());
                intent.putExtra("menu_sub_details",Data);
                context.startActivity(intent);

        });
        return convertView;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static class MyViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView textView;
    }
}
