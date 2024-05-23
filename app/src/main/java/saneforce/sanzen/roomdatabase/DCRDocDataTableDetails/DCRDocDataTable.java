package saneforce.sanzen.roomdatabase.DCRDocDataTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;

@Entity(tableName = "dcr_doc_table")
public class DCRDocDataTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "hq_values")
    private String hqValues = "";

    @ColumnInfo(name = "dcr_values")
    private String dcrValues;

    public DCRDocDataTable() {}

    @Ignore
    public DCRDocDataTable(@NonNull String hqValues, String dcrValues) {
        this.hqValues = hqValues;
        this.dcrValues = dcrValues;
    }

    @NonNull
    public String getHqValues() {
        return hqValues;
    }

    public void setHqValues(@NonNull String hqValues) {
        this.hqValues = hqValues;
    }

    public String getDcrValues() {
        return dcrValues;
    }

    public void setDcrValues(String dcrValues) {
        this.dcrValues = dcrValues;
    }

    public JSONArray getDCRDocDataJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            if (dcrValues != null && !dcrValues.isEmpty()) jsonArray = new JSONArray(dcrValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
