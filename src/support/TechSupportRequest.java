package support;

import enums.RequestStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TechSupportRequest implements Serializable {
    private String id;
    private String title;
    private String description;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime viewedAt;

    public TechSupportRequest(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = RequestStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public void markViewed() {
        this.status = RequestStatus.VIEWED;
        this.viewedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getViewedAt() { return viewedAt; }
}
