package saneforce.santrip.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import saneforce.santrip.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.santrip.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataTable;
import saneforce.santrip.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.santrip.roomdatabase.CallOfflineTableDetails.CallOfflineDataTable;
import saneforce.santrip.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.santrip.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataTable;
import saneforce.santrip.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.santrip.roomdatabase.CallTableDetails.CallsLinechartTable;
import saneforce.santrip.roomdatabase.DCRDocDataTableDetails.DCRDocDataDao;
import saneforce.santrip.roomdatabase.DCRDocDataTableDetails.DCRDocDataTable;
import saneforce.santrip.roomdatabase.LoginTableDetails.LoginDataDao;
import saneforce.santrip.roomdatabase.LoginTableDetails.LoginDataTable;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.santrip.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataTable;
import saneforce.santrip.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.santrip.roomdatabase.PresentationTableDetails.PresentationDataTable;
import saneforce.santrip.roomdatabase.SlideTable.SlidesDao;
import saneforce.santrip.roomdatabase.SlideTable.SlidesTableDeatils;
import saneforce.santrip.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataDao;
import saneforce.santrip.roomdatabase.TourPlanOfflineTableDetails.TourPlanOfflineDataTable;
import saneforce.santrip.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataDao;
import saneforce.santrip.roomdatabase.TourPlanOnlineTableDetails.TourPlanOnlineDataTable;

@Database(entities = {MasterDataTable.class, CallsLinechartTable.class, LoginDataTable.class, TourPlanOfflineDataTable.class, TourPlanOnlineDataTable.class, DCRDocDataTable.class, PresentationDataTable.class, OfflineCheckInOutDataTable.class, CallOfflineWorkTypeDataTable.class, CallOfflineECDataTable.class, CallOfflineDataTable.class, SlidesTableDeatils.class}, version = 1, exportSchema = false)
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

}