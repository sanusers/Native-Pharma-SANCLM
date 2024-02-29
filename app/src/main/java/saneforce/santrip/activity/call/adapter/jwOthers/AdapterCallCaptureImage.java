package saneforce.santrip.activity.call.adapter.jwOthers;

import static saneforce.santrip.activity.call.DCRCallActivity.isFromActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.pojo.CallCaptureImageList;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class AdapterCallCaptureImage extends RecyclerView.Adapter<AdapterCallCaptureImage.ViewHolder> {
    Context context;
    ArrayList<CallCaptureImageList> callCaptureImageLists;
    SQLite sqLite;

    public AdapterCallCaptureImage(Context context, ArrayList<CallCaptureImageList> callCaptureImageLists) {
        this.context = context;
        this.callCaptureImageLists = callCaptureImageLists;
        sqLite = new SQLite(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_capture_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_image_name.setText(callCaptureImageLists.get(position).getImg_name());
        holder.ed_img_desc.setText(callCaptureImageLists.get(position).getImg_description());

        switch (isFromActivity) {
            case "new":
                holder.img_view.setImageBitmap(callCaptureImageLists.get(position).getImg_view());
                break;
            case "edit_local":
                File imgFile = new File(callCaptureImageLists.get(position).getFilePath());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    holder.img_view.setImageBitmap(myBitmap);
                }
                break;
            case "edit_online":
                break;
        }


        holder.img_del_img.setOnClickListener(v -> {
            File fileDelete = new File(callCaptureImageLists.get(position).getFilePath());
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
                    System.out.println("file Deleted :" + callCaptureImageLists.get(position).getFilePath());
                } else {
                    System.out.println("file not Deleted :" + callCaptureImageLists.get(position).getFilePath());
                }
            }
            sqLite.deleteOfflineECImage(callCaptureImageLists.get(position).getSystemImgName());
            removeAt(holder.getBindingAdapterPosition());
        });


        holder.img_view.setOnClickListener(v -> {
            switch (isFromActivity) {
                case "new":
                    showImage(callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_view());
                    break;
                case "edit_local":
                    showImageLocal(callCaptureImageLists.get(holder.getBindingAdapterPosition()).getFilePath());
                    break;
                case "edit_online":
                    ShowImageEdit(callCaptureImageLists.get(position).getSystemImgName());
                    break;
            }
        });

        holder.tv_image_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                callCaptureImageLists.set(holder.getBindingAdapterPosition(), new CallCaptureImageList(editable.toString(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_description(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_view(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getFilePath(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getSystemImgName(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).isNewlyAdded()));
            }
        });

        holder.ed_img_desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                callCaptureImageLists.set(holder.getBindingAdapterPosition(), new CallCaptureImageList(callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_name(), editable.toString(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getImg_view(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getFilePath(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).getSystemImgName(), callCaptureImageLists.get(holder.getBindingAdapterPosition()).isNewlyAdded()));
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
        }
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
    }

    public void removeAt(int position) {
        callCaptureImageLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, callCaptureImageLists.size());
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