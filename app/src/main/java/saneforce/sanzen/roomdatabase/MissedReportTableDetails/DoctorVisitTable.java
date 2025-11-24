package saneforce.sanzen.roomdatabase.MissedReportTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(
        tableName = "doctor_visit_table",
        primaryKeys = {"sfcode", "date"}
)
public class DoctorVisitTable {

    @NonNull
    @ColumnInfo(name = "sfcode")
    private String sfcode;

    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "values")
    private String values;

    public DoctorVisitTable(@NonNull String sfcode, @NonNull String date, String values) {
        this.sfcode = sfcode;
        this.date = date;
        this.values = values;
    }

    @NonNull
    public String getSfcode() {
        return sfcode;
    }

    public void setSfcode(@NonNull String sfcode) {
        this.sfcode = sfcode;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}

