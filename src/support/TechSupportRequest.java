package support;

import enums.RequestStatus;

import java.time.LocalDateTime;

public class TechSupportRequest {
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
        this.status = RequestStatus.IN_PROCESS;
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
}
