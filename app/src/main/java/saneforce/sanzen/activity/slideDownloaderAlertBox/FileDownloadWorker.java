package saneforce.sanzen.activity.slideDownloaderAlertBox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

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
import java.net.UnknownHostException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesDao;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesTableDeatils;

public class FileDownloadWorker extends Worker {
    RoomDB roomDB;
    SlidesDao slidesDao;
    String TAG = "Downloading Task";

    public FileDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint("WrongThread")
    @NonNull
    @Override
    public Result doWork() {
        File apkStorage = null;
        File outputFile = null;
        int totalSize;
        int downloadedSize = 0;
        int Progress = 0;

        roomDB = RoomDB.getDatabase(getApplicationContext());
        slidesDao = roomDB.slidesDao();

        String url1 = getInputData().getString("file_url");
        String fileId = getInputData().getString("Slide_id");
        String downloadFileName = getInputData().getString("Slide_name");
        String Flag = getInputData().getString("Flag");
        String FilePosition = getInputData().getString("FilePosition");

        try {
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                slidesDao.saveSlideData(new SlidesTableDeatils(fileId, downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag != null && Flag.equalsIgnoreCase("1")) {
                    ServicesRestarmehtod();
                }
                return Result.failure();
            }
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                apkStorage = new File(getApplicationContext().getExternalFilesDir(null) + "/Slides/");
            } else {
                slidesDao.saveSlideData(new SlidesTableDeatils(fileId, downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag != null && Flag.equalsIgnoreCase("1")) {
                    ServicesRestarmehtod();
                }
                return Result.failure();
            }

            if (!apkStorage.exists()) {
                if (!apkStorage.mkdirs()) {
                    Log.e(TAG, "Directory Creation Failed.");
                    slidesDao.saveSlideData(new SlidesTableDeatils(fileId, downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                    if (Flag != null && Flag.equalsIgnoreCase("1")) {
                        ServicesRestarmehtod();
                    }
                    return Result.failure();
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
                slidesDao.saveSlideData(new SlidesTableDeatils(fileId, downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag.equalsIgnoreCase("1")) {
                    ServicesRestarmehtod();
                }
                return Result.failure();
            }

            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = connection.getInputStream();
            totalSize = connection.getContentLength();
            String progressTex = "";
            byte[] buffer = new byte[8192];
            int len1;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
                downloadedSize += len1;
                Progress = (int) (((double) downloadedSize / (double) totalSize) * 100);
                progressTex = String.format("%.1f MB of %.1f MB", downloadedSize / (1024.0 * 1024), totalSize / (1024.0 * 1024));
                slidesDao.saveSlideData(new SlidesTableDeatils(fileId, downloadFileName, progressTex, "2", String.valueOf(Progress), "1", FilePosition));
            }

            if (downloadFileName.contains("zip")) {
                String filePath = outputFile.getAbsolutePath();
                File unzipDir = new File(getApplicationContext().getExternalFilesDir(null), "/Slides/");
                unzip(filePath, unzipDir);
            }
            slidesDao.saveSlideData(new SlidesTableDeatils(fileId, downloadFileName, String.valueOf(progressTex), "3", "100", "1", FilePosition));

            fos.close();
            is.close();
            connection.disconnect();
            Thumbnail(downloadFileName);
            if (Flag.equalsIgnoreCase("1")) {
                ServicesRestarmehtod();
            }

            return Result.success();
        } catch (UnknownHostException e) {
            Log.e(TAG, "DNS failed", e);
            slidesDao.saveSlideData(new SlidesTableDeatils(fileId, downloadFileName, "No Internet Connection", "0", "0", "1", FilePosition));
            return Result.failure();
        } catch (Exception e) {
            e.printStackTrace();
            slidesDao.saveSlideData(new SlidesTableDeatils(fileId, downloadFileName, "Downlaoding Failure", "0", "0", "1", FilePosition));
            if (Flag.equalsIgnoreCase("1")) {
                ServicesRestarmehtod();
            }
            Log.e(TAG, "Download Error Exception " + e.getMessage());
            return Result.failure();
        }
    }

    public static void unzip(String filepath, File targetDirectory) throws IOException {
        File zipFile = new File(filepath);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        try {
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
        } finally {
            zis.close();
        }
    }

    boolean Thumbnail(String fileName) {
        String fileFormat = SupportClass.getFileExtension(fileName);
        File sourceFile = new File(getApplicationContext().getExternalFilesDir(null) + "/Slides/", fileName);
        File thumbnailStorage;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            thumbnailStorage = new File(getApplicationContext().getExternalFilesDir(null) + "/Thumbnails/");
        } else {
            return false;
        }
        if (!thumbnailStorage.exists()) {
            if (!thumbnailStorage.mkdirs()) {
                Log.e("Thumbnail Conversion", "Directory Creation Failed.");
                return false;
            }
        }
        File destinationFile = new File(thumbnailStorage, fileName.replace(fileFormat, "jpeg"));
        if (!sourceFile.exists()) return false;

        Bitmap bitmap = null;
        try {
            bitmap = decodeSampledBitmap(sourceFile.getAbsolutePath(), 200, 200);

            if (bitmap == null) {
                Log.e("Thumbnail Creation", "Bitmap not generated for: " + fileName);
                return false;
            }

            if (destinationFile.exists()) {
                if (destinationFile.delete()) {
                    Log.d("Thumbnail Conversion", "Old thumbnail(" + fileName + ") deleted.");
                }
            }

            if (!destinationFile.createNewFile()) {
                Log.e("Thumbnail Conversion", "Destination File Creation Failed.");
                return false;
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);
                fileOutputStream.flush();
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Thumbnail Conversion", "IOException: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Thumbnail Conversion", "Exception: " + e.getMessage());
            return false;
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    private Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.e("Thumbnail Creation", "OOM even with sampling: " + e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Thumbnail Creation", "Error decoding: " + e.getMessage());
            return null;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public void ServicesRestarmehtod() {
        Intent Intent = new Intent(getApplicationContext(), SlideServices.class);
        getApplicationContext().stopService(Intent);

        Intent Intent1 = new Intent(getApplicationContext(), SlideServices.class);
        getApplicationContext().startService(Intent1);
    }
}
