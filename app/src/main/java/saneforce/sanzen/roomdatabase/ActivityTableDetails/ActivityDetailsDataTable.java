package saneforce.sanzen.roomdatabase.ActivityTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;

@Entity(tableName = "activity_details_table")
public class ActivityDetailsDataTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";

    @ColumnInfo(name = "json_data")
    private String jsonData = "";

    @ColumnInfo(name = "status")
    private String status = "";

    @Ignore
    public ActivityDetailsDataTable() {
    }

    public ActivityDetailsDataTable(@NonNull String id, String jsonData, String status) {
        this.id = id;
        this.jsonData = jsonData;
        this.status = status;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONArray getActivityDataJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            if(jsonData != null && !jsonData.isEmpty()) jsonArray = new JSONArray(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

}
