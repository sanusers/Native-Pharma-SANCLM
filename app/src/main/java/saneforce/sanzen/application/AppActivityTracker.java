package saneforce.sanzen.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class AppActivityTracker implements Application.ActivityLifecycleCallbacks {
    private static AppActivityTracker instance;
    private WeakReference<Activity> currentActivity;

    public static void init(Application application) {
        if(instance == null) {
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