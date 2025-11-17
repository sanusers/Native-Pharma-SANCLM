package saneforce.sanzen.activity.chat.model;

public class ChatMessage {
    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;
    public static final int TYPE_DATE = 2;

    private String message;
    private String time;
    private String date;
    private int messageType;

    public ChatMessage(String message, String time, String date, int messageType) {
        this.message = message;
        this.time = time;
        this.date = date;
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
