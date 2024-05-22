package saneforce.sanzen.roomdatabase.PresentationTableDetails;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;

@Dao
public interface PresentationDataDao {
    @Insert
    void insert(PresentationDataTable presentationDataTable);

    @Update
    void update(PresentationDataTable presentationDataTable);

    @Delete
    void delete(PresentationDataTable presentationDataTable);

    @Query("DELETE FROM `PRESENTATION_TABLE`")
    void deleteAllData();

    @Query("SELECT `PRESENTATION_DATA` FROM `PRESENTATION_TABLE`")
    List<String> getAllPresentationData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePresentation(PresentationDataTable presentationDataTable);

    @Query("SELECT EXISTS(SELECT * FROM `PRESENTATION_TABLE` WHERE `PRESENTATION_NAME` = :presentationName)")
    boolean presentationExists(String presentationName);

    @Query("DELETE FROM `PRESENTATION_TABLE` WHERE `PRESENTATION_NAME` = :presentationName")
    void deletePresentation(String presentationName);

    default void savePresentation(String oldName, String newName, String data) {
        PresentationDataTable presentationDataTable = new PresentationDataTable(newName, data);
        if(!oldName.isEmpty()) {
            deletePresentation(oldName);
            savePresentation(presentationDataTable);
        }else {
            savePresentation(presentationDataTable);
        }
    }

    default ArrayList<BrandModelClass.Presentation> getPresentations() {
        ArrayList<BrandModelClass.Presentation> presentations = new ArrayList<>();
        List<String> values = getAllPresentationData();
        for (String data : values) {
            Type type = new TypeToken<BrandModelClass.Presentation>() {
            }.getType();
            presentations.add(new Gson().fromJson(data, type));
        }
        return presentations;
    }
}
