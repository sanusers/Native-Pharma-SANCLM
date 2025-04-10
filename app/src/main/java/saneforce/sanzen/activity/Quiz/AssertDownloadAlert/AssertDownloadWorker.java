package saneforce.sanzen.activity.Quiz.AssertDownloadAlert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDao;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;

public class AssertDownloadWorker extends Worker {

    private QuizAssertsDao quizAssertsDao;
    private String TAG = "Downloading Task";

    public AssertDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint({"WrongThread", "DefaultLocale"})
    @NonNull
    @Override
    public ListenableWorker.Result doWork() {

        File apkStorage = null;
        File outputFile = null;
        int totalSize;
        int downloadedSize = 0;
        int Progress = 0;

        RoomDB roomDB = RoomDB.getDatabase(getApplicationContext());
        quizAssertsDao = roomDB.quizAssertsDao();

        String url1 = getInputData().getString("file_url");
        String downloadFileName = getInputData().getString("Assert_name");
        String Flag = getInputData().getString("Flag");
        String FilePosition = getInputData().getString("FilePosition");

        try {
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                quizAssertsDao.saveQuizAssertData(new QuizAssertsDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag != null && Flag.equalsIgnoreCase("1")) {
                    servicesRestartMethod();
                }
                return ListenableWorker.Result.failure();
            }
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                apkStorage = new File(getApplicationContext().getExternalFilesDir(null) + "/QuizAsserts/");
            } else {
                quizAssertsDao.saveQuizAssertData(new QuizAssertsDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag != null && Flag.equalsIgnoreCase("1")) {
                    servicesRestartMethod();
                }
                return ListenableWorker.Result.failure();
            }

            if (!apkStorage.exists()) {
                if (!apkStorage.mkdirs()) {
                    Log.e(TAG, "Directory Creation Failed.");
                    quizAssertsDao.saveQuizAssertData(new QuizAssertsDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                    if (Flag != null && Flag.equalsIgnoreCase("1")) {
                        servicesRestartMethod();
                    }
                    return ListenableWorker.Result.failure();
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
                quizAssertsDao.saveQuizAssertData(new QuizAssertsDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
                if (Flag != null && Flag.equalsIgnoreCase("1")) {
                    servicesRestartMethod();
                }
                return ListenableWorker.Result.failure();
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
                quizAssertsDao.saveQuizAssertData(new QuizAssertsDataTable(downloadFileName, progressTex, "2", String.valueOf(Progress), "1", FilePosition));
            }

            if (downloadFileName != null && downloadFileName.contains("zip")) {
                String filePath = outputFile.getAbsolutePath();
                File unzipDir = new File(getApplicationContext().getExternalFilesDir(null), "/QuizAsserts/");
                unzip(filePath, unzipDir);
            }
            quizAssertsDao.saveQuizAssertData(new QuizAssertsDataTable(downloadFileName, String.valueOf(progressTex), "3", "100", "1", FilePosition));

            fos.close();
            is.close();
            if (Flag != null && Flag.equalsIgnoreCase("1")) {
                servicesRestartMethod();
            }

            return ListenableWorker.Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            quizAssertsDao.saveQuizAssertData(new QuizAssertsDataTable(downloadFileName, "Downloading Failure", "0", "0", "1", FilePosition));
            if (Flag != null && Flag.equalsIgnoreCase("1")) {
                servicesRestartMethod();
            }
            Log.e(TAG, "Download Error Exception " + e.getMessage());
            return ListenableWorker.Result.failure();
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

    private void servicesRestartMethod() {
        Intent Intent = new Intent(getApplicationContext(), AssertDownloadService.class);
        getApplicationContext().stopService(Intent);
        Intent Intent1 = new Intent(getApplicationContext(), AssertDownloadService.class);
        getApplicationContext().startService(Intent1);
    }
}
