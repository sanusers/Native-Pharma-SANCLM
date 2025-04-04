package saneforce.sanzen.activity.slideDownloaderAlertBox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
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
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.SlideTable.WelcomeSlidesDao;
import saneforce.sanzen.roomdatabase.SlideTable.WelcomeSlidesDataTable;

public class WelcomeSlideDownloadWorker extends Worker {

    private WelcomeSlidesDao welcomeSlidesDao;
    private String TAG = "Downloading Task";

    public WelcomeSlideDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint({"WrongThread", "DefaultLocale"})
    @NonNull
    @Override
    public Result doWork() {

        File apkStorage = null;
        File outputFile = null;
        int totalSize;
        int downloadedSize = 0;
        int Progress = 0;

        RoomDB roomDB = RoomDB.getDatabase(getApplicationContext());
        welcomeSlidesDao = roomDB.welcomeSlidesDao();

        String url1 = getInputData().getString("file_url");
        String downloadFileName = getInputData().getString("Slide_name");
        String fileName = "";
        if(downloadFileName != null && !downloadFileName.isEmpty()) {
            fileName = downloadFileName.substring(downloadFileName.indexOf('/') + 1);
        }
        String Flag = getInputData().getString("Flag");
        String FilePosition = getInputData().getString("FilePosition");

        try {
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                welcomeSlidesDao.saveWelcomeSlideData(new WelcomeSlidesDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag != null && Flag.equalsIgnoreCase("1")) {
                    servicesRestartMethod();
                }
                return Result.failure();
            }
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                apkStorage = new File(getApplicationContext().getExternalFilesDir(null) + "/Slides/");
            } else {
                welcomeSlidesDao.saveWelcomeSlideData(new WelcomeSlidesDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag != null && Flag.equalsIgnoreCase("1")) {
                    servicesRestartMethod();
                }
                return Result.failure();
            }

            if (!apkStorage.exists()) {
                if (!apkStorage.mkdirs()) {
                    Log.e(TAG, "Directory Creation Failed.");
                    welcomeSlidesDao.saveWelcomeSlideData(new WelcomeSlidesDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                    if (Flag != null && Flag.equalsIgnoreCase("1")) {
                        servicesRestartMethod();
                    }
                    return Result.failure();
                }
            }

            outputFile = new File(apkStorage, fileName);

            if (outputFile.exists()) {
                if (outputFile.delete()) {
                    Log.e(TAG, "Old file deleted.");
                } else {
                    Log.e(TAG, "Failed to delete old file.");
                }
            }

            if (!outputFile.createNewFile()) {
                Log.e(TAG, "File Creation Failed.");
                welcomeSlidesDao.saveWelcomeSlideData(new WelcomeSlidesDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag != null && Flag.equalsIgnoreCase("1")) {
                    servicesRestartMethod();
                }
                return Result.failure();
            }

            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = connection.getInputStream();
            totalSize = connection.getContentLength();
            String progressTex = "";
            byte[] buffer = new byte[1024];
            int len1;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
                downloadedSize += len1;
                Progress = (int) (((double) downloadedSize / (double) totalSize) * 100);
                progressTex = String.format("%.1f MB of %.1f MB", downloadedSize / (1024.0 * 1024), totalSize / (1024.0 * 1024));
                welcomeSlidesDao.saveWelcomeSlideData(new WelcomeSlidesDataTable(downloadFileName, progressTex, "2", String.valueOf(Progress), "1", FilePosition));
            }

            if (downloadFileName != null && downloadFileName.contains("zip")) {
                String filePath = outputFile.getAbsolutePath();
                File unzipDir = new File(getApplicationContext().getExternalFilesDir(null), "/Slides/");
                unzip(filePath, unzipDir);
            }
            welcomeSlidesDao.saveWelcomeSlideData(new WelcomeSlidesDataTable(downloadFileName, String.valueOf(progressTex), "3", "100", "1", FilePosition));

            fos.close();
            is.close();
            createThumbnail(fileName);
            if (Flag != null && Flag.equalsIgnoreCase("1")) {
                servicesRestartMethod();
            }

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            welcomeSlidesDao.saveWelcomeSlideData(new WelcomeSlidesDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
            if (Flag != null && Flag.equalsIgnoreCase("1")) {
                servicesRestartMethod();
            }
            Log.e(TAG, "Download Error Exception " + e.getMessage());
            return Result.failure();
        }
    }


    public static void unzip(String filepath, File targetDirectory) {
        File zipFile = new File(filepath);
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[10240];
            while ((ze = zis.getNextEntry()) != null) {
                String name = ze.getName();
                try {
                    File file = new File(targetDirectory, name);
                    if (ze.isDirectory()) {
                        file.mkdirs();
                    } else {
                        try {
                            String dirName = name.substring(0, name.lastIndexOf('/') + 1);
                            File dirFile = new File(targetDirectory, dirName);
                            Log.i("File Dir", "unzip: " + dirFile.getAbsolutePath());
                            if (!dirFile.exists()) {
                                dirFile.mkdirs();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try (FileOutputStream fout = new FileOutputStream(file)) {
                            try {
                                while ((count = zis.read(buffer)) != -1) {
                                    fout.write(buffer, 0, count);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                fout.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createThumbnail(String fileName) {
        String fileFormat = SupportClass.getFileExtension(fileName);
        File sourceFile = new File(getApplicationContext().getExternalFilesDir(null) + "/Slides/", fileName);
        File thumbnailStorage;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            thumbnailStorage = new File(getApplicationContext().getExternalFilesDir(null) + "/Thumbnails/");
        } else {
            return;
        }
        if (!thumbnailStorage.exists()) {
            if (!thumbnailStorage.mkdirs()) {
                Log.e("Thumbnail Conversion", "Directory Creation Failed.");
                return;
            }
        }
        File destinationFile = new File(thumbnailStorage, fileName.replace(fileFormat, "jpeg"));
        String destinationFilePath = destinationFile.getAbsolutePath();
        if (sourceFile.exists()) {
            Bitmap bitmap = SupportClass.generateBitmap(getApplicationContext(), sourceFile, fileFormat);
            if (bitmap != null) {
                try {
                    if (destinationFile.exists()) {
                        if (destinationFile.delete()) {
                            Log.d("Thumbnail Conversion", "Old thumbnail(" + fileName + ") deleted.");
                        } else {
                            Log.e("Thumbnail Conversion", "Failed to delete old thumbnail(" + fileName + ").");
                        }
                    }
                    if (!destinationFile.createNewFile()) {
                        Log.e("Thumbnail Conversion", "Destination File Creation Failed.");
                        return;
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e("Thumbnail Conversion", Objects.requireNonNull(e.getMessage()));
                }
            } else Log.e("Thumbnail Creation", "Bitmap not generated");
        }
    }

    private void servicesRestartMethod() {
        Intent Intent = new Intent(getApplicationContext(), WelcomeSlideService.class);
        getApplicationContext().stopService(Intent);
        Intent Intent1 = new Intent(getApplicationContext(), WelcomeSlideService.class);
        getApplicationContext().startService(Intent1);
    }
}
