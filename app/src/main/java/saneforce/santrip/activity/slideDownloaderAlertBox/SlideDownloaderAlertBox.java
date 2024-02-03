package saneforce.santrip.activity.slideDownloaderAlertBox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.storage.SharedPref;

public  class SlideDownloaderAlertBox {

    public  static  int downloading_count ;
    public  static TextView txt_downloadcount;
    public  static  int dialogdismisscount;

    static int totalcount=0;
    public  static   Slide_adapter  adapter ;
    public  static   RecyclerView recyclerView ;
    public  static   Dialog dialog ;



    public static void openCustomDialog(Activity activity,String MoveingFlog ,ArrayList<SlideModelClass> Slide_list) {
        CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(activity);
        if(MoveingFlog.equalsIgnoreCase("1")) {
            downloading_count = 0;
            dialogdismisscount = 0;
            totalcount=0;
        }

        if(Slide_list.size()>0){
            totalcount=Slide_list.size();
        }else {

            downloading_count=0;
            totalcount=0;
            commonUtilsMethods.ShowToast(activity ,activity.getString(R.string.no_slides),100);
            if(MoveingFlog.equalsIgnoreCase("1")) {
                activity.startActivity(new Intent(activity, HomeDashBoard.class));}
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.slide_downloader_alert_box, null);
        recyclerView = dialogView.findViewById(R.id.recyelerview123);
        txt_downloadcount = dialogView.findViewById(R.id.txt_downloadcount);

        txt_downloadcount.setText(String.valueOf(downloading_count) + "/" +String.valueOf(totalcount ));
        ImageView cancel_img = dialogView.findViewById(R.id.cancel_img);
        adapter = new Slide_adapter(activity, Slide_list);
        LinearLayoutManager  manager = new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);

        for (SlideModelClass slide : Slide_list) {

            String imageName = slide.getImageName();
            boolean downloadStatus = slide.getDownloadStatus();
            String progressValue = slide.getProgressValue();
            String img_size_status = slide.getDownloadSizeStatus();


            if(!downloadStatus){

            String url= "https://"+SharedPref.getLogInsite(activity)+"/"+SharedPref.getSlideUrl(activity)+imageName;
            new DownloadTask(activity, url, imageName, progressValue, downloadStatus, img_size_status, slide,MoveingFlog);

            }
        }

        adapter.notifyDataSetChanged();

        cancel_img.setOnClickListener(v -> {
            SharedPref.saveSlideDownloadingList(activity, String.valueOf(downloading_count),Slide_list);
            dialog.dismiss();
        });
    }
}