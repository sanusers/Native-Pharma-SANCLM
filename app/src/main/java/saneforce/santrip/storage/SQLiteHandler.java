package saneforce.santrip.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteHandler {
    private final SQLite SQLite;
    private SQLiteDatabase db;

    public SQLiteHandler(Context context) {
        SQLite = new SQLite(context);
    }

    public void open() throws SQLException {
        db = SQLite.getWritableDatabase();
    }

    public void close() {
        SQLite.close();
    }

    public Cursor select_master_list(String tableName) {
        return db.rawQuery(" SELECT * FROM " + SQLite.MASTER_SYNC_TABLE+ " WHERE " + SQLite.MASTER_KEY + " = '" + tableName + "' ", null);
    }
}
