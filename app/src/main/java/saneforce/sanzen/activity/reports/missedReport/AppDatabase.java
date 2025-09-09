//package saneforce.sanzen.activity.reports.missedReport;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import android.content.Context;
//
//// Add entities = your model classes here
//@Database(entities = {DoctorVisitItem.class}, version = 1, exportSchema = false)
//public abstract class AppDatabase extends RoomDatabase {
//
//    // Abstract method to get DAO
//    public abstract DoctorVisitDao doctorVisitDao();
//
//    // Singleton instance for database
//    private static AppDatabase INSTANCE;
//
//    // Method to get database instance
//    public static synchronized AppDatabase getInstance(Context context) {
//        if (INSTANCE == null) {
//            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            AppDatabase.class, "doctor_visit_db")
//                    .fallbackToDestructiveMigration()  // This resets DB on version change, good for dev
//                    .build();
//        }
//        return INSTANCE;
//    }
//}
