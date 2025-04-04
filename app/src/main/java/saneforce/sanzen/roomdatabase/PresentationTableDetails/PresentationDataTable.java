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

    @ColumnInfo(name = "customer_type")
    private String customerType;

    @ColumnInfo(name = "customer_codes")
    private String customerCodes;

    @ColumnInfo(name = "headquarter_code")
    private String headquarterCode;

    public PresentationDataTable() {
    }

    @Ignore
    public PresentationDataTable(@NonNull String presentationName, String presentationData) {
        this.presentationName = presentationName;
        this.presentationData = presentationData;
    }

    @Ignore
    public PresentationDataTable(@NonNull String presentationName, String customerType, String customerCodes, String headquarterCode, String presentationData) {
        this.presentationName = presentationName;
        this.customerType = customerType;
        this.customerCodes = customerCodes;
        this.headquarterCode = headquarterCode;
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

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerCodes() {
        return customerCodes;
    }

    public void setCustomerCodes(String customerCodes) {
        this.customerCodes = customerCodes;
    }

    public String getHeadquarterCode() {
        return headquarterCode;
    }

    public void setHeadquarterCode(String headquarterCode) {
        this.headquarterCode = headquarterCode;
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
