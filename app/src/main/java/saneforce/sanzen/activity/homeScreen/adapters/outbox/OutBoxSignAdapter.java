package saneforce.sanzen.activity.homeScreen.adapters.outbox;

import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.gson.JsonObject;

import org.json.JSONArray;
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
import saneforce.sanzen.AWS.AWSBucketsSign;
import saneforce.sanzen.AWS.Util;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.call.fragments.signature.SignatureCanvas;
import saneforce.sanzen.activity.homeScreen.modelClass.SignModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.OutboxUtil;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class OutBoxSignAdapter extends RecyclerView.Adapter<OutBoxSignAdapter.ViewHolder> {
    Context context;
    ArrayList<SignModelClass> signModelClasses;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    CommonUtilsMethods commonUtilsMethods;
    SignatureCanvas signatureCanvas;
    Activity activity;
    private final CallOfflineSignDataDao callOfflineSignDataDao;
    private final CallOfflineDataDao callOfflineDataDao;
    private final OfflineDaySubmitDao offlineDaySubmitDao;
    private String id;
    Util util;
    private OutboxUtil outboxUtil;

    public OutBoxSignAdapter(Activity activity, Context context, ArrayList<SignModelClass> signModelClasses) {
        this.activity = activity;
        this.context = context;
        this.signModelClasses = signModelClasses;
        this.commonUtilsMethods = new CommonUtilsMethods(context);
        RoomDB roomDB = RoomDB.getDatabase(context);
        this.callOfflineSignDataDao = roomDB.callOfflineSignDataDao();
        this.callOfflineDataDao = roomDB.callOfflineDataDao();
        this.offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
        this.util = new Util();
        outboxUtil = new OutboxUtil(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_sign_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvImageName.setText(signModelClasses.get(position).getImg_name());
        holder.tvStatus.setText(signModelClasses.get(position).getSync_status());

        File imgFile = new File(signModelClasses.get(position).getFilePath());

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imgView.setImageBitmap(myBitmap);
        } else {
            Log.d("imgFile", "signImg: " + "image file doesn't exists in external file storage");
        }

        holder.imgView.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                File imgFile1 = new File(signModelClasses.get(position).getFilePath());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                showImage(myBitmap);
            }
        });

        holder.tvMenu.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                final PopupMenu popup = new PopupMenu(wrapper, view, Gravity.END);
                popup.inflate(R.menu.sign_call_menu);
                MenuItem deleteMenu = popup.getMenu().findItem(R.id.menuDelete);
                if (signModelClasses.get(position).getSync_status().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || signModelClasses.get(position).getSync_status().equalsIgnoreCase(Constants.CALL_FAILED)) {
                    deleteMenu.setVisible(false);
                } else {
                    deleteMenu.setVisible(true);
                }
                popup.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.menuSync) {
//                        if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
//                            CallSignImageApiS3(id, signModelClasses.get(position), signModelClasses.get(position).getFilePath(), signModelClasses.get(position).getJson_values());
//                        } else {
//                            CallSignImageApi(id, signModelClasses.get(position), signModelClasses.get(position).getFilePath(), signModelClasses.get(position).getJson_values());
//                        }

                        if (UtilityClass.isNetworkAvailable(context)) {
                            if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                                CallSignImageApiS3(id, signModelClasses.get(position), signModelClasses.get(position).getFilePath(), signModelClasses.get(position).getJson_values());
                            } else {
                                CallSignImageApi(id, signModelClasses.get(position), signModelClasses.get(position).getFilePath(), signModelClasses.get(position).getJson_values());
                            }
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

                        btn_yes.setOnClickListener(view1 -> {
                            dialog.dismiss();
                            try {
                                JSONObject jsonObject;
                                jsonObject = new JSONObject(callOfflineDataDao.getJsonCallList(signModelClasses.get(position).getDates(), signModelClasses.get(position).getCusCode()));
                                jsonObject.remove("sign_path");
                                jsonObject.remove("sign_Img");

                                for (int i = 0; i < listDates.size(); i++) {
                                    if (listDates.get(i).getGroupName().equalsIgnoreCase(signModelClasses.get(position).getDates())) {
                                        for (int j = 0; j < listDates.get(i).getChildItems().get(2).getOutBoxCallLists().size(); j++) {
                                            OutBoxCallList outBoxCallList = listDates.get(i).getChildItems().get(2).getOutBoxCallLists().get(j);
                                            if (outBoxCallList.getCusCode().equalsIgnoreCase(signModelClasses.get(position).getCusCode())) {
                                                jsonObject = new JSONObject(outBoxCallList.getJsonData());
                                                jsonObject.remove("sign_path");
                                                jsonObject.remove("sign_Img");
                                                outBoxCallList.setJsonData(String.valueOf(jsonObject));
                                            }
                                        }
                                    }
                                }

                                callOfflineDataDao.saveOfflineUpdateJson(signModelClasses.get(position).getDates(), signModelClasses.get(position).getCusCode(), jsonObject.toString());
                                outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
                                commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
                                outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
                                outBoxHeaderAdapter.notifyDataSetChanged();
                            } catch (Exception ignored) {
                                ignored.printStackTrace();
                            }
                            File fileDelete = new File(signModelClasses.get(position).getFilePath());
                            if (fileDelete.exists()) {
                                if (fileDelete.delete()) {
//                                System.out.println("file Deleted :" + signModelClasses.get(position).getFilePath());
                                } else {
//                                System.out.println("file not Deleted :" + signModelClasses.get(position).getFilePath());
                                }
                            }
                            callOfflineSignDataDao.deleteSignDataByImageName(signModelClasses.get(position).getImg_name());
                            removeAt(position);
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

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        signModelClasses.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, signModelClasses.size());
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    private void CallSignImageApi(String id, SignModelClass signModelClass, String filePath, String jsonValues){
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getTagApiImageUrl(context));
        MultipartBody.Part img = convertImg("SignImg", filePath);
        HashMap<String, RequestBody> values = field(jsonValues);
        Call<JsonObject> saveImgDcr = apiInterface.SaveImg(values, img);
        saveImgDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().toString());
                        if (json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Profile Has Been Updated")) {
                            DeleteCacheFileSign(filePath, id, signModelClass);
                        } else {
                            signModelClass.setSynced(1);
                            signModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineSignDataDao.updateSignStatus(id, Constants.DUPLICATE_CALL, 1);
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        signModelClass.setSynced(1);
                        signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineSignDataDao.updateSignStatus(id, Constants.DUPLICATE_CALL, 1);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                signModelClass.setSynced(1);
                signModelClass.setSync_status(Constants.CALL_FAILED);
                callOfflineSignDataDao.updateSignStatus(id, Constants.CALL_FAILED, 1);
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFileSign(String filePath, String id, SignModelClass signModelClass) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        callOfflineSignDataDao.deleteOfflineSignImage(filePath);
        signModelClasses.remove(signModelClass);
        ArrayList<GroupModelClass> listDatesDup = outboxUtil.getOutBoxDatesWithData(context);
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

    private void CallSignImageApiS3(String id, SignModelClass signModelClass, String filePath, String jsonValues) {
        try {
            util.getS3Client(context);
            String bucketName = "san-one";
            if (!filePath.isEmpty()) {
                File fileToUpload = new File(filePath);
                Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
                if (fileToUpload.toString().isEmpty()) {
                    Log.d("fileToUploadSignAdapter", "not exists: " + filePath);
                } else {
//                    String s3Key = SharedPref.getDivisionCode(context).replace(",", "/") + "Signature" + "/" + fileToUpload.getName();
                    String s3Key = "uploads/"+SharedPref.getDivisionSname(context)+SharedPref.getDivisionCode(context).replace(",", "/") + "Signature" + "/" + fileToUpload.getName();
                    Log.d("TAG", "CallSendAPIImage: " + s3Key);


                    TransferNetworkLossHandler.getInstance(context);

                    TransferUtility transferUtility = TransferUtility.builder()
                            .context(context)
                            .s3Client(util.getS3Client(context))
                            .build();

                    TransferObserver uploadObserver = transferUtility.upload(
                            bucketName,
                            s3Key,
                            fileToUpload);
                    uploadObserver.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            if (state == TransferState.COMPLETED) {
                                Log.d("TAG", "signModelClass: " + signModelClass.getFilePath());
                                InsertImageSign(signModelClass.getFilePath(), context);
                                Log.d("S3 Upload", "Upload Successful: " + s3Key);


                            } else if (state == TransferState.FAILED) {

                                Log.e("S3 Upload", "Upload Failed");
                                InsertImageSign(signModelClass.getFilePath(), context);
                                signModelClass.setSynced(1);
                                signModelClass.setSync_status(Constants.CALL_FAILED);
                                try {
                                    callOfflineSignDataDao.updateSignStatus(String.valueOf(id), Constants.CALL_FAILED, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            double progress = (bytesCurrent * 100.0) / bytesTotal;
                            Log.d("S3 Upload", "Upload Progress: " + progress + "%");
                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            Log.e("S3 Upload", "Error: " + ex.getMessage());
                            signModelClass.setSynced(1);
                            signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                            callOfflineSignDataDao.updateSignStatus(String.valueOf(id), Constants.EXCEPTION_ERROR, 1);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.v("img_tagOSA", e.toString());
            signModelClass.setSynced(1);
            signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineSignDataDao.updateSignStatus(id, Constants.EXCEPTION_ERROR, 1);
        }
    }

    private void InsertImageSign(final String ImageUrl, Context context) {
        File imageFile = new File(ImageUrl);
        Log.d("AWS_s3", "fileToUpload" + "--" + imageFile);
        String fileName = new File(ImageUrl).getName();
        new AWSBucketsSign(context, fileName, imageFile, "");
    }


    @Override
    public int getItemCount() {
        return signModelClasses.size();
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
            if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(context).compressToFile(new File(path));
                Log.d("path", tag + "-" + path);
            } else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

            Log.d("path", String.valueOf(yy));
        } catch (Exception ignored) {
        }
        return yy;
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

