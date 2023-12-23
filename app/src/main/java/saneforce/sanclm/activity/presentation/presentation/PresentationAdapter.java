package saneforce.sanclm.activity.presentation.presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.presentation.SupportClass;
import saneforce.sanclm.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanclm.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.sanclm.activity.presentation.playPreview.PlaySlidePreviewActivity;
import saneforce.sanclm.storage.SQLite;

public class PresentationAdapter extends RecyclerView.Adapter<PresentationAdapter.MyViewHolder> {
    Context context;
    ArrayList<BrandModelClass.Presentation> arrayList = new ArrayList<>();
    SQLite sqLite;

    public PresentationAdapter (Context context, ArrayList<BrandModelClass.Presentation> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        sqLite = new SQLite(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.presentation_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BrandModelClass.Presentation presentation = arrayList.get(position);
        ArrayList<BrandModelClass.Product> products = presentation.getProducts();

        if (products.size() > 0)
            getFromFilePath(products.get(0).getFileName(),holder);

        holder.name.setText(presentation.getPresentationName());
        if (products.size() > 1)
            holder.count.setText(products.size() + " Asserts");
        else
            holder.count.setText(products.size() + " Assert");

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                final PopupMenu popup = new PopupMenu(wrapper, view, Gravity.END);
                popup.inflate(R.menu.presentation_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        PresentationActivity presentationActivity = (PresentationActivity) context;

                        if (menuItem.getItemId() == R.id.menuPlay) {
                            Intent intent = new Intent(presentationActivity, PlaySlidePreviewActivity.class);
                            String data = new Gson().toJson(products);
                            Bundle bundle = new Bundle();
                            bundle.putString("slideBundle",data);
                            intent.putExtra("bundle",bundle);
                            context.startActivity(intent);
                        } else if (menuItem.getItemId() == R.id.menuEdit) {
                            Intent intent = new Intent(presentationActivity, CreatePresentationActivity.class);
                            String data = new Gson().toJson(products);
                            Bundle bundle = new Bundle();
                            bundle.putString("slideBundle",data);
                            bundle.putString("presentationName",presentation.getPresentationName());
                            intent.putExtra("bundle",bundle);
                            context.startActivity(intent);
                        } else if (menuItem.getItemId() == R.id.menuDelete) {
                            removeAt(position);
                            sqLite.presentationDelete(presentation.getPresentationName());
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout menu;
        TextView name, count;
        ImageView imageView;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.verticalDot);
            count = itemView.findViewById(R.id.assertCount);
            name = itemView.findViewById(R.id.presentationName);
            imageView = itemView.findViewById(R.id.imageView);

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

    public void removeAt(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
    }

}
