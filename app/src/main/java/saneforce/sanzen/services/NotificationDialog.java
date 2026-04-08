package saneforce.sanzen.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.call.profile.CustomerProfile;
import saneforce.sanzen.activity.login.LoginActivity;
import saneforce.sanzen.activity.myresource.ProfilingActivity;
import saneforce.sanzen.activity.presentation.playPreview.PlaySlidePreviewActivity;
import saneforce.sanzen.activity.previewPresentation.PreviewActivity;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class NotificationDialog {
    private static AlertDialog dialog;
    private static final Queue<NotificationData> queue = new LinkedList<>();
    private static boolean isShowing = false;

    public static void showDialog(Context context, String title, String message, String type, String hqCode, String monthYear, long id) {
        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) return;

        if (((context instanceof DCRCallActivity) || (context instanceof CustomerProfile)
                || (context instanceof ProfilingActivity) || (context instanceof PlaySlidePreviewActivity)
                || (context instanceof PreviewActivity) || (context instanceof PlaySlideDetailing))
                && (!type.matches("(?i)LT|VT|BK|PWD"))) {

            RoomDB.getDatabase(context).notificationDataDao().changeNotificationReadStatus((int) id, 0);
            RoomDB.getDatabase(context).notificationDataDao().changeNotificationSyncStatus((int) id, 1);
            return;
        }

        queue.offer(new NotificationData(context, title, message, type, hqCode, monthYear, id));
        if (!isShowing) {
            showNextDialog();
        }
    }

    private static void showNextDialog() {
        NotificationData data = queue.poll();
        if (data == null || !(data.context instanceof Activity) || ((Activity) data.context).isFinishing()) {
            isShowing = false;
            return;
        }

        isShowing = true;

        View dialogView = LayoutInflater.from(data.context).inflate(R.layout.notification_dialog, null);
        TextView titleView = dialogView.findViewById(R.id.tv_title);
        TextView messageView = dialogView.findViewById(R.id.tv_message);
        titleView.setText(data.title);
        messageView.setText(data.message);

        dialog = new AlertDialog.Builder(data.context)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        dialog.show();

        NotificationDataDao notificationDataDao = RoomDB.getDatabase(data.context).notificationDataDao();

        if (data.type.equalsIgnoreCase("LT") || data.type.equalsIgnoreCase("VT")
                || data.type.equalsIgnoreCase("BK") || data.type.equalsIgnoreCase("PWD")) {
            notificationDataDao.changeNotificationSyncStatus((int) data.id, 5);
            SharedPref.saveLoginState(data.context, false);
            SharedPref.saveLoginPwd(data.context, "");
            Intent intent = new Intent(data.context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            data.context.startActivity(intent);
            ((Activity) data.context).finish();
            new Handler().postDelayed(NotificationDialog::dismissDialog, 2000);
        } else if (data.type.matches("(?i)DR|CH|ST|UL|HOS|CIP|GIF|TM|WT|PR|MI|SUB|SE|HW|OTR|FSD|AMS|DCR|TP|STP|TPD|LE")) {
            new SyncManager(data.context, data.hqCode, data.type, data.monthYear, data.id).sync();
        } else {
            new Handler().postDelayed(NotificationDialog::dismissDialog, 3000);
        }
    }

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dialog = null;
        isShowing = false;
        showNextDialog();
    }

    private static class NotificationData {
        Context context;
        String title, message, type, hqCode, monthYear;
        long id;

        NotificationData(Context context, String title, String message, String type, String hqCode, String monthYear, long id) {
            this.context = context;
            this.title = title;
            this.message = message;
            this.type = type;
            this.hqCode = hqCode;
            this.monthYear = monthYear;
            this.id = id;
        }
    }
}