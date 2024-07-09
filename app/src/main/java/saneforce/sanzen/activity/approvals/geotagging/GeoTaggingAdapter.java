package saneforce.sanzen.activity.approvals.geotagging;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.activity.map.MapsActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;

public class GeoTaggingAdapter extends RecyclerView.Adapter<GeoTaggingAdapter.ViewHolder> {
    public static ArrayList<GeoTaggingModelList> geoTagViewList = new ArrayList<>();
    Context context;
    ArrayList<GeoTaggingModelList> geoTaggingModelLists;
    ApiInterface api_interface;
    ProgressDialog progressDialog = null;
    JSONObject jsonGeoTag = new JSONObject();
    CommonUtilsMethods commonUtilsMethods;

    public GeoTaggingAdapter(Context context, ArrayList<GeoTaggingModelList> geoTaggingModelLists) {
        this.context = context;
        this.geoTaggingModelLists = geoTaggingModelLists;
        commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public GeoTaggingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_geotagging, parent, false);
        return new GeoTaggingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GeoTaggingAdapter.ViewHolder holder, int position) {
        api_interface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        holder.tv_name.setText(geoTaggingModelLists.get(position).getName());
        holder.tv_hq.setText(geoTaggingModelLists.get(position).getHqName());
        holder.tv_cluster.setText(geoTaggingModelLists.get(position).getCluster());
        holder.tv_address.setText(geoTaggingModelLists.get(position).getAddress());
        holder.tv_date_time.setText(geoTaggingModelLists.get(position).getDate_time());

        switch (geoTaggingModelLists.get(position).getCust_mode()) {
            case "D":
                holder.tv_cust_mode.setText(SharedPref.getDrCap(context));
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_dr_img));
                break;
            case "C":
                holder.tv_cust_mode.setText(SharedPref.getChmCap(context));
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_chemist_img));
                break;
            case "S":
                holder.tv_cust_mode.setText(SharedPref.getStkCap(context));
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_stockist_img));
                break;
            case "U":
                holder.tv_cust_mode.setText(SharedPref.getUNLcap(context));
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_unlistdr_img));
                break;
            case "H":
                holder.tv_cust_mode.setText(SharedPref.getHospCaption(context));
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_cip_img));
                break;
            case "CIP":
                holder.tv_cust_mode.setText(SharedPref.getCipCaption(context));
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_cip_img));
                break;
        }
        holder.tag_view.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("from", "view_tag_approval");
            geoTagViewList.clear();
            geoTagViewList.add(new GeoTaggingModelList(geoTaggingModelLists.get(position).getName(), geoTaggingModelLists.get(position).getLatitude(), geoTaggingModelLists.get(position).getLongitude(), geoTaggingModelLists.get(position).getAddress()));
         //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.btn_approved.setOnClickListener(v -> {
            if (UtilityClass.isNetworkAvailable(context)){
                CallApi("0", geoTaggingModelLists.get(position).getMapId(), geoTaggingModelLists.get(position).getCust_mode(), geoTaggingModelLists.get(position).getCode(), geoTaggingModelLists.get(position).getHqCode(), holder.getBindingAdapterPosition(),holder);
            }else {
                Toast.makeText(context,"Please check a Internet connection",Toast.LENGTH_SHORT).show();
            }
        });
        holder.btn_rejected.setOnClickListener(v -> {
            if (UtilityClass.isNetworkAvailable(context)) {
                CallApi("2", geoTaggingModelLists.get(position).getMapId(), geoTaggingModelLists.get(position).getCust_mode(), geoTaggingModelLists.get(position).getCode(), geoTaggingModelLists.get(position).getHqCode(), holder.getBindingAdapterPosition(), holder);
            }else {
                Toast.makeText(context,"Please check a Internet connection",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void CallApi(String Status, String MapId, String CustMode, String CustId, String hqCode, int Position,ViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);

        try {
            jsonGeoTag=CommonUtilsMethods.CommonObjectParameter(context);
            jsonGeoTag.put("tableName", "savegeo_appr");
            switch (CustMode) {
                case "D":
                    jsonGeoTag.put("doctor_mapId", MapId);
                    jsonGeoTag.put("docId", CustId);
                    jsonGeoTag.put("chemist_mapId", "");
                    jsonGeoTag.put("chemistId", "");
                    jsonGeoTag.put("stockiest_mapId", "");
                    jsonGeoTag.put("stockiestId", "");
                    jsonGeoTag.put("unlisted_doctor_mapId", "");
                    jsonGeoTag.put("unlisted_doctorId", "");
                    jsonGeoTag.put("hospital_mapId", "");
                    jsonGeoTag.put("hospitalId", "");
                    break;
                case "C":
                    jsonGeoTag.put("doctor_mapId", "");
                    jsonGeoTag.put("docId", "");
                    jsonGeoTag.put("chemist_mapId", MapId);
                    jsonGeoTag.put("chemistId", CustId);
                    jsonGeoTag.put("stockiest_mapId", "");
                    jsonGeoTag.put("stockiestId", "");
                    jsonGeoTag.put("unlisted_doctor_mapId", "");
                    jsonGeoTag.put("unlisted_doctorId", "");
                    jsonGeoTag.put("hospital_mapId", "");
                    jsonGeoTag.put("hospitalId", "");
                    break;
                case "S":
                    jsonGeoTag.put("doctor_mapId", "");
                    jsonGeoTag.put("docId", "");
                    jsonGeoTag.put("chemist_mapId", "");
                    jsonGeoTag.put("chemistId", "");
                    jsonGeoTag.put("stockiest_mapId", MapId);
                    jsonGeoTag.put("stockiestId", CustId);
                    jsonGeoTag.put("unlisted_doctor_mapId", "");
                    jsonGeoTag.put("unlisted_doctorId", "");
                    jsonGeoTag.put("hospital_mapId", "");
                    jsonGeoTag.put("hospitalId", "");
                    break;
                case "U":
                    jsonGeoTag.put("doctor_mapId", "");
                    jsonGeoTag.put("docId", "");
                    jsonGeoTag.put("chemist_mapId", "");
                    jsonGeoTag.put("chemistId", "");
                    jsonGeoTag.put("stockiest_mapId", "");
                    jsonGeoTag.put("stockiestId", "");
                    jsonGeoTag.put("unlisted_doctor_mapId", MapId);
                    jsonGeoTag.put("unlisted_doctorId", CustId);
                    jsonGeoTag.put("hospital_mapId", "");
                    jsonGeoTag.put("hospitalId", "");
                    break;
                case "H":
                    jsonGeoTag.put("doctor_mapId", "");
                    jsonGeoTag.put("docId", "");
                    jsonGeoTag.put("chemist_mapId", "");
                    jsonGeoTag.put("chemistId", "");
                    jsonGeoTag.put("stockiest_mapId", "");
                    jsonGeoTag.put("stockiestId", "");
                    jsonGeoTag.put("unlisted_doctor_mapId", "");
                    jsonGeoTag.put("unlisted_doctorId", "");
                    jsonGeoTag.put("hospital_mapId", MapId);
                    jsonGeoTag.put("hospitalId", CustId);
                    break;
            }

            jsonGeoTag.put("status", Status);
            jsonGeoTag.put("sfcode", SharedPref.getSfCode(context));
            jsonGeoTag.put("division_code",SharedPref.getDivisionCode(context));
            jsonGeoTag.put("Rsf", hqCode);

            Log.v("json_getGeoTag", jsonGeoTag.toString());
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");
        Call<JsonElement> callGeoTag = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonGeoTag.toString());

        callGeoTag.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    holder.progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        boolean isSuccess = Boolean.valueOf(jsonSaveRes.getString("success"));
                        if (isSuccess) {
                            removeAt(Position);
                            ApprovalsActivity.GeoTagCount--;
                            if (Status.equalsIgnoreCase("0")) {
                                Toast.makeText(context,"Approved Successfully",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,"Rejected Successfully",Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    holder.progressBar.setVisibility(View.GONE);
                    Toast.makeText(context,"Response Failed! Please Try Again",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                holder.progressBar.setVisibility(View.GONE);
                Toast.makeText(context,"Response Failed! Please Try Again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return geoTaggingModelLists.size();
    }

    public void removeAt(int position) {
        geoTaggingModelLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, geoTaggingModelLists.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<GeoTaggingModelList> filteredNames) {
        this.geoTaggingModelLists = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_hq, tv_cluster, tv_address, tv_cust_mode, tv_date_time, tag_view;
        Button btn_approved, btn_rejected;
        ImageView img_cust;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_hq = itemView.findViewById(R.id.tv_hq);
            img_cust = itemView.findViewById(R.id.img_cust);
            tv_cluster = itemView.findViewById(R.id.tv_cluster_geo);
            tv_address = itemView.findViewById(R.id.tv_address_geo);
            tv_cust_mode = itemView.findViewById(R.id.tv_cust_mode);
            btn_approved = itemView.findViewById(R.id.btn_approved);
            btn_rejected = itemView.findViewById(R.id.btn_reject);
            tag_view = itemView.findViewById(R.id.tag_view);
            tv_date_time = itemView.findViewById(R.id.tv_date_time);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
