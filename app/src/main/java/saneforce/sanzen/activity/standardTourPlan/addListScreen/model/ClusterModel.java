package saneforce.sanzen.activity.standardTourPlan.addListScreen.model;

import androidx.annotation.NonNull;

public class ClusterModel {
    private final String clusterCode;
    private final String clusterName;

    public ClusterModel(String clusterCode, String clusterName) {
        this.clusterCode = clusterCode;
        this.clusterName = clusterName;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public String getClusterName() {
        return clusterName;
    }

    @NonNull
    @Override
    public String toString() {
        return "ClusterModel{" +
                "clusterCode='" + clusterCode + '\'' +
                ", clusterName='" + clusterName + '\'' +
                '}';
    }
}
