package saneforce.sanzen.commonClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;

public class InAppUpdate {
    private final Activity activity;
    private AppUpdateManager mAppUpdateManager;
    public static final int updateRequestCode = 12321;
    private AlertDialog updateProgressDialog;
    private CommonUtilsMethods commonUtilsMethods;

    public InAppUpdate(Activity activity) {
        this.activity = activity;
        commonUtilsMethods = new CommonUtilsMethods(activity);
    }
    
    public void doCheckAndUpdate() {
        try {
            mAppUpdateManager = AppUpdateManagerFactory.create(activity);
            mAppUpdateManager.registerListener(installStateUpdatedListener);
            Log.i("App update", "onNavigationItemSelected: ");
            mAppUpdateManager.getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {
                        Log.d("App update", "onNavigationItemSelected: on success");
                        switch (appUpdateInfo.updateAvailability()) {
                            case UpdateAvailability.UPDATE_AVAILABLE:
                                Log.d("App update", "onNavigationItemSelected: update available");
                                try {
                                    mAppUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo, AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/, activity, updateRequestCode);

                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case UpdateAvailability.UPDATE_NOT_AVAILABLE:
                                Log.d("App update", "onNavigationItemSelected: update not available");
                                commonUtilsMethods.showToastMessage(activity, "No Update Available");
                                break;
                            case UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS:
                                Log.d("App update", "onNavigationItemSelected: update in progress");
                                break;
                            case UpdateAvailability.UNKNOWN:
                                Log.e("App update", "onNavigationItemSelected: unknown1");
                                break;
                        }
                        switch (appUpdateInfo.installStatus()) {
                            case InstallStatus.INSTALLED:
                                Log.d("App update", "onNavigationItemSelected: installed");
                                break;
                            case InstallStatus.DOWNLOADING:
                                Log.d("App update", "onNavigationItemSelected: downloading -> " + appUpdateInfo.bytesDownloaded() + " / " + appUpdateInfo.totalBytesToDownload());
                                break;
                            case InstallStatus.DOWNLOADED:
                                Log.d("App update", "onNavigationItemSelected: downloaded");
//                                    installUpdateAlert();
                                startInstalling();
                                if(updateProgressDialog != null) {
                                    updateProgressDialog.dismiss();
                                }
//                                    popupSnackbarForCompleteUpdate();
                                break;
                            case InstallStatus.CANCELED:
                                Log.d("App update", "onNavigationItemSelected: canceled");
                                break;
                            case InstallStatus.INSTALLING:
                                Log.d("App update", "onNavigationItemSelected: installing");
                                break;
                            case InstallStatus.PENDING:
                                Log.d("App update", "onNavigationItemSelected: pending");
                                break;
                            case InstallStatus.FAILED:
                                Log.e("App update", "onNavigationItemSelected: failed");
                                break;
                            case InstallStatus.REQUIRES_UI_INTENT:
                                break;
                            case InstallStatus.UNKNOWN:
                                Log.e("App update", "onNavigationItemSelected: unknown2");
                                break;
                        }
                    })
                    .addOnFailureListener(e -> e.printStackTrace())
                    .addOnCanceledListener(() -> Log.e("App update", "onNavigationItemSelected: canceled"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopUpdate() {
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }


    private final InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    switch (state.installStatus()) {
                        case InstallStatus.INSTALLED:
                            Log.d("App update", "onStateUpdate: installed");
                            stopUpdate();
                            break;
                        case InstallStatus.DOWNLOADING:
                            int progress = (int) (100.0 * state.bytesDownloaded() / state.totalBytesToDownload());

                            long bytesDownloaded = state.bytesDownloaded();
                            long totalBytes = state.totalBytesToDownload();

                            // Convert bytes to MB
                            double mbDownloaded = bytesDownloaded / (1024.0 * 1024.0);
                            double mbTotal = totalBytes / (1024.0 * 1024.0);

                            // Format to two decimal place
                            String formattedDownloaded = String.format(Locale.getDefault(), "%.2f", mbDownloaded);
                            String formattedTotal = String.format(Locale.getDefault(), "%.2f", mbTotal);

                            if (updateProgressDialog == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                View dialogView = activity.getLayoutInflater().inflate(R.layout.update_progress_dialog_layout, null);
                                builder.setView(dialogView).setCancelable(false); // Prevent canceling while downloading
                                updateProgressDialog = builder.create();
                                updateProgressDialog.show();
                            }

                            ProgressBar progressBar = updateProgressDialog.findViewById(R.id.download_progress);
                            TextView progressSize = updateProgressDialog.findViewById(R.id.txt_download_size);
                            TextView progressPercentage = updateProgressDialog.findViewById(R.id.progress_percentage);
                            Log.i("app download", "onStateUpdate: " + progress + " -> " + String.format(Locale.getDefault(), "%s MB / %s MB", formattedDownloaded, formattedTotal));
                            if(progressBar != null && progressSize != null && progressPercentage != null) {
                                progressBar.setProgress(progress);
                                progressPercentage.setText(String.format(Locale.getDefault(), "%d %%", progress));
                                progressSize.setText(String.format(Locale.getDefault(), "%s MB / %s MB", formattedDownloaded, formattedTotal));
                            }
                            Log.d("App update", "onStateUpdate: downloading -> " + state.bytesDownloaded() + " / " + state.totalBytesToDownload());
                            break;
                        case InstallStatus.DOWNLOADED:
                            Log.d("App update", "onStateUpdate: downloaded");
//                            installUpdateAlert();
                            startInstalling();
                            if(updateProgressDialog != null) {
                                updateProgressDialog.dismiss();
                            }
//                            popupSnackbarForCompleteUpdate();
                            break;
                        case InstallStatus.CANCELED:
                            Log.d("App update", "onStateUpdate: canceled");
                            break;
                        case InstallStatus.INSTALLING:
                            Log.d("App update", "onStateUpdate: installing");
                            break;
                        case InstallStatus.PENDING:
                            Log.d("App update", "onStateUpdate: pending");
                            break;
                        case InstallStatus.FAILED:
                            Log.d("App update", "onStateUpdate: failed");
                            break;
                        case InstallStatus.REQUIRES_UI_INTENT:
                            break;
                        case InstallStatus.UNKNOWN:
                            Log.d("App update", "onStateUpdate: unknown");
                            break;
                    }
                }
            };


    private void installUpdateAlert() {
        Dialog installUpdateDialog = new Dialog(activity);
        installUpdateDialog.setContentView(R.layout.install_update);
        installUpdateDialog.setCancelable(false);
        Button installBtn = installUpdateDialog.findViewById(R.id.install_btn);
        installUpdateDialog.show();

        installBtn.setOnClickListener(view -> {
            installUpdateDialog.dismiss();
        });
    }

    private void startInstalling() {
        if (mAppUpdateManager != null){
            mAppUpdateManager.completeUpdate();
        }
    }

    private void popupSnackbarForCompleteUpdate(View rootView) {
        Snackbar snackbar = Snackbar.make(rootView, "New app is ready!", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            startInstalling();
        });

        snackbar.setActionTextColor(activity.getResources().getColor(R.color.green_60));
        snackbar.show();
    }

}
