package saneforce.sanclm.activity.slideDownloaderAlertBox;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;

public class Slide_adapter extends RecyclerView.Adapter<Slide_adapter.listDataViewholider> {
    Context context;
    ArrayList<SlideModelClass> list ;
    public Slide_adapter(Context context, ArrayList<SlideModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_slide_item, parent, false);
        return new listDataViewholider(view);

    }

    @Override
    public void onBindViewHolder(@NonNull listDataViewholider holder, int position) {
        holder.setIsRecyclable(false);

        holder.txt_imagename.setText(list.get(position).getImageName());
        holder.progressBar.setProgress(Integer.parseInt(list.get(position).getProgressValue()));
        holder.text_download_size.setText(list.get(position).getDownloadSizeStatus());

        if(list.get(position).getDownloadStatus().equalsIgnoreCase("2")){
            int redColor = Color.RED;
            ColorStateList colorStateList = ColorStateList.valueOf(redColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.progressBar.setProgressTintList(colorStateList);
                holder.text_retry.setVisibility(View.VISIBLE);
            }

        }  if(list.get(position).getDownloadStatus().equalsIgnoreCase("1")) {
            int redColor2 = context.getResources().getColor(R.color.Green_45);
            ColorStateList colorStateList = ColorStateList.valueOf(redColor2);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.progressBar.setProgressTintList(colorStateList);
                holder.text_retry.setVisibility(View.INVISIBLE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class listDataViewholider extends RecyclerView.ViewHolder {

        TextView txt_imagename,text_download_size,text_retry;
        public  ProgressBar progressBar;

        public listDataViewholider(@NonNull View itemView) {
            super(itemView);

            txt_imagename = itemView.findViewById(R.id.txt_imagename);
            progressBar = itemView.findViewById(R.id.img_download_progress);
            text_download_size = itemView.findViewById(R.id.txt_download_size);
            text_retry = itemView.findViewById(R.id.text_retry);


        }
    }






}