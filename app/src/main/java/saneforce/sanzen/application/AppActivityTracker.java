package saneforce.sanzen.application;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class AppActivityTracker implements Application.ActivityLifecycleCallbacks {
    private static AppActivityTracker instance;
    private WeakReference<Activity> currentActivity;

    public static void init(Application application) {
        if (instance == null) {
            instance = new AppActivityTracker();
            application.registerActivityLifecycleCallbacks(instance);
        }
    }

    public static AppActivityTracker getInstance() {
        return instance;
    }

    public Activity getCurrentActivity() {
        return currentActivity != null ? currentActivity.get() : null;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

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

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}