package saneforce.santrip.activity.approvals.geotagging;

import static saneforce.santrip.activity.approvals.ApprovalsActivity.CIPCaption;
import static saneforce.santrip.activity.approvals.ApprovalsActivity.ChemistCaption;
import static saneforce.santrip.activity.approvals.ApprovalsActivity.DrCaption;
import static saneforce.santrip.activity.approvals.ApprovalsActivity.HosCaption;
import static saneforce.santrip.activity.approvals.ApprovalsActivity.StockistCaption;
import static saneforce.santrip.activity.approvals.ApprovalsActivity.UnDrCaption;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.ApprovalsActivity;
import saneforce.santrip.activity.map.MapsActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SharedPref;

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
                holder.tv_cust_mode.setText(DrCaption);
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_dr_img));
                break;
            case "C":
                holder.tv_cust_mode.setText(ChemistCaption);
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_chemist_img));
                break;
            case "S":
                holder.tv_cust_mode.setText(StockistCaption);
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_stockist_img));
                break;
            case "U":
                holder.tv_cust_mode.setText(UnDrCaption);
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_unlistdr_img));
                break;
            case "H":
                holder.tv_cust_mode.setText(HosCaption);
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_cip_img));
                break;
            case "CIP":
                holder.tv_cust_mode.setText(CIPCaption);
                holder.img_cust.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_cip_img));
                break;
        }
        holder.tag_view.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("from", "view_tag_approval");
            geoTagViewList.clear();
            geoTagViewList.add(new GeoTaggingModelList(geoTaggingModelLists.get(position).getName(), geoTaggingModelLists.get(position).getLatitude(), geoTaggingModelLists.get(position).getLongitude(), geoTaggingModelLists.get(position).getAddress()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.btn_approved.setOnClickListener(v -> CallApi("0", geoTaggingModelLists.get(position).getMapId(), geoTaggingModelLists.get(position).getCust_mode(), geoTaggingModelLists.get(position).getCode(), geoTaggingModelLists.get(position).getHqCode(), holder.getBindingAdapterPosition()));
        holder.btn_rejected.setOnClickListener(v -> CallApi("2", geoTaggingModelLists.get(position).getMapId(), geoTaggingModelLists.get(position).getCust_mode(), geoTaggingModelLists.get(position).getCode(), geoTaggingModelLists.get(position).getHqCode(), holder.getBindingAdapterPosition()));

    }

    private void CallApi(String Status, String MapId, String CustMode, String CustId, String hqCode, int Position) {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        try {
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
            jsonGeoTag.put("sfcode", GeoTaggingActivity.SfCode);
            jsonGeoTag.put("sfname", GeoTaggingActivity.SfName);
            jsonGeoTag.put("division_code", GeoTaggingActivity.DivCode);
            jsonGeoTag.put("Rsf", hqCode);
            jsonGeoTag.put("sf_type", GeoTaggingActivity.SfType);
            jsonGeoTag.put("Designation", GeoTaggingActivity.Designation);
            jsonGeoTag.put("state_code", GeoTaggingActivity.StateCode);
            jsonGeoTag.put("subdivision_code", GeoTaggingActivity.SubDivisionCode);
            jsonGeoTag.put("Mode", Constants.APP_MODE);
            Log.v("json_getGeoTag", jsonGeoTag.toString());
        } catch (Exception ignored) {

        }

        Call<JsonObject> callGeoTag;
        callGeoTag = api_interface.SendGeoTagApprovalReject(jsonGeoTag.toString());

        callGeoTag.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            if (Status.equalsIgnoreCase("0")) {
                                commonUtilsMethods.ShowToast(context,context.getString(R.string.approved_successfully),100);
                            } else {
                                commonUtilsMethods.ShowToast(context,context.getString(R.string.rejected_successfully),100);
                            }
                            removeAt(Position);
                            ApprovalsActivity.GeoTagCount--;
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.ShowToast(context,context.getString(R.string.toast_response_failed),100);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.ShowToast(context,context.getString(R.string.toast_response_failed),100);
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
        }
    }
}
