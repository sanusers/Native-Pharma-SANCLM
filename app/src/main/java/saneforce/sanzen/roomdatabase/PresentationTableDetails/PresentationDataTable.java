package saneforce.sanzen.roomdatabase.PresentationTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;

@Entity(tableName = "presentation_table")
public class PresentationDataTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "presentation_name")
    private String presentationName = "";

    @ColumnInfo(name = "presentation_data")
    private String presentationData;

    public PresentationDataTable() {
    }

    @Ignore
    public PresentationDataTable(@NonNull String presentationName, String presentationData) {
        this.presentationName = presentationName;
        this.presentationData = presentationData;
    }

    @NonNull
    public String getPresentationName() {
        return presentationName;
    }

    public void setPresentationName(@NonNull String presentationName) {
        this.presentationName = presentationName;
    }

    public String getPresentationData() {
        return presentationData;
    }

    public void setPresentationData(String presentationData) {
        this.presentationData = presentationData;
    }

    public BrandModelClass.Presentation getPresentationDataOrNull() {
        if(presentationData != null) {
            Type type = new TypeToken<BrandModelClass.Presentation>() {
            }.getType();
            return new Gson().fromJson(presentationData, type);
        }
        return null;
    }
}
