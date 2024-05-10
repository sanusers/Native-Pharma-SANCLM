package saneforce.santrip.activity.SlideDownloadNew;

import android.content.Context;

import io.reactivex.Scheduler;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWorker  extends Worker {
    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String urlToDownload = getInputData().getString("url");
        String fileName = getInputData().getString("file_name");
        String downloadLocation = getInputData().getString("download_location");

        try {
            URL url = new URL(urlToDownload);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Get input stream and output stream
            InputStream inputStream = connection.getInputStream();

            // Create the directory if it doesn't exist
            File directory = new File(downloadLocation);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create the output file
            File file = new File(directory, fileName);
            OutputStream outputStream = new FileOutputStream(file);

            // Get file size
            int fileSize = connection.getContentLength();

            // Initialize downloaded size
            int downloadedSize = 0;

            // Read data and write to file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                downloadedSize += length;

                // Report progress if needed
                // setProgress(Data.Builder().putInt("progress", (downloadedSize * 100) / fileSize).build());
            }

            // Close streams
            outputStream.close();
            inputStream.close();

            // Pass the downloaded file path as output
            Data outputData = new Data.Builder()
                    .putString("file_path", file.getAbsolutePath())
                    .build();
//            setOutputData(outputData);

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
