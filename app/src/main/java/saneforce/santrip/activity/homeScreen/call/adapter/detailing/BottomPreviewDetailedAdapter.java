package saneforce.santrip.activity.homeScreen.call.adapter.detailing;

import static saneforce.santrip.activity.previewPresentation.PreviewActivity.from_where;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.presentation.SupportClass;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.activity.presentation.playPreview.PlaySlidePreviewActivity;

public class BottomPreviewDetailedAdapter extends RecyclerView.Adapter<BottomPreviewDetailedAdapter.MyViewHolder> {

    Context context;
    ArrayList<BrandModelClass.Product> arrayList;
    ViewPager viewPager;

    public BottomPreviewDetailedAdapter(Context context, ArrayList<BrandModelClass.Product> arrayList, ViewPager viewPager) {
        this.context = context;
        this.arrayList = arrayList;
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public BottomPreviewDetailedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.presentation_btm_preview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomPreviewDetailedAdapter.MyViewHolder holder, int position) {
        getFromFilePath(arrayList.get(holder.getAbsoluteAdapterPosition()).getSlideName(), holder.imageView);

        holder.itemView.setSelected(viewPager.getCurrentItem() == holder.getAbsoluteAdapterPosition());

        holder.itemView.setOnClickListener(view -> {
            viewPager.setCurrentItem(holder.getAbsoluteAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public void getFromFilePath(String fileName, ImageView imageView) {
        File file = new File(context.getExternalFilesDir(null) + "/Slides/", fileName);
        if (file.exists()) {
            String fileFormat = SupportClass.getFileExtension(fileName);
            Bitmap bitmap = null;
            switch (fileFormat) {
                case "jpg":
                case "png":
                case "jpeg":
                case "mp4": {
                    Glide.with(context).asBitmap().load(Uri.fromFile(new File(file.getAbsolutePath()))).into(imageView);
                    return;
                }
                case "pdf": {
                    bitmap = SupportClass.pdfToBitmap(file.getAbsoluteFile());
                    Glide.with(context).asBitmap().load(bitmap).into(imageView);
                    return;
                }
                case "zip": {
                    bitmap = BitmapFactory.decodeFile(SupportClass.getFileFromZip(file.getAbsolutePath(), "image"));
                    if (bitmap != null)
                        Glide.with(context).asBitmap().load(bitmap).into(imageView);
                    return;
                }
                case "gif": {
                    Glide.with(context).asGif().load(new File(file.getAbsolutePath())).into(imageView);
                    return;
                }
            }
        }
    }


}
