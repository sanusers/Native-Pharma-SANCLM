package saneforce.sanzen.activity.homeScreen.adapters.outbox;

import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityUploadModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.OutboxUtil;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class OutBoxActivityUploadAdapter extends RecyclerView.Adapter<OutBoxActivityUploadAdapter.ViewHolder> {
    Context context;
    ArrayList<ActivityUploadModelClass> activityUploadModelClassList;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    private final OfflineDaySubmitDao offlineDaySubmitDao;
    private final ActivityOfflineDataDao activityOfflineDataDao;
    private final ActivityUploadDataDao activityUploadDataDao;
    private final OutboxUtil outboxUtil;
    private final ApiInterface apiInterface;
    ProgressDialog progressDialog;

    public OutBoxActivityUploadAdapter(Activity activity, Context context, ArrayList<ActivityUploadModelClass> activityUploadModelClassList, ApiInterface apiInterface) {
        this.activity = activity;
        this.context = context;
        this.apiInterface = apiInterface;
        this.activityUploadModelClassList = activityUploadModelClassList;
        commonUtilsMethods = new CommonUtilsMethods(context);
        RoomDB roomDB = RoomDB.getDatabase(context);
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
        activityOfflineDataDao = roomDB.activityOfflineDataDao();
        activityUploadDataDao = roomDB.activityUploadDataDao();
        outboxUtil = new OutboxUtil(context);
    }

    @NonNull
    @Override
    public OutBoxActivityUploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_ec_view, parent, false);
        return new OutBoxActivityUploadAdapter.ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxActivityUploadAdapter.ViewHolder holder, int position) {
        holder.tvImageName.setText(activityUploadModelClassList.get(position).getImageName());
        holder.tvStatus.setText(activityUploadModelClassList.get(position).getSyncStatus());

        File imgFile = new File(activityUploadModelClassList.get(position).getFilePath());
        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imgView.setImageBitmap(myBitmap);
        }

        holder.imgView.setOnClickListener(v -> {
            File imgFile1 = new File(activityUploadModelClassList.get(position).getFilePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
            showImage(myBitmap);
        });

        holder.tvMenu.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
            popup.inflate(R.menu.ec_call_menu);
            MenuItem deleteMenu = popup.getMenu().findItem(R.id.menuDelete);
            deleteMenu.setVisible(offlineDaySubmitDao.getDaySubmit(activityUploadModelClassList.get(position).getActivityDate()) == null);
            popup.setOnMenuItemClickListener(menuItem -> {
                if(menuItem.getItemId() == R.id.menuSync) {
                    if(UtilityClass.isNetworkAvailable(context)) {
                        CallImageApi(holder.getAbsoluteAdapterPosition(), activityUploadModelClassList.get(position));
                    }else {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                    }
                }else if(menuItem.getItemId() == R.id.menuDelete) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dcr_cancel_alert);
                    dialog.setCancelable(false);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                    TextView btn_no = dialog.findViewById(R.id.btn_no);
                    TextView titte = dialog.findViewById(R.id.ed_alert_msg);
                    titte.setText(R.string.are_you_sure_to_delete);

                    btn_yes.setOnClickListener(view -> {
                        dialog.dismiss();
//                        try {
//                            JSONObject jsonObject;
//                            for (int i = 0; i<listDates.size(); i++) {
//                                if(listDates.get(i).getGroupName().equalsIgnoreCase(activityUploadModelClassList.get(position).getDates())) {
//                                    for (int j = 0; j<listDates.get(i).getChildItems().get(2).getOutBoxCallLists().size(); j++) {
//                                        OutBoxCallList outBoxCallList = listDates.get(i).getChildItems().get(2).getOutBoxCallLists().get(j);
//                                        if(outBoxCallList.getCusCode().equalsIgnoreCase(activityUploadModelClassList.get(position).getImg_name())) {
//                                            jsonObject = new JSONObject(outBoxCallList.getJsonData());
//                                            for (int m = 0; m<jsonArray.length(); m++) {
//                                                JSONObject jsonObjectEC = jsonArray.getJSONObject(i);
//                                                if(jsonObjectEC.getString("EventImageName").equalsIgnoreCase(activityUploadModelClassList.get(position).getImg_name())) {
//                                                    jsonArray.remove(i);
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            callOfflineDataDao.saveOfflineUpdateJson(activityUploadModelClassList.get(position).getDates(), activityUploadModelClassList.get(position).getCusCode(), jsonObject.toString());
//                            outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
//                            commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
//                            outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
//                            outBoxHeaderAdapter.notifyDataSetChanged();
//                        } catch (Exception ignored) {
//                        }
                        File fileDelete = new File(activityUploadModelClassList.get(position).getFilePath());
                        if(fileDelete.exists()) {
                            if(fileDelete.delete()) {
                                System.out.println("file Deleted :" + activityUploadModelClassList.get(position).getFilePath());
                            }else {
                                System.out.println("file not Deleted :" + activityUploadModelClassList.get(position).getFilePath());
                            }
                        }
                        activityUploadDataDao.deleteUploadActivity(activityUploadModelClassList.get(position).getId(), activityUploadModelClassList.get(position).getActivityID());
                        removeAt(position);
                    });

                    btn_no.setOnClickListener(view -> {
                        dialog.dismiss();
                    });
                }
                return true;
            });
            popup.show();
        });
    }

    private void CallImageApi(int pos, ActivityUploadModelClass activityUploadModelClass) {
        try {
            progressDialog = CommonUtilsMethods.createProgressDialog(context);
            File file = new File(activityUploadModelClass.getFilePath());
            MultipartBody.Part img = convertImg("ActivityFile", String.valueOf(file));
            HashMap<String, RequestBody> values = field(activityUploadModelClass.getJsonData());
            Call<JsonObject> saveAttachment = apiInterface.SaveImg(values, img);
            saveAttachment.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if(response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if(jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                activityUploadDataDao.deleteUploadActivity(activityUploadModelClass.getId(), activityUploadModelClass.getActivityID());
                                removeAt(pos);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.activity_upload_saved_successfully));
                            }else {
                                outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.FAILED);
                                activityUploadModelClass.setSyncStatus(Constants.FAILED);
                                activityUploadModelClass.setSyncCount(5);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.sync_failed));
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncCount(5);
                            Log.v("SendOutboxCall", "---" + e);
                            progressDialog.dismiss();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable throwable) {
                    outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), activityUploadModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityUploadModelClass.setSyncStatus(Constants.FAILED);
                    activityUploadModelClass.setSyncCount(activityUploadModelClass.getSyncCount() + 1);
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.sync_failed));
                    progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(txt, MultipartBody.FORM);
    }

    public MultipartBody.Part convertImg(String tag, String path) {
        Log.d("path", tag + "-" + path);
        MultipartBody.Part yy = null;
        try {
            File file;
            if(path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(context).compressToFile(new File(path));
                Log.d("path", tag + "-" + path);
            }else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

            Log.d("path", String.valueOf(yy));
        } catch (Exception ignored) {
        }
        return yy;
    }

    @Override
    public int getItemCount() {
        return activityUploadModelClassList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        activityUploadModelClassList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, activityUploadModelClassList.size());
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    public void showImage(Bitmap img_view) {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(true);
        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(img_view);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._300sdp), (int) context.getResources().getDimension(R.dimen._300sdp)));
        builder.show();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvImageName, tvStatus, tvMenu;
        ImageView imgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImageName = itemView.findViewById(R.id.tvImageName);
            imgView = itemView.findViewById(R.id.img_view);
            tvMenu = itemView.findViewById(R.id.optionView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
