package saneforce.sanzen.activity.BusinessEntry.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.BusinessEntry.ModelClass.DoctorBusinessModel;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.GPSTrack;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class AdapterDoctorBusinessProduct extends RecyclerView.Adapter<AdapterDoctorBusinessProduct.ViewHolder> {
    private final Context context;
    private ArrayList<CustList> cusListArrayList;
    private ArrayList<CustList> FillteredList;
    private ArrayList<DoctorBusinessModel> DoctorProductList;
    private CommonUtilsMethods commonUtilsMethods;
    private Activity activity;
    private RoomDB roomDB;
    private OnDoctorClickListener listener;
    private GPSTrack gpsTrack;
    private final MasterDataDao masterDataDao;
    private final String isFrom;

    public AdapterDoctorBusinessProduct(Activity activity, Context context, ArrayList<CustList> cusListArrayList, ArrayList<DoctorBusinessModel> DocBusinessProductDetails, String isFrom, OnDoctorClickListener listener) {
        this.activity = activity;
        this.context = context;
        this.cusListArrayList = cusListArrayList;
        this.FillteredList=cusListArrayList;
        this.isFrom = isFrom;
        this.DoctorProductList=DocBusinessProductDetails;
        roomDB=RoomDB.getDatabase(context);
        masterDataDao=roomDB.masterDataDao();
        gpsTrack = new GPSTrack(activity);
        commonUtilsMethods=new CommonUtilsMethods(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterDoctorBusinessProduct.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_doctorbusiness_list, parent, false);
        return new AdapterDoctorBusinessProduct.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDoctorBusinessProduct.ViewHolder holder, int position) {
        CustList doctor = cusListArrayList.get(position);
        holder.tv_name.setText(cusListArrayList.get(position).getName());
        if(cusListArrayList.get(position).getCategory().isEmpty())
            holder.tv_category.setText("");
        else
            holder.tv_category.setText(cusListArrayList.get(position).getCategory());
        holder.tv_specialist.setText(cusListArrayList.get(position).getSpecialist());
        holder.tv_area.setText(cusListArrayList.get(position).getTown_name());
        holder.tv_value.setText(cusListArrayList.get(position).getTotvalue());

        if (isFrom.equalsIgnoreCase("1") || isFrom.equalsIgnoreCase("4")) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.VISIBLE);
        }
        else if (isFrom.equalsIgnoreCase("2") || isFrom.equalsIgnoreCase("5")) {
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_specialist.setVisibility(View.GONE);
        }
        else {
            holder.tv_category.setVisibility(View.GONE);
            holder.tv_specialist.setVisibility(View.GONE);
        }

        holder.tv_name.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                commonUtilsMethods.displayPopupWindow(context, view, cusListArrayList.get(position).getName());
            }
        });

        holder.constraint_main.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                try {
                    if (listener != null) {
                        listener.onDoctorClicked(doctor, position);
                    }
                } catch (Exception e) {
                    Log.v("Call_Data", "---" + e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cusListArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CustList> filteredNames) {
        this.cusListArrayList = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_category, tv_specialist, tv_area,tv_value;
        public ConstraintLayout constraint_main;
        Button delete;
        View view_top;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.txt_cust_name);
            tv_category = itemView.findViewById(R.id.txt_cat);
            tv_specialist = itemView.findViewById(R.id.txt_specialist);
            tv_area = itemView.findViewById(R.id.txt_address);
            delete = itemView.findViewById(R.id.btn_del);
            tv_value = itemView.findViewById(R.id.txt_value);
            constraint_main = itemView.findViewById(R.id.constraint_main);
            view_top = itemView.findViewById(R.id.view_top);
        }
    }
    public interface OnDoctorClickListener {
        void onDoctorClicked(CustList doctor, int position);
    }
}
