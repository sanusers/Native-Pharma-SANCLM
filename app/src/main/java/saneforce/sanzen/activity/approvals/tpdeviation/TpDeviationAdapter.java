package saneforce.sanzen.activity.approvals.tpdeviation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;

public class TpDeviationAdapter extends RecyclerView.Adapter<TpDeviationAdapter.ViewHolder> {
    Context context;
    ArrayList<TpDeviationModelList> tpDeviationModelLists;
    JSONObject jsonTpDeviation = new JSONObject();
    ApiInterface api_interface;
    ProgressDialog progressDialog = null;
CommonUtilsMethods commonUtilsMethods;
    public TpDeviationAdapter(Context context, ArrayList<TpDeviationModelList> tpDeviationModelLists) {
        this.context = context;
        this.tpDeviationModelLists = tpDeviationModelLists;
        commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public TpDeviationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tpdeviation_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TpDeviationAdapter.ViewHolder holder, int position) {
        api_interface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        holder.tv_Name.setText(tpDeviationModelLists.get(position).getSfName());
        holder.tv_date.setText(tpDeviationModelLists.get(position).getDate());
        holder.tv_deviationRemarks.setText(tpDeviationModelLists.get(position).getDeviationRemarks());

        holder.btn_approve.setOnClickListener(view -> CallApprovedRejectTpDeviation(tpDeviationModelLists.get(position).getSfName(), tpDeviationModelLists.get(position).getSfCode(), tpDeviationModelLists.get(position).getSlNo(), holder.getBindingAdapterPosition(), "4"));
        holder.btn_reject.setOnClickListener(view -> CallApprovedRejectTpDeviation(tpDeviationModelLists.get(position).getSfName(), tpDeviationModelLists.get(position).getSfCode(), tpDeviationModelLists.get(position).getSlNo(), holder.getBindingAdapterPosition(), "3"));
    }

    private void CallApprovedRejectTpDeviation(String sfName, String sfCode, String slNo, int position, String status) {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        try {
            jsonTpDeviation.put("tableName", "savedev_appr");
            jsonTpDeviation.put("slno", slNo);
            jsonTpDeviation.put("status", status);
            jsonTpDeviation.put("sfcode", sfCode);
            jsonTpDeviation.put("sfname", sfName);
            jsonTpDeviation.put("division_code", SharedPref.getDivisionCode(context).replace(",", "").trim());
            jsonTpDeviation.put("Rsf", SharedPref.getHqCode(context));
            jsonTpDeviation.put("sf_type", SharedPref.getSfType(context));
            jsonTpDeviation.put("Designation", SharedPref.getDesig(context));
            jsonTpDeviation.put("state_code", SharedPref.getStateCode(context));
            jsonTpDeviation.put("subdivision_code", SharedPref.getSubdivisionCode(context));
            jsonTpDeviation.put("Mode", "");
            Log.v("json_approve_tpDev", jsonTpDeviation.toString());
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");
        Call<JsonElement> callApprovedTpDev = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonTpDeviation.toString());

        callApprovedTpDev.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            if (status.equalsIgnoreCase("4")) {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.approved_successfully));
                            } else {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.rejected_successfully));
                            }
                            removeAt(position);
                            ApprovalsActivity.DeviationCount--;
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
            }
        });
    }

    public void removeAt(int position) {
        tpDeviationModelLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tpDeviationModelLists.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<TpDeviationModelList> filteredNames) {
        this.tpDeviationModelLists = filteredNames;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tpDeviationModelLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Name, tv_date, tv_deviationRemarks;
        Button btn_reject, btn_approve;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_Name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_deviationRemarks = itemView.findViewById(R.id.tv_deviation_remarks);
            btn_reject = itemView.findViewById(R.id.btn_reject);
            btn_approve = itemView.findViewById(R.id.btn_approved);
        }
    }
}