package saneforce.sanzen.roomdatabase.QuizAssertsTable;

import android.annotation.SuppressLint;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface QuizAssertsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(QuizAssertsDataTable quizAssertsDataTable);

    @Update
    void update(QuizAssertsDataTable quizAssertsDataTable);

    @Delete
    void delete(QuizAssertsDataTable quizAssertsDataTable);

    @Query("DELETE FROM `QUIZ_ASSERTS_TABLE`")
    void deleteAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveQuizAssertData(QuizAssertsDataTable quizAssertsDataTable);

    @Query("SELECT * FROM `QUIZ_ASSERTS_TABLE` WHERE `NAME` = :name")
    QuizAssertsDataTable getQuizAssertsDataByName(String name);

    @Query("SELECT * FROM `QUIZ_ASSERTS_TABLE`")
    LiveData<List<QuizAssertsDataTable>> getAllQuizAssertsList();

    @Query("SELECT COUNT(*) FROM `QUIZ_ASSERTS_TABLE` WHERE `downloading_status` = '3'")
    LiveData<Integer> getCountOfQuizAssertsWithDownloadingStatus();

    @Query("SELECT COUNT(*) FROM `QUIZ_ASSERTS_TABLE` WHERE `downloading_status` = '3' OR `downloading_status` = '0'")
    LiveData<Integer> getCountOfDownloadingProcessDone();

    @Query("SELECT COUNT(*) FROM `QUIZ_ASSERTS_TABLE` WHERE `downloading_status` = '1'")
    LiveData<Integer> getCountNewStatus();

    @Query("SELECT COUNT(*) FROM `QUIZ_ASSERTS_TABLE` WHERE `downloading_status` = '2'")
    int getInProcessCount();

    @Query("SELECT COUNT(*) FROM `QUIZ_ASSERTS_TABLE`")
    int getTotalQuizAssertCount();

    @Query("SELECT `NAME` FROM `QUIZ_ASSERTS_TABLE`")
    List<String> getAllQuizAssertNames();

    @Query("DELETE FROM `QUIZ_ASSERTS_TABLE` WHERE `NAME` = :name")
    void deleteQuizAssertByName(String name);

    @Query("Update `QUIZ_ASSERTS_TABLE` set `BACKGROUND_TASK`=:New WHERE `BACKGROUND_TASK` = :old")
    void setChangeStatus(String New, String old);

    @Query("SELECT EXISTS(SELECT 1 FROM `QUIZ_ASSERTS_TABLE` WHERE `NAME` = :name)")
    boolean isQuizAssertAvailable(String name);

    @Query("SELECT `NAME` FROM `QUIZ_ASSERTS_TABLE` WHERE `NAME` = :name")
    String getQuizAssertName(String name);

    @Query("SELECT * FROM `QUIZ_ASSERTS_TABLE`")
    Cursor getAllQuizAsserts();

    default ArrayList<QuizAssertsDataTable> cursorToArrayList() {
        Cursor cursor = getAllQuizAsserts();
        ArrayList<QuizAssertsDataTable> quizAsserts = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String quizAssertName = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String quizAssertSize = cursor.getString(cursor.getColumnIndex("assert_size"));
                @SuppressLint("Range") String downloadingStatus = cursor.getString(cursor.getColumnIndex("downloading_status"));
                @SuppressLint("Range") String progress = cursor.getString(cursor.getColumnIndex("progress"));
                @SuppressLint("Range") String backgroundTask = cursor.getString(cursor.getColumnIndex("background_task"));
                @SuppressLint("Range") String filePosition = cursor.getString(cursor.getColumnIndex("assert_position"));
                QuizAssertsDataTable quizAssert = new QuizAssertsDataTable(quizAssertName, quizAssertSize, downloadingStatus, progress, backgroundTask, filePosition);
                quizAsserts.add(quizAssert);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return quizAsserts;
    }

}
