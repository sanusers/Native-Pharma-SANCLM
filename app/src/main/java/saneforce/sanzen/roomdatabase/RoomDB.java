package saneforce.sanzen.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataTable;
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
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataTable;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDataTable;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataTable;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesDao;
import saneforce.sanzen.roomdatabase.SlideTable.SlidesTableDeatils;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.sanzen.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataTable;
import saneforce.sanzen.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataDao;
import saneforce.sanzen.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataTable;

@Database(entities = {MasterDataTable.class, CallsLinechartTable.class, LoginDataTable.class, TourPlanOfflineDataTable.class, TourPlanOnlineDataTable.class, DCRDocDataTable.class, PresentationDataTable.class, OfflineCheckInOutDataTable.class, CallOfflineWorkTypeDataTable.class, CallOfflineECDataTable.class, CallOfflineDataTable.class, OfflineDaySubmitDataTable.class,  SlidesTableDeatils.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static final String DATABASE_NAME = "sanclmroom.dp";
    public synchronized static RoomDB getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }


    public abstract SlidesDao slidesDao();

    public abstract MasterDataDao masterDataDao();

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

}