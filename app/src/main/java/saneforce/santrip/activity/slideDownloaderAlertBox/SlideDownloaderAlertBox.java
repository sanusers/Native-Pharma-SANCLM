package saneforce.santrip.activity.slideDownloaderAlertBox;

import android.annotation.SuppressLint;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.masterSync.DialogboxClass;
import saneforce.santrip.activity.masterSync.MasterSyncActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class SlideDownloaderAlertBox {

    public static int downloading_count;
    public static TextView txt_downloadcount;
    public static int dialogdismisscount;
    public static Slide_adapter adapter;
    public static RecyclerView recyclerView;
    public static Dialog dialog;
    static int totalcount = 0;
    public static boolean MoveMainFlag;
    public static ArrayList<String> slideId123 = new ArrayList<>();


    @SuppressLint("NotifyDataSetChanged")
    public static void openCustomDialog(Activity activity, boolean MoveFlag) {
        try {
        ArrayList<SlideModelClass> Slide_list = new ArrayList<>();
        ArrayList<String> slideIdList = new ArrayList<>();
        SQLite sqLite=new SQLite(activity);
        boolean AleartShowFlag=false;
        MoveMainFlag = MoveFlag;
        CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(activity);
        if (MoveFlag) {
            downloading_count = 0;
            dialogdismisscount = 0;
            totalcount = 0;
            Slide_list.clear();
            slideIdList.clear();
        }

        String slideids = SharedPref.GetSlideID(activity);
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        slideIdList = new Gson().fromJson(slideids, listType);
        SlideDownloaderAlertBox.downloading_count = Integer.valueOf(slideIdList.size());
        JSONArray slidedata = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
        if (slidedata.length() > 0) {
                for (int i = 0; i < slidedata.length(); i++) {
                    JSONObject jsonObject = slidedata.getJSONObject(i);
                    String FilePath = jsonObject.optString("FilePath");
                    String id = jsonObject.optString("SlideId");
                    if (slideIdList.contains(id)) {
                        Slide_list.add(new SlideModelClass(FilePath, true, "Download Completed", "100",id));
                    }else{
                        AleartShowFlag=true;
                        Slide_list.add(new SlideModelClass(FilePath, false, "", "",id));
                    }
                }
            } else {
                Slide_list.clear();
                slideIdList.clear();
            }

        if (Slide_list.size() > 0) {
            if(AleartShowFlag){
                totalcount = Slide_list.size();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.slide_downloader_alert_box, null);
            recyclerView = dialogView.findViewById(R.id.recyelerview123);
            txt_downloadcount = dialogView.findViewById(R.id.txt_downloadcount);
            txt_downloadcount.setText(String.format("%d/%d", downloading_count, totalcount));
            ImageView cancel_img = dialogView.findViewById(R.id.cancel_img);
            adapter = new Slide_adapter(activity, Slide_list);
            LinearLayoutManager manager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
            builder.setView(dialogView);
            dialog = builder.create();
            HomeDashBoard.dialog=dialog;
            if (!txt_downloadcount.getText().toString().equalsIgnoreCase("0/0")) {
                dialog.show();
            }
            dialog.setCancelable(false);
                MasterSyncActivity.binding.imgDownloading.setVisibility(View.VISIBLE);
            for (SlideModelClass slide : Slide_list) {
                String imageName = slide.getImageName();
                boolean downloadStatus = slide.getDownloadStatus();
                String progressValue = slide.getProgressValue();
                String img_size_status = slide.getDownloadSizeStatus();

                if (!downloadStatus) {
                    SharedPref.putSlidestatus(activity.getApplicationContext(),false);
                    String url = "https://" + SharedPref.getLogInsite(activity) + "/" + SharedPref.getSlideUrl(activity) + imageName;
                    new DownloadTask(activity, url, imageName, progressValue, downloadStatus, img_size_status, slide, () -> new ThumbnailTask(activity.getApplicationContext(), imageName, null));
                }
            }
                adapter.notifyDataSetChanged();
            cancel_img.setOnClickListener(v -> {
                    MoveMainFlag = false;
                    dialog.dismiss();
                });
            }else {
                MasterSyncActivity.binding.imgDownloading.setVisibility(View.GONE);
                commonUtilsMethods.showToastMessage(activity, "Already All Slide Downloaded");
            }
            DialogboxClass dialogboxClass=new DialogboxClass();
            dialogboxClass.setDialog(dialog);
        } else {
            downloading_count = 0;
            totalcount = 0;
            commonUtilsMethods.showToastMessage(activity, activity.getString(R.string.no_slides));
            if (MoveMainFlag) {
                activity.startActivity(new Intent(activity, HomeDashBoard.class));
            }
        }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

    }

}