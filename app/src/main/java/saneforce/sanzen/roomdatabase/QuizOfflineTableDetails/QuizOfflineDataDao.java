package saneforce.sanzen.roomdatabase.QuizOfflineTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import saneforce.sanzen.activity.homeScreen.modelClass.QuizModelClass;

@Dao
public interface QuizOfflineDataDao {

    @Insert
    void insert(QuizOfflineDataTable quizOfflineDataTable);

    @Update
    void update(QuizOfflineDataTable quizOfflineDataTable);

    @Delete
    void delete(QuizOfflineDataTable quizOfflineDataTable);

    @Query("DELETE FROM `QUIZ_OFFLINE_TABLE`")
    void deleteAllData();
    
    @Query("SELECT COUNT(1) > 0 FROM QUIZ_OFFLINE_TABLE")
    boolean isQuizAvailable();
    
    @Query("SELECT * FROM `QUIZ_OFFLINE_TABLE` WHERE `QUIZ_DATE` = :id")
    QuizOfflineDataTable getQuizOfflineData(int id);
    
    @Query("DELETE FROM `QUIZ_OFFLINE_TABLE` WHERE `ID` = :id")
    void deleteOfflineQuiz(int id);

    @Query("SELECT * FROM `QUIZ_OFFLINE_TABLE` WHERE `QUIZ_DATE` = :date")
    QuizOfflineDataTable getQuiz(String date);

    @Query("SELECT `QUIZ_DATE` FROM `QUIZ_OFFLINE_TABLE`")
    List<String> getAllQuizOfflineDates();

    default QuizModelClass getQuizModelClass(String date) {
        QuizModelClass quizModelClass = null;
        QuizOfflineDataTable quizOfflineDataTable = getQuiz(date);
        if(quizOfflineDataTable != null) {
            quizModelClass = new QuizModelClass(quizOfflineDataTable.getId(), quizOfflineDataTable.getQuizDate(), quizOfflineDataTable.getQuizTime(), quizOfflineDataTable.getJsonData(), quizOfflineDataTable.getSyncCount(), quizOfflineDataTable.getSyncStatus());
        }
        return quizModelClass;
    }

}
