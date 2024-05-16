package saneforce.santrip.activity.SlideDownloadNew;

import static saneforce.santrip.activity.slideDownloaderAlertBox.SlideDownloaderAlertBox.downloading_count;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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

import io.reactivex.Scheduler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.masterSync.MasterSyncActivity;
import saneforce.santrip.activity.slideDownloaderAlertBox.DownloadTask;
import saneforce.santrip.activity.slideDownloaderAlertBox.SlideDownloaderAlertBox;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.storage.SharedPref;

public class FileDownloadWorker extends Worker {
    public   String url1 ;
    public String fileName;
    public String fileId;

    String TAG="Downloading Task";
    public FileDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint("WrongThread")
    @NonNull
    @Override
    public Result doWork() {

        url1 = getInputData().getString("file_url");
        fileId = getInputData().getString("Slide_id");
        fileName = getInputData().getString("Slide_name");





        return Result.success();
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
}
