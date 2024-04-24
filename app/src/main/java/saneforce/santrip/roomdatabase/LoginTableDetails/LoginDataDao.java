package saneforce.santrip.roomdatabase.LoginTableDetails;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LoginDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveLoginData(LoginDataTable loginData);

    @Query("DELETE FROM `LOGIN_TABLE`")
    void deleteAllData();

    @Query("SELECT * FROM `LOGIN_TABLE` LIMIT 1")
    LoginDataTable getLoginData();
}
