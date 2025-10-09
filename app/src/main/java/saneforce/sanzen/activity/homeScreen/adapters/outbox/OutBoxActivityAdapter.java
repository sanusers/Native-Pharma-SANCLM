package saneforce.sanzen.activity.homeScreen.adapters.outbox;

import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.OutboxUtil;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class OutBoxActivityAdapter extends RecyclerView.Adapter<OutBoxActivityAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<ActivityModelClass> activityModelClassList;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    private final CommonUtilsMethods commonUtilsMethods;
    private final Activity activity;
    private final ApiInterface apiInterface;
    ProgressDialog progressDialog;
    private final RoomDB roomDB;
    private final OfflineDaySubmitDao offlineDaySubmitDao;
    private final ActivityOfflineDataDao activityOfflineDataDao;
    private final ActivityUploadDataDao activityUploadDataDao;
    private final OutboxUtil outboxUtil;

    public OutBoxActivityAdapter(Activity activity, Context context, ArrayList<ActivityModelClass> activityModelClassList, ApiInterface apiInterface) {
        this.context = context;
        this.activity = activity;
        this.activityModelClassList = activityModelClassList;
        this.apiInterface = apiInterface;
        commonUtilsMethods = new CommonUtilsMethods(context);
        roomDB=RoomDB.getDatabase(context);
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
        activityOfflineDataDao = roomDB.activityOfflineDataDao();
        activityUploadDataDao = roomDB.activityUploadDataDao();
        outboxUtil = new OutboxUtil(context);
    }

    @NonNull
    @Override
    public OutBoxActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_child_view, parent, false);
        return new OutBoxActivityAdapter.ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxActivityAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(activityModelClassList.get(position).getName());
        holder.imgPic.setVisibility(View.GONE);

        holder.tvInOut.setText(String.format("%s %s", activityModelClassList.get(position).getActivityDate(), activityModelClassList.get(position).getActivityTime()));
        String status = activityModelClassList.get(position).getSyncStatus();
        if(status.equalsIgnoreCase(Constants.WAITING_FOR_SYNC)) {
            holder.tvStatus.setText(context.getString(R.string.waiting_for_sync));
        } else if (status.equalsIgnoreCase(Constants.FAILED)) {
            holder.tvStatus.setText(context.getString(R.string.sync_failed));
        } else if (status.equalsIgnoreCase(Constants.DUPLICATE_CALL)) {
            holder.tvStatus.setText(context.getString(R.string.duplicate_call));
        } else if (status.equalsIgnoreCase(Constants.EXCEPTION_ERROR)) {
            holder.tvStatus.setText(context.getString(R.string.exception_error));
        } else {
            holder.tvStatus.setText(status);
        }

        holder.tvMenu.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                final PopupMenu popup = new PopupMenu(wrapper, view, Gravity.END);
                popup.inflate(R.menu.call_menu);
                MenuItem editMenu = popup.getMenu().findItem(R.id.menuEdit);
                MenuItem deleteMenu = popup.getMenu().findItem(R.id.menuDelete);
                editMenu.setVisible(false);
                deleteMenu.setVisible(offlineDaySubmitDao.getDaySubmit(activityModelClassList.get(position).getActivityDate()) == null);
                popup.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.menuSync) {
                        if (UtilityClass.isNetworkAvailable(context)) {
                            ActivityModelClass activityModelClass = activityModelClassList.get(position);
                            CallAPI(holder.getAbsoluteAdapterPosition(), activityModelClass);
                        } else {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                        }
                    } else if (menuItem.getItemId() == R.id.menuDelete) {

                        Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dcr_cancel_alert);
                        dialog.setCancelable(false);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                        TextView btn_no = dialog.findViewById(R.id.btn_no);
                        TextView titte = dialog.findViewById(R.id.ed_alert_msg);
                        titte.setText(R.string.are_you_sure_to_delete);

                        btn_yes.setOnClickListener(new SafeClickListener() {
                            @Override
                            public void onSafeClick(View view) {
                                activityOfflineDataDao.deleteOfflineActivity(activityModelClassList.get(position).getId());
                                activityUploadDataDao.deleteUploadActivity(activityModelClassList.get(position).getId());
                                dialog.dismiss();
                                removeAt(position);
                            }
                        });

                        btn_no.setOnClickListener(new SafeClickListener() {
                            @Override
                            public void onSafeClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                    return true;
                });
                popup.show();
            }
        });

    }

    private void CallAPI(int pos, ActivityModelClass activityModelClass) {
        JSONObject jsonSaveActivity;
        try {
            progressDialog = CommonUtilsMethods.createProgressDialog(context);
            jsonSaveActivity = new JSONObject(activityModelClass.getJsonData());
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/activity");
            Call<JsonElement> activitySaveDcr = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonSaveActivity.toString());
            activitySaveDcr.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                activityOfflineDataDao.deleteOfflineActivity(activityModelClass.getId());
                                removeAt(pos);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.activity_saved_successfully));
                            } else {
                                outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.FAILED);
                                activityModelClass.setSyncStatus(Constants.FAILED);
                                activityModelClass.setSyncCount(5);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.sync_failed));
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncCount(5);
                            Log.v("SendOutboxCall", "---" + e);
                            progressDialog.dismiss();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    outboxUtil.updateStatusActivity(activityModelClass.getId(), activityModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityModelClass.setSyncStatus(Constants.FAILED);
                    activityModelClass.setSyncCount(activityModelClass.getSyncCount() + 1);
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.sync_failed));
                    progressDialog.dismiss();
                }
            });

        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return activityModelClassList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        activityModelClassList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, activityModelClassList.size());
        ArrayList<GroupModelClass> listDatesDup = outboxUtil.getOutBoxDatesWithData();
        try {
            for (int i = 0; i < listDates.size(); i++) {
                GroupModelClass groupModelClass = listDates.get(i);
                if (groupModelClass.isExpanded()) {
                    for (int j = 0; j < listDatesDup.size(); j++) {
                        GroupModelClass groupModelClass1 = listDatesDup.get(j);
                        if (groupModelClass1.getGroupName().equalsIgnoreCase(groupModelClass.getGroupName())) {
                            groupModelClass1.setExpanded(true);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listDates = listDatesDup;
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInOut, tvStatus, tvMenu;
        ImageView imgPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textViewLabel1);
            tvInOut = itemView.findViewById(R.id.textViewLabel2);
            tvStatus = itemView.findViewById(R.id.tv_call_status);
            tvMenu = itemView.findViewById(R.id.optionview);
            imgPic = itemView.findViewById(R.id.profile_icon);
        }
    }
}
