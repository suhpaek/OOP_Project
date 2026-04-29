package communication;

import enums.UrgencyLevel;

import java.time.LocalDateTime;

public class Complaint {
    private int id;
    private String text;
    private UrgencyLevel urgency;
    private LocalDateTime createdAt;
    private String teacherId;

    public Complaint(int id, String text, UrgencyLevel urgency, String teacherId) {
        this.id = id;
        this.text = text;
        this.urgency = urgency;
        this.teacherId = teacherId;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTeacherId() {
        return teacherId;
    }
}
