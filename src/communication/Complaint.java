package communication;

import enums.UrgencyLevel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Complaint implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String text;
    private UrgencyLevel urgency;
    private String teacherId;
    private String targetStudentId;
    private LocalDateTime createdAt;
    private boolean resolved;


    public Complaint(int id, String text, UrgencyLevel urgency, String teacherId, String targetStudentId) {
        this.id = id;
        this.text = Objects.requireNonNull(text,      "text must not be null");
        this.urgency = Objects.requireNonNull(urgency,   "urgency must not be null");
        this.teacherId = Objects.requireNonNull(teacherId, "teacherId must not be null");
        this.targetStudentId = targetStudentId;
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }

    public Complaint(int id, String text, UrgencyLevel urgency, String teacherId) {
        this(id, text, urgency, teacherId, null);
    }

    public void resolve(){
        this.resolved = true;
    }

    public int getId(){ 
        return id; 
    }
    public String getText(){ 
        return text; 
    }
    public UrgencyLevel getUrgency(){ 
        return urgency; 
    }
    public String getTeacherId(){ 
        return teacherId; 
    }
    public String getTargetStudentId() { 
        return targetStudentId; 
    }
    public LocalDateTime getCreatedAt(){ 
        return createdAt; 
    }
    public boolean isResolved(){ 
        return resolved; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint)) return false;
        Complaint complaint = (Complaint) o;
        return id == complaint.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Complaint{id=%d, teacher='%s', urgency=%s, resolved=%b, text='%s'}",
                id, teacherId, urgency, resolved, text);
    }
}