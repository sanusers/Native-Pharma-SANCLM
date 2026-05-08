/*
package saneforce.sanzen.activity.approvals.leave;

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
import android.view.inputmethod.InputMethodManager;
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
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;

public class LeaveApprovalAdapter extends RecyclerView.Adapter<LeaveApprovalAdapter.ViewHolder> {
    Context context;
    ArrayList<LeaveModelList> leaveModelLists;
    Dialog dialogReject;
    JSONObject jsonLeave = new JSONObject();
    ApiInterface api_interface;
    ProgressDialog progressDialog = null;
    CommonUtilsMethods commonUtilsMethods;
    LeaveApprovalActivity leaveApprovalActivity;


    public LeaveApprovalAdapter(Context context, ArrayList<LeaveModelList> leaveModelLists) {
        this.context = context;
        this.leaveModelLists = leaveModelLists;
        commonUtilsMethods = new CommonUtilsMethods(context);
        leaveApprovalActivity = new LeaveApprovalActivity();
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
        holder.tv_emp_code.setText(leaveModelLists.get(position).getSf_emp_id());
        holder.tv_address.setText(leaveModelLists.get(position).getAddr());
        holder.tv_from_date.setText(leaveModelLists.get(position).getFrom_date());
        holder.tv_to_date.setText(leaveModelLists.get(position).getTo_date());
        holder.tv_leave_type.setText(leaveModelLists.get(position).getLeave_type());
        holder.tv_no_of_days.setText(leaveModelLists.get(position).getNo_of_days());

        holder.btn_reject.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialogReject = new Dialog(context);
                dialogReject.setContentView(R.layout.popup_reject);
                Objects.requireNonNull(dialogReject.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogReject.setCancelable(false);

                ImageView iv_close = dialogReject.findViewById(R.id.img_close);
                EditText ed_reason = dialogReject.findViewById(R.id.ed_reason_reject);
                Button btn_cancel = dialogReject.findViewById(R.id.btn_cancel);
                Button btn_reject = dialogReject.findViewById(R.id.btn_reject);

                ed_reason.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(ed_reason, 300)});

                btn_cancel.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        closeKeyBoard(view);
                        ed_reason.setText("");
                        dialogReject.dismiss();
                    }
                });
                iv_close.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        closeKeyBoard(view);
                        ed_reason.setText("");
                        dialogReject.dismiss();
                    }
                });

                btn_reject.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        if (!TextUtils.isEmpty(ed_reason.getText().toString())) {
                            closeKeyBoard(view);
                            RejectedLeave(leaveModelLists.get(holder.getBindingAdapterPosition()).getLeave_id(), holder.getBindingAdapterPosition(), ed_reason.getText().toString());
                        } else {
                            closeKeyBoard(view);
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_enter_reason_for_reject));
                        }
                    }
                });
                dialogReject.show();
            }
        });

        holder.btn_accept.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ApprovedLeave(leaveModelLists.get(holder.getBindingAdapterPosition()).getLeave_id(), holder.getBindingAdapterPosition());
            }
        });


    }

    private void RejectedLeave(String leave_id, int Position, String reason) {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        try {
            jsonLeave = CommonUtilsMethods.CommonObjectParameter(context);
            jsonLeave.put("tableName", "leaveapproverej");
            jsonLeave.put("LvID", leave_id);
            jsonLeave.put("LvAPPFlag", "1");
            jsonLeave.put("RejRem", reason);
            jsonLeave.put("sfcode", SharedPref.getSfCode(context));
            jsonLeave.put("division_code", SharedPref.getDivisionCode(context).replace(",", "").trim());
            jsonLeave.put("Rsf", */
/*SharedPref.getHqCode(context)*//*
leaveModelLists.get(Position).getSf_code());
            Log.v("reject_leave", jsonLeave.toString());
        } catch (Exception ignored) {

        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");
        Call<JsonElement> callRejectedLeave = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonLeave.toString());
        callRejectedLeave.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.rejected_successfully));
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
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                dialogReject.dismiss();
            }
        });
    }


    private void ApprovedLeave(String leave_id, int Position) {
        progressDialog = CommonUtilsMethods.createProgressDialog(context);
        try {
            jsonLeave = CommonUtilsMethods.CommonObjectParameter(context);
            jsonLeave.put("tableName", "leaveapproverej");
            jsonLeave.put("LvID", leave_id);
            jsonLeave.put("LvAPPFlag", "0");
            jsonLeave.put("RejRem", "");
            jsonLeave.put("sfcode", SharedPref.getSfCode(context));
            jsonLeave.put("division_code", SharedPref.getDivisionCode(context).replace(",", "").trim());
            jsonLeave.put("Rsf", */
/*SharedPref.getHqCode(context)*//*
leaveModelLists.get(Position).getSf_code());
        } catch (Exception ignored) {

        }
        Log.e("Response :", "" + jsonLeave.toString());
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");
        Call<JsonElement> callApprovedLeave = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonLeave.toString());

        callApprovedLeave.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                Log.e("Response :", "" + response);
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        assert response.body() != null;
                        JSONObject jsonSaveRes = new JSONObject(response.body().toString());
                        if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.approved_successfully));
                            removeAt(Position);
                            ApprovalsActivity.LeaveCount--;

                        }
                    } catch (Exception ignored) {
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

    private void closeKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
*/
package saneforce.sanzen.activity.approvals.leave;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.TextUtils;
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
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;

