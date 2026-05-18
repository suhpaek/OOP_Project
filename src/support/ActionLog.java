package support;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ActionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String        id;
    private String        actorId;
    private String        action;
    private LocalDateTime createdAt;

    public ActionLog(String id, String actorId, String action) {
        this.id        = Objects.requireNonNull(id,      "id must not be null");
        this.actorId   = Objects.requireNonNull(actorId, "actorId must not be null");
        this.action    = Objects.requireNonNull(action,  "action must not be null");
        this.createdAt = LocalDateTime.now();
    }

    public String        getId()        { return id; }
    public String        getActorId()   { return actorId; }
    public String        getAction()    { return action; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionLog)) return false;
        ActionLog actionLog = (ActionLog) o;
        return Objects.equals(id, actionLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ActionLog{id='%s', actor='%s', at=%s, action='%s'}",
                id, actorId, createdAt, action);
    }
}