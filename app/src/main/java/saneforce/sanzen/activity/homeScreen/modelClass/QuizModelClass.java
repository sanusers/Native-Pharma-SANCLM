package saneforce.sanzen.activity.homeScreen.modelClass;

public class QuizModelClass {
    private int id;
    private String quizDate;
    private String quizTime;
    private String jsonData;
    private int syncCount;
    private String syncStatus;

    public QuizModelClass(int id, String quizDate, String quizTime, String jsonData, int syncCount, String syncStatus) {
        this.id = id;
        this.quizDate = quizDate;
        this.quizTime = quizTime;
        this.jsonData = jsonData;
        this.syncCount = syncCount;
        this.syncStatus = syncStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuizDate() {
        return quizDate;
    }

    public void setQuizDate(String quizDate) {
        this.quizDate = quizDate;
    }

    public String getQuizTime() {
        return quizTime;
    }

    public void setQuizTime(String quizTime) {
        this.quizTime = quizTime;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public int getSyncCount() {
        return syncCount;
    }

    public void setSyncCount(int syncCount) {
        this.syncCount = syncCount;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
}
