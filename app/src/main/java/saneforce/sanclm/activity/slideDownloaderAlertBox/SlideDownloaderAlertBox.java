package saneforce.sanclm.activity.slideDownloaderAlertBox;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public  class SlideDownloaderAlertBox {

    public  static  int downloading_count ;
    public  static TextView txt_downloadcount;
    public  static  int dialogdismisscount;

    public static void openCustomDialog(Activity activity,String MoveingFlog ) {

        ArrayList<SlideModelClass> Slide_list=new ArrayList<>();
        SQLite sqLite =new SQLite(activity);
        JSONArray slidedata = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
        Slide_list.clear();
        try {
            if (slidedata.length() > 0) {
                for (int i = 0; i < slidedata.length(); i++) {
                    JSONObject jsonObject = slidedata.getJSONObject(i);
                    String FilePath = jsonObject.optString("FilePath");
                    Slide_list.add(new SlideModelClass(FilePath,"0","0","0"));
                }
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
        downloading_count=0;
        dialogdismisscount=0;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.slide_downloader_alert_box, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyelerview123);
        txt_downloadcount = dialogView.findViewById(R.id.txt_downloadcount);

        txt_downloadcount.setText("0/"+Slide_list.size());
        ImageView cancel_img = dialogView.findViewById(R.id.cancel_img);
        Slide_adapter  adapter = new Slide_adapter(activity, Slide_list);
        LinearLayoutManager  manager = new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);

        for (SlideModelClass slide : Slide_list) {

            String imageName = slide.getImageName();
            String downloadStatus = slide.getDownloadStatus();
            String progressValue = slide.getProgressValue();
            String img_size_status = slide.getDownloadSizeStatus();

            String url= "https://"+SharedPref.getLogInsite(activity)+"/"+SharedPref.getSlideUrl(activity)+imageName;
            Log.e("test", "Slide Url : " + url);
            new DownloadTask(activity, url, imageName, progressValue, downloadStatus, img_size_status, slide, adapter, recyclerView, dialog, MoveingFlog);
            adapter.notifyDataSetChanged();
        }


        cancel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }
}