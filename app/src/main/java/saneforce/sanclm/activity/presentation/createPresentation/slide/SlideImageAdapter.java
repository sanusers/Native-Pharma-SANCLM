package saneforce.sanclm.activity.presentation.createPresentation.slide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.presentation.createPresentation.BrandModelClass;

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
        getFromFilePath(product.getFileName(),holder);

        if (product.isImageSelected()){
            holder.tickImage.setVisibility(View.VISIBLE);
            holder.imageView.setImageAlpha(50);
        }else{
            holder.tickImage.setVisibility(View.GONE);
            holder.imageView.setImageAlpha(255);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                arrayList.get(holder.getAbsoluteAdapterPosition()).setImageSelected(!product.isImageSelected());
                imageSelectionInterface.imageSelection(arrayList,holder.getAbsoluteAdapterPosition());

            }
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
            String fileFormat = getFileExtension(fileName);
//            Log.e("test","file format is :" + fileFormat);
            Bitmap bitmap = null;
            switch (fileFormat){
                case "jpg" :
                case "png" :
                case "jpeg" :
                case "mp4" :{
//                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Glide.with(context).asBitmap().load(Uri.fromFile(new File(file.getAbsolutePath()))).into(holder.imageView);
                    return;
                }
                case "pdf" :{
                    bitmap = pdfToBitmap(file.getAbsoluteFile());
                    Glide.with(context).asBitmap().load(bitmap).into(holder.imageView);
                    return;
                }
                case "zip" :{
//                    unpackZip(file.getAbsolutePath());
                    break;
                }
                case "gif" :{
                    Glide.with(context).asGif().load(new File(file.getAbsolutePath())).into(holder.imageView);
                    return;
                }

            }

//            if (bitmap != null){
//                final Bitmap scaledBitmap = Bitmap.createScaledBitmap(
//                        bitmap,
//                        (int) (bitmap.getWidth() * 0.1),
//                        (int) (bitmap.getHeight() * 0.1),
//                        true
//                );
//            holder.imageView.setImageBitmap(scaledBitmap);
//            }

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

    private boolean unpackZip(String file) {

        try {
            InputStream inputStream = new FileInputStream(file);;
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));;

            ZipEntry zipEntry;

            while((zipEntry = zipInputStream.getNextEntry()) != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;

                String filename = zipEntry.getName();
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                // reading and writing
                while((count = zipInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, count);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    fileOutputStream.write(bytes);
                    byteArrayOutputStream.reset();
                }

                fileOutputStream.close();
                zipInputStream.closeEntry();
            }

            zipInputStream.close();

        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static void unzip (String zipFile, String targetDirectory) throws IOException {

        ZipInputStream zipInputStream = new ZipInputStream (new BufferedInputStream (new FileInputStream (zipFile)));

        try {
            ZipEntry zipEntry;
            int count;
            byte [] buffer = new byte [8192];
            while ((zipEntry = zipInputStream.getNextEntry ()) != null) {
                File file = new File (targetDirectory, zipEntry.getName ());
                File dir = zipEntry.isDirectory () ? file : file.getParentFile ();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath ());
                if (zipEntry.isDirectory())
                    continue;
                FileOutputStream fileOutputStream = new FileOutputStream (file);
                try {
                    while ( (count = zipInputStream.read (buffer)) != -1)
                        fileOutputStream.write (buffer, 0, count);
                } finally {
                    fileOutputStream.close ();
                }
            }
        } finally {
            zipInputStream.close ();
        }
    }


}
