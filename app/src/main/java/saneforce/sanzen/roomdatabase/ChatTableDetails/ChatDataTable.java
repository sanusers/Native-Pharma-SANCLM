package saneforce.sanzen.roomdatabase.ChatTableDetails;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_table")
public class ChatDataTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String ID = "";

    @ColumnInfo(name = "subject")
    private String subject = "";

    @ColumnInfo(name = "date")
    private String date = "";

    @ColumnInfo(name = "message")
    private String message = "";

    @ColumnInfo(name = "is_sender")
    private String isSender = "";

    @ColumnInfo(name = "received_date")
    private String receivedDate = "";

    @ColumnInfo(name = "ref_id")
    private String refID = "";

    @ColumnInfo(name = "ref_id_name")
    private String refIDName = "";

    @ColumnInfo(name = "ref_id_type")
    private String refIDType = "";

    @ColumnInfo(name = "owner_id")
    private String ownerID = "";

    @ColumnInfo(name = "owner")
    private String owner = "";

    @ColumnInfo(name = "files")
    private String files = "";

    public ChatDataTable() {
    }

    @Ignore
    public ChatDataTable(@NonNull String ID, String subject, String date, String message, String isSender, String receivedDate, String refID, String refIDName, String refIDType, String ownerID, String owner, String files) {
        this.ID = ID;
        this.subject = subject;
        this.date = date;
        this.message = message;
        this.isSender = isSender;
        this.receivedDate = receivedDate;
        this.refID = refID;
        this.refIDName = refIDName;
        this.refIDType = refIDType;
        this.ownerID = ownerID;
        this.owner = owner;
        this.files = files;
    }

    @NonNull
    public String getID() {
        return ID;
    }

    public void setID(@NonNull String ID) {
        this.ID = ID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsSender() {
        return isSender;
    }

    public void setIsSender(String isSender) {
        this.isSender = isSender;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public String getRefIDName() {
        return refIDName;
    }

    public void setRefIDName(String refIDName) {
        this.refIDName = refIDName;
    }

    public String getRefIDType() {
        return refIDType;
    }

    public void setRefIDType(String refIDType) {
        this.refIDType = refIDType;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }
}
