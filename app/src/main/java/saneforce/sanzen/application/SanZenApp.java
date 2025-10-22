package saneforce.sanzen.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import saneforce.sanzen.BuildConfig;
import saneforce.sanzen.commonClasses.ContinuousLogCollector;
import saneforce.sanzen.commonClasses.EmailSender;

public class SanZenApp extends Application {
    private static final String TAG = "CrashReport";

    @Override
    public void onCreate() {
        super.onCreate();
//        if (BuildConfig.DEBUG) {
            // Disable Crashlytics collection for debug builds
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
//        } else {
            // Explicitly enable for release builds (it's true by default, but this ensures it)
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
//        }
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
//                if (activity.getWindow() != null) {
//                    activity.getWindow().setFlags(
//                            WindowManager.LayoutParams.FLAG_SECURE,
//                            WindowManager.LayoutParams.FLAG_SECURE
//                    );
////                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30+
//                        final WindowInsetsController insetsController = activity.getWindow().getInsetsController();
//                        if (insetsController != null) {
//                            insetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
//                            insetsController.setSystemBarsBehavior(
//                                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                            );
//                        }
//                    } else {
//                        // Legacy for API < 30
//                        activity.getWindow().getDecorView().setSystemUiVisibility(
//                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        );
//                    }
//                }
            }

            @Override
            public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
//                ActivityLifecycleCallbacks.super.onActivityPostCreated(activity, savedInstanceState);
                if (activity.getWindow() != null) {
//                    activity.getWindow().setFlags(
//                            WindowManager.LayoutParams.FLAG_SECURE,
//                            WindowManager.LayoutParams.FLAG_SECURE
//                    );
//                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30+
                        final WindowInsetsController insetsController = activity.getWindow().getInsetsController();
                        if (insetsController != null) {
                            insetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                            insetsController.setSystemBarsBehavior(
                                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            );
                        }
                    } else {
                        // Legacy for API < 30
                        activity.getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        );
                    }
                }
            }

            @Override public void onActivityStarted(@NonNull Activity activity) {}
            @Override public void onActivityResumed(@NonNull Activity activity) {}
            @Override public void onActivityPaused(@NonNull Activity activity) {}
            @Override public void onActivityStopped(@NonNull Activity activity) {}
            @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
            @Override public void onActivityDestroyed(@NonNull Activity activity) {}
        });

        // Set up a custom UncaughtExceptionHandler
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            private final Thread.UncaughtExceptionHandler defaultUEH =
//                    Thread.getDefaultUncaughtExceptionHandler();
//
//            @Override
//            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
//                try {
//                    Log.e(TAG, "App crashed!", throwable);
//
//                    // Get the latest log file
//                    File logFile = getLatestLogFile(getApplicationContext());
//
//                    if (logFile != null) {
//                        // Send the log file (you'll need to implement this method)
////                        sendCrashLog(logFile, throwable);
//                        StringWriter sw = new StringWriter();
//                        PrintWriter pw = new PrintWriter(sw);
//                        throwable.printStackTrace(pw);
//                        String stackTrace = sw.toString();
//
////                        new EmailSender().sendCrashLog(getApplicationContext(), logFile, stackTrace);
//                    }
//
//                } catch (Exception e) {
//                    Log.e(TAG, "Error while handling uncaught exception", e);
//                } finally {
//                    // Let the default exception handler finish processing
//                    if (defaultUEH != null) {
//                        defaultUEH.uncaughtException(thread, throwable);
//                    } else {
//                        // If default handler is null, which should not happen, force termination
//                        System.exit(1);
//                    }
//                }
//            }
//        });
    }

    // Helper method to get the latest log file (assuming your naming convention)
    private File getLatestLogFile(Context context) {
        File logDir = ContinuousLogCollector.getLogDirectory(context);
        File[] files = logDir.listFiles((dir, name) -> name.startsWith(ContinuousLogCollector.LOG_FILE_NAME_PREFIX) && name.endsWith(ContinuousLogCollector.LOG_FILE_EXTENSION));
        if (files != null && files.length > 0) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            return files[0]; // Return the most recent file
        }
        return null;
    }

    // Method to send the crash log (implement your preferred method)
    private void sendCrashLog(File logFile, Throwable throwable) {
        // Implement your logic to send the log file via email or API

        // Example using email (remember to handle network operations off the main thread)
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String[] to = {"saneforceapps@gmail.com"}; // Replace with developer email addresses
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Crash Report - Version " + getAppVersionName());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTrace = sw.toString();

        String body = "App crashed with the following stack trace:\n\n" + stackTrace + "\n\n";
        if (logFile != null) {
            body += "Attached is the log file for more details.";
            Uri uri = Uri.fromFile(logFile);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            // For Android Nougat and above, you might need to use FileProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
        } else {
            body += "Log file not found.";
        }

        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required when starting from Application context
        startActivity(emailIntent);

        // For API sending, you would typically read the log file content
        // and make a network request to your backend. Remember to do this
        // on a background thread to avoid blocking the main thread.
    }

    private String getAppVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "N/A";
        }
    }
}
