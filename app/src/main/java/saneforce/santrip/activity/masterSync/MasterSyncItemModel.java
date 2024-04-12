package saneforce.santrip.activity.masterSync;

public class MasterSyncItemModel {
    String name = "";
    int count = 0;
    String masterOf = "";
    String remoteTableName = "";
    String localTableKeyName ="";
    boolean PBarVisibility = false;
    int syncSuccess = 0; //0 - success , 1- failed

    public MasterSyncItemModel(){

    }

    public MasterSyncItemModel (String name, int count, String masterOf, String remoteTableName, String localTableKeyName, boolean PBarVisibility) {
        this.name = name;
        this.count = count;
        this.masterOf = masterOf;
        this.remoteTableName = remoteTableName;
        this.localTableKeyName = localTableKeyName;
        this.PBarVisibility = PBarVisibility;
    }

    public MasterSyncItemModel (String name, int count, String masterOf, String remoteTableName, String localTableKeyName, int syncSuccess, boolean PBarVisibility) {
        this.name = name;
        this.count = count;
        this.masterOf = masterOf;
        this.remoteTableName = remoteTableName;
        this.localTableKeyName = localTableKeyName;
        this.syncSuccess = syncSuccess;
        this.PBarVisibility = PBarVisibility;
    }

    public MasterSyncItemModel (String masterOf, String remoteTableName, String localTableKeyName) {
        this.masterOf = masterOf;
        this.remoteTableName = remoteTableName;
        this.localTableKeyName = localTableKeyName;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getCount () {
        return count;
    }

    public void setCount (int count) {
        this.count = count;
    }

    public String getMasterOf () {
        return masterOf;
    }

    public void setMasterOf (String masterOf) {
        this.masterOf = masterOf;
    }

    public String getRemoteTableName () {
        return remoteTableName;
    }

    public String getLocalTableKeyName () {
        return localTableKeyName;
    }

    public void setLocalTableKeyName (String localTableKeyName) {
        this.localTableKeyName = localTableKeyName;
    }

    public void setRemoteTableName (String remoteTableName) {
        this.remoteTableName = remoteTableName;
    }

    public boolean isPBarVisibility () {
        return PBarVisibility;
    }

    public void setPBarVisibility (boolean PBarVisibility) {
        this.PBarVisibility = PBarVisibility;
    }

    public int getSyncSuccess () {
        return syncSuccess;
    }

    public void setSyncSuccess (int syncSuccess) {
        this.syncSuccess = syncSuccess;
    }
}
