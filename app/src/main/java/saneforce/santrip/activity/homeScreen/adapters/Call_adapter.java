package saneforce.santrip.activity.homeScreen.adapters;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SampleValidation;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.Designation;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.DivCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.InputValidation;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.SfCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.SfType;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.StateCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.SubDivisionCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.TodayPlanSfCode;
import static saneforce.santrip.activity.homeScreen.fragment.CallsFragment.binding;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.fragment.CallsFragment;
import saneforce.santrip.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.santrip.activity.map.custSelection.CustList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SharedPref;


public class Call_adapter extends RecyclerView.Adapter<Call_adapter.listDataViewholider> {
    Resources resources;
    Context context;
    List<CallsModalClass> list = new ArrayList<>();
    ApiInterface apiInterface;
    ProgressDialog progressBar;

    public Call_adapter(Context context, List<CallsModalClass> list, ApiInterface apiInterface) {
        this.context = context;
        this.list = list;
        this.apiInterface = apiInterface;

    }

    @NonNull
    @Override
    public listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calls_item_view, parent, false);
        return new listDataViewholider(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listDataViewholider holder, int position) {

        CallsModalClass callslist = list.get(position);
        holder.DocName.setText(callslist.getDocName());
        holder.datetime.setText(callslist.getCallsDateTime());

        String type = callslist.getDocNameID();

        if (type.equalsIgnoreCase("1")) {
            holder.imageView.setImageResource(R.drawable.map_dr_img);
        } else if (type.equalsIgnoreCase("2")) {
            holder.imageView.setImageResource(R.drawable.map_chemist_img);
        } else if (type.equalsIgnoreCase("3")) {
            holder.imageView.setImageResource(R.drawable.map_stockist_img);
        } else if (type.equalsIgnoreCase("4")) {
            holder.imageView.setImageResource(R.drawable.map_unlistdr_img);
        } else if (type.equalsIgnoreCase("5")) {
            holder.imageView.setImageResource(R.drawable.map_cip_img);
        } else if (type.equalsIgnoreCase("6")) {
            holder.imageView.setImageResource(R.drawable.tp_hospital_icon);
        }

        holder.menu.setOnClickListener((View.OnClickListener) v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
            popup.inflate(R.menu.call_menu);
            popup.setOnMenuItemClickListener(menuItem -> {

                if (menuItem.getItemId() == R.id.menuEdit) {
                    CallEditAPI(callslist.getTrans_Slno(), callslist.getADetSLNo(), callslist.getDocName(), callslist.getDocCode(), callslist.getDocNameID());
                } else if (menuItem.getItemId() == R.id.menuDelete) {
                    CallDeleteAPI(position, callslist.getADetSLNo(), callslist.getDocNameID());
                    removeAt(position);
                }
                return true;
            });
            popup.show();
        });
    }

    private void CallDeleteAPI(int position, String aDetSLNo, String type) {
        progressBar = CommonUtilsMethods.createProgressDialog(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sfcode", SfCode);
            jsonObject.put("division_code", DivCode);
            jsonObject.put("Rsf", TodayPlanSfCode);
            jsonObject.put("sf_type", SfType);
            jsonObject.put("Designation", Designation);
            jsonObject.put("state_code", StateCode);
            jsonObject.put("subdivision_code", SubDivisionCode);
            jsonObject.put("amc", aDetSLNo);
            jsonObject.put("CusType", type);
            jsonObject.put("sample_validation", CallsFragment.SampleValidation);
            jsonObject.put("input_validation", InputValidation);
            Log.v("delCall", jsonObject.toString());
        } catch (Exception e) {
            Log.v("delCall", e.toString());
        }

        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

        Call<ResponseBody> deleteCall = null;
        deleteCall = apiInterface.DeleteCall(jsonObject.toString());

        deleteCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        progressBar.dismiss();
                        assert response.body() != null;
                        // JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.v("delCall", response.toString());
                        removeAt(position);
                        Toast.makeText(context, "Call Deleted Successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        progressBar.dismiss();
                        Log.v("delCall", e.toString());
                    }
                } else {
                    progressBar.dismiss();
                    Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressBar.dismiss();
                Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CallEditAPI(String transSlno, String aDetSLNo, String docName, String docCode, String type) {
        progressBar = CommonUtilsMethods.createProgressDialog(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("headerno", transSlno);
            jsonObject.put("detno", aDetSLNo);
            jsonObject.put("sfcode", SfCode);
            jsonObject.put("division_code", DivCode);
            jsonObject.put("Rsf", TodayPlanSfCode);
            jsonObject.put("sf_type", SfType);
            jsonObject.put("Designation", Designation);
            jsonObject.put("state_code", StateCode);
            jsonObject.put("subdivision_code", SubDivisionCode);
            jsonObject.put("cusname", docName);
            jsonObject.put("custype", docCode);
            jsonObject.put("pob", "1");
            Log.v("editCall", jsonObject.toString());

        } catch (Exception e) {
            Log.v("editCall", e.toString());
        }
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

        Call<JsonObject> getEditCallDetails = null;
        getEditCallDetails = apiInterface.getEditCallDetails(jsonObject.toString());

        getEditCallDetails.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        progressBar.dismiss();
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.v("editCall", jsonObject.toString());
                        Intent intent = new Intent(context, DCRCallActivity.class);
                        CallActivityCustDetails = new ArrayList<>();
                        CallActivityCustDetails.add(0, new CustList(docName, docCode, type, transSlno, aDetSLNo, jsonObject.toString()));
                        intent.putExtra("isDetailedRequired", "false");
                        intent.putExtra("from_activity", "edit");
                        context.startActivity(intent);
                    } catch (Exception e) {
                        progressBar.dismiss();
                        Log.v("editCall", e.toString());
                    }
                } else {
                    progressBar.dismiss();
                    Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressBar.dismiss();
                Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
        binding.txtCallcount.setText(String.valueOf(list.size()));
    }

    public static class listDataViewholider extends RecyclerView.ViewHolder {
        TextView DocName, datetime, menu;
        CircleImageView imageView;

        public listDataViewholider(@NonNull View itemView) {
            super(itemView);
            DocName = itemView.findViewById(R.id.textViewLabel1);
            datetime = itemView.findViewById(R.id.textViewLabel2);
            imageView = itemView.findViewById(R.id.profile_icon);
            menu = itemView.findViewById(R.id.optionview);
        }
    }
}