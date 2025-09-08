package saneforce.sanzen.roomdatabase.MissedReportTableDetails;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DoctorVisitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVisit(DoctorVisitTable doctorVisitTable);

    @Update
    void updateVisit(DoctorVisitTable doctorVisitTable);

    @Query("SELECT * FROM doctor_visit_table WHERE sfcode = :sfcode AND date = :date")
    DoctorVisitTable getVisitBySfcodeAndDate(String sfcode, String date);

    @Query("SELECT `values` FROM doctor_visit_table WHERE sfcode = :sfcode AND date = :date")
    String getVisitValues(String sfcode, String date);

    @Query("DELETE FROM doctor_visit_table")
    void deleteAll();

    @Query("UPDATE doctor_visit_table SET `values` = :values WHERE sfcode = :sfcode AND date = :date")
    int updateVisitValues(String sfcode, String date, String values);

    @Query("SELECT EXISTS(SELECT 1 FROM doctor_visit_table WHERE sfcode = :sfcode AND date = :date)")
    boolean isVisitDataAvailable(String sfcode, String date);

    default DoctorVisitTable getOrCreate(String sfcode, String date) {
        DoctorVisitTable data = getVisitBySfcodeAndDate(sfcode, date);
        if (data == null) data = new DoctorVisitTable(sfcode, date, null);
        return data;
    }
    default void saveVisitJson(String sfcode, String date, String json) {
        DoctorVisitTable data = getVisitBySfcodeAndDate(sfcode, date);
        if (data != null) {
            data.setValues(json);
            updateVisit(data);
        } else {
            insertVisit(new DoctorVisitTable(sfcode, date, json));
        }
    }
}
