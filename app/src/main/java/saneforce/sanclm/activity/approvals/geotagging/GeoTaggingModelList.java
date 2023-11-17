package saneforce.sanclm.activity.approvals.geotagging;

public class GeoTaggingModelList {
    String name;
    String hqName;
    String cluster;
    String address;

    public GeoTaggingModelList(String name, String hqName, String cluster, String address) {
        this.name = name;
        this.hqName = hqName;
        this.cluster = cluster;
        this.address = address;
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
