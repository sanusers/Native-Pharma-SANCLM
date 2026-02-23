package saneforce.sanzen.roomdatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataTable;
import saneforce.sanzen.roomdatabase.ActivityTableDetails.ActivityDetailsDataDao;
import saneforce.sanzen.roomdatabase.ActivityTableDetails.ActivityDetailsDataTable;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataTable;
import saneforce.sanzen.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.sanzen.roomdatabase.CallTableDetails.CallsLinechartTable;
import saneforce.sanzen.roomdatabase.DCRDocDataTableDetails.DCRDocDataDao;
import saneforce.sanzen.roomdatabase.DCRDocDataTableDetails.DCRDocDataTable;
import saneforce.sanzen.roomdatabase.LoginTableDetails.LoginDataDao;
import saneforce.sanzen.roomdatabase.LoginTableDetails.LoginDataTable;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.DoctorVisitTable;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.MissedDao;
import saneforce.sanzen.roomdatabase.MissedReportTableDetails.MissedTable;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataDao;
import saneforce.sanzen.roomdatabase.NotificationTableDetails.NotificationDataTable;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataTable;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDataTable;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataTable;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDao;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDataTable;
import saneforce.sanzen.roomdatabase.QuizOfflineTableDetails.QuizOfflineDataDao;
import saneforce.sanzen.roomdatabase.QuizOfflineTableDetails.QuizOfflineDataTable;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataDao;
import saneforce.sanzen.roomdatabase.STPOfflineTableDetails.STPOfflineDataTable;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesDao;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesTableDeatils;
import saneforce.sanzen.roomdatabase.SlideTable.WelcomeSlidesDao;
import saneforce.sanzen.roomdatabase.SlideTable.WelcomeSlidesDataTable;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataTable;
import saneforce.sanzen.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataDao;
import saneforce.sanzen.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataTable;

@Database(entities = {MasterDataTable.class, CallsLinechartTable.class, LoginDataTable.class, TourPlanOfflineDataTable.class, TourPlanOnlineDataTable.class, DCRDocDataTable.class, PresentationDataTable.class, OfflineCheckInOutDataTable.class, CallOfflineWorkTypeDataTable.class, CallOfflineECDataTable.class, CallOfflineSignDataTable.class, CallOfflineDataTable.class, OfflineDaySubmitDataTable.class, SlidesTableDeatils.class, STPOfflineDataTable.class, WelcomeSlidesDataTable.class, ActivityDetailsDataTable.class, ActivityOfflineDataTable.class, ActivityUploadDataTable.class, QuizOfflineDataTable.class, QuizAssertsDataTable.class, NotificationDataTable.class, MissedTable.class, DoctorVisitTable.class}, version = 11, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static final String DATABASE_NAME = "sanclmroom.dp";
    private static RoomDB database;

    public synchronized static RoomDB getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_11)
                    .addMigrations(MIGRATION_1_10)
                    .addMigrations(MIGRATION_1_9)
                    .addMigrations(MIGRATION_1_8)
                    .addMigrations(MIGRATION_1_7)
                    .addMigrations(MIGRATION_1_6)
                    .addMigrations(MIGRATION_1_5)
                    .addMigrations(MIGRATION_1_4)
                    .addMigrations(MIGRATION_1_3)
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_11)
                    .addMigrations(MIGRATION_2_10)
                    .addMigrations(MIGRATION_2_9)
                    .addMigrations(MIGRATION_2_8)
                    .addMigrations(MIGRATION_2_7)
                    .addMigrations(MIGRATION_2_6)
                    .addMigrations(MIGRATION_2_5)
                    .addMigrations(MIGRATION_2_4)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_11)
                    .addMigrations(MIGRATION_3_10)
                    .addMigrations(MIGRATION_3_9)
                    .addMigrations(MIGRATION_3_8)
                    .addMigrations(MIGRATION_3_7)
                    .addMigrations(MIGRATION_3_6)
                    .addMigrations(MIGRATION_3_5)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_11)
                    .addMigrations(MIGRATION_4_10)
                    .addMigrations(MIGRATION_4_9)
                    .addMigrations(MIGRATION_4_8)
                    .addMigrations(MIGRATION_4_7)
                    .addMigrations(MIGRATION_4_6)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_11)
                    .addMigrations(MIGRATION_5_10)
                    .addMigrations(MIGRATION_5_9)
                    .addMigrations(MIGRATION_5_8)
                    .addMigrations(MIGRATION_5_7)
                    .addMigrations(MIGRATION_5_6)
                    .addMigrations(MIGRATION_6_11)
                    .addMigrations(MIGRATION_6_10)
                    .addMigrations(MIGRATION_6_9)
                    .addMigrations(MIGRATION_6_8)
                    .addMigrations(MIGRATION_6_7)
                    .addMigrations(MIGRATION_7_11)
                    .addMigrations(MIGRATION_7_10)
                    .addMigrations(MIGRATION_7_9)
                    .addMigrations(MIGRATION_7_8)
                    .addMigrations(MIGRATION_8_11)
                    .addMigrations(MIGRATION_8_10)
                    .addMigrations(MIGRATION_8_9)
                    .addMigrations(MIGRATION_9_11)
                    .addMigrations(MIGRATION_9_10)
                    .addMigrations(MIGRATION_10_11)
