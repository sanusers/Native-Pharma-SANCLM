package saneforce.sanzen.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// You can add logic here to handle the notification click without changing the screen
// For example, log the event, update a badge count, or trigger a background service
public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationClick", "Notification clicked!");

        // Add your logic here. Examples:
        // - Dismiss the notification programmatically if needed (though autoCancel=true handles this)
        // int notificationId = intent.getIntExtra("notification_id", -1); // Retrieve ID if you passed it
        // if (notificationId != -1) {
        //     NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //     notificationManager.cancel(notificationId);
        // }

        // - Perform a background task (e.g., using WorkManager or a Service)
        // Intent backgroundServiceIntent = new Intent(context, MyBackgroundService.class);
        // context.startService(backgroundServiceIntent);

        // - You can also pass data from the FCM message via the Intent extras
        // String messageId = intent.getStringExtra("message_id");
        // Log.d("NotificationClick", "Message ID: " + messageId);

        // IMPORTANT: Do NOT start an Activity from here if you want to stay on the current screen.
    }
}