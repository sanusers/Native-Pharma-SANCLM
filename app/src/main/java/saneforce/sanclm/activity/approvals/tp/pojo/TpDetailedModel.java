package saneforce.sanclm.activity.approvals.tp.pojo;

public class TpDetailedModel {
    String DayNo;
    String Status;
    String reasonForRejection;
    String WtCode;
    String WtCode2;
    String WtCode3;
    String WtName;
    String WtName2;
    String WtName3;
    String ClusterCode;
    String ClusterCode2;
    String ClusterCode3;
    String ClusterName;
    String ClusterName2;
    String ClusterName3;
    String FWFlg;
    String FWFlg2;
    String FWFlg3;
    String DayRemarks;
    String DayRemarks2;
    String DayRemarks3;
    String drName;
    String drName2;
    String drName3;
    String chemistName;
    String chemistName2;
    String chemistName3;
    String stockiestName;
    String stockiestName2;
    String stockiestName3;
    String jwName;
    String jwName2;
    String jwName3;
    public TpDetailedModel(String dayNo, String wtName, String clusterName, String FWFlg, String dayRemarks, String drName,String chemName,String StockiestName,String jwName) {
        DayNo = dayNo;
        WtName = wtName;
        ClusterName = clusterName;
        this.FWFlg = FWFlg;
        DayRemarks = dayRemarks;
        this.drName = drName;
        this.chemistName = chemName;
        this.stockiestName = StockiestName;
        this.jwName = jwName;
    }
    public TpDetailedModel(String dayNo, String status, String reasonForRejection, String wtCode, String wtCode2, String wtCode3, String wtName, String wtName2, String wtName3, String clusterCode, String clusterCode2, String clusterCode3, String clusterName, String clusterName2, String clusterName3, String FWFlg, String FWFlg2, String FWFlg3, String dayRemarks, String dayRemarks2, String dayRemarks3, String drName, String drName2, String drName3, String chemistName, String chemistName2, String chemistName3, String stockiestName, String stockiestName2, String stockiestName3, String jwName, String jwName2, String jwName3) {
        DayNo = dayNo;
        Status = status;
        this.reasonForRejection = reasonForRejection;
        WtCode = wtCode;
        WtCode2 = wtCode2;
        WtCode3 = wtCode3;
        WtName = wtName;
        WtName2 = wtName2;
        WtName3 = wtName3;
        ClusterCode = clusterCode;
        ClusterCode2 = clusterCode2;
        ClusterCode3 = clusterCode3;
        ClusterName = clusterName;
        ClusterName2 = clusterName2;
        ClusterName3 = clusterName3;
        this.FWFlg = FWFlg;
        this.FWFlg2 = FWFlg2;
        this.FWFlg3 = FWFlg3;
        DayRemarks = dayRemarks;
        DayRemarks2 = dayRemarks2;
        DayRemarks3 = dayRemarks3;
        this.drName = drName;
        this.drName2 = drName2;
        this.drName3 = drName3;
        this.chemistName = chemistName;
        this.chemistName2 = chemistName2;
        this.chemistName3 = chemistName3;
        this.stockiestName = stockiestName;
        this.stockiestName2 = stockiestName2;
        this.stockiestName3 = stockiestName3;
        this.jwName = jwName;
        this.jwName2 = jwName2;
        this.jwName3 = jwName3;
    }

    public String getDrName() {
        return drName;
    }

    public void setDrName(String drName) {
        this.drName = drName;
    }

    public String getDrName2() {
        return drName2;
    }

    public void setDrName2(String drName2) {
        this.drName2 = drName2;
    }

    public String getDrName3() {
        return drName3;
    }

    public void setDrName3(String drName3) {
        this.drName3 = drName3;
    }

    public String getChemistName() {
        return chemistName;
    }

    public void setChemistName(String chemistName) {
        this.chemistName = chemistName;
    }

    public String getChemistName2() {
        return chemistName2;
    }

    public void setChemistName2(String chemistName2) {
        this.chemistName2 = chemistName2;
    }

