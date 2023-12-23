package saneforce.sanclm.activity.approvals.leave;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.ApprovalsActivity;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SharedPref;

public class LeaveApprovalAdapter extends RecyclerView.Adapter<LeaveApprovalAdapter.ViewHolder> {
    Context context;
    ArrayList<LeaveModelList> leaveModelLists;
    Dialog dialogReject;
    JSONObject jsonLeave = new JSONObject();
    ApiInterface api_interface;
    ProgressDialog progressDialog = null;


    public LeaveApprovalAdapter(Context context, ArrayList<LeaveModelList> leaveModelLists) {
        this.context = context;
        this.leaveModelLists = leaveModelLists;
    }

    @NonNull
    @Override
    public LeaveApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_leave_approval, parent, false);
        return new LeaveApprovalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveApprovalAdapter.ViewHolder holder, int position) {
        api_interface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        holder.tv_name.setText(leaveModelLists.get(position).getName());
        holder.tv_reason.setText(leaveModelLists.get(position).getReason());
        holder.tv_emp_code.setText(leaveModelLists.get(position).getSf_code());
        holder.tv_address.setText(leaveModelLists.get(position).getAddr());
        holder.tv_from_date.setText(leaveModelLists.get(position).getFrom_date());
        holder.tv_to_date.setText(leaveModelLists.get(position).getTo_date());
        holder.tv_leave_type.setText(leaveModelLists.get(position).getLeave_type());
        holder.tv_no_of_days.setText(leaveModelLists.get(position).getNo_of_days());

        holder.btn_reject.setOnClickListener(view -> {
            dialogReject = new Dialog(context);
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
                if (!TextUtils.isEmpty(ed_reason.getText().toString())) {
                    RejectedLeave(leaveModelLists.get(holder.getBindingAdapterPosition()).getLeave_id(), holder.getBindingAdapterPosition(), ed_reason.getText().toString());
                } else {
                    Toast.makeText(context, context.getResources().getText(R.string.toast_enter_reason_for_reject), Toast.LENGTH_SHORT).show();
                }
            });
            dialogReject.show();
        });

        holder.btn_accept.setOnClickListener(view -> ApprovedLeave(leaveModelLists.get(holder.getBindingAdapterPosition()).getLeave_id(), holder.getBindingAdapterPosition()));


    }

    private void RejectedLeave(String leave_id, int Position, String reason) {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        try {
            jsonLeave.put("tableName", "leaveapproverej");
            jsonLeave.put("LvID", leave_id);
            jsonLeave.put("LvAPPFlag", "1");
            jsonLeave.put("RejRem", reason);
            jsonLeave.put("sfcode", LeaveApprovalActivity.SfCode);
            jsonLeave.put("division_code", LeaveApprovalActivity.DivCode.replace(",", "").trim());
            jsonLeave.put("Rsf", LeaveApprovalActivity.TodayPlanSfCode);
            jsonLeave.put("sf_type", LeaveApprovalActivity.SfType);
            jsonLeave.put("Designation", LeaveApprovalActivity.Designation);
            jsonLeave.put("state_code", LeaveApprovalActivity.StateCode);
            jsonLeave.put("subdivision_code", LeaveApprovalActivity.SubDivisionCode);
            Log.v("reject_leave", jsonLeave.toString());
        } catch (Exception ignored) {

        }
        Call<JsonObject> callRejectedLeave;
        callRejectedLeave = api_interface.saveLeaveApproval(jsonLeave.toString());
        callRejectedLeave.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(context, "Rejected Successfully", Toast.LENGTH_SHORT).show();
                            dialogReject.dismiss();
                            removeAt(Position);
                            ApprovalsActivity.LeaveCount--;
                        }
                    } catch (Exception e) {
                        dialogReject.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    dialogReject.dismiss();
                    Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                dialogReject.dismiss();
            }
        });
    }


    private void ApprovedLeave(String leave_id, int Position) {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        try {
            jsonLeave.put("tableName", "leaveapproverej");
            jsonLeave.put("LvID", leave_id);
            jsonLeave.put("LvAPPFlag", "0");
            jsonLeave.put("RejRem", "");
            jsonLeave.put("sfcode", LeaveApprovalActivity.SfCode);
            jsonLeave.put("division_code", LeaveApprovalActivity.DivCode.replace(",", "").trim());
            jsonLeave.put("Rsf", LeaveApprovalActivity.TodayPlanSfCode);
            jsonLeave.put("sf_type", LeaveApprovalActivity.SfType);
            jsonLeave.put("Designation", LeaveApprovalActivity.Designation);
            jsonLeave.put("state_code", LeaveApprovalActivity.StateCode);
            jsonLeave.put("subdivision_code", LeaveApprovalActivity.SubDivisionCode);
        } catch (Exception ignored) {

        }

        Call<JsonObject> callApprovedLeave;
        callApprovedLeave = api_interface.saveLeaveApproval(jsonLeave.toString());

        callApprovedLeave.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            Toast.makeText(context, "Approved Successfully", Toast.LENGTH_SHORT).show();
                            removeAt(Position);
                            ApprovalsActivity.LeaveCount--;
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaveModelLists.size();
    }

    public void removeAt(int position) {
        leaveModelLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, leaveModelLists.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<LeaveModelList> filteredNames) {
        this.leaveModelLists = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_emp_code;
        TextView tv_reason;
        TextView tv_from_date;
        TextView tv_to_date;
        TextView tv_no_of_days;
        TextView tv_leave_type;
        TextView tv_address;
        Button btn_accept, btn_reject;

        public ViewHolder(@NonNull View item) {
            super(item);
            tv_name = item.findViewById(R.id.tv_name);
            tv_emp_code = item.findViewById(R.id.tv_emp_code);
            tv_reason = item.findViewById(R.id.tv_reason);
            tv_from_date = item.findViewById(R.id.tv_leave_from);
            tv_to_date = item.findViewById(R.id.tv_leave_to);
            tv_leave_type = item.findViewById(R.id.tv_leave_type);
            tv_no_of_days = item.findViewById(R.id.tv_no_of_days);
            tv_address = item.findViewById(R.id.tv_address);
            btn_accept = item.findViewById(R.id.btn_approved);
            btn_reject = item.findViewById(R.id.btn_reject);
        }
    }
}
