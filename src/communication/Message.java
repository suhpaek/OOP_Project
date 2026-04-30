package communication;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String id;
    private String senderId;
    private String receiverId;
    private String text;
    private LocalDateTime sentAt;

    public Message(String id, String senderId, String receiverId, String text) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.sentAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setText(String text) {
        this.text = text;
    }
}
