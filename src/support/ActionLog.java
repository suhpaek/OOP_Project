package support;

import java.time.LocalDateTime;

public class ActionLog {
    private String id;
    private String actorId;
    private String action;
    private LocalDateTime createdAt;

    public ActionLog(String id, String actorId, String action) {
        this.id = id;
        this.actorId = actorId;
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getActorId() {
        return actorId;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
