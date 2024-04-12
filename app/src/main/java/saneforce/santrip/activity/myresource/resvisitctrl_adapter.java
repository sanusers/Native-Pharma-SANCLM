package saneforce.santrip.activity.myresource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;

public class resvisitctrl_adapter extends RecyclerView.Adapter<resvisitctrl_adapter.MyViewHolder> {

    ArrayList<Resourcemodel_class> resList;
    Context context;
    String identity;
    TextView vstcount;
    String vst_count = "",vst_cat = "",vst_town = "";

    public resvisitctrl_adapter(Context context,ArrayList<Resourcemodel_class> resList) {
        this.context = context;
        this.resList = resList;

    }



    @NonNull
    @Override
    public resvisitctrl_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visitcntrl_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Resourcemodel_class app_list = resList.get(position);

            Log.d("visit_lited",app_list.getCust_name()+"==1"+app_list.getVisit_date()+"---2"+app_list.getCust_id()+"--3"+app_list.getMax_count());
            String vdd = String.valueOf(position+1);
            holder.tv_dot.setText(vdd+" )");
            holder.tv_name.setText(app_list.getCust_name());
//       Res_sidescreenAdapter.visit_dt.setText(resList.size()+"/3-Visit");
            holder.tv_date.setText(app_list.getVisit_date());
    }

    @Override
    public int getItemCount() {
        return resList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_date,tv_dot;

        @SuppressLint("ResourceAsColor")
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_cusName);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_dot = itemView.findViewById(R.id.tv_dot);
        }
    }
}
