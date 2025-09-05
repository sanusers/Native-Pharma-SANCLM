//package saneforce.sanzen.activity.reports.missedReport;
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import java.util.List;
//
//@Dao
//public interface DoctorVisitDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertAll(List<DoctorVisitItem> doctors);
//
//    @Query("SELECT * FROM doctor_visit WHERE monthKey = :monthKey")
//    List<DoctorVisitItem> getByMonth(String monthKey);
//
//    @Query("DELETE FROM doctor_visit WHERE monthKey = :monthKey")
//    void deleteByMonth(String monthKey);
//}
