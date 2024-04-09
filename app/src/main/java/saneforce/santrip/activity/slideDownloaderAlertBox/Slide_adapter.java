package saneforce.santrip.activity.slideDownloaderAlertBox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.storage.SharedPref;

public class Slide_adapter extends RecyclerView.Adapter<Slide_adapter.listDataViewholider> {
    Activity activity;
    ArrayList<SlideModelClass> list ;


    public Slide_adapter(Activity activity, ArrayList<SlideModelClass> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_slide_item, parent, false);
        return new listDataViewholider(view);

    }

    @Override
    public void onBindViewHolder(@NonNull listDataViewholider holder, @SuppressLint("RecyclerView") int position) {
        holder.setIsRecyclable(false);

        holder.txt_imagename.setText(list.get(position).getImageName());
        if(!list.get(position).getProgressValue().equalsIgnoreCase("")){
            holder.progressBar.setProgress(Integer.parseInt(list.get(position).getProgressValue()));
        }
        if(!list.get(position).getDownloadSizeStatus().equalsIgnoreCase("")) {
            holder.text_download_size.setText(list.get(position).getDownloadSizeStatus());
        }
        if(list.get(position).getDownloadStatus()){
            int greencolor = activity.getResources().getColor(R.color.Green_45);
            ColorStateList colorStateList = ColorStateList.valueOf(greencolor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.progressBar.setProgressTintList(colorStateList);

            }
        }else {
            int redColor = Color.RED;
            ColorStateList colorStateList = ColorStateList.valueOf(redColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.progressBar.setProgressTintList(colorStateList);

            }
        }

        if (list.get(position).getDownloadSizeStatus().equalsIgnoreCase("Download failed")) {
            holder.text_retry.setVisibility(View.VISIBLE);

        } else {
            holder.text_retry.setVisibility(View.GONE);
        }


        holder.rl_title_layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String url= "https://"+ SharedPref.getLogInsite(activity)+"/"+SharedPref.getSlideUrl(activity)+list.get(position).getImageName();
               new DownloadTask(activity,url,list.get(position).getImageName(),list.get(position).getProgressValue(),list.get(position).getDownloadStatus(),list.get(position).getDownloadSizeStatus(),list.get(position), ()-> new ThumbnailTask(activity.getApplicationContext(), list.get(position).getImageName(), null));

           }
       });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class listDataViewholider extends RecyclerView.ViewHolder {

        TextView txt_imagename,text_download_size,text_retry;
        public  ProgressBar progressBar;

        RelativeLayout rl_title_layout;

        public listDataViewholider(@NonNull View itemView) {
            super(itemView);

            txt_imagename = itemView.findViewById(R.id.txt_imagename);
            progressBar = itemView.findViewById(R.id.img_download_progress);
            text_download_size = itemView.findViewById(R.id.txt_download_size);
            text_retry = itemView.findViewById(R.id.text_retry);
            rl_title_layout = itemView.findViewById(R.id.rl_calender_syn);


        }
    }
   public ArrayList<SlideModelClass> getList(){
        return list;
    }
}