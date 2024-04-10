package saneforce.santrip.activity.slideDownloaderAlertBox;

import static saneforce.santrip.activity.slideDownloaderAlertBox.SlideDownloaderAlertBox.downloading_count;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.masterSync.MasterSyncActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.storage.SharedPref;

public class DownloadTask {
    private static final String TAG = "DownloadTask";
    Context context;
    String progressBar_value, downloadmsg;
    int processvalue;
    SlideModelClass Slidevalue;
    SharedPreferences sharedpreferences;
    boolean downloadingstatus;
    private final Activity activity;
    private String downloadUrl = "";
    private String downloadFileName = "";
    private TaskCallback taskCallback;

    public DownloadTask(Activity activity, String downloadUrl, String filename, String progressBar_value, boolean downloadingstatus, String downloadmsg, SlideModelClass slidevalue, TaskCallback taskCallback) {
        this.activity = activity;
        this.downloadUrl = downloadUrl;
        this.downloadFileName = filename;
        this.progressBar_value = progressBar_value;
        this.downloadingstatus = downloadingstatus;
        this.downloadmsg = downloadmsg;
        this.Slidevalue = slidevalue;
        this.taskCallback = taskCallback;

        this.context = activity.getApplicationContext();
        processvalue = 0;
        new DownloadingTask().execute();
    }

    public static void unzip(String filepath, File targetDirectory) throws IOException {

        File zipFile = new File(filepath);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        try {

            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {

                String name = ze.getName();
                File file = new File(targetDirectory, name);

                if (ze.isDirectory()) {
                    file.mkdirs();
                } else {
                    FileOutputStream fout = new FileOutputStream(file);
                    try {
                        while ((count = zis.read(buffer)) != -1) {
                            fout.write(buffer, 0, count);
                        }
                    } finally {
                        fout.close();
                    }
                }
            }
        } finally {
            zis.close();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadingTask extends AsyncTask<Void, Integer, Boolean> {
        File apkStorage = null;
        File outputFile = null;
        int totalSize;
        int downloadedSize = 0;

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                    return false;
                }

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    apkStorage = new File(activity.getExternalFilesDir(null) + "/Slides/");
                } else {
                    return false;
                }

                if (!apkStorage.exists()) {
                    if (!apkStorage.mkdirs()) {
                        Log.e(TAG, "Directory Creation Failed.");
                        return false;
                    }
                }

                outputFile = new File(apkStorage, downloadFileName);

                if (outputFile.exists()) {
                    if (outputFile.delete()) {
                        Log.e(TAG, "Old file deleted.");
                    } else {
                        Log.e(TAG, "Failed to delete old file.");
                    }
                }

                if (!outputFile.createNewFile()) {
                    Log.e(TAG, "File Creation Failed.");
                    return false;
                }

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = connection.getInputStream();
                totalSize = connection.getContentLength();

                byte[] buffer = new byte[1024];
                int len1;


                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloadedSize += len1;
                    publishProgress((int) (downloadedSize * 100 / totalSize));
                }


                if (downloadFileName.contains("zip")) {
                    String filePath = outputFile.getAbsolutePath();
                    File unzipDir = new File(activity.getExternalFilesDir(null), "/Slides/");
                    unzip(filePath, unzipDir);
                }

                fos.close();
                is.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Slidevalue.setDownloadSizeStatus("Network Error");
                Log.e(TAG, "Download Error Exception " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            String progressText = String.format("%.1f MB of %.1f MB", downloadedSize / (1024.0 * 1024), totalSize / (1024.0 * 1024));
            Slidevalue.setDownloadSizeStatus(progressText);
            Slidevalue.setProgressValue(String.valueOf(progress));
            Slidevalue.setDownloadStatus(true);
            processvalue = progress;
            SlideDownloaderAlertBox.adapter.notifyDataSetChanged();

        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(Boolean isDownloadSuccessful) {
            super.onPostExecute(isDownloadSuccessful);
            CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(activity);
            if (isDownloadSuccessful) {
                Log.e(TAG, "Download  :" + downloading_count);
                Slidevalue.setDownloadSizeStatus("Download completed");
                Slidevalue.setProgressValue(String.valueOf(100));
                Slidevalue.setDownloadStatus(true);
                downloading_count++;
                taskCallback.onTaskCompleted();
                SlideDownloaderAlertBox.slideId123.add(Slidevalue.getId());
                SlideDownloaderAlertBox.dialogdismisscount++;
                SlideDownloaderAlertBox.txt_downloadcount.setText(downloading_count + "/" + SlideDownloaderAlertBox.adapter.getItemCount());
                SlideDownloaderAlertBox.recyclerView.getLayoutManager().scrollToPosition(downloading_count);
                SlideDownloaderAlertBox.adapter.notifyDataSetChanged();
                if (SlideDownloaderAlertBox.dialogdismisscount == SlideDownloaderAlertBox.adapter.getItemCount()) {
                    SlideDownloaderAlertBox.dialog.dismiss();
                    SharedPref.saveSlideDownloadingList(activity, String.valueOf(downloading_count), SlideDownloaderAlertBox.adapter.getList(), SlideDownloaderAlertBox.slideId123);
                    if (SlideDownloaderAlertBox.MoveMainFlag) {
                        Intent intent = new Intent(context, HomeDashBoard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        activity.finish();
                    } else {

                        commonUtilsMethods.showToastMessage(activity, "All Downloading Completed ");

                    }
                    SharedPref.putSlidestatus(context,true);
                    MasterSyncActivity.binding.imgDownloading.setVisibility(View.GONE);
                }
            } else {
                Slidevalue.setProgressValue(String.valueOf(processvalue));
                SlideDownloaderAlertBox.txt_downloadcount.setText(downloading_count + "/" + SlideDownloaderAlertBox.adapter.getItemCount());
                Slidevalue.setDownloadSizeStatus("Download failed");
                Slidevalue.setDownloadStatus(false);
                SlideDownloaderAlertBox.dialogdismisscount++;
                SlideDownloaderAlertBox.adapter.notifyDataSetChanged();

                if (SlideDownloaderAlertBox.dialogdismisscount == SlideDownloaderAlertBox.adapter.getItemCount()) {
                    SharedPref.saveSlideDownloadingList(activity, String.valueOf(downloading_count), SlideDownloaderAlertBox.adapter.getList(), SlideDownloaderAlertBox.slideId123);
                    SlideDownloaderAlertBox.dialog.dismiss();
                    if (SlideDownloaderAlertBox.MoveMainFlag) {
                        Intent intent = new Intent(context, HomeDashBoard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        activity.finish();
                    } else {
                        commonUtilsMethods.showToastMessage(activity, "All Slide Downloading Completed ");
                    }
                    MasterSyncActivity.binding.imgDownloading.setVisibility(View.GONE);
                    SharedPref.putSlidestatus(context,true);
                }

            }


        }
    }
}
