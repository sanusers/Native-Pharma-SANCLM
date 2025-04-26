package saneforce.sanzen.activity.homeScreen.notification;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataTable;

public class NotificationViewModel extends AndroidViewModel {

    private final NotificationRepository repository;
    private final LiveData<List<NotificationDataTable>> allNotifications;
    private final LiveData<Integer> unreadNotificationCount;

    public NotificationViewModel (Application application) {
        super(application);
        repository = new NotificationRepository(application);
        allNotifications = repository.getAllNotifications();
        unreadNotificationCount = repository.getUnreadNotificationCount();
    }

    public void insert(NotificationDataTable notification) {
        repository.insert(notification);
    }

    public LiveData<List<NotificationDataTable>> getAllNotifications() {
        return allNotifications;
    }

    public LiveData<Integer> getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public void markAsRead(int notificationId) {
        repository.markAsRead(notificationId);
    }

    public void deleteNotification(int notificationId) {
        repository.deleteNotification(notificationId);
    }

    public void clearAll() {
        repository.clearAll();
    }

}