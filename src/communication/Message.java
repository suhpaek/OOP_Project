package communication;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private String        id;
    private String        senderId;
    private String        receiverId;
    private String        text;
    private LocalDateTime sentAt;
    private boolean       read;


    public Message(String id, String senderId, String receiverId, String sentMsg1) {
        this.id         = Objects.requireNonNull(id,         "id must not be null");
        this.senderId   = Objects.requireNonNull(senderId,   "senderId must not be null");
        this.receiverId = Objects.requireNonNull(receiverId, "receiverId must not be null");
        this.text       = Objects.requireNonNull(sentMsg1,       "text must not be null");
        this.sentAt     = LocalDateTime.now();
        this.read       = false;
    }

    public void markAsRead() {
        this.read = true;
    }

    public String        getId()         { return id; }
    public String        getSenderId()   { return senderId; }
    public String        getReceiverId() { return receiverId; }
    public String        getText()       { return text; }
    public LocalDateTime getSentAt()     { return sentAt; }
    public boolean       isRead()        { return read; }

    public void setText(String text) {
        this.text = Objects.requireNonNull(text, "text must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Message{from='%s', to='%s', at=%s, text='%s'}",
                senderId, receiverId, sentAt, text);
    }
}