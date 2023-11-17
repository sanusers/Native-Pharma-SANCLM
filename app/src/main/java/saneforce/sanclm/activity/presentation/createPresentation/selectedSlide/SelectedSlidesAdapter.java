package saneforce.sanclm.activity.presentation.createPresentation.selectedSlide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanclm.activity.presentation.createPresentation.slide.ImageSelectionInterface;
import saneforce.sanclm.activity.presentation.createPresentation.slide.SlideImageAdapter;

public class SelectedSlidesAdapter extends RecyclerView.Adapter<SelectedSlidesAdapter.MyViewHolder> {

    Context context;
    ArrayList<BrandModelClass.Product> arrayList;
    ImageSelectionInterface imageSelectionInterface;

    public SelectedSlidesAdapter () {
    }

    public SelectedSlidesAdapter (Context context, ArrayList<BrandModelClass.Product> arrayList,ImageSelectionInterface imageSelectionInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.imageSelectionInterface = imageSelectionInterface;
        Log.e("test","arrayList size : " + arrayList.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.presentation_selected_slides_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        BrandModelClass.Product product = arrayList.get(holder.getAbsoluteAdapterPosition());

        holder.brandName.setText(product.getBrandName());
        holder.fileName.setText(product.getFileName());
        getFromFilePath(product.getFileName(),holder);

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                arrayList.get(holder.getAbsoluteAdapterPosition()).setImageSelected(false);
                imageSelectionInterface.imageSelection(arrayList,holder.getAbsoluteAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount () {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView brandName,fileName;
        ImageView slideImage,dragIcon;
        LinearLayout deleteIcon;
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
            String fileFormat = getFileExtension(fileName);
//            Log.e("test","file format is :" + fileFormat);
            Bitmap bitmap = null;
            switch (fileFormat){
                case "jpg" :
                case "png" :
                case "jpeg" :
                case "mp4" :{
//                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Glide.with(context).asBitmap().load(Uri.fromFile(new File(file.getAbsolutePath()))).into(holder.slideImage);
                    return;
                }
                case "pdf" :{
                    bitmap = pdfToBitmap(file.getAbsoluteFile());
                    Glide.with(context).asBitmap().load(bitmap).into(holder.slideImage);
                    return;
                }
                case "zip" :{
//                    unpackZip(file.getAbsolutePath());
                    break;
                }
                case "gif" :{
                    Glide.with(context).asGif().load(new File(file.getAbsolutePath())).into(holder.slideImage);
                    return;
                }

            }

        }
    }

    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }


    private Bitmap pdfToBitmap(File pdfFile) {
        Bitmap bitmap = null;
        try {
            PdfRenderer renderer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                final int pageCount = renderer.getPageCount();
                if(pageCount > 0){
                    PdfRenderer.Page page = renderer.openPage(0);
                    int width = (int) (page.getWidth());
                    int height = (int) (page.getHeight());
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    page.close();
                    renderer.close();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    public void onItemMove(int sourcePosition, int targetPosition){
        Log.e("test","size in onItemMove : " + arrayList.size());
//        Collections.swap(arrayList,sourcePosition,targetPosition);
//        notifyItemMoved(sourcePosition,targetPosition);
    }


}
