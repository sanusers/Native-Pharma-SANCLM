package saneforce.santrip.activity.presentation.createPresentation.slide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.presentation.SupportClass;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;

public class SlideImageAdapter extends RecyclerView.Adapter<SlideImageAdapter.MyViewHolder> {

    Context context;
    ArrayList<BrandModelClass.Product> arrayList;
    ImageSelectionInterface imageSelectionInterface;

    public SlideImageAdapter () {
    }

    public SlideImageAdapter (Context context, ArrayList<BrandModelClass.Product> arrayList,ImageSelectionInterface imageSelectionInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.imageSelectionInterface = imageSelectionInterface;
    }

    @NonNull
    @Override
    public SlideImageAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.presentation_image_adapter_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull SlideImageAdapter.MyViewHolder holder, int position) {
        BrandModelClass.Product product = arrayList.get(holder.getAbsoluteAdapterPosition());
        getFromFilePath(product.getSlideName(),holder);

        if (product.isImageSelected()){
            holder.tickImage.setVisibility(View.VISIBLE);
            holder.imageView.setImageAlpha(50);
        }else{
            holder.tickImage.setVisibility(View.GONE);
            holder.imageView.setImageAlpha(255);
        }

        holder.itemView.setOnClickListener(view -> {
            arrayList.get(holder.getAbsoluteAdapterPosition()).setImageSelected(!product.isImageSelected());
            arrayList.get(holder.getAbsoluteAdapterPosition()).setDraggedPosition(-1);
            imageSelectionInterface.imageSelection(arrayList,holder.getAbsoluteAdapterPosition());
        });

    }

    @Override
    public int getItemCount () {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        LinearLayout tickImage;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slideImage);
            tickImage = itemView.findViewById(R.id.tickImage);

        }
    }

    public void getFromFilePath(String fileName,MyViewHolder holder){
        File file = new File(context.getExternalFilesDir(null)+ "/Slides/", fileName);
        if (file.exists()){
            String fileFormat = SupportClass.getFileExtension(fileName);
            Bitmap bitmap = null;
            switch (fileFormat){
                case "jpg" :
                case "png" :
                case "jpeg" :
                case "mp4" :{
                    Glide.with(context).asBitmap().load(Uri.fromFile(new File(file.getAbsolutePath()))).into(holder.imageView);
                    return;
                }
                case "pdf" :{
                    bitmap = SupportClass.pdfToBitmap(file.getAbsoluteFile());
                    Glide.with(context).asBitmap().load(bitmap).into(holder.imageView);
                    return;
                }
                case "zip" :{
                    bitmap = BitmapFactory.decodeFile(SupportClass.getFileFromZip(file.getAbsolutePath(),"image"));
                    if (bitmap != null)
                        Glide.with(context).asBitmap().load(bitmap).into(holder.imageView);
                    return;
                }
                case "gif" :{
                    Glide.with(context).asGif().load(new File(file.getAbsolutePath())).into(holder.imageView);
                    return;
                }
            }
        }
    }



}