    public String getChemistName3() {
        return chemistName3;
    }

    public void setChemistName3(String chemistName3) {
        this.chemistName3 = chemistName3;
    }

    public String getStockiestName() {
        return stockiestName;
    }

    public void setStockiestName(String stockiestName) {
        this.stockiestName = stockiestName;
    }

    public String getStockiestName2() {
        return stockiestName2;
    }

    public void setStockiestName2(String stockiestName2) {
        this.stockiestName2 = stockiestName2;
    }

    public String getStockiestName3() {
        return stockiestName3;
    }

    public void setStockiestName3(String stockiestName3) {
        this.stockiestName3 = stockiestName3;
    }

    public String getJwName() {
        return jwName;
    }

    public void setJwName(String jwName) {
        this.jwName = jwName;
    }

    public String getJwName2() {
        return jwName2;
    }

    public void setJwName2(String jwName2) {
        this.jwName2 = jwName2;
    }

    public String getJwName3() {
        return jwName3;
    }

    public void setJwName3(String jwName3) {
        this.jwName3 = jwName3;
    }

    public String getDayNo() {
        return DayNo;
    }

    public void setDayNo(String dayNo) {
        DayNo = dayNo;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public String getWtCode() {
        return WtCode;
    }

    public void setWtCode(String wtCode) {
        WtCode = wtCode;
    }

    public String getWtCode2() {
        return WtCode2;
    }

    public void setWtCode2(String wtCode2) {
        WtCode2 = wtCode2;
    }

    public String getWtCode3() {
        return WtCode3;
    }

    public void setWtCode3(String wtCode3) {
        WtCode3 = wtCode3;
    }

    public String getWtName() {
        return WtName;
    }

    public void setWtName(String wtName) {
        WtName = wtName;
    }

    public String getWtName2() {
        return WtName2;
    }

    public void setWtName2(String wtName2) {
        WtName2 = wtName2;
    }

    public String getWtName3() {
        return WtName3;
    }

    public void setWtName3(String wtName3) {
        WtName3 = wtName3;
    }

    public String getClusterCode() {
        return ClusterCode;
    }

    public void setClusterCode(String clusterCode) {
        ClusterCode = clusterCode;
    }

    public String getClusterCode2() {
        return ClusterCode2;
    }

    public void setClusterCode2(String clusterCode2) {
        ClusterCode2 = clusterCode2;
    }

    public String getClusterCode3() {
        return ClusterCode3;
    }

    public void setClusterCode3(String clusterCode3) {
        ClusterCode3 = clusterCode3;
    }

    public String getClusterName() {
        return ClusterName;
    }

    public void setClusterName(String clusterName) {
        ClusterName = clusterName;
    }

    public String getClusterName2() {
        return ClusterName2;
    }

    public void setClusterName2(String clusterName2) {
        ClusterName2 = clusterName2;
    }

    public String getClusterName3() {
        return ClusterName3;
    }

    public void setClusterName3(String clusterName3) {
        ClusterName3 = clusterName3;
    }

    public String getFWFlg() {
        return FWFlg;
    }

    public void setFWFlg(String FWFlg) {
        this.FWFlg = FWFlg;
    }

    public String getFWFlg2() {
        return FWFlg2;
    }

    public void setFWFlg2(String FWFlg2) {
        this.FWFlg2 = FWFlg2;
    }

    public String getFWFlg3() {
        return FWFlg3;
    }

    public void setFWFlg3(String FWFlg3) {
        this.FWFlg3 = FWFlg3;
    }

    public String getDayRemarks() {
        return DayRemarks;
    }

    public void setDayRemarks(String dayRemarks) {
        DayRemarks = dayRemarks;
    }

    public String getDayRemarks2() {
        return DayRemarks2;
    }

    public void setDayRemarks2(String dayRemarks2) {
        DayRemarks2 = dayRemarks2;
    }

    public String getDayRemarks3() {
        return DayRemarks3;
    }

    public void setDayRemarks3(String dayRemarks3) {
        DayRemarks3 = dayRemarks3;
    }
}
