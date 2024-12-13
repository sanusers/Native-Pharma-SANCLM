package saneforce.sanzen.roomdatabase.QuizOfflineTableDetails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;

@Entity(tableName = "quiz_offline_table")
public class QuizOfflineDataTable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "quiz_date")
    private String quizDate = "";
    
    @ColumnInfo(name = "quiz_time")
    private String quizTime = "";
    
    @ColumnInfo(name = "json_data")
    private String jsonData = "";

    @ColumnInfo(name = "sync_count")
    private int syncCount = 0;

    @ColumnInfo(name = "sync_status")
    private String syncStatus = "";

    @Ignore
    public QuizOfflineDataTable() {
    }

    public QuizOfflineDataTable(String quizDate, String quizTime, String jsonData, int syncCount, String syncStatus) {
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

    public JSONArray getQuizDataJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            if(jsonData != null && !jsonData.isEmpty()) jsonArray = new JSONArray(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
    
}
