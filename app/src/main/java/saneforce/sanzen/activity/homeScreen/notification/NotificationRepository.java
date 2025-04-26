package saneforce.sanzen.activity.homeScreen.notification;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataDao;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;

public class NotificationRepository {

    private final NotificationDataDao notificationDataDao;
    private final LiveData<List<NotificationDataTable>> allNotifications;
    private final LiveData<Integer> unreadNotificationCount;

    public NotificationRepository(Application application) {
        RoomDB database = RoomDB.getDatabase(application);
        notificationDataDao = database.notificationDataDao();
        allNotifications = notificationDataDao.getAllNotifications();
        unreadNotificationCount = notificationDataDao.getUnreadNotificationCount();
    }

    public void insert(NotificationDataTable notification) {
        notificationDataDao.saveNotification(notification);
    }

    public LiveData<List<NotificationDataTable>> getAllNotifications() {
        return allNotifications;
    }

    public LiveData<Integer> getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public void markAsRead(int notificationId) {
        RoomDB.databaseWriteExecutor.execute(() -> notificationDataDao.markAsRead(notificationId));
    }

    public void deleteNotification(int notificationId) {
        RoomDB.databaseWriteExecutor.execute(() -> notificationDataDao.deleteNotificationByID(notificationId));
    }

    public void clearAll() {
        RoomDB.databaseWriteExecutor.execute(notificationDataDao::deleteAllData);
    }
}
