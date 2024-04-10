package saneforce.santrip.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import saneforce.santrip.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.santrip.roomdatabase.CallTableDetails.CallsLinechartTable;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;

@Database(entities = {MasterDataTable.class, CallsLinechartTable.class}, version = 1, exportSchema = false)
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

    public abstract MasterDataDao masterDataDao();

    public abstract CallTableDao callTableDao();


}