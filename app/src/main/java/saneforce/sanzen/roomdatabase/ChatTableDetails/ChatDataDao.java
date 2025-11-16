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

    @Query("DELETE FROM `chat_table`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDCRDocValues(ChatDataTable chatDataTable);

    @Query("SELECT * FROM `chat_table`")
    List<ChatDataTable> getAllChatData();

}

