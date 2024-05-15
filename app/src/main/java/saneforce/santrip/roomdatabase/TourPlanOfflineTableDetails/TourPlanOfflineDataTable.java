package saneforce.santrip.roomdatabase.TourPlanOfflineTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;

@Entity(tableName = "tour_plan_offline_table")
public class TourPlanOfflineDataTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tp_month")
    private String tpMonth = "";

    @ColumnInfo(name = "tp_data")
    private String tpData;

    @ColumnInfo(name = "tp_month_synced")
    private String tpMonthSynced="0";

    @ColumnInfo(name = "tp_approval_status")
    private String tpApprovalStatus;  // 0 - Planning, 1 - Pending, 2 - Rejected, 3 - Approved

    @ColumnInfo(name = "tp_rejection_reason")
    private String tpRejectionReason;

    public TourPlanOfflineDataTable() {}

    @Ignore
    public TourPlanOfflineDataTable(@NonNull String tpMonth, String tpData) {
        this.tpMonth = tpMonth;
        this.tpData = tpData;
    }

    @Ignore
    public TourPlanOfflineDataTable(@NonNull String tpMonth, String tpData, String tpMonthSynced, String tpApprovalStatus, String tpRejectionReason) {
        this.tpMonth = tpMonth;
        this.tpData = tpData;
        this.tpMonthSynced = tpMonthSynced;
        this.tpApprovalStatus = tpApprovalStatus;
        this.tpRejectionReason = tpRejectionReason;
    }

    @NonNull
    public String getTpMonth() {
        return tpMonth;
    }

    public void setTpMonth(@NonNull String tpMonth) {
        this.tpMonth = tpMonth;
    }

    public String getTpData() {
        return tpData;
    }

    public void setTpData(String tpData) {
        this.tpData = tpData;
    }

    public String getTpMonthSynced() {
        return tpMonthSynced;
    }

    public void setTpMonthSynced(String tpMonthSynced) {
        this.tpMonthSynced = tpMonthSynced;
    }

    public String getTpApprovalStatus() {
        return tpApprovalStatus;
    }

    public void setTpApprovalStatus(String tpApprovalStatus) {
        this.tpApprovalStatus = tpApprovalStatus;
    }

    public String getTpRejectionReason() {
        return tpRejectionReason;
    }

    public void setTpRejectionReason(String tpRejectionReason) {
        this.tpRejectionReason = tpRejectionReason;
    }

    public JSONArray getTpDataJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            if (tpData != null && !tpData.isEmpty()) jsonArray = new JSONArray(tpData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public String getTpMonthSyncedOrEmpty() {
        return tpMonthSynced != null ? tpMonthSynced : "";
    }

    public String getTpApprovalStatusOrEmpty() {
        return tpApprovalStatus != null ? tpApprovalStatus : "";
    }

    public String getTpRejectionReasonOrEmpty() {
        return tpRejectionReason != null ? tpRejectionReason : "";
    }

}
