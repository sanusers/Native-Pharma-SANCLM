package saneforce.sanclm.activity.masterSync;

public class MasterSyncItemModel {
    String name = "";
    int count = 0;
    String masterFor = "";
    String remoteTableName = "";
    String localTableKeyName ="";
    boolean PB_visibility = false;


    public MasterSyncItemModel (String name, int count, String masterFor, String remoteTableName, String localTableKeyName, boolean PB_visibility) {
        this.name = name;
        this.count = count;
        this.masterFor = masterFor;
        this.remoteTableName = remoteTableName;
        this.localTableKeyName = localTableKeyName;
        this.PB_visibility = PB_visibility;
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

    public String getMasterFor () {
        return masterFor;
    }

    public void setMasterFor (String masterFor) {
        this.masterFor = masterFor;
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

    public boolean isPB_visibility () {
        return PB_visibility;
    }

    public void setPB_visibility (boolean PB_visibility) {
        this.PB_visibility = PB_visibility;
    }
}