//                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    private static final int NUMBER_OF_THREADS = 4;
    // Executor service for background database operations
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `stp_offline_table` (`day_id` TEXT NOT NULL PRIMARY KEY, `day_caption` TEXT, `cluste_code` TEXT, `cluster_name` TEXT, `doctor_code` TEXT, `doctor_name` TEXT, `chemist_code` TEXT, `chemist_name` TEXT, `stp_data` TEXT, `status` INTEGER NOT NULL, `sync_status` TEXT)");
//            database.execSQL("ALTER TABLE `stp_offline_table` ADD COLUMN `status` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("CREATE TABLE IF NOT EXISTS `welcome_slides_table` (`name` TEXT NOT NULL PRIMARY KEY, `slide_size` TEXT, `downloading_status` TEXT, `progress` TEXT, `background_task` TEXT, `file_position` TEXT)");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `activity_details_table` (`id` TEXT NOT NULL PRIMARY KEY, `json_data` TEXT, `status` TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `activity_offline_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `activity_date` TEXT, `activity_time` TEXT, `sl_no` TEXT, `name` TEXT, `dr_code` TEXT, `json_data` TEXT, `sync_count` INTEGER NOT NULL, `sync_status` TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `activity_upload_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `activity_id` INTEGER NOT NULL, `activity_date` TEXT, `activity_time` TEXT, `sl_no` TEXT,  `name` TEXT, `image_name` TEXT,  `file_path` TEXT, `json_data` TEXT, `sync_count` INTEGER NOT NULL, `sync_status` TEXT)");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `quiz_offline_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `quiz_date` TEXT, `quiz_time` TEXT, `json_data` TEXT, `sync_count` INTEGER NOT NULL, `sync_status` TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `quiz_asserts_table` (`name` TEXT NOT NULL PRIMARY KEY, `assert_size` TEXT, `downloading_status` TEXT, `progress` TEXT, `background_task` TEXT, `assert_position` TEXT)");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `presentation_table` ADD COLUMN `customer_type` TEXT NOT NULL");
            database.execSQL("ALTER TABLE `presentation_table` ADD COLUMN `customer_codes` TEXT");
            database.execSQL("ALTER TABLE `presentation_table` ADD COLUMN `headquarter_code` TEXT");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `notification_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `message` TEXT, `date_time` TEXT, `is_read` INTEGER NOT NULL DEFAULT 0, `sync_status` INTEGER NOT NULL DEFAULT 0)");
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `presentation_table_new` (" +
                                     "`presentation_name` TEXT NOT NULL PRIMARY KEY, " +
                                     "`presentation_data` TEXT DEFAULT '', " +
                                     "`customer_type` TEXT NOT NULL DEFAULT '', " +
                                     "`customer_codes` TEXT DEFAULT '', " +
                                     "`headquarter_code` TEXT DEFAULT ''" +
                                     ")");

            database.execSQL("INSERT INTO `presentation_table_new` " +
                                     "(`presentation_name`, `presentation_data`) " +
                                     "SELECT `presentation_name`, `presentation_data` " +
                                     "FROM `presentation_table`");

            database.execSQL("DROP TABLE `presentation_table`");

            database.execSQL("ALTER TABLE `presentation_table_new` RENAME TO `presentation_table`");
        }
    };

    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `missed_table` (`id` TEXT NOT NULL, `sfcode` TEXT, `values` TEXT, PRIMARY KEY(`id`))");
            db.execSQL("CREATE TABLE IF NOT EXISTS `doctor_visit_table` (`sfcode` TEXT NOT NULL, `date` TEXT NOT NULL, `values` TEXT, PRIMARY KEY(`sfcode`, `date`))");
        }
    };

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `call_offline_sign_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `call_image_name_sign` TEXT, `call_file_path_sign` TEXT, `call_json_values_sign` TEXT, `call_status_sign` TEXT, `call_sync_status_sign` INTEGER  NOT NULL, `call_date_sign` TEXT, `call_cus_code_sign` TEXT, `call_cus_name_sign` TEXT)");
        }
    };

    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notification_table ADD COLUMN is_dialog_shown INTEGER NOT NULL DEFAULT 0");
        }
    };
    public static final Migration MIGRATION_9_11 = new Migration(9, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notification_table ADD COLUMN is_dialog_shown INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE IF NOT EXISTS `stp_offline_table` (`day_id` TEXT NOT NULL PRIMARY KEY, `day_caption` TEXT, `cluste_code` TEXT, `cluster_name` TEXT, `doctor_code` TEXT, `doctor_name` TEXT, `chemist_code` TEXT, `chemist_name` TEXT, `Speciality_Name` TEXT, `Category_Name` TEXT, `Class_Name` TEXT, `stp_data` TEXT, `status` INTEGER NOT NULL, `sync_status` TEXT)");
//            database.execSQL("ALTER TABLE stp_offline_table ADD COLUMN Speciality_Name TEXT");
//            database.execSQL("ALTER TABLE stp_offline_table ADD COLUMN Category_Name TEXT");
//            database.execSQL("ALTER TABLE stp_offline_table ADD COLUMN Class_Name TEXT");

            database.execSQL(
                    "CREATE TABLE stp_offline_table_new (" +
                            "day_id TEXT NOT NULL, " +
                            "sf_code TEXT NOT NULL, " +
                            "day_caption TEXT, " +
                            "cluste_code TEXT, " +
                            "cluster_name TEXT, " +
                            "doctor_code TEXT, " +
                            "doctor_name TEXT, " +
                            "chemist_code TEXT, " +
                            "chemist_name TEXT, " +
                            "stp_data TEXT, " +
                            "status INTEGER NOT NULL, " +
                            "sync_status TEXT, " +
                            "Speciality_Name TEXT, " +
                            "Category_Name TEXT, " +
                            "Category_Code TEXT, " +
                            "Class_Name TEXT, " +
                            "PRIMARY KEY(day_id, sf_code))"
            );

            database.execSQL(
                    "INSERT INTO stp_offline_table_new (" +
                            "day_id, sf_code, day_caption, cluste_code, cluster_name, " +
                            "doctor_code, doctor_name, chemist_code, chemist_name, " +
                            "stp_data, status, sync_status) " +
                            "SELECT day_id, '', day_caption, cluste_code, cluster_name, " +
                            "doctor_code, doctor_name, chemist_code, chemist_name, " +
                            "stp_data, status, sync_status " +
                            "FROM stp_offline_table"
            );

            database.execSQL("DROP TABLE stp_offline_table");

            database.execSQL("ALTER TABLE stp_offline_table_new RENAME TO stp_offline_table");

        }
    };

    public static final Migration MIGRATION_1_3 = new Migration(1, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
        }
    };

    public static final Migration MIGRATION_1_4 = new Migration(1, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
        }
    };

    public static final Migration MIGRATION_1_5 = new Migration(1, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
        }
    };

    public static final Migration MIGRATION_1_6 = new Migration(1, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
        }
    };

    public static final Migration MIGRATION_1_7 = new Migration(1, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
        }
    };

    public static final Migration MIGRATION_1_8 = new Migration(1, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
        }
    };

    public static final Migration MIGRATION_1_9 = new Migration(1, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
        }
    };

    public static final Migration MIGRATION_1_10 = new Migration(1, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
        }
    };
    public static final Migration MIGRATION_1_11 = new Migration(1, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
            MIGRATION_10_11.migrate(database);
        }
    };

    public static final Migration MIGRATION_2_4 = new Migration(2, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
        }
    };

    public static final Migration MIGRATION_2_5 = new Migration(2, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
        }
    };

    public static final Migration MIGRATION_2_6 = new Migration(2, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
        }
    };

    public static final Migration MIGRATION_2_7 = new Migration(2, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
        }
    };

    public static final Migration MIGRATION_2_8 = new Migration(2, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
        }
    };

    public static final Migration MIGRATION_2_9 = new Migration(2, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
        }
    };

    public static final Migration MIGRATION_2_10 = new Migration(2, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
        }
    };

    public static final Migration MIGRATION_2_11 = new Migration(2, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_1_2.migrate(database);
            MIGRATION_2_3.migrate(database);
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
            MIGRATION_10_11.migrate(database);
        }
    };

    public static final Migration MIGRATION_3_5 = new Migration(3, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
        }
    };

    public static final Migration MIGRATION_3_6 = new Migration(3, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
        }
    };

    public static final Migration MIGRATION_3_7 = new Migration(3, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
        }
    };

    public static final Migration MIGRATION_3_8 = new Migration(3, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
        }
    };

    public static final Migration MIGRATION_3_9 = new Migration(3, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
        }
    };

    public static final Migration MIGRATION_3_10 = new Migration(3, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
        }
    };
    public static final Migration MIGRATION_3_11 = new Migration(3, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_3_4.migrate(database);
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
            MIGRATION_10_11.migrate(database);
        }
    };

    public static final Migration MIGRATION_4_6 = new Migration(4, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
        }
    };

    public static final Migration MIGRATION_4_7 = new Migration(4, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
        }
    };

    public static final Migration MIGRATION_4_8 = new Migration(4, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
        }
    };

    public static final Migration MIGRATION_4_9 = new Migration(4, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
        }
    };

    public static final Migration MIGRATION_4_10 = new Migration(4, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
        }
    };
    public static final Migration MIGRATION_4_11 = new Migration(4, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_4_5.migrate(database);
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
            MIGRATION_10_11.migrate(database);
        }
    };

    public static final Migration MIGRATION_5_7 = new Migration(5, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
        }
    };

    public static final Migration MIGRATION_5_8 = new Migration(5, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
        }
    };

    public static final Migration MIGRATION_5_9 = new Migration(5, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
        }
    };

    public static final Migration MIGRATION_5_10 = new Migration(5, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
        }
    };
    public static final Migration MIGRATION_5_11 = new Migration(5, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_5_6.migrate(database);
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
            MIGRATION_10_11.migrate(database);
        }
    };

    public static final Migration MIGRATION_6_8 = new Migration(6, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
        }
    };

    public static final Migration MIGRATION_6_9 = new Migration(6, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
        }
    };

    public static final Migration MIGRATION_6_10 = new Migration(6, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
        }
    };

    public static final Migration MIGRATION_6_11 = new Migration(6, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_6_7.migrate(database);
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
            MIGRATION_10_11.migrate(database);
        }
    };

    public static final Migration MIGRATION_7_9 = new Migration(7, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
        }
    };

    public static final Migration MIGRATION_7_10 = new Migration(7, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
        }
    };
    public static final Migration MIGRATION_7_11 = new Migration(7, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_7_8.migrate(database);
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
            MIGRATION_10_11.migrate(database);
        }
    };

    public static final Migration MIGRATION_8_10 = new Migration(8, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
        }
    };
    public static final Migration MIGRATION_8_11 = new Migration(8, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            MIGRATION_8_9.migrate(database);
            MIGRATION_9_10.migrate(database);
            MIGRATION_10_11.migrate(database);

        }
    };

    public abstract SlidesDao slidesDao();

    public abstract MasterDataDao masterDataDao();

    public abstract MissedDao missedDao();

    public abstract DoctorVisitDao doctorVisitDao();

    public abstract CallTableDao callTableDao();

    public abstract LoginDataDao loginDataDao();

    public abstract TourPlanOfflineDataDao tourPlanOfflineDataDao();

    public abstract TourPlanOnlineDataDao tourPlanOnlineDataDao();

    public abstract DCRDocDataDao dcrDocDataDao();

    public abstract PresentationDataDao presentationDataDao();

    public abstract OfflineCheckInOutDataDao offlineCheckInOutDataDao();

    public abstract CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao();

    public abstract CallOfflineECDataDao callOfflineECDataDao();

    public abstract CallOfflineDataDao callOfflineDataDao();

    public abstract OfflineDaySubmitDao offlineDaySubmitDao();

    public abstract STPOfflineDataDao stpOfflineDataDao();

    public abstract WelcomeSlidesDao welcomeSlidesDao();

    public abstract ActivityDetailsDataDao activityDetailsDataDao();

    public abstract ActivityOfflineDataDao activityOfflineDataDao();

    public abstract ActivityUploadDataDao activityUploadDataDao();

    public abstract QuizOfflineDataDao quizOfflineDataDao();

    public abstract QuizAssertsDao quizAssertsDao();

    public abstract NotificationDataDao notificationDataDao();

    public abstract CallOfflineSignDataDao callOfflineSignDataDao();

}