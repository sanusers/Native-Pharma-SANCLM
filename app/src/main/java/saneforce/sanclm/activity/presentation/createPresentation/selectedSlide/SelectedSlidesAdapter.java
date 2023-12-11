package saneforce.sanclm.activity.presentation.createPresentation.selectedSlide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.presentation.SupportClass;
import saneforce.sanclm.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanclm.activity.presentation.createPresentation.slide.ImageSelectionInterface;

public class SelectedSlidesAdapter extends RecyclerView.Adapter<SelectedSlidesAdapter.MyViewHolder> implements ItemTouchHelperCallBack.ItemTouchHelperContract {

    Context context;
    ArrayList<BrandModelClass.Product> arrayList;
    ImageSelectionInterface imageSelectionInterface;
    ItemDragListener itemDragListener;

    public SelectedSlidesAdapter (Context context, ArrayList<BrandModelClass.Product> arrayList, ImageSelectionInterface imageSelectionInterface, ItemDragListener itemDragListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.imageSelectionInterface = imageSelectionInterface;
        this.itemDragListener = itemDragListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.presentation_selected_slides_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        BrandModelClass.Product product = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.setIsRecyclable(false);

        holder.brandName.setText(product.getBrandName());
        holder.fileName.setText(product.getFileName());
        getFromFilePath(product.getFileName(),holder);

        arrayList.get(holder.getAbsoluteAdapterPosition()).setDraggedPosition(holder.getAbsoluteAdapterPosition() + 1);

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                arrayList.get(holder.getAbsoluteAdapterPosition()).setImageSelected(false);
                imageSelectionInterface.imageSelection(arrayList,holder.getAbsoluteAdapterPosition());
            }
        });

        holder.dragIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    itemDragListener.requestDrag(holder);
                }
                return false;
            }
        });

        holder.dragIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick (View view) {
                itemDragListener.requestDrag(holder);
                return false;
            }
        });



    }

    @Override
    public int getItemCount () {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView brandName,fileName;
        ImageView slideImage;
        LinearLayout deleteIcon,dragIcon;
        View rowView;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.brandName);
            fileName = itemView.findViewById(R.id.fileName);
            slideImage = itemView.findViewById(R.id.slideImage);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            dragIcon = itemView.findViewById(R.id.dragIcon);
            rowView = itemView;
        }

    }

    public void getFromFilePath(String fileName, MyViewHolder holder){
        File file = new File(context.getExternalFilesDir(null)+ "/Slides/", fileName);
        if (file.exists()){
            String fileFormat = SupportClass.getFileExtension(fileName);
            Bitmap bitmap = null;
            switch (fileFormat){
                case "jpg" :
                case "png" :
                case "jpeg" :
                case "mp4" :{
                    Glide.with(context).asBitmap().load(Uri.fromFile(new File(file.getAbsolutePath()))).into(holder.slideImage);
                    return;
                }
                case "pdf" :{
                    bitmap = SupportClass.pdfToBitmap(file.getAbsoluteFile());
                    Glide.with(context).asBitmap().load(bitmap).into(holder.slideImage);
                    return;
                }
                case "zip" :{
                    bitmap = BitmapFactory.decodeFile(SupportClass.getFileFromZip(file.getAbsolutePath(),"image"));
                    if (bitmap != null)
                        Glide.with(context).asBitmap().load(bitmap).into(holder.slideImage);
                    return;
                }
                case "gif" :{
                    Glide.with(context).asGif().load(new File(file.getAbsolutePath())).into(holder.slideImage);
                    return;
                }
            }
        }
    }


    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        arrayList.get(fromPosition).setDraggedPosition(toPosition);
        arrayList.get(toPosition).setDraggedPosition(fromPosition);

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(arrayList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(arrayList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(context.getResources().getColor(R.color.green_2));
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }


}