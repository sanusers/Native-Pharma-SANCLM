package saneforce.sanclm.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLite extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "san_clm.db";
    public static final String LOGIN_TABLE = "login_table";
    public static final String LOGIN_DATA = "login_data";




    public SQLite (@Nullable Context context) {
        super(context, DATA_BASE_NAME, null, 1);
    }

    @Override
    public void onCreate (SQLiteDatabase sqLiteDB) {
        sqLiteDB.execSQL("CREATE TABLE IF NOT EXISTS " + LOGIN_TABLE + "(" + "Login_data text" + ")");

    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDB, int i, int i1) {
        sqLiteDB.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);

    }

    public void deleteAllTable(){
        SQLiteDatabase sqLiteDB = this.getWritableDatabase();
        sqLiteDB.execSQL("DELETE FROM " + LOGIN_TABLE);
    }

    public void saveLoginData(String data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOGIN_DATA,data);
        db.insert(LOGIN_TABLE,null,contentValues);
        db.close();
    }

    public Cursor getLoginData(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + LOGIN_TABLE, null);
    }


}
