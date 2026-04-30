package communication;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Comment implements Serializable {
    private int id;
    private String text;
    private LocalDateTime createdAt;
    private String authorId;

    public Comment(int id, String text, String authorId) {
        this.id = id;
        this.text = text;
        this.authorId = authorId;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setText(String text) {
        this.text = text;
    }
}