public class LeaveApprovalAdapter extends RecyclerView.Adapter<LeaveApprovalAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<LeaveModelList> originalList; // MASTER LIST
    private final ArrayList<LeaveModelList> displayList;  // FILTERED LIST

    private Dialog dialogReject;
    private JSONObject jsonLeave = new JSONObject();
    private ApiInterface api_interface;
    private ProgressDialog progressDialog;
    private final CommonUtilsMethods commonUtilsMethods;

    public LeaveApprovalAdapter(Context context, ArrayList<LeaveModelList> leaveModelLists) {
        this.context = context;
        this.originalList = new ArrayList<>(leaveModelLists);
        this.displayList = new ArrayList<>(leaveModelLists);
        this.commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.adapter_leave_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        api_interface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

        LeaveModelList model = displayList.get(position);

        holder.tv_name.setText(model.getName());
        holder.tv_reason.setText(model.getReason());
        holder.tv_emp_code.setText(model.getSf_emp_id());
        holder.tv_address.setText(model.getAddr());
        holder.tv_from_date.setText(model.getFrom_date());
        holder.tv_to_date.setText(model.getTo_date());
        holder.tv_leave_type.setText(model.getLeave_type());
        holder.tv_no_of_days.setText(model.getNo_of_days());

        holder.btn_reject.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                showRejectDialog(holder.getBindingAdapterPosition());
            }
        });

        holder.btn_accept.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ApprovedLeave(model.getLeave_id(), holder.getBindingAdapterPosition());
            }
        });
    }

    private void showRejectDialog(int position) {
        dialogReject = new Dialog(context);
        dialogReject.setContentView(R.layout.popup_reject);
        Objects.requireNonNull(dialogReject.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReject.setCancelable(false);

        ImageView iv_close = dialogReject.findViewById(R.id.img_close);
        EditText ed_reason = dialogReject.findViewById(R.id.ed_reason_reject);
        Button btn_cancel = dialogReject.findViewById(R.id.btn_cancel);
        Button btn_reject = dialogReject.findViewById(R.id.btn_reject);

        ed_reason.setFilters(new InputFilter[]{
                CommonUtilsMethods.FilterSpaceEditText(ed_reason, 300)
        });

        btn_cancel.setOnClickListener(v -> dialogReject.dismiss());
        iv_close.setOnClickListener(v -> dialogReject.dismiss());

        btn_reject.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(ed_reason.getText().toString())) {
                RejectedLeave(displayList.get(position).getLeave_id(),
                        position, ed_reason.getText().toString());
            } else {
                commonUtilsMethods.showToastMessage(
                        context, context.getString(R.string.toast_enter_reason_for_reject), true);
            }
        });

        dialogReject.show();
    }

    private void RejectedLeave(String leaveId, int position, String reason) {

        progressDialog = CommonUtilsMethods.createProgressDialog(context);

        try {
            jsonLeave = CommonUtilsMethods.CommonObjectParameter(context);
            jsonLeave.put("tableName", "leaveapproverej");
            jsonLeave.put("LvID", leaveId);
            jsonLeave.put("LvAPPFlag", "1");
            jsonLeave.put("RejRem", reason);
            jsonLeave.put("sfcode", SharedPref.getSfCode(context));
            jsonLeave.put("division_code",
                    SharedPref.getDivisionCode(context).replace(",", "").trim());
            jsonLeave.put("Rsf", displayList.get(position).getSf_code());
        } catch (Exception ignored) {}

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");

        api_interface.getJSONElement(
                SharedPref.getCallApiUrl(context), mapString, jsonLeave.toString()
        ).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call,
                                   @NonNull Response<JsonElement> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        JSONObject res = new JSONObject(response.body().toString());
                        if (res.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(
                                    context, context.getString(R.string.rejected_successfully), true);
                            dialogReject.dismiss();
                            removeAt(position);
                            ApprovalsActivity.LeaveCount--;
                        }
                    } catch (Exception ignored) {}
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void ApprovedLeave(String leaveId, int position) {

        progressDialog = CommonUtilsMethods.createProgressDialog(context);

        try {
            jsonLeave = CommonUtilsMethods.CommonObjectParameter(context);
            jsonLeave.put("tableName", "leaveapproverej");
            jsonLeave.put("LvID", leaveId);
            jsonLeave.put("LvAPPFlag", "0");
            jsonLeave.put("RejRem", "");
            jsonLeave.put("sfcode", SharedPref.getSfCode(context));
            jsonLeave.put("division_code",
                    SharedPref.getDivisionCode(context).replace(",", "").trim());
            jsonLeave.put("Rsf", displayList.get(position).getSf_code());
        } catch (Exception ignored) {}

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/approvals");

        api_interface.getJSONElement(
                SharedPref.getCallApiUrl(context), mapString, jsonLeave.toString()
        ).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call,
                                   @NonNull Response<JsonElement> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    removeAt(position);
                    ApprovalsActivity.LeaveCount--;
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void removeAt(int position) {
        LeaveModelList removed = displayList.get(position);
        displayList.remove(position);
        originalList.remove(removed);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, displayList.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(String text) {
        displayList.clear();
        if (text.isEmpty()) {
            displayList.addAll(originalList);
        } else {
            text = text.toLowerCase();
            for (LeaveModelList s : originalList) {
                if (s.getName().toLowerCase().contains(text)
                        || s.getLeave_type().toLowerCase().contains(text)
                        || s.getReason().toLowerCase().contains(text)
                        || s.getLeave_id().toLowerCase().contains(text)
                        || s.getAddr().toLowerCase().contains(text)
                        || s.getNo_of_days().toLowerCase().contains(text)) {
                    displayList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_emp_code, tv_reason, tv_from_date,
                tv_to_date, tv_no_of_days, tv_leave_type, tv_address;
        Button btn_accept, btn_reject;

        ViewHolder(@NonNull View item) {
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
