package saneforce.sanzen.roomdatabase.ChatTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ChatDataDao {
    @Insert
    void insert(ChatDataTable chatDataTable);

    @Update
    void update(ChatDataTable chatDataTable);

    @Delete
    void delete(ChatDataTable chatDataTable);

    @Query("DELETE FROM `CHAT_TABLE`")
    void deleteAllData();

    @Query("DELETE FROM `CHAT_TABLE` WHERE `SUBJECT` = 'sub'")
    void deleteAllLocalData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveChat(ChatDataTable chatDataTable);

    @Query("SELECT * FROM `CHAT_TABLE`")
    List<ChatDataTable> getAllChatData();

    @Query("SELECT * FROM `CHAT_TABLE` WHERE `OWNER_ID` = :userCode ORDER BY `DATE`")
    List<ChatDataTable> getAllChatData(String userCode);

    @Query("SELECT * FROM `CHAT_TABLE` WHERE `OWNER_ID` = :userCode ORDER BY `DATE` DESC LIMIT 1")
    ChatDataTable getLastChatData(String userCode);

    @Query("SELECT * FROM `CHAT_TABLE` ORDER BY `ID` DESC LIMIT 1")
    ChatDataTable getLastID();
}

