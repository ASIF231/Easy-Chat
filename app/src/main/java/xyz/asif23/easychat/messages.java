package xyz.asif23.easychat;

public class messages {
    String  message,senderId,currenttime;
    Long timestamp;

    public messages(String message, String senderId, Long timestamp, String currenttime) {
        this.message = message;
        this.senderId = senderId;
        this.currenttime = currenttime;
        this.timestamp = timestamp;
    }

    public messages() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
