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
    void saveNotification(NotificationDataTable notificationDataTable);

    @Query("SELECT COUNT(1) > 0 FROM `NOTIFICATION_TABLE`")
    boolean isNotificationAvailable();

    @Query("SELECT COUNT(1) > 0 FROM `NOTIFICATION_TABLE` where `IS_READ` = 0")
    boolean isNotificationUnReadAvailable();

    @Query("SELECT COUNT(1) > 0 FROM NOTIFICATION_TABLE WHERE `DATE_TIME` = :dateTime")
    boolean isAvailableNotificationOnDateTime(String dateTime);

    @Query("SELECT EXISTS(SELECT 1 FROM `NOTIFICATION_TABLE` WHERE `IS_READ` = :status)")
    boolean isNotificationAvailableByStatus(boolean status);

    @Query("SELECT * FROM `NOTIFICATION_TABLE` WHERE `ID` = :id")
    NotificationDataTable getNotificationByID(int id);

    @Query("SELECT * FROM `NOTIFICATION_TABLE` WHERE `IS_READ` = :status")
    List<NotificationDataTable> getNotificationByStatus(boolean status);

    @Query("UPDATE `NOTIFICATION_TABLE` SET `IS_READ` = :status")
    void changeNotificationStatus(int status);

    @Query("DELETE FROM `NOTIFICATION_TABLE` WHERE `ID` = :id")
    void deleteNotificationByID(int id);

    @Query("SELECT * FROM `NOTIFICATION_TABLE` ORDER BY `DATE_TIME` DESC")
    LiveData<List<NotificationDataTable>> getAllNotifications();

    @Query("SELECT COUNT(*) FROM `NOTIFICATION_TABLE` WHERE `IS_READ` = 0")
    LiveData<Integer> getUnreadNotificationCount();

    @Query("UPDATE `NOTIFICATION_TABLE` SET `IS_READ` = 1 WHERE `ID` = :notificationId")
    int markAsRead(int notificationId);

}
