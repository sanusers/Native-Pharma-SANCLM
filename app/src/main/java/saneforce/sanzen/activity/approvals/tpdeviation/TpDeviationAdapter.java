package saneforce.sanzen.activity.approvals.tpdeviation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class TpDeviationAdapter extends RecyclerView.Adapter<TpDeviationAdapter.ViewHolder> {
    Context context;
    ArrayList<TpDeviationModelList> tpDeviationModelLists;
    JSONObject jsonTpDeviation = new JSONObject();
    ApiInterface api_interface;
    ProgressDialog progressDialog = null;
    CommonUtilsMethods commonUtilsMethods;
    private ViewPlanClickListener viewPlanClickListener;

    public interface ViewPlanClickListener{
        public void onClick(TpDeviationModelList tpDeviationModelList);
    }

    public TpDeviationAdapter(Context context, ArrayList<TpDeviationModelList> tpDeviationModelLists, ViewPlanClickListener viewPlanClickListener) {
        this.context = context;
        this.tpDeviationModelLists = tpDeviationModelLists;
        commonUtilsMethods = new CommonUtilsMethods(context);
        this.viewPlanClickListener = viewPlanClickListener;
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
        holder.tv_date.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_19, tpDeviationModelLists.get(position).getDate()));
        holder.tv_deviationRemarks.setText(tpDeviationModelLists.get(position).getDeviationRemarks());
        holder.tv_view_plan.setOnClickListener(view -> {
            viewPlanClickListener.onClick(tpDeviationModelLists.get(position));
        });

        holder.btn_approve.setOnClickListener(view -> CallApprovedTpDeviation(tpDeviationModelLists.get(position).getSfName(), tpDeviationModelLists.get(position).getSfCode(), tpDeviationModelLists.get(position).getSlNo(), holder.getBindingAdapterPosition(), tpDeviationModelLists.get(position).getDate(), "4"));
        holder.btn_reject.setOnClickListener(view -> showRemarksAlert(tpDeviationModelLists.get(position).getSfName(), tpDeviationModelLists.get(position).getSfCode(), tpDeviationModelLists.get(position).getSlNo(), holder.getBindingAdapterPosition(), tpDeviationModelLists.get(position).getDate(), "2"));
    }

    private void showRemarksAlert(String sfName, String sfCode, String slNo, int position, String date, String status) {
        Dialog dialogReject = new Dialog(context);
        dialogReject.setContentView(R.layout.popup_reject);
        Objects.requireNonNull(dialogReject.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReject.setCancelable(false);

        ImageView iv_close = dialogReject.findViewById(R.id.img_close);
        EditText ed_reason = dialogReject.findViewById(R.id.ed_reason_reject);
        Button btn_cancel = dialogReject.findViewById(R.id.btn_cancel);
        Button btn_reject = dialogReject.findViewById(R.id.btn_reject);
        ed_reason.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_reason)});
        btn_cancel.setOnClickListener(view1 -> {
            ed_reason.setText("");
            dialogReject.dismiss();
        });

        iv_close.setOnClickListener(view12 -> {
            ed_reason.setText("");
            dialogReject.dismiss();
        });

        btn_reject.setOnClickListener(view13 -> {
            if(!TextUtils.isEmpty(ed_reason.getText().toString())) {
                CallRejectedTpDeviation(sfName, sfCode, slNo, position, status, date, ed_reason.getText().toString());
            }else {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_enter_reason_for_reject));
            }
            dialogReject.dismiss();
        });
        dialogReject.show();
    }

    private void CallApprovedTpDeviation(String sfName, String sfCode, String slNo, int position, String date, String status) {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        try {
            jsonTpDeviation=CommonUtilsMethods.CommonObjectParameter(context);
            jsonTpDeviation.put("tableName", "savedev_appr");
            jsonTpDeviation.put("slno", slNo);
            jsonTpDeviation.put("status", status);
            jsonTpDeviation.put("sfcode", sfCode);
            jsonTpDeviation.put("sfname", sfName);
            jsonTpDeviation.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_15, date));
            jsonTpDeviation.put("division_code", SharedPref.getDivisionCode(context).replace(",", "").trim());
            jsonTpDeviation.put("Rsf", SharedPref.getHqCode(context));
            Log.v("json_approve_tpDev", jsonTpDeviation.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        submitAPI(position, status);
    }

    private void CallRejectedTpDeviation(String sfName, String sfCode, String slNo, int position, String status, String date, String reason) {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        try {
            jsonTpDeviation=CommonUtilsMethods.CommonObjectParameter(context);
            jsonTpDeviation.put("tableName", "savedev_appr");
            jsonTpDeviation.put("slno", slNo);
            jsonTpDeviation.put("status", status);
            jsonTpDeviation.put("sfcode", sfCode);
            jsonTpDeviation.put("sfname", sfName);
            jsonTpDeviation.put("reason", reason);
            jsonTpDeviation.put("date", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_6, TimeUtils.FORMAT_15, date));
            jsonTpDeviation.put("division_code", SharedPref.getDivisionCode(context).replace(",", "").trim());
            jsonTpDeviation.put("Rsf", SharedPref.getHqCode(context));
            Log.v("json_reject_tpDev", jsonTpDeviation.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        submitAPI(position, status);
    }

    private void submitAPI(int position, String status) {
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");
        Call<JsonElement> callApprovedTpDev = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonTpDeviation.toString());

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
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
        TextView tv_Name, tv_date, tv_deviationRemarks, tv_view_plan;
        Button btn_reject, btn_approve;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_Name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_deviationRemarks = itemView.findViewById(R.id.tv_deviation_remarks);
            tv_view_plan = itemView.findViewById(R.id.tv_view_plan);
            btn_reject = itemView.findViewById(R.id.btn_reject);
            btn_approve = itemView.findViewById(R.id.btn_approved);
        }
    }
}