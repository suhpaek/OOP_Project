package support;

import enums.RequestStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class TechSupportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String description;
    private String submittedById;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime viewedAt;
    private LocalDateTime resolvedAt;

    public TechSupportRequest(String id, String title, String description, String submittedById) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = description;
        this.submittedById = submittedById;
        this.status = RequestStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public void markViewed() {
        if (status == RequestStatus.NEW) {
            this.status   = RequestStatus.VIEWED;
            this.viewedAt = LocalDateTime.now();
        }
    }

    public void markInProcess() {
        if (status == RequestStatus.ACCEPTED || status == RequestStatus.VIEWED) {
            this.status = RequestStatus.IN_PROCESS;
        }
    }

    void accept() {
        if (status == RequestStatus.VIEWED || status == RequestStatus.NEW) {
            this.status = RequestStatus.ACCEPTED;
        }
    }

    void reject() {
        if (status != RequestStatus.DONE) {
            this.status      = RequestStatus.REJECTED;
            this.resolvedAt  = LocalDateTime.now();
        }
    }

    void complete() {
        this.status     = RequestStatus.DONE;
        this.resolvedAt = LocalDateTime.now();
    }

    public String getId() {
        return id; 
    }
    public String getTitle() { 
        return title; 
    }
    public String  getDescription() { 
        return description; 
    }
    public String  getSubmittedById(){ 
        return submittedById; 
    }
    public RequestStatus getStatus() { 
        return status; 
    }
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public LocalDateTime getViewedAt() { 
        return viewedAt; 
    }
    public LocalDateTime getResolvedAt() { 
        return resolvedAt; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TechSupportRequest)) return false;
        TechSupportRequest that = (TechSupportRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("TechSupportRequest{id='%s', title='%s', status=%s, submittedBy='%s'}",
                id, title, status, submittedById);
    }
}