package saneforce.sanzen.roomdatabase.MissedReportTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "missed_table")
public class MissedTable {


    @PrimaryKey
    @NonNull
    private String key;
    @ColumnInfo(name = "values")
    private String values;

    public MissedTable(@NonNull String key, String values) {
        this.key = key;
        this.values = values;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
