package saneforce.sanzen.activity.reports.dayReport.adapter;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.DynamicWebActivity;
import saneforce.sanzen.activity.reports.ReportWebActivity;
import saneforce.sanzen.activity.reports.dayReport.model.SubMenuModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class DynamicSubMenuAdapter  extends RecyclerView.Adapter<DynamicSubMenuAdapter.ViewHolder>{

    public ArrayList<SubMenuModel> subMenuModelArrayList = new ArrayList<>();
    Context context;
    CommonUtilsMethods commonUtilsMethods;

    public DynamicSubMenuAdapter(ArrayList<SubMenuModel> subMenuModelArrayList, Context context) {
        this.subMenuModelArrayList = subMenuModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dynamic_submenu_webview, parent, false);
        commonUtilsMethods = new CommonUtilsMethods(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubMenuModel menuSubModel = subMenuModelArrayList.get(position);

        holder.titleTextView.setText(menuSubModel.getMenuName());
        holder.itemView.setOnClickListener(v -> {
            if(isNetworkConnected()){
                Intent intent = new Intent(holder.itemView.getContext(), DynamicWebActivity.class);
                intent.putExtra("url", menuSubModel.getMenuLink());
                intent.putExtra("title", menuSubModel.getMenuName());
                holder.itemView.getContext().startActivity(intent);
            }else {
                commonUtilsMethods.showToastMessage(context, String.valueOf((R.string.no_network)));            }
        });
    }

    @Override
    public int getItemCount() {
        return subMenuModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_title);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
