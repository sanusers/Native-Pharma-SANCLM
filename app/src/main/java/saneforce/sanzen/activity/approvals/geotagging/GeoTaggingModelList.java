package saneforce.sanzen.activity.approvals.geotagging;

public class GeoTaggingModelList {
    String name;
    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMapId() {
        return MapId;
    }

    public void setMapId(String mapId) {
        MapId = mapId;
    }

    public String getHqCode() {
        return hqCode;
    }

    public void setHqCode(String hqCode) {
        this.hqCode = hqCode;
    }

    String MapId;
    String hqName,hqCode;
    String cluster;
    String address;
    String latitude, longitude, cust_mode;
    String date_time;

    public GeoTaggingModelList(String name, String code,String hqName,String hqCode, String cluster, String address, String latitude, String longitude, String cust_mode, String date_time,String mapId) {
        this.name = name;
        this.code = code;
        this.hqName = hqName;
        this.hqCode = hqCode;
        this.cluster = cluster;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cust_mode = cust_mode;
        this.date_time = date_time;
        this.MapId = mapId;
    }

    public GeoTaggingModelList(String name, String latitude, String longitude, String address) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCust_mode() {
        return cust_mode;
    }

    public void setCust_mode(String cust_mode) {
        this.cust_mode = cust_mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHqName() {
        return hqName;
    }

    public void setHqName(String hqName) {
        this.hqName = hqName;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
