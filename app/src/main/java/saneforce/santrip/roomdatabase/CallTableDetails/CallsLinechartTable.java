package saneforce.santrip.roomdatabase.CallTableDetails;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "LINE_CHAT_DATA_TABLE")
public class CallsLinechartTable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String LINECHAR_CUSTCODE;
    public String LINECHAR_CUSTTYPE;
    public String LINECHAR_DCR_DT;
    public String LINECHAR_MONTH_NAME;
    public String LINECHAR_MNTH;
    public String LINECHAR_YR;
    public String LINECHAR_CUSTNAME;
    public String LINECHAR_TOWN_CODE;
    public String LINECHAR_TOWN_NAME;
    public String LINECHAR_DCR_FLAG;
    public String LINECHAR_SF_CODE;
    public String LINECHAR_TRANS_SLNO;
    public String LINECHAR_AMSLNO;
    public String LINECHAR_FM_INDICATOR;

    public String LINECHAR_DATE_FLOG;

    public String getLINECHAR_DATE_FLOG() {
        return LINECHAR_DATE_FLOG;
    }

    public void setLINECHAR_DATE_FLOG(String LINECHAR_DATE_FLOG) {
        this.LINECHAR_DATE_FLOG = LINECHAR_DATE_FLOG;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLINECHAR_CUSTCODE() {
        return LINECHAR_CUSTCODE;
    }

    public void setLINECHAR_CUSTCODE(String LINECHAR_CUSTCODE) {
        this.LINECHAR_CUSTCODE = LINECHAR_CUSTCODE;
    }

    public String getLINECHAR_CUSTTYPE() {
        return LINECHAR_CUSTTYPE;
    }

    public void setLINECHAR_CUSTTYPE(String LINECHAR_CUSTTYPE) {
        this.LINECHAR_CUSTTYPE = LINECHAR_CUSTTYPE;
    }

    public String getLINECHAR_DCR_DT() {
        return LINECHAR_DCR_DT;
    }

    public void setLINECHAR_DCR_DT(String LINECHAR_DCR_DT) {
        this.LINECHAR_DCR_DT = LINECHAR_DCR_DT;
    }

    public String getLINECHAR_MONTH_NAME() {
        return LINECHAR_MONTH_NAME;
    }

    public void setLINECHAR_MONTH_NAME(String LINECHAR_MONTH_NAME) {
        this.LINECHAR_MONTH_NAME = LINECHAR_MONTH_NAME;
    }

    public String getLINECHAR_MNTH() {
        return LINECHAR_MNTH;
    }

    public void setLINECHAR_MNTH(String LINECHAR_MNTH) {
        this.LINECHAR_MNTH = LINECHAR_MNTH;
    }

    public String getLINECHAR_YR() {
        return LINECHAR_YR;
    }

    public void setLINECHAR_YR(String LINECHAR_YR) {
        this.LINECHAR_YR = LINECHAR_YR;
    }

    public String getLINECHAR_CUSTNAME() {
        return LINECHAR_CUSTNAME;
    }

    public void setLINECHAR_CUSTNAME(String LINECHAR_CUSTNAME) {
        this.LINECHAR_CUSTNAME = LINECHAR_CUSTNAME;
    }

    public String getLINECHAR_TOWN_CODE() {
        return LINECHAR_TOWN_CODE;
    }

    public void setLINECHAR_TOWN_CODE(String LINECHAR_TOWN_CODE) {
        this.LINECHAR_TOWN_CODE = LINECHAR_TOWN_CODE;
    }

    public String getLINECHAR_TOWN_NAME() {
        return LINECHAR_TOWN_NAME;
    }

    public void setLINECHAR_TOWN_NAME(String LINECHAR_TOWN_NAME) {
        this.LINECHAR_TOWN_NAME = LINECHAR_TOWN_NAME;
    }

    public String getLINECHAR_DCR_FLAG() {
        return LINECHAR_DCR_FLAG;
    }

    public void setLINECHAR_DCR_FLAG(String LINECHAR_DCR_FLAG) {
        this.LINECHAR_DCR_FLAG = LINECHAR_DCR_FLAG;
    }

    public String getLINECHAR_SF_CODE() {
        return LINECHAR_SF_CODE;
    }

    public void setLINECHAR_SF_CODE(String LINECHAR_SF_CODE) {
        this.LINECHAR_SF_CODE = LINECHAR_SF_CODE;
    }

    public String getLINECHAR_TRANS_SLNO() {
        return LINECHAR_TRANS_SLNO;
    }

    public void setLINECHAR_TRANS_SLNO(String LINECHAR_TRANS_SLNO) {
        this.LINECHAR_TRANS_SLNO = LINECHAR_TRANS_SLNO;
    }

    public String getLINECHAR_AMSLNO() {
        return LINECHAR_AMSLNO;
    }

    public void setLINECHAR_AMSLNO(String LINECHAR_AMSLNO) {
        this.LINECHAR_AMSLNO = LINECHAR_AMSLNO;
    }

    public String getLINECHAR_FM_INDICATOR() {
        return LINECHAR_FM_INDICATOR;
    }

    public void setLINECHAR_FM_INDICATOR(String LINECHAR_FM_INDICATOR) {
        this.LINECHAR_FM_INDICATOR = LINECHAR_FM_INDICATOR;
    }
}



