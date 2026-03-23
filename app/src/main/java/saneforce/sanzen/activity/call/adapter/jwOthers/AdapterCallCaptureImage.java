package saneforce.sanzen.activity.call.adapter.jwOthers;

import static saneforce.sanzen.activity.call.DCRCallActivity.isFromActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanzen.AWS.AWSBuckets;
import saneforce.sanzen.AWS.S3DownloadFiles;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.fragments.jwOthers.JWOthersFragment;
import saneforce.sanzen.activity.call.pojo.CallCaptureImageList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class AdapterCallCaptureImage extends RecyclerView.Adapter<AdapterCallCaptureImage.ViewHolder> {
    Context context;
    ArrayList<CallCaptureImageList> callCaptureImageLists;
    private RoomDB roomDB;
    private CallOfflineECDataDao callOfflineECDataDao;
    ProgressDialog progressBar;
    CommonUtilsMethods commonUtilsMethods;

    public AdapterCallCaptureImage(Context context, ArrayList<CallCaptureImageList> callCaptureImageLists) {
        this.context = context;
        this.callCaptureImageLists = callCaptureImageLists;
        roomDB = RoomDB.getDatabase(context);
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_capture_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallCaptureImageList callCaptureImageList = callCaptureImageLists.get(position);
        holder.tv_image_name.setText(callCaptureImageList.getImg_name());
        holder.ed_img_desc.setText(callCaptureImageList.getImg_description());
        if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
            switch (isFromActivity) {
                case "new":
                    if (callCaptureImageList.getImg_view() == null) {
                        try {
                            Bitmap photo = BitmapFactory.decodeFile(callCaptureImageList.getFilePath());
                            holder.img_view.setImageBitmap(photo);
                            CallCaptureImageList callCaptureImageList1 = JWOthersFragment.callCaptureImageLists.get(position);
                            callCaptureImageList1.setImg_view(photo);
                            JWOthersFragment.callCaptureImageLists.set(position, callCaptureImageList1);
                        } catch (Exception e) {
                            Log.e("EC", "onBindViewHolder: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        holder.img_view.setImageBitmap(callCaptureImageList.getImg_view());
                    }
                    break;
                case "edit_local":
                    File imgFile = new File(callCaptureImageList.getFilePath());
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.img_view.setImageBitmap(myBitmap);
                    }
                    break;
                case "edit_online":
                    if (callCaptureImageList.isShowPreview()) {
                        if (callCaptureImageList.isNewlyAdded()) {
                            holder.img_view.setImageBitmap(callCaptureImageList.getImg_view());
                        }
                        if (callCaptureImageList.getImg_view() == null) {

                            Bitmap photo = BitmapFactory.decodeFile(callCaptureImageList.getFilePath());
                            holder.img_view.setImageBitmap(photo);
                            CallCaptureImageList callCaptureImageList1 = JWOthersFragment.callCaptureImageLists.get(position);
                            callCaptureImageList1.setImg_view(photo);
                            JWOthersFragment.callCaptureImageLists.set(position, callCaptureImageList1);
                        }
                    }
                    break;

            }
        } else {
            switch (isFromActivity) {
                case "new":
                    if (callCaptureImageList.getImg_view() == null) {
                        try {
                            Bitmap photo = BitmapFactory.decodeFile(callCaptureImageList.getFilePath());
                            holder.img_view.setImageBitmap(photo);
                            CallCaptureImageList callCaptureImageList1 = JWOthersFragment.callCaptureImageLists.get(position);
                            callCaptureImageList1.setImg_view(photo);
                            JWOthersFragment.callCaptureImageLists.set(position, callCaptureImageList1);
                        } catch (Exception e) {
                            Log.e("EC", "onBindViewHolder: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        holder.img_view.setImageBitmap(callCaptureImageList.getImg_view());
                    }
                    break;
                case "edit_local":
                    File imgFile = new File(callCaptureImageList.getFilePath());
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.img_view.setImageBitmap(myBitmap);
                    }
                    break;
                case "edit_online":
                    if (callCaptureImageList.isShowPreview()) {
                        if (callCaptureImageList.isNewlyAdded()) {
                            holder.img_view.setImageBitmap(callCaptureImageList.getImg_view());
                        } else {
                            //  Glide.with(context).load(SharedPref.getTagImageUrl(context) + "photos/" + callCaptureImageList.getSystemImgName()).fitCenter().into(holder.img_view);
                            Glide.with(context)
                                    .load(SharedPref.getTagImageUrl(context) + "photos/" + callCaptureImageList.getSystemImgName())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized
                                    .skipMemoryCache(false) // allow memory caching
                                    .fitCenter()
                                    .into(holder.img_view);
                        }
                    }
                    break;
            }
        }


//        holder.img_del_img.setOnClickListener(new SafeClickListener() {
//            @Override
//            public void onSafeClick(View view) {
//                Dialog dialog = new Dialog(context);
//                dialog.setContentView(R.layout.dcr_cancel_alert);
//                dialog.setCancelable(false);
//                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//                TextView btn_yes = dialog.findViewById(R.id.btn_yes);
//                TextView btn_no = dialog.findViewById(R.id.btn_no);
//                TextView title = dialog.findViewById(R.id.ed_alert_msg);
//                title.setText(R.string.are_you_sure_to_delete);
//                btn_yes.setOnClickListener(new SafeClickListener() {
//                    @Override
//                    public void onSafeClick(View view) {
//                        dialog.dismiss();
//                        File fileDelete = new File(callCaptureImageList.getFilePath());
//                        if (fileDelete.exists()) {
//                            if (fileDelete.delete()) {
////                        System.out.println("file Deleted :" + callCaptureImageList.getFilePath());
//                            } else {
////                        System.out.println("file not Deleted :" + callCaptureImageList.getFilePath());
//                            }
//                        }
//                        callOfflineECDataDao.deleteOfflineECImage(callCaptureImageList.getSystemImgName());
//                        removeAt(holder.getBindingAdapterPosition());
//                    }
//                });
//                btn_no.setOnClickListener(new SafeClickListener() {
//                    @Override
//                    public void onSafeClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//        });
        holder.img_del_img.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dcr_cancel_alert);
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                TextView btn_no = dialog.findViewById(R.id.btn_no);
                TextView title = dialog.findViewById(R.id.ed_alert_msg);
                title.setText(R.string.are_you_sure_to_delete);
                btn_yes.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        // 1. Adapter-kulla RecyclerView-ah kandupidiunga
                        RecyclerView rv_img_capture = (RecyclerView) holder.itemView.getParent();

                        if (rv_img_capture != null) {
                            // 2. Focus-ah block pannunga (Crash-ah thadukka)
                            rv_img_capture.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                            rv_img_capture.clearFocus();
                        }

                        dialog.dismiss();

                        final int currentPos = holder.getBindingAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            // ... Unga File & DB delete logic ...

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                removeAt(currentPos);

                                // 3. Delete mudinjadhukku apparam thirumba focus-ah open pannunga
                                if (rv_img_capture != null) {
                                    rv_img_capture.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                                }
                            }, 300);
                        }
                    }
                });
                btn_no.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void onSafeClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        holder.img_view.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Log.e("TAG", "onSafeClick: " + "on safe click ");
                ProgressDialog existingPB = progressBar;
                progressBar = CommonUtilsMethods.createProgressDialog(context);
                progressBar.show();
                if (existingPB != null && existingPB.isShowing()) {
                    new Handler().postDelayed(existingPB::dismiss, 500);
                }
                if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                    switch (isFromActivity) {
                        case "new":
                            showImage(callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_view());
//                            progressBar.dismiss();
                            break;
                        case "edit_local":
                            showImageLocal(callCaptureImageLists.get(holder.getBindingAdapterPosition()).getFilePath());
//                            progressBar.dismiss();
                            break;
                        case "edit_online":
                            if (UtilityClass.isNetworkAvailable(context)) {
                                if (callCaptureImageList.isNewlyAdded()) {
                                    if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                                        showImage(callCaptureImageList.getImg_view());
//                                        progressBar.dismiss();
                                    } else {
                                        showImage(callCaptureImageList.getImg_view());
//                                        progressBar.dismiss();
                                    }
                                } else {
                                    if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                                        ShowImageEditS3(callCaptureImageList.getSystemImgName(), holder, position);
//                                        progressBar.dismiss();
                                    } else {
                                        ShowImageEdit(callCaptureImageList.getSystemImgName());
//                                        progressBar.dismiss();
                                    }
                                }
                            } else {
                                progressBar.dismiss();
                                new CommonUtilsMethods(context).showToastMessage(context, context.getString(R.string.no_network_available));
                            }
                            break;
                    }
                } else {
                    switch (isFromActivity) {
                        case "new":
                            showImage(callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_view());
//                            progressBar.dismiss();
                            break;
                        case "edit_local":
                            showImageLocal(callCaptureImageLists.get(holder.getBindingAdapterPosition()).getFilePath());
//                            progressBar.dismiss();
                            break;
                        case "edit_online":
                            if (UtilityClass.isNetworkAvailable(context)) {
                                if (!callCaptureImageList.isShowPreview()) {
                                    callCaptureImageList.setShowPreview(true);
                                    notifyItemChanged(position);
                                }
                                if (callCaptureImageList.isNewlyAdded()) {
                                    showImage(callCaptureImageList.getImg_view());
//                                    progressBar.dismiss();
                                } else {
                                    if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                                        ShowImageEditS3(callCaptureImageList.getSystemImgName(), holder, position);
//                                        progressBar.dismiss();
                                    } else {
                                        ShowImageEdit(callCaptureImageList.getSystemImgName());
//                                        progressBar.dismiss();
                                    }
                                }
                            } else {
                                progressBar.dismiss();
                                new CommonUtilsMethods(context).showToastMessage(context, context.getString(R.string.no_network_available));
                            }
                            break;
                    }
                }
            }
        });
        holder.tv_image_name.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(holder.tv_image_name, 100)});

        holder.tv_image_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                callCaptureImageLists.set(holder.getBindingAdapterPosition(), new CallCaptureImageList(editable.toString(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_description(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_view(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getFilePath(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getSystemImgName(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).isNewlyAdded(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).isShowPreview()));
            }
        });
        holder.ed_img_desc.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(holder.ed_img_desc, 300)});

        holder.ed_img_desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                callCaptureImageLists.set(holder.getBindingAdapterPosition(), new CallCaptureImageList(callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_name(), editable.toString(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_view(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getFilePath(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getSystemImgName(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).isNewlyAdded(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).isShowPreview()));
            }
        });
    }

    private void showImageLocal(String filePath) {
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Dialog builder = new Dialog(context);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.setCancelable(true);
            Objects.requireNonNull(builder.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(myBitmap);
            builder.addContentView(imageView, new RelativeLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._300sdp), (int) context.getResources().getDimension(R.dimen._300sdp)));
            builder.show();
            progressBar.dismiss();
        } else {
            progressBar.dismiss();
        }
    }

    private void ShowImageEditS3(String systemImageName, @NonNull ViewHolder holder, int position) {
        CallCaptureImageList callCaptureImageList = callCaptureImageLists.get(position);
        String fileName = callCaptureImageList.getSystemImgName();
        File file = new File(context.getExternalFilesDir("JWOthersImages"), fileName);
        Log.d("TAG_acci", "onBindViewHolder: " + file.getAbsolutePath());
        new AWSBuckets(context, fileName, file, 0, "", new S3DownloadFiles() {
            @Override
            public void fileDataAdd(int pos, Bitmap bitmap) {
                if (bitmap != null) {
                    Log.d("bitmap image edit", "Image successfully loaded.");
                    holder.img_view.setImageBitmap(bitmap);
                    holder.img_view.setVisibility(View.VISIBLE);
                    showImage(bitmap);
                    progressBar.dismiss();
                } else {
                    Log.d("bitmap image", "Failed to load image, bitmap is null.");
                    holder.img_view.setVisibility(View.GONE);
                    progressBar.dismiss();
                }
            }

            @Override
            public void onFailure(int pos) {
                Log.d("bitmap image", "Failed to load image, bitmap is null.");
               // commonUtilsMethods.showToastMessage(context, "Image Not Found");
                commonUtilsMethods.showToastMessage(context,context.getString(R.string.image_not_found));
                holder.img_view.setVisibility(View.VISIBLE);
                progressBar.dismiss();
            }
        });
    }

    private void ShowImageEdit(String systemImgName) {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(true);
        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageView = new ImageView(context);
        Glide.with(context).load(SharedPref.getTagImageUrl(context) + "photos/" + systemImgName).fitCenter().into(imageView);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._300sdp), (int) context.getResources().getDimension(R.dimen._300sdp)));
        builder.show();
        progressBar.dismiss();
    }

    @Override
    public int getItemCount() {
        return callCaptureImageLists.size();
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
        progressBar.dismiss();
    }

//    public void removeAt(int position) {
//        callCaptureImageLists.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, callCaptureImageLists.size());
//    }
public void removeAt(int position) {
    if (position < callCaptureImageLists.size()) {
        callCaptureImageLists.remove(position);
        notifyItemRemoved(position);
        // Indha line romba mukkiyam, appo thaan matha items-oda position refresh aagum
        notifyItemRangeChanged(position, callCaptureImageLists.size());
    }
}
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_view, img_del_img;
        EditText tv_image_name;
        EditText ed_img_desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_view = itemView.findViewById(R.id.img_view);
            img_del_img = itemView.findViewById(R.id.img_del);
            tv_image_name = itemView.findViewById(R.id.tv_img_name);
            ed_img_desc = itemView.findViewById(R.id.ed_img_desc);
        }
    }
}
