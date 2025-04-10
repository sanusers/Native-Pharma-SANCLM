package saneforce.sanzen.activity.Quiz.AssertDownloadAlert;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDao;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;

public class AssertDownloadViewModel extends AndroidViewModel {

    private final LiveData<List<QuizAssertsDataTable>> allQuizAsserts;
    private final LiveData<Integer> downloadingCount;
    private final LiveData<Integer> statusNewCount;
    private final LiveData<Integer> processDoneCount;

    public AssertDownloadViewModel(@NonNull Application application) {
        super(application);
        RoomDB db = RoomDB.getDatabase(application.getApplicationContext());
        QuizAssertsDao quizAssertsDao = db.quizAssertsDao();
        allQuizAsserts = quizAssertsDao.getAllQuizAssertsList();
        downloadingCount = quizAssertsDao.getCountOfQuizAssertsWithDownloadingStatus();
        statusNewCount = quizAssertsDao.getCountNewStatus();
        processDoneCount = quizAssertsDao.getCountOfDownloadingProcessDone();
    }

    public LiveData<List<QuizAssertsDataTable>> getAllQuizAsserts() {
        return allQuizAsserts;
    }

    public LiveData<Integer> getDownloadingCount() {
        return downloadingCount;
    }

    public LiveData<Integer> getCountOfDownloadingProcessDone() {
        return processDoneCount;
    }

    public LiveData<Integer> slideNewCount() {
        return statusNewCount;
    }

}
