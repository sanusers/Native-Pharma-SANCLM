package saneforce.sanzen.commonClasses;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContinuousLogCollector {

    private static final String TAG = "ContinuousLogCollector";
    public static final String LOG_FILE_NAME_PREFIX = "app_full_log_";
    public static final String LOG_FILE_EXTENSION = ".txt";
    private static final AtomicBoolean isLogging = new AtomicBoolean(false);
    private static Process logcatProcess;
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void startLogging(Context context) {
        if(isLogging.compareAndSet(false, true)) {
            if(checkStoragePermission(context)) {
//                Toast.makeText(context, "started collecting logs", Toast.LENGTH_SHORT).show();
                executorService.execute(() -> collectLogs(context));
            }else {
//                Toast.makeText(context, "collecting logs requires storage permission", Toast.LENGTH_SHORT).show();
                isLogging.set(false);
                // You might want to request permission here if not already granted
            }
        }else {
//            Toast.makeText(context, "already started collecting logs", Toast.LENGTH_SHORT).show();
        }
    }

    public static void stopLogging(Context context) {
        if(isLogging.compareAndSet(true, false)) {
            Toast.makeText(context, "stopped collecting logs", Toast.LENGTH_SHORT).show();
            if(logcatProcess != null) {
                logcatProcess.destroy();
                logcatProcess = null;
            }
            executorService.shutdownNow();
            executorService = Executors.newSingleThreadExecutor(); // Create a new one if you might start again later
        }else {
            Log.i(TAG, "Logging is not currently in progress.");
        }
    }

    private static void collectLogs(Context context) {
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            Log.e(TAG, "Error clearing log buffers: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            String packageName = context.getPackageName();
            String command = "logcat -v threadtime " + packageName + ":*";
            logcatProcess = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
            File logFile = new File(getLogDirectory(context), LOG_FILE_NAME_PREFIX + getCurrentDateTime() + LOG_FILE_EXTENSION);
            System.out.println(logFile.getAbsolutePath());
            manageLogFiles(context);
            try (FileOutputStream fos = new FileOutputStream(logFile)) {
                String line;
                while (isLogging.get() && (line = reader.readLine()) != null) {
                    if((line.contains(" W ") && line.contains("onLocationChanged"))) { // Check if the line contains ":W " which indicates a Warning log
                        fos.write((line + "\n").getBytes());
                    }else if(!line.contains(" W ")
                            && !line.contains("InsetsSourceConsumer")
                            && !line.contains("OpenGLRenderer")
                            && !line.contains("Binder")
                            && !line.contains("ViewRootImpl")
                            && !line.contains("SessionLifecycleClient")
                            && !line.contains("InputMethodManager")
                            && !line.contains("DecorView")
                            && !line.contains("EventGDTLogger")
                            && !line.contains("InputTransport")
                            && !line.contains("DynamiteModule")
                            && !line.contains("nativeloader")
                            && !line.contains("DataRequestDispatcher")
                            && !line.contains("Choreographer")
                            && !line.contains("SessionLifecycleService")
                            && !line.contains("MiuiMultiWindowUtils")
                            && !line.contains("ScrollerOptimizationManager")
                            && !line.contains("CompatibilityChangeReporter")
                            && !line.contains("TrafficStats")
                    ) {
                        fos.write((line + "\n").getBytes());
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error writing logs to file: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing logcat reader: " + e.getMessage());
                    e.printStackTrace();
                }
                if(logcatProcess != null) {
                    logcatProcess.destroy();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error executing logcat command: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void manageLogFiles(Context context) {
        File logDir = getLogDirectory(context);
        File[] files = logDir.listFiles();

        if (files != null && files.length > 2) {
            // Sort files by last modified date (which should correspond to creation time)
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

            // Keep the first two (most recent) and delete the rest
            for (int i = 2; i < files.length; i++) {
                if (files[i].delete()) {
                    Log.i(TAG, "Deleted old log file: " + files[i].getName());
                } else {
                    Log.w(TAG, "Failed to delete old log file: " + files[i].getName());
                }
            }
        }
    }
//    private static void collectLogs(Context context) {
//        try {
//            String packageName = context.getPackageName();
//            String command = "logcat -v threadtime " + packageName + ":V *:S";
//
//            logcatProcess = Runtime.getRuntime().exec(command);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
//
//            File logFile = new File(getLogDirectory(context), LOG_FILE_NAME_PREFIX + getCurrentDateTime() + LOG_FILE_EXTENSION);
//            System.out.println(logFile.getAbsolutePath());
//            try (FileOutputStream fos = new FileOutputStream(logFile)) {
//                String line;
//                while (isLogging.get() && (line = reader.readLine()) != null) {
//                    fos.write((line + "\n").getBytes());
//                }
//                Log.i(TAG, "Log collection finished. Logs saved to: " + logFile.getAbsolutePath());
//            } catch (IOException e) {
//                Log.e(TAG, "Error writing logs to file: " + e.getMessage());
//                e.printStackTrace();
//            } finally {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    Log.e(TAG, "Error closing logcat reader: " + e.getMessage());
//                    e.printStackTrace();
//                }
//                if (logcatProcess != null) {
//                    logcatProcess.destroy();
//                }
//            }
//
//        } catch (IOException e) {
//            Log.e(TAG, "Error executing logcat command: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    public static File getLogDirectory(Context context) {
        File directory;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
            directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "AppLogs");
        }else {
            directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AppLogs");
        }
        if(!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private static boolean checkStoragePermission(Context context) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
            return true;
        }else {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

}
