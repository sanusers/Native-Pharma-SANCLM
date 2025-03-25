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

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanzen.AWS.AWSBuckets;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.RoomDB;


public class OutBoxECAdapter extends RecyclerView.Adapter<OutBoxECAdapter.ViewHolder> {
    Context context;
    ArrayList<EcModelClass> ecModelClasses;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    private final CallOfflineECDataDao callOfflineECDataDao;
    private final CallOfflineDataDao callOfflineDataDao;
    private final OfflineDaySubmitDao offlineDaySubmitDao;

    public OutBoxECAdapter(Activity activity, Context context, ArrayList<EcModelClass> ecModelClasses) {
        this.activity = activity;
        this.context = context;
        this.ecModelClasses = ecModelClasses;
        commonUtilsMethods = new CommonUtilsMethods(context);
        RoomDB roomDB = RoomDB.getDatabase(context);
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
    }

    @NonNull
    @Override
    public OutBoxECAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_ec_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxECAdapter.ViewHolder holder, int position) {
        holder.tvImageName.setText(ecModelClasses.get(position).getImg_name());
        holder.tvStatus.setText(ecModelClasses.get(position).getSync_status());

        File imgFile = new File(ecModelClasses.get(position).getFilePath());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imgView.setImageBitmap(myBitmap);
        }

        holder.imgView.setOnClickListener(v -> {
            File imgFile1 = new File(ecModelClasses.get(position).getFilePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
            showImage(myBitmap);
        });

        holder.tvMenu.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
            popup.inflate(R.menu.ec_call_menu);
            MenuItem deleteMenu = popup.getMenu().findItem(R.id.menuDelete);
            if(offlineDaySubmitDao.getDaySubmit(ecModelClasses.get(position).getDates()) != null) {
                deleteMenu.setVisible(false);
            } else {
                deleteMenu.setVisible(true);
            }
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menuSync) {
                    CallImageApi(ecModelClasses.get(position));
                    if (UtilityClass.isNetworkAvailable(context)) {
                        CallImageApi(ecModelClasses.get(position));
                    } else {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                    }
                } else if (menuItem.getItemId() == R.id.menuDelete) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dcr_cancel_alert);
                    dialog.setCancelable(false);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    TextView btn_yes=dialog.findViewById(R.id.btn_yes);
                    TextView btn_no=dialog.findViewById(R.id.btn_no);
                    TextView titte=dialog.findViewById(R.id.ed_alert_msg);
                    titte.setText(R.string.are_you_sure_to_delete);

                    btn_yes.setOnClickListener(view -> {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject;
                            jsonObject = new JSONObject(callOfflineDataDao.getJsonCallList(ecModelClasses.get(position).getDates(), ecModelClasses.get(position).getCusCode()));
                            JSONArray jsonArray = jsonObject.getJSONArray("EventCapture");
                            for (int i = 0; i<jsonArray.length(); i++) {
                                JSONObject jsonObjectEC = jsonArray.getJSONObject(i);
                                if(jsonObjectEC.getString("EventImageName").equalsIgnoreCase(ecModelClasses.get(position).getImg_name())) {
                                    jsonArray.remove(i);
                                    break;
                                }
                            }

                            for (int i = 0; i<listDates.size(); i++) {
                                if(listDates.get(i).getGroupName().equalsIgnoreCase(ecModelClasses.get(position).getDates())) {
                                    for (int j = 0; j<listDates.get(i).getChildItems().get(2).getOutBoxCallLists().size(); j++) {
                                        OutBoxCallList outBoxCallList = listDates.get(i).getChildItems().get(2).getOutBoxCallLists().get(j);
                                        if(outBoxCallList.getCusCode().equalsIgnoreCase(ecModelClasses.get(position).getImg_name())) {
                                            jsonObject = new JSONObject(outBoxCallList.getJsonData());
                                            for (int m = 0; m<jsonArray.length(); m++) {
                                                JSONObject jsonObjectEC = jsonArray.getJSONObject(i);
                                                if(jsonObjectEC.getString("EventImageName").equalsIgnoreCase(ecModelClasses.get(position).getImg_name())) {
                                                    jsonArray.remove(i);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            callOfflineDataDao.saveOfflineUpdateJson(ecModelClasses.get(position).getDates(), ecModelClasses.get(position).getCusCode(), jsonObject.toString());
                            outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
                            commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
                            outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
                            outBoxHeaderAdapter.notifyDataSetChanged();
                        } catch (Exception ignored) {
                        }
                        File fileDelete = new File(ecModelClasses.get(position).getFilePath());
                        if(fileDelete.exists()) {
                            if(fileDelete.delete()) {
//                                System.out.println("file Deleted :" + ecModelClasses.get(position).getFilePath());
                            }else {
//                                System.out.println("file not Deleted :" + ecModelClasses.get(position).getFilePath());
                            }
                        }
                        callOfflineECDataDao.deleteOfflineEC(String.valueOf(ecModelClasses.get(position).getId()));
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

    private void CallImageApi(/*int parentPos,*/ EcModelClass ecModelClass /*int childPos, int CurrentPos,*/
                              /*String jsonValues,*/ /*String filePath *//*, String id, GroupModelClass modelClass*/) {
        try {

            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    "ap-south-1:c4c0fc81-118d-43e3-84cf-051f1bd831b9",
                    Regions.AP_SOUTH_1);

            AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
            s3Client.setRegion(Region.getRegion(Regions.AP_SOUTH_1));


            File fileToUpload = new File(ecModelClass.getFilePath());
            Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
            if (!fileToUpload.exists()) {
                Log.d("fileToUpload", "not exists: " + ecModelClass.getFilePath());
            } else {

                String bucketName = "san.one";
                String s3Key = "uploads/" + fileToUpload.getName();
                Log.d("TAG", "CallSendAPIImage: " + s3Key);

                String UploadUrl = "https://" + "s3." + "ap-south-1." + "amazonaws.com/" + bucketName + "/" + s3Key;
                Log.d("S3UploadUrl", "CallSendAPIImage: " + UploadUrl);

                TransferNetworkLossHandler.getInstance(context);

                TransferUtility transferUtility = TransferUtility.builder()
                        .context(context)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .defaultBucket(bucketName)
                        .build();

                TransferObserver uploadObserver = transferUtility.upload(
                        bucketName,
                        s3Key,
                        fileToUpload);
                uploadObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            Log.d("TAG", "ecModelClass: " + ecModelClass.getFilePath());
                            InsertImage(ecModelClass.getFilePath(), context);
                            /*DeleteCacheFile(filePath, "", CurrentPos, parentPos, childPos, modelClass);*/
                            Log.d("S3 Upload", "Upload Successful: " + s3Key);


                        } else if (state == TransferState.FAILED) {

                            Log.e("S3 Upload", "Upload Failed");
                            InsertImage(ecModelClass.getFilePath(), context);

                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.CALL_FAILED);
                            callOfflineECDataDao.updateECStatus("", Constants.CALL_FAILED, 1);
//                            CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
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
                        ecModelClass.setSynced(1);
                        ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineECDataDao.updateECStatus("", Constants.EXCEPTION_ERROR, 1);
                    }
                });

            }
        } catch(Exception e){
            Log.v("img_tag", e.toString());
        }
    }
   private void InsertImage(final String ImageUrl, Context context) {
        File imageFile = new File(ImageUrl);
        Log.d("AWS_s3", "fileToUpload" + "--" + imageFile);
        String fileName = new File(ImageUrl).getName();
        new AWSBuckets(context,fileName,imageFile,"");
    }

    @Override
    public int getItemCount() {
        return ecModelClasses.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        ecModelClasses.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ecModelClasses.size());
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
