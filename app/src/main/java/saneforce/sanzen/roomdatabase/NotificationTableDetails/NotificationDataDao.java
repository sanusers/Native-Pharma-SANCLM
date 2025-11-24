package saneforce.sanzen.roomdatabase.NotificationTableDetails;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotificationDataDao {
    
    @Insert
    void insert(NotificationDataTable notificationDataTable);

    @Update
    void update(NotificationDataTable notificationDataTable);

    @Delete
    void delete(NotificationDataTable notificationDataTable);

    @Query("DELETE FROM `NOTIFICATION_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveNotification(NotificationDataTable notificationDataTable);

    @Query("SELECT COUNT(1) > 0 FROM `NOTIFICATION_TABLE`")
    boolean isNotificationAvailable();

    @Query("SELECT COUNT(1) > 0 FROM `NOTIFICATION_TABLE` where `IS_READ` = 0")
    boolean isNotificationUnReadAvailable();

    @Query("SELECT COUNT(1) > 0 FROM NOTIFICATION_TABLE WHERE `DATE_TIME` = :dateTime")
    boolean isAvailableNotificationOnDateTime(String dateTime);

    @Query("SELECT EXISTS(SELECT 1 FROM `NOTIFICATION_TABLE` WHERE `IS_READ` = :status)")
    boolean isNotificationAvailableByStatus(int status);

    @Query("SELECT EXISTS(SELECT 1 FROM `NOTIFICATION_TABLE` WHERE `SYNC_STATUS` = :status)")
    boolean isNotificationAvailableBySyncStatus(int status);

    @Query("SELECT * FROM `NOTIFICATION_TABLE` WHERE `ID` = :id")
    NotificationDataTable getNotificationByID(int id);

    @Query("SELECT * FROM `NOTIFICATION_TABLE` WHERE `IS_READ` = :status")
    List<NotificationDataTable> getNotificationByStatus(int status);

    @Query("SELECT * FROM `NOTIFICATION_TABLE` WHERE `SYNC_STATUS` = :status")
    List<NotificationDataTable> getNotificationBySyncStatus(int status);

    @Query("UPDATE `NOTIFICATION_TABLE` SET `IS_READ` = :status WHERE `ID` = :id")
    void changeNotificationReadStatus(int id, int status);

    @Query("UPDATE `NOTIFICATION_TABLE` SET `SYNC_STATUS` = :syncStatus WHERE `ID` = :id")
    void changeNotificationSyncStatus(int id, int syncStatus);

    @Query("DELETE FROM `NOTIFICATION_TABLE` WHERE `ID` = :id")
    void deleteNotificationByID(int id);

    @Query("SELECT * FROM `NOTIFICATION_TABLE` ORDER BY `DATE_TIME` DESC")
    LiveData<List<NotificationDataTable>> getAllNotifications();

    @Query("SELECT * FROM `NOTIFICATION_TABLE` WHERE `SYNC_STATUS` != 0 AND `IS_DIALOG_SHOWN` = 0 ORDER BY `DATE_TIME` DESC")
    LiveData<List<NotificationDataTable>> getAllUnsyncedNotifications();

  /*  @Query("SELECT * FROM `NOTIFICATION_TABLE` WHERE `SYNC_STATUS` != 0 AND `SYNC_STATUS` != 2 ORDER BY `DATE_TIME` DESC")
    LiveData<List<NotificationDataTable>> getAllUnsyncedNotifications();
*/
    @Query("SELECT COUNT(*) FROM `NOTIFICATION_TABLE` WHERE `IS_READ` = 0")
    LiveData<Integer> getUnreadNotificationCount();

    @Query("SELECT COUNT(*) FROM `NOTIFICATION_TABLE` WHERE `SYNC_STATUS` != 0")
    LiveData<Integer> getUnsyncedNotificationCount();

    @Query("UPDATE `NOTIFICATION_TABLE` SET `IS_READ` = 1 WHERE `ID` = :notificationId")
    void markAsRead(int notificationId);

    @Query("UPDATE `NOTIFICATION_TABLE` SET `IS_DIALOG_SHOWN` = 1 WHERE `ID` = :notificationId")
    void setIsDialogShown(int notificationId);

}
